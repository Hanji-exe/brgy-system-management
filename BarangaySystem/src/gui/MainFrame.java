package gui;

import model.*;
import db.DatabaseHandler;

import javax.swing.*;
import java.awt.*;

/**
 * MainFrame.java
 *
 * The root JFrame of the Barangay Record Management System.
 * Acts as the application shell — holds all panels and switches
 * between them using CardLayout.
 *
 * OOP Concept: Encapsulation — panel switching logic is
 * contained here. No other class needs to know how panels
 * are swapped, only that they can call showPanel().
 */
public class MainFrame extends JFrame {

    // ── CONSTANTS ────────────────────────────────────────────────────────
    public static final Color COLOR_PRIMARY        = new Color(0x1A, 0x3A, 0x6B);
    public static final Color COLOR_SECONDARY      = new Color(0x2E, 0x6D, 0xB4);
    public static final Color COLOR_ACCENT         = new Color(0xF5, 0xA6, 0x23);
    public static final Color COLOR_BACKGROUND     = new Color(0xF4, 0xF6, 0xF9);
    public static final Color COLOR_CARD           = new Color(0xFF, 0xFF, 0xFF);
    public static final Color COLOR_TEXT_PRIMARY   = new Color(0x2D, 0x2D, 0x2D);
    public static final Color COLOR_TEXT_SECONDARY = new Color(0x88, 0x88, 0x88);
    public static final Color COLOR_TEXT_LIGHT     = new Color(0xFF, 0xFF, 0xFF);
    public static final Color COLOR_SUCCESS        = new Color(0x27, 0xAE, 0x60);
    public static final Color COLOR_WARNING        = new Color(0xF3, 0x9C, 0x12);
    public static final Color COLOR_DANGER         = new Color(0xE7, 0x4C, 0x3C);
    public static final Color COLOR_TABLE_ALT      = new Color(0xF0, 0xF4, 0xFA);
    public static final Color COLOR_BORDER         = new Color(0xDC, 0xE3, 0xED);

    // ── PANEL NAME CONSTANTS ─────────────────────────────────────────────
    public static final String PANEL_LOGIN      = "LOGIN";
    public static final String PANEL_DASHBOARD  = "DASHBOARD";
    public static final String PANEL_ADD_RES    = "ADD_RESIDENT";
    public static final String PANEL_ADD_CERT   = "ADD_CERT";
    public static final String PANEL_VIEW       = "VIEW";
    public static final String PANEL_SEARCH     = "SEARCH";
    public static final String PANEL_UPDATE     = "UPDATE";
    public static final String PANEL_DELETE     = "DELETE";
    public static final String PANEL_REPORT     = "REPORT";

    // ── FIELDS ───────────────────────────────────────────────────────────
    private CardLayout   cardLayout;
    private JPanel       cardContainer;
    private String       loggedInUser = "";

    // ── CONSTRUCTOR ──────────────────────────────────────────────────────
    public MainFrame() {
        initFrame();
        initCards();
    }

    // ── FRAME SETUP ──────────────────────────────────────────────────────

    /**
     * Configures the root JFrame properties.
     * Sets title, size, close behavior, and look and feel.
     */
    private void initFrame() {
        setTitle("Barangay Record Management System");
        setSize(1200, 720);
        setMinimumSize(new Dimension(1024, 640));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(COLOR_BACKGROUND);

        // Use system look and feel as base
        try {
            UIManager.setLookAndFeel(
                UIManager.getCrossPlatformLookAndFeelClassName()
            );
        } catch (Exception e) {
            // Fall back to default if system L&F unavailable
            System.out.println("[UI] Using default look and feel.");
        }
    }

    /**
     * Initializes CardLayout and registers all panels.
     * CardLayout allows switching between panels by name
     * without destroying and recreating them.
     *
     * Each panel is instantiated once and kept in memory
     * for the duration of the session.
     */
    private void initCards() {
        cardLayout    = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setBackground(COLOR_BACKGROUND);

        // Register all panels with their name constants
        // Order does not matter — CardLayout shows by name
        cardContainer.add(new LoginPanel(this),     PANEL_LOGIN);
        cardContainer.add(new DashboardPanel(this), PANEL_DASHBOARD);
        cardContainer.add(new ResidentFormPanel(this), PANEL_ADD_RES);
        cardContainer.add(new CertificateFormPanel(this), PANEL_ADD_CERT);
        cardContainer.add(new ViewPanel(this),      PANEL_VIEW);
        cardContainer.add(new SearchPanel(this),    PANEL_SEARCH);
        cardContainer.add(new UpdatePanel(this),    PANEL_UPDATE);
        cardContainer.add(new DeletePanel(this),    PANEL_DELETE);
        cardContainer.add(new ReportPanel(this),    PANEL_REPORT);

        add(cardContainer);

        // Start on login screen
        cardLayout.show(cardContainer, PANEL_LOGIN);
    }

    // ── PUBLIC API ───────────────────────────────────────────────────────

    /**
     * Switches the visible panel by name.
     * Called by every panel's navigation buttons.
     *
     * Example:
     *   frame.showPanel(MainFrame.PANEL_DASHBOARD);
     */
    public void showPanel(String panelName) {
        cardLayout.show(cardContainer, panelName);

        // Refresh dashboard stats every time it is shown
        if (panelName.equals(PANEL_DASHBOARD)) {
            refreshDashboard();
        }
    }

    /**
     * Stores the logged-in username for display across panels.
     * Called by LoginPanel after successful authentication.
     */
    public void setLoggedInUser(String username) {
        this.loggedInUser = username;
    }

    /**
     * Returns the currently logged-in username.
     * Used by panels that display the current user.
     */
    public String getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Triggers a data refresh on the DashboardPanel.
     * Called every time the dashboard is shown so stats
     * always reflect the latest database state.
     */
    private void refreshDashboard() {
        for (Component c : cardContainer.getComponents()) {
            if (c instanceof DashboardPanel) {
                ((DashboardPanel) c).refreshStats();
                break;
            }
        }
    }

    /**
     * Logs out the current user and returns to login screen.
     * Clears the stored username before switching panels.
     */
    public void logout() {
        this.loggedInUser = "";
        cardLayout.show(cardContainer, PANEL_LOGIN);
    }
}
