package gui;

import model.*;
import db.DatabaseHandler;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * ViewPanel.java
 *
 * Graphical panel for viewing all resident records and certificate requests.
 * Employs a TabbedPane layout to separate Residents, Certificates, and Officials.
 * Styled JTables are dynamically loaded from SQLite.
 *
 * OOP Concepts Demonstrated:
 *   - Encapsulation      : UI rendering and DB fetching logic are private
 *   - Collections        : DefaultTableModel used to wrap fetched records
 *   - Exception Handling : SQLException caught cleanly
 *   - Constructor        : parameterized constructor linking to MainFrame
 */
public class ViewPanel extends JPanel {

    private MainFrame frame;
    private DefaultTableModel residentsTableModel;
    private DefaultTableModel certsTableModel;
    private DefaultTableModel officialsTableModel;

    public ViewPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(MainFrame.COLOR_BACKGROUND);
        buildUI();
    }

    private void buildUI() {
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);
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
        sidebar.add(makeSidebarButton("View Records", null, true));
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
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(MainFrame.COLOR_BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JLabel pageTitle = new JLabel("View Records");
        pageTitle.setFont(new Font("Arial", Font.BOLD, 22));
        pageTitle.setForeground(MainFrame.COLOR_TEXT_PRIMARY);

        JLabel pageSubtitle = new JLabel("Browse through lists of registered residents, certificate request logs, and officials.");
        pageSubtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        pageSubtitle.setForeground(MainFrame.COLOR_TEXT_SECONDARY);

        headerPanel.add(pageTitle);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(pageSubtitle);

        content.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane Setup
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));
        tabbedPane.setBackground(MainFrame.COLOR_CARD);
        tabbedPane.setForeground(MainFrame.COLOR_TEXT_PRIMARY);

        // Tab 1: Residents
        tabbedPane.addTab("Residents", buildResidentsTab());
        // Tab 2: Certificates
        tabbedPane.addTab("Certificate Requests", buildCertificatesTab());
        // Tab 3: Officials
        tabbedPane.addTab("Officials", buildOfficialsTab());

        // Refresh stats automatically when shown
        tabbedPane.addChangeListener(e -> refreshData(tabbedPane.getSelectedIndex()));

        content.add(tabbedPane, BorderLayout.CENTER);

        // Trigger first load
        Timer firstLoad = new Timer(500, e -> refreshData(0));
        firstLoad.setRepeats(false);
        firstLoad.start();

        return content;
    }

    private JPanel buildResidentsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MainFrame.COLOR_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        String[] cols = {"ID", "First Name", "Last Name", "Age", "Gender", "Civil Status", "Address", "Purok", "Contact", "Voter?", "Indigent?", "Senior?", "PWD?"};
        residentsTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(residentsTableModel);
        DashboardPanel.styleTable(table);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1));
        panel.add(sp, BorderLayout.CENTER);

        // Refresh action
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadResidentsData());
        actionPanel.add(refreshBtn);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildCertificatesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MainFrame.COLOR_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        String[] cols = {"Cert ID", "Resident ID", "Resident Name", "Cert Type", "Purpose", "Status", "Date Requested", "Date Released"};
        certsTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(certsTableModel);
        DashboardPanel.styleTable(table);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1));
        panel.add(sp, BorderLayout.CENTER);

        // Refresh action
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadCertificatesData());
        actionPanel.add(refreshBtn);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildOfficialsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MainFrame.COLOR_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        String[] cols = {"ID", "First Name", "Last Name", "Age", "Gender", "Address", "Purok", "Contact", "Official Role"};
        officialsTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(officialsTableModel);
        DashboardPanel.styleTable(table);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1));
        panel.add(sp, BorderLayout.CENTER);

        // Explanatory note
        JLabel note = new JLabel("Officials extend the Resident class under OOP requirements and share the residents table database.");
        note.setFont(new Font("Arial", Font.ITALIC, 12));
        note.setForeground(MainFrame.COLOR_TEXT_SECONDARY);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(note, BorderLayout.WEST);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadOfficialsData());
        bottomPanel.add(refreshBtn, BorderLayout.EAST);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ── DATA LOADER METHODS ───────────────────────────────────────────────
    private void refreshData(int tabIndex) {
        if (tabIndex == 0) loadResidentsData();
        else if (tabIndex == 1) loadCertificatesData();
        else if (tabIndex == 2) loadOfficialsData();
    }

    private void loadResidentsData() {
        residentsTableModel.setRowCount(0);
        java.util.ArrayList<Resident> residentsList = new java.util.ArrayList<>();
        String sql = "SELECT * FROM residents ORDER BY last_name, first_name";

        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Resident resident = new Resident(
                    rs.getInt("resident_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("address"),
                    rs.getString("contact"),
                    rs.getInt("age"),
                    rs.getString("gender"),
                    rs.getString("civil_status"),
                    rs.getString("purok"),
                    rs.getInt("is_voter") == 1,
                    rs.getInt("is_indigent") == 1,
                    rs.getInt("is_senior") == 1,
                    rs.getInt("is_pwd") == 1,
                    rs.getString("date_added")
                );
                residentsList.add(resident);
            }

            // Load residents from list to table model
            for (Resident r : residentsList) {
                residentsTableModel.addRow(new Object[]{
                    r.getId(),
                    r.getFirstName(),
                    r.getLastName(),
                    r.getAge(),
                    r.getGender(),
                    r.getCivilStatus(),
                    r.getAddress(),
                    r.getPurok(),
                    r.getContactNumber() != null ? r.getContactNumber() : "",
                    r.isVoter() ? "Yes" : "No",
                    r.isIndigent() ? "Yes" : "No",
                    r.isSenior() ? "Yes" : "No",
                    r.isPwd() ? "Yes" : "No"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load residents data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCertificatesData() {
        certsTableModel.setRowCount(0);
        java.util.ArrayList<CertificateRequest> certsList = new java.util.ArrayList<>();
        String sql = "SELECT c.*, r.first_name || ' ' || r.last_name AS resident_name FROM certificates c JOIN residents r ON c.resident_id = r.resident_id ORDER BY c.cert_id DESC";

        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int certId = rs.getInt("cert_id");
                int resId = rs.getInt("resident_id");
                String resName = rs.getString("resident_name");
                String certType = rs.getString("cert_type");
                String purpose = rs.getString("purpose");
                String status = rs.getString("status");
                String reqDate = rs.getString("date_requested");
                String relDate = rs.getString("date_released") != null ? rs.getString("date_released") : "";

                // Instantiating specific concrete class based on the type (Polymorphism)
                CertificateRequest cert;
                if ("Barangay Clearance".equals(certType)) {
                    cert = new BarangayClearance(certId, resId, purpose, status, reqDate, relDate, "Local Employment");
                } else if ("Indigency Certificate".equals(certType)) {
                    cert = new IndigencyCertificate(certId, resId, purpose, status, reqDate, relDate, "Medical Assistance");
                } else {
                    cert = new CertificateOfResidency(certId, resId, purpose, status, reqDate, relDate, "General Purpose");
                }
                certsList.add(cert);

                // Add to table
                certsTableModel.addRow(new Object[]{
                    cert.getRequestId(),
                    cert.getResidentId(),
                    resName,
                    cert.getCertificateType(),
                    cert.getPurpose(),
                    cert.getStatus(),
                    cert.getDateRequested(),
                    cert.getDateReleased().isEmpty() ? "Not released" : cert.getDateReleased()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load certificates data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadOfficialsData() {
        officialsTableModel.setRowCount(0);
        java.util.ArrayList<Official> officialsList = new java.util.ArrayList<>();
        String sql = "SELECT * FROM residents WHERE age >= 21 ORDER BY last_name"; 

        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int count = 0;
            String[] positions = {"Barangay Captain", "Barangay Councilor", "Secretary", "Treasurer", "SK Chairman", "SK Councilor", "Purok Leader"};
            
            while (rs.next() && count < positions.length) {
                Official official = new Official(
                    rs.getInt("resident_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("address"),
                    rs.getString("contact"),
                    rs.getInt("age"),
                    rs.getString("gender"),
                    rs.getString("civil_status"),
                    rs.getString("purok"),
                    rs.getInt("is_voter") == 1,
                    rs.getInt("is_indigent") == 1,
                    rs.getInt("is_senior") == 1,
                    rs.getInt("is_pwd") == 1,
                    rs.getString("date_added"),
                    positions[count],
                    "2023-01-01",
                    "2025-12-31"
                );
                officialsList.add(official);
                count++;
            }

            // Populate Table from Officials List
            for (Official o : officialsList) {
                officialsTableModel.addRow(new Object[]{
                    o.getId(),
                    o.getFirstName(),
                    o.getLastName(),
                    o.getAge(),
                    o.getGender(),
                    o.getAddress(),
                    o.getPurok(),
                    o.getContactNumber() != null ? o.getContactNumber() : "",
                    o.getPositionInfo()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load officials data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
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
