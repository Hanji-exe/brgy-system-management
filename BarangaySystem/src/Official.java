/**
 * Official.java
 *
 * OOP Concepts Demonstrated:
 *   - Inheritance    : extends Resident (3-level chain: Person→Resident→Official)
 *   - Polymorphism   : @Override getRole(), toString()
 *   - Constructor    : default + parameterized (calls super chain)
 */
public class Official extends Resident {

    // ── FIELDS ───────────────────────────────────────────────────────────────
    /**
     * Official-specific fields only.
     * All Person and Resident fields are inherited —
     * no need to redeclare them here.
     */
    private String position;   // e.g., "Barangay Captain", "Kagawad"
    private String termStart;  // e.g., "2023-01-01"
    private String termEnd;    // e.g., "2025-12-31"

    // ── CONSTRUCTORS ─────────────────────────────────────────────────────────

    /**
     * Default constructor.
     * Chains up: Official() → Resident() → Person()
     */
    public Official() {
        super(); // Resident() default constructor
        this.position  = "";
        this.termStart = "";
        this.termEnd   = "";
    }

    /**
     * Parameterized constructor.
     * Passes all Resident + Person fields up through super(),
     * then sets Official-specific fields.
     */
    public Official(int id, String firstName, String lastName,
                    String address, String contactNumber,
                    int age, String gender, String civilStatus,
                    String purok, boolean isVoter, boolean isIndigent,
                    boolean isSenior, boolean isPwd, String dateAdded,
                    String position, String termStart, String termEnd) {

        super(id, firstName, lastName, address, contactNumber,
              age, gender, civilStatus, purok,
              isVoter, isIndigent, isSenior, isPwd, dateAdded);

        this.position  = position;
        this.termStart = termStart;
        this.termEnd   = termEnd;
    }

    // ── OVERRIDDEN METHODS ───────────────────────────────────────────────────

    /**
     * @Override — returns "Official" instead of "Resident".
     * This is the polymorphism demonstration:
     *
     *   Person p = new Official(...);
     *   p.getRole(); // returns "Official" not "Resident"
     *
     * The method called depends on the ACTUAL object type,
     * not the reference type. That is runtime polymorphism.
     */
    @Override
    public String getRole() {
        return "Official";
    }

    /**
     * @Override — extends Resident's toString() with position info.
     */
    @Override
    public String toString() {
        return "================================\n" +
               "Official ID  : " + getId()          + "\n" +
               "Name         : " + getFullName()    + "\n" +
               "Position     : " + position         + "\n" +
               "Term         : " + termStart +
                                   " to " + termEnd + "\n" +
               "Age          : " + getAge()         + "\n" +
               "Gender       : " + getGender()      + "\n" +
               "Address      : " + getAddress()     + "\n" +
               "Purok        : " + getPurok()       + "\n" +
               "Contact      : " + getContactNumber()+ "\n" +
               "================================";
    }

    // ── OFFICIAL-SPECIFIC METHOD ─────────────────────────────────────────────

    /**
     * Returns a formatted string of this official's position and term.
     * Used in View module when displaying barangay officials separately.
     */
    public String getPositionInfo() {
        return position + " (Term: " + termStart + " to " + termEnd + ")";
    }

    // ── GETTERS AND SETTERS ──────────────────────────────────────────────────

    public String getPosition()  { return position;  }
    public String getTermStart() { return termStart; }
    public String getTermEnd()   { return termEnd;   }

    public void setPosition(String position)   { this.position  = position;  }
    public void setTermStart(String termStart) { this.termStart = termStart; }
    public void setTermEnd(String termEnd)     { this.termEnd   = termEnd;   }
}
