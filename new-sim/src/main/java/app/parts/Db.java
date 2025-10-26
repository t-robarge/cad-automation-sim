package app.parts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class Db {
    private static final String URL = "jdbc:sqlite:parts.db";

    // Db.java
    public static Connection get() throws SQLException {
        var c = DriverManager.getConnection(URL);
        try (var st = c.createStatement()) { st.execute("PRAGMA foreign_keys = ON"); }
        return c;
    }


    public static void initSchema() throws SQLException {
        try (Connection c = get(); Statement st = c.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS parts (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  part_number TEXT NOT NULL UNIQUE,
                  name TEXT NOT NULL,
                  unit TEXT NOT NULL
                );
            """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS features (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  part_id INTEGER NOT NULL,
                  type TEXT NOT NULL,
                  x REAL NOT NULL,
                  y REAL NOT NULL,
                  d1 REAL NOT NULL,
                  d2 REAL NOT NULL,
                  FOREIGN KEY (part_id) REFERENCES parts(id) ON DELETE CASCADE
                );
            """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS assemblies (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE,
                    description TEXT
                );
                """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS assembly_parts (
                    assembly_id INTEGER NOT NULL,
                    part_id INTEGER NOT NULL,
                    quantity INTEGER NOT NULL DEFAULT 1,
                    PRIMARY KEY (assembly_id, part_id),
                    FOREIGN KEY (assembly_id) REFERENCES assemblies(id) ON DELETE CASCADE,
                    FOREIGN KEY (part_id) REFERENCES parts(id) ON DELETE CASCADE
                );
                """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS part_revisions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    part_id INTEGER NOT NULL,
                    rev_code TEXT NOT NULL,
                    author TEXT,
                    change_notes TEXT,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (part_id) REFERENCES parts(id) ON DELETE CASCADE
                );
                """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS rule_runs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    part_id INTEGER NOT NULL,
                    started_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    finished_at DATETIME,
                    status TEXT NOT NULL,     -- RUNNING, OK, VIOLATIONS, ERROR
                    FOREIGN KEY (part_id) REFERENCES parts(id) ON DELETE CASCADE
                );
                """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS rule_violations (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    rule_run_id INTEGER NOT NULL,
                    code TEXT NOT NULL,
                    message TEXT NOT NULL,
                    severity TEXT NOT NULL,   -- INFO,WARN,ERROR
                    data TEXT,                -- JSON blob (optional)
                    FOREIGN KEY (rule_run_id) REFERENCES rule_runs(id) ON DELETE CASCADE
                );
                """);

        }
    }
}
