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

    public User checkLogin(String phone, String password) {
        String sql = "SELECT user_id, full_name, phone, email, role, status "
                + "FROM users "
                + "WHERE phone = ? AND password_hash = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, phone);
            st.setString(2, password);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setFullName(rs.getString("full_name"));
                u.setPhone(rs.getString("phone"));
                u.setEmail(rs.getString("email"));
                u.setRole(Role.valueOf(rs.getString("role")));
                u.setStatus(Status.valueOf(rs.getString("status")));
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

        String sql = """
            INSERT INTO users (full_name, phone, email, password_hash, dob, address, gender, role, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, 'patient', 'active')
        """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, fullName);
            st.setString(2, phone);
            st.setString(3, email);
            st.setString(4, password);
            st.setDate(5, dob);
            st.setString(6, address);
            st.setString(7, gender);

            st.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
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
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, email);
            return st.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<User> getUsersByRole(Role role) {
        String sql = "SELECT user_id, full_name, phone, email, role, status FROM users WHERE role = ? ORDER BY full_name";
        List<User> users = new ArrayList<>();

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, role.toString());
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setFullName(rs.getString("full_name"));
                u.setPhone(rs.getString("phone"));
                u.setEmail(rs.getString("email"));
                u.setRole(Role.valueOf(rs.getString("role")));
                u.setStatus(Status.valueOf(rs.getString("status")));
                users.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public void createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (full_name, phone, email, password_hash, role, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, user.getFullName());
            st.setString(2, user.getPhone());
            st.setString(3, user.getEmail());
            st.setString(4, user.getPasswordHash());
            st.setString(5, user.getRole().toString());
            st.setString(6, user.getStatus().toString());
            st.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, email = ?, status = ? WHERE phone = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, user.getFullName());
            st.setString(2, user.getEmail());
            st.setString(3, user.getStatus().toString());
            st.setString(4, user.getPhone());
            st.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    public void toggleUserStatus(String phone) throws SQLException {
        String sql = "UPDATE users SET status = CASE WHEN status = 'active' THEN 'inactive' ELSE 'active' END WHERE phone = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, phone);
            st.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    public User getUserById(int userId) {
        String sql = "SELECT user_id, full_name, phone, email, role, status FROM users WHERE user_id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, userId);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setFullName(rs.getString("full_name"));
                u.setPhone(rs.getString("phone"));
                u.setEmail(rs.getString("email"));
                u.setRole(Role.valueOf(rs.getString("role")));
                u.setStatus(Status.valueOf(rs.getString("status")));
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserByPhone(String phone) {
        String sql = "SELECT user_id, full_name, phone, email, role, status FROM users WHERE phone = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, phone);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setFullName(rs.getString("full_name"));
                u.setPhone(rs.getString("phone"));
                u.setEmail(rs.getString("email"));
                u.setRole(Role.valueOf(rs.getString("role")));
                u.setStatus(Status.valueOf(rs.getString("status")));
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT user_id, full_name, phone, email, role, status FROM users WHERE email = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, email);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setFullName(rs.getString("full_name"));
                u.setPhone(rs.getString("phone"));
                u.setEmail(rs.getString("email"));
                u.setRole(Role.valueOf(rs.getString("role")));
                u.setStatus(Status.valueOf(rs.getString("status")));
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    


    public List<User> getPatientList() {
        String sql = """
        SELECT u.user_id, u.full_name, u.phone, u.email, u.role, u.status 
        FROM users u 
        WHERE u.role = 'patient' 
        ORDER BY u.full_name
    """;
        List<User> users = new ArrayList<>();

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setFullName(rs.getString("full_name"));
                u.setPhone(rs.getString("phone"));
                u.setEmail(rs.getString("email"));
                u.setRole(Role.valueOf(rs.getString("role")));
                u.setStatus(Status.valueOf(rs.getString("status")));
                users.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public void updateUserRole(int userId, Role role) throws SQLException {
        String sql = "UPDATE users SET role = ? WHERE user_id = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, role.toString());
            st.setInt(2, userId);
            st.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    public List<User> searchUsers(String keyword, Role role) {
        String sql = """
        SELECT user_id, full_name, phone, email, role, status 
        FROM users 
        WHERE (full_name LIKE ? OR phone LIKE ? OR email LIKE ?) AND role = ? 
        ORDER BY full_name
    """;
        List<User> users = new ArrayList<>();
        keyword = "%" + keyword + "%";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, keyword);
            st.setString(2, keyword);
            st.setString(3, keyword);
            st.setString(4, role.toString());
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setFullName(rs.getString("full_name"));
                u.setPhone(rs.getString("phone"));
                u.setEmail(rs.getString("email"));
                u.setRole(Role.valueOf(rs.getString("role")));
                u.setStatus(Status.valueOf(rs.getString("status")));
                users.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public List<User> getAllStaffAndAdmin() {
        String sql = """
        SELECT user_id, full_name, phone, email, role, status 
        FROM users 
        WHERE role IN ('admin', 'doctor', 'receptionist', 'technician') 
        ORDER BY 
            CASE 
                WHEN role = 'admin' THEN 1
                WHEN role = 'doctor' THEN 2
                WHEN role = 'receptionist' THEN 3
                WHEN role = 'technician' THEN 4
            END, full_name
    """;
        List<User> users = new ArrayList<>();

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setFullName(rs.getString("full_name"));
                u.setPhone(rs.getString("phone"));
                u.setEmail(rs.getString("email"));
                u.setRole(Role.valueOf(rs.getString("role")));
                u.setStatus(Status.valueOf(rs.getString("status")));
                users.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public List<User> getUsersByRoleAndStatus(Role role, Status status) {
        String sql = """
        SELECT user_id, full_name, phone, email, role, status 
        FROM users 
        WHERE role = ? AND status = ? 
        ORDER BY full_name
    """;
        List<User> users = new ArrayList<>();

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, role.toString());
            st.setString(2, status.toString());
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setFullName(rs.getString("full_name"));
                u.setPhone(rs.getString("phone"));
                u.setEmail(rs.getString("email"));
                u.setRole(Role.valueOf(rs.getString("role")));
                u.setStatus(Status.valueOf(rs.getString("status")));
                users.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public User getUserById1(int userId) {

        String sql = "SELECT user_id, full_name, phone, email, password_hash, "
                + "role, status, address, image_url "
                + "FROM users WHERE user_id = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {

            st.setInt(1, userId);

            try (ResultSet rs = st.executeQuery()) {

                if (rs.next()) {

                    User u = new User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setFullName(rs.getString("full_name"));
                    u.setPhone(rs.getString("phone"));
                    u.setEmail(rs.getString("email"));
                    u.setPasswordHash(rs.getString("password_hash"));

                    // Convert String -> Enum (an toàn)
                    u.setRole(Role.valueOf(rs.getString("role").trim()));
                    u.setStatus(Status.valueOf(rs.getString("status").trim()));

                    u.setAddress(rs.getString("address"));
                    u.setImageUrl(rs.getString("image_url"));

                    return u;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean checkOldPassword(int userId, String oldPass) {
        String sql = "SELECT password_hash FROM users WHERE user_id=?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, userId);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                return rs.getString("password_hash").equals(oldPass);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updatePassword(int userId, String newPass) {
        String sql = "UPDATE users SET password_hash=? WHERE user_id=?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, newPass);
            st.setInt(2, userId);
            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUser(int id,
            String name,
            String phone,
            String email,
            String address) {

        String sql = """
        UPDATE users
        SET full_name = ?,
            phone = ?,
            email = ?,
            
            updated_at = CURRENT_TIMESTAMP
        WHERE user_id = ?
        """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, email);
            ps.setInt(4, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
