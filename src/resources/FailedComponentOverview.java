package resources;

import domain.User;
import javax.swing.*;
import java.awt.*;

public class FailedComponentOverview extends JFrame {
    private JButton btnBack;
    private User loggedInUser;

    // Constructor accepts User
    public FailedComponentOverview(User user) {
        this.loggedInUser = user;

        setTitle("Failed Components Overview");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- ADD YOUR TABLE/CONTENT HERE ---
        JLabel placeholder = new JLabel("Failed Components List (Placeholder)", SwingConstants.CENTER);
        placeholder.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(placeholder, BorderLayout.CENTER);

        // Back Button Logic
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnBack = new JButton("Back");
        btnBack.addActionListener(e -> {
            // FIX: Pass loggedInUser back to CRPHomePage
            new Dashboard(loggedInUser).setVisible(true);
            dispose();
        });
        bottomPanel.add(btnBack);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Default constructor for compatibility if needed (but prefer passing user)
    public FailedComponentOverview() {
        this(null);
    }
}