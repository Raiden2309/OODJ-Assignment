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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class Dashboard extends JFrame {

    private JButton btnCheck;
    private JButton btnCRP;
    private JButton btnLogOut;
    private JButton btnGenerate;
    private JButton btnEdit;
    private JLabel lblDashboard;
    private JPanel frmDashboard;
    private UserDAO userDAO;
    private User loggedInUser;

    public Dashboard() {

        frmDashboard = new JPanel() {
            private Image backgroundImage;
            {
                try {
                    backgroundImage = new ImageIcon(getClass().getResource("/resources/bg1.png")).getImage();
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

        userDAO = new UserDAO();

        setTitle("CRS Dashboard");
        setContentPane(frmDashboard);
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/resources/apulogo.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(225, 225, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(scaledImage));
        lblLogo.setAlignmentX(CENTER_ALIGNMENT);
        lblLogo.setOpaque(false);

        JLabel lblDashboard = new JLabel("CRP Dashboard", SwingConstants.CENTER);
        lblDashboard.setFont(new Font("Segoe UI", Font.BOLD, 50));
        lblDashboard.setForeground(new Color(0, 102, 204));
        lblDashboard.setAlignmentX(CENTER_ALIGNMENT);
        lblDashboard.setOpaque(true);
        lblDashboard.setBackground(new Color(255, 255, 255, 200));

        topPanel.add(lblLogo);
        topPanel.add(Box.createVerticalStrut(20));
        topPanel.add(lblDashboard);

        add(topPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 30, 30));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 150, 50, 150));
        buttonPanel.setOpaque(false);

        btnCheck = new JButton("Check Recovery Eligibility");
        btnCRP = new JButton("Course Recovery Plan");
        btnGenerate = new JButton("Generate Academic Performance Report");
        btnEdit = new JButton("Edit Profile");

        btnCheck.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCRP.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnGenerate.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 16));

        btnCheck.setPreferredSize(new Dimension(400, 60));
        btnCheck.setMaximumSize(new Dimension(400, 60));
        btnCheck.setBackground(new Color(0, 102, 204));
        btnCheck.setForeground(Color.WHITE);

        btnCRP.setPreferredSize(new Dimension(400, 60));
        btnCRP.setMaximumSize(new Dimension(400, 60));
        btnCRP.setBackground(new Color(0, 102, 204));
        btnCRP.setForeground(Color.WHITE);

        btnGenerate.setPreferredSize(new Dimension(400, 60));
        btnGenerate.setMaximumSize(new Dimension(400, 60));
        btnGenerate.setBackground(new Color(0, 102, 204));
        btnGenerate.setForeground(Color.WHITE);

        btnEdit.setPreferredSize(new Dimension(400, 60));
        btnEdit.setMaximumSize(new Dimension(400, 60));
        btnEdit.setBackground(new Color(0, 102, 204));
        btnEdit.setForeground(Color.WHITE);

        styleButton(btnCheck, "/resources/checklogo.png");
        styleButton(btnCRP, "/resources/courselogo.png");
        styleButton(btnGenerate, "/resources/generatelogo.png");
        styleButton(btnEdit, "/resources/profilelogo.png");

        buttonPanel.add(btnCheck);
        buttonPanel.add(btnCRP);
        buttonPanel.add(btnGenerate);
        buttonPanel.add(btnEdit);

        add(buttonPanel, BorderLayout.CENTER);

        btnLogOut = new JButton("Log Out");
        btnLogOut.setFont(new Font("Arial", Font.BOLD, 14));
        JPanel backPanel = new JPanel();
        backPanel.setBackground(Color.WHITE);
        btnLogOut.setPreferredSize(new Dimension(140, 38));
        btnLogOut.setBackground(Color.RED);
        backPanel.add(btnLogOut);
        add(backPanel, BorderLayout.SOUTH);

        btnLogOut.setBorder(new LineBorder(new Color(150, 0, 0), 2, true));
        btnLogOut.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogOut.setBackground(new Color(150, 0, 0));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnLogOut.setBackground(Color.RED);
            }
        });


        btnCheck.addActionListener(e -> {
            new CheckRecoveryEligibility().setVisible(true);
            dispose();
        });

        btnCRP.addActionListener(e -> {
            new CRPHomePage().setVisible(true);
            dispose();
        });

        btnGenerate.addActionListener(e-> {
            new GenerateAPReport().setVisible(true);
            dispose();
        });

        btnEdit.addActionListener(e-> {
            /*new EditProfile(null).setVisible(true);
            dispose();*/
        });

        btnLogOut.addActionListener(e-> {
            new LoginView().setVisible(true);
            dispose();
        });

    }

    private void styleButton(JButton button, String iconPath) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(400, 60));
        button.setMaximumSize(new Dimension(400, 60));
        button.setBackground(new Color(0, 102, 204));
        button.setForeground(Color.WHITE);
        button.setBorder(new LineBorder(new Color(0, 80, 150), 2, true));
        button.setFocusPainted(false);
        button.setBorderPainted(true);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image scaledIcon = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaledIcon));
            button.setHorizontalTextPosition(SwingConstants.RIGHT);
        } catch (Exception e) {

        }

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 80, 150));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 102, 204));
            }
        });
    }

    public static void main(String[] args) {
        new Dashboard().setVisible(true);
    }

}
