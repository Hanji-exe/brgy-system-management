package model;

/**
 * Person.java
 *
 * OOP Concepts Demonstrated:
 *   - Abstraction    : abstract class with abstract method getRole()
 *   - Constructor    : default + parameterized constructors
 *   - Encapsulation  : protected fields, public methods
 */
public abstract class Person {

    // ── FIELDS ───────────────────────────────────────────────────────────────
    /**
     * Protected — not private.
     * Subclasses (Resident, Official) inherit these fields directly
     * without needing getters just to access their own data internally.
     * Still hidden from outside classes — encapsulation is preserved.
     */
    protected int    id;
    protected String firstName;
    protected String lastName;
    protected String address;
    protected String contactNumber;

    // ── CONSTRUCTORS ─────────────────────────────────────────────────────────

    /**
     * Default constructor.
     * Required by rubric. Used when creating an empty Person object
     * before populating fields one by one (e.g., from a form input).
     */
    public Person() {
        this.id            = 0;
        this.firstName     = "";
        this.lastName      = "";
        this.address       = "";
        this.contactNumber = "";
    }

    /**
     * Parameterized constructor.
     * Used when building a Person object directly from a DB result set
     * or from a fully collected set of user inputs.
     */
    public Person(int id, String firstName, String lastName,
                  String address, String contactNumber) {
        this.id            = id;
        this.firstName     = firstName;
        this.lastName      = lastName;
        this.address       = address;
        this.contactNumber = contactNumber;
    }

    // ── ABSTRACT METHOD ──────────────────────────────────────────────────────

    /**
     * Abstract method — no body here.
     *
     * Every concrete subclass MUST override this and return
     * its own role label. This is the Abstraction requirement.
     *
     * Resident  → returns "Resident"
     * Official  → returns "Official"
     *
     * If a subclass forgets to implement this, the compiler
     * throws an error — abstraction is enforced at compile time.
     */
    public abstract String getRole();

    // ── CONCRETE METHODS ─────────────────────────────────────────────────────

    /**
     * Returns the full name as "FirstName LastName".
     * Shared across all subclasses — defined once here,
     * inherited by Resident and Official for free.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * toString() — overrides java.lang.Object's default.
     * Subclasses will override this again with their own fields.
     * Demonstrates polymorphism when toString() is called
     * on a Person reference pointing to a Resident or Official.
     */
    @Override
    public String toString() {
        return "ID: "       + id            + "\n" +
               "Name: "     + getFullName() + "\n" +
               "Address: "  + address       + "\n" +
               "Contact: "  + contactNumber + "\n" +
               "Role: "     + getRole();
    }

    // ── GETTERS AND SETTERS ──────────────────────────────────────────────────

    public int    getId()            { return id;            }
    public String getFirstName()     { return firstName;     }
    public String getLastName()      { return lastName;      }
    public String getAddress()       { return address;       }
    public String getContactNumber() { return contactNumber; }

    public void setId(int id)                       { this.id            = id;            }
    public void setFirstName(String firstName)       { this.firstName     = firstName;     }
    public void setLastName(String lastName)         { this.lastName      = lastName;      }
    public void setAddress(String address)           { this.address       = address;       }
    public void setContactNumber(String contactNumber){ this.contactNumber = contactNumber; }
}
