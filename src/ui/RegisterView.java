package ui;

import service.UserDAO;
import service.StudentDAO;
import domain.Student;
import domain.SystemRole;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.ArrayList;

public class RegisterView extends JFrame {

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JLabel loginLink;

    private UserDAO userDAO;
    private StudentDAO studentDAO;

    public RegisterView() {
        userDAO = new UserDAO();
        studentDAO = new StudentDAO();

        setTitle("CRS Registration");
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
                Image newImg = img.getScaledInstance(450, 450, java.awt.Image.SCALE_SMOOTH);
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

        JLabel titleLabel = new JLabel("CREATE ACCOUNT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(new Color(0, 102, 204));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTitleLabel = new JLabel("Join the Course Recovery System");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subTitleLabel.setForeground(Color.GRAY);
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Name Row
        JPanel nameRow = new JPanel(new GridLayout(1, 2, 10, 0));
        nameRow.setBackground(Color.WHITE);
        nameRow.setMaximumSize(new Dimension(350, 80)); // Matching width of single inputs

        firstNameField = createStyledTextField();
        lastNameField = createStyledTextField();

        // Wrap name inputs to align left within their grid cells
        nameRow.add(createInputBlock("First Name", firstNameField));
        nameRow.add(createInputBlock("Last Name", lastNameField));
        nameRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Inputs
        emailField = createStyledTextField();
        JPanel emailBlock = createInputBlock("Email Address", emailField);

        passwordField = createStyledPasswordField();
        JPanel passBlock = createInputBlock("Password (Min 6 chars)", passwordField);

        confirmPasswordField = createStyledPasswordField();
        JPanel confirmPassBlock = createInputBlock("Confirm Password", confirmPasswordField);

        // --- CUSTOM ROUNDED BUTTON ---
        registerButton = createRoundedButton("SIGN UP", new Color(0, 102, 204), Color.WHITE);

        // Login Link (No Hover Animation)
        loginLink = new JLabel("Have an account? Log in here!");
        loginLink.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginLink.setForeground(new Color(0, 102, 204));
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginView().setVisible(true);
            }
        });

        // Assembly - Adjusted spacing for cleaner look with more fields
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(titleLabel);
        formPanel.add(subTitleLabel);
        formPanel.add(Box.createVerticalStrut(20));

        formPanel.add(nameRow);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(emailBlock);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(passBlock);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(confirmPassBlock);

        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(registerButton);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(loginLink);

        rightPanel.add(formPanel);
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        registerButton.addActionListener(e -> handleRegister());
        getRootPane().setDefaultButton(registerButton);

        setVisible(true);
    }

    // --- Helpers ---

    private JButton createRoundedButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int shadowGap = 5;
                int arcSize = 50;
                int width = getWidth() - shadowGap;
                int height = getHeight() - shadowGap;

                g2.setColor(new Color(200, 200, 200));
                g2.fillRoundRect(shadowGap, shadowGap, width, height, arcSize, arcSize);

                if (getModel().isPressed()) {
                    g2.translate(2, 2);
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.translate(-1, -1);
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }

                g2.fillRoundRect(0, 0, width, height, arcSize, arcSize);

                g2.setColor(fgColor);
                FontMetrics fm = g2.getFontMetrics();
                int textX = (width - fm.stringWidth(getText())) / 2;
                int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), textX, textY);

                g2.dispose();
            }
        };

        btn.setPreferredSize(new Dimension(350, 60));
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

    private JPanel createInputBlock(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);

        if (panel.getMaximumSize().width > 350) {
            panel.setMaximumSize(new Dimension(350, 80));
        }
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return panel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(100, 40));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 10, 5, 10)));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(100, 40));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 10, 5, 10)));
        return field;
    }

    private void handleRegister() {
        String first = firstNameField.getText().trim();
        String last = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        if (first.isEmpty() || last.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (pass.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Security Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String newId = "S" + (System.currentTimeMillis() % 100000);
        SystemRole studentRole = new SystemRole("Student", new ArrayList<>());

        Student newStudent = new Student(newId, pass, studentRole, first, last, "General", "Year 1", email);
        newStudent.setActive(false);

        if (userDAO.saveUserCredentials(newStudent)) {
            if (studentDAO.saveStudent(newStudent)) {
                String msg = "Account Request Submitted!\n" +
                        "Your ID is: " + newId + "\n" +
                        "Please wait for an Administrator to activate your account.";
                JOptionPane.showMessageDialog(this, msg, "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new LoginView().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Error saving student details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error saving credentials.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterView());
    }
}