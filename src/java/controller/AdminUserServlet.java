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
            } else if ("updateRole".equals(action)) {
                handleUpdateRole(request, response);
            } else if ("search".equals(action)) {
                handleSearch(request, response);
            } else if ("filter".equals(action)) {
                handleFilter(request, response);
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
        String email = request.getParameter("email");
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
            user.setEmail(email);
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
        
        // Lấy danh sách nhân viên (receptionist + technician)
        List<User> receptionists = userDAO.getUsersByRole(Role.receptionist);
        List<User> technicians = userDAO.getUsersByRole(Role.technician);
        
        List<User> allStaff = new ArrayList<>();
        if (receptionists != null) allStaff.addAll(receptionists);
        if (technicians != null) allStaff.addAll(technicians);
        
        request.setAttribute("staffs", allStaff);
        
        // Lấy danh sách bệnh nhân
        List<User> patients = userDAO.getPatientList();
        request.setAttribute("patients", patients != null ? patients : new ArrayList<>());
        
        request.getRequestDispatcher("pages/admin/users.jsp").forward(request, response);
    }

    private void handleUpdateRole(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String userIdStr = request.getParameter("userId");
        String roleStr = request.getParameter("role");
        
        if (userIdStr == null || userIdStr.trim().isEmpty() || 
            roleStr == null || roleStr.trim().isEmpty()) {
            request.setAttribute("error", "Thông tin không hợp lệ");
            loadUsers(request, response);
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdStr);
            Role role;
            
            if ("receptionist".equals(roleStr)) {
                role = Role.receptionist;
            } else if ("technician".equals(roleStr)) {
                role = Role.technician;
            } else {
                role = Role.receptionist;
            }
            
            UserDAO userDAO = new UserDAO();
            userDAO.updateUserRole(userId, role);
            request.setAttribute("success", "Cập nhật vai trò thành công");
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi khi cập nhật vai trò: " + e.getMessage());
        }
        
        loadUsers(request, response);
    }

    private void handleSearch(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String tab = request.getParameter("tab");
        
        if (keyword == null) keyword = "";
        if (tab == null) tab = "staff";
        
        UserDAO userDAO = new UserDAO();
        
        if ("staff".equals(tab)) {
            List<User> receptionists = userDAO.searchUsers(keyword, Role.receptionist);
            List<User> technicians = userDAO.searchUsers(keyword, Role.technician);
            
            List<User> allStaff = new ArrayList<>();
            if (receptionists != null) allStaff.addAll(receptionists);
            if (technicians != null) allStaff.addAll(technicians);
            
            request.setAttribute("staffs", allStaff);
            request.setAttribute("patients", new ArrayList<>());
        } else {
            List<User> patients = userDAO.searchUsers(keyword, Role.patient);
            request.setAttribute("patients", patients != null ? patients : new ArrayList<>());
            request.setAttribute("staffs", new ArrayList<>());
        }
        
        request.setAttribute("currentTab", tab);
        request.setAttribute("searchKeyword", keyword);
        request.getRequestDispatcher("pages/admin/users.jsp").forward(request, response);
    }

    private void handleFilter(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String statusStr = request.getParameter("status");
        String tab = request.getParameter("tab");
        
        if (statusStr == null || statusStr.isEmpty()) statusStr = "all";
        if (tab == null) tab = "staff";
        
        UserDAO userDAO = new UserDAO();
        
        if ("all".equals(statusStr)) {
            loadUsers(request, response);
            return;
        }
        
        Status status = Status.valueOf(statusStr);
        
        if ("staff".equals(tab)) {
            List<User> receptionists = userDAO.getUsersByStatus(Role.receptionist, status);
            List<User> technicians = userDAO.getUsersByStatus(Role.technician, status);
            
            List<User> allStaff = new ArrayList<>();
            if (receptionists != null) allStaff.addAll(receptionists);
            if (technicians != null) allStaff.addAll(technicians);
            
            request.setAttribute("staffs", allStaff);
            request.setAttribute("patients", new ArrayList<>());
        } else {
            List<User> patients = userDAO.getUsersByStatus(Role.patient, status);
            request.setAttribute("patients", patients != null ? patients : new ArrayList<>());
            request.setAttribute("staffs", new ArrayList<>());
        }
        
        request.setAttribute("currentTab", tab);
        request.setAttribute("filterStatus", statusStr);
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
