import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * SearchPanel.java
 *
 * Graphical panel for searching resident records.
 * Integrates search-by-name, search-by-ID, and search-by-Purok into a single UI view.
 *
 * OOP Concepts Demonstrated:
 *   - Encapsulation      : UI search actions and query construction are private
 *   - Collections        : DefaultTableModel wraps resident search results
 *   - Exception Handling : SQLException and NumberFormatException caught cleanly
 *   - Constructor        : parameterized constructor linking to MainFrame
 */
public class SearchPanel extends JPanel {

    private MainFrame frame;
    private JTextField searchField;
    private JComboBox<String> criteriaBox;
    private DefaultTableModel resultsTableModel;
    private JLabel countLabel;

    public SearchPanel(MainFrame frame) {
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
        sidebar.add(makeSidebarButton("View Records", MainFrame.PANEL_VIEW, false));
        sidebar.add(makeSidebarButton("Search", null, true));
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
        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setBackground(MainFrame.COLOR_BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel pageTitle = new JLabel("Search Records");
        pageTitle.setFont(new Font("Arial", Font.BOLD, 22));
        pageTitle.setForeground(MainFrame.COLOR_TEXT_PRIMARY);

        JLabel pageSubtitle = new JLabel("Perform precise searches across the resident master database utilizing ID, Name, or Purok zone keywords.");
        pageSubtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        pageSubtitle.setForeground(MainFrame.COLOR_TEXT_SECONDARY);

        headerPanel.add(pageTitle);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(pageSubtitle);
        content.add(headerPanel, BorderLayout.NORTH);

        // Center Panel (Search Controls & JTable results)
        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setOpaque(false);

        // Search Control Bar Card
        JPanel searchBarCard = new JPanel(new GridBagLayout());
        searchBarCard.setBackground(MainFrame.COLOR_CARD);
        searchBarCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 8, 0, 8);
        
        criteriaBox = new JComboBox<>(new String[]{"Search by Name", "Search by ID", "Search by Purok"});
        criteriaBox.setFont(new Font("Arial", Font.PLAIN, 13));
        criteriaBox.setPreferredSize(new Dimension(160, 38));

        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(0, 38));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Arial", Font.BOLD, 13));
        searchBtn.setBackground(MainFrame.COLOR_PRIMARY);
        searchBtn.setForeground(MainFrame.COLOR_TEXT_LIGHT);
        searchBtn.setPreferredSize(new Dimension(120, 38));
        searchBtn.setOpaque(true);
        searchBtn.setBorderPainted(false);
        searchBtn.addActionListener(e -> performSearch());

        // Dynamic search trigger on enter
        searchField.addActionListener(e -> performSearch());

        gbc.gridx = 0; gbc.weightx = 0.2;
        searchBarCard.add(criteriaBox, gbc);

        gbc.gridx = 1; gbc.weightx = 0.6;
        searchBarCard.add(searchField, gbc);

        gbc.gridx = 2; gbc.weightx = 0.2;
        searchBarCard.add(searchBtn, gbc);

        centerPanel.add(searchBarCard, BorderLayout.NORTH);

        // Results Card (Table)
        JPanel resultsCard = new JPanel(new BorderLayout());
        resultsCard.setBackground(MainFrame.COLOR_CARD);
        resultsCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        String[] cols = {"ID", "First Name", "Last Name", "Age", "Gender", "Civil Status", "Address", "Purok", "Contact", "Voter?", "Indigent?", "Senior?", "PWD?"};
        resultsTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(resultsTableModel);
        DashboardPanel.styleTable(table);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1));
        resultsCard.add(sp, BorderLayout.CENTER);

        // Count / footer info
        countLabel = new JLabel("Enter a search term above and press search.");
        countLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        countLabel.setForeground(MainFrame.COLOR_TEXT_SECONDARY);
        resultsCard.add(countLabel, BorderLayout.SOUTH);

        centerPanel.add(resultsCard, BorderLayout.CENTER);
        content.add(centerPanel, BorderLayout.CENTER);

        return content;
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        String criteria = (String) criteriaBox.getSelectedItem();

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        resultsTableModel.setRowCount(0);
        String sql = "";

        if ("Search by Name".equals(criteria)) {
            sql = "SELECT * FROM residents WHERE first_name LIKE ? OR last_name LIKE ? ORDER BY last_name";
        } else if ("Search by ID".equals(criteria)) {
            sql = "SELECT * FROM residents WHERE resident_id = ?";
        } else if ("Search by Purok".equals(criteria)) {
            sql = "SELECT * FROM residents WHERE purok LIKE ? ORDER BY last_name";
        }

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if ("Search by Name".equals(criteria)) {
                pstmt.setString(1, "%" + keyword + "%");
                pstmt.setString(2, "%" + keyword + "%");
            } else if ("Search by ID".equals(criteria)) {
                int id;
                try {
                    id = Integer.parseInt(keyword);
                    pstmt.setInt(1, id);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Resident ID must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else if ("Search by Purok".equals(criteria)) {
                pstmt.setString(1, "%" + keyword + "%");
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                java.util.ArrayList<Resident> foundResidents = new java.util.ArrayList<>();
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
                    foundResidents.add(resident);
                }

                // Output to console via toString() and load to table
                System.out.println("\n--- Search Results Found (toString Display) ---");
                for (Resident r : foundResidents) {
                    System.out.println(r.toString());

                    resultsTableModel.addRow(new Object[]{
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

                int rows = foundResidents.size();
                if (rows > 0) {
                    countLabel.setText(rows + " resident(s) found matching \"" + keyword + "\".");
                } else {
                    countLabel.setText("No residents found matching your search term.");
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to perform database search: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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
