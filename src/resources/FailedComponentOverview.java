package resources;

import javax.swing.*;
import academic.FailedComponent;
import service.FailedComponentDAO;
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

    public FailedComponentOverview() {

        try {
            frmFailed = new JPanel(new BorderLayout());
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

            lblFailed = new JLabel("Failed Components");
            lblFailed.setFont(new Font("Arial", Font.BOLD, 18));
            lblFailed.setForeground(Color.BLACK);
            lblFailed.setHorizontalAlignment(SwingConstants.CENTER);
            lblFailed.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/resources/apulogo.png"));
            Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            ImageIcon logoIcon = new ImageIcon(scaledImage);
            JLabel lblLogo = new JLabel(logoIcon);

            JPanel logoTitlePanel = new JPanel(new BorderLayout());
            logoTitlePanel.setBackground(new Color(229, 215, 139));
            logoTitlePanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            logoTitlePanel.add(lblLogo, BorderLayout.WEST);
            logoTitlePanel.add(lblFailed, BorderLayout.CENTER);

            frmFailed.add(buttonPanel, BorderLayout.WEST);
            frmFailed.add(pnlFailed, BorderLayout.CENTER);
            frmFailed.add(btnBack, BorderLayout.SOUTH);
            frmFailed.add(logoTitlePanel, BorderLayout.NORTH);

            loadFailedComponents();

            btnBack.addActionListener(e -> {
                new CRPHomePage().setVisible(true);
                dispose();
            });

            btnMilestone.addActionListener(e -> {
                new MilestoneActionPlan(null).setVisible(true);
                dispose();

            });

            btnRec.addActionListener(e -> {
                new RecommendationEntry(null).setVisible(true);
                dispose();

            });

            btnRecovery.addActionListener(e -> {
                new RecoveryProgress(null).setVisible(true);
                dispose();

            });
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error initializing GUI: " + e.getMessage());
        }
    }

    private void loadFailedComponents() {
        try {
            FailedComponentDAO dao = new FailedComponentDAO();
            List<FailedComponent> failedComponents = dao.loadAllFailedComponents();
            System.out.println("Loaded " + failedComponents.size() + " failed components.");
            String[] columns = {"StudentID", "CourseID", "ExamScore", "AssignmentScore", "ExamWeight", "AssignmentWeight", "FailedComponent"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            for (FailedComponent fc : failedComponents) {
                Object[] row = {
                        fc.getStudentID(),
                        fc.getCourseID(),
                        fc.getExamScore(),
                        fc.getAssignmentScore(),
                        fc.getExamWeight(),
                        fc.getAssignmentWeight(),
                        fc.getFailedComponent()
                };
                model.addRow(row);
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
            new FailedComponentOverview().setVisible(true);
        });
    }
}



