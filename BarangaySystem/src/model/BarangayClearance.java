package model;

/**
 * BarangayClearance.java
 *
 * OOP Concepts Demonstrated:
 *   - Inheritance  : extends CertificateRequest
 *   - Polymorphism : @Override getCertificateType(), generateDetails()
 *   - Constructor  : default + parameterized
 */
public class BarangayClearance extends CertificateRequest {

    // ── FIELDS ───────────────────────────────────────────────────────────────
    /**
     * Clearance type — describes what the clearance is for.
     * Examples: "Employment", "Travel", "Legal"
     */
    private String clearanceType;

    // ── CONSTRUCTORS ─────────────────────────────────────────────────────────

    public BarangayClearance() {
        super();
        this.clearanceType = "";
    }

    public BarangayClearance(int requestId, int residentId, String purpose,
                              String status, String dateRequested,
                              String dateReleased, String clearanceType) {
        super(requestId, residentId, purpose, status,
              dateRequested, dateReleased);
        this.clearanceType = clearanceType;
    }

    // ── OVERRIDDEN METHODS ───────────────────────────────────────────────────

    @Override
    public String getCertificateType() {
        return "Barangay Clearance";
    }

    @Override
    public String generateDetails() {
        return "Clearance Type: " + clearanceType;
    }

    // ── GETTERS AND SETTERS ──────────────────────────────────────────────────

    public String getClearanceType()              { return clearanceType;  }
    public void   setClearanceType(String type)   { this.clearanceType = type; }
}
