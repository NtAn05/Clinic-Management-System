package dal;

import model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Role;
import model.Status;
import model.Users;

public class UserDAO extends DBContext {

    // ===== ĐÃ SỬA: Thay tham số email thành phone cho chuẩn logic đăng nhập =====
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

        String sqlUser = """
        INSERT INTO users (full_name, phone, email, password_hash, role, status)
        VALUES (?, ?, ?, ?, 'patient', 'active')
    """;

        String sqlPatient = """
        INSERT INTO patients (user_id, full_name, phone, dob, address, email, gender)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

        connection.setAutoCommit(false);

        try (PreparedStatement stUser = connection.prepareStatement(sqlUser, PreparedStatement.RETURN_GENERATED_KEYS)) {
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

    // ===== ĐÃ SỬA: Sửa tên bảng "us" thành "users" =====
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

    // Lấy danh sách người dùng theo vai trò
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

    // Tạo người dùng mới
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

    // Cập nhật thông tin người dùng
    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, phone = ?, email = ?, status = ? WHERE user_id = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, user.getFullName());
            st.setString(2, user.getPhone());
            st.setString(3, user.getEmail());
            st.setString(4, user.getStatus().toString());
            st.setInt(5, user.getUserId());
            st.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    // Bật/Tắt trạng thái người dùng
    public void toggleUserStatus(String phone) throws SQLException {
        String sql = "UPDATE users SET status = CASE WHEN status = 'active' THEN 'inactive' ELSE 'active' END WHERE phone = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, phone);
            st.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    // Lấy thông tin user theo ID
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

    // Lấy thông tin user theo số điện thoại
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

    // Lấy danh sách bệnh nhân
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

    // Cập nhật role của người dùng
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

    // Tìm kiếm người dùng theo tên hoặc số điện thoại
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

    // Lấy danh sách tất cả nhân viên (bao gồm bác sĩ, tiếp tân, kỹ thuật viên) và admin
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

    // Lọc người dùng theo role và status
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


    public Object getUserById2(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, userId);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                Users u = new Users();
                u.setUserId(rs.getInt("user_id"));
                u.setFullName(rs.getString("full_name"));
                u.setPhone(rs.getString("phone"));
                u.setEmail(rs.getString("email"));
                u.setGender(rs.getString("gender"));
                u.setDob(rs.getDate("dob"));
                u.setCity(rs.getString("address_province"));
                u.setDistrict(rs.getString("address_district"));
                u.setDetail(rs.getString("address_detail"));
                return u;
            }
        } catch (Exception e) {
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

    public void updateUser(int userId, String name, String phone, String email, String gender, String dob, String city, String commune, String house) { {

        String sql = "UPDATE users SET full_name=?, phone=?, email=?, gender=?, dob=?, "
                + "address_province=?, address_district=?, address_detail=?, updated_at=NOW() "
                + "WHERE user_id=?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {

            st.setString(1, name);
            st.setString(2, phone);
            st.setString(3, email);
            st.setString(4, gender);
            st.setString(5, dob);
            st.setString(6, city);
            st.setString(7, commune);
            st.setString(8, house);
            st.setInt(9, userId);

            st.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }  }
}

}
