package model;

/**
 * CertificateOfResidency.java
 *
 * OOP Concepts Demonstrated:
 *   - Inheritance  : extends CertificateRequest
 *   - Polymorphism : @Override getCertificateType(), generateDetails()
 *   - Constructor  : default + parameterized
 */
public class CertificateOfResidency extends CertificateRequest {

    // ── FIELDS ───────────────────────────────────────────────────────────────
    /**
     * The specific purpose of residency proof.
     * Examples: "School Enrollment", "Government Benefit", "Bank Requirement"
     */
    private String residencyPurpose;

    // ── CONSTRUCTORS ─────────────────────────────────────────────────────────

    public CertificateOfResidency() {
        super();
        this.residencyPurpose = "";
    }

    public CertificateOfResidency(int requestId, int residentId, String purpose,
                                   String status, String dateRequested,
                                   String dateReleased, String residencyPurpose) {
        super(requestId, residentId, purpose, status,
              dateRequested, dateReleased);
        this.residencyPurpose = residencyPurpose;
    }

    // ── OVERRIDDEN METHODS ───────────────────────────────────────────────────

    @Override
    public String getCertificateType() {
        return "Certificate of Residency";
    }

    @Override
    public String generateDetails() {
        return "Residency Purpose: " + residencyPurpose;
    }

    // ── GETTERS AND SETTERS ──────────────────────────────────────────────────

    public String getResidencyPurpose()              { return residencyPurpose;      }
    public void   setResidencyPurpose(String purpose){ this.residencyPurpose = purpose; }
}
