package db;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.DriverPropertyInfo;
import java.sql.Statement;
import java.util.Properties;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * DatabaseHandler
 *
 * OOP Concept: Utility/Service Class
 * Responsibility: Single point of DB connection and schema initialization.
 * All CRUD modules call getConnection() from here — never construct
 * their own connection. This keeps the DB URL in one place.
 *
 * Exception Handling: getConnection() declares throws SQLException.
 * Every caller wraps it in try-catch — satisfying the rubric requirement.
 */
public class DatabaseHandler {

    // ── FIELDS ──────────────────────────────────────────────────────────────
    /**
    * Relative path to the SQLite database file.
    * "jdbc:sqlite:barangay.db" means the .db file sits in the
    * same folder as the running .jar or IDE working directory.
    * No absolute path = works on any machine with zero config.
     */
    private static final String DB_URL = "jdbc:sqlite:barangay.db";

    /**
    * Bundled SQLite JDBC driver jar name.
    * The app can be launched from src/ or the project root, so the
    * loader checks both lib/ and ../lib/ for the jar.
    */
    private static final String SQLITE_DRIVER_JAR = "sqlite-jdbc-3.45.1.0.jar";
    private static final String SLF4J_API_JAR = "slf4j-api-2.0.9.jar";
    private static final String SLF4J_SIMPLE_JAR = "slf4j-simple-2.0.9.jar";

    /**
     * Keeps the reflectively loaded driver alive after registration.
     */
    private static Driver loadedDriver;

    // ── CONSTRUCTORS ─────────────────────────────────────────────────────────
    /**
     * Default constructor.
     * DatabaseHandler has no instance fields — it is used as a
     * service class. Constructors are included to satisfy the rubric
     * requirement that all classes have constructors.
     */
    public DatabaseHandler() {
        // intentionally empty — no instance state needed
    }

    // ── METHODS ──────────────────────────────────────────────────────────────

    /**
     * Returns a live Connection to the SQLite database.
     *
     * Why static: every module needs a connection. Making it static
     * means callers write DatabaseHandler.getConnection() without
     * needing to instantiate DatabaseHandler first.
     *
     * throws SQLException — callers MUST handle this with try-catch.
     * This is where exception handling is demonstrated system-wide.
     */
    public static Connection getConnection() throws SQLException {
        ensureSQLiteDriverLoaded();
        if (loadedDriver != null) {
            Connection connection = loadedDriver.connect(DB_URL, new Properties());
            if (connection != null) {
                return connection;
            }
        }
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Loads and registers the bundled SQLite JDBC driver when the app
     * is launched without an explicit classpath.
     */
    private static void ensureSQLiteDriverLoaded() throws SQLException {
        try {
            DriverManager.getDriver(DB_URL);
            return;
        } catch (SQLException ignored) {
            // Driver not registered yet; attempt to load it from disk.
        }

        Path[] candidateJars = new Path[] {
            Paths.get("lib", SQLITE_DRIVER_JAR),
            Paths.get("lib", SLF4J_API_JAR),
            Paths.get("lib", SLF4J_SIMPLE_JAR),
            Paths.get("..", "lib", SQLITE_DRIVER_JAR),
            Paths.get("..", "lib", SLF4J_API_JAR),
            Paths.get("..", "lib", SLF4J_SIMPLE_JAR)
        };

        List<URL> jarUrls = new ArrayList<>();
        for (Path jarPath : candidateJars) {
            if (Files.exists(jarPath)) {
                try {
                    jarUrls.add(jarPath.toUri().toURL());
                } catch (Exception e) {
                    throw new SQLException("Unable to read bundled dependency path " + jarPath.toAbsolutePath(), e);
                }
            }
        }

        if (jarUrls.isEmpty()) {
            throw new SQLException("Bundled SQLite driver not found. Checked: " + Arrays.toString(candidateJars));
        }

        try {
            URLClassLoader loader = new URLClassLoader(jarUrls.toArray(new URL[0]), DatabaseHandler.class.getClassLoader());
            Class<?> driverClass = Class.forName("org.sqlite.JDBC", true, loader);
            Driver driver = (Driver) driverClass.getDeclaredConstructor().newInstance();
            loadedDriver = driver;
        } catch (Exception e) {
            throw new SQLException("Unable to load bundled SQLite driver and dependencies.", e);
        }
    }

    /**
     * Creates all three tables if they do not already exist.
     * Inserts the default admin account using INSERT OR IGNORE.
     *
     * Called once at application startup from Main.java before
     * the login screen appears.
     *
     * Uses try-with-resources — Connection and Statement are
     * AutoCloseable, so they close automatically even if an
     * exception is thrown mid-execution.
     */
    public static void initializeDatabase() {
        // SQL statements in execution order
        String createUsers = 
            "CREATE TABLE IF NOT EXISTS users (" +
            "    user_id  INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    username TEXT NOT NULL UNIQUE," +
            "    password TEXT NOT NULL," +
            "    role     TEXT NOT NULL DEFAULT 'staff'" +
            ")";

        String createResidents = 
            "CREATE TABLE IF NOT EXISTS residents (" +
            "    resident_id  INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    first_name   TEXT    NOT NULL," +
            "    last_name    TEXT    NOT NULL," +
            "    age          INTEGER NOT NULL," +
            "    gender       TEXT    NOT NULL," +
            "    civil_status TEXT    NOT NULL," +
            "    address      TEXT    NOT NULL," +
            "    purok        TEXT    NOT NULL," +
            "    contact      TEXT," +
            "    is_voter     INTEGER DEFAULT 0," +
            "    is_indigent  INTEGER DEFAULT 0," +
            "    is_senior    INTEGER DEFAULT 0," +
            "    is_pwd       INTEGER DEFAULT 0," +
            "    date_added   TEXT    DEFAULT (date('now'))" +
            ")";

        String createCertificates = 
            "CREATE TABLE IF NOT EXISTS certificates (" +
            "    cert_id        INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    resident_id    INTEGER NOT NULL," +
            "    cert_type      TEXT    NOT NULL," +
            "    purpose        TEXT    NOT NULL," +
            "    status         TEXT    DEFAULT 'Pending'," +
            "    date_requested TEXT    DEFAULT (date('now'))," +
            "    date_released  TEXT," +
            "    FOREIGN KEY (resident_id)" +
            "        REFERENCES residents(resident_id)" +
            ")";

        String insertAdmin = 
            "INSERT OR IGNORE INTO users (username, password, role) " +
            "VALUES ('admin', 'admin123', 'admin')";

        // try-with-resources: Connection and Statement auto-close
        try (Connection conn = getConnection();
             Statement  stmt = conn.createStatement()) {

            stmt.execute(createUsers);
            stmt.execute(createResidents);
            stmt.execute(createCertificates);
            stmt.execute(insertAdmin);

            System.out.println("[DB] Database initialized successfully.");

        } catch (SQLException e) {
            // Print the error and exit — if DB can't initialize,
            // the entire system cannot run. No point continuing.
            System.out.println("[DB ERROR] Failed to initialize database.");
            System.out.println("Cause: " + e.getMessage());
            System.exit(1);
        }
    }
}
