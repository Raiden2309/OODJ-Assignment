package resources;

import service.UserDAO;
import domain.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class LoginView extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private UserDAO userDAO;

    public LoginView() {
        // Initialize DAO
        userDAO = new UserDAO();

        // Window Setup
        setTitle("CRS Login");
        setSize(1000, 600); // Slightly larger default window
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Container (Split Layout)
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        add(mainPanel);

        // --- LEFT PANEL (Image Side) ---
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setLayout(new GridBagLayout());

        JLabel logoLabel = new JLabel();
        try {
            URL imgUrl = getClass().getResource("apulogo.png");
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

        // --- RIGHT PANEL (Form Side) ---
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new GridBagLayout());

        // Form Container
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        // Title
        JLabel titleLabel = new JLabel("LOGIN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(new Color(0, 102, 204));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Email Label
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align to the left

        // Email Input
        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(350, 50));
        emailField.setMaximumSize(new Dimension(350, 50));
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        emailField.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(10, 15, 10, 15)));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT); // Align to the left

        // Password Label
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align to the left

        // Password Input
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(350, 50));
        passwordField.setMaximumSize(new Dimension(350, 50));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(10, 15, 10, 15)));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT); // Align to the left

        // Login Button
        loginButton = new JButton("LOGIN");
        loginButton.setPreferredSize(new Dimension(350, 50));
        loginButton.setMaximumSize(new Dimension(350, 50));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBackground(new Color(0, 102, 204));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Keep button centered

        // Spacing Helpers
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(40));

        // Wrap input fields and labels in panels for left alignment within the centered form
        JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
        emailPanel.setBackground(Color.WHITE);
        emailPanel.add(emailLabel);
        emailPanel.add(Box.createVerticalStrut(8));
        emailPanel.add(emailField);
        // Ensure emailPanel doesn't stretch excessively horizontally but fills width
        emailPanel.setMaximumSize(new Dimension(350, 80));
        emailPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the panel itself

        formPanel.add(emailPanel);

        formPanel.add(Box.createVerticalStrut(20));

        JPanel passPanel = new JPanel();
        passPanel.setLayout(new BoxLayout(passPanel, BoxLayout.Y_AXIS));
        passPanel.setBackground(Color.WHITE);
        passPanel.add(passLabel);
        passPanel.add(Box.createVerticalStrut(8));
        passPanel.add(passwordField);
        passPanel.setMaximumSize(new Dimension(350, 80));
        passPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        formPanel.add(passPanel);

        formPanel.add(Box.createVerticalStrut(40));
        formPanel.add(loginButton);

        // Add Form to Right Panel
        rightPanel.add(formPanel);

        // Add Panels to Main Frame
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        // --- Logic ---
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        getRootPane().setDefaultButton(loginButton);

        setVisible(true);
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
            JOptionPane.showMessageDialog(this, "Login Successful!\nRole: " + user.getRole().getRoleName());
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Email or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginView());
    }
}