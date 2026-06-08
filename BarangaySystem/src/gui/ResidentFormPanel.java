package gui;

import model.*;
import db.DatabaseHandler;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * ResidentFormPanel.java
 *
 * Graphical panel for adding a new resident to the database.
 * Uses a sidebar navigation matching the official system layout.
 *
 * OOP Concepts Demonstrated:
 *   - Encapsulation      : UI construction and database insertions are kept private
 *   - Exception Handling : SQLException and NumberFormatException caught cleanly
 *   - Constructor        : parameterized constructor linking to MainFrame
 */
public class ResidentFormPanel extends JPanel {

    private MainFrame frame;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField ageField;
    private JComboBox<String> genderBox;
    private JComboBox<String> civilStatusBox;
    private JTextField addressField;
    private JTextField purokField;
    private JTextField contactField;
    private JCheckBox voterCheck;
    private JCheckBox indigentCheck;
    private JCheckBox pwdCheck;
    private JLabel statusLabel;

    public ResidentFormPanel(MainFrame frame) {
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
        sidebar.add(makeSidebarButton("Add Resident", null, true));
        sidebar.add(makeSidebarButton("Add Certificate", MainFrame.PANEL_ADD_CERT, false));
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
        JLabel pageTitle = new JLabel("Add Resident Record");
        pageTitle.setFont(new Font("Arial", Font.BOLD, 22));
        pageTitle.setForeground(MainFrame.COLOR_TEXT_PRIMARY);
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pageSubtitle = new JLabel("Enter personal demographic and profiling details for a new barangay resident.");
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

        // Form fields
        firstNameField = new JTextField();
        lastNameField = new JTextField();
        ageField = new JTextField();
        
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        civilStatusBox = new JComboBox<>(new String[]{"Single", "Married", "Widowed", "Separated"});
        
        addressField = new JTextField();
        purokField = new JTextField();
        contactField = new JTextField();

        voterCheck = new JCheckBox("Registered Voter");
        voterCheck.setBackground(MainFrame.COLOR_CARD);
        voterCheck.setForeground(MainFrame.COLOR_TEXT_PRIMARY);
        voterCheck.setFont(new Font("Arial", Font.PLAIN, 13));

        indigentCheck = new JCheckBox("Indigent Family");
        indigentCheck.setBackground(MainFrame.COLOR_CARD);
        indigentCheck.setForeground(MainFrame.COLOR_TEXT_PRIMARY);
        indigentCheck.setFont(new Font("Arial", Font.PLAIN, 13));

        pwdCheck = new JCheckBox("Person with Disability (PWD)");
        pwdCheck.setBackground(MainFrame.COLOR_CARD);
        pwdCheck.setForeground(MainFrame.COLOR_TEXT_PRIMARY);
        pwdCheck.setFont(new Font("Arial", Font.PLAIN, 13));

        // Row 1: First Name & Last Name
        addFormField(formCard, "First Name*", firstNameField, gbc, 0, 0);
        addFormField(formCard, "Last Name*", lastNameField, gbc, 1, 0);

        // Row 2: Age, Gender, Civil Status
        addFormField(formCard, "Age*", ageField, gbc, 0, 1);
        
        JPanel genderPanel = new JPanel(new BorderLayout());
        genderPanel.add(genderBox, BorderLayout.CENTER);
        addFormField(formCard, "Gender*", genderPanel, gbc, 1, 1);

        // Row 3: Civil Status & Contact
        JPanel civilPanel = new JPanel(new BorderLayout());
        civilPanel.add(civilStatusBox, BorderLayout.CENTER);
        addFormField(formCard, "Civil Status*", civilPanel, gbc, 0, 2);
        addFormField(formCard, "Contact Number (Optional)", contactField, gbc, 1, 2);

        // Row 4: Address & Purok
        addFormField(formCard, "Address*", addressField, gbc, 0, 3);
        addFormField(formCard, "Purok / Zone*", purokField, gbc, 1, 3);

        // Row 5: Demographic Tags
        JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        tagsPanel.setOpaque(false);
        tagsPanel.add(voterCheck);
        tagsPanel.add(indigentCheck);
        tagsPanel.add(pwdCheck);
        
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        formCard.add(makeFieldLabel("Demographic Profiling Options"), gbc);
        
        gbc.gridy = 9;
        formCard.add(tagsPanel, gbc);

        // Row 6: Status Message
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        statusLabel.setForeground(MainFrame.COLOR_SUCCESS);
        gbc.gridy = 10;
        formCard.add(statusLabel, gbc);

        // Row 7: Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        btnPanel.setOpaque(false);

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Arial", Font.BOLD, 13));
        clearBtn.setBackground(MainFrame.COLOR_CARD);
        clearBtn.setForeground(MainFrame.COLOR_TEXT_PRIMARY);
        clearBtn.setPreferredSize(new Dimension(100, 38));
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> clearForm());

        JButton saveBtn = new JButton("Save Record");
        saveBtn.setFont(new Font("Arial", Font.BOLD, 13));
        saveBtn.setBackground(MainFrame.COLOR_PRIMARY);
        saveBtn.setForeground(MainFrame.COLOR_TEXT_LIGHT);
        saveBtn.setPreferredSize(new Dimension(150, 38));
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.setOpaque(true);
        saveBtn.setBorderPainted(false);
        saveBtn.addActionListener(e -> saveResident());

        btnPanel.add(clearBtn);
        btnPanel.add(saveBtn);

        gbc.gridy = 11;
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
        } else if (comp instanceof JComboBox) {
            comp.setPreferredSize(new Dimension(0, 38));
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

    private void saveResident() {
        String fName = firstNameField.getText().trim();
        String lName = lastNameField.getText().trim();
        String ageStr = ageField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();
        String civilStatus = (String) civilStatusBox.getSelectedItem();
        String address = addressField.getText().trim();
        String purok = purokField.getText().trim();
        String contact = contactField.getText().trim();

        // Required validation
        if (fName.isEmpty() || lName.isEmpty() || ageStr.isEmpty() || address.isEmpty() || purok.isEmpty()) {
            statusLabel.setForeground(MainFrame.COLOR_DANGER);
            statusLabel.setText("Please fill in all required (*) fields.");
            return;
        }

        // Age numeric validation
        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 0 || age > 150) {
                statusLabel.setForeground(MainFrame.COLOR_DANGER);
                statusLabel.setText("Please enter a valid age (0 - 150).");
                return;
            }
        } catch (NumberFormatException e) {
            statusLabel.setForeground(MainFrame.COLOR_DANGER);
            statusLabel.setText("Age must be a valid number.");
            return;
        }

        boolean isVoter = voterCheck.isSelected();
        boolean isIndigent = indigentCheck.isSelected();
        boolean isPwd = pwdCheck.isSelected();
        boolean isSenior = age >= 60; // Auto-detected as per business rules

        // SQL Insertion
        String sql = "INSERT INTO residents (first_name, last_name, age, gender, civil_status, address, purok, contact, is_voter, is_indigent, is_senior, is_pwd) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

            pstmt.executeUpdate();

            statusLabel.setForeground(MainFrame.COLOR_SUCCESS);
            statusLabel.setText("Resident added successfully!");
            clearFormFields();

        } catch (SQLException e) {
            statusLabel.setForeground(MainFrame.COLOR_DANGER);
            statusLabel.setText("Database error: " + e.getMessage());
        }
    }

    private void clearForm() {
        clearFormFields();
        statusLabel.setText(" ");
    }

    private void clearFormFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        ageField.setText("");
        genderBox.setSelectedIndex(0);
        civilStatusBox.setSelectedIndex(0);
        addressField.setText("");
        purokField.setText("");
        contactField.setText("");
        voterCheck.setSelected(false);
        indigentCheck.setSelected(false);
        pwdCheck.setSelected(false);
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
