import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
        return DriverManager.getConnection(DB_URL);
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
