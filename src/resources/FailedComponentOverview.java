package resources;

import javax.swing.*;
import academic.FailedComponent;
import service.FailedComponentDAO;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FailedComponentOverview extends JFrame {

    private JTextField txtFailed;
    private JTable tblFailed;
    private JButton btnBack;
    private JScrollPane pnlFailed;
    private JPanel frmFailed;
    private JPanel panelMain;

    public FailedComponentOverview() {

        try {
            frmFailed = new JPanel(new BorderLayout());
            setContentPane(frmFailed);
            setTitle("Failed Component Overview");
            setSize(900, 600);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            tblFailed = new JTable();
            pnlFailed = new JScrollPane(tblFailed);
            btnBack = new JButton("Back");
            frmFailed.add(pnlFailed, BorderLayout.CENTER);
            frmFailed.add(btnBack, BorderLayout.SOUTH);

            frmFailed.setBackground(new Color(229,215,139));
            pnlFailed.getViewport().setBackground(new Color(229,215,139));
            btnBack.setFont(new Font("Arial", Font.BOLD, 14));

            loadFailedComponents();

            btnBack.addActionListener(e -> {
                new CRPHomePage().setVisible(true);
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



