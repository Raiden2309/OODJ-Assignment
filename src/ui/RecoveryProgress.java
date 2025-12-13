package ui;

import academic.RecoveryResult;
import academic.Course;
import domain.User;
import domain.Student;
import service.RecoveryDAO;
import service.CourseCatalog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class RecoveryProgress extends JFrame {
    private JTable tblAttempt;
    private JTextField txtAttemptID;
    private JComboBox<String>  cmbStatus;
    private JLabel lblAttemptID;
    private JLabel lblStatus;
    private JButton btnSave;
    private JButton btnBack;

    private JLabel statusMessageLabel;

    private DefaultTableModel tableModel;
    private RecoveryDAO recoveryDAO;
    private User loggedInUser;

    private JPanel inputPanel;

    private final Color ACCENT_COLOR = new Color(0, 102, 204);
    private final Color RED_COLOR = new Color(220, 53, 69);
    private final Color TEXT_COLOR = Color.WHITE;
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 12);

    public RecoveryProgress(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.recoveryDAO = new RecoveryDAO();

        JPanel backgroundPanel = new JPanel() {
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
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        setTitle("Recovery Progress Monitor");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        ImageIcon logoIcon = null;
        try {
            Image logoImage = new ImageIcon(getClass().getResource("/resources/apulogo.png")).getImage();
            Image scaledLogo = logoImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(scaledLogo);
        } catch (Exception e) {
            System.out.println("Logo image not found at /resources/apulogo.png");
        }

        JLabel logoLabel = new JLabel(logoIcon);
        headerPanel.add(logoLabel);

        JLabel titleLabel = new JLabel("RECOVERY PROGRESS");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 35));
        titleLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(titleLabel);

        backgroundPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel centerContainer = new JPanel(new BorderLayout(10, 10));
        centerContainer.setOpaque(false);
        centerContainer.setBorder(new EmptyBorder(10, 20, 10, 20));

        inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        lblAttemptID = new JLabel("Attempt ID:");
        lblAttemptID.setFont(LABEL_FONT);
        inputPanel.add(lblAttemptID, gbc);

        gbc.gridx = 1;
        txtAttemptID = new JTextField(15);
        inputPanel.add(txtAttemptID, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        lblStatus = new JLabel("Update Status:");
        lblStatus.setFont(LABEL_FONT);
        inputPanel.add(lblStatus, gbc);

        gbc.gridx = 1;
        cmbStatus = new JComboBox<>(new String[]{"Not Started", "In Progress", "Completed", "Passed", "Failed - Retry", "Failed - No Attempts Left"});
        inputPanel.add(cmbStatus, gbc);

        centerContainer.add(inputPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new CardLayout());
        tablePanel.setOpaque(false);

        tableModel = new DefaultTableModel(new String[] {
                "AttemptID", "StudentID", "CourseID", "FailedComponent", "ExamScore", "AssignmentScore", "Status"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tblAttempt = new JTable(tableModel);
        tblAttempt.setRowHeight(25);
        tblAttempt.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane tableScroll = new JScrollPane(tblAttempt);
        tablePanel.add(tableScroll, "TABLE");

        statusMessageLabel = new JLabel("No recovery attempts found.", SwingConstants.CENTER);
        statusMessageLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statusMessageLabel.setForeground(Color.GRAY);
        tablePanel.add(statusMessageLabel, "EMPTY");

        centerContainer.add(tablePanel, BorderLayout.CENTER);
        backgroundPanel.add(centerContainer, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setOpaque(false);

        btnSave = createRoundedButton("Save Changes", ACCENT_COLOR, TEXT_COLOR);
        btnBack = createRoundedButton("Back to Dashboard", RED_COLOR, TEXT_COLOR);

        buttonPanel.add(btnBack);
        buttonPanel.add(btnSave);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        tblAttempt.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tblAttempt.getSelectedRow();
                if (selectedRow != -1) {
                    if (!(loggedInUser instanceof Student)) {
                        txtAttemptID.setText((String) tableModel.getValueAt(selectedRow, 0));
                    }
                }
            }
        });

        btnSave.addActionListener(e -> saveStatusUpdate());

        btnBack.addActionListener(e -> {
            dispose();
        });

        if (loggedInUser instanceof Student) {
            setupStudentView();
            if (tableModel.getRowCount() == 0) {
                ((CardLayout) tablePanel.getLayout()).show(tablePanel, "EMPTY");
            }
        } else {
            loadRecoveryProgress(null);
            ((CardLayout) tablePanel.getLayout()).show(tablePanel, "TABLE");
        }

        setVisible(true);
    }

    private JButton createRoundedButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int shadowGap = 3;
                int arcSize = 30;
                int width = getWidth() - shadowGap;
                int height = getHeight() - shadowGap;
                g2.setColor(new Color(200, 200, 200));
                g2.fillRoundRect(shadowGap, shadowGap, width, height, arcSize, arcSize);
                if (getModel().isPressed()) {
                    g2.translate(1, 1);
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, width, height, arcSize, arcSize);
                g2.setColor(fgColor);
                FontMetrics fm = g2.getFontMetrics();
                int textX = (width - fm.stringWidth(getText())) / 2;
                int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), textX, textY);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(160, 45));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void setupStudentView() {
        inputPanel.setVisible(false);
        btnSave.setVisible(false);
        loadRecoveryProgress(loggedInUser.getUserID());
    }

    private void loadRecoveryProgress(String studentIDFilter) {
        tableModel.setRowCount(0);
        List<RecoveryResult> results = recoveryDAO.loadRecoveryResults();

        if (studentIDFilter != null) {
            results = results.stream()
                    .filter(r -> r.getStudentID().equalsIgnoreCase(studentIDFilter))
                    .collect(Collectors.toList());
        }

        for (RecoveryResult result : results) {
            tableModel.addRow(new Object[]{
                    result.getAttemptID(),
                    result.getStudentID(),
                    result.getCourseID(),
                    result.getFailedComponent(),
                    result.getExamScore(),
                    result.getAssignmentScore(),
                    result.getRecoveryStatus()
            });
        }
    }

    private void saveStatusUpdate() {
        if (loggedInUser instanceof Student) return;

        String enteredAttemptID = txtAttemptID.getText().trim();
        if (enteredAttemptID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Error: Please enter an AttemptID.");
            return;
        }

        int rowIndex = -1;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (enteredAttemptID.equals(tableModel.getValueAt(i, 0))) {
                rowIndex = i;
                break;
            }
        }

        if (rowIndex == -1) {
            JOptionPane.showMessageDialog(this, "Error: AttemptID does not exist.");
            return;
        }

        int confirmSave = JOptionPane.showConfirmDialog(this, "Save changes?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
        if (confirmSave != JOptionPane.OK_OPTION) return;

        String newStatus = (String) cmbStatus.getSelectedItem();
        String studentID = (String) tableModel.getValueAt(rowIndex, 1);
        String courseID = (String) tableModel.getValueAt(rowIndex, 2);
        String failedComponent = (String) tableModel.getValueAt(rowIndex, 3);

        int examScore = 0, assignmentScore = 0;
        try {
            examScore = Integer.parseInt(tableModel.getValueAt(rowIndex, 4).toString());
            assignmentScore = Integer.parseInt(tableModel.getValueAt(rowIndex, 5).toString());
        } catch (Exception e) { }

        Course course = CourseCatalog.getInstance().getCourse(courseID);
        RecoveryResult updatedResult = new RecoveryResult(
                enteredAttemptID, studentID, courseID, course, failedComponent,
                examScore, assignmentScore, newStatus
        );

        recoveryDAO.updateRecoveryResult(studentID, courseID, enteredAttemptID, updatedResult);
        tableModel.setValueAt(newStatus, rowIndex, 6);
        JOptionPane.showMessageDialog(this, "Status updated successfully!");

        loadRecoveryProgress(null);
        txtAttemptID.setText("");
        cmbStatus.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RecoveryProgress(null).setVisible(true);
        });
    }
}