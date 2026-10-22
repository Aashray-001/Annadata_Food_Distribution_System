package web;

import db.DatabaseConnection;

import java.sql.*;
import java.util.*;

/** Small generic query helper for the few joins the existing DAOs do not offer. */
public final class Db {
    private Db() {}

    public static List<Map<String, Object>> query(String sql, Object... params) {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int c = 1; c <= md.getColumnCount(); c++) {
                        int type = md.getColumnType(c);
                        Object v;
                        if (type == Types.TIMESTAMP || type == Types.DATE || type == Types.TIME) {
                            // same conversion the DAOs use, so dates match the desktop app
                            Timestamp ts = rs.getTimestamp(c);
                            v = ts == null ? null : ts.toLocalDateTime().toString();
                        } else {
                            v = rs.getObject(c);
                        }
                        row.put(camel(md.getColumnLabel(c)), v);
                    }
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
        return rows;
    }

    public static Map<String, Object> one(String sql, Object... params) {
        List<Map<String, Object>> rows = query(sql, params);
        return rows.isEmpty() ? null : rows.get(0);
    }

    private static String camel(String s) {
        StringBuilder sb = new StringBuilder();
        boolean up = false;
        for (char c : s.toCharArray()) {
            if (c == '_') { up = true; continue; }
            sb.append(up ? Character.toUpperCase(c) : c);
            up = false;
        }
        return sb.toString();
    }
}
