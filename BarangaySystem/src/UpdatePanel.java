import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

/**
 * UpdatePanel.java
 *
 * Graphical panel for updating resident profiles and certificate request statuses.
 * Provides interactive loading of records from the database, visual editing, and updates.
 *
 * OOP Concepts Demonstrated:
 *   - Encapsulation      : UI rendering and database transactional queries are private
 *   - Exception Handling : SQLException and NumberFormatException caught gracefully
 *   - Constructor        : parameterized constructor linking to MainFrame
 */
public class UpdatePanel extends JPanel {

    private MainFrame frame;
    
    // Resident Update Fields
    private JTextField resSearchIdField;
    private JTextField resFirstNameField;
    private JTextField resLastNameField;
    private JTextField resAgeField;
    private JComboBox<String> resGenderBox;
    private JComboBox<String> resCivilStatusBox;
    private JTextField resAddressField;
    private JTextField resPurokField;
    private JTextField resContactField;
    private JCheckBox resVoterCheck;
    private JCheckBox resIndigentCheck;
    private JCheckBox resPwdCheck;
    private JLabel resStatusLabel;
    private int loadedResidentId = -1;

    // Certificate Update Fields
    private JTextField certSearchIdField;
    private JLabel certResidentLabel;
    private JLabel certTypeLabel;
    private JLabel certPurposeLabel;
    private JComboBox<String> certStatusBox;
    private JLabel certStatusLabel;
    private int loadedCertId = -1;

    public UpdatePanel(MainFrame frame) {
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
        sidebar.add(makeSidebarButton("Add Certificate", MainFrame.PANEL_ADD_CERT, false));
        sidebar.add(makeSidebarButton("View Records", MainFrame.PANEL_VIEW, false));
        sidebar.add(makeSidebarButton("Search", MainFrame.PANEL_SEARCH, false));
        sidebar.add(makeSidebarButton("Update", null, true));
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
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(MainFrame.COLOR_BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JLabel pageTitle = new JLabel("Update Records");
        pageTitle.setFont(new Font("Arial", Font.BOLD, 22));
        pageTitle.setForeground(MainFrame.COLOR_TEXT_PRIMARY);

        JLabel pageSubtitle = new JLabel("Modify resident registration particulars or transition certificate requests along the status pipeline.");
        pageSubtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        pageSubtitle.setForeground(MainFrame.COLOR_TEXT_SECONDARY);

        headerPanel.add(pageTitle);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(pageSubtitle);
        content.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Panel
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));

        tabbedPane.addTab("Update Resident Profile", buildUpdateResidentTab());
        tabbedPane.addTab("Update Certificate Request", buildUpdateCertificateTab());

        content.add(tabbedPane, BorderLayout.CENTER);

        return content;
    }

    // ── RESIDENT UPDATE TAB ───────────────────────────────────────────────
    private JPanel buildUpdateResidentTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(MainFrame.COLOR_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Search Bar Row
        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        searchBarPanel.setOpaque(false);
        
        JLabel idLabel = new JLabel("Resident ID to Update:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 13));
        idLabel.setForeground(MainFrame.COLOR_TEXT_PRIMARY);
        
        resSearchIdField = new JTextField();
        resSearchIdField.setPreferredSize(new Dimension(100, 38));
        resSearchIdField.setFont(new Font("Arial", Font.PLAIN, 13));
        resSearchIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        JButton loadBtn = new JButton("Load Resident Details");
        loadBtn.setFont(new Font("Arial", Font.BOLD, 13));
        loadBtn.setBackground(MainFrame.COLOR_SECONDARY);
        loadBtn.setForeground(MainFrame.COLOR_TEXT_LIGHT);
        loadBtn.setPreferredSize(new Dimension(180, 38));
        loadBtn.setOpaque(true);
        loadBtn.setBorderPainted(false);
        loadBtn.addActionListener(e -> loadResidentDetails());

        // Dynamic load on Enter
        resSearchIdField.addActionListener(e -> loadResidentDetails());

        searchBarPanel.add(idLabel);
        searchBarPanel.add(resSearchIdField);
        searchBarPanel.add(loadBtn);

        panel.add(searchBarPanel, BorderLayout.NORTH);

        // Edit Form (GridBagLayout)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, MainFrame.COLOR_BORDER),
            BorderFactory.createEmptyBorder(16, 0, 0, 0)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(8, 8, 8, 8);

        resFirstNameField = new JTextField();
        resLastNameField = new JTextField();
        resAgeField = new JTextField();
        resGenderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        resCivilStatusBox = new JComboBox<>(new String[]{"Single", "Married", "Widowed", "Separated"});
        resAddressField = new JTextField();
        resPurokField = new JTextField();
        resContactField = new JTextField();

        resVoterCheck = new JCheckBox("Registered Voter");
        resVoterCheck.setBackground(MainFrame.COLOR_CARD);
        resVoterCheck.setForeground(MainFrame.COLOR_TEXT_PRIMARY);

        resIndigentCheck = new JCheckBox("Indigent");
        resIndigentCheck.setBackground(MainFrame.COLOR_CARD);
        resIndigentCheck.setForeground(MainFrame.COLOR_TEXT_PRIMARY);

        resPwdCheck = new JCheckBox("PWD");
        resPwdCheck.setBackground(MainFrame.COLOR_CARD);
        resPwdCheck.setForeground(MainFrame.COLOR_TEXT_PRIMARY);

        // Disabled by default until a resident is searched
        setResidentFieldsEnabled(false);

        // Adding Fields to Form
        addFormField(formPanel, "First Name*", resFirstNameField, gbc, 0, 0);
        addFormField(formPanel, "Last Name*", resLastNameField, gbc, 1, 0);

        addFormField(formPanel, "Age*", resAgeField, gbc, 0, 1);
        
        JPanel genderWrapper = new JPanel(new BorderLayout());
        genderWrapper.add(resGenderBox, BorderLayout.CENTER);
        addFormField(formPanel, "Gender*", genderWrapper, gbc, 1, 1);

        JPanel civilWrapper = new JPanel(new BorderLayout());
        civilWrapper.add(resCivilStatusBox, BorderLayout.CENTER);
        addFormField(formPanel, "Civil Status*", civilWrapper, gbc, 0, 2);
        addFormField(formPanel, "Contact Number", resContactField, gbc, 1, 2);

        addFormField(formPanel, "Address*", resAddressField, gbc, 0, 3);
        addFormField(formPanel, "Purok*", resPurokField, gbc, 1, 3);

        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        checkPanel.setOpaque(false);
        checkPanel.add(resVoterCheck);
        checkPanel.add(resIndigentCheck);
        checkPanel.add(resPwdCheck);

        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        formPanel.add(makeFieldLabel("Profiling / Demographic Tags"), gbc);
        gbc.gridy = 9;
        formPanel.add(checkPanel, gbc);

        // Status Label Row
        resStatusLabel = new JLabel(" ");
        resStatusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridy = 10;
        formPanel.add(resStatusLabel, gbc);

        // Submit Action Row
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        JButton saveBtn = new JButton("Save Profile Changes");
        saveBtn.setFont(new Font("Arial", Font.BOLD, 13));
        saveBtn.setBackground(MainFrame.COLOR_PRIMARY);
        saveBtn.setForeground(MainFrame.COLOR_TEXT_LIGHT);
        saveBtn.setPreferredSize(new Dimension(185, 38));
        saveBtn.setOpaque(true);
        saveBtn.setBorderPainted(false);
        saveBtn.addActionListener(e -> saveResidentProfile());
        
        btnPanel.add(saveBtn);
        gbc.gridy = 11;
        formPanel.add(btnPanel, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    // ── CERTIFICATE UPDATE TAB ────────────────────────────────────────────
    private JPanel buildUpdateCertificateTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(MainFrame.COLOR_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Search Panel
        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        searchBarPanel.setOpaque(false);
        
        JLabel idLabel = new JLabel("Certificate Request ID:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 13));
        idLabel.setForeground(MainFrame.COLOR_TEXT_PRIMARY);
        
        certSearchIdField = new JTextField();
        certSearchIdField.setPreferredSize(new Dimension(100, 38));
        certSearchIdField.setFont(new Font("Arial", Font.PLAIN, 13));
        certSearchIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        JButton loadBtn = new JButton("Load Request Details");
        loadBtn.setFont(new Font("Arial", Font.BOLD, 13));
        loadBtn.setBackground(MainFrame.COLOR_SECONDARY);
        loadBtn.setForeground(MainFrame.COLOR_TEXT_LIGHT);
        loadBtn.setPreferredSize(new Dimension(180, 38));
        loadBtn.setOpaque(true);
        loadBtn.setBorderPainted(false);
        loadBtn.addActionListener(e -> loadCertificateDetails());

        certSearchIdField.addActionListener(e -> loadCertificateDetails());

        searchBarPanel.add(idLabel);
        searchBarPanel.add(certSearchIdField);
        searchBarPanel.add(loadBtn);

        panel.add(searchBarPanel, BorderLayout.NORTH);

        // Display panel card for loaded request details
        JPanel displayCard = new JPanel(new GridBagLayout());
        displayCard.setOpaque(false);
        displayCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, MainFrame.COLOR_BORDER),
            BorderFactory.createEmptyBorder(24, 8, 8, 8)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(10, 10, 10, 10);

        certResidentLabel = new JLabel("No certificate request loaded.");
        certResidentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        certResidentLabel.setForeground(MainFrame.COLOR_TEXT_PRIMARY);

        certTypeLabel = new JLabel("Type: --");
        certTypeLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        certTypeLabel.setForeground(MainFrame.COLOR_TEXT_SECONDARY);

        certPurposeLabel = new JLabel("Purpose: --");
        certPurposeLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        certPurposeLabel.setForeground(MainFrame.COLOR_TEXT_SECONDARY);

        certStatusBox = new JComboBox<>(new String[]{"Pending", "Approved", "Released"});
        certStatusBox.setFont(new Font("Arial", Font.PLAIN, 13));
        certStatusBox.setPreferredSize(new Dimension(180, 38));
        certStatusBox.setEnabled(false);

        certStatusLabel = new JLabel(" ");
        certStatusLabel.setFont(new Font("Arial", Font.BOLD, 13));

        JButton updateBtn = new JButton("Update Status");
        updateBtn.setFont(new Font("Arial", Font.BOLD, 13));
        updateBtn.setBackground(MainFrame.COLOR_PRIMARY);
        updateBtn.setForeground(MainFrame.COLOR_TEXT_LIGHT);
        updateBtn.setPreferredSize(new Dimension(150, 38));
        updateBtn.setOpaque(true);
        updateBtn.setBorderPainted(false);
        updateBtn.addActionListener(e -> saveCertificateStatus());

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        displayCard.add(certResidentLabel, gbc);

        gbc.gridy = 1; gbc.gridwidth = 1;
        displayCard.add(certTypeLabel, gbc);
        gbc.gridx = 1;
        displayCard.add(certPurposeLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        displayCard.add(new JLabel("Set Pipeline Status:"), gbc);
        gbc.gridy = 3;
        JPanel comboWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        comboWrapper.setOpaque(false);
        comboWrapper.add(certStatusBox);
        displayCard.add(comboWrapper, gbc);

        gbc.gridy = 4;
        displayCard.add(certStatusLabel, gbc);

        gbc.gridy = 5;
        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnWrapper.setOpaque(false);
        btnWrapper.add(updateBtn);
        displayCard.add(btnWrapper, gbc);

        panel.add(displayCard, BorderLayout.CENTER);

        return panel;
    }

    // ── DATABASE OPERATIONS ───────────────────────────────────────────────
    private void loadResidentDetails() {
        String idStr = resSearchIdField.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Resident ID.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Resident ID must be a numeric integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "SELECT * FROM residents WHERE resident_id = ?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    resFirstNameField.setText(rs.getString("first_name"));
                    resLastNameField.setText(rs.getString("last_name"));
                    resAgeField.setText(String.valueOf(rs.getInt("age")));
                    resGenderBox.setSelectedItem(rs.getString("gender"));
                    resCivilStatusBox.setSelectedItem(rs.getString("civil_status"));
                    resAddressField.setText(rs.getString("address"));
                    resPurokField.setText(rs.getString("purok"));
                    resContactField.setText(rs.getString("contact") != null ? rs.getString("contact") : "");
                    resVoterCheck.setSelected(rs.getInt("is_voter") == 1);
                    resIndigentCheck.setSelected(rs.getInt("is_indigent") == 1);
                    resPwdCheck.setSelected(rs.getInt("is_pwd") == 1);

                    loadedResidentId = id;
                    setResidentFieldsEnabled(true);
                    resStatusLabel.setForeground(MainFrame.COLOR_SUCCESS);
                    resStatusLabel.setText("Resident profile loaded successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Resident ID " + id + " not found.", "Not Found", JOptionPane.ERROR_MESSAGE);
                    clearResidentForm();
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB load failure: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveResidentProfile() {
        if (loadedResidentId == -1) {
            JOptionPane.showMessageDialog(this, "No resident record has been loaded.", "Save Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String fName = resFirstNameField.getText().trim();
        String lName = resLastNameField.getText().trim();
        String ageStr = resAgeField.getText().trim();
        String gender = (String) resGenderBox.getSelectedItem();
        String civilStatus = (String) resCivilStatusBox.getSelectedItem();
        String address = resAddressField.getText().trim();
        String purok = resPurokField.getText().trim();
        String contact = resContactField.getText().trim();

        if (fName.isEmpty() || lName.isEmpty() || ageStr.isEmpty() || address.isEmpty() || purok.isEmpty()) {
            resStatusLabel.setForeground(MainFrame.COLOR_DANGER);
            resStatusLabel.setText("Please fill out all required (*) fields.");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 0 || age > 150) {
                resStatusLabel.setForeground(MainFrame.COLOR_DANGER);
                resStatusLabel.setText("Age must be in range (0 - 150).");
                return;
            }
        } catch (NumberFormatException e) {
            resStatusLabel.setForeground(MainFrame.COLOR_DANGER);
            resStatusLabel.setText("Age must be a numeric integer.");
            return;
        }

        boolean isVoter = resVoterCheck.isSelected();
        boolean isIndigent = resIndigentCheck.isSelected();
        boolean isPwd = resPwdCheck.isSelected();
        boolean isSenior = age >= 60; // Auto detected

        String sql = "UPDATE residents SET first_name = ?, last_name = ?, age = ?, gender = ?, civil_status = ?, address = ?, purok = ?, contact = ?, is_voter = ?, is_indigent = ?, is_senior = ?, is_pwd = ? WHERE resident_id = ?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fName);
            pstmt.setString(2, lName);
            pstmt.setInt(3, age);
            pstmt.setString(4, gender);
            pstmt.setString(5, civilStatus);
            pstmt.setString(6, address);
            pstmt.setString(7, purok);
            pstmt.setString(8, contact.isEmpty() ? null : contact);
            pstmt.setInt(9, isVoter ? 1 : 0);
            pstmt.setInt(10, isIndigent ? 1 : 0);
            pstmt.setInt(11, isSenior ? 1 : 0);
            pstmt.setInt(12, isPwd ? 1 : 0);
            pstmt.setInt(13, loadedResidentId);

            pstmt.executeUpdate();
            
            resStatusLabel.setForeground(MainFrame.COLOR_SUCCESS);
            resStatusLabel.setText("Resident ID " + loadedResidentId + " profile updated successfully!");
            clearResidentForm();

        } catch (SQLException e) {
            resStatusLabel.setForeground(MainFrame.COLOR_DANGER);
            resStatusLabel.setText("Failed to update resident profile: " + e.getMessage());
        }
    }

    private void loadCertificateDetails() {
        String idStr = certSearchIdField.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Request ID.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Request ID must be a numeric integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "SELECT c.*, r.first_name || ' ' || r.last_name AS res_name FROM certificates c JOIN residents r ON c.resident_id = r.resident_id WHERE c.cert_id = ?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    certResidentLabel.setText("Requester: " + rs.getString("res_name") + " (Resident ID: " + rs.getInt("resident_id") + ")");
                    certTypeLabel.setText("Type: " + rs.getString("cert_type"));
                    certPurposeLabel.setText("Purpose: " + rs.getString("purpose"));
                    certStatusBox.setSelectedItem(rs.getString("status"));

                    loadedCertId = id;
                    certStatusBox.setEnabled(true);
                    certStatusLabel.setForeground(MainFrame.COLOR_SUCCESS);
                    certStatusLabel.setText("Certificate request loaded!");
                } else {
                    JOptionPane.showMessageDialog(this, "Certificate ID " + id + " not found.", "Not Found", JOptionPane.ERROR_MESSAGE);
                    clearCertificateForm();
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB load failure: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveCertificateStatus() {
        if (loadedCertId == -1) {
            JOptionPane.showMessageDialog(this, "No certificate request loaded.", "Update Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String status = (String) certStatusBox.getSelectedItem();
        String dateReleased = "Released".equals(status) ? LocalDate.now().toString() : null;

        String sql = "UPDATE certificates SET status = ?, date_released = ? WHERE cert_id = ?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setString(2, dateReleased);
            pstmt.setInt(3, loadedCertId);

            pstmt.executeUpdate();

            certStatusLabel.setForeground(MainFrame.COLOR_SUCCESS);
            certStatusLabel.setText("Certificate status set to \"" + status + "\"!");
            clearCertificateForm();

        } catch (SQLException e) {
            certStatusLabel.setForeground(MainFrame.COLOR_DANGER);
            certStatusLabel.setText("Update failure: " + e.getMessage());
        }
    }

    // ── FORM CLEANERS ──────────────────────────────────────────────────────
    private void clearResidentForm() {
        resFirstNameField.setText("");
        resLastNameField.setText("");
        resAgeField.setText("");
        resGenderBox.setSelectedIndex(0);
        resCivilStatusBox.setSelectedIndex(0);
        resAddressField.setText("");
        resPurokField.setText("");
        resContactField.setText("");
        resVoterCheck.setSelected(false);
        resIndigentCheck.setSelected(false);
        resPwdCheck.setSelected(false);
        setResidentFieldsEnabled(false);
        loadedResidentId = -1;
    }

    private void clearCertificateForm() {
        certResidentLabel.setText("No certificate request loaded.");
        certTypeLabel.setText("Type: --");
        certPurposeLabel.setText("Purpose: --");
        certStatusBox.setSelectedIndex(0);
        certStatusBox.setEnabled(false);
        loadedCertId = -1;
    }

    private void setResidentFieldsEnabled(boolean val) {
        resFirstNameField.setEnabled(val);
        resLastNameField.setEnabled(val);
        resAgeField.setEnabled(val);
        resGenderBox.setEnabled(val);
        resCivilStatusBox.setEnabled(val);
        resAddressField.setEnabled(val);
        resPurokField.setEnabled(val);
        resContactField.setEnabled(val);
        resVoterCheck.setEnabled(val);
        resIndigentCheck.setEnabled(val);
        resPwdCheck.setEnabled(val);
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
