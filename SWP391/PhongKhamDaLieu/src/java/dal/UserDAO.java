package dal;

import model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    // Hàm đăng ký tài khoản mới (Có thêm Address)
    public void register(User u) {
        String sql = "INSERT INTO Users (FullName, DOB, Gender, Phone, Email, Address, Password) VALUES (?,?,?,?,?,?,?)";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, u.getFullName());
            st.setDate(2, u.getDob());
            st.setString(3, u.getGender());
            st.setString(4, u.getPhone());
            st.setString(5, u.getEmail());
            st.setString(6, u.getAddress()); // Lưu địa chỉ
            st.setString(7, u.getPassword());
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
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
}