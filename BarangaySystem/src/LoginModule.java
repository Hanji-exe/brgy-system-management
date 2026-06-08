import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * LoginModule.java
 *
 * OOP Concepts Demonstrated:
 * - Exception Handling : try-catch on all DB operations
 * - Encapsulation : login logic isolated in one class
 *
 * Input Method: JOptionPane (satisfies the JOptionPane requirement)
 * Flow: User gets 3 attempts. On 3rd failure, system exits.
 */
public class LoginModule {

    // Maximum login attempts before system locks
    private static final int MAX_ATTEMPTS = 3;

    // ── CONSTRUCTORS ────────────────────────────────────────────────────────
    public LoginModule() {
    }

    // ── MAIN LOGIN METHOD ───────────────────────────────────────────────────

    /**
     * Runs the login screen using JOptionPane dialogs.
     * Returns true if login is successful, false if all attempts fail.
     *
     * Logic flow:
     * 1. Show username input dialog
     * 2. Show password input dialog
     * 3. Query users table for matching credentials
     * 4. If match found → return true
     * 5. If no match → decrement attempts, loop
     * 6. If 0 attempts left → show error, exit system
     */
    public boolean login() {
        int attempts = 0;

        while (attempts < MAX_ATTEMPTS) {
            // ── Step 1: Get username via JOptionPane ──────────────────────
            String username = JOptionPane.showInputDialog(
                    null,
                    "Enter Username:",
                    "Barangay Record Management System — Login",
                    JOptionPane.PLAIN_MESSAGE);

            // User clicked Cancel or closed the dialog
            if (username == null) {
                System.out.println("Login cancelled. Exiting system.");
                System.exit(0);
            }

            // ── Step 2: Get password via JOptionPane ──────────────────────
            String password = JOptionPane.showInputDialog(
                    null,
                    "Enter Password:",
                    "Barangay Record Management System — Login",
                    JOptionPane.PLAIN_MESSAGE);

            if (password == null) {
                System.out.println("Login cancelled. Exiting system.");
                System.exit(0);
            }

            // ── Step 3: Validate credentials against DB ───────────────────
            if (validateCredentials(username.trim(), password.trim())) {
                JOptionPane.showMessageDialog(
                        null,
                        "Welcome, " + username + "!\nLogin successful.",
                        "Login Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                attempts++;
                int remaining = MAX_ATTEMPTS - attempts;

                if (remaining > 0) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Invalid username or password.\n" +
                                    "Attempts remaining: " + remaining,
                            "Login Failed",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        }

        // ── Step 4: All attempts exhausted ────────────────────────────────
        JOptionPane.showMessageDialog(
                null,
                "Too many failed attempts.\nSystem will now exit.",
                "Access Denied",
                JOptionPane.ERROR_MESSAGE);
        System.exit(0);
        return false; // unreachable but required by compiler
    }

    // ── PRIVATE HELPER ──────────────────────────────────────────────────────

    /**
     * Queries the users table for matching username + password.
     * Uses PreparedStatement — prevents SQL injection.
     *
     * Returns true if exactly one matching record is found.
     * Returns false if no match or if a DB error occurs.
     */
    private boolean validateCredentials(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseHandler.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // true if a row was found

        } catch (SQLException e) {
            System.out.println("[LOGIN ERROR] " + e.getMessage());
            return false;
        }
    }
}
