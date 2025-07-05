import auth.LoginForm;
import auth.RegisterForm;
import config.DBConnection;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.connect()) {
            String query = "SELECT COUNT(*) FROM admins";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            rs.next();

            if (rs.getInt(1) > 0) {
                new LoginForm().setVisible(true);
            } else {
                new RegisterForm().setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
