import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 * UpdateModule.java
 *
 * Handles:
 *   - Update resident information
 *   - Update certificate status
 *
 * OOP Concepts Demonstrated:
 *   - Exception Handling : try-catch on all DB writes
 *   - Encapsulation      : update logic isolated in this class
 */
public class UpdateModule {

    private Scanner scanner;

    public UpdateModule() {
        this.scanner = new Scanner(System.in);
    }

    public UpdateModule(Scanner scanner) {
        this.scanner = scanner;
    }

    // ── UPDATE MENU ──────────────────────────────────────────────────────────

    public void showUpdateMenu() {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║        UPDATE RECORD          ║");
        System.out.println("╠══════════════════════════════╣");
        System.out.println("║  [1] Update Resident Info     ║");
        System.out.println("║  [2] Update Certificate Status║");
        System.out.println("║  [0] Back to Main Menu        ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.print("   Enter choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1: updateResident();          break;
                case 2: updateCertificateStatus(); break;
                case 0: break;
                default: System.out.println("[!] Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("[!] Please enter a number.");
        }
    }

    // ── UPDATE RESIDENT ──────────────────────────────────────────────────────

    /**
     * Finds resident by ID, displays current record,
     * then collects updated values via JOptionPane.
     * Empty input = keep existing value (non-destructive update).
     */
    public void updateResident() {
        String idStr = JOptionPane.showInputDialog(null,
            "Enter Resident ID to update:",
            "Update Resident", JOptionPane.PLAIN_MESSAGE);

        if (idStr == null || idStr.trim().isEmpty()) {
            System.out.println("[!] ID is required."); return;
        }

        int residentId;
        try {
            residentId = Integer.parseInt(idStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("[!] ID must be a number."); return;
        }

        // Fetch existing record first
        Resident existing = fetchResidentById(residentId);
        if (existing == null) {
            System.out.println("[!] Resident ID " + residentId + " not found.");
            return;
        }

        // Show current record
        System.out.println("\n[Current Record]");
        System.out.println(existing.toString());
        System.out.println("\nLeave blank to keep current value.\n");

        // Collect new values — blank = keep existing
        String firstName = JOptionPane.showInputDialog(null,
            "First Name [" + existing.getFirstName() + "]:",
            "Update Resident", JOptionPane.PLAIN_MESSAGE);
        if (firstName == null || firstName.trim().isEmpty())
            firstName = existing.getFirstName();

        String lastName = JOptionPane.showInputDialog(null,
            "Last Name [" + existing.getLastName() + "]:",
            "Update Resident", JOptionPane.PLAIN_MESSAGE);
        if (lastName == null || lastName.trim().isEmpty())
            lastName = existing.getLastName();

        String ageStr = JOptionPane.showInputDialog(null,
            "Age [" + existing.getAge() + "]:",
            "Update Resident", JOptionPane.PLAIN_MESSAGE);
        int age = existing.getAge();
        if (ageStr != null && !ageStr.trim().isEmpty()) {
            try {
                age = Integer.parseInt(ageStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid age — keeping existing value.");
            }
        }

        String address = JOptionPane.showInputDialog(null,
            "Address [" + existing.getAddress() + "]:",
            "Update Resident", JOptionPane.PLAIN_MESSAGE);
        if (address == null || address.trim().isEmpty())
            address = existing.getAddress();

        String purok = JOptionPane.showInputDialog(null,
            "Purok [" + existing.getPurok() + "]:",
            "Update Resident", JOptionPane.PLAIN_MESSAGE);
        if (purok == null || purok.trim().isEmpty())
            purok = existing.getPurok();

        String contact = JOptionPane.showInputDialog(null,
            "Contact [" + existing.getContactNumber() + "]:",
            "Update Resident", JOptionPane.PLAIN_MESSAGE);
        if (contact == null) contact = existing.getContactNumber();

        // Execute update
        String sql =
            "UPDATE residents SET " +
            "first_name = ?, last_name = ?, age = ?, " +
            "address = ?, purok = ?, contact = ? " +
            "WHERE resident_id = ?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, firstName.trim());
            pstmt.setString(2, lastName.trim());
            pstmt.setInt   (3, age);
            pstmt.setString(4, address.trim());
            pstmt.setString(5, purok.trim());
            pstmt.setString(6, contact.trim());
            pstmt.setInt   (7, residentId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("[✓] Resident updated successfully.");
            } else {
                System.out.println("[!] No changes made.");
            }

        } catch (SQLException e) {
            System.out.println("[DB ERROR] Failed to update: " + e.getMessage());
        }
    }

    // ── UPDATE CERTIFICATE STATUS ────────────────────────────────────────────

    /**
     * Updates a certificate's status and sets date_released
     * automatically when status is changed to "Released".
     */
    public void updateCertificateStatus() {
        String idStr = JOptionPane.showInputDialog(null,
            "Enter Certificate ID to update status:",
            "Update Certificate", JOptionPane.PLAIN_MESSAGE);

        if (idStr == null || idStr.trim().isEmpty()) {
            System.out.println("[!] Certificate ID is required."); return;
        }

        int certId;
        try {
            certId = Integer.parseInt(idStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("[!] ID must be a number."); return;
        }

        String[] statusOptions = {"Pending", "Approved", "Released"};
        String newStatus = (String) JOptionPane.showInputDialog(
            null, "Select new status:", "Update Certificate Status",
            JOptionPane.PLAIN_MESSAGE, null, statusOptions, statusOptions[0]);

        if (newStatus == null) {
            System.out.println("[!] Status selection cancelled."); return;
        }

        // If Released, auto-set date_released to today
        String sql;
        if (newStatus.equals("Released")) {
            sql = "UPDATE certificates SET status = ?, " +
                  "date_released = date('now') WHERE cert_id = ?";
        } else {
            sql = "UPDATE certificates SET status = ? WHERE cert_id = ?";
        }

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt   (2, certId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("[✓] Certificate status updated to: "
                                   + newStatus);
            } else {
                System.out.println("[!] Certificate ID " + certId +
                                   " not found.");
            }

        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
        }
    }

    // ── HELPER ───────────────────────────────────────────────────────────────

    private Resident fetchResidentById(int residentId) {
        String sql = "SELECT * FROM residents WHERE resident_id = ?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, residentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Resident(
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
            }

        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
        }
        return null;
    }
}
