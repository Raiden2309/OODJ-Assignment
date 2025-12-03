package resources;

import javax.swing.*;
import java.awt.event.*;

public class CRPHomePage extends JFrame{
    private JTextField lblCRP;
    private JButton btnMonitor;
    private JButton btnRec;
    private JButton btnSet;
    private JButton btnFailed;
    private JButton btnBack;
    private JPanel frmCRP;

    public CRPHomePage() {
        setTitle("CRP Home Page");
        setContentPane(frmCRP);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        btnFailed.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                FailedComponentOverview page = new FailedComponentOverview();
                page.setVisible(true);
                dispose();
            }
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
