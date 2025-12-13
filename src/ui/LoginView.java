package ui;

import service.UserDAO;
import domain.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.Properties;
import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class LoginView extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JCheckBox rememberMeCheck;
    private JLabel forgotPassLabel;
    private JLabel registerLink;
    private UserDAO userDAO;

    // Properties for "Remember Me"
    private final String CONFIG_FILE = "config.properties";
    private final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final String LOG_FILE = "data/activity_log.csv";

    public LoginView() {
        userDAO = new UserDAO();

        setTitle("CRS Login");
        setSize(1000, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        add(mainPanel);

        // --- LEFT PANEL ---
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setLayout(new GridBagLayout());

        JLabel logoLabel = new JLabel();
        try {
            URL imgUrl = getClass().getResource("/resources/apulogo.png");
            if (imgUrl != null) {
                ImageIcon icon = new ImageIcon(imgUrl);
                Image img = icon.getImage();
                Image newImg = img.getScaledInstance(450, 450,  java.awt.Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(newImg));
            } else {
                logoLabel.setText("Logo Not Found");
            }
        } catch (Exception e) {
            logoLabel.setText("Error Loading Image");
        }
        leftPanel.add(logoLabel);

        // --- RIGHT PANEL ---
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        JLabel titleLabel = new JLabel("LOGIN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(new Color(0, 102, 204));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTitleLabel = new JLabel("Welcome to the CRS");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subTitleLabel.setForeground(Color.GRAY);
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Inputs Setup
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(350, 50));
        emailField.setMaximumSize(new Dimension(350, 50));
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        emailField.setBorder(new CompoundBorder(new LineBorder(new Color(200, 200, 200), 1), new EmptyBorder(10, 15, 10, 15)));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(350, 50));
        passwordField.setMaximumSize(new Dimension(350, 50));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setBorder(new CompoundBorder(new LineBorder(new Color(200, 200, 200), 1), new EmptyBorder(10, 15, 10, 15)));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Extras
        JPanel extrasPanel = new JPanel(new BorderLayout());
        extrasPanel.setBackground(Color.WHITE);
        extrasPanel.setMaximumSize(new Dimension(350, 30));
        extrasPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        rememberMeCheck = new JCheckBox("Remember me");
        rememberMeCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rememberMeCheck.setBackground(Color.WHITE);
        rememberMeCheck.setFocusPainted(false);

        forgotPassLabel = new JLabel("Forgot password?");
        forgotPassLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        forgotPassLabel.setForeground(new Color(0, 102, 204));
        forgotPassLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPassLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                handleForgotPassword();
            }
        });

        extrasPanel.add(rememberMeCheck, BorderLayout.WEST);
        extrasPanel.add(forgotPassLabel, BorderLayout.EAST);

        // --- CUSTOM ROUNDED BUTTONS ---

        // Login Button (Blue)
        loginButton = createRoundedButton("LOGIN", new Color(0, 102, 204), Color.WHITE);

        // Register Link (Standard Link Style WITHOUT Hover Animation)
        registerLink = new JLabel("Create new account");
        registerLink.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerLink.setForeground(new Color(0, 102, 204));
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.setAlignmentX(Component.CENTER_ALIGNMENT);

        registerLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                handleRegister();
            }
        });

        // Assembly
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(titleLabel);
        formPanel.add(subTitleLabel);
        formPanel.add(Box.createVerticalStrut(30));

        JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
        emailPanel.setBackground(Color.WHITE);
        emailPanel.add(emailLabel);
        emailPanel.add(Box.createVerticalStrut(8));
        emailPanel.add(emailField);
        emailPanel.setMaximumSize(new Dimension(350, 80));
        emailPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(emailPanel);

        formPanel.add(Box.createVerticalStrut(15));

        JPanel passPanel = new JPanel();
        passPanel.setLayout(new BoxLayout(passPanel, BoxLayout.Y_AXIS));
        passPanel.setBackground(Color.WHITE);
        passPanel.add(passLabel);
        passPanel.add(Box.createVerticalStrut(8));
        passPanel.add(passwordField);
        passPanel.setMaximumSize(new Dimension(350, 80));
        passPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(passPanel);

        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(extrasPanel);

        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(loginButton);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(registerLink);

        rightPanel.add(formPanel);
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        loadPreferences();

        loginButton.addActionListener(e -> handleLogin());
        getRootPane().setDefaultButton(loginButton);

        setVisible(true);
    }

    // --- Helper for Rounded Button with Shadow & Hover ---
    private JButton createRoundedButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int shadowGap = 5;
                int arcSize = 50; // Increased roundness
                int width = getWidth() - shadowGap;
                int height = getHeight() - shadowGap;

                // Draw Soft Shadow
                g2.setColor(new Color(200, 200, 200));
                g2.fillRoundRect(shadowGap, shadowGap, width, height, arcSize, arcSize);

                // Hover Effect: Shift button slightly
                if (getModel().isPressed()) {
                    g2.translate(2, 2);
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.translate(-1, -1); // Lift up effect
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }

                // Draw Button Body
                g2.fillRoundRect(0, 0, width, height, arcSize, arcSize);

                // Paint Text
                g2.setColor(fgColor);
                FontMetrics fm = g2.getFontMetrics();
                int textX = (width - fm.stringWidth(getText())) / 2;
                int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), textX, textY);

                g2.dispose();
            }
        };

        btn.setPreferredSize(new Dimension(350, 60)); // Taller for better roundness
        btn.setMaximumSize(new Dimension(350, 60));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        return btn;
    }

    private void handleLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both email and password.");
            return;
        }

        User user = userDAO.login(email, password);

        if (user != null) {
            // Track activity log
            trackUserActivity(user, "LOGIN");

            if (rememberMeCheck.isSelected()) {
                savePreferences(email);
            } else {
                savePreferences("");
            }

            // --- NEW: Format and Show Timestamp ---
            Date sessionStart = user.getLoginTimestamp();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timeStr = (sessionStart != null) ? fmt.format(sessionStart) : "Now";

            String welcomeMsg = "Login Successful!\n" +
                    "Role: " + user.getRole().getRoleName() + "\n" +
                    "Session Started: " + timeStr;

            JOptionPane.showMessageDialog(this, welcomeMsg);

            // Add Shutdown Hook to track logout
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                trackUserActivity(user, "LOGOUT");
            }));

            // --- OPEN DASHBOARD HERE ---
            dispose(); // Close LoginView
            new Dashboard(user).setVisible(true); // Open Dashboard with logged-in user

        } else {
            JOptionPane.showMessageDialog(this, "Invalid Email or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // NEW FUNCTION: Logs Login/Logout timestamps to a CSV file
    private void trackUserActivity(User user, String action) {
        String timestamp = TIMESTAMP_FORMAT.format(new Date());
        // Simple CSV format: Timestamp, UserID, Action
        String logEntry = String.format("%s,%s,%s", timestamp, user.getUserID(), action);

        File file = new File(LOG_FILE);
        boolean isNewFile = !file.exists();

        // Print to console for immediate verification
        System.out.println("Logging Activity: " + logEntry);

        // Append to file
        try (FileWriter fw = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            // Write header if new file
            if (isNewFile) {
                out.println("Timestamp,UserID,Action");
            }

            out.println(logEntry);
        } catch (IOException e) {
            System.err.println("Error logging activity: " + e.getMessage());
        }
    }

    private void handleForgotPassword() {
        String email = JOptionPane.showInputDialog(this, "Enter your registered email address:");
        if (email != null && !email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "If an account exists for " + email + ", a reset link has been sent.", "Reset Password", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleRegister() {
        dispose();
        new RegisterView().setVisible(true);
    }

    private void savePreferences(String email) {
        Properties prefs = new Properties();
        prefs.setProperty("last_email", email);
        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            prefs.store(out, "CRS User Preferences");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPreferences() {
        Properties prefs = new Properties();
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file)) {
                prefs.load(in);
                String savedEmail = prefs.getProperty("last_email", "");
                if (!savedEmail.isEmpty()) {
                    emailField.setText(savedEmail);
                    rememberMeCheck.setSelected(true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginView());
    }
}