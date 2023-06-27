package org.app.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteHelper {
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

    private static String getURL(File location) {
        return String.format(
                "jdbc:sqlite:%s",
                location.getAbsolutePath()
        );
    }

    public static Connection connectToDB(File location) {
        if ( !location.exists() ) {
            Logger.logAndThrow(String.format(
                    "Tried to connect to '%s', but failed",
                    location.getAbsolutePath()
            ), RuntimeException.class);
        }
        try {
            return DriverManager.getConnection(getURL(location));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection createInputDB(File location) {
        if ( location.exists() ) {
            Logger.logWarn(String.format(
                    "Tried to create database at '%s', but file already exists - assuming it to be valid",
                    location.getAbsolutePath()
            ));
            return connectToDB(location);
        }
        String url = getURL(location);

        Connection conn;
        try {
            conn = DriverManager.getConnection(url);

            createAdapterTable(conn);
            createInputModeTable(conn);
            createInputTable(conn);
            createActionTable(conn);
            createAction2InputTable(conn);

            Logger.logDebug(String.format(
                    "Created input database at '%s'",
                    location.getAbsolutePath()
            ));
        } catch (SQLException e) {
            Logger.logAndThrow(String.format(
                    "Tried to connect to SQLite database at '%s', but failed",
                    url
            ), RuntimeException.class);
            throw new RuntimeException();
        }
        return conn;
    }

    public static void createAdapterTable(Connection conn) throws SQLException {
        String sql = """
                CREATE TABLE Adapter (
                AdapterID INTEGER PRIMARY KEY,
                path TEXT(256)
                )""";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public static void createInputModeTable(Connection conn) throws SQLException {
        String sql = """
                CREATE TABLE InputMode (
                Mode TEXT(40) PRIMARY KEY
                )""";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public static void createInputTable(Connection conn) throws SQLException {
        String sql = """
                CREATE TABLE Input (
                AdapterID INTEGER PRIMARY KEY,
                alias TEXT(256),
                mode TEXT(40)
                )""";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public static void createActionTable(Connection conn) throws SQLException {
        String sql = """
                CREATE TABLE Adapter (
                ActionID TEXT(256) PRIMARY KEY
                )""";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public static void createAction2InputTable(Connection conn) throws SQLException {
        String sql = """
                CREATE TABLE Adapter (
                ActionID TEXT(256),
                InputID TEXT(256),
                PRIMARY KEY (ActionID, InputID)
                )""";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public static Connection createSceneDB(File location) {
        if ( location.exists() ) {
            Logger.logWarn(String.format(
                    "Tried to create database at '%s', but file already exists - assuming it to be valid",
                    location.getAbsolutePath()
            ));
            return connectToDB(location);
        }
        String url = getURL(location);

        Connection conn;
        try {
            conn = DriverManager.getConnection(url);
            createEntityTable(conn);
            createComponentTypeTable(conn);
            createComponentTable(conn);
            createSystemTable(conn);
            createResourceTypeTable(conn);
            createResourceTable(conn);

            Logger.logDebug(String.format(
                    "Created scene database at '%s'",
                    location.getAbsolutePath()
            ));
        } catch (SQLException e) {
            Logger.logAndThrow(String.format(
                    "Tried to connect to SQLite database at '%s', but failed",
                    url
            ), RuntimeException.class);
            throw new RuntimeException();
        }

        return conn;
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
        String sql = """
                CREATE TABLE Resource (
                ResourceID INTEGER PRIMARY KEY,
                type INTEGER,
                name TEXT(100),
                data BLOB,
                FOREIGN KEY (type) REFERENCES ResourceType(ResourceTypeID)
                )""";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
}