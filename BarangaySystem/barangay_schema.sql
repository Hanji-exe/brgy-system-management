-- ============================================================
-- Barangay Record Management System
-- Database: MySQL
-- File: barangay_schema.sql
-- Purpose: MySQL-compatible schema for the barangay system
-- ============================================================

-- TABLE 1: users
-- Stores login credentials for barangay staff.
-- Standalone table — no foreign key dependencies.
CREATE TABLE IF NOT EXISTS users (
    user_id   INT NOT NULL AUTO_INCREMENT,
    username  VARCHAR(100) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,
    role      VARCHAR(50) NOT NULL DEFAULT 'staff',
    PRIMARY KEY (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Default admin account
-- INSERT IGNORE prevents duplicate on repeated runs
INSERT IGNORE INTO users (username, password, role)
VALUES ('admin', 'admin123', 'admin');

-- TABLE 2: residents
-- Master resident database.
-- Status tags (is_indigent, is_senior, is_pwd, is_voter)
-- stored as TINYINT(1): 1 = true, 0 = false
-- MySQL supports boolean-style flags through tiny integer columns.
CREATE TABLE IF NOT EXISTS residents (
    resident_id  INT NOT NULL AUTO_INCREMENT,
    first_name   VARCHAR(100) NOT NULL,
    last_name    VARCHAR(100) NOT NULL,
    age          INT NOT NULL,
    gender       VARCHAR(20) NOT NULL,
    civil_status VARCHAR(50) NOT NULL,
    address      VARCHAR(255) NOT NULL,
    purok        VARCHAR(100) NOT NULL,
    contact      VARCHAR(50),
    is_voter     TINYINT(1) NOT NULL DEFAULT 0,
    is_indigent  TINYINT(1) NOT NULL DEFAULT 0,
    is_senior    TINYINT(1) NOT NULL DEFAULT 0,
    is_pwd       TINYINT(1) NOT NULL DEFAULT 0,
    date_added   DATE DEFAULT (CURRENT_DATE),
    PRIMARY KEY (resident_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- TABLE 3: certificates
-- Tracks all certificate requests linked to residents.
-- resident_id FK ensures no orphan certificate records.
-- status pipeline: Pending → Approved → Released
CREATE TABLE IF NOT EXISTS certificates (
    cert_id        INT NOT NULL AUTO_INCREMENT,
    resident_id    INT NOT NULL,
    cert_type      VARCHAR(100) NOT NULL,
    purpose        VARCHAR(255) NOT NULL,
    status         VARCHAR(20) DEFAULT 'Pending',
    date_requested DATE DEFAULT (CURRENT_DATE),
    date_released  DATE,
    PRIMARY KEY (cert_id),
    FOREIGN KEY (resident_id)
        REFERENCES residents(resident_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
