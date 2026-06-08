package model;

/**
 * Resident.java
 *
 * OOP Concepts Demonstrated:
 *   - Inheritance    : extends Person
 *   - Encapsulation  : private fields, public getters/setters
 *   - Constructor    : default + parameterized
 *   - Polymorphism   : @Override getRole(), toString()
 */
public class Resident extends Person {

    // ── FIELDS ───────────────────────────────────────────────────────────────
    /**
     * Private — not protected.
     * Official extends Resident, but Official does NOT need direct
     * access to these fields. Official uses Resident's getters.
     * True encapsulation: even child classes go through the API.
     */
    private int     age;
    private String  gender;
    private String  civilStatus;
    private String  purok;
    private boolean isVoter;
    private boolean isIndigent;
    private boolean isSenior;
    private boolean isPwd;
    private String  dateAdded;

    // ── CONSTRUCTORS ─────────────────────────────────────────────────────────

    /**
     * Default constructor.
     * Calls super() — triggers Person's default constructor first,
     * then sets Resident-specific fields to safe defaults.
     */
    public Resident() {
        super(); // Person() default constructor
        this.age         = 0;
        this.gender      = "";
        this.civilStatus = "";
        this.purok       = "";
        this.isVoter     = false;
        this.isIndigent  = false;
        this.isSenior    = false;
        this.isPwd       = false;
        this.dateAdded   = "";
    }

    /**
     * Parameterized constructor.
     * Calls super(id, firstName, ...) to initialize Person fields,
     * then handles Resident-specific fields.
     * Used when loading a resident record from the database.
     */
    public Resident(int id, String firstName, String lastName,
                    String address, String contactNumber,
                    int age, String gender, String civilStatus,
                    String purok, boolean isVoter, boolean isIndigent,
                    boolean isSenior, boolean isPwd, String dateAdded) {
        super(id, firstName, lastName, address, contactNumber);
        this.age         = age;
        this.gender      = gender;
        this.civilStatus = civilStatus;
        this.purok       = purok;
        this.isVoter     = isVoter;
        this.isIndigent  = isIndigent;
        this.isSenior    = isSenior;
        this.isPwd       = isPwd;
        this.dateAdded   = dateAdded;
    }

    // ── ABSTRACT METHOD IMPLEMENTATION ───────────────────────────────────────

    /**
     * Fulfills Person's abstract contract.
     * Returns "Resident" as this object's role label.
     */
    @Override
    public String getRole() {
        return "Resident";
    }

    // ── HELPER METHOD ────────────────────────────────────────────────────────

    /**
     * Returns a comma-separated string of all active status tags.
     * Used in View and Report modules to display tags cleanly.
     * Example output: "Indigent, Senior, PWD"
     * If no tags are active, returns "None".
     */
    public String getStatusTags() {
        StringBuilder tags = new StringBuilder();
        if (isIndigent) tags.append("Indigent, ");
        if (isSenior)   tags.append("Senior, ");
        if (isPwd)      tags.append("PWD, ");
        if (isVoter)    tags.append("Voter, ");

        if (tags.length() == 0) return "None";

        // Remove trailing ", "
        return tags.substring(0, tags.length() - 2);
    }

    /**
     * toString() — overrides Person's toString().
     * Adds Resident-specific fields to the display.
     * Called by View and Search modules.
     */
    @Override
    public String toString() {
        return "================================\n" +
               "Resident ID  : " + id              + "\n" +
               "Name         : " + getFullName()   + "\n" +
               "Age          : " + age             + "\n" +
               "Gender       : " + gender          + "\n" +
               "Civil Status : " + civilStatus     + "\n" +
               "Address      : " + address         + "\n" +
               "Purok        : " + purok           + "\n" +
               "Contact      : " + contactNumber   + "\n" +
               "Status Tags  : " + getStatusTags() + "\n" +
               "Date Added   : " + dateAdded       + "\n" +
               "================================";
    }

    // ── GETTERS AND SETTERS ──────────────────────────────────────────────────

    public int     getAge()         { return age;         }
    public String  getGender()      { return gender;      }
    public String  getCivilStatus() { return civilStatus; }
    public String  getPurok()       { return purok;       }
    public boolean isVoter()        { return isVoter;     }
    public boolean isIndigent()     { return isIndigent;  }
    public boolean isSenior()       { return isSenior;    }
    public boolean isPwd()          { return isPwd;       }
    public String  getDateAdded()   { return dateAdded;   }

    public void setAge(int age)                 { this.age         = age;         }
    public void setGender(String gender)        { this.gender      = gender;      }
    public void setCivilStatus(String cs)       { this.civilStatus = cs;          }
    public void setPurok(String purok)          { this.purok       = purok;       }
    public void setVoter(boolean isVoter)       { this.isVoter     = isVoter;     }
    public void setIndigent(boolean isIndigent) { this.isIndigent  = isIndigent;  }
    public void setSenior(boolean isSenior)     { this.isSenior    = isSenior;    }
    public void setPwd(boolean isPwd)           { this.isPwd       = isPwd;       }
    public void setDateAdded(String dateAdded)  { this.dateAdded   = dateAdded;   }
}
