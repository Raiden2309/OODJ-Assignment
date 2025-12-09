package resources;

import domain.User;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class CRPHomePage extends JFrame{
    private JTextField lblCRP;
    private JButton btnMonitor;
    private JButton btnRec;
    private JButton btnSet;
    private JButton btnFailed;
    private JButton btnBack;
    private JPanel frmCRP;
    private User loggedInUser;

    public CRPHomePage() {
        setTitle("CRP Home Page");
        setContentPane(frmCRP);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(229, 215, 139));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/resources/apulogo.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(225, 225, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(scaledImage));
        lblLogo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("CRP Home Page", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 50));
        lblTitle.setForeground(new Color(74, 112, 229));
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);

        topPanel.add(lblLogo);
        topPanel.add(Box.createVerticalStrut(20));
        topPanel.add(lblTitle);

        add(topPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 150, 50, 150));
        buttonPanel.setBackground(new Color(229, 215, 139));

        btnFailed = new JButton("List Failed Components");
        btnRec = new JButton("Recommendation Entry");
        btnSet = new JButton("Set Milestone Action Plan");
        btnMonitor = new JButton("Monitor Recovery Progress");

        btnFailed.setFont(new Font("Arial", Font.BOLD, 16));
        btnRec.setFont(new Font("Arial", Font.BOLD, 16));
        btnSet.setFont(new Font("Arial", Font.BOLD, 16));
        btnMonitor.setFont(new Font("Arial", Font.BOLD, 16));

        buttonPanel.add(btnFailed);
        buttonPanel.add(btnRec);
        buttonPanel.add(btnSet);
        buttonPanel.add(btnMonitor);

        add(buttonPanel, BorderLayout.CENTER);

        btnBack = new JButton("Back");
        btnBack.setFont(new Font("Arial", Font.BOLD, 14));
        JPanel backPanel = new JPanel();
        backPanel.setBackground(new Color(229, 215, 139));
        btnBack.setPreferredSize(new Dimension(150, 50));
        btnBack.setBackground(new Color(229, 93, 138));
        backPanel.add(btnBack);
        add(backPanel, BorderLayout.SOUTH);

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

    }

    public static void main(String[] args) {
        new CRPHomePage().setVisible(true);
    }
}