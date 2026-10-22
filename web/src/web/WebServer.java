package web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import model.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * Annadata web edition: serves the HTML/CSS/JS front end and a JSON API.
 * The existing model / dao / db / util classes are reused unchanged.
 */
public class WebServer {

    // ---------- tiny framework ----------
    public interface Handler { Object handle(Ctx c) throws Exception; }

    public static class HttpError extends RuntimeException {
        public final int status;
        public HttpError(int status, String msg) { super(msg); this.status = status; }
    }

    public static class Ctx {
        public final HttpExchange ex;
        public final Map<String, String> params = new HashMap<>();
        public User user;
        public String sid;
        private Map<String, Object> body;

        Ctx(HttpExchange ex) { this.ex = ex; }

        public Map<String, Object> body() throws IOException {
            if (body == null) {
                try {
                    body = Json.parseObject(new String(readAll(ex.getRequestBody()), StandardCharsets.UTF_8));
                } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                    throw new HttpError(400, "Invalid JSON body");
                }
            }
            return body;
        }

        public String str(String key) throws IOException {
            Object v = body().get(key);
            return v == null ? "" : String.valueOf(v).trim();
        }

        public int integer(String key, int def) throws IOException {
            Object v = body().get(key);
            if (v == null || String.valueOf(v).trim().isEmpty()) return def;
            try {
                return (int) Double.parseDouble(String.valueOf(v).trim());
            } catch (NumberFormatException e) {
                throw new HttpError(400, key + " must be a number");
            }
        }

        public boolean bool(String key, boolean def) throws IOException {
            Object v = body().get(key);
            return v == null ? def : Boolean.parseBoolean(String.valueOf(v));
        }

        public int id(String name) {
            try {
                return Integer.parseInt(params.get(name));
            } catch (NumberFormatException e) {
                throw new HttpError(400, "Bad id");
            }
        }
    }

    private static class Route {
        final String method;
        final String[] seg;
        final String[] roles; // null = public, empty = any logged-in user
        final Handler handler;

        Route(String method, String pattern, String[] roles, Handler handler) {
            this.method = method;
            this.seg = split(pattern);
            this.roles = roles;
            this.handler = handler;
        }
    }

    private static final Map<String, User> SESSIONS = new ConcurrentHashMap<>();
    private static final List<Route> ROUTES = new ArrayList<>();
    private static Path publicDir;

    public static void pub(String method, String pattern, Handler h) { ROUTES.add(new Route(method, pattern, null, h)); }
    public static void auth(String method, String pattern, Handler h) { ROUTES.add(new Route(method, pattern, new String[0], h)); }
    public static void role(String method, String pattern, Handler h, String... roles) { ROUTES.add(new Route(method, pattern, roles, h)); }

    public static String newSession(User u) {
        String sid = UUID.randomUUID().toString().replace("-", "") + Long.toHexString(System.nanoTime());
        SESSIONS.put(sid, u);
        return sid;
    }

    public static void endSession(String sid) { if (sid != null) SESSIONS.remove(sid); }

    // ---------- main ----------
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        publicDir = Paths.get(System.getenv().getOrDefault("WEB_ROOT", "web/public")).toAbsolutePath().normalize();
        if (!Files.isDirectory(publicDir)) {
            System.err.println("Cannot find web root: " + publicDir + "\nRun this from the project folder (where run-web.bat is).");
            System.exit(1);
        }

        Api.register();

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/", WebServer::handleApi);
        server.createContext("/", WebServer::handleStatic);
        // The DAOs share one JDBC connection and close it after each call,
        // so requests are processed one at a time.
        server.setExecutor(Executors.newSingleThreadExecutor());
        server.start();
        System.out.println("Annadata web is running at http://localhost:" + port);
        System.out.println("Serving pages from " + publicDir);
    }

    // ---------- API dispatch ----------
    private static void handleApi(HttpExchange ex) throws IOException {
        int status = 200;
        Object result;
        try {
            String[] seg = split(ex.getRequestURI().getPath().substring("/api/".length()));
            String method = ex.getRequestMethod();
            Ctx c = new Ctx(ex);
            c.sid = cookie(ex, "sid");
            c.user = c.sid == null ? null : SESSIONS.get(c.sid);

            Route found = null;
            boolean pathMatched = false;
            for (Route r : ROUTES) {
                if (!matches(r.seg, seg, c.params)) { c.params.clear(); continue; }
                pathMatched = true;
                if (r.method.equals(method)) { found = r; break; }
                c.params.clear();
            }
            if (found == null) {
                throw new HttpError(pathMatched ? 405 : 404, pathMatched ? "Method not allowed" : "Unknown API endpoint");
            }
            if (found.roles != null) {
                if (c.user == null) throw new HttpError(401, "Please log in");
                if (found.roles.length > 0 && !Arrays.asList(found.roles).contains(c.user.getRole().toLowerCase())) {
                    throw new HttpError(403, "You do not have permission for this action");
                }
                if (!c.user.isActive()) throw new HttpError(403, "Account is deactivated");
            }
            result = found.handler.handle(c);
        } catch (HttpError e) {
            status = e.status;
            result = Collections.singletonMap("error", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            status = 500;
            result = Collections.singletonMap("error", "Server error: " + e.getMessage());
        }
        byte[] out = Json.stringify(result).getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.getResponseHeaders().set("Cache-Control", "no-store");
        ex.sendResponseHeaders(status, out.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(out); }
    }

    private static boolean matches(String[] pattern, String[] seg, Map<String, String> params) {
        if (pattern.length != seg.length) return false;
        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i].startsWith("{")) params.put(pattern[i].substring(1, pattern[i].length() - 1), seg[i]);
            else if (!pattern[i].equals(seg[i])) return false;
        }
        return true;
    }

    // ---------- static files ----------
    private static void handleStatic(HttpExchange ex) throws IOException {
        String p = ex.getRequestURI().getPath();
        if (p.equals("/")) p = "/index.html";
        Path file = publicDir.resolve(p.substring(1)).normalize();
        if (!file.startsWith(publicDir) || !Files.isRegularFile(file)) {
            byte[] msg = "404 - Page not found".getBytes(StandardCharsets.UTF_8);
            ex.sendResponseHeaders(404, msg.length);
            try (OutputStream os = ex.getResponseBody()) { os.write(msg); }
            return;
        }
        byte[] data = Files.readAllBytes(file);
        ex.getResponseHeaders().set("Content-Type", mime(file.toString()));
        ex.getResponseHeaders().set("Cache-Control", "no-cache");
        ex.sendResponseHeaders(200, data.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(data); }
    }

    private static String mime(String name) {
        String n = name.toLowerCase();
        if (n.endsWith(".html")) return "text/html; charset=utf-8";
        if (n.endsWith(".css")) return "text/css; charset=utf-8";
        if (n.endsWith(".js")) return "application/javascript; charset=utf-8";
        if (n.endsWith(".png")) return "image/png";
        if (n.endsWith(".svg")) return "image/svg+xml";
        if (n.endsWith(".ico")) return "image/x-icon";
        return "application/octet-stream";
    }

    // ---------- helpers ----------
    static String[] split(String path) {
        List<String> out = new ArrayList<>();
        for (String s : path.split("/")) if (!s.isEmpty()) out.add(s);
        return out.toArray(new String[0]);
    }

    private static String cookie(HttpExchange ex, String name) {
        List<String> headers = ex.getRequestHeaders().get("Cookie");
        if (headers == null) return null;
        for (String h : headers) {
            for (String part : h.split(";")) {
                String[] kv = part.trim().split("=", 2);
                if (kv.length == 2 && kv[0].equals(name)) return kv[1];
            }
        }
        return null;
    }

    static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = in.read(buf)) > 0) bos.write(buf, 0, n);
        return bos.toByteArray();
    }
}
