# web/ - Annadata front end and HTTP server

- `src/web/WebServer.java` - HTTP server, routing, login sessions, static file serving
- `src/web/Api.java` - all JSON endpoints (call the DAO classes in ../src/dao)
- `src/web/Json.java`, `Db.java` - small JSON helper and generic query helper
- `public/index.html` - login / register
- `public/donor.html`, `recipient.html`, `admin.html` - role dashboards
- `public/css/style.css` - all styling
- `public/js/common.js` - shared helpers + emergency, notifications, analytics, schedule and places views
- `public/js/donor.js`, `recipient.js`, `admin.js`, `login.js` - role specific pages

Build with `compile.bat`, run with `run.bat` (see the main README).
