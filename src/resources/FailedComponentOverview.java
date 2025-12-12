package resources;

import javax.swing.*;
import academic.EnrolledCourse;
import domain.User;
import service.EnrolledCourseDAO;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FailedComponentOverview extends JFrame {
    private JTable tblFailed;
    private JButton btnBack;
    private JScrollPane pnlFailed;
    private JPanel frmFailed;
    private JButton btnMilestone;
    private JButton btnRecovery;
    private JButton btnRec;
    private JLabel lblFailed;
    private JPanel panelMain;
    private EnrolledCourseDAO enrolledCoursesDAO;
    private User loggedInUser;

    // Constructor accepts User
    public FailedComponentOverview(User user) {
        this.loggedInUser = user;

        frmFailed = new JPanel() {
            private Image backgroundImage;
            {
                try {
                    backgroundImage = new ImageIcon(getClass().getResource("/resources/bg3.png")).getImage();
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

        try {
            frmFailed.setLayout(new BorderLayout());
            setContentPane(frmFailed);
            setTitle("Failed Component Overview");
            setSize(1000, 700);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            tblFailed = new JTable();
            pnlFailed = new JScrollPane(tblFailed);
            btnMilestone = new JButton("Milestone");
            btnRec = new JButton("Recommendation Entry");
            btnRecovery = new JButton("Recovery Progress");
            btnBack = new JButton("Back");
            btnBack.setBackground(new Color(229, 93, 138));
            frmFailed.add(pnlFailed, BorderLayout.CENTER);
            frmFailed.add(btnBack, BorderLayout.SOUTH);

            frmFailed.setBackground(new Color(229,215,139));
            pnlFailed.getViewport().setBackground(new Color(229,215,139));
            btnBack.setFont(new Font("Arial", Font.BOLD, 14));

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
            buttonPanel.setBackground(new Color(229, 205, 103));

            Dimension size = new Dimension(200, 40);
            btnMilestone.setMaximumSize(size);
            btnRec.setMaximumSize(size);
            btnRecovery.setMaximumSize(size);

            buttonPanel.add(btnMilestone);
            buttonPanel.add(Box.createVerticalStrut(100));
            buttonPanel.add(btnRec);
            buttonPanel.add(Box.createVerticalStrut(100));
            buttonPanel.add(btnRecovery);

            lblFailed = new JLabel("FAILED COMPONENTS");
            lblFailed.setFont(new Font("Comic Sans MS", Font.BOLD, 35));
            lblFailed.setForeground(Color.BLACK);
            lblFailed.setHorizontalAlignment(SwingConstants.CENTER);
            lblFailed.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/resources/apulogo.png"));
            Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            ImageIcon logoIcon = new ImageIcon(scaledImage);
            JLabel lblLogo = new JLabel(logoIcon);

            JPanel logoTitlePanel = new JPanel(new BorderLayout());
            logoTitlePanel.setOpaque(false);
            logoTitlePanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            logoTitlePanel.add(lblLogo, BorderLayout.WEST);
            logoTitlePanel.add(lblFailed, BorderLayout.CENTER);

            frmFailed.add(buttonPanel, BorderLayout.WEST);
            frmFailed.add(pnlFailed, BorderLayout.CENTER);
            frmFailed.add(btnBack, BorderLayout.SOUTH);
            frmFailed.add(logoTitlePanel, BorderLayout.NORTH);

            pnlFailed.setOpaque(false);
            pnlFailed.getViewport().setOpaque(false);

            loadFailedComponents();

            btnBack.addActionListener(e -> {
                new CRPHomePage(loggedInUser).setVisible(true);
                dispose();
            });

            btnMilestone.addActionListener(e -> {
                new MilestoneActionPlan(loggedInUser).setVisible(true);
                dispose();
            });

            btnRec.addActionListener(e -> {
                new RecommendationEntry(loggedInUser).setVisible(true);
                dispose();
            });

            btnRecovery.addActionListener(e -> {
                new RecoveryProgress(loggedInUser).setVisible(true);
                dispose();
            });
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error initializing GUI: " + e.getMessage());
        }
    }

    // Default constructor for compatibility if needed (but prefer passing user)
    public FailedComponentOverview() {
        this(null);
    }

    private void loadFailedComponents() {
        try {
            EnrolledCourseDAO dao = new EnrolledCourseDAO();
            List<EnrolledCourse> enrolledCourses = dao.loadAllEnrolledCourses();
            System.out.println("Loaded " + enrolledCourses.size() + " failed components.");
            String[] columns = {"StudentID", "CourseID", "ExamScore", "AssignmentScore", "ExamWeight", "AssignmentWeight", "FailedComponent"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            for (EnrolledCourse ec : enrolledCourses) {
                if (!ec.getFailedComponent().equals("None")) {
                    Object[] row = {
                            ec.getStudentID(),
                            ec.getCourseID(),
                            ec.getExamScore(),
                            ec.getAssignmentScore(),
                            ec.getExamWeight(),
                            ec.getAssignmentWeight(),
                            ec.getFailedComponent()
                    };
                    model.addRow(row);
                }
            }
            tblFailed.setModel(model);
            System.out.println("Table model set successfully.");
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading failed components: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FailedComponentOverview(null).setVisible(true);
        });
    }
}