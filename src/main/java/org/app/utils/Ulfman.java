package org.app.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Ulfman {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:sqlite:src/main/resources/sql/test.db"; // Passe den Pfad und Namen der Datenbankdatei an

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                // Erstellt tabellen
                createEntityTable(conn);
                createComponentTypeTable(conn);
                createComponentTable(conn);
                createSystemTable(conn);
                createResourceTypeTable(conn);
                createResourceTable(conn);

                System.out.println("Die Datenbank wurde erfolgreich erstellt.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void createEntityTable(Connection conn) throws SQLException {
        String sql = """
                CREATE TABLE Entity (
                EntityID INTEGER PRIMARY KEY
                )""";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void createComponentTypeTable(Connection conn) throws SQLException {
        String sql = """
                CREATE TABLE ComponentType (
                CompTypeID INTEGER PRIMARY KEY, 
                classpath TEXT(256), 
                signature BLOB
                )""";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void createComponentTable(Connection conn) throws SQLException {
        String sql = """
                CREATE TABLE Component (
                CompID INTEGER PRIMARY KEY,
                entity INTEGER, 
                type INTEGER, 
                data BLOB, 
                FOREIGN KEY (entity) REFERENCES Entity(EntityID),  
                FOREIGN KEY (type) REFERENCES ComponentType(CompTypeID)
                )""";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void createSystemTable(Connection conn) throws SQLException {
        String sql = """
                CREATE TABLE System (
                SystemID INTEGER PRIMARY KEY, 
                classpath TEXT(256), 
                signature BLOB 
                )""";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void createResourceTypeTable(Connection conn) throws SQLException {
        String sql = """
                CREATE TABLE ResourceType (
                ResourceTypeID INTEGER PRIMARY KEY, 
                classpath TEXT(256)
                )""";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void createResourceTable(Connection conn) throws SQLException {
        char[] sql = """
                CREATE TABLE Resource (
                ResourceID INTEGER PRIMARY KEY,
                type INTEGER,
                name TEXT(100),
                data BLOB,
                FOREIGN KEY (type) REFERENCES ResourceType(ResourceTypeID)
                )""".toCharArray();

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(String.valueOf(sql));
        }
    }
}