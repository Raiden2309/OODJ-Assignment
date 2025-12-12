package resources;

import domain.User;
import resources.Dashboard; // IMPORT THE DASHBOARD CLASS
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.event.*;
import java.awt.*;

public class CRPHomePage extends JFrame {
    private JTextField lblCRP;
    private JButton btnMonitor;
    private JButton btnRec;
    private JButton btnSet;
    private JButton btnFailed;
    private JButton btnBack;
    private JPanel frmCRP;
    private User loggedInUser;

    // Constructor accepts User
    public CRPHomePage(User user) {
        this.loggedInUser = user;

        frmCRP = new JPanel() {
            private Image backgroundImage;
            {
                try {
                    backgroundImage = new ImageIcon(getClass().getResource("/resources/bg2.png")).getImage();
                } catch (Exception e) {
                    setBackground(Color.WHITE);
                }
                setOpaque(false);
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        setTitle("CRP Home Page");
        setContentPane(frmCRP);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        lblCRP = new JTextField("Course Recovery Plan");
        lblCRP.setEditable(false);
        lblCRP.setHorizontalAlignment(SwingConstants.CENTER);
        lblCRP.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblCRP.setBorder(null);
        lblCRP.setOpaque(false);
        lblCRP.setForeground(new Color(255, 255, 255));
        lblCRP.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(Box.createVerticalGlue());
        topPanel.add(lblCRP);
        topPanel.add(Box.createVerticalGlue());

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new GridBagLayout());

        btnFailed = new JButton("List Failed Components");
        btnRec = new JButton("Add/Update Recommendation");
        btnSet = new JButton("Set Milestone Action Plan");
        btnMonitor = new JButton("Monitor Student Progress");
        btnBack = new JButton("Back");

        styleButton(btnFailed);
        styleButton(btnRec);
        styleButton(btnSet);
        styleButton(btnMonitor);

        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setBackground(new Color(229, 93, 138));
        btnBack.setForeground(Color.WHITE);
        btnBack.setPreferredSize(new Dimension(100, 40));
        btnBack.setFocusPainted(false);
        btnBack.setBorder(new LineBorder(new Color(180, 70, 100), 2, true));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        centerPanel.add(btnFailed, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        centerPanel.add(btnRec, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        centerPanel.add(btnSet, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        centerPanel.add(btnMonitor, gbc);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 20));
        bottomPanel.setOpaque(false);
        bottomPanel.add(btnBack);

        frmCRP.setLayout(new BorderLayout());
        frmCRP.add(topPanel, BorderLayout.NORTH);
        frmCRP.add(centerPanel, BorderLayout.CENTER);
        frmCRP.add(bottomPanel, BorderLayout.SOUTH);

        btnBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnBack.setBackground(new Color(200, 80, 120));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnBack.setBackground(new Color(229, 93, 138));
            }
        });

        // --- BUTTON ACTIONS ---
        btnFailed.addActionListener(e -> {
            new FailedComponentOverview().setVisible(true);
            dispose();
        });

        btnRec.addActionListener(e -> {
            new RecommendationEntry(null).setVisible(true);
            dispose();
        });

        btnSet.addActionListener(e-> {
            new MilestoneActionPlan(null).setVisible(true);
            dispose();
        });

        btnMonitor.addActionListener(e-> {
            new RecoveryProgress(null).setVisible(true);
            dispose();
        });

        // FIX: Pass 'loggedInUser' to Dashboard constructor
        // This resolves "Expected 1 argument but found 0"
        btnBack.addActionListener(e-> {
            new Dashboard(loggedInUser).setVisible(true);
            dispose();
        });
    }

    private void styleButton(JButton button) {
        button.setBorder(BorderFactory.createLineBorder(new Color(200, 150, 100), 2, true));
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(300, 50));
        button.setBackground(new Color(229, 193, 205));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(200, 150, 100));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(229, 193, 205));
            }
        });
    }

    // Test Main (Optional)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CRPHomePage(null));
    }
}