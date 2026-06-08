import gui.MainFrame;
import db.DatabaseHandler;

import javax.swing.SwingUtilities;

/**
 * Main.java
 *
 * Entry point for the upgraded Barangay Record Management System.
 * Transitions the application from a console-based interface to a modern Swing GUI.
 *
 * Execution Flow:
 *   1. Initialize database (creates tables + default admin if missing)
 *   2. Launch the MainFrame window on the Event Dispatch Thread
 *
 * OOP Concepts Demonstrated:
 *   - Program entry encapsulation
 *   - Threading safety using SwingUtilities.invokeLater
 *   - Exception Handling on startup database calls
 */
public class Main {

    public static void main(String[] args) {
        // ── STEP 1: Initialize Database ───────────────────────────────────
        // Creates all 3 tables if they don't exist.
        // Inserts default admin account.
        // If this fails, System.exit(1) is called inside initializeDatabase()
        DatabaseHandler.initializeDatabase();

        // ── STEP 2: Launch Swing UI ──────────────────────────────────────
        // Starts the root JFrame shell on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}

