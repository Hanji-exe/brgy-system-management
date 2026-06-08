import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 * DeleteModule.java
 *
 * Handles:
 *   - Delete a resident record (and cascades to their certificates)
 *   - Delete a specific certificate request
 *
 * OOP Concepts Demonstrated:
 *   - Exception Handling : try-catch on all DB deletes
 *   - Encapsulation      : delete logic isolated here
 */
public class DeleteModule {

    private Scanner scanner;

    public DeleteModule() {
        this.scanner = new Scanner(System.in);
    }

    public DeleteModule(Scanner scanner) {
        this.scanner = scanner;
    }

    // ── DELETE MENU ──────────────────────────────────────────────────────────

    public void showDeleteMenu() {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║        DELETE RECORD          ║");
        System.out.println("╠══════════════════════════════╣");
        System.out.println("║  [1] Delete Resident          ║");
        System.out.println("║  [2] Delete Certificate       ║");
        System.out.println("║  [0] Back to Main Menu        ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.print("   Enter choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1: deleteResident();    break;
                case 2: deleteCertificate(); break;
                case 0: break;
                default: System.out.println("[!] Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("[!] Please enter a number.");
        }
    }

    // ── DELETE RESIDENT ──────────────────────────────────────────────────────

    /**
     * Deletes a resident and all their certificate records.
     * Requires confirmation via JOptionPane before executing.
     * Certificates deleted first to avoid FK constraint violation.
     */
    public void deleteResident() {
        String idStr = JOptionPane.showInputDialog(null,
            "Enter Resident ID to delete:",
            "Delete Resident", JOptionPane.PLAIN_MESSAGE);

        if (idStr == null || idStr.trim().isEmpty()) {
            System.out.println("[!] ID is required."); return;
        }

        int residentId;
        try {
            residentId = Integer.parseInt(idStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("[!] ID must be a number."); return;
        }

        // Confirm before deleting
        int confirm = JOptionPane.showConfirmDialog(null,
            "Delete Resident ID " + residentId + "?\n" +
            "This will also delete ALL their certificate records.\n" +
            "This action CANNOT be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            System.out.println("[!] Delete cancelled.");
            return;
        }

        // Delete certificates first (FK constraint)
        String deleteCerts =
            "DELETE FROM certificates WHERE resident_id = ?";
        String deleteResident =
            "DELETE FROM residents WHERE resident_id = ?";

        try (Connection conn = DatabaseHandler.getConnection()) {

            // Delete certificates first
            try (PreparedStatement p1 = conn.prepareStatement(deleteCerts)) {
                p1.setInt(1, residentId);
                int certsDeleted = p1.executeUpdate();
                System.out.println("[✓] Removed " + certsDeleted +
                                   " certificate record(s).");
            }

            // Then delete resident
            try (PreparedStatement p2 = conn.prepareStatement(deleteResident)) {
                p2.setInt(1, residentId);
                int rows = p2.executeUpdate();
                if (rows > 0) {
                    System.out.println("[✓] Resident ID " + residentId +
                                       " deleted successfully.");
                } else {
                    System.out.println("[!] Resident ID " + residentId +
                                       " not found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("[DB ERROR] Failed to delete: " + e.getMessage());
        }
    }

    // ── DELETE CERTIFICATE ───────────────────────────────────────────────────

    /**
     * Deletes a single certificate request by cert_id.
     * Requires confirmation before executing.
     */
    public void deleteCertificate() {
        String idStr = JOptionPane.showInputDialog(null,
            "Enter Certificate ID to delete:",
            "Delete Certificate", JOptionPane.PLAIN_MESSAGE);

        if (idStr == null || idStr.trim().isEmpty()) {
            System.out.println("[!] ID is required."); return;
        }

        int certId;
        try {
            certId = Integer.parseInt(idStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("[!] ID must be a number."); return;
        }

        int confirm = JOptionPane.showConfirmDialog(null,
            "Delete Certificate ID " + certId + "?\n" +
            "This action CANNOT be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            System.out.println("[!] Delete cancelled."); return;
        }

        String sql = "DELETE FROM certificates WHERE cert_id = ?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, certId);
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("[✓] Certificate ID " + certId +
                                   " deleted successfully.");
            } else {
                System.out.println("[!] Certificate ID " + certId +
                                   " not found.");
            }

        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
        }
    }
}
