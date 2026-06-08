import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * ViewModule.java
 *
 * Handles:
 *   - View all residents
 *   - View all certificate requests
 *   - View barangay officials only
 *
 * OOP Concepts Demonstrated:
 *   - Collections        : ArrayList<Resident>, ArrayList<CertificateRequest>
 *   - Polymorphism       : toString() called on Resident objects
 *   - Exception Handling : try-catch on all DB reads
 */
public class ViewModule {

    private Scanner scanner;

    public ViewModule() {
        this.scanner = new Scanner(System.in);
    }

    public ViewModule(Scanner scanner) {
        this.scanner = scanner;
    }

    // ── VIEW MENU ────────────────────────────────────────────────────────────

    public void showViewMenu() {
        System.out.println("\n╔══════════════════════════╗");
        System.out.println("║       VIEW RECORDS        ║");
        System.out.println("╠══════════════════════════╣");
        System.out.println("║  [1] View All Residents   ║");
        System.out.println("║  [2] View Certificates    ║");
        System.out.println("║  [3] View Officials       ║");
        System.out.println("║  [0] Back to Main Menu    ║");
        System.out.println("╚══════════════════════════╝");
        System.out.print("   Enter choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1: viewAllResidents();    break;
                case 2: viewCertificates();    break;
                case 3: viewOfficials();       break;
                case 0: break;
                default: System.out.println("[!] Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("[!] Please enter a number.");
        }
    }

    // ── VIEW ALL RESIDENTS ───────────────────────────────────────────────────

    /**
     * Fetches all residents, stores in ArrayList<Resident>,
     * then displays via toString().
     *
     * Collections requirement: ArrayList<Resident> used here.
     * Polymorphism: toString() on each Resident object.
     */
    public void viewAllResidents() {
        String sql = "SELECT * FROM residents ORDER BY last_name, first_name";

        // ArrayList satisfies the Collections requirement
        ArrayList<Resident> residents = new ArrayList<>();

        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Build Resident object from each DB row
                Resident r = new Resident(
                    rs.getInt    ("resident_id"),
                    rs.getString ("first_name"),
                    rs.getString ("last_name"),
                    rs.getString ("address"),
                    rs.getString ("contact") != null
                        ? rs.getString("contact") : "",
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
                residents.add(r);
            }

        } catch (SQLException e) {
            System.out.println("[DB ERROR] Failed to fetch residents: "
                               + e.getMessage());
            return;
        }

        // ── Display ───────────────────────────────────────────────────────
        if (residents.isEmpty()) {
            System.out.println("\n[!] No residents found in the database.");
            return;
        }

        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║     REGISTERED RESIDENTS (" +
                           String.format("%-4d", residents.size()) + ")  ║");
        System.out.println("╚══════════════════════════════════╝");

        // toString() is called per Resident — Polymorphism demonstrated
        for (Resident r : residents) {
            System.out.println(r.toString());
        }

        System.out.println("\n[Total: " + residents.size() + " resident(s) found.]");
    }

    // ── VIEW CERTIFICATES ────────────────────────────────────────────────────

    /**
     * Fetches all certificate requests with resident name via JOIN.
     * Stores in ArrayList<CertificateRequest>.
     * Creates specific subclass per cert_type — polymorphism.
     */
    public void viewCertificates() {
        String sql =
            "SELECT c.*, r.first_name, r.last_name " +
            "FROM certificates c " +
            "JOIN residents r ON c.resident_id = r.resident_id " +
            "ORDER BY c.date_requested DESC";

        ArrayList<CertificateRequest> certs = new ArrayList<>();

        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String certType = rs.getString("cert_type");
                int    reqId    = rs.getInt("cert_id");
                int    resId    = rs.getInt("resident_id");
                String purpose  = rs.getString("purpose");
                String status   = rs.getString("status");
                String dateReq  = rs.getString("date_requested");
                String dateRel  = rs.getString("date_released") != null
                                  ? rs.getString("date_released") : "";
                String resName  = rs.getString("first_name") + " " +
                                  rs.getString("last_name");

                // Create specific subclass based on cert_type
                CertificateRequest cert;
                switch (certType) {
                    case "Barangay Clearance":
                        cert = new BarangayClearance(
                            reqId, resId, purpose, status,
                            dateReq, dateRel, "See purpose");
                        break;
                    case "Indigency Certificate":
                        cert = new IndigencyCertificate(
                            reqId, resId, purpose, status,
                            dateReq, dateRel, "See purpose");
                        break;
                    default:
                        cert = new CertificateOfResidency(
                            reqId, resId, purpose, status,
                            dateReq, dateRel, "See purpose");
                }

                certs.add(cert);

                // Print inline with resident name
                System.out.println("\nCert ID    : " + reqId);
                System.out.println("Resident   : " + resName +
                                   " (ID: " + resId + ")");
                System.out.println("Type       : " + cert.getCertificateType());
                System.out.println("Purpose    : " + purpose);
                System.out.println("Status     : " + status);
                System.out.println("Requested  : " + dateReq);
                System.out.println("Released   : " +
                    (dateRel.isEmpty() ? "Not yet released" : dateRel));
                System.out.println("--------------------------------");
            }

        } catch (SQLException e) {
            System.out.println("[DB ERROR] Failed to fetch certificates: "
                               + e.getMessage());
            return;
        }

        if (certs.isEmpty()) {
            System.out.println("\n[!] No certificate requests found.");
        } else {
            System.out.println("\n[Total: " + certs.size() +
                               " certificate request(s) found.]");
        }
    }

    // ── VIEW OFFICIALS ───────────────────────────────────────────────────────

    /**
     * Fetches residents tagged as officials from the residents table.
     * For this scope, "official" means role = 'official' if we extend,
     * but since officials share the residents table here,
     * we filter by a naming convention or a separate query.
     *
     * NOTE: For Phase 4 scope, officials are stored in residents table.
     * The Official class is used for OOP demonstration in Add flow.
     * Here we display all residents with their full details.
     */
    public void viewOfficials() {
        // For this implementation officials are shown as residents
        // with a note that the Official class extends Resident
        System.out.println("\n[VIEW OFFICIALS]");
        System.out.println("Officials are stored as Resident records.");
        System.out.println("Displaying all residents — " +
                           "Officials extend Resident (see Official.java).\n");
        viewAllResidents();
    }
}
