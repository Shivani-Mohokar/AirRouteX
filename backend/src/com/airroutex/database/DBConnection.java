package com.airroutex.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DBConnection.java
 * ------------------------------------------------------------------
 * Central place that knows how to open a JDBC connection to MySQL.
 * Every DAO class calls DBConnection.getConnection() and is
 * responsible for closing it (via try-with-resources) when done.
 *
 * Configuration is read from db.properties (found on the classpath,
 * i.e. WEB-INF/classes/db.properties once deployed) instead of being
 * hardcoded, so the same compiled code works against any MySQL
 * server just by editing that one file.
 *
 * Driver note: we use the MariaDB Connector/J JDBC driver
 * (org.mariadb.jdbc.Driver). It is wire-protocol compatible with
 * real MySQL servers - this is the standard modern substitute for
 * Oracle's mysql-connector-java, which is no longer freely
 * redistributable through most Linux package managers. Connecting
 * to an actual MySQL 8 server with it works identically; only the
 * driver class name and JDBC URL prefix differ from the classic
 * "com.mysql.cj.jdbc.Driver" / "jdbc:mysql://" combination.
 * ------------------------------------------------------------------
 */
public class DBConnection {

    private static final String CONFIG_FILE = "db.properties";
    private static Properties properties;

    static {
        loadProperties();
        registerDriver();
    }

    private DBConnection() {
        // utility class - no instances
    }

    private static void loadProperties() {
        properties = new Properties();
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new RuntimeException(
                        "Could not find " + CONFIG_FILE + " on the classpath. "
                        + "Make sure it is placed in WEB-INF/classes (see README setup instructions).");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + CONFIG_FILE, e);
        }
    }

    private static void registerDriver() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "MariaDB JDBC driver not found on classpath. "
                    + "Make sure mariadb-java-client.jar is in WEB-INF/lib.", e);
        }
    }

    /**
     * Opens a new JDBC connection using the URL/user/password from
     * db.properties. Callers MUST close this connection when done
     * (use try-with-resources).
     */
    public static Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");
        return DriverManager.getConnection(url, user, password);
    }
}
