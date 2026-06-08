package model;

/**
 * CertificateRequest.java
 *
 * OOP Concepts Demonstrated:
 *   - Abstraction  : abstract class, two abstract methods
 *   - Constructor  : default + parameterized
 *   - Encapsulation: protected fields, public getters/setters
 *   - Polymorphism : abstract methods overridden by all 3 subclasses
 */
public abstract class CertificateRequest {

    // ── FIELDS ───────────────────────────────────────────────────────────────
    protected int    requestId;
    protected int    residentId;
    protected String purpose;
    protected String status;
    protected String dateRequested;
    protected String dateReleased;

    // ── CONSTRUCTORS ─────────────────────────────────────────────────────────

    public CertificateRequest() {
        this.requestId     = 0;
        this.residentId    = 0;
        this.purpose       = "";
        this.status        = "Pending";
        this.dateRequested = "";
        this.dateReleased  = "";
    }

    public CertificateRequest(int requestId, int residentId, String purpose,
                               String status, String dateRequested,
                               String dateReleased) {
        this.requestId     = requestId;
        this.residentId    = residentId;
        this.purpose       = purpose;
        this.status        = status;
        this.dateRequested = dateRequested;
        this.dateReleased  = dateReleased;
    }

    // ── ABSTRACT METHODS ─────────────────────────────────────────────────────

    /**
     * Returns the type label for this certificate.
     * Each subclass returns its own specific string.
     *
     * BarangayClearance    → "Barangay Clearance"
     * IndigencyCertificate → "Indigency Certificate"
     * CertificateOfResidency → "Certificate of Residency"
     */
    public abstract String getCertificateType();

    /**
     * Returns a formatted details string for this certificate.
     * Each subclass includes its own unique field in the output.
     * This is where polymorphism is most visible —
     * same method call, different output per subclass.
     */
    public abstract String generateDetails();

    // ── CONCRETE METHOD ──────────────────────────────────────────────────────

    /**
     * Shared display method used by all certificate subclasses.
     * Calls getCertificateType() and generateDetails() —
     * both are abstract, so the actual output depends on
     * which subclass is calling this at runtime.
     */
    @Override
    public String toString() {
        return "================================\n" +
               "Certificate ID   : " + requestId        + "\n" +
               "Resident ID      : " + residentId       + "\n" +
               "Type             : " + getCertificateType() + "\n" +
               "Purpose          : " + purpose          + "\n" +
               "Status           : " + status           + "\n" +
               "Date Requested   : " + dateRequested    + "\n" +
               "Date Released    : " + (dateReleased.isEmpty()
                                        ? "Not yet released"
                                        : dateReleased) + "\n" +
               "Details          : " + generateDetails() + "\n" +
               "================================";
    }

    // ── GETTERS AND SETTERS ──────────────────────────────────────────────────

    public int    getRequestId()     { return requestId;     }
    public int    getResidentId()    { return residentId;    }
    public String getPurpose()       { return purpose;       }
    public String getStatus()        { return status;        }
    public String getDateRequested() { return dateRequested; }
    public String getDateReleased()  { return dateReleased;  }

    public void setRequestId(int requestId)          { this.requestId     = requestId;     }
    public void setResidentId(int residentId)        { this.residentId    = residentId;    }
    public void setPurpose(String purpose)           { this.purpose       = purpose;       }
    public void setStatus(String status)             { this.status        = status;        }
    public void setDateRequested(String dr)          { this.dateRequested = dr;            }
    public void setDateReleased(String dr)           { this.dateReleased  = dr;            }
}
