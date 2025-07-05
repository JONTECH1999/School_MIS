package student;

import config.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Dashboard extends JFrame {

    private JPanel mainPanel;
    private String username;

    public Dashboard(String username) {
    this.username = username;

    setTitle("STI Student Dashboard");
    setSize(1280, 800);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // Color scheme
    Color stiBlue = Color.decode("#002060");
    Color stiYellow = Color.decode("#FFD700");
    Color backgroundColor = Color.decode("#E6F0FF");
    Color cardColor = Color.WHITE;
    Color textColor = new Color(0x333333);

    // Sidebar
    JPanel sidebar = new JPanel();
    sidebar.setBackground(stiBlue);
    sidebar.setPreferredSize(new Dimension(240, getHeight()));
    sidebar.setLayout(new BorderLayout());

    JPanel sidebarContent = new JPanel();
    sidebarContent.setBackground(stiBlue);
    sidebarContent.setLayout(new BoxLayout(sidebarContent, BoxLayout.Y_AXIS));
    sidebarContent.setBorder(new EmptyBorder(20, 15, 20, 15));

    JLabel welcomeLabel = new JLabel("Student Portal");
    welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
    welcomeLabel.setForeground(stiYellow);
    welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    sidebarContent.add(welcomeLabel);
    sidebarContent.add(Box.createVerticalStrut(30));

    JButton logoutBtn = new JButton("🚪 Logout");
    logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    logoutBtn.setBackground(stiYellow);
    logoutBtn.setForeground(stiBlue);
    logoutBtn.setFocusPainted(false);
    logoutBtn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    logoutBtn.setMaximumSize(new Dimension(200, 45));
    logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

    logoutBtn.addActionListener(e -> {
        int confirm = JOptionPane.showConfirmDialog(this, "Logout now?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new auth.LoginForm().setVisible(true);
            dispose();
        }
    });

    JPanel logoutPanel = new JPanel();
    logoutPanel.setBackground(stiBlue);
    logoutPanel.setLayout(new BoxLayout(logoutPanel, BoxLayout.Y_AXIS));
    logoutPanel.setBorder(new EmptyBorder(20, 15, 30, 15));
    logoutPanel.add(Box.createVerticalGlue());
    logoutPanel.add(logoutBtn);

    sidebar.add(sidebarContent, BorderLayout.NORTH);
    sidebar.add(logoutPanel, BorderLayout.SOUTH);

    // Main content area
    mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(backgroundColor);

    JLabel headerLabel = new JLabel("📚 Student Dashboard", SwingConstants.CENTER);
    headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
    headerLabel.setForeground(stiBlue);
    headerLabel.setBorder(new EmptyBorder(30, 10, 10, 10));
    mainPanel.add(headerLabel, BorderLayout.NORTH);

    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
    centerPanel.setBackground(backgroundColor);
    JScrollPane scrollPane = new JScrollPane(centerPanel);
    scrollPane.setBorder(null);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    mainPanel.add(scrollPane, BorderLayout.CENTER);

    loadStudentByUsername(centerPanel);

    add(sidebar, BorderLayout.WEST);
    add(mainPanel, BorderLayout.CENTER);

    // Open frame maximized
    setExtendedState(JFrame.MAXIMIZED_BOTH);
}


    private void loadStudentByUsername(JPanel centerPanel) {
        try (Connection conn = DBConnection.getConnection()) {
            String studentQuery = "SELECT * FROM students WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(studentQuery);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String fullName = rs.getString("full_name");
                String course = rs.getString("course");
                int studentId = rs.getInt("id");

                // Student Info Card
                JPanel infoCard = new JPanel(new GridLayout(3, 1, 10, 10));
                infoCard.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        new EmptyBorder(20, 20, 20, 20)
                ));
                infoCard.setBackground(Color.WHITE);

                Font infoFont = new Font("Segoe UI", Font.PLAIN, 16);
                JLabel nameLabel = new JLabel("👤 Full Name: " + fullName);
                JLabel usernameLabel = new JLabel("🆔 Username: " + username);
                JLabel courseLabel = new JLabel("🎓 Course: " + course);

                nameLabel.setFont(infoFont);
                usernameLabel.setFont(infoFont);
                courseLabel.setFont(infoFont);

                infoCard.add(nameLabel);
                infoCard.add(usernameLabel);
                infoCard.add(courseLabel);

                centerPanel.add(infoCard);
                centerPanel.add(Box.createVerticalStrut(20));

                // Subjects Table
                String[] columns = {"Subject", "Grade", "Attendance", "Status", "Day", "Start Time", "End Time"};
                DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

                JTable table = new JTable(tableModel);
                table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                table.setRowHeight(28);
                table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
                table.getTableHeader().setBackground(new Color(230, 240, 255));
                table.setGridColor(Color.LIGHT_GRAY);

                String subjectQuery = "SELECT * FROM student_subjects WHERE student_id = ?";
                PreparedStatement subStmt = conn.prepareStatement(subjectQuery);
                subStmt.setInt(1, studentId);
                ResultSet subRs = subStmt.executeQuery();

                while (subRs.next()) {
                    String subject = subRs.getString("subject_name");
                    String grade = subRs.getString("grade");
                    String attendance = subRs.getString("attendance");
                    String status = isPass(grade, attendance) ? "✅ PASS" : "❌ FAIL";
                    String day = subRs.getString("day");
                    String start = subRs.getString("start_time");
                    String end = subRs.getString("end_time");

                    Object[] row = {subject, grade, attendance, status, day, start, end};
                    tableModel.addRow(row);
                }

                JScrollPane tableScrollPane = new JScrollPane(table);
                tableScrollPane.setPreferredSize(new Dimension(900, 300));
                centerPanel.add(tableScrollPane);

            } else {
                JOptionPane.showMessageDialog(this, "No student found with username: " + username);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading student data: " + e.getMessage());
        }
    }

    private boolean isPass(String gradeStr, String attendanceStr) {
        try {
            double grade = Double.parseDouble(gradeStr);
            int attendance = Integer.parseInt(attendanceStr);
            return grade >= 75 && attendance >= 3;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard("student_username_here").setVisible(true));
    }
}
