package dal;

import model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Role;
import model.Status;

public class UserDAO extends DBContext {

    // Hàm kiểm tra đăng nhập
    public User checkLogin(String phone, String password) {
        String sql = """
        SELECT user_id, phone, role, status
        FROM users
        WHERE phone = ?
          AND password_hash = ?
          AND status = 'active'
    """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, phone);
            st.setString(2, password); // sau này hash

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("user_id")); // đúng tên cột
                u.setPhone(rs.getString("phone"));
                u.setRole(Role.valueOf(rs.getString("role").toUpperCase()));
                u.setStatus(Status.valueOf(rs.getString("status").toUpperCase()));
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void registerPatient(
            String fullName,
            String phone,
            String email,
            String password,
            java.sql.Date dob,
            String address,
            String gender
    ) throws SQLException {

        String sqlUser = """
        INSERT INTO users (full_name, phone, email, password_hash, role, status)
        VALUES (?, ?, ?, ?, 'patient', 'active')
    """;

        String sqlPatient = """
        INSERT INTO patients (user_id, full_name, phone, dob, address, email, gender)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

        connection.setAutoCommit(false);

        try (
                PreparedStatement stUser
                = connection.prepareStatement(sqlUser, PreparedStatement.RETURN_GENERATED_KEYS)) {
            // users
            stUser.setString(1, fullName);
            stUser.setString(2, phone);
            stUser.setString(3, email);
            stUser.setString(4, password); // TODO hash sau

            stUser.executeUpdate();

            ResultSet rs = stUser.getGeneratedKeys();
            if (!rs.next()) {
                throw new SQLException("Không lấy được user_id");
            }

            int userId = rs.getInt(1);

            // patients
            try (PreparedStatement stPatient = connection.prepareStatement(sqlPatient)) {
                stPatient.setInt(1, userId);
                stPatient.setString(2, fullName);
                stPatient.setString(3, phone);
                stPatient.setDate(4, dob);
                stPatient.setString(5, address);
                stPatient.setString(6, email);
                stPatient.setString(7, gender); // male/female/other

                stPatient.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public boolean isPhoneExist(String phone) {
        String sql = "SELECT 1 FROM users WHERE phone = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, phone);
            return st.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isEmailExist(String email) {
        String sql = "SELECT 1 FROM patients WHERE email = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, email);
            return st.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa User (Thực tế nên là Khóa tài khoản thay vì xóa hẳn)
    public void deleteUser(String phone) {
        String sql = "DELETE FROM Users WHERE Phone = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, phone);
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

}
