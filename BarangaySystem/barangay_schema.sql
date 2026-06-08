-- ============================================================
-- Barangay Record Management System
-- Database: SQLite
-- File: barangay.db (auto-created by DatabaseHandler)
-- ============================================================

-- TABLE 1: users
-- Stores login credentials for barangay staff.
-- Standalone table — no foreign key dependencies.
CREATE TABLE IF NOT EXISTS users (
    user_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    username  TEXT NOT NULL UNIQUE,
    password  TEXT NOT NULL,
    role      TEXT NOT NULL DEFAULT 'staff'
);

-- Default admin account
-- INSERT OR IGNORE prevents duplicate on repeated runs
INSERT OR IGNORE INTO users (username, password, role)
VALUES ('admin', 'admin123', 'admin');

-- TABLE 2: residents
-- Master resident database.
-- Status tags (is_indigent, is_senior, is_pwd, is_voter)
-- stored as INTEGER: 1 = true, 0 = false
-- SQLite has no native BOOLEAN type.
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
-- Tracks all certificate requests linked to residents.
-- resident_id FK ensures no orphan certificate records.
-- status pipeline: Pending → Approved → Released
CREATE TABLE IF NOT EXISTS certificates (
    cert_id        INTEGER PRIMARY KEY AUTOINCREMENT,
    resident_id    INTEGER NOT NULL,
    cert_type      TEXT    NOT NULL,
    purpose        TEXT    NOT NULL,
    status         TEXT    DEFAULT 'Pending',
    date_requested TEXT    DEFAULT (date('now')),
    date_released  TEXT,
    FOREIGN KEY (resident_id)
        REFERENCES residents(resident_id)
);
