import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * CertificateFormPanel.java
 *
 * Graphical panel for requesting certificate issuance in the system.
 * Connects directly to the SQLite database and validates resident existence.
 *
 * OOP Concepts Demonstrated:
 *   - Encapsulation      : UI construction and query validations are private
 *   - Exception Handling : SQLException and NumberFormatException caught gracefully
 *   - Constructor        : parameterized constructor linking to MainFrame
 */
public class CertificateFormPanel extends JPanel {

    private MainFrame frame;
    private JTextField residentIdField;
    private JComboBox<String> certTypeBox;
    private JTextField purposeField;
    private JLabel detailFieldLabel;
    private JTextField typeDetailField;
    private JLabel statusLabel;

    public CertificateFormPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(MainFrame.COLOR_BACKGROUND);
        buildUI();
    }

    private void buildUI() {
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);
        
        JPanel content = buildContent();
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling
        add(scrollPane, BorderLayout.CENTER);
    }

    // ── TOP BAR ──────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(MainFrame.COLOR_CARD);
        topBar.setPreferredSize(new Dimension(0, 56));
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, MainFrame.COLOR_BORDER),
            BorderFactory.createEmptyBorder(0, 24, 0, 24)
        ));

        JLabel titleLabel = new JLabel("Barangay Record Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));
        titleLabel.setForeground(MainFrame.COLOR_PRIMARY);

        JPanel rightSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightSection.setOpaque(false);

        JLabel userLabel = new JLabel("Administrator");
        userLabel.setFont(new Font("Arial", Font.BOLD, 13));
        userLabel.setForeground(MainFrame.COLOR_SECONDARY);

        rightSection.add(userLabel);

        topBar.add(titleLabel, BorderLayout.WEST);
        topBar.add(rightSection, BorderLayout.EAST);
        return topBar;
    }

    // ── SIDEBAR ──────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(MainFrame.COLOR_PRIMARY);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(24, 0, 24, 0));

        JLabel sysLabel = new JLabel("BRMS");
        sysLabel.setFont(new Font("Arial", Font.BOLD, 20));
        sysLabel.setForeground(MainFrame.COLOR_TEXT_LIGHT);
        sysLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        sysLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel versionLabel = new JLabel("Barangay RMS v1.0");
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        versionLabel.setForeground(new Color(0xA8, 0xBE, 0xD8));
        versionLabel.setBorder(BorderFactory.createEmptyBorder(2, 20, 0, 0));
        versionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(sysLabel);
        sidebar.add(versionLabel);
        sidebar.add(Box.createVerticalStrut(32));
        sidebar.add(makeSectionLabel("MENU"));

        sidebar.add(makeSidebarButton("Dashboard", MainFrame.PANEL_DASHBOARD, false));
        sidebar.add(makeSidebarButton("Add Resident", MainFrame.PANEL_ADD_RES, false));
        sidebar.add(makeSidebarButton("Add Certificate", null, true));
        sidebar.add(makeSidebarButton("View Records", MainFrame.PANEL_VIEW, false));
        sidebar.add(makeSidebarButton("Search", MainFrame.PANEL_SEARCH, false));
        sidebar.add(makeSidebarButton("Update", MainFrame.PANEL_UPDATE, false));
        sidebar.add(makeSidebarButton("Delete", MainFrame.PANEL_DELETE, false));
        sidebar.add(makeSidebarButton("Reports", MainFrame.PANEL_REPORT, false));

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(makeSectionLabel("SYSTEM"));

        JButton logoutSide = makeSidebarButton("Logout", null, false);
        logoutSide.setForeground(new Color(0xFF, 0x99, 0x99));
        logoutSide.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) frame.logout();
        });
        sidebar.add(logoutSide);

        return sidebar;
    }

    // ── MAIN CONTENT ──────────────────────────────────────────────────────
    private JPanel buildContent() {
        JPanel content = new JPanel();
        content.setBackground(MainFrame.COLOR_BACKGROUND);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        // Header Section
        JLabel pageTitle = new JLabel("Request Certificate");
        pageTitle.setFont(new Font("Arial", Font.BOLD, 22));
        pageTitle.setForeground(MainFrame.COLOR_TEXT_PRIMARY);
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pageSubtitle = new JLabel("Issue official certificates (Clearance, Indigency, Residency) for registered residents.");
        pageSubtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        pageSubtitle.setForeground(MainFrame.COLOR_TEXT_SECONDARY);
        pageSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(pageTitle);
        content.add(Box.createVerticalStrut(4));
        content.add(pageSubtitle);
        content.add(Box.createVerticalStrut(24));

        // Form Card Panel
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(MainFrame.COLOR_CARD);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(32, 32, 32, 32)
        ));
        formCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.weightx = 0.5;

        // Form Fields
        residentIdField = new JTextField();
        purposeField = new JTextField();
        
        certTypeBox = new JComboBox<>(new String[]{
            "Barangay Clearance",
            "Indigency Certificate",
            "Certificate of Residency"
        });
        
        typeDetailField = new JTextField();
        detailFieldLabel = new JLabel("Clearance Type (e.g., Employment, Travel, Legal)*");

        // Action Listener for dynamic label switching
        certTypeBox.addActionListener(e -> {
            String selected = (String) certTypeBox.getSelectedItem();
            if ("Barangay Clearance".equals(selected)) {
                detailFieldLabel.setText("Clearance Type (e.g., Employment, Travel, Legal)*");
            } else if ("Indigency Certificate".equals(selected)) {
                detailFieldLabel.setText("Assistance Type (e.g., Medical, Burial, Educational)*");
            } else if ("Certificate of Residency".equals(selected)) {
                detailFieldLabel.setText("Residency Purpose (e.g., School Enrollment, Bank)*");
            }
        });

        // Add to layout
        addFormField(formCard, "Resident ID*", residentIdField, gbc, 0, 0);
        
        JPanel comboPanel = new JPanel(new BorderLayout());
        comboPanel.add(certTypeBox, BorderLayout.CENTER);
        addFormField(formCard, "Certificate Type*", comboPanel, gbc, 1, 0);

        addFormField(formCard, "Purpose*", purposeField, gbc, 0, 1);
        
        // Dynamically labeled field
        gbc.gridx = 1;
        gbc.gridy = 2;
        formCard.add(detailFieldLabel, gbc);
        gbc.gridy = 3;
        typeDetailField.setPreferredSize(new Dimension(0, 38));
        typeDetailField.setFont(new Font("Arial", Font.PLAIN, 13));
        typeDetailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        formCard.add(typeDetailField, gbc);

        // Status Label Row
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        statusLabel.setForeground(MainFrame.COLOR_SUCCESS);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formCard.add(statusLabel, gbc);

        // Button Row
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        btnPanel.setOpaque(false);

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Arial", Font.BOLD, 13));
        clearBtn.setBackground(MainFrame.COLOR_CARD);
        clearBtn.setForeground(MainFrame.COLOR_TEXT_PRIMARY);
        clearBtn.setPreferredSize(new Dimension(100, 38));
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> clearForm());

        JButton saveBtn = new JButton("Submit Request");
        saveBtn.setFont(new Font("Arial", Font.BOLD, 13));
        saveBtn.setBackground(MainFrame.COLOR_PRIMARY);
        saveBtn.setForeground(MainFrame.COLOR_TEXT_LIGHT);
        saveBtn.setPreferredSize(new Dimension(150, 38));
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.setOpaque(true);
        saveBtn.setBorderPainted(false);
        saveBtn.addActionListener(e -> saveCertificateRequest());

        btnPanel.add(clearBtn);
        btnPanel.add(saveBtn);

        gbc.gridy = 5;
        formCard.add(btnPanel, gbc);

        content.add(formCard);
        content.add(Box.createVerticalGlue());

        return content;
    }

    private void addFormField(JPanel panel, String labelStr, Component comp, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y * 2;
        gbc.gridwidth = 1;
        panel.add(makeFieldLabel(labelStr), gbc);

        gbc.gridy = y * 2 + 1;
        
        if (comp instanceof JTextField) {
            JTextField tf = (JTextField) comp;
            tf.setPreferredSize(new Dimension(0, 38));
            tf.setFont(new Font("Arial", Font.PLAIN, 13));
            tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
            ));
        } else if (comp instanceof JPanel) {
            comp.setPreferredSize(new Dimension(0, 38));
        }
        
        panel.add(comp, gbc);
    }

    private JLabel makeFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(MainFrame.COLOR_TEXT_PRIMARY);
        return label;
    }

    private void saveCertificateRequest() {
        String resIdStr = residentIdField.getText().trim();
        String certType = (String) certTypeBox.getSelectedItem();
        String purpose = purposeField.getText().trim();
        String detail = typeDetailField.getText().trim();

        // Validation
        if (resIdStr.isEmpty() || purpose.isEmpty() || detail.isEmpty()) {
            statusLabel.setForeground(MainFrame.COLOR_DANGER);
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        int residentId;
        try {
            residentId = Integer.parseInt(resIdStr);
        } catch (NumberFormatException e) {
            statusLabel.setForeground(MainFrame.COLOR_DANGER);
            statusLabel.setText("Resident ID must be a valid number.");
            return;
        }

        // Database logic
        try (Connection conn = DatabaseHandler.getConnection()) {
            // First verify resident exists
            if (!residentExists(conn, residentId)) {
                statusLabel.setForeground(MainFrame.COLOR_DANGER);
                statusLabel.setText("Resident ID " + residentId + " does not exist.");
                return;
            }

            // Insert into certificates
            String sql = "INSERT INTO certificates (resident_id, cert_type, purpose, status) VALUES (?, ?, ?, 'Pending')";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, residentId);
                pstmt.setString(2, certType);
                pstmt.setString(3, purpose + " (" + detail + ")");
                pstmt.executeUpdate();
            }

            statusLabel.setForeground(MainFrame.COLOR_SUCCESS);
            statusLabel.setText("Certificate request submitted successfully!");
            clearFormFields();

        } catch (SQLException e) {
            statusLabel.setForeground(MainFrame.COLOR_DANGER);
            statusLabel.setText("Database error: " + e.getMessage());
        }
    }

    private boolean residentExists(Connection conn, int residentId) throws SQLException {
        String sql = "SELECT resident_id FROM residents WHERE resident_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, residentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void clearForm() {
        clearFormFields();
        statusLabel.setText(" ");
    }

    private void clearFormFields() {
        residentIdField.setText("");
        purposeField.setText("");
        typeDetailField.setText("");
        certTypeBox.setSelectedIndex(0);
    }

    // ── HELPER GENERATOR METHODS ───────────────────────────────────────────
    private JButton makeTextButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setForeground(MainFrame.COLOR_SECONDARY);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeSidebarButton(String text, String panelName, boolean isActive) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", isActive ? Font.BOLD : Font.PLAIN, 13));
        btn.setForeground(isActive ? MainFrame.COLOR_ACCENT : MainFrame.COLOR_TEXT_LIGHT);
        btn.setBackground(isActive ? new Color(0x24, 0x4A, 0x82) : MainFrame.COLOR_PRIMARY);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setPreferredSize(new Dimension(220, 44));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!isActive) btn.setBackground(new Color(0x0D, 0x2B, 0x55));
            }
            public void mouseExited(MouseEvent e) {
                if (!isActive) btn.setBackground(MainFrame.COLOR_PRIMARY);
            }
        });

        if (panelName != null) {
            btn.addActionListener(e -> frame.showPanel(panelName));
        }
        return btn;
    }

    private JLabel makeSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 10));
        label.setForeground(new Color(0x70, 0x90, 0xB0));
        label.setBorder(BorderFactory.createEmptyBorder(8, 20, 4, 20));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
}
