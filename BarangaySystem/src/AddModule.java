import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 * AddModule.java
 *
 * Handles:
 *   - Add Resident
 *   - Add Certificate Request
 *
 * OOP Concepts Demonstrated:
 *   - Encapsulation      : uses Resident + CertificateRequest objects
 *   - Polymorphism       : creates specific certificate subclass based on user choice
 *   - Exception Handling : try-catch on all DB writes + input validation
 *
 * Input Method: Scanner for menu choices, JOptionPane for data fields
 */
public class AddModule {

    private Scanner scanner;

    // ── CONSTRUCTORS ─────────────────────────────────────────────────────────
    public AddModule() {
        this.scanner = new Scanner(System.in);
    }

    public AddModule(Scanner scanner) {
        this.scanner = scanner;
    }

    // ── ADD MENU ────────────────────────────────────────────────────────────

    /**
     * Entry point for the Add sub-menu.
     * Called from Main Menu when user selects "Add Record".
     */
    public void showAddMenu() {
        System.out.println("\n╔══════════════════════════╗");
        System.out.println("║        ADD RECORD         ║");
        System.out.println("╠══════════════════════════╣");
        System.out.println("║  [1] Add Resident         ║");
        System.out.println("║  [2] Add Certificate      ║");
        System.out.println("║  [0] Back to Main Menu    ║");
        System.out.println("╚══════════════════════════╝");
        System.out.print("   Enter choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1: addResident();    break;
                case 2: addCertificate(); break;
                case 0: break;
                default:
                    System.out.println("[!] Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("[!] Please enter a number.");
        }
    }

    // ── ADD RESIDENT ─────────────────────────────────────────────────────────

    /**
     * Collects resident data via JOptionPane dialogs.
     * Validates required fields before inserting into DB.
     * Creates a Resident object then extracts fields for the SQL insert.
     */
    public void addResident() {
        System.out.println("\n[ADD RESIDENT] Fill in the details.");

        try {
            // ── Collect input via JOptionPane ─────────────────────────────
            String firstName = JOptionPane.showInputDialog(null,
                "First Name:", "Add Resident", JOptionPane.PLAIN_MESSAGE);
            if (firstName == null || firstName.trim().isEmpty()) {
                System.out.println("[!] First name is required."); return;
            }

            String lastName = JOptionPane.showInputDialog(null,
                "Last Name:", "Add Resident", JOptionPane.PLAIN_MESSAGE);
            if (lastName == null || lastName.trim().isEmpty()) {
                System.out.println("[!] Last name is required."); return;
            }

            String ageStr = JOptionPane.showInputDialog(null,
                "Age:", "Add Resident", JOptionPane.PLAIN_MESSAGE);
            if (ageStr == null || ageStr.trim().isEmpty()) {
                System.out.println("[!] Age is required."); return;
            }

            int age;
            try {
                age = Integer.parseInt(ageStr.trim());
                if (age < 0 || age > 150) {
                    System.out.println("[!] Invalid age value."); return;
                }
            } catch (NumberFormatException e) {
                System.out.println("[!] Age must be a number."); return;
            }

            // Gender dropdown via JOptionPane
            String[] genders = {"Male", "Female", "Other"};
            String gender = (String) JOptionPane.showInputDialog(
                null, "Select Gender:", "Add Resident",
                JOptionPane.PLAIN_MESSAGE, null, genders, genders[0]);
            if (gender == null) {
                System.out.println("[!] Gender is required."); return;
            }

            // Civil status dropdown
            String[] civilOptions = {"Single", "Married", "Widowed", "Separated"};
            String civilStatus = (String) JOptionPane.showInputDialog(
                null, "Select Civil Status:", "Add Resident",
                JOptionPane.PLAIN_MESSAGE, null, civilOptions, civilOptions[0]);
            if (civilStatus == null) {
                System.out.println("[!] Civil status is required."); return;
            }

            String address = JOptionPane.showInputDialog(null,
                "Address:", "Add Resident", JOptionPane.PLAIN_MESSAGE);
            if (address == null || address.trim().isEmpty()) {
                System.out.println("[!] Address is required."); return;
            }

            String purok = JOptionPane.showInputDialog(null,
                "Purok / Zone:", "Add Resident", JOptionPane.PLAIN_MESSAGE);
            if (purok == null || purok.trim().isEmpty()) {
                System.out.println("[!] Purok is required."); return;
            }

            String contact = JOptionPane.showInputDialog(null,
                "Contact Number (optional, press Cancel to skip):",
                "Add Resident", JOptionPane.PLAIN_MESSAGE);
            if (contact == null) contact = "";

            // Status tag checkboxes via Yes/No dialogs
            boolean isVoter = JOptionPane.showConfirmDialog(null,
                "Is this resident a registered voter?",
                "Status Tags", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;

            boolean isIndigent = JOptionPane.showConfirmDialog(null,
                "Tag as Indigent?",
                "Status Tags", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;

            boolean isSenior = (age >= 60) || JOptionPane.showConfirmDialog(null,
                "Tag as Senior Citizen?",
                "Status Tags", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;

            boolean isPwd = JOptionPane.showConfirmDialog(null,
                "Tag as Person with Disability (PWD)?",
                "Status Tags", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;

            // ── Build Resident object ──────────────────────────────────────
            Resident resident = new Resident(
                0, firstName.trim(), lastName.trim(),
                address.trim(), contact.trim(),
                age, gender, civilStatus,
                purok.trim(), isVoter, isIndigent, isSenior, isPwd, ""
            );

            // ── Insert into DB ─────────────────────────────────────────────
            insertResident(resident);

        } catch (Exception e) {
            System.out.println("[ADD ERROR] Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Executes the INSERT statement for a Resident object.
     * Uses PreparedStatement with positional parameters.
     */
    private void insertResident(Resident r) {
        String sql =
            "INSERT INTO residents " +
            "(first_name, last_name, age, gender, civil_status, " +
            " address, purok, contact, is_voter, is_indigent, " +
            " is_senior, is_pwd) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString (1,  r.getFirstName());
            pstmt.setString (2,  r.getLastName());
            pstmt.setInt    (3,  r.getAge());
            pstmt.setString (4,  r.getGender());
            pstmt.setString (5,  r.getCivilStatus());
            pstmt.setString (6,  r.getAddress());
            pstmt.setString (7,  r.getPurok());
            pstmt.setString (8,  r.getContactNumber());
            pstmt.setInt    (9,  r.isVoter()    ? 1 : 0);
            pstmt.setInt    (10, r.isIndigent()  ? 1 : 0);
            pstmt.setInt    (11, r.isSenior()    ? 1 : 0);
            pstmt.setInt    (12, r.isPwd()       ? 1 : 0);

            pstmt.executeUpdate();
            System.out.println("[✓] Resident added successfully.");

        } catch (SQLException e) {
            System.out.println("[DB ERROR] Failed to add resident: " + e.getMessage());
        }
    }

    // ── ADD CERTIFICATE ──────────────────────────────────────────────────────

    /**
     * Collects certificate request data.
     * Demonstrates Polymorphism — creates a specific subclass
     * based on user's certificate type selection,
     * but stores it as a CertificateRequest reference.
     */
    public void addCertificate() {
        System.out.println("\n[ADD CERTIFICATE] Fill in the details.");

        try {
            String resIdStr = JOptionPane.showInputDialog(null,
                "Enter Resident ID:", "Add Certificate",
                JOptionPane.PLAIN_MESSAGE);
            if (resIdStr == null || resIdStr.trim().isEmpty()) {
                System.out.println("[!] Resident ID is required."); return;
            }

            int residentId;
            try {
                residentId = Integer.parseInt(resIdStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("[!] Resident ID must be a number."); return;
            }

            // Verify resident exists before inserting certificate
            if (!residentExists(residentId)) {
                System.out.println("[!] Resident ID " + residentId +
                                   " does not exist in the database.");
                return;
            }

            // Certificate type selection
            String[] certTypes = {
                "Barangay Clearance",
                "Indigency Certificate",
                "Certificate of Residency"
            };
            String certType = (String) JOptionPane.showInputDialog(
                null, "Select Certificate Type:", "Add Certificate",
                JOptionPane.PLAIN_MESSAGE, null, certTypes, certTypes[0]);
            if (certType == null) {
                System.out.println("[!] Certificate type is required."); return;
            }

            String purpose = JOptionPane.showInputDialog(null,
                "Purpose of Request:", "Add Certificate",
                JOptionPane.PLAIN_MESSAGE);
            if (purpose == null || purpose.trim().isEmpty()) {
                System.out.println("[!] Purpose is required."); return;
            }

            // ── Polymorphism: create specific subclass ─────────────────────
            CertificateRequest cert;

            switch (certType) {
                case "Barangay Clearance":
                    String clearanceType = JOptionPane.showInputDialog(null,
                        "Clearance Type (e.g., Employment, Travel, Legal):",
                        "Add Certificate", JOptionPane.PLAIN_MESSAGE);
                    if (clearanceType == null) clearanceType = "General";
                    cert = new BarangayClearance(
                        0, residentId, purpose.trim(),
                        "Pending", "", "", clearanceType.trim());
                    break;

                case "Indigency Certificate":
                    String assistanceType = JOptionPane.showInputDialog(null,
                        "Assistance Type (e.g., Medical, Burial, Educational):",
                        "Add Certificate", JOptionPane.PLAIN_MESSAGE);
                    if (assistanceType == null) assistanceType = "General";
                    cert = new IndigencyCertificate(
                        0, residentId, purpose.trim(),
                        "Pending", "", "", assistanceType.trim());
                    break;

                case "Certificate of Residency":
                    String residencyPurpose = JOptionPane.showInputDialog(null,
                        "Residency Purpose (e.g., School Enrollment, Bank):",
                        "Add Certificate", JOptionPane.PLAIN_MESSAGE);
                    if (residencyPurpose == null) residencyPurpose = "General";
                    cert = new CertificateOfResidency(
                        0, residentId, purpose.trim(),
                        "Pending", "", "", residencyPurpose.trim());
                    break;

                default:
                    System.out.println("[!] Unknown certificate type."); return;
            }

            // ── Insert into DB ─────────────────────────────────────────────
            insertCertificate(cert, certType);

        } catch (Exception e) {
            System.out.println("[ADD ERROR] Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Executes INSERT for a CertificateRequest.
     * Accepts the certType string separately because
     * getCertificateType() is on the object — but SQL
     * needs the value as a parameter.
     */
    private void insertCertificate(CertificateRequest cert, String certType) {
        String sql =
            "INSERT INTO certificates " +
            "(resident_id, cert_type, purpose, status) " +
            "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt   (1, cert.getResidentId());
            pstmt.setString(2, certType);
            pstmt.setString(3, cert.getPurpose());
            pstmt.setString(4, cert.getStatus());

            pstmt.executeUpdate();
            System.out.println("[✓] Certificate request added successfully.");
            System.out.println("    Type    : " + cert.getCertificateType());
            System.out.println("    Details : " + cert.generateDetails());

        } catch (SQLException e) {
            System.out.println("[DB ERROR] Failed to add certificate: "
                               + e.getMessage());
        }
    }

    /**
     * Checks if a resident_id exists in the residents table.
     * Prevents FK violation — certificate cannot be added
     * for a non-existent resident.
     */
    private boolean residentExists(int residentId) {
        String sql = "SELECT resident_id FROM residents WHERE resident_id = ?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, residentId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
            return false;
        }
    }
}
