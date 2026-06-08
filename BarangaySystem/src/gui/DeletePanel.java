package gui;

import model.*;
import db.DatabaseHandler;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * DeletePanel.java
 *
 * Graphical panel for removing resident profiles and certificate request records safely.
 * Prompts user confirmations and strictly handles foreign key constraint order logic.
 *
 * OOP Concepts Demonstrated:
 *   - Encapsulation      : UI construction and transactional delete statements are private
 *   - Exception Handling : SQLException caught cleanly with rollback handling
 *   - Constructor        : parameterized constructor linking to MainFrame
 */
public class DeletePanel extends JPanel {

    private MainFrame frame;

    // Resident delete fields
    private JTextField resIdField;
    private JLabel resDetailsLabel;
    private JLabel resCertCountLabel;
    private JButton resDeleteBtn;
    private int loadedResidentId = -1;

    // Certificate delete fields
    private JTextField certIdField;
    private JLabel certDetailsLabel;
    private JButton certDeleteBtn;
    private int loadedCertId = -1;

    public DeletePanel(MainFrame frame) {
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
        sidebar.add(makeSidebarButton("Update", MainFrame.PANEL_UPDATE, false));
        sidebar.add(makeSidebarButton("Delete", null, true));
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

        JLabel pageTitle = new JLabel("Delete Records");
        pageTitle.setFont(new Font("Arial", Font.BOLD, 22));
        pageTitle.setForeground(MainFrame.COLOR_TEXT_PRIMARY);

        JLabel pageSubtitle = new JLabel("Permanently remove resident profiles or certificate records from the database. Confirms safely.");
        pageSubtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        pageSubtitle.setForeground(MainFrame.COLOR_TEXT_SECONDARY);

        headerPanel.add(pageTitle);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(pageSubtitle);
        content.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Panel
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));

        tabbedPane.addTab("Delete Resident Profile", buildDeleteResidentTab());
        tabbedPane.addTab("Delete Certificate Request", buildDeleteCertificateTab());

        content.add(tabbedPane, BorderLayout.CENTER);

        return content;
    }

    // ── RESIDENT DELETE TAB ───────────────────────────────────────────────
    private JPanel buildDeleteResidentTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(MainFrame.COLOR_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Search Section
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        searchPanel.setOpaque(false);

        JLabel idLabel = new JLabel("Resident ID to Delete:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 13));
        idLabel.setForeground(MainFrame.COLOR_TEXT_PRIMARY);

        resIdField = new JTextField();
        resIdField.setPreferredSize(new Dimension(100, 38));
        resIdField.setFont(new Font("Arial", Font.PLAIN, 13));
        resIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        JButton findBtn = new JButton("Load Profile");
        findBtn.setFont(new Font("Arial", Font.BOLD, 13));
        findBtn.setBackground(MainFrame.COLOR_SECONDARY);
        findBtn.setForeground(MainFrame.COLOR_TEXT_LIGHT);
        findBtn.setPreferredSize(new Dimension(130, 38));
        findBtn.setOpaque(true);
        findBtn.setBorderPainted(false);
        findBtn.addActionListener(e -> loadResidentForDeletion());

        resIdField.addActionListener(e -> loadResidentForDeletion());

        searchPanel.add(idLabel);
        searchPanel.add(resIdField);
        searchPanel.add(findBtn);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Details Card
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(MainFrame.COLOR_BACKGROUND);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        resDetailsLabel = new JLabel("No resident record loaded.");
        resDetailsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resDetailsLabel.setForeground(MainFrame.COLOR_TEXT_PRIMARY);
        resDetailsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        resCertCountLabel = new JLabel("Certificates count: --");
        resCertCountLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        resCertCountLabel.setForeground(MainFrame.COLOR_TEXT_SECONDARY);
        resCertCountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        resDeleteBtn = new JButton("Delete Resident & All Certificates");
        resDeleteBtn.setFont(new Font("Arial", Font.BOLD, 13));
        resDeleteBtn.setBackground(MainFrame.COLOR_DANGER);
        resDeleteBtn.setForeground(MainFrame.COLOR_TEXT_LIGHT);
        resDeleteBtn.setPreferredSize(new Dimension(280, 44));
        resDeleteBtn.setMaximumSize(new Dimension(280, 44));
        resDeleteBtn.setOpaque(true);
        resDeleteBtn.setBorderPainted(false);
        resDeleteBtn.setEnabled(false);
        resDeleteBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        resDeleteBtn.addActionListener(e -> deleteResidentRecord());

        cardPanel.add(resDetailsLabel);
        cardPanel.add(Box.createVerticalStrut(12));
        cardPanel.add(resCertCountLabel);
        cardPanel.add(Box.createVerticalStrut(24));
        cardPanel.add(resDeleteBtn);

        panel.add(cardPanel, BorderLayout.CENTER);

        return panel;
    }

    // ── CERTIFICATE DELETE TAB ────────────────────────────────────────────
    private JPanel buildDeleteCertificateTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(MainFrame.COLOR_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Search Section
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        searchPanel.setOpaque(false);

        JLabel idLabel = new JLabel("Certificate Request ID:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 13));
        idLabel.setForeground(MainFrame.COLOR_TEXT_PRIMARY);

        certIdField = new JTextField();
        certIdField.setPreferredSize(new Dimension(100, 38));
        certIdField.setFont(new Font("Arial", Font.PLAIN, 13));
        certIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        JButton findBtn = new JButton("Load Request");
        findBtn.setFont(new Font("Arial", Font.BOLD, 13));
        findBtn.setBackground(MainFrame.COLOR_SECONDARY);
        findBtn.setForeground(MainFrame.COLOR_TEXT_LIGHT);
        findBtn.setPreferredSize(new Dimension(130, 38));
        findBtn.setOpaque(true);
        findBtn.setBorderPainted(false);
        findBtn.addActionListener(e -> loadCertificateForDeletion());

        certIdField.addActionListener(e -> loadCertificateForDeletion());

        searchPanel.add(idLabel);
        searchPanel.add(certIdField);
        searchPanel.add(findBtn);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Details Card
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(MainFrame.COLOR_BACKGROUND);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        certDetailsLabel = new JLabel("No certificate request loaded.");
        certDetailsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        certDetailsLabel.setForeground(MainFrame.COLOR_TEXT_PRIMARY);
        certDetailsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        certDeleteBtn = new JButton("Delete Certificate Request");
        certDeleteBtn.setFont(new Font("Arial", Font.BOLD, 13));
        certDeleteBtn.setBackground(MainFrame.COLOR_DANGER);
        certDeleteBtn.setForeground(MainFrame.COLOR_TEXT_LIGHT);
        certDeleteBtn.setPreferredSize(new Dimension(240, 44));
        certDeleteBtn.setMaximumSize(new Dimension(240, 44));
        certDeleteBtn.setOpaque(true);
        certDeleteBtn.setBorderPainted(false);
        certDeleteBtn.setEnabled(false);
        certDeleteBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        certDeleteBtn.addActionListener(e -> deleteCertificateRecord());

        cardPanel.add(certDetailsLabel);
        cardPanel.add(Box.createVerticalStrut(24));
        cardPanel.add(certDeleteBtn);

        panel.add(cardPanel, BorderLayout.CENTER);

        return panel;
    }

    // ── DATABASE OPERATIONS ───────────────────────────────────────────────
    private void loadResidentForDeletion() {
        String idStr = resIdField.getText().trim();
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

        String resSql = "SELECT * FROM residents WHERE resident_id = ?";
        String certSql = "SELECT COUNT(*) AS total FROM certificates WHERE resident_id = ?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement resPstmt = conn.prepareStatement(resSql);
             PreparedStatement certPstmt = conn.prepareStatement(certSql)) {

            resPstmt.setInt(1, id);
            certPstmt.setInt(1, id);

            try (ResultSet resRs = resPstmt.executeQuery();
                 ResultSet certRs = certPstmt.executeQuery()) {

                if (resRs.next()) {
                    String name = resRs.getString("first_name") + " " + resRs.getString("last_name");
                    resDetailsLabel.setText("Resident Name: " + name + " (Age: " + resRs.getInt("age") + ", Purok: " + resRs.getString("purok") + ")");
                    
                    int count = 0;
                    if (certRs.next()) {
                        count = certRs.getInt("total");
                    }
                    resCertCountLabel.setText("Warning: This resident is linked to " + count + " certificate request(s). All will be deleted.");
                    resCertCountLabel.setForeground(count > 0 ? MainFrame.COLOR_DANGER : MainFrame.COLOR_TEXT_SECONDARY);

                    loadedResidentId = id;
                    resDeleteBtn.setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Resident ID " + id + " not found.", "Not Found", JOptionPane.ERROR_MESSAGE);
                    clearResidentDelete();
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB read error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteResidentRecord() {
        if (loadedResidentId == -1) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to permanently delete this resident and all their certificate requests?\nThis action cannot be undone.",
            "Confirm Destructive Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        // Transactional query: delete certificates first, then resident
        String delCertsSql = "DELETE FROM certificates WHERE resident_id = ?";
        String delResSql = "DELETE FROM residents WHERE resident_id = ?";

        try (Connection conn = DatabaseHandler.getConnection()) {
            conn.setAutoCommit(false); // start transaction

            try (PreparedStatement pstmt1 = conn.prepareStatement(delCertsSql);
                 PreparedStatement pstmt2 = conn.prepareStatement(delResSql)) {

                // Delete child certificate records
                pstmt1.setInt(1, loadedResidentId);
                pstmt1.executeUpdate();

                // Delete parent resident record
                pstmt2.setInt(1, loadedResidentId);
                pstmt2.executeUpdate();

                conn.commit(); // commit transaction
                JOptionPane.showMessageDialog(this, "Resident ID " + loadedResidentId + " and associated certificates deleted successfully!", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                clearResidentDelete();

            } catch (SQLException ex) {
                conn.rollback(); // rollback on failure
                JOptionPane.showMessageDialog(this, "Deletion failed. Transaction rolled back: " + ex.getMessage(), "Transaction Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failure: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCertificateForDeletion() {
        String idStr = certIdField.getText().trim();
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
                    String name = rs.getString("res_name");
                    certDetailsLabel.setText("<html>Requester: <b>" + name + "</b><br/>Type: " + rs.getString("cert_type") + "<br/>Purpose: " + rs.getString("purpose") + "<br/>Status: <b>" + rs.getString("status") + "</b></html>");
                    loadedCertId = id;
                    certDeleteBtn.setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Certificate ID " + id + " not found.", "Not Found", JOptionPane.ERROR_MESSAGE);
                    clearCertificateDelete();
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB read error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCertificateRecord() {
        if (loadedCertId == -1) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to permanently delete this certificate request record?\nThis action cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM certificates WHERE cert_id = ?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, loadedCertId);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Certificate request record deleted successfully!", "Deleted", JOptionPane.INFORMATION_MESSAGE);
            clearCertificateDelete();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to delete certificate: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── CLEANERS ───────────────────────────────────────────────────────────
    private void clearResidentDelete() {
        resIdField.setText("");
        resDetailsLabel.setText("No resident record loaded.");
        resCertCountLabel.setText("Certificates count: --");
        resCertCountLabel.setForeground(MainFrame.COLOR_TEXT_SECONDARY);
        resDeleteBtn.setEnabled(false);
        loadedResidentId = -1;
    }

    private void clearCertificateDelete() {
        certIdField.setText("");
        certDetailsLabel.setText("No certificate request loaded.");
        certDeleteBtn.setEnabled(false);
        loadedCertId = -1;
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
