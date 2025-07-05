package auth;

import config.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginForm extends JFrame {
    private final Dimension RESTORED_SIZE = new Dimension(600, 700);

    public LoginForm() {
        setTitle("STI MIS Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Background panel with background image
        JPanel backgroundPanel = new JPanel(new GridBagLayout()) {
            ImageIcon backgroundImage = new ImageIcon("C:\\Users\\ALONZO\\Desktop\\NetBeansProjects\\STI_MIS\\src\\STI_MIS_JavaProject\\img\\bg.jpeg");

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };

        // Glass-style login panel (slightly darker)
        JPanel loginPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 100)); // Slightly darker glass effect
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        loginPanel.setOpaque(false);
        loginPanel.setPreferredSize(new Dimension(400, 520));
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        Font font = new Font("Segoe UI", Font.PLAIN, 16);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Login Portal", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(font);
        userLabel.setForeground(Color.WHITE);
        JTextField userField = new JTextField(20);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(font);
        passLabel.setForeground(Color.WHITE);
        JPasswordField passField = new JPasswordField(20);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(font);
        roleLabel.setForeground(Color.WHITE);
        String[] roles = {"Student", "Admin"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);

        // Rounded login button
        JButton loginBtn = new JButton("Login") {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setContentAreaFilled(false);
                setFocusPainted(false);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 144, 255));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            }
        };
        loginBtn.setFont(font);
        loginBtn.setBackground(new Color(30, 144, 255));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setOpaque(true);
        loginBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Add components to panel
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        loginPanel.add(title, gbc);
        gbc.gridy++;
        loginPanel.add(userLabel, gbc);
        gbc.gridy++;
        loginPanel.add(userField, gbc);
        gbc.gridy++;
        loginPanel.add(passLabel, gbc);
        gbc.gridy++;
        loginPanel.add(passField, gbc);
        gbc.gridy++;
        loginPanel.add(roleLabel, gbc);
        gbc.gridy++;
        loginPanel.add(roleCombo, gbc);
        gbc.gridy++;
        loginPanel.add(loginBtn, gbc);

        // Add login panel to background
        backgroundPanel.add(loginPanel, new GridBagConstraints());
        add(backgroundPanel);

        // *** ADD WINDOW STATE LISTENER FOR RESTORE BEHAVIOR ***
        addWindowStateListener(e -> {
            if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == 0) { // When restored (not maximized)
                setSize(RESTORED_SIZE);
                setLocationRelativeTo(null); // center
            }
        });

        // Also set initial size and center if not maximized
        setSize(RESTORED_SIZE);
        setLocationRelativeTo(null);

        // Login logic
        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            String role = roleCombo.getSelectedItem().toString().toLowerCase();
            String table = role.equals("admin") ? "admins" : "students";

            try (Connection conn = DBConnection.connect()) {
                String query = "SELECT * FROM " + table + " WHERE username = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    if (role.equals("admin")) {
                        new admin.Dashboard().setVisible(true);
                    } else {
                        new student.Dashboard(username).setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
