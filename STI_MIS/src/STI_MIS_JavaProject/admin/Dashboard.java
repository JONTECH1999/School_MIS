package admin;

import config.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class Dashboard extends JFrame {

    private JPanel mainContentPanel;

    public Dashboard() {
        setTitle("STI Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Color scheme
        Color stiBlue = Color.decode("#002060");
        Color stiYellow = Color.decode("#FFD700");
        Color backgroundColor = Color.decode("#E6F0FF"); // highlighted background
        Color cardColor = Color.decode("#FFFFFF");
        Color textColor = Color.decode("#333333");

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(stiBlue);
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setLayout(new BorderLayout());

        JPanel sidebarContent = new JPanel();
        sidebarContent.setBackground(stiBlue);
        sidebarContent.setLayout(new BoxLayout(sidebarContent, BoxLayout.Y_AXIS));
        sidebarContent.setBorder(new EmptyBorder(20, 15, 20, 15));

        JLabel welcomeLabel = new JLabel("STI Admin");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(stiYellow);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarContent.add(welcomeLabel);
        sidebarContent.add(Box.createVerticalStrut(30));

        JButton manageStudentBtn = new JButton("📋 Manage Students");
        JButton registerBtn = new JButton("➕ Register Student");

        JButton[] buttons = {manageStudentBtn, registerBtn};
        for (JButton btn : buttons) {
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            btn.setBackground(stiYellow);
            btn.setForeground(stiBlue);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            btn.setMaximumSize(new Dimension(200, 45));
            sidebarContent.add(Box.createVerticalStrut(15));
            sidebarContent.add(btn);
        }

        sidebar.add(sidebarContent, BorderLayout.NORTH);

        // Logout at bottom
        JButton logoutBtn = new JButton("🚪 Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        logoutBtn.setBackground(stiYellow);
        logoutBtn.setForeground(stiBlue);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        logoutBtn.setMaximumSize(new Dimension(200, 45));

        JPanel logoutPanel = new JPanel();
        logoutPanel.setBackground(stiBlue);
        logoutPanel.setLayout(new BoxLayout(logoutPanel, BoxLayout.Y_AXIS));
        logoutPanel.setBorder(new EmptyBorder(20, 15, 30, 15));
        logoutPanel.add(Box.createVerticalGlue());
        logoutPanel.add(logoutBtn);

        sidebar.add(logoutPanel, BorderLayout.SOUTH);
        add(sidebar, BorderLayout.WEST);

        // Main content area
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);

        JLabel headerLabel = new JLabel("📚 Manage Student Records", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(stiBlue);
        headerLabel.setBorder(new EmptyBorder(30, 10, 10, 10));

        mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setBackground(backgroundColor);

        JScrollPane scrollPane = new JScrollPane(mainContentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(headerLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Button actions
        registerBtn.addActionListener(e -> {
            new RegisterStudent().setVisible(true);
            dispose();
        });

        manageStudentBtn.addActionListener(e -> loadStudents());

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Logout now?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new auth.LoginForm().setVisible(true);
                dispose();
            }
        });

        loadStudents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    private void loadStudents() {
        mainContentPanel.removeAll();

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM students");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("full_name");
                String username = rs.getString("username");
                String course = rs.getString("course");

                JPanel card = createStudentCard(conn, id, name, username, course);
                mainContentPanel.add(card);
                mainContentPanel.add(Box.createVerticalStrut(15));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage());
        }

        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private JPanel createStudentCard(Connection conn, int id, String name, String username, String course) throws SQLException {
        Color cardColor = Color.decode("#FFFFFF");
        Color textColor = Color.decode("#333333");

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLabel.setForeground(textColor);

        JTextArea info = new JTextArea();
        info.setEditable(false);
        info.setBackground(cardColor);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        info.setForeground(textColor);
        info.setText("👤 Username: " + username + "\n🎓 Course: " + course + "\n");

        try (PreparedStatement subStmt = conn.prepareStatement("SELECT * FROM student_subjects WHERE student_id = ?")) {
            subStmt.setInt(1, id);
            try (ResultSet subRs = subStmt.executeQuery()) {
                while (subRs.next()) {
                    String subject = subRs.getString("subject_name");
                    String grade = subRs.getString("grade");
                    String attendance = subRs.getString("attendance");
                    String day = subRs.getString("day");
                    String start = subRs.getString("start_time");
                    String end = subRs.getString("end_time");

                    String status = isPass(grade, attendance) ? "✅ PASS" : "❌ FAIL";

                    info.append("\n📘 Subject: " + subject);
                    info.append("\n  📈 Grade: " + grade);
                    info.append("\n  🕒 Attendance: " + attendance);
                    info.append("\n  📊 Status: " + status);
                    info.append("\n  📅 Schedule: " + day + " " + start + " - " + end + "\n");
                }
            }
        }

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(cardColor);
        top.add(nameLabel, BorderLayout.NORTH);
        top.add(info, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(cardColor);
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");

        editBtn.setFocusPainted(false);
        deleteBtn.setFocusPainted(false);

        // Edit button action opens EditStudent with this student id
        editBtn.addActionListener(e -> {
            new EditStudent(id).setVisible(true);
            dispose();
        });

        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete " + name + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteStudent(id);
                loadStudents();
            }
        });

        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);

        card.add(top, BorderLayout.CENTER);
        card.add(btnPanel, BorderLayout.SOUTH);

        return card;
    }

    private boolean isPass(String gradeStr, String attendanceStr) {
        try {
            double grade = Double.parseDouble(gradeStr);
            int attendance = Integer.parseInt(attendanceStr);
            return grade >= 75 && attendance >= 3;
        } catch (Exception e) {
            return false;
        }
    }

    private void deleteStudent(int id) {
        try (Connection conn = DBConnection.connect()) {
            PreparedStatement delSubjects = conn.prepareStatement("DELETE FROM student_subjects WHERE student_id = ?");
            delSubjects.setInt(1, id);
            delSubjects.executeUpdate();

            PreparedStatement delStudent = conn.prepareStatement("DELETE FROM students WHERE id = ?");
            delStudent.setInt(1, id);
            delStudent.executeUpdate();

            JOptionPane.showMessageDialog(this, "Deleted successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Delete failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Dashboard::new);
    }
}
