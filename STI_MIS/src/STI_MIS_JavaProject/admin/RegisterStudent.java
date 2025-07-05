package admin;

import config.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class RegisterStudent extends JFrame {
    private JTextField usernameField, fullNameField, studentIdField, courseField;
    private JPasswordField passwordField;
    private JPanel subjectPanel;
    private ArrayList<SubjectForm> subjects = new ArrayList<>();

    public RegisterStudent() {
        setTitle("Register Student");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 700));
        setLayout(new BorderLayout());

        // === STI THEMED SIDEBAR ===
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(new Color(0x002060));
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel welcomeLabel = new JLabel("STI Admin", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(0xFFD700));
        welcomeLabel.setBorder(new EmptyBorder(20, 10, 20, 10));
        sidebar.add(welcomeLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        Font btnFont = new Font("Segoe UI", Font.PLAIN, 16);
        JButton manageStudentBtn = new JButton("Manage Student");
        JButton registerBtn = new JButton("Register Student");

        JButton[] buttons = {manageStudentBtn, registerBtn};
        for (JButton btn : buttons) {
            btn.setFont(btnFont);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(180, 40));
            btn.setFocusPainted(false);
            btn.setBackground(new Color(0xFFD700));
            btn.setForeground(Color.BLACK);
            btn.setBorder(BorderFactory.createLineBorder(new Color(0xFFD700)));
            buttonPanel.add(Box.createVerticalStrut(20));
            buttonPanel.add(btn);
        }

        sidebar.add(buttonPanel, BorderLayout.CENTER);

       JPanel bottomPanel = new JPanel();
       bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
       bottomPanel.setOpaque(false);
       bottomPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

       JButton logoutBtn = new JButton("Logout");
       logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
       logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
       logoutBtn.setMaximumSize(new Dimension(180, 40));
       logoutBtn.setPreferredSize(new Dimension(180, 40));
       logoutBtn.setFocusPainted(false);
       logoutBtn.setBackground(new Color(0xFFD700)); // Yellow background
       logoutBtn.setForeground(Color.BLACK);        // Black text
       logoutBtn.setBorder(BorderFactory.createLineBorder(new Color(0xFFD700)));

       bottomPanel.add(Box.createVerticalGlue());
       bottomPanel.add(logoutBtn);
       bottomPanel.add(Box.createVerticalGlue());

       sidebar.add(bottomPanel, BorderLayout.SOUTH);
       add(sidebar, BorderLayout.WEST);


        // === MAIN CARD PANEL ===
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(200, 200, 200), 2, true));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(true);
        card.setBorder(BorderFactory.createCompoundBorder(
                card.getBorder(),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        // Header with STI blue
        JLabel headerLabel = new JLabel("Register Student");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        headerLabel.setForeground(new Color(0x002060));
        card.add(headerLabel);

        card.add(createFormField("Student ID:", studentIdField = createTextFieldWithPlaceholder("Enter student ID")));
        card.add(createFormField("Full Name:", fullNameField = createTextFieldWithPlaceholder("Enter full name")));
        card.add(createFormField("Username:", usernameField = createTextFieldWithPlaceholder("Choose a username")));
        card.add(createFormField("Password:", passwordField = createPasswordFieldWithPlaceholder("Enter a secure password")));
        card.add(createFormField("Course:", courseField = createTextFieldWithPlaceholder("Enter course")));

        subjectPanel = new JPanel();
        subjectPanel.setLayout(new BoxLayout(subjectPanel, BoxLayout.Y_AXIS));
        subjectPanel.setOpaque(false);

        JButton addSubjectBtn = new JButton("Add Subject");
        addSubjectBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addSubjectBtn.addActionListener(e -> {
            SubjectForm sf = new SubjectForm();
            subjects.add(sf);
            subjectPanel.add(sf.getPanel());
            subjectPanel.revalidate();
            subjectPanel.repaint();
        });

        card.add(Box.createVerticalStrut(10));
        card.add(addSubjectBtn);
        card.add(subjectPanel);

        JButton registerButton = new JButton("Register");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        registerButton.addActionListener(e -> registerStudent());
        card.add(Box.createVerticalStrut(20));
        card.add(registerButton);

        JScrollPane cardScrollPane = new JScrollPane(card);
        cardScrollPane.setBorder(null);
        cardScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(cardScrollPane, BorderLayout.CENTER);

        // Button Actions
        manageStudentBtn.addActionListener(e -> {
            new Dashboard().setVisible(true);
            dispose();
        });

        registerBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "You are already on the Register Student page.");
        });

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new auth.LoginForm().setVisible(true);
                dispose();
            }
        });

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    private JTextField createTextFieldWithPlaceholder(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setPreferredSize(new Dimension(300, 30));
        field.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        field.setForeground(Color.GRAY);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    field.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                    field.setForeground(Color.GRAY);
                }
            }
        });
        return field;
    }

    private JPasswordField createPasswordFieldWithPlaceholder(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(300, 30));
        field.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        field.setForeground(Color.GRAY);
        field.setEchoChar((char) 0);
        field.setText(placeholder);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    field.setForeground(Color.BLACK);
                    field.setEchoChar('•');
                }
            }
            public void focusLost(FocusEvent e) {
                if (String.valueOf(field.getPassword()).isEmpty()) {
                    field.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                    field.setForeground(Color.GRAY);
                    field.setEchoChar((char) 0);
                    field.setText(placeholder);
                }
            }
        });
        return field;
    }

    private JPanel createFormField(String label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jLabel.setPreferredSize(new Dimension(120, 30));
        panel.add(jLabel, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        return panel;
    }

    private void registerStudent() {
        String id = studentIdField.getText().trim();
        String name = fullNameField.getText().trim();
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();
        String course = courseField.getText().trim();

        if (id.isEmpty() || id.equals("Enter student ID") ||
            name.isEmpty() || name.equals("Enter full name") ||
            user.isEmpty() || user.equals("Choose a username") ||
            pass.isEmpty() || pass.equals("Enter a secure password") ||
            course.isEmpty() || course.equals("Enter course")) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Integer.parseInt(id);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Student ID must be a valid number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.connect()) {
            PreparedStatement insertStudent = conn.prepareStatement(
                    "INSERT INTO students (id, full_name, username, password, course) VALUES (?, ?, ?, ?, ?)");
            insertStudent.setInt(1, Integer.parseInt(id));
            insertStudent.setString(2, name);
            insertStudent.setString(3, user);
            insertStudent.setString(4, pass);
            insertStudent.setString(5, course);
            insertStudent.executeUpdate();

            for (SubjectForm subject : subjects) {
                PreparedStatement insertSubject = conn.prepareStatement(
                        "INSERT INTO student_subjects (student_id, subject_name, grade, attendance, day, start_time, end_time) VALUES (?, ?, ?, ?, ?, ?, ?)");
                insertSubject.setInt(1, Integer.parseInt(id));
                insertSubject.setString(2, subject.subjectName.getText().trim());
                insertSubject.setString(3, subject.grade.getText().trim());
                insertSubject.setString(4, subject.attendance.getText().trim());
                insertSubject.setString(5, subject.day.getText().trim());
                insertSubject.setString(6, subject.startTime.getText().trim());
                insertSubject.setString(7, subject.endTime.getText().trim());
                insertSubject.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Student registered successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            new Dashboard().setVisible(true);
            dispose();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error registering student: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class SubjectForm {
        JPanel panel;
        JTextField subjectName, grade, attendance, day, startTime, endTime;

        SubjectForm() {
            panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createTitledBorder("Subject Details"));
            panel.setBackground(Color.WHITE);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 15, 8, 15);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            subjectName = createTextFieldWithPlaceholder("Enter subject name");
            grade = createTextFieldWithPlaceholder("Enter grade");
            attendance = createTextFieldWithPlaceholder("Enter attendance");
            day = createTextFieldWithPlaceholder("Enter day");
            startTime = createTextFieldWithPlaceholder("Enter start time");
            endTime = createTextFieldWithPlaceholder("Enter end time");

            JButton removeBtn = new JButton("Remove");
            removeBtn.setForeground(Color.WHITE);
            removeBtn.setBackground(new Color(220, 53, 69));
            removeBtn.setFocusPainted(false);
            removeBtn.addActionListener(e -> {
                subjects.remove(this);
                subjectPanel.remove(panel);
                subjectPanel.revalidate();
                subjectPanel.repaint();
            });

            int row = 0;
            addLabeledField(panel, gbc, "Subject", subjectName, 0, row++);
            addLabeledField(panel, gbc, "Grade", grade, 0, row++);
            addLabeledField(panel, gbc, "Attendance", attendance, 0, row++);
            row = 0;
            addLabeledField(panel, gbc, "Day", day, 2, row++);
            addLabeledField(panel, gbc, "Start Time", startTime, 2, row++);
            addLabeledField(panel, gbc, "End Time", endTime, 2, row++);

            gbc.gridx = 0;
            gbc.gridy = Math.max(row, 3);
            gbc.gridwidth = 4;
            gbc.anchor = GridBagConstraints.CENTER;
            panel.add(removeBtn, gbc);
        }

        private void addLabeledField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField textField, int col, int row) {
            gbc.gridx = col;
            gbc.gridy = row;
            gbc.gridwidth = 1;
            panel.add(new JLabel(labelText + ":"), gbc);

            gbc.gridx = col + 1;
            panel.add(textField, gbc);
        }

        JPanel getPanel() {
            return panel;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegisterStudent::new);
    }
}
