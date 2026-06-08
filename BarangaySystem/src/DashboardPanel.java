import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * DashboardPanel.java
 *
 * The main home screen after login.
 * Shows live statistics, sidebar navigation,
 * and a recent certificates table.
 *
 * OOP Concepts Demonstrated:
 *   - Encapsulation      : stat fetching logic is private
 *   - Collections        : DefaultTableModel holds recent records
 *   - Exception Handling : SQLException caught in all DB queries
 *   - Constructor        : parameterized constructor
 */
public class DashboardPanel extends JPanel {

    // ── FIELDS ───────────────────────────────────────────────────────────
    private MainFrame        frame;
    private JLabel           totalResidentsVal;
    private JLabel           pendingCertsVal;
    private JLabel           indigentVal;
    private JLabel           seniorVal;
    private JLabel           pwdVal;
    private JLabel           voterVal;
    private JLabel           userLabel;
    private DefaultTableModel recentTableModel;

    // ── CONSTRUCTOR ──────────────────────────────────────────────────────
    public DashboardPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(MainFrame.COLOR_BACKGROUND);
        buildUI();
    }

    // ── UI BUILDER ───────────────────────────────────────────────────────

    private void buildUI() {
        add(buildTopBar(),   BorderLayout.NORTH);
        add(buildSidebar(),  BorderLayout.WEST);
        add(buildContent(),  BorderLayout.CENTER);
    }

    // ── TOP BAR ──────────────────────────────────────────────────────────

    /**
     * Builds the top navigation bar.
     * Shows system title on left, logged-in user on right.
     */
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

        userLabel = new JLabel("Administrator");
        userLabel.setFont(new Font("Arial", Font.BOLD, 13));
        userLabel.setForeground(MainFrame.COLOR_SECONDARY);

        rightSection.add(userLabel);

        topBar.add(titleLabel,    BorderLayout.WEST);
        topBar.add(rightSection,  BorderLayout.EAST);

        return topBar;
    }

    // ── SIDEBAR ───────────────────────────────────────────────────────────

    /**
     * Builds the left sidebar navigation panel.
     * Each button switches to its corresponding panel
     * via MainFrame.showPanel().
     */
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(MainFrame.COLOR_PRIMARY);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(24, 0, 24, 0));

        // System label at top of sidebar
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

        // Section label
        sidebar.add(makeSectionLabel("MENU"));

        // Navigation buttons
        sidebar.add(makeSidebarButton("Dashboard",    null,                      true));
        sidebar.add(makeSidebarButton("Add Resident", MainFrame.PANEL_ADD_RES,   false));
        sidebar.add(makeSidebarButton("Add Certificate", MainFrame.PANEL_ADD_CERT, false));
        sidebar.add(makeSidebarButton("View Records", MainFrame.PANEL_VIEW,      false));
        sidebar.add(makeSidebarButton("Search",       MainFrame.PANEL_SEARCH,    false));
        sidebar.add(makeSidebarButton("Update",       MainFrame.PANEL_UPDATE,    false));
        sidebar.add(makeSidebarButton("Delete",       MainFrame.PANEL_DELETE,    false));
        sidebar.add(makeSidebarButton("Reports",      MainFrame.PANEL_REPORT,    false));

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(makeSectionLabel("SYSTEM"));

        // Logout at bottom
        JButton logoutSide = makeSidebarButton("Logout", null, false);
        logoutSide.setForeground(new Color(0xFF, 0x99, 0x99));
        logoutSide.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                frame.logout();
            }
        });
        sidebar.add(logoutSide);

        return sidebar;
    }

    // ── MAIN CONTENT ──────────────────────────────────────────────────────

    /**
     * Builds the main content area.
     * Contains stat cards at top and recent records table below.
     */
    private JPanel buildContent() {
        JPanel content = new JPanel();
        content.setBackground(MainFrame.COLOR_BACKGROUND);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        // Page title
        JLabel pageTitle = new JLabel("Dashboard");
        pageTitle.setFont(new Font("Arial", Font.BOLD, 22));
        pageTitle.setForeground(MainFrame.COLOR_TEXT_PRIMARY);
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pageSubtitle = new JLabel(
            "Welcome to the Barangay Record Management System");
        pageSubtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        pageSubtitle.setForeground(MainFrame.COLOR_TEXT_SECONDARY);
        pageSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(pageTitle);
        content.add(Box.createVerticalStrut(4));
        content.add(pageSubtitle);
        content.add(Box.createVerticalStrut(24));

        // Stat cards row
        content.add(buildStatCardsRow());
        content.add(Box.createVerticalStrut(28));

        // Recent certificates table
        content.add(buildRecentTable());

        return content;
    }

    // ── STAT CARDS ────────────────────────────────────────────────────────

    /**
     * Builds the row of 6 live statistic cards.
     * Each card queries the database for its count.
     */
    private JPanel buildStatCardsRow() {
        JPanel row = new JPanel(new GridLayout(1, 6, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Create stat cards — values are set in refreshStats()
        totalResidentsVal = new JLabel("--");
        pendingCertsVal   = new JLabel("--");
        indigentVal       = new JLabel("--");
        seniorVal         = new JLabel("--");
        pwdVal            = new JLabel("--");
        voterVal          = new JLabel("--");

        row.add(buildStatCard("Total Residents",
            totalResidentsVal, MainFrame.COLOR_PRIMARY));
        row.add(buildStatCard("Pending Certs",
            pendingCertsVal,   MainFrame.COLOR_SECONDARY));
        row.add(buildStatCard("Indigent",
            indigentVal,       new Color(0xE6, 0x75, 0x22)));
        row.add(buildStatCard("Senior Citizens",
            seniorVal,         new Color(0x27, 0x6E, 0x48)));
        row.add(buildStatCard("PWD",
            pwdVal,            new Color(0x6C, 0x35, 0x9E)));
        row.add(buildStatCard("Registered Voters",
            voterVal,          new Color(0x1A, 0x7A, 0x9A)));

        return row;
    }

    /**
     * Builds a single stat card with a colored top border,
     * a label, and a large value display.
     */
    private JPanel buildStatCard(String title,
                                  JLabel valueLabel,
                                  Color  accentColor) {
        JPanel card = new JPanel();
        card.setBackground(MainFrame.COLOR_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(3, 0, 0, 0, accentColor),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
            )
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        titleLbl.setForeground(MainFrame.COLOR_TEXT_SECONDARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 30));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);

        return card;
    }

    // ── RECENT TABLE ──────────────────────────────────────────────────────

    /**
     * Builds the recent certificate requests table.
     * Uses DefaultTableModel — a Collection-backed data model.
     * Shows the 10 most recent requests with resident names.
     */
    private JPanel buildRecentTable() {
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(MainFrame.COLOR_CARD);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        tableCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        tableCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        // Table card header
        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(MainFrame.COLOR_CARD);
        tableHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, MainFrame.COLOR_BORDER),
            BorderFactory.createEmptyBorder(14, 20, 14, 20)
        ));

        JLabel tableTitle = new JLabel("Recent Certificate Requests");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 14));
        tableTitle.setForeground(MainFrame.COLOR_TEXT_PRIMARY);

        JButton refreshBtn = makeTextButton("Refresh");
        refreshBtn.addActionListener(e -> refreshStats());

        tableHeader.add(tableTitle,  BorderLayout.WEST);
        tableHeader.add(refreshBtn,  BorderLayout.EAST);

        // Table model and JTable
        String[] columns = {
            "Cert ID", "Resident Name", "Certificate Type",
            "Purpose", "Status", "Date Requested"
        };
        recentTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false; // read-only table
            }
        };

        JTable recentTable = new JTable(recentTableModel);
        styleTable(recentTable);

        JScrollPane scrollPane = new JScrollPane(recentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(MainFrame.COLOR_CARD);

        tableCard.add(tableHeader, BorderLayout.NORTH);
        tableCard.add(scrollPane,  BorderLayout.CENTER);

        return tableCard;
    }

    // ── DATA REFRESH ──────────────────────────────────────────────────────

    /**
     * Fetches live counts from the database and updates
     * all stat card labels and the recent table.
     *
     * Called automatically every time the Dashboard is shown.
     * Also called manually by the Refresh button.
     *
     * Exception Handling: SQLException caught here.
     * Collections: DefaultTableModel used as data collection.
     */
    public void refreshStats() {
        // Update logged-in user label
        userLabel.setText(frame.getLoggedInUser());

        try (Connection conn = DatabaseHandler.getConnection()) {

            // Total residents
            totalResidentsVal.setText(
                String.valueOf(queryCount(conn,
                    "SELECT COUNT(*) FROM residents")));

            // Pending certificates
            pendingCertsVal.setText(
                String.valueOf(queryCount(conn,
                    "SELECT COUNT(*) FROM certificates " +
                    "WHERE status = 'Pending'")));

            // Indigent residents
            indigentVal.setText(
                String.valueOf(queryCount(conn,
                    "SELECT COUNT(*) FROM residents " +
                    "WHERE is_indigent = 1")));

            // Senior residents
            seniorVal.setText(
                String.valueOf(queryCount(conn,
                    "SELECT COUNT(*) FROM residents " +
                    "WHERE is_senior = 1")));

            // PWD residents
            pwdVal.setText(
                String.valueOf(queryCount(conn,
                    "SELECT COUNT(*) FROM residents " +
                    "WHERE is_pwd = 1")));

            // Registered voters
            voterVal.setText(
                String.valueOf(queryCount(conn,
                    "SELECT COUNT(*) FROM residents " +
                    "WHERE is_voter = 1")));

            // Recent certificates table
            loadRecentCertificates(conn);

        } catch (SQLException e) {
            System.out.println("[DASHBOARD ERROR] " + e.getMessage());
        }
    }

    /**
     * Executes a COUNT query and returns the integer result.
     * Reused for all 6 stat card queries.
     */
    private int queryCount(Connection conn, String sql)
            throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Loads the 10 most recent certificate requests
     * into the DefaultTableModel (Collection).
     * Joins certificates with residents for the name column.
     */
    private void loadRecentCertificates(Connection conn)
            throws SQLException {
        recentTableModel.setRowCount(0); // clear existing rows

        String sql =
            "SELECT c.cert_id, " +
            "       r.first_name || ' ' || r.last_name AS resident_name, " +
            "       c.cert_type, c.purpose, c.status, c.date_requested " +
            "FROM certificates c " +
            "JOIN residents r ON c.resident_id = r.resident_id " +
            "ORDER BY c.date_requested DESC " +
            "LIMIT 10";

        try (Statement stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            while (rs.next()) {
                recentTableModel.addRow(new Object[]{
                    rs.getInt   ("cert_id"),
                    rs.getString("resident_name"),
                    rs.getString("cert_type"),
                    rs.getString("purpose"),
                    rs.getString("status"),
                    rs.getString("date_requested")
                });
            }
        }
    }

    // ── SHARED STYLE HELPERS ──────────────────────────────────────────────

    /**
     * Applies consistent styling to any JTable in this system.
     * Row height, font, header color, grid color, selection color.
     */
    public static void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(36);
        table.setGridColor(MainFrame.COLOR_BORDER);
        table.setSelectionBackground(new Color(0xD6, 0xE4, 0xF7));
        table.setSelectionForeground(MainFrame.COLOR_TEXT_PRIMARY);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFocusable(false);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setBackground(MainFrame.COLOR_PRIMARY);
        header.setForeground(MainFrame.COLOR_TEXT_LIGHT);
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createEmptyBorder());

        // Alternate row coloring using custom renderer
        table.setDefaultRenderer(Object.class,
            new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(
                        JTable t, Object val, boolean sel,
                        boolean foc, int row, int col) {

                    super.getTableCellRendererComponent(
                        t, val, sel, foc, row, col);
                    setBorder(
                        BorderFactory.createEmptyBorder(0, 12, 0, 12));

                    if (!sel) {
                        setBackground(row % 2 == 0
                            ? MainFrame.COLOR_CARD
                            : MainFrame.COLOR_TABLE_ALT);
                        setForeground(MainFrame.COLOR_TEXT_PRIMARY);
                    }

                    // Color-code the Status column
                    if (t.getColumnName(col).equals("Status")) {
                        String status = val != null
                            ? val.toString() : "";
                        if (!sel) {
                            switch (status) {
                                case "Pending":
                                    setForeground(MainFrame.COLOR_WARNING);
                                    break;
                                case "Approved":
                                    setForeground(MainFrame.COLOR_SECONDARY);
                                    break;
                                case "Released":
                                    setForeground(MainFrame.COLOR_SUCCESS);
                                    break;
                            }
                        }
                    }
                    return this;
                }
            });
    }

    /**
     * Creates a flat text-style button with no border or background.
     * Used for Logout, Refresh, and other secondary actions.
     */
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

    /**
     * Creates a sidebar navigation button.
     * If panelName is null, button has no navigation action
     * (used for Dashboard which is already active,
     *  and Logout which has custom logic).
     */
    private JButton makeSidebarButton(String text,
                                       String panelName,
                                       boolean isActive) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial",
            isActive ? Font.BOLD : Font.PLAIN, 13));
        btn.setForeground(isActive
            ? MainFrame.COLOR_ACCENT
            : MainFrame.COLOR_TEXT_LIGHT);
        btn.setBackground(isActive
            ? new Color(0x24, 0x4A, 0x82)
            : MainFrame.COLOR_PRIMARY);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setPreferredSize(new Dimension(220, 44));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!isActive) {
                    btn.setBackground(new Color(0x0D, 0x2B, 0x55));
                }
            }
            public void mouseExited(MouseEvent e) {
                if (!isActive) {
                    btn.setBackground(MainFrame.COLOR_PRIMARY);
                }
            }
        });

        if (panelName != null) {
            btn.addActionListener(e -> frame.showPanel(panelName));
        }

        return btn;
    }

    /**
     * Creates a small section label for sidebar grouping.
     */
    private JLabel makeSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 10));
        label.setForeground(new Color(0x70, 0x90, 0xB0));
        label.setBorder(BorderFactory.createEmptyBorder(8, 20, 4, 20));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
}
