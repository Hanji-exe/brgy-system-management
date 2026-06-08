import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 * SearchModule.java
 *
 * Handles:
 *   - Search resident by name
 *   - Search resident by ID
 *   - Search resident by Purok
 *
 * OOP Concepts Demonstrated:
 *   - Collections        : ArrayList<Resident>
 *   - Exception Handling : try-catch on all DB reads
 *   - Encapsulation      : search logic isolated here
 */
public class SearchModule {

    private Scanner scanner;

    public SearchModule() {
        this.scanner = new Scanner(System.in);
    }

    public SearchModule(Scanner scanner) {
        this.scanner = scanner;
    }

    // ── SEARCH MENU ──────────────────────────────────────────────────────────

    public void showSearchMenu() {
        System.out.println("\n╔══════════════════════════╗");
        System.out.println("║      SEARCH RECORDS       ║");
        System.out.println("╠══════════════════════════╣");
        System.out.println("║  [1] Search by Name       ║");
        System.out.println("║  [2] Search by ID         ║");
        System.out.println("║  [3] Search by Purok      ║");
        System.out.println("║  [0] Back to Main Menu    ║");
        System.out.println("╚══════════════════════════╝");
        System.out.print("   Enter choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1: searchByName();  break;
                case 2: searchById();    break;
                case 3: searchByPurok(); break;
                case 0: break;
                default: System.out.println("[!] Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("[!] Please enter a number.");
        }
    }

    // ── SEARCH BY NAME ───────────────────────────────────────────────────────

    public void searchByName() {
        String keyword = JOptionPane.showInputDialog(null,
            "Enter name to search (first or last name):",
            "Search Resident", JOptionPane.PLAIN_MESSAGE);

        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("[!] Search keyword is required."); return;
        }

        // LIKE with wildcards — case-insensitive partial match
        String sql =
            "SELECT * FROM residents " +
            "WHERE first_name LIKE ? OR last_name LIKE ? " +
            "ORDER BY last_name";

        String pattern = "%" + keyword.trim() + "%";
        displaySearchResults(sql, pattern, pattern);
    }

    // ── SEARCH BY ID ─────────────────────────────────────────────────────────

    public void searchById() {
        String idStr = JOptionPane.showInputDialog(null,
            "Enter Resident ID:", "Search Resident",
            JOptionPane.PLAIN_MESSAGE);

        if (idStr == null || idStr.trim().isEmpty()) {
            System.out.println("[!] ID is required."); return;
        }

        try {
            int id = Integer.parseInt(idStr.trim());
            String sql = "SELECT * FROM residents WHERE resident_id = ?";

            ArrayList<Resident> results = new ArrayList<>();

            try (Connection conn = DatabaseHandler.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    results.add(buildResident(rs));
                }

            } catch (SQLException e) {
                System.out.println("[DB ERROR] " + e.getMessage()); return;
            }

            printResults(results, "ID: " + id);

        } catch (NumberFormatException e) {
            System.out.println("[!] ID must be a number.");
        }
    }

    // ── SEARCH BY PUROK ──────────────────────────────────────────────────────

    public void searchByPurok() {
        String purok = JOptionPane.showInputDialog(null,
            "Enter Purok / Zone name:",
            "Search by Purok", JOptionPane.PLAIN_MESSAGE);

        if (purok == null || purok.trim().isEmpty()) {
            System.out.println("[!] Purok name is required."); return;
        }

        String sql =
            "SELECT * FROM residents WHERE purok LIKE ? " +
            "ORDER BY last_name";

        String pattern = "%" + purok.trim() + "%";
        displaySearchResults(sql, pattern, null);
    }

    // ── SHARED DISPLAY HELPER ────────────────────────────────────────────────

    /**
     * Executes a prepared query with 1 or 2 string parameters
     * and displays results. Handles both single-param and
     * dual-param queries (e.g., search by first OR last name).
     */
    private void displaySearchResults(String sql,
                                       String param1,
                                       String param2) {
        ArrayList<Resident> results = new ArrayList<>();

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, param1);
            if (param2 != null) pstmt.setString(2, param2);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(buildResident(rs));
            }

        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage()); return;
        }

        printResults(results, param1.replace("%", ""));
    }

    /**
     * Builds a Resident object from the current ResultSet row.
     * Extracted as a helper to avoid repeating 14 rs.get() calls
     * across every search method.
     */
    private Resident buildResident(ResultSet rs) throws SQLException {
        return new Resident(
            rs.getInt    ("resident_id"),
            rs.getString ("first_name"),
            rs.getString ("last_name"),
            rs.getString ("address"),
            rs.getString ("contact") != null ? rs.getString("contact") : "",
            rs.getInt    ("age"),
            rs.getString ("gender"),
            rs.getString ("civil_status"),
            rs.getString ("purok"),
            rs.getInt    ("is_voter")    == 1,
            rs.getInt    ("is_indigent") == 1,
            rs.getInt    ("is_senior")   == 1,
            rs.getInt    ("is_pwd")      == 1,
            rs.getString ("date_added")
        );
    }

    /**
     * Prints the search results list with a count summary.
     */
    private void printResults(ArrayList<Resident> results, String keyword) {
        if (results.isEmpty()) {
            System.out.println("\n[!] No residents found matching: " + keyword);
            return;
        }

        System.out.println("\n[Search Results for: \"" + keyword + "\"]");
        for (Resident r : results) {
            System.out.println(r.toString());
        }
        System.out.println("[" + results.size() + " result(s) found.]");
    }
}
