package admin;

import config.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EditStudent extends JFrame {
    private int studentId;

    private JTextField fullNameField;
    private JTextField usernameField;
    private JTextField courseField;

    private JPanel subjectsPanel;
    private JScrollPane subjectsScrollPane;

    private List<SubjectRow> subjectRows = new ArrayList<>();

    // STI theme colors
    private final Color stiBlue = Color.decode("#002060");
    private final Color stiYellow = Color.decode("#FFD700");
    private final Color backgroundColor = new Color(240, 244, 252);

    // Inner class to hold UI components for each subject row
    private class SubjectRow {
        JTextField subjectNameField;
        JTextField gradeField;
        JTextField attendanceField;
        JComboBox<String> dayComboBox;
        JTextField startTimeField;
        JTextField endTimeField;
    }

    public EditStudent(int studentId) {
        this.studentId = studentId;
        initializeUI();
        loadStudentData();
    }

    private void initializeUI() {
        setTitle("Edit Student");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        // Set background color
        getContentPane().setBackground(backgroundColor);

        // Student Info Panel with STI Blue background
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(stiBlue);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(stiYellow, 2),
                "Student Information",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 18),
                stiYellow));

        // Labels and fields with STI Yellow text on blue background
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Color labelColor = stiYellow;
        Color fieldBg = Color.WHITE;
        Color fieldFg = stiBlue;

        JLabel lblFullName = new JLabel("Full Name:");
        lblFullName.setFont(labelFont);
        lblFullName.setForeground(labelColor);
        fullNameField = new JTextField();
        styleTextField(fullNameField, fieldBg, fieldFg);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(labelFont);
        lblUsername.setForeground(labelColor);
        usernameField = new JTextField();
        styleTextField(usernameField, fieldBg, fieldFg);

        JLabel lblCourse = new JLabel("Course:");
        lblCourse.setFont(labelFont);
        lblCourse.setForeground(labelColor);
        courseField = new JTextField();
        styleTextField(courseField, fieldBg, fieldFg);

        infoPanel.add(lblFullName);
        infoPanel.add(fullNameField);
        infoPanel.add(lblUsername);
        infoPanel.add(usernameField);
        infoPanel.add(lblCourse);
        infoPanel.add(courseField);

        // Subjects panel with white background and STI Yellow border
        subjectsPanel = new JPanel();
        subjectsPanel.setLayout(new BoxLayout(subjectsPanel, BoxLayout.Y_AXIS));
        subjectsPanel.setBackground(Color.WHITE);
        subjectsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(stiYellow, 2),
                "Subjects",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 18),
                stiBlue));

        subjectsScrollPane = new JScrollPane(subjectsPanel);
        subjectsScrollPane.setPreferredSize(new Dimension(860, 400));
        subjectsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        subjectsScrollPane.getViewport().setBackground(Color.WHITE);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBackground(backgroundColor);

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        styleButton(saveBtn, stiYellow, stiBlue);
        styleButton(cancelBtn, Color.LIGHT_GRAY, stiBlue);

        buttonsPanel.add(saveBtn);
        buttonsPanel.add(cancelBtn);

        // Add components to frame
        add(infoPanel, BorderLayout.NORTH);
        add(subjectsScrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);

        // Button actions
        saveBtn.addActionListener(e -> saveStudent());
        cancelBtn.addActionListener(e -> {
            new Dashboard().setVisible(true);
            dispose();
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    // Helper to style text fields
    private void styleTextField(JTextField field, Color bg, Color fg) {
        field.setBackground(bg);
        field.setForeground(fg);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createLineBorder(stiBlue));
    }

    // Helper to style buttons
    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
    }

    private void loadStudentData() {
        try (Connection conn = DBConnection.connect()) {
            // Load student info
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM students WHERE id = ?")) {
                stmt.setInt(1, studentId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    fullNameField.setText(rs.getString("full_name"));
                    usernameField.setText(rs.getString("username"));
                    courseField.setText(rs.getString("course"));
                }
            }

            // Load subjects
            try (PreparedStatement stmtSubj = conn.prepareStatement(
                    "SELECT * FROM student_subjects WHERE student_id = ?")) {
                stmtSubj.setInt(1, studentId);
                ResultSet rsSubj = stmtSubj.executeQuery();

                while (rsSubj.next()) {
                    SubjectRow row = new SubjectRow();

                    row.subjectNameField = new JTextField(rsSubj.getString("subject_name"));
                    row.subjectNameField.setEditable(false);
                    styleTextField(row.subjectNameField, Color.LIGHT_GRAY, stiBlue);
                    row.subjectNameField.setColumns(10);

                    row.gradeField = new JTextField(rsSubj.getString("grade"));
                    styleTextField(row.gradeField, Color.WHITE, stiBlue);
                    row.gradeField.setColumns(5);

                    row.attendanceField = new JTextField(rsSubj.getString("attendance"));
                    styleTextField(row.attendanceField, Color.WHITE, stiBlue);
                    row.attendanceField.setColumns(5);

                    row.dayComboBox = new JComboBox<>(new String[]{
                            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"});
                    row.dayComboBox.setSelectedItem(rsSubj.getString("day"));
                    row.dayComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    row.dayComboBox.setBackground(Color.WHITE);
                    row.dayComboBox.setForeground(stiBlue);

                    row.startTimeField = new JTextField(rsSubj.getString("start_time"));
                    styleTextField(row.startTimeField, Color.WHITE, stiBlue);
                    row.startTimeField.setColumns(7);

                    row.endTimeField = new JTextField(rsSubj.getString("end_time"));
                    styleTextField(row.endTimeField, Color.WHITE, stiBlue);
                    row.endTimeField.setColumns(7);

                    JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                    rowPanel.setBackground(Color.WHITE);
                    rowPanel.add(new JLabel("Subject:"));
                    rowPanel.add(row.subjectNameField);
                    rowPanel.add(new JLabel("Grade:"));
                    rowPanel.add(row.gradeField);
                    rowPanel.add(new JLabel("Attendance:"));
                    rowPanel.add(row.attendanceField);
                    rowPanel.add(new JLabel("Day:"));
                    rowPanel.add(row.dayComboBox);
                    rowPanel.add(new JLabel("Start:"));
                    rowPanel.add(row.startTimeField);
                    rowPanel.add(new JLabel("End:"));
                    rowPanel.add(row.endTimeField);

                    subjectsPanel.add(rowPanel);
                    subjectRows.add(row);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load student data: " + ex.getMessage());
        }

        subjectsPanel.revalidate();
        subjectsPanel.repaint();
    }

    private void saveStudent() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String course = courseField.getText().trim();

        if (fullName.isEmpty() || username.isEmpty() || course.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all student information fields.");
            return;
        }

        try (Connection conn = DBConnection.connect()) {
            conn.setAutoCommit(false);

            // Update students table
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE students SET full_name = ?, username = ?, course = ? WHERE id = ?")) {
                stmt.setString(1, fullName);
                stmt.setString(2, username);
                stmt.setString(3, course);
                stmt.setInt(4, studentId);
                stmt.executeUpdate();
            }

            // Update student_subjects
            String updateSubjectSql = "UPDATE student_subjects SET grade = ?, attendance = ?, day = ?, start_time = ?, end_time = ? WHERE student_id = ? AND subject_name = ?";
            try (PreparedStatement stmtSubj = conn.prepareStatement(updateSubjectSql)) {
                for (SubjectRow row : subjectRows) {
                    String grade = row.gradeField.getText().trim();
                    String attendance = row.attendanceField.getText().trim();
                    String day = (String) row.dayComboBox.getSelectedItem();
                    String start = row.startTimeField.getText().trim();
                    String end = row.endTimeField.getText().trim();
                    String subjectName = row.subjectNameField.getText().trim();

                    if (grade.isEmpty() || attendance.isEmpty() || day.isEmpty() || start.isEmpty() || end.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Please fill in all subject fields.");
                        conn.rollback();
                        return;
                    }

                    stmtSubj.setString(1, grade);
                    stmtSubj.setString(2, attendance);
                    stmtSubj.setString(3, day);
                    stmtSubj.setString(4, start);
                    stmtSubj.setString(5, end);
                    stmtSubj.setInt(6, studentId);
                    stmtSubj.setString(7, subjectName);
                    stmtSubj.addBatch();
                }
                stmtSubj.executeBatch();
            }

            conn.commit();

            JOptionPane.showMessageDialog(this, "Student and subjects updated successfully.");
            new Dashboard().setVisible(true);
            dispose();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating student and subjects: " + ex.getMessage());
        }
    }
}
