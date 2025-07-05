package auth;

import config.DBConnection;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class RegisterForm extends JFrame {
    // STI-themed colors
    private final Color stiBlue = Color.decode("#002060");
    private final Color stiYellow = Color.decode("#FFD700");
    private final Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
    private final Font fieldFont = new Font("Segoe UI", Font.PLAIN, 13);

    public RegisterForm() {
        setTitle("STI Admin Registration");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar with STI Blue
        JPanel sidebar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(stiBlue);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setPreferredSize(new Dimension(200, 400));

        JLabel titleLabel = new JLabel("STI", SwingConstants.CENTER);
        titleLabel.setForeground(stiYellow);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        sidebar.add(titleLabel, BorderLayout.CENTER);

        // Form panel (right side)
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new GridLayout(6, 1, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel userLabel = new JLabel("Admin Username:");
        userLabel.setFont(labelFont);
        JTextField userField = new JTextField(20);
        userField.setFont(fieldFont);

        JLabel passLabel = new JLabel("Admin Password:");
        passLabel.setFont(labelFont);
        JPasswordField passField = new JPasswordField(20);
        passField.setFont(fieldFont);

        JButton registerBtn = new JButton("Register");
        registerBtn.setBackground(stiBlue);
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerBtn.setFocusPainted(false);

        // Add components to form panel
        formPanel.add(userLabel);
        formPanel.add(userField);
        formPanel.add(passLabel);
        formPanel.add(passField);
        formPanel.add(new JLabel()); // empty space
        formPanel.add(registerBtn);

        // Combine panels
        add(sidebar, BorderLayout.WEST);
        add(formPanel, BorderLayout.CENTER);

        // Register button logic
        registerBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.connect()) {
                String checkQuery = "SELECT COUNT(*) FROM admins";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "Admin already registered. Proceed to login.");
                    dispose();
                    new LoginForm().setVisible(true);
                    return;
                }

                String insertQuery = "INSERT INTO admins (username, password) VALUES (?, ?)";
                PreparedStatement stmt = conn.prepareStatement(insertQuery);
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Admin registered successfully!");
                dispose();
                new LoginForm().setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Registration failed.");
            }
        });
    }
}
