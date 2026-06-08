import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * ReportPanel.java
 *
 * Graphical panel for displaying analytical and statistical reports from the database.
 * Computes population-per-Purok, certificate trends, and age demographics dynamically.
 *
 * OOP Concepts Demonstrated:
 *   - Encapsulation      : UI structuring and database grouping queries are private
 *   - Collections        : DefaultTableModel wraps calculated statistics
 *   - Exception Handling : SQLException handled cleanly
 *   - Constructor        : parameterized constructor linking to MainFrame
 */
public class ReportPanel extends JPanel {

    private MainFrame frame;
    private DefaultTableModel purokTableModel;
    private DefaultTableModel certsTrendTableModel;
    private DefaultTableModel ageDemographicsTableModel;
    private JLabel insightsLabel;

    public ReportPanel(MainFrame frame) {
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
        sidebar.add(makeSidebarButton("Delete", MainFrame.PANEL_DELETE, false));
        sidebar.add(makeSidebarButton("Reports", null, true));

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

        JLabel pageTitle = new JLabel("System Reports & Analytics");
        pageTitle.setFont(new Font("Arial", Font.BOLD, 22));
        pageTitle.setForeground(MainFrame.COLOR_TEXT_PRIMARY);

        JLabel pageSubtitle = new JLabel("Real-time aggregated demographic profiling, zone distributions, and certificate trends.");
        pageSubtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        pageSubtitle.setForeground(MainFrame.COLOR_TEXT_SECONDARY);

        headerPanel.add(pageTitle);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(pageSubtitle);

        content.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane Setup
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));

        // Tab 1: Purok Population
        tabbedPane.addTab("Purok Statistics", buildPurokReportTab());
        // Tab 2: Certificate Request Trend
        tabbedPane.addTab("Certificate Trends", buildCertificateTrendTab());
        // Tab 3: Age & Gender Demographics
        tabbedPane.addTab("Demographics Summary", buildDemographicsTab());

        tabbedPane.addChangeListener(e -> refreshReport(tabbedPane.getSelectedIndex()));

        content.add(tabbedPane, BorderLayout.CENTER);

        // Load first tab report automatically on open
        Timer timer = new Timer(500, e -> refreshReport(0));
        timer.setRepeats(false);
        timer.start();

        return content;
    }

    private JPanel buildPurokReportTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MainFrame.COLOR_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        String[] cols = {"Purok / Zone", "Total Population", "Registered Voters", "Indigent Residents", "Senior Citizens", "PWD Count"};
        purokTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(purokTableModel);
        DashboardPanel.styleTable(table);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1));
        panel.add(sp, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setOpaque(false);
        JButton refreshBtn = new JButton("Recalculate Purok Stats");
        refreshBtn.addActionListener(e -> loadPurokStatsReport());
        footerPanel.add(refreshBtn);
        panel.add(footerPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildCertificateTrendTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MainFrame.COLOR_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        String[] cols = {"Certificate Type", "Pending Requests", "Approved Requests", "Released Requests", "Total Requested"};
        certsTrendTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(certsTrendTableModel);
        DashboardPanel.styleTable(table);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1));
        panel.add(sp, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setOpaque(false);
        JButton refreshBtn = new JButton("Refresh Certificate Trends");
        refreshBtn.addActionListener(e -> loadCertificateTrendReport());
        footerPanel.add(refreshBtn);
        panel.add(footerPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildDemographicsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(MainFrame.COLOR_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        String[] cols = {"Demographic Group", "Age Segment", "Total Residents", "Percentage Share", "Male", "Female"};
        ageDemographicsTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(ageDemographicsTableModel);
        DashboardPanel.styleTable(table);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1));
        panel.add(sp, BorderLayout.CENTER);

        // Visual Insights card
        JPanel insightsCard = new JPanel(new BorderLayout());
        insightsCard.setBackground(MainFrame.COLOR_BACKGROUND);
        insightsCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JLabel cardTitle = new JLabel("Key Analytical Insights");
        cardTitle.setFont(new Font("Arial", Font.BOLD, 13));
        cardTitle.setForeground(MainFrame.COLOR_PRIMARY);

        insightsLabel = new JLabel("<html>Loading insight highlights from records...</html>");
        insightsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        insightsLabel.setForeground(MainFrame.COLOR_TEXT_PRIMARY);

        insightsCard.add(cardTitle, BorderLayout.NORTH);
        insightsCard.add(insightsLabel, BorderLayout.CENTER);

        panel.add(insightsCard, BorderLayout.NORTH);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setOpaque(false);
        JButton refreshBtn = new JButton("Regenerate Demographics");
        refreshBtn.addActionListener(e -> loadDemographicsReport());
        footerPanel.add(refreshBtn);
        panel.add(footerPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ── DATA COMPILER METHODS ─────────────────────────────────────────────
    private void refreshReport(int index) {
        if (index == 0) loadPurokStatsReport();
        else if (index == 1) loadCertificateTrendReport();
        else if (index == 2) loadDemographicsReport();
    }

    private void loadPurokStatsReport() {
        purokTableModel.setRowCount(0);
        String sql = "SELECT purok, " +
                     "COUNT(*) AS total_pop, " +
                     "SUM(CASE WHEN is_voter = 1 THEN 1 ELSE 0 END) AS voters, " +
                     "SUM(CASE WHEN is_indigent = 1 THEN 1 ELSE 0 END) AS indigents, " +
                     "SUM(CASE WHEN is_senior = 1 THEN 1 ELSE 0 END) AS seniors, " +
                     "SUM(CASE WHEN is_pwd = 1 THEN 1 ELSE 0 END) AS pwds " +
                     "FROM residents GROUP BY purok ORDER BY total_pop DESC";

        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int grandPop = 0, grandVoters = 0, grandIndigents = 0, grandSeniors = 0, grandPwds = 0;

            while (rs.next()) {
                String purok = rs.getString("purok");
                int pop = rs.getInt("total_pop");
                int voters = rs.getInt("voters");
                int indigents = rs.getInt("indigents");
                int seniors = rs.getInt("seniors");
                int pwds = rs.getInt("pwds");

                purokTableModel.addRow(new Object[]{purok, pop, voters, indigents, seniors, pwds});

                grandPop += pop;
                grandVoters += voters;
                grandIndigents += indigents;
                grandSeniors += seniors;
                grandPwds += pwds;
            }

            // Total row
            if (grandPop > 0) {
                purokTableModel.addRow(new Object[]{"GRAND TOTAL", grandPop, grandVoters, grandIndigents, grandSeniors, grandPwds});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to compile Purok reports: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCertificateTrendReport() {
        certsTrendTableModel.setRowCount(0);
        String sql = "SELECT cert_type, " +
                     "SUM(CASE WHEN status = 'Pending' THEN 1 ELSE 0 END) AS pending, " +
                     "SUM(CASE WHEN status = 'Approved' THEN 1 ELSE 0 END) AS approved, " +
                     "SUM(CASE WHEN status = 'Released' THEN 1 ELSE 0 END) AS released, " +
                     "COUNT(*) AS total_certs " +
                     "FROM certificates GROUP BY cert_type";

        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int grandPending = 0, grandApproved = 0, grandReleased = 0, grandTotal = 0;

            while (rs.next()) {
                String type = rs.getString("cert_type");
                int pending = rs.getInt("pending");
                int approved = rs.getInt("approved");
                int released = rs.getInt("released");
                int total = rs.getInt("total_certs");

                certsTrendTableModel.addRow(new Object[]{type, pending, approved, released, total});

                grandPending += pending;
                grandApproved += approved;
                grandReleased += released;
                grandTotal += total;
            }

            if (grandTotal > 0) {
                certsTrendTableModel.addRow(new Object[]{"GRAND TOTAL", grandPending, grandApproved, grandReleased, grandTotal});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to compile Certificate Trends: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDemographicsReport() {
        ageDemographicsTableModel.setRowCount(0);

        String popSql = "SELECT COUNT(*) AS total FROM residents";
        String demoSql = "SELECT " +
                         "CASE WHEN age < 18 THEN 'Minor' " +
                         "     WHEN age BETWEEN 18 AND 59 THEN 'Adult' " +
                         "     ELSE 'Senior Citizen' END AS age_group, " +
                         "CASE WHEN age < 18 THEN 'Under 18' " +
                         "     WHEN age BETWEEN 18 AND 59 THEN '18 - 59' " +
                         "     ELSE '60 and above' END AS age_range, " +
                         "COUNT(*) AS count, " +
                         "SUM(CASE WHEN gender = 'Male' THEN 1 ELSE 0 END) AS males, " +
                         "SUM(CASE WHEN gender = 'Female' THEN 1 ELSE 0 END) AS females " +
                         "FROM residents GROUP BY age_group ORDER BY age";

        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt = conn.createStatement()) {

            int totalPop = 0;
            try (ResultSet rs1 = stmt.executeQuery(popSql)) {
                if (rs1.next()) {
                    totalPop = rs1.getInt("total");
                }
            }

            if (totalPop == 0) {
                insightsLabel.setText("No resident records in database. Populate profiles to load demographics.");
                return;
            }

            try (ResultSet rs2 = stmt.executeQuery(demoSql)) {
                int minorCount = 0, adultCount = 0, seniorCount = 0;
                
                while (rs2.next()) {
                    String group = rs2.getString("age_group");
                    String range = rs2.getString("age_range");
                    int count = rs2.getInt("count");
                    int males = rs2.getInt("males");
                    int females = rs2.getInt("females");

                    double pct = (double) count / totalPop * 100;
                    String pctStr = String.format("%.2f%%", pct);

                    ageDemographicsTableModel.addRow(new Object[]{group, range, count, pctStr, males, females});

                    if ("Minor".equals(group)) minorCount = count;
                    else if ("Adult".equals(group)) adultCount = count;
                    else if ("Senior Citizen".equals(group)) seniorCount = count;
                }

                // Compile analytical insights in HTML display
                StringBuilder insights = new StringBuilder("<html>");
                insights.append("• Total Barangay Registered Population: <b>").append(totalPop).append("</b> residents.<br/>");
                insights.append(String.format("• <b>Demographic Segments:</b> Minors: <b>%.1f%%</b> | Adults: <b>%.1f%%</b> | Seniors: <b>%.1f%%</b>.<br/>", 
                    ((double)minorCount/totalPop*100), ((double)adultCount/totalPop*100), ((double)seniorCount/totalPop*100)));
                
                // Fetch dynamic tags summary for insights
                String tagSql = "SELECT SUM(is_voter) as voters, SUM(is_pwd) as pwds, SUM(is_indigent) as indigents FROM residents";
                try (ResultSet tagRs = stmt.executeQuery(tagSql)) {
                    if (tagRs.next()) {
                        insights.append("• <b>Special Groups:</b> Voters: <b>").append(tagRs.getInt("voters")).append("</b> | ");
                        insights.append("Indigents: <b>").append(tagRs.getInt("indigents")).append("</b> | ");
                        insights.append("PWDs: <b>").append(tagRs.getInt("pwds")).append("</b>.");
                    }
                }
                insights.append("</html>");
                insightsLabel.setText(insights.toString());
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to compile demographics report: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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
