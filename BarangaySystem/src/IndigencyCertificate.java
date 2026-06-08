/**
 * IndigencyCertificate.java
 *
 * OOP Concepts Demonstrated:
 *   - Inheritance  : extends CertificateRequest
 *   - Polymorphism : @Override getCertificateType(), generateDetails()
 *   - Constructor  : default + parameterized
 */
public class IndigencyCertificate extends CertificateRequest {

    // ── FIELDS ───────────────────────────────────────────────────────────────
    /**
     * Type of assistance this certificate supports.
     * Examples: "Medical", "Burial", "Educational"
     */
    private String assistanceType;

    // ── CONSTRUCTORS ─────────────────────────────────────────────────────────

    public IndigencyCertificate() {
        super();
        this.assistanceType = "";
    }

    public IndigencyCertificate(int requestId, int residentId, String purpose,
                                 String status, String dateRequested,
                                 String dateReleased, String assistanceType) {
        super(requestId, residentId, purpose, status,
              dateRequested, dateReleased);
        this.assistanceType = assistanceType;
    }

    // ── OVERRIDDEN METHODS ───────────────────────────────────────────────────

    @Override
    public String getCertificateType() {
        return "Indigency Certificate";
    }

    @Override
    public String generateDetails() {
        return "Assistance Type: " + assistanceType;
    }

    // ── GETTERS AND SETTERS ──────────────────────────────────────────────────

    public String getAssistanceType()             { return assistanceType;      }
    public void   setAssistanceType(String type)  { this.assistanceType = type; }
}
