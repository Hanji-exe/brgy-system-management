# Barangay Record Management System (BRMS)
**OOP Final Project — Polytechnic University of the Philippines**
**Course: Object-Oriented Programming with Database Integration**
**Submission Date: June 5, 2025**

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [Objectives](#2-objectives)
3. [Scope and Limitations](#3-scope-and-limitations)
4. [System Features](#4-system-features)
5. [OOP Concepts Applied](#5-oop-concepts-applied)
6. [Database Design](#6-database-design)
7. [Entity Relationship Diagram](#7-entity-relationship-diagram)
8. [System Flowchart](#8-system-flowchart)
9. [Source Code Explanation](#9-source-code-explanation)
10. [How to Run](#10-how-to-run)
11. [Conclusion](#11-conclusion)

---

## 1. Introduction

The **Barangay Record Management System (BRMS)** is a desktop-based information system developed in Java, designed to address the persistent challenges faced by barangay offices in managing resident records and certificate issuance processes. In the Philippines, the barangay is the smallest administrative unit of local government and serves as the frontline of public service delivery. Despite this critical role, many barangay offices continue to rely on manual, paper-based systems for recording resident information, tracking certificate requests, and generating official documents.

This reliance on manual record-keeping leads to several operational problems: slow and inefficient certificate issuance, lost or duplicate records, inaccurate population data, and difficulty retrieving specific resident information during emergencies or assistance distribution. The government's ongoing push for local-level digitalization through initiatives such as the **eGovPH platform** makes the development of a barangay-level information system both timely and relevant.

The BRMS provides barangay staff with a **centralized, offline-capable system** for managing resident records and certificate requests. The system is built using Java with an SQLite database, enabling it to function without internet connectivity — a critical requirement given that many barangays in the Philippines experience intermittent or no internet service. Input is handled through both **Scanner** (console navigation) and **JOptionPane** (dialog-based data entry), providing a practical and accessible interface for barangay staff.

The system is developed as the final project for the Object-Oriented Programming with Database Integration course. It demonstrates the practical application of core OOP principles — **Abstraction, Encapsulation, Inheritance, Polymorphism, Constructors, Exception Handling, and Collections** — within a real-world domain that directly addresses a recognized public administration challenge in the Philippines.

---

## 2. Objectives

The Barangay Record Management System aims to achieve the following objectives:

1. To develop a functional, menu-driven Java application that digitizes barangay resident records and replaces paper-based record-keeping with a structured database system.
2. To implement all seven core OOP concepts — **Abstraction, Encapsulation, Inheritance, Polymorphism, Constructors, Exception Handling, and Collections** — within a coherent class hierarchy that models real-world barangay entities.
3. To provide a complete set of data management operations including **Login, Add, View, Search, Update, Delete, and Report Generation**, all connected to an SQLite database with a minimum of three related tables.
4. To enable barangay staff to issue and track three types of official certificates — **Barangay Clearance, Indigency Certificate, and Certificate of Residency** — with status tracking from request to release.
5. To support targeted social assistance programs by tagging residents with demographic status indicators including **Indigent, Senior Citizen, Person with Disability (PWD), and Registered Voter**, and to generate aggregated reports based on these classifications.
6. To generate three operationally meaningful reports: **population distribution per Purok**, **monthly certificate request trends by type**, and **age group demographic breakdown** — enabling data-driven decision-making at the barangay level.
7. To implement a secure login system with attempt limiting and input validation using exception handling throughout all modules, ensuring the system fails gracefully without data corruption or application crashes.

---

## 3. Scope and Limitations

### 3.1 Scope

The Barangay Record Management System covers the following functional areas:

- **Resident record management**: adding, viewing, searching, updating, and deleting resident records with demographic information and status tags.
- **Certificate request management**: creating, viewing, updating the status of, and deleting certificate requests for three certificate types, linked to resident records via foreign key.
- **Login authentication**: username and password verification against the `users` table with a maximum of three login attempts.
- **Report generation**: three aggregated data reports covering population per purok, certificate request trends by type and month, and resident age group distribution.
- **Offline operation**: the system uses SQLite as its database engine, requiring no network connection, server, or external database installation.
- **Local deployment**: the system is intended for single-workstation use by barangay staff, with the database file stored locally alongside the application.

### 3.2 Limitations

The following features are explicitly **outside the scope** of this system:

- Blotter and incident record management is not implemented.
- Business permit and household group management are not included.
- Role-based access control enforcement is not implemented beyond the login screen. All authenticated users have access to all system functions.
- **Password encryption is not applied.** Passwords are stored as plain text in the database. Production deployment would require hashing.
- The system does not generate printable or exportable documents (PDF, Word, or Excel). Certificate details are displayed in the console only.
- The system does not include network functionality, multi-user concurrent access, or cloud synchronization.
- No GUI framework such as Java Swing or JavaFX is used beyond JOptionPane dialogs. Navigation is primarily console-based.

---

## 4. System Features

| Feature | Description |
|---------|-------------|
| **Login Module** | Staff authenticate using a username and password stored in the `users` table. JOptionPane dialogs collect credentials. The system allows 3 attempts before locking access. Credentials are validated using `PreparedStatement` to prevent SQL injection. |
| **Add Resident** | New resident records are added through JOptionPane input dialogs. Required fields include name, age, gender, civil status, address, purok, and optional contact number. Status tags (Indigent, Senior, PWD, Voter) are assigned via Yes/No confirmation dialogs. Senior citizen status is auto-detected if age is 60 or above. |
| **Add Certificate** | Certificate requests are linked to an existing resident via Resident ID. Staff select from three certificate types: Barangay Clearance, Indigency Certificate, and Certificate of Residency. Each type collects a type-specific field. The system verifies the Resident ID exists before inserting. |
| **View Records** | Displays all residents ordered alphabetically or all certificate requests with resident names via a JOIN query. Results are stored in an `ArrayList` before display, satisfying the Collections requirement. Each `Resident` object's `toString()` method formats the output. |
| **Search Records** | Residents can be searched by full or partial name (LIKE query), by Resident ID (exact match), or by Purok name (LIKE query). Results are stored in `ArrayList<Resident>` and displayed using `Resident.toString()`. |
| **Update Records** | Resident information (name, age, address, purok, contact) can be updated. The current record is displayed before editing, and blank input retains the existing value. Certificate request status (Pending / Approved / Released) can also be updated, with automatic `date_released` assignment when status is set to Released. |
| **Delete Records** | Residents and their associated certificates can be deleted after a JOptionPane confirmation prompt. Certificate records are deleted first to satisfy the foreign key constraint before the resident record is removed. Individual certificate records can also be deleted independently. |
| **Report Generation** | Three aggregated reports: **(1)** Residents per Purok showing total population and status tag counts per zone; **(2)** Certificate Requests by Type showing monthly counts with status breakdowns; **(3)** Age Group Distribution showing demographic breakdown by Minor, Adult, and Senior groups with gender and status tag sub-counts and percentage share. |

---

## 5. OOP Concepts Applied

| OOP Concept | Class / Location | How It Is Applied |
|-------------|-----------------|-------------------|
| **Abstraction** | `Person.java`, `CertificateRequest.java` | Both are declared as `abstract` classes. `Person` defines `abstract getRole()` and `CertificateRequest` defines `abstract getCertificateType()` and `generateDetails()`. Subclasses are forced to implement these methods — the compiler rejects any concrete subclass that does not. |
| **Encapsulation** | `Resident.java`, `Official.java` | All instance fields are declared `private`. Public getter and setter methods control all access and modification. No field is directly accessible from outside the class. |
| **Inheritance** | `Resident extends Person`; `Official extends Resident`; 3 certificate subclasses `extend CertificateRequest` | Two distinct inheritance chains. Chain 1: `Person → Resident → Official` (3-level). Chain 2: `CertificateRequest → BarangayClearance / IndigencyCertificate / CertificateOfResidency`. |
| **Polymorphism** | `getRole()`, `getCertificateType()`, `generateDetails()`, `toString()` | Method overriding demonstrated in every subclass. A `CertificateRequest` reference resolves `getCertificateType()` and `generateDetails()` differently per subclass at runtime. |
| **Constructors** | All 8 classes | Every class implements both a **default** (no-arg) and **parameterized** constructor. Subclass constructors use `super()` to invoke the parent constructor first. |
| **Exception Handling** | `DatabaseHandler.java`, all CRUD modules | All DB operations are wrapped in `try-catch` blocks targeting `SQLException`. Input parsing is protected against `NumberFormatException`. `try-with-resources` ensures automatic closure of `Connection` and `Statement` objects. |
| **Collections** | `ViewModule.java`, `SearchModule.java`, `ReportModule.java` | `ArrayList<Resident>` and `ArrayList<CertificateRequest>` collect and store query results before display. `ArrayList<String[]>` aggregates report data rows. |

### 5.1 Class Hierarchy

**Chain 1 — Person Hierarchy**
```
java.lang.Object
└── Person  [ABSTRACT]
    └── Resident  [CONCRETE] — extends Person
        └── Official  [CONCRETE] — extends Resident
```

**Chain 2 — CertificateRequest Hierarchy**
```
java.lang.Object
└── CertificateRequest  [ABSTRACT]
    ├── BarangayClearance      [CONCRETE]
    ├── IndigencyCertificate   [CONCRETE]
    └── CertificateOfResidency [CONCRETE]
```

### 5.2 Class Summary

| # | Class | Type | Extends | OOP Role |
|---|-------|------|---------|----------|
| 1 | `Person` | Abstract | — | Root abstract class. Defines shared identity fields and `abstract getRole()`. |
| 2 | `Resident` | Concrete | `Person` | Primary entity. Private fields + full getters/setters = Encapsulation. Overrides `getRole()` and `toString()`. |
| 3 | `Official` | Concrete | `Resident` | 3-level chain. Adds position and term fields. `getRole()` → `"Official"`. |
| 4 | `CertificateRequest` | Abstract | — | Second abstract root. Defines `abstract getCertificateType()` and `generateDetails()`. |
| 5 | `BarangayClearance` | Concrete | `CertificateRequest` | `getCertificateType()` → `"Barangay Clearance"`. |
| 6 | `IndigencyCertificate` | Concrete | `CertificateRequest` | `getCertificateType()` → `"Indigency Certificate"`. |
| 7 | `CertificateOfResidency` | Concrete | `CertificateRequest` | `getCertificateType()` → `"Certificate of Residency"`. |
| 8 | `DatabaseHandler` | Concrete | — | Utility class. `getConnection()` + `initializeDatabase()`. Demonstrates Exception Handling. |

> **Total: 8 classes — exceeds minimum requirement of 5 by 3.**

---

## 6. Database Design

### 6.1 Technology

- **Engine**: SQLite via `sqlite-jdbc-3.45.1.0.jar`
- **No server required**: database is a single `.db` file in the project directory
- **Offline-capable**: no internet connection needed
- **JDBC-compatible**: migrating to MySQL/PostgreSQL requires minimal code changes

### 6.2 Tables and Relationships

| Table | Key Fields | Purpose |
|-------|-----------|---------|
| `users` | `user_id` (PK), `username` (UNIQUE) | Login credentials. Standalone — no FK. Default admin inserted on first run. |
| `residents` | `resident_id` (PK), `purok`, `is_indigent`, `is_senior`, `is_pwd`, `is_voter` | Master resident database. Status tag columns (INTEGER 0/1) support assistance programs and demographic reporting. |
| `certificates` | `cert_id` (PK), `resident_id` (FK → residents) | Certificate request tracking. FK enforces referential integrity — no orphan records. Status tracks issuance pipeline. |

### 6.3 Relationship

```
users          ──── standalone (no FK)

residents ──┐
            │  1 resident → 0..* certificates
certificates─┘  FK: certificates.resident_id → residents.resident_id
```

### 6.4 SQL Schema

```sql
-- TABLE 1: users
CREATE TABLE IF NOT EXISTS users (
    user_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    username  TEXT NOT NULL UNIQUE,
    password  TEXT NOT NULL,
    role      TEXT NOT NULL DEFAULT 'staff'
);
INSERT OR IGNORE INTO users (username, password, role)
VALUES ('admin', 'admin123', 'admin');

-- TABLE 2: residents
CREATE TABLE IF NOT EXISTS residents (
    resident_id  INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name   TEXT    NOT NULL,
    last_name    TEXT    NOT NULL,
    age          INTEGER NOT NULL,
    gender       TEXT    NOT NULL,
    civil_status TEXT    NOT NULL,
    address      TEXT    NOT NULL,
    purok        TEXT    NOT NULL,
    contact      TEXT,
    is_voter     INTEGER DEFAULT 0,
    is_indigent  INTEGER DEFAULT 0,
    is_senior    INTEGER DEFAULT 0,
    is_pwd       INTEGER DEFAULT 0,
    date_added   TEXT    DEFAULT (date('now'))
);

-- TABLE 3: certificates
CREATE TABLE IF NOT EXISTS certificates (
    cert_id        INTEGER PRIMARY KEY AUTOINCREMENT,
    resident_id    INTEGER NOT NULL,
    cert_type      TEXT    NOT NULL,
    purpose        TEXT    NOT NULL,
    status         TEXT    DEFAULT 'Pending',
    date_requested TEXT    DEFAULT (date('now')),
    date_released  TEXT,
    FOREIGN KEY (resident_id) REFERENCES residents(resident_id)
);
```

---

## 7. Entity Relationship Diagram

### ERD — Text Representation

```
┌─────────────────────────────┐
│           USERS             │
├─────────────────────────────┤
│ PK  user_id   INTEGER       │
│     username  TEXT (UNIQUE) │
│     password  TEXT          │
│     role      TEXT          │
└─────────────────────────────┘
         (standalone)

┌──────────────────────────────┐        ┌────────────────────────────────┐
│          RESIDENTS           │        │         CERTIFICATES           │
├──────────────────────────────┤        ├────────────────────────────────┤
│ PK  resident_id  INTEGER     │ 1    * │ PK  cert_id       INTEGER      │
│     first_name   TEXT        ├────────┤ FK  resident_id   INTEGER      │
│     last_name    TEXT        │        │     cert_type     TEXT         │
│     age          INTEGER     │        │     purpose       TEXT         │
│     gender       TEXT        │        │     status        TEXT         │
│     civil_status TEXT        │        │     date_requested TEXT        │
│     address      TEXT        │        │     date_released  TEXT        │
│     purok        TEXT        │        └────────────────────────────────┘
│     contact      TEXT        │
│     is_voter     INTEGER     │
│     is_indigent  INTEGER     │
│     is_senior    INTEGER     │
│     is_pwd       INTEGER     │
│     date_added   TEXT        │
└──────────────────────────────┘
```

> **Note to Docs Lead:** Recreate this ERD using draw.io or Lucidchart. Show PK with a key icon, FK with a connecting line labeled `1` on the RESIDENTS side and `*` on the CERTIFICATES side. Insert the image in your documentation.

---

## 8. System Flowchart

### 8.1 Main Flow

```
START
  │
  ▼
DatabaseHandler.initializeDatabase()
  (CREATE TABLE IF NOT EXISTS, INSERT OR IGNORE admin)
  │
  ▼
Display Login Screen (JOptionPane)
  │
  ▼
Collect username + password
  │
  ▼
Validate credentials against users table (PreparedStatement)
  │
  ├─ INVALID ──► Decrement attempts
  │                  │
  │              Attempts > 0? ──YES──► Back to Login
  │                  │
  │                  NO ──► System.exit(0)
  │
  └─ VALID ───► Display Welcome message
                    │
                    ▼
              Display Main Menu (Scanner input)
                    │
           ┌────────┼────────┐
          [1]      [2]     [3–6]     [0]
        Add      View    Modules   Exit
        Menu     Menu              │
          │        │               ▼
          │        │          scanner.close()
          │        │               │
          └────────┴──────────── END
                   ▲
                   │
              (return after each module)
```

### 8.2 Add Resident Sub-flow

```
Display Add sub-menu
  │
  ▼
Collect all fields via JOptionPane
  │
  ▼
Validate required fields (not null, not empty)
  │
  ▼
Validate age (numeric, range 0–150)
  │
  ▼
Auto-tag Senior if age >= 60
  │
  ▼
Create Resident object (parameterized constructor)
  │
  ▼
Execute INSERT PreparedStatement
  │
  ▼
Display success or error → return to Add sub-menu
```

### 8.3 Report Generation Sub-flow

```
Display Report sub-menu
  │
  ▼
User selects report type [1–3]
  │
  ▼
Execute aggregated SQL query (GROUP BY / CASE WHEN)
  │
  ▼
Store results in ArrayList<String[]>
  │
  ▼
Print formatted report (header + rows + totals + footer)
  │
  ▼
Report 3 only: calculate percentages + print key insights
  │
  ▼
Return to Report sub-menu
```

> **Note to Docs Lead:** Draw the flowchart using standard symbols — oval (START/END), rectangle (process), diamond (decision), parallelogram (input/output). Recommended tools: draw.io (free), Lucidchart, Microsoft Visio.

---

## 9. Source Code Explanation

### 9.1 DatabaseHandler.java

`DatabaseHandler` is a utility class that centralizes all database connectivity. The static constant `DB_URL = "jdbc:sqlite:barangay.db"` uses a relative path so the `.db` file is always created in the same directory as the running application — no configuration needed on any machine.

`getConnection()` declares `throws SQLException`, forcing every caller to handle database exceptions. `initializeDatabase()` uses **try-with-resources** to open `Connection` and `Statement` objects that are automatically closed on completion or exception — preventing connection leaks regardless of success or failure.

### 9.2 Main.java

The entry point follows a strict three-step launch sequence: **database initialization → login → main menu loop**. A single `Scanner` instance is created at the class level and passed to every module constructor — this prevents the known Java bug where multiple `Scanner` instances on `System.in` cause `NoSuchElementException`.

The menu loop uses `Integer.parseInt(scanner.nextLine().trim())`. `nextLine()` is used instead of `nextInt()` to consume the full line including the newline character, preventing buffer issues in subsequent module calls. `NumberFormatException` is caught at the loop level for graceful non-numeric input handling.

### 9.3 CRUD Modules (AddModule, UpdateModule, DeleteModule)

All data modification operations use **`PreparedStatement`** with positional `?` parameters instead of String concatenation — preventing SQL injection. Boolean status tags (`is_voter`, `is_indigent`, etc.) are stored as `INTEGER` and converted using the ternary operator: `r.isVoter() ? 1 : 0`.

`UpdateModule` implements a **non-destructive update pattern**: the existing record is fetched first, displayed to the user, and blank input retains the original value. `DeleteModule` deletes child certificate records before the parent resident record to comply with the foreign key constraint.

### 9.4 OOP Class Hierarchy

`Person`'s `abstract getRole()` enforces a contract at the **compiler level** — any concrete subclass that fails to implement it will not compile. The `protected` field visibility allows subclasses direct access without getter calls internally, while still blocking external class access.

`CertificateRequest.toString()` calls `getCertificateType()` and `generateDetails()` — both abstract — so the actual output is determined at **runtime** by whichever concrete subclass is instantiated. This is textbook runtime polymorphism in a working, production-style context.

### 9.5 Error Handling — Three Levels

| Level | Exception Caught | Where |
|-------|-----------------|-------|
| Database | `SQLException` | All modules via `try-with-resources` |
| Input parsing | `NumberFormatException` | All ID and menu input fields |
| Dialog cancel | `null` check on return value | All `JOptionPane` calls |

---

## 10. How to Run

### Prerequisites

- Java 17+ (OpenJDK Temurin recommended)
- No additional software required

### Project Structure

```
BarangaySystem/
├── .vscode/settings.json
├── lib/
│   ├── sqlite-jdbc-3.45.1.0.jar
│   ├── slf4j-api-2.0.9.jar
│   └── slf4j-simple-2.0.9.jar
├── src/          (16 .java source files)
├── out/          (compiled .class files)
├── barangay_schema.sql
└── project-overview.md
```

### Compile

```powershell
# Run from inside BarangaySystem/
javac -encoding UTF-8 -cp "lib/sqlite-jdbc-3.45.1.0.jar;lib/slf4j-api-2.0.9.jar;lib/slf4j-simple-2.0.9.jar" -d out src/*.java
```

> **Important:** `-encoding UTF-8` is required on Windows due to the box-drawing characters (╔, ║, ═) used in the menu display.

### Run

```powershell
java -cp "out;lib/sqlite-jdbc-3.45.1.0.jar;lib/slf4j-api-2.0.9.jar;lib/slf4j-simple-2.0.9.jar" Main
```

### Default Login Credentials

| Username | Password |
|----------|----------|
| `admin`  | `admin123` |

### First Run Output

```
[DB] Database initialized successfully.
→ JOptionPane login dialog appears
→ Welcome, admin! Login successful.

╔══════════════════════════════════════════╗
║     BARANGAY RECORD MANAGEMENT SYSTEM    ║
...
```

The file `barangay.db` is auto-created in the `BarangaySystem/` folder on first run.

---

## 11. Conclusion

The Barangay Record Management System successfully demonstrates the practical application of Object-Oriented Programming principles within a real-world domain that addresses a recognized public administration challenge in the Philippines. The system delivers all eight required modules — Login, Add, View, Search, Update, Delete, and Report Generation — connected to a three-table SQLite database through a clean, well-structured Java codebase.

All seven OOP concepts required by the course rubric are explicitly demonstrated:

- **Abstraction** — two abstract class hierarchies (`Person` and `CertificateRequest`)
- **Encapsulation** — private fields and public getter/setter methods in `Resident` and `Official`
- **Inheritance** — two multi-level inheritance chains
- **Polymorphism** — method overriding of `getRole()`, `getCertificateType()`, `generateDetails()`, and `toString()` across all concrete subclasses
- **Constructors** — default and parameterized constructors with `super()` chaining in all eight classes
- **Exception Handling** — `try-catch` and `try-with-resources` on all database operations and input parsing
- **Collections** — `ArrayList` usage in View, Search, and Report modules

The system addresses three core problems identified in the project rationale: slow certificate issuance is resolved by the digital certificate request and status tracking module; inaccurate resident data is addressed by the structured resident database with status tags; and offline reliability is ensured by SQLite, which operates without internet connectivity.

Every design decision — from abstract classes over interfaces, to `PreparedStatement` over raw SQL strings, to a single shared `Scanner` instance — was made deliberately to produce correct, robust, and professionally structured code.

---

*Barangay Record Management System | OOP Final Project | Polytechnic University of the Philippines | June 5, 2025*
