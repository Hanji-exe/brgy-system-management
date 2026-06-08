import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * ReportModule.java
 *
 * OOP Concepts Demonstrated:
 *   - Collections        : ArrayList used to store aggregated results
 *   - Exception Handling : try-catch on all DB queries
 *   - Encapsulation      : all report logic isolated in this class
 *
 * Report Types:
 *   1. Residents per Purok         — GROUP BY purok
 *   2. Certificate Requests by Type (Monthly) — GROUP BY cert_type + month
 *   3. Resident Age Group Distribution — CASE WHEN age grouping
 *
 * These are NOT raw SELECT * dumps.
 * Each query aggregates, groups, or transforms data
 * to answer a specific operational question.
 */
public class ReportModule {

    private Scanner scanner;

    // ── CONSTRUCTORS ─────────────────────────────────────────────────────────
    public ReportModule() {
        this.scanner = new Scanner(System.in);
    }

    public ReportModule(Scanner scanner) {
        this.scanner = scanner;
    }

    // ── REPORT MENU ──────────────────────────────────────────────────────────

    public void showReportMenu() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║           REPORT GENERATION           ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  [1] Residents per Purok              ║");
        System.out.println("║  [2] Certificate Requests by Type     ║");
        System.out.println("║  [3] Age Group Distribution           ║");
        System.out.println("║  [0] Back to Main Menu                ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.print("   Enter choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1: reportResidentsPerPurok();        break;
                case 2: reportCertificatesByType();       break;
                case 3: reportAgeGroupDistribution();     break;
                case 0: break;
                default: System.out.println("[!] Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("[!] Please enter a number.");
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // REPORT 1 — Residents per Purok
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Answers: "How many residents live in each purok?"
     *
     * SQL Logic:
     *   GROUP BY purok — collapses all rows per purok into one
     *   COUNT(*) — counts residents per group
     *   Additional columns count status tags per purok
     *   ORDER BY total DESC — highest population purok first
     *
     * Collections: ArrayList<String[]> stores each row
     * before printing — demonstrates Collections requirement.
     */
    public void reportResidentsPerPurok() {
        String sql =
            "SELECT " +
            "    purok, " +
            "    COUNT(*) AS total, " +
            "    SUM(is_voter)    AS voters, " +
            "    SUM(is_indigent) AS indigent, " +
            "    SUM(is_senior)   AS seniors, " +
            "    SUM(is_pwd)      AS pwd " +
            "FROM residents " +
            "GROUP BY purok " +
            "ORDER BY total DESC";

        // ArrayList stores each row as String array
        // Satisfies Collections requirement explicitly
        ArrayList<String[]> rows = new ArrayList<>();

        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt   = conn.createStatement();
             ResultSet rs     = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rows.add(new String[]{
                    rs.getString("purok"),
                    String.valueOf(rs.getInt("total")),
                    String.valueOf(rs.getInt("voters")),
                    String.valueOf(rs.getInt("indigent")),
                    String.valueOf(rs.getInt("seniors")),
                    String.valueOf(rs.getInt("pwd"))
                });
            }

        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
            return;
        }

        // ── Print Report ──────────────────────────────────────────────────
        printReportHeader("REPORT 1: RESIDENTS PER PUROK");

        if (rows.isEmpty()) {
            System.out.println("  No resident data available.");
            printReportFooter();
            return;
        }

        // Column headers
        System.out.printf("  %-20s %8s %8s %10s %9s %6s%n",
            "PUROK", "TOTAL", "VOTERS", "INDIGENT", "SENIORS", "PWD");
        System.out.println("  " + "─".repeat(66));

        int grandTotal = 0;

        for (String[] row : rows) {
            System.out.printf("  %-20s %8s %8s %10s %9s %6s%n",
                row[0], row[1], row[2], row[3], row[4], row[5]);
            grandTotal += Integer.parseInt(row[1]);
        }

        System.out.println("  " + "─".repeat(66));
        System.out.printf("  %-20s %8d%n", "TOTAL RESIDENTS:", grandTotal);
        printReportFooter();
    }

    // ════════════════════════════════════════════════════════════════════════
    // REPORT 2 — Certificate Requests by Type (Monthly)
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Answers: "Which certificate type is most requested and when?"
     *
     * SQL Logic:
     *   strftime('%Y-%m', date_requested) — extracts year-month
     *   GROUP BY cert_type, month — groups by type AND month
     *   COUNT(*) — counts requests per group
     *   ORDER BY month DESC — most recent month first
     *
     * This is a multi-dimensional aggregation —
     * not a simple count, it breaks down by both type and time.
     */
    public void reportCertificatesByType() {
        String sql =
            "SELECT " +
            "    cert_type, " +
            "    strftime('%Y-%m', date_requested) AS month, " +
            "    COUNT(*) AS total, " +
            "    SUM(CASE WHEN status = 'Pending'  THEN 1 ELSE 0 END) AS pending, " +
            "    SUM(CASE WHEN status = 'Approved' THEN 1 ELSE 0 END) AS approved, " +
            "    SUM(CASE WHEN status = 'Released' THEN 1 ELSE 0 END) AS released " +
            "FROM certificates " +
            "GROUP BY cert_type, month " +
            "ORDER BY month DESC, total DESC";

        ArrayList<String[]> rows = new ArrayList<>();

        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt   = conn.createStatement();
             ResultSet rs     = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rows.add(new String[]{
                    rs.getString("cert_type"),
                    rs.getString("month")    != null
                        ? rs.getString("month") : "N/A",
                    String.valueOf(rs.getInt("total")),
                    String.valueOf(rs.getInt("pending")),
                    String.valueOf(rs.getInt("approved")),
                    String.valueOf(rs.getInt("released"))
                });
            }

        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
            return;
        }

        // ── Print Report ──────────────────────────────────────────────────
        printReportHeader("REPORT 2: CERTIFICATE REQUESTS BY TYPE (MONTHLY)");

        if (rows.isEmpty()) {
            System.out.println("  No certificate data available.");
            printReportFooter();
            return;
        }

        System.out.printf("  %-28s %-9s %7s %9s %10s %10s%n",
            "CERTIFICATE TYPE", "MONTH", "TOTAL",
            "PENDING", "APPROVED", "RELEASED");
        System.out.println("  " + "─".repeat(78));

        String lastMonth = "";
        int    grandTotal = 0;

        for (String[] row : rows) {
            // Print month divider when month changes
            if (!row[1].equals(lastMonth)) {
                if (!lastMonth.isEmpty()) {
                    System.out.println("  " + "·".repeat(78));
                }
                lastMonth = row[1];
            }

            System.out.printf("  %-28s %-9s %7s %9s %10s %10s%n",
                row[0], row[1], row[2], row[3], row[4], row[5]);
            grandTotal += Integer.parseInt(row[2]);
        }

        System.out.println("  " + "─".repeat(78));
        System.out.printf("  %-28s %-9s %7d%n",
            "TOTAL REQUESTS:", "", grandTotal);
        printReportFooter();

        // Summary per type across all months
        printCertTypeSummary();
    }

    /**
     * Secondary summary for Report 2.
     * Shows total per certificate type across ALL months combined.
     * Uses a simpler GROUP BY cert_type only.
     */
    private void printCertTypeSummary() {
        String sql =
            "SELECT cert_type, COUNT(*) AS total " +
            "FROM certificates " +
            "GROUP BY cert_type " +
            "ORDER BY total DESC";

        System.out.println("\n  ── ALL-TIME TOTALS BY TYPE ──");
        System.out.printf("  %-30s %s%n", "TYPE", "TOTAL");
        System.out.println("  " + "─".repeat(40));

        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt   = conn.createStatement();
             ResultSet rs     = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf("  %-30s %d%n",
                    rs.getString("cert_type"),
                    rs.getInt("total"));
            }

        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
        }
        System.out.println("  " + "─".repeat(40));
    }

    // ════════════════════════════════════════════════════════════════════════
    // REPORT 3 — Age Group Distribution
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Answers: "What is the age breakdown of barangay residents?"
     *
     * SQL Logic:
     *   CASE WHEN — classifies each resident into an age group
     *   GROUP BY age_group — counts per group
     *   The CASE expression runs per row before grouping
     *
     * Age Groups:
     *   Minor  → age < 18
     *   Adult  → age 18–59
     *   Senior → age >= 60
     *
     * Also breaks down by gender per age group —
     * makes the report more operationally useful.
     */
    public void reportAgeGroupDistribution() {
        String sql =
            "SELECT " +
            "    CASE " +
            "        WHEN age < 18        THEN 'Minor (0-17)' " +
            "        WHEN age BETWEEN 18 AND 59 THEN 'Adult (18-59)' " +
            "        ELSE                      'Senior (60+)' " +
            "    END AS age_group, " +
            "    COUNT(*) AS total, " +
            "    SUM(CASE WHEN gender = 'Male'   THEN 1 ELSE 0 END) AS male, " +
            "    SUM(CASE WHEN gender = 'Female' THEN 1 ELSE 0 END) AS female, " +
            "    SUM(is_indigent) AS indigent, " +
            "    SUM(is_pwd)      AS pwd " +
            "FROM residents " +
            "GROUP BY age_group " +
            "ORDER BY " +
            "    CASE age_group " +
            "        WHEN 'Minor (0-17)'   THEN 1 " +
            "        WHEN 'Adult (18-59)'  THEN 2 " +
            "        ELSE 3 " +
            "    END";

        ArrayList<String[]> rows = new ArrayList<>();
        int grandTotal = 0;

        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt   = conn.createStatement();
             ResultSet rs     = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rows.add(new String[]{
                    rs.getString("age_group"),
                    String.valueOf(rs.getInt("total")),
                    String.valueOf(rs.getInt("male")),
                    String.valueOf(rs.getInt("female")),
                    String.valueOf(rs.getInt("indigent")),
                    String.valueOf(rs.getInt("pwd"))
                });
                grandTotal += rs.getInt("total");
            }

        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
            return;
        }

        // ── Print Report ──────────────────────────────────────────────────
        printReportHeader("REPORT 3: RESIDENT AGE GROUP DISTRIBUTION");

        if (rows.isEmpty()) {
            System.out.println("  No resident data available.");
            printReportFooter();
            return;
        }

        System.out.printf("  %-18s %8s %8s %9s %10s %6s %8s%n",
            "AGE GROUP", "TOTAL", "%", "MALE",
            "FEMALE", "INDIG.", "PWD");
        System.out.println("  " + "─".repeat(72));

        for (String[] row : rows) {
            int total = Integer.parseInt(row[1]);
            // Calculate percentage share of total population
            double pct = grandTotal > 0
                ? (total * 100.0 / grandTotal) : 0;

            System.out.printf("  %-18s %8s %7.1f%% %8s %9s %8s %6s%n",
                row[0], row[1], pct,
                row[2], row[3], row[4], row[5]);
        }

        System.out.println("  " + "─".repeat(72));
        System.out.printf("  %-18s %8d %7s%n",
            "TOTAL:", grandTotal, "100.0%");

        // Insight line
        System.out.println();
        printInsights(rows, grandTotal);
        printReportFooter();
    }

    /**
     * Prints actionable insight lines below Report 3.
     * Shows which group is largest and flags high indigent/PWD counts.
     * This elevates the report from a raw count to an operational tool.
     */
    private void printInsights(ArrayList<String[]> rows, int grandTotal) {
        System.out.println("  ── KEY INSIGHTS ──");

        String largestGroup = "";
        int    largestCount = 0;

        for (String[] row : rows) {
            int count = Integer.parseInt(row[1]);
            if (count > largestCount) {
                largestCount = count;
                largestGroup = row[0];
            }
        }

        if (!largestGroup.isEmpty()) {
            System.out.println("  • Largest group : " + largestGroup +
                               " (" + largestCount + " residents)");
        }

        // Flag if indigent population exceeds 20% of total
        int totalIndigent = 0;
        for (String[] row : rows) {
            totalIndigent += Integer.parseInt(row[4]);
        }

        double indigentPct = grandTotal > 0
            ? (totalIndigent * 100.0 / grandTotal) : 0;

        System.out.printf("  • Indigent residents: %d (%.1f%% of total)%n",
            totalIndigent, indigentPct);

        if (indigentPct > 20) {
            System.out.println("  [!] Indigent population exceeds 20%." +
                               " Consider assistance programs.");
        }
    }

    // ── SHARED FORMATTING HELPERS ────────────────────────────────────────────

    /**
     * Prints a consistent report header with timestamp.
     * All 3 reports use this — defined once, reused three times.
     */
    private void printReportHeader(String title) {
        String timestamp = new java.util.Date().toString();
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.printf( "║  %-48s║%n", title);
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.printf( "║  Generated: %-36s║%n",
            timestamp.length() > 36
                ? timestamp.substring(0, 36)
                : timestamp);
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║  Barangay Record Management System               ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println();
    }

    private void printReportFooter() {
        System.out.println("\n  ══════════════ END OF REPORT ══════════════");
        System.out.println("  Report generated by Barangay RMS.");
        System.out.println("  For official use only.\n");
    }
}
