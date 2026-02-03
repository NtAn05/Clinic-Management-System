package controller;

import dal.UserDAO;
import model.Role;
import model.Status;
import model.User;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AdminUserServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        // Kiểm tra quyền admin
        if (session == null || session.getAttribute("account") == null) {
            response.sendRedirect(request.getContextPath() + "/pages/auth/login.jsp");
            return;
        }
        
        User user = (User) session.getAttribute("account");
        if (user.getRole() != Role.admin) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Chỉ admin mới được truy cập");
            return;
        }
        
        String action = request.getParameter("action");
        
        try {
            if ("add".equals(action)) {
                handleAddUser(request, response);
            } else if ("delete".equals(action)) {
                handleDeleteUser(request, response);
            } else if ("edit".equals(action)) {
                handleEditUser(request, response);
            } else if ("toggleStatus".equals(action)) {
                handleToggleStatus(request, response);
            } else {
                loadUsers(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi cơ sở dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("pages/admin/users.jsp").forward(request, response);
        }
    }

    private void handleAddUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String fullName = request.getParameter("fullname");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String roleStr = request.getParameter("role");
        
        // Validation
        if (fullName == null || fullName.trim().isEmpty() ||
            phone == null || phone.trim().isEmpty() ||
            roleStr == null || roleStr.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng điền đầy đủ thông tin");
            loadUsers(request, response);
            return;
        }
        
        UserDAO userDAO = new UserDAO();
        
        // Kiểm tra số điện thoại đã tồn tại
        if (userDAO.isPhoneExist(phone)) {
            request.setAttribute("error", "Số điện thoại này đã tồn tại");
            loadUsers(request, response);
            return;
        }
        
        try {
            Role role;
            if ("2".equals(roleStr)) {
                role = Role.doctor;
            } else if ("3".equals(roleStr)) {
                role = Role.receptionist;
            } else if ("4".equals(roleStr)) {
                role = Role.technician;
            } else {
                role = Role.receptionist;
            }
            
            User newUser = new User();
            newUser.setFullName(fullName);
            newUser.setPhone(phone);
            newUser.setPasswordHash(password);
            newUser.setRole(role);
            newUser.setStatus(Status.active);
            
            userDAO.createUser(newUser);
            request.setAttribute("success", "Tạo tài khoản thành công");
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi khi tạo tài khoản: " + e.getMessage());
        }
        
        loadUsers(request, response);
    }

    private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String phone = request.getParameter("phone");
        
        if (phone == null || phone.trim().isEmpty()) {
            request.setAttribute("error", "Không tìm thấy người dùng");
            loadUsers(request, response);
            return;
        }
        
        UserDAO userDAO = new UserDAO();
        try {
            userDAO.deleteUser(phone);
            request.setAttribute("success", "Xóa tài khoản thành công");
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi khi xóa tài khoản: " + e.getMessage());
        }
        
        loadUsers(request, response);
    }

    private void handleEditUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String fullName = request.getParameter("fullname");
        String phone = request.getParameter("phone");
        String statusStr = request.getParameter("status");
        
        if (fullName == null || fullName.trim().isEmpty() ||
            phone == null || phone.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng điền đầy đủ thông tin");
            loadUsers(request, response);
            return;
        }
        
        UserDAO userDAO = new UserDAO();
        try {
            User user = new User();
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setStatus(Status.valueOf(statusStr != null ? statusStr : "active"));
            
            userDAO.updateUser(user);
            request.setAttribute("success", "Cập nhật tài khoản thành công");
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi khi cập nhật: " + e.getMessage());
        }
        
        loadUsers(request, response);
    }

    private void handleToggleStatus(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String phone = request.getParameter("phone");
        
        if (phone == null || phone.trim().isEmpty()) {
            request.setAttribute("error", "Không tìm thấy người dùng");
            loadUsers(request, response);
            return;
        }
        
        UserDAO userDAO = new UserDAO();
        try {
            userDAO.toggleUserStatus(phone);
            request.setAttribute("success", "Cập nhật trạng thái thành công");
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
        
        loadUsers(request, response);
    }

    private void loadUsers(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        UserDAO userDAO = new UserDAO();
        
        // Lấy danh sách bác sĩ
        List<User> doctors = userDAO.getUsersByRole(Role.doctor);
        request.setAttribute("doctors", doctors != null ? doctors : new ArrayList<>());
        
        // Lấy danh sách nhân viên
        List<User> staffs = userDAO.getUsersByRole(Role.receptionist);
        List<User> technicians = userDAO.getUsersByRole(Role.technician);
        
        List<User> allStaff = new ArrayList<>();
        if (staffs != null) allStaff.addAll(staffs);
        if (technicians != null) allStaff.addAll(technicians);
        
        request.setAttribute("staffs", allStaff);
        
        request.getRequestDispatcher("pages/admin/users.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Admin User Management Servlet";
    }
}
