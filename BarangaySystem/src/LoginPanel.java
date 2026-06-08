import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * LoginPanel.java
 *
 * The login screen of the Barangay Record Management System.
 * Displayed as the first panel when the application launches.
 *
 * OOP Concepts Demonstrated:
 *   - Encapsulation      : login validation logic is private
 *   - Exception Handling : SQLException caught on DB query
 *   - Constructor        : parameterized constructor
 */
public class LoginPanel extends JPanel {

    // ── FIELDS ───────────────────────────────────────────────────────────
    private MainFrame    frame;
    private JTextField   usernameField;
    private JPasswordField passwordField;
    private JLabel       messageLabel;
    private JButton      loginButton;
    private int          attemptsRemaining = 3;

    // ── CONSTRUCTOR ──────────────────────────────────────────────────────
    public LoginPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(MainFrame.COLOR_PRIMARY);
        buildUI();
    }

    // ── UI BUILDER ───────────────────────────────────────────────────────

    private void buildUI() {
        // ── Left branding panel ──────────────────────────────────────────
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(MainFrame.COLOR_PRIMARY);
        leftPanel.setPreferredSize(new Dimension(420, 0));
        leftPanel.setLayout(new GridBagLayout());

        JPanel brandContent = new JPanel();
        brandContent.setOpaque(false);
        brandContent.setLayout(new BoxLayout(brandContent, BoxLayout.Y_AXIS));

        JLabel systemLabel = new JLabel("BARANGAY RECORD");
        systemLabel.setFont(new Font("Arial", Font.BOLD, 28));
        systemLabel.setForeground(MainFrame.COLOR_TEXT_LIGHT);
        systemLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel systemLabel2 = new JLabel("MANAGEMENT SYSTEM");
        systemLabel2.setFont(new Font("Arial", Font.BOLD, 28));
        systemLabel2.setForeground(MainFrame.COLOR_TEXT_LIGHT);
        systemLabel2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("Digitizing Barangay Services");
        tagline.setFont(new Font("Arial", Font.PLAIN, 14));
        tagline.setForeground(new Color(0xA8, 0xBE, 0xD8));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pupLabel = new JLabel("Polytechnic University of the Philippines");
        pupLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        pupLabel.setForeground(new Color(0xA8, 0xBE, 0xD8));
        pupLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        brandContent.add(systemLabel);
        brandContent.add(systemLabel2);
        brandContent.add(Box.createVerticalStrut(12));
        brandContent.add(tagline);
        brandContent.add(Box.createVerticalStrut(6));
        brandContent.add(pupLabel);

        leftPanel.add(brandContent);

        // ── Right login form panel ───────────────────────────────────────
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(MainFrame.COLOR_BACKGROUND);
        rightPanel.setLayout(new GridBagLayout());

        JPanel formCard = new JPanel();
        formCard.setBackground(MainFrame.COLOR_CARD);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(48, 48, 48, 48)
        ));

        // Form title
        JLabel titleLabel = new JLabel("Staff Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(MainFrame.COLOR_TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Enter your credentials to continue");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(MainFrame.COLOR_TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Username field
        JLabel usernameLabel = makeFieldLabel("Username");
        usernameField = makeTextField();

        // Password field
        JLabel passwordLabel = makeFieldLabel("Password");
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        passwordField.setPreferredSize(new Dimension(320, 42));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login button
        loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(MainFrame.COLOR_PRIMARY);
        loginButton.setForeground(MainFrame.COLOR_TEXT_LIGHT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginButton.setPreferredSize(new Dimension(320, 44));
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setOpaque(true);

        // Hover effect on login button
        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(MainFrame.COLOR_SECONDARY);
            }
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(MainFrame.COLOR_PRIMARY);
            }
        });

        // Message label for errors and attempt count
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        messageLabel.setForeground(MainFrame.COLOR_DANGER);
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Attempts label
        JLabel attemptsLabel = new JLabel("Default credentials: admin / admin123");
        attemptsLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        attemptsLabel.setForeground(MainFrame.COLOR_TEXT_SECONDARY);
        attemptsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Assemble form card
        formCard.add(titleLabel);
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(subtitleLabel);
        formCard.add(Box.createVerticalStrut(32));
        formCard.add(usernameLabel);
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(usernameField);
        formCard.add(Box.createVerticalStrut(18));
        formCard.add(passwordLabel);
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(passwordField);
        formCard.add(Box.createVerticalStrut(24));
        formCard.add(loginButton);
        formCard.add(Box.createVerticalStrut(12));
        formCard.add(messageLabel);
        formCard.add(Box.createVerticalStrut(8));
        formCard.add(attemptsLabel);

        rightPanel.add(formCard);

        // ── Assemble main layout ─────────────────────────────────────────
        add(leftPanel,  BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // ── Wire login action ────────────────────────────────────────────
        loginButton.addActionListener(e -> attemptLogin());

        // Allow Enter key to trigger login
        passwordField.addActionListener(e -> attemptLogin());
        usernameField.addActionListener(e -> passwordField.requestFocus());
    }

    // ── LOGIN LOGIC ──────────────────────────────────────────────────────

    /**
     * Validates credentials against the users table.
     * Decrements attempts on failure.
     * Locks system after 3 failures.
     *
     * Exception Handling: SQLException caught here.
     * Collections: not applicable for login.
     */
    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Username and password are required.");
            return;
        }

        if (validateCredentials(username, password)) {
            // Success — store user and switch to dashboard
            frame.setLoggedInUser(username);
            frame.showPanel(MainFrame.PANEL_DASHBOARD);
            clearForm();
        } else {
            attemptsRemaining--;
            if (attemptsRemaining > 0) {
                messageLabel.setText("Invalid credentials. " +
                    attemptsRemaining + " attempt(s) remaining.");
            } else {
                messageLabel.setText("Too many failed attempts. System will exit.");
                loginButton.setEnabled(false);

                // Exit after 2 seconds
                Timer exitTimer = new Timer(2000, e -> System.exit(0));
                exitTimer.setRepeats(false);
                exitTimer.start();
            }
        }
    }

    /**
     * Queries users table using PreparedStatement.
     * Returns true if credentials match exactly one record.
     */
    private boolean validateCredentials(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            messageLabel.setText("Database error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Clears the login form after successful login.
     * Resets attempt counter for next logout/login cycle.
     */
    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        messageLabel.setText(" ");
        attemptsRemaining = 3;
        loginButton.setEnabled(true);
    }

    // ── SHARED FIELD HELPERS ─────────────────────────────────────────────

    private JLabel makeFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(MainFrame.COLOR_TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField makeTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setPreferredSize(new Dimension(320, 42));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }
}
