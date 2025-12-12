package resources;

import domain.User;
import domain.Student;
import domain.AcademicOfficer;
import domain.CourseAdministrator;
import service.StudentDAO;
import service.AcademicRecordDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.net.URL;
import java.util.List;

// Import your other views (Assume they are in 'resources' package or adjust imports)
import resources.FailedComponentOverview;
import resources.RecommendationEntry;
import resources.MilestoneActionPlan;
import resources.RecoveryProgress;
import resources.ManageAccountView; // Keep if ManageAccountView is still in 'ui'
import resources.LoginView;         // Keep if LoginView is still in 'ui'
import resources.CheckRecoveryEligibility; // Import if needed

public class Dashboard extends JFrame {

    private final Color SIDEBAR_COLOR = new Color(33, 41, 54);
    private final Color SIDEBAR_HOVER_COLOR = new Color(55, 65, 81);
    private final Color TEXT_COLOR = new Color(236, 240, 241);
    private final Color ACCENT_COLOR = new Color(0, 102, 204);
    private final Color CONTENT_BG = new Color(245, 247, 250);

    private JPanel mainContentPanel;
    private User currentUser;
    private JButton currentActiveBtn;

    public Dashboard(User user) {
        this.currentUser = user;
        if (currentUser instanceof Student) {
            reloadStudentData();
        }

        setTitle("CRS Dashboard - " + user.getRole().getRoleName());
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel container = new JPanel(new BorderLayout());
        add(container);

        // --- SIDEBAR ---
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(280, 800));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setLayout(new BorderLayout());

        // Header
        JPanel sidebarHeader = new JPanel();
        sidebarHeader.setLayout(new BoxLayout(sidebarHeader, BoxLayout.Y_AXIS));
        sidebarHeader.setBackground(SIDEBAR_COLOR);
        sidebarHeader.setBorder(new EmptyBorder(40, 20, 40, 20));

        JLabel appTitle = new JLabel("<html><center>CRS MANAGEMENT<br><span style='font-size:10px; color:#bdc3c7'>SYSTEM</span></center></html>");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appTitle.setForeground(TEXT_COLOR);

        sidebarHeader.add(appTitle);
        sidebar.add(sidebarHeader, BorderLayout.NORTH);

        // Menu Buttons
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(SIDEBAR_COLOR);
        menuPanel.setBorder(new EmptyBorder(10, 10, 20, 10));

        JButton btnHome = createMenuButton("Dashboard", "home.png");
        menuPanel.add(btnHome);
        menuPanel.add(Box.createVerticalStrut(15));

        // --- ROLE BASED SIDEBAR CONTENT ---
        String role = currentUser.getRole().getRoleName();

        JLabel lblCRP = new JLabel("  COURSE RECOVERY");
        lblCRP.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblCRP.setForeground(new Color(149, 165, 166));
        lblCRP.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblCRP.setBorder(new EmptyBorder(10, 5, 5, 0));
        menuPanel.add(lblCRP);

        // Define Buttons
        JButton btnCheck = createMenuButton("Check Eligibility", "check.png");
        JButton btnFailed = createMenuButton("Failed Components", "course.png");
        JButton btnRec = createMenuButton("Recommendations", "report.png");
        JButton btnSet = createMenuButton("Milestone Plans", "check.png");
        JButton btnMonitor = createMenuButton("Progress Monitor", "check.png");

        // Logic: Who sees what?
        if ("Student".equalsIgnoreCase(role)) {
            // Students see their own data
            menuPanel.add(btnCheck); // Check My Eligibility
            menuPanel.add(Box.createVerticalStrut(5));
            menuPanel.add(btnFailed); // My Failed Components
            menuPanel.add(Box.createVerticalStrut(5));
            menuPanel.add(btnRec);    // My Recommendations
            menuPanel.add(Box.createVerticalStrut(5));
            menuPanel.add(btnSet);    // My Plans
            menuPanel.add(Box.createVerticalStrut(5));
            menuPanel.add(btnMonitor);// My Progress
        }
        else if ("AcademicOfficer".equalsIgnoreCase(role) || "CourseAdministrator".equalsIgnoreCase(role)) {
            // Officers/Admins see management tools
            // (You might want to rename buttons here for clarity, e.g. "Manage Recommendations")
            menuPanel.add(btnCheck); // Check ANY Student
            menuPanel.add(Box.createVerticalStrut(5));
            menuPanel.add(btnFailed);
            menuPanel.add(Box.createVerticalStrut(5));
            menuPanel.add(btnRec);
            menuPanel.add(Box.createVerticalStrut(5));
            menuPanel.add(btnSet);
            menuPanel.add(Box.createVerticalStrut(5));
            menuPanel.add(btnMonitor);
        }

        menuPanel.add(Box.createVerticalStrut(25));

        JLabel lblAccount = new JLabel("  ACCOUNT");
        lblAccount.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblAccount.setForeground(new Color(149, 165, 166));
        lblAccount.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblAccount.setBorder(new EmptyBorder(10, 5, 5, 0));
        menuPanel.add(lblAccount);

        JButton btnAccount = createMenuButton("My Profile", "user.png");
        menuPanel.add(btnAccount);

        menuPanel.add(Box.createVerticalGlue());

        JButton btnLogout = createMenuButton("Logout", "logout.png");
        menuPanel.add(btnLogout);

        sidebar.add(menuPanel, BorderLayout.CENTER);
        container.add(sidebar, BorderLayout.WEST);

        // --- CONTENT AREA ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(CONTENT_BG);

        // Top Bar
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(Color.WHITE);
        topHeader.setPreferredSize(new Dimension(100, 60));
        topHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        JLabel pageTitle = new JLabel("  Dashboard Overview");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        pageTitle.setForeground(new Color(50, 50, 50));

        String timeStr = (user.getLoginTimestamp() != null)
                ? new SimpleDateFormat("MMM dd, HH:mm").format(user.getLoginTimestamp())
                : "Now";
        JLabel sessionLabel = new JLabel("Session started: " + timeStr + "   ");
        sessionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sessionLabel.setForeground(Color.GRAY);

        topHeader.add(pageTitle, BorderLayout.WEST);
        topHeader.add(sessionLabel, BorderLayout.EAST);
        rightPanel.add(topHeader, BorderLayout.NORTH);

        mainContentPanel = new JPanel(new CardLayout());
        mainContentPanel.setBackground(CONTENT_BG);
        mainContentPanel.add(createOverviewPanel(), "HOME");

        rightPanel.add(mainContentPanel, BorderLayout.CENTER);
        container.add(rightPanel, BorderLayout.CENTER);

        // --- ACTIONS ---
        btnHome.addActionListener(e -> {
            setActiveButton(btnHome);
            switchView("HOME", "Dashboard Overview", pageTitle);
        });

        btnCheck.addActionListener(e -> {
            setActiveButton(btnCheck);
            new resources.CheckRecoveryEligibility().setVisible(true);
        });

        btnFailed.addActionListener(e -> {
            setActiveButton(btnFailed);
            new FailedComponentOverview(currentUser).setVisible(true);
            dispose();
        });

        btnRec.addActionListener(e -> {
            setActiveButton(btnRec);
            new RecommendationEntry(currentUser).setVisible(true);
            dispose();
        });

        btnSet.addActionListener(e -> {
            setActiveButton(btnSet);
            new MilestoneActionPlan(currentUser).setVisible(true);
            dispose();
        });

        btnMonitor.addActionListener(e -> {
            setActiveButton(btnMonitor);
            new RecoveryProgress(currentUser).setVisible(true);
            dispose();
        });

        btnAccount.addActionListener(e -> {
            setActiveButton(btnAccount);
            new ManageAccountView(currentUser).setVisible(true);
        });

        btnLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, "Logout?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginView().setVisible(true);
            }
        });

        setActiveButton(btnHome);
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CONTENT_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;

        JPanel welcomeCard = new JPanel(new BorderLayout());
        welcomeCard.setBackground(Color.WHITE);
        welcomeCard.setBorder(createCardBorder());

        String displayName = currentUser.getUserID();
        if (currentUser instanceof Student) {
            displayName = ((Student) currentUser).getFullName();
        } else if (currentUser instanceof AcademicOfficer) {
            displayName = ((AcademicOfficer) currentUser).getFullName();
        } else if (currentUser instanceof CourseAdministrator) {
            displayName = ((CourseAdministrator) currentUser).getFullName();
        }

        JLabel welcomeLbl = new JLabel("  Welcome back, " + displayName);
        welcomeLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLbl.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Logo logic (kept from previous step)
        JLabel logoLabel = new JLabel();
        logoLabel.setBorder(new EmptyBorder(10, 10, 10, 30));
        try {
            URL logoUrl = getClass().getResource("apulogo.png");
            if (logoUrl != null) {
                ImageIcon icon = new ImageIcon(logoUrl);
                Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(img));
            }
        } catch (Exception e) {}

        welcomeCard.add(welcomeLbl, BorderLayout.CENTER);
        welcomeCard.add(logoLabel, BorderLayout.EAST);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3; gbc.weightx = 1.0; gbc.weighty = 0.1;
        panel.add(welcomeCard, gbc);

        // Only show Student Stats if user is a Student
        if (currentUser instanceof Student) {
            Student s = (Student) currentUser;
            double cgpa = s.getAcademicProfile().getCGPA();
            JPanel cgpaCard = createInfoCard("Current CGPA", String.format("%.2f", cgpa), new Color(0, 150, 136));

            int failed = s.getAcademicProfile().getTotalFailedCourses();
            JPanel failCard = createInfoCard("Failed Courses", String.valueOf(failed), new Color(220, 53, 69));

            String status = s.getRecoveryEligibility();
            Color statusColor = "Eligible".equalsIgnoreCase(status) ? new Color(40, 167, 69) : Color.GRAY;
            JPanel statusCard = createInfoCard("Recovery Status", status, statusColor);

            gbc.gridy = 1; gbc.gridwidth = 1; gbc.weighty = 0.2;

            gbc.gridx = 0; panel.add(cgpaCard, gbc);
            gbc.gridx = 1; panel.add(failCard, gbc);
            gbc.gridx = 2; panel.add(statusCard, gbc);
        }

        return panel;
    }

    private JPanel createInfoCard(String title, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, accent),
                createCardBorder().getInsideBorder()
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(Color.GRAY);
        lblTitle.setBorder(new EmptyBorder(15, 15, 5, 15));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValue.setForeground(Color.DARK_GRAY);
        lblValue.setBorder(new EmptyBorder(0, 15, 15, 15));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    private javax.swing.border.CompoundBorder createCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(10, 10, 10, 10)
        );
    }

    private JButton createMenuButton(String text, String iconName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(new Color(180, 180, 180));
        btn.setBackground(SIDEBAR_COLOR);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        try {
            URL iconUrl = getClass().getResource("icons/" + iconName);
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
                btn.setIconTextGap(15);
            }
        } catch (Exception e) {}

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != currentActiveBtn) {
                    btn.setBackground(SIDEBAR_HOVER_COLOR);
                    btn.setForeground(Color.WHITE);
                }
            }
            public void mouseExited(MouseEvent e) {
                if (btn != currentActiveBtn) {
                    btn.setBackground(SIDEBAR_COLOR);
                    btn.setForeground(new Color(180, 180, 180));
                }
            }
        });
        return btn;
    }

    private void setActiveButton(JButton btn) {
        if (currentActiveBtn != null) {
            currentActiveBtn.setBackground(SIDEBAR_COLOR);
            currentActiveBtn.setForeground(new Color(180, 180, 180));
            currentActiveBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        }
        currentActiveBtn = btn;
        currentActiveBtn.setBackground(SIDEBAR_HOVER_COLOR);
        currentActiveBtn.setForeground(Color.WHITE);
        currentActiveBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, ACCENT_COLOR),
                new EmptyBorder(10, 16, 10, 20)
        ));
    }

    private void switchView(String cardName, String title, JLabel titleLabel) {
        CardLayout cl = (CardLayout) (mainContentPanel.getLayout());
        cl.show(mainContentPanel, cardName);
        titleLabel.setText("  " + title);
    }

    private void reloadStudentData() {
        StudentDAO sDao = new StudentDAO();
        AcademicRecordDAO rDao = new AcademicRecordDAO();
        List<Student> students = sDao.loadAllStudents();
        rDao.loadRecords(students);

        for (Student s : students) {
            if (s.getUserID().equals(currentUser.getUserID())) {
                this.currentUser = s;
                s.checkEligibility();
                break;
            }
        }
    }
}