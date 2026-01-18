package dal;

import model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DBContext {

    // Hàm kiểm tra đăng nhập
    public User checkLogin(String phone, String password) {
        String sql = "SELECT * FROM Users WHERE Phone = ? AND Password = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, phone);
            st.setString(2, password);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setFullName(rs.getString("FullName"));
                u.setPhone(rs.getString("Phone"));
                u.setRoleId(rs.getInt("RoleID"));
                u.setStatus(rs.getBoolean("Status")); // Check status để xem có bị lock không
                return u;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public void register(User u) {
        String sql = "INSERT INTO Users (FullName, DOB, Gender, Phone, Email, Address, Password, RoleID, Status) VALUES (?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, u.getFullName());
            st.setDate(2, u.getDob());
            st.setString(3, u.getGender());
            st.setString(4, u.getPhone());
            st.setString(5, u.getEmail());
            st.setString(6, u.getAddress());
            st.setString(7, u.getPassword());
            st.setInt(8, u.getRoleId()); 
            st.setBoolean(9, true);      
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Lỗi Register: " + e);
        }
    }
    
    // Hàm check xem số điện thoại đã tồn tại chưa
    public boolean isPhoneExist(String phone) {
        String sql = "SELECT * FROM Users WHERE Phone = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, phone);
            ResultSet rs = st.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }
    // Hàm check xem email đã tồn tại chưa
    public boolean isEmailExist(String email) {
        String sql = "SELECT * FROM Users WHERE Email = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, email);
            ResultSet rs = st.executeQuery();
            return rs.next(); // Nếu tìm thấy (true) nghĩa là trùng
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }
    
    // Lấy danh sách User theo Role (2: Bác sĩ, 3: Nhân viên)
    public List<User> getUsersByRole(int roleId) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE RoleID = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, roleId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                User u = new User(
                    rs.getString("FullName"),
                    rs.getDate("DOB"),
                    rs.getString("Gender"),
                    rs.getString("Phone"),
                    rs.getString("Email"),
                    rs.getString("Address"),
                    rs.getString("Password")
                );
                u.setRoleId(rs.getInt("RoleID")); // Set Role
                u.setStatus(rs.getBoolean("Status")); // Set Status
                // Lưu ý: Class User của bạn cần thêm setter cho ID nếu chưa có
                // u.setId(rs.getInt("UserID")); 
                list.add(u);
            }
        } catch (SQLException e) { System.out.println(e); }
        return list;
    }

    // Xóa User (Thực tế nên là Khóa tài khoản thay vì xóa hẳn)
    public void deleteUser(String phone) {
        String sql = "DELETE FROM Users WHERE Phone = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, phone);
            st.executeUpdate();
        } catch (SQLException e) { System.out.println(e); }
    }

}