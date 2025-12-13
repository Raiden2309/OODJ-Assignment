package ui;

import academic.CourseResult;
import domain.User;
import domain.Student;
import domain.AcademicOfficer;
import domain.CourseAdministrator;
import service.StudentDAO;
import service.AcademicRecordDAO;
import academic.Course;
// import service.MilestoneDAO;
import service.EnrollmentDAO; // Used for pending reviews/approvals
// import academic.RecoveryMilestone;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Dashboard extends JFrame {

    // Colors
    private final Color SIDEBAR_COLOR = new Color(33, 41, 54);
    private final Color SIDEBAR_HOVER_COLOR = new Color(55, 65, 81);
    private final Color TEXT_COLOR = new Color(236, 240, 241);
    private final Color ACCENT_COLOR = new Color(0, 102, 204);
    private final Color CONTENT_BG = new Color(245, 247, 250);
    private final Color CARD_BG = Color.WHITE;
    private final Color INELIGIBLE_COLOR = new Color(220, 53, 69);
    private final Color ELIGIBLE_COLOR = new Color(40, 167, 69);

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

        // --- 1. SIDEBAR ---
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(280, 800));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setLayout(new BorderLayout());

        // Header
        JPanel sidebarHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        sidebarHeader.setBackground(SIDEBAR_COLOR);


        JLabel appTitle = new JLabel("<html><center>CRS<br><span style='font-size:12px'>MANAGEMENT</span></center></html>");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        appTitle.setForeground(TEXT_COLOR);

        sidebarHeader.add(Box.createHorizontalStrut(5));
        sidebarHeader.add(appTitle);
        sidebar.add(sidebarHeader, BorderLayout.NORTH);

        // Menu Buttons
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(SIDEBAR_COLOR);
        menuPanel.setBorder(new EmptyBorder(20, 10, 20, 10));

        // Core Nav (using dummy icons since files are not guaranteed)
        JButton btnHome = createMenuButton("Dashboard", "home.png");

        JLabel lblCRP = new JLabel("  COURSE RECOVERY");
        lblCRP.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCRP.setForeground(Color.GRAY);
        lblCRP.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnCheck = createMenuButton("Check Eligibility", "check.png");
        JButton btnFailed = createMenuButton("Failed Components", "course.png");
        JButton btnRec = createMenuButton("Recommendations", "report.png");
        JButton btnSet = createMenuButton("Milestone Plans", "check.png");
        JButton btnMonitor = createMenuButton("Progress Monitor", "check.png");

        JLabel lblAccount = new JLabel("  ACCOUNT");
        lblAccount.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAccount.setForeground(Color.GRAY);
        lblAccount.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnAccount = createMenuButton("My Profile", "user.png");
        JButton btnLogout = createMenuButton("Logout", "logout.png");

        // Add to Menu
        menuPanel.add(btnHome);
        menuPanel.add(Box.createVerticalStrut(20));

        menuPanel.add(lblCRP);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(btnCheck);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnFailed);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnRec);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnSet);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnMonitor);

        // Staff Only: Management View
        if (currentUser instanceof AcademicOfficer || currentUser instanceof CourseAdministrator) {

            // FIX: Add Enrollment Approval Button
            JButton btnApproval = createMenuButton("Enrollment Approval", "review.png");
            btnApproval.addActionListener(e -> {
                setActiveButton(btnApproval);
                // PASSING THIS (the Dashboard instance) to the Approval View
                new EnrollmentApprovalView(currentUser, this).setVisible(true);
            });
            menuPanel.add(Box.createVerticalStrut(15));
            menuPanel.add(btnApproval);

            JButton btnManageUsers = createMenuButton("Manage Students", "user.png");
            btnManageUsers.addActionListener(e -> {
                setActiveButton(btnManageUsers);
                // Assume StudentManagementView exists in resources
                new StudentManagementView(currentUser).setVisible(true);
            });
            menuPanel.add(Box.createVerticalStrut(5));
            menuPanel.add(btnManageUsers);
        }

        menuPanel.add(Box.createVerticalStrut(30));

        menuPanel.add(lblAccount);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(btnAccount);
        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(btnLogout);

        sidebar.add(menuPanel, BorderLayout.CENTER);
        container.add(sidebar, BorderLayout.WEST);

        // --- 2. MAIN CONTENT ---
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

        // Content Area
        mainContentPanel = new JPanel(new CardLayout());
        mainContentPanel.setBackground(CONTENT_BG);

        // FIX: Display Staff Overview if user is Academic Officer or Course Admin
        if (currentUser instanceof AcademicOfficer || currentUser instanceof CourseAdministrator) {
            mainContentPanel.add(new JScrollPane(createStaffOverviewPanel()), "HOME");
        } else {
            mainContentPanel.add(new JScrollPane(createStudentOverviewPanel()), "HOME");
        }

        rightPanel.add(mainContentPanel, BorderLayout.CENTER);
        container.add(rightPanel, BorderLayout.CENTER);

        // --- ACTIONS ---
        btnHome.addActionListener(e -> {
            setActiveButton(btnHome);
            switchView("HOME", "Dashboard Overview", pageTitle);
        });

        // Assuming all navigation targets are now in 'resources' package
        btnCheck.addActionListener(e -> {
            setActiveButton(btnCheck);
            new CheckRecoveryEligibility(currentUser).setVisible(true);
        });

        btnFailed.addActionListener(e -> {
            setActiveButton(btnFailed);
            new FailedComponentOverview(currentUser).setVisible(true);
        });

        btnRec.addActionListener(e -> {
            setActiveButton(btnRec);
            new RecommendationEntry(currentUser).setVisible(true);
        });

        btnSet.addActionListener(e -> {
            setActiveButton(btnSet);
            new MilestoneActionPlan(currentUser).setVisible(true);
        });

        btnMonitor.addActionListener(e -> {
            setActiveButton(btnMonitor);
            new RecoveryProgress(currentUser).setVisible(true);
        });

        btnAccount.addActionListener(e -> {
            setActiveButton(btnAccount);
            new ManageAccountView(currentUser).setVisible(true);
        });

        btnLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, "Logout?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                // Assuming LoginView is in 'resources' or 'ui'
                new LoginView().setVisible(true);
            }
        });

        setActiveButton(btnHome);
    }

    // FIX: Public method to refresh dashboard content
    public void refreshStaffContent() {
        if (currentUser instanceof AcademicOfficer || currentUser instanceof CourseAdministrator) {
            // Remove old panel
            mainContentPanel.removeAll();
            // Add new panel
            mainContentPanel.add(new JScrollPane(createStaffOverviewPanel()), "HOME");
            // Validate and repaint
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
            CardLayout cl = (CardLayout) (mainContentPanel.getLayout());
            cl.show(mainContentPanel, "HOME"); // Ensure the home view is visible
        }
    }



    // ====================================================================
    // STAFF OVERVIEW PANEL IMPLEMENTATION
    // ====================================================================

    private JPanel createStaffOverviewPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CONTENT_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // --- DATA FETCHING & CALCULATION ---
        StudentDAO sDao = new StudentDAO();
        List<Student> allStudents = sDao.loadAllStudents();
        AcademicRecordDAO rDao = new AcademicRecordDAO();
        rDao.loadRecords(allStudents);

        // Assuming EnrollmentDAO loads Enrollments (needed for pending status)
        EnrollmentDAO eDao = new EnrollmentDAO();
        List<domain.Enrollment> allEnrollments = eDao.loadEnrollments();

        // Calculate Metrics
        final int totalStudents = allStudents.size();
        final long eligibleStudents = allStudents.stream().filter(s -> s.checkEligibility().isEligible()).count();

        final long atRiskStudents = allStudents.stream()
                .filter(s -> {
                    double cgpa = s.getAcademicProfile().getCGPA();
                    int failed = s.getAcademicProfile().getTotalFailedCourses();
                    // At Risk: CGPA below 2.5 (but above minimum 2.0) OR 1+ failures
                    return (cgpa < 2.5 && cgpa >= 2.0) || failed >= 1;
                }).count();

        // Assuming Enrollment class has a Status field
        final long pendingApprovals = allEnrollments.stream()
                .filter(e -> "Pending Approval".equalsIgnoreCase(e.getStatus()))
                .count();

        // Removed submitted milestones calculation
        // final long submittedMilestones = 0;


        // --- 1. OVERVIEW CARDS (Row 0) ---
        gbc.gridy = 0; gbc.gridwidth = 1; gbc.weighty = 0.0;

        JPanel card1 = createInfoCard("Total Students", String.valueOf(totalStudents), ACCENT_COLOR);
        gbc.gridx = 0; gbc.weightx = 0.33; panel.add(card1, gbc); // Adjusted weight

        JPanel card2 = createInfoCard("At-Risk Students", String.valueOf(atRiskStudents), INELIGIBLE_COLOR);
        gbc.gridx = 1; gbc.weightx = 0.33; panel.add(card2, gbc); // Adjusted weight

        JPanel card3 = createInfoCard("Pending Approvals", String.valueOf(pendingApprovals), Color.ORANGE);
        gbc.gridx = 2; gbc.gridwidth = 2; gbc.weightx = 0.34; panel.add(card3, gbc); // Adjusted to take 2/3 space

        // --- 2 & 3. RECENT ACTIVITY & ALERTS (Row 1) ---
        gbc.gridy = 1; gbc.gridwidth = 2; gbc.weightx = 0.5; gbc.weighty = 0.5;

        // Recent Activity Feed
        JPanel activityPanel = createWidgetPanel("Recent Activity Feed");
        JTextArea activityArea = new JTextArea();
        activityArea.setEditable(false);
        activityArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        activityArea.setText(generateActivityFeed(allStudents)); // Updated call signature
        activityPanel.add(new JScrollPane(activityArea), BorderLayout.CENTER);
        gbc.gridx = 0; panel.add(activityPanel, gbc);

        // Critical Alerts / Notifications
        JPanel alertPanel = createWidgetPanel("Critical Alerts / Probation");
        JTextArea alertArea = new JTextArea();
        alertArea.setEditable(false);
        alertArea.setFont(new Font("Monospaced", Font.BOLD, 12));
        alertArea.setForeground(INELIGIBLE_COLOR);
        alertArea.setText(generateAlerts(allStudents));
        alertPanel.add(new JScrollPane(alertArea), BorderLayout.CENTER);
        gbc.gridx = 2; panel.add(alertPanel, gbc);



        // --- 4 & 5. ANALYTICS / GENERIC STATUS (Row 3 - Now Row 2 due to removal) ---
        gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 0.5; gbc.weighty = 0.5;

        // Analytics Summary
        JPanel analyticsPanel = createWidgetPanel("Eligibility Analytics Summary");
        JTextArea analyticsArea = new JTextArea();
        analyticsArea.setEditable(false);
        analyticsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        analyticsArea.setText(generateAnalyticsSummary(allStudents, eligibleStudents, totalStudents));
        analyticsPanel.add(new JScrollPane(analyticsArea), BorderLayout.CENTER);
        gbc.gridx = 0; panel.add(analyticsPanel, gbc);

        // Upcoming Tasks removed - replaced with generic status
        JPanel statusPanel = createWidgetPanel("System Status");
        JTextArea statusArea = new JTextArea("System operating normally.\nData synchronization successful.");
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statusPanel.add(new JScrollPane(statusArea), BorderLayout.CENTER);
        gbc.gridx = 2; panel.add(statusPanel, gbc);


        return panel;
    }

    // ====================================================================
    // STAFF HELPER METHODS
    // ====================================================================

    // Removed milestones argument
    private String generateActivityFeed(List<Student> students) {
        StringBuilder sb = new StringBuilder();

        // Activity 1: New Enrollments (using pending approval count)
        long newEnrollments = 0;
        try {
            // Assume EnrollmentDAO has a loadEnrollments method
            newEnrollments = new EnrollmentDAO().loadEnrollments().stream()
                    .filter(e -> "Pending Approval".equalsIgnoreCase(e.getStatus()))
                    .count();
        } catch (Exception ex) {}

        if (newEnrollments > 0) {
            sb.append(String.format(">> %d new enrollment(s) requiring review.\n", newEnrollments));
        }

        // Activity 2: Recent Milestone Completions (REMOVED)
        // sb.append("\n--- MILESTONE COMPLETIONS ---\n");

        // Activity 3: New Failed Components (Simulation: Look for recent 'F' grades)
        sb.append("\n--- RECENT GRADE DROPS ---\n");
        students.stream()
                .flatMap(s -> s.getAcademicProfile().getCourseResults().stream())
                .filter(r -> r.getGrade().equalsIgnoreCase("F"))
                .limit(3)
                .forEach(r -> sb.append(String.format("! %s failed %s.\n",
                        r.getCourse().getCourseId(), r.getCourse().getName())));

        if (sb.length() < 30) {
            sb.append("No recent high-priority activity.\n");
        }
        return sb.toString();
    }

    private String generateAlerts(List<Student> students) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- URGENT ATTENTION REQUIRED ---\n");

        // Alert 1: Critical Failures (3+ Fs)
        students.stream()
                .filter(s -> s.getAcademicProfile().getTotalFailedCourses() >= 3)
                .limit(5)
                .forEach(s -> sb.append(String.format("! %s: CRITICAL FAILURE (%d Fs, CGPA %.2f)\n",
                        s.getUserID(), s.getAcademicProfile().getTotalFailedCourses(), s.getAcademicProfile().getCGPA())));

        // Alert 2: Approaching Probation (CGPA < 2.0)
        students.stream()
                .filter(s -> s.getAcademicProfile().getCGPA() < 2.0 && s.getAcademicProfile().getTotalFailedCourses() < 3)
                .limit(5)
                .forEach(s -> sb.append(String.format("! %s: PROBATION (CGPA %.2f)\n",
                        s.getUserID(), s.getAcademicProfile().getCGPA())));

        if (sb.length() < 30) {
            sb.append("No critical alerts currently.\n");
        }
        return sb.toString();
    }

    private String generateAnalyticsSummary(List<Student> students, long eligible, int total) {
        StringBuilder sb = new StringBuilder();
        long ineligible = total - eligible;

        sb.append("--- ELIGIBILITY BREAKDOWN ---\n");
        sb.append(String.format("Total Students: %d\n", total));
        sb.append(String.format("Eligible for Enrollment: %d (%.1f%%)\n", eligible, (double)eligible / total * 100));
        sb.append(String.format("Ineligible / Monitoring: %d (%.1f%%)\n", ineligible, (double)ineligible / total * 100));

        sb.append("\n--- FAILED COURSE ANALYSIS (TOP 3) ---\n");

        // Group by Course Name and count failures
        Map<String, Long> failedCourseCounts = students.stream()
                .flatMap(s -> s.getAcademicProfile().getCourseResults().stream())
                .filter(r -> r.getGrade().equalsIgnoreCase("F"))
                .collect(Collectors.groupingBy(r -> r.getCourse().getName(), Collectors.counting()));

        // Sort and limit to top 3
        failedCourseCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .forEach(entry -> sb.append(String.format("- %s: %d failures\n", entry.getKey(), entry.getValue())));

        if (failedCourseCounts.isEmpty()) {
            sb.append("No recorded failures across all students.\n");
        }

        return sb.toString();
    }


    // ====================================================================
    // STUDENT OVERVIEW PANEL
    // ====================================================================

    private JPanel createStudentOverviewPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CONTENT_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        if (!(currentUser instanceof Student)) return panel;
        Student s = (Student) currentUser;

        // Data Loading
        double cgpa = s.getAcademicProfile().getCGPA();
        int failed = s.getAcademicProfile().getTotalFailedCourses();

        // Calculate Passed, Failed, and In Progress (Enrolled) courses
        List<CourseResult> allResults = s.getAcademicProfile().getCourseResults();

        List<String> passedCourses = allResults.stream()
                .filter(r -> r.calculateGradePoint() >= 2.0)
                .map(r -> r.getCourse().getCourseId() + " (" + r.getGrade() + ")")
                .collect(Collectors.toList());

        List<String> failedCourses = allResults.stream()
                .filter(r -> r.getGrade().equalsIgnoreCase("F"))
                .map(r -> r.getCourse().getCourseId() + " (" + r.getGrade() + ")")
                .collect(Collectors.toList());

        long passedCount = passedCourses.size();

        // Use Enrollment records to determine 'In Progress' plans/courses
        long enrolledCourses = 0;
        try {
            enrolledCourses = new EnrollmentDAO().loadEnrollments().stream()
                    .filter(e -> s.getUserID().equalsIgnoreCase(e.getStudentId()))
                    .filter(e -> "Active".equalsIgnoreCase(e.getStatus()) || "Pending Approval".equalsIgnoreCase(e.getStatus()))
                    .count();
        } catch (Exception e) {
            System.err.println("Error fetching enrollments for student: " + e.getMessage());
        }
        long inProgressCount = enrolledCourses;

        // --- 1. Header (Welcome) ---
        JPanel welcomeCard = new JPanel(new BorderLayout());
        welcomeCard.setBackground(Color.WHITE);
        welcomeCard.setBorder(createCardBorder());

        JPanel titleBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        titleBox.setOpaque(false);
        titleBox.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel logoLabel = new JLabel();
        try {
            URL imgUrl = getClass().getResource("/resources/apulogo.png");
            if (imgUrl != null) {
                ImageIcon icon = new ImageIcon(imgUrl);
                // FIX 1: Enlarge logo to 100x100
                Image img = icon.getImage().getScaledInstance(150, 150,  java.awt.Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(img));
            }
        } catch (Exception e) {}

        JLabel welcomeLbl = new JLabel("Welcome back, " + s.getFullName());
        welcomeLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));

        // Place logo on the RIGHT side of the welcome banner
        titleBox.add(welcomeLbl);
        titleBox.add(Box.createHorizontalGlue()); // Push label left

        welcomeCard.add(titleBox, BorderLayout.WEST); // Title on Left
        welcomeCard.add(logoLabel, BorderLayout.EAST); // Logo on Right

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3; gbc.weighty = 0.0;
        panel.add(welcomeCard, gbc);

        // --- 2. Academic Standing Badge ---
        String standing = "Good Standing";
        Color standingColor = ELIGIBLE_COLOR;
        if (cgpa < 2.0) { standing = "Probation"; standingColor = Color.ORANGE; }
        if (failed >= 3) { standing = "At Risk"; standingColor = INELIGIBLE_COLOR; }

        JPanel standingCard = createInfoCard("Academic Standing", standing, standingColor);
        // FIX 2: Reduce top/bottom insets to 5 for Row 1 elements
        gbc.insets = new Insets(2, 10, 2, 10);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0.33;
        panel.add(standingCard, gbc);

        JPanel cgpaCard = createInfoCard("Current CGPA", String.format("%.2f", cgpa), ACCENT_COLOR);
        gbc.insets = new Insets(2, 10, 2, 10);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0.33;
        panel.add(cgpaCard, gbc);


        // --- 4. Eligibility Predictor ---
        String predictor = (cgpa < 2.0) ? "Needs +0.20 CGPA to clear Probation" : "On track for next semester.";
        if (failed > 0) predictor = "Retake failed courses to improve standing.";
        JPanel predictCard = createInfoCard("Eligibility Forecast", predictor, new Color(100, 100, 100));
        // FIX 2: Reduce top/bottom insets to 5 for Row 1 elements
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.gridx = 2; gbc.gridy = 1; // Row 1, Col 2
        panel.add(predictCard, gbc);

        // --- 5. Academic Record List (Replaced CGPA Trend) ---
        JPanel trendPanel = createWidgetPanel("Academic Record Overview");
        JTextArea trendArea = new JTextArea();
        trendArea.setEditable(false);
        trendArea.setFont(new Font("Monospaced", Font.PLAIN, 12));


        StringBuilder recordText = new StringBuilder();
        recordText.append("--- COURSE HISTORY ---\n");
        if (allResults.isEmpty()) {
            recordText.append("No academic results recorded.\n");
        } else {
            // Adjust formatting to ensure columns fit in the limited JTextArea width
            // FIX: Added Instructor column
            recordText.append(String.format("%-8s %-18s %-5s %-15s\n", "Code", "Name", "Grade", "Instructor"));
            recordText.append("----------------------------------------------------\n");

            // Sort results by grade (F first, then others)
            allResults.stream()
                    .sorted(Comparator.comparing(r -> r.getGrade().equalsIgnoreCase("F") ? 0 : 1))
                    .forEach(r -> {
                        // Truncate course name for display if necessary
                        String courseName = r.getCourse().getName();
                        if (courseName.length() > 18) { // Reduced from 20 for better fit
                            courseName = courseName.substring(0, 15) + "...";
                        }
                        // Get instructor
                        String instr = r.getCourse().getInstructor();
                        if (instr != null && instr.length() > 15) instr = instr.substring(0, 12) + "...";

                        recordText.append(String.format("%-8s %-18s %-5s %-15s\n",
                                r.getCourse().getCourseId(),
                                courseName,
                                r.getGrade(),
                                instr));
                    });
        }

        trendArea.setText(recordText.toString());

        trendPanel.add(new JScrollPane(trendArea), BorderLayout.CENTER);

        // Row 2, Col 0
        gbc.insets = new Insets(10, 10, 10, 10); // Restore standard insets for Row 2+
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.weighty = 0.5; gbc.weightx = 0.33;
        panel.add(trendPanel, gbc);

        // --- 3. Course Status Overview (Breakdown) ---
        JPanel courseStatusPanel = createWidgetPanel("Course Status (Breakdown)");
        JTextArea statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); // Increased font size

        StringBuilder statusText = new StringBuilder();
        statusText.append(String.format("Passed: %d courses\n", passedCount));
        statusText.append(String.format("Failed: %d courses\n", failed));
        statusText.append(String.format("Active Plans: %d\n", inProgressCount));

        statusArea.setText(statusText.toString());
        courseStatusPanel.add(new JScrollPane(statusArea), BorderLayout.CENTER);

        // Row 2, Col 1
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 1; gbc.weighty = 0.5; gbc.weightx = 0.33;
        panel.add(courseStatusPanel, gbc);

        // --- 6. Notifications ---
        JPanel notifPanel = createWidgetPanel("Notifications");
        JTextArea notifArea = new JTextArea();
        notifArea.setEditable(false);
        notifArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        String notifs = "• System: Welcome to CRS.\n";
        if (failed > 0) notifs += "• Alert: You have " + failed + " failed courses.\n";

        if (inProgressCount > 0) {
            notifs += String.format("• Action: You have %d active recovery plan(s).\n", inProgressCount);
        } else if (failed > 0) {
            notifs += "• Action: Enroll in a recovery plan now!\n";
        }

        notifArea.setText(notifs);
        notifPanel.add(new JScrollPane(notifArea), BorderLayout.CENTER);

        // This panel now takes 1 unit of space (Col 2)
        gbc.gridx = 2; gbc.gridy = 2; gbc.gridwidth = 1; gbc.weightx = 0.33;
        panel.add(notifPanel, gbc);

        // --- 7. Advisor & Quick Recommendations (Row 3, Col 0-2) ---
        JPanel advisorPanel = createWidgetPanel("Advisor & Quick Recommendations");
        JTextArea recArea = new JTextArea();
        recArea.setEditable(false);
        recArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        String advisorName = "Dr. Smith";
        // Logic to get real advisor from course (if available)
        Optional<Course> courseOpt = s.getAcademicProfile().getCourseResults().stream()
                .map(CourseResult::getCourse)
                .findFirst();

        if (courseOpt.isPresent()) {
            advisorName = courseOpt.get().getInstructor();
        }

        String recs = "Advisor: " + advisorName + " (staff@uni.edu)\n\n" +
                "Quick Tips:\n";
        if (cgpa < 2.5) recs += "- Book tutoring session.\n";
        if (failed > 0) recs += "- Review failed components.\n";
        recs += "- Check eligibility weekly.";

        recArea.setText(recs);
        advisorPanel.add(new JScrollPane(recArea), BorderLayout.CENTER);

        // Panel now spans full 3 columns in Row 3
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3; gbc.weightx = 1.0; gbc.weighty = 0.3;
        panel.add(advisorPanel, gbc);


        return panel;
    }

    // ====================================================================
    // UI CORE HELPER METHODS
    // ====================================================================

    private JPanel createWidgetPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(createCardBorder());

        JLabel lblTitle = new JLabel("  " + title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(ACCENT_COLOR);
        lblTitle.setBorder(new EmptyBorder(10, 10, 10, 10));
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(240, 248, 255));

        panel.add(lblTitle, BorderLayout.NORTH);
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

    private JButton createRoundedActionButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(220, 45));

        // Add rounding
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor, 1, true),
                new EmptyBorder(5, 15, 5, 15)
        ));

        return btn;
    }


    private javax.swing.border.CompoundBorder createCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(10, 10, 10, 10)
        );
    }

    // --- HELPER: Create Button with Icon ---
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
            URL iconUrl = getClass().getResource("/resources/icons/" + iconName);
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