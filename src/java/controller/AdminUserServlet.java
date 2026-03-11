package controller;

import dal.UserDAO;
import dal.DoctorDAO;
import util.SystemLogService;
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

    private static final int PAGE_SIZE = 10;

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
        String tab = request.getParameter("tab");

        try {
            if ("add".equals(action)) {
                handleAddUser(request, response);
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
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String roleStr = request.getParameter("role");

        // Validation
        if (fullName == null || fullName.trim().isEmpty()
                || phone == null || phone.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || roleStr == null || roleStr.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng điền đầy đủ thông tin");
            request.setAttribute("addModalOpen", true);
            request.setAttribute("addModalType", "patient".equals(roleStr) ? "patient" : "staff");
            request.setAttribute("addFullName", fullName);
            request.setAttribute("addPhone", phone);
            request.setAttribute("addEmail", email);
            loadUsers(request, response);
            return;
        }
        UserDAO userDAO = new UserDAO();

        // Kiểm tra số điện thoại đã tồn tại
        if (userDAO.isPhoneExist(phone)) {
            request.setAttribute("error", "Số điện thoại này đã tồn tại");
            request.setAttribute("addModalOpen", true);
            request.setAttribute("addModalType", "patient".equals(roleStr) ? "patient" : "staff");
            request.setAttribute("addFullName", fullName);
            request.setAttribute("addPhone", phone);
            request.setAttribute("addEmail", email);
            request.setAttribute("addPhoneError", "Số điện thoại này đã tồn tại");
            loadUsers(request, response);
            return;
        }

        if (userDAO.isEmailExist(email)) {
            request.setAttribute("error", "Email này đã tồn tại");
            request.setAttribute("addModalOpen", true);
            request.setAttribute("addModalType", "patient".equals(roleStr) ? "patient" : "staff");
            request.setAttribute("addFullName", fullName);
            request.setAttribute("addPhone", phone);
            request.setAttribute("addEmail", email);
            request.setAttribute("addEmailError", "Email này đã tồn tại");
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
            newUser.setEmail(email);
            newUser.setPasswordHash(password);
            newUser.setRole(role);
            newUser.setStatus(Status.active);

            userDAO.createUser(newUser);
            if (role == Role.doctor) {
                new DoctorDAO().syncDoctorProfilesForAllDoctorUsers();
            }
            request.setAttribute("success", "Tạo tài khoản thành công");

            // Ghi system log
            HttpSession session = request.getSession(false);
            SystemLogService.logWithSession(session, "CREATE_USER",
                    "Tạo tài khoản mới: " + fullName + " (" + email + "), role=" + role.name());
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi khi tạo tài khoản: " + e.getMessage());
        }

        loadUsers(request, response);
    }

    private void handleEditUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String userIdStr = request.getParameter("userId");
        String editType = request.getParameter("editType");
        String fullName = request.getParameter("fullname");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String statusStr = request.getParameter("status");

        if (userIdStr == null || userIdStr.trim().isEmpty()
                || fullName == null || fullName.trim().isEmpty()
                || phone == null || phone.trim().isEmpty()
                || email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng điền đầy đủ thông tin");
            request.setAttribute("editModalOpen", true);
            request.setAttribute("editModalType", "patient".equals(editType) ? "patient" : "staff");
            request.setAttribute("editUserId", userIdStr);
            request.setAttribute("editFullName", fullName);
            request.setAttribute("editPhone", phone);
            request.setAttribute("editEmail", email);
            request.setAttribute("editStatusValue", statusStr != null ? statusStr : "active");
            loadUsers(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();

        try {
            int userId = Integer.parseInt(userIdStr);

            User phoneOwner = userDAO.getUserByPhone(phone);
            if (phoneOwner != null && phoneOwner.getUserId() != userId) {
                request.setAttribute("error", "Số điện thoại này đã tồn tại");
                request.setAttribute("editModalOpen", true);
                request.setAttribute("editModalType", "patient".equals(editType) ? "patient" : "staff");
                request.setAttribute("editUserId", userIdStr);
                request.setAttribute("editFullName", fullName);
                request.setAttribute("editPhone", phone);
                request.setAttribute("editEmail", email);
                request.setAttribute("editStatusValue", statusStr != null ? statusStr : "active");
                request.setAttribute("editPhoneError", "Số điện thoại này đã tồn tại");
                loadUsers(request, response);
                return;
            }

            User emailOwner = userDAO.getUserByEmail(email);
            if (emailOwner != null && emailOwner.getUserId() != userId) {
                request.setAttribute("error", "Email này đã tồn tại");
                request.setAttribute("editModalOpen", true);
                request.setAttribute("editModalType", "patient".equals(editType) ? "patient" : "staff");
                request.setAttribute("editUserId", userIdStr);
                request.setAttribute("editFullName", fullName);
                request.setAttribute("editPhone", phone);
                request.setAttribute("editEmail", email);
                request.setAttribute("editStatusValue", statusStr != null ? statusStr : "active");
                request.setAttribute("editEmailError", "Email này đã tồn tại");
                loadUsers(request, response);
                return;
            }

            User user = new User();
            user.setUserId(userId);
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setEmail(email);
            user.setStatus(Status.valueOf(statusStr != null ? statusStr : "active"));

            userDAO.updateUser(user);
            request.setAttribute("success", "Cập nhật tài khoản thành công");

            // Ghi system log
            HttpSession session = request.getSession(false);
            SystemLogService.logWithSession(session, "UPDATE_USER",
                    "Cập nhật tài khoản userId=" + userId + ", fullName=" + fullName + ", status=" + user.getStatus());
        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi khi cập nhật thông tin: " + e.getMessage());
            request.setAttribute("editModalOpen", true);
            request.setAttribute("editModalType", "patient".equals(editType) ? "patient" : "staff");
            request.setAttribute("editUserId", userIdStr);
            request.setAttribute("editFullName", fullName);
            request.setAttribute("editPhone", phone);
            request.setAttribute("editEmail", email);
            request.setAttribute("editStatusValue", statusStr != null ? statusStr : "active");
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi khi câp nhật: " + e.getMessage());
            request.setAttribute("editModalOpen", true);
            request.setAttribute("editModalType", "patient".equals(editType) ? "patient" : "staff");
            request.setAttribute("editUserId", userIdStr);
            request.setAttribute("editFullName", fullName);
            request.setAttribute("editPhone", phone);
            request.setAttribute("editEmail", email);
            request.setAttribute("editStatusValue", statusStr != null ? statusStr : "active");
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
            User user = userDAO.getUserByPhone(phone);
            if (user == null) {
                request.setAttribute("error", "Không tìm thấy người dùng");
                loadUsers(request, response);
                return;
            }

            userDAO.toggleUserStatus(phone);
            request.setAttribute("success", "Cập nhật trạng thái của " + user.getFullName() + " thành công");

            // Ghi system log
            HttpSession session = request.getSession(false);
            SystemLogService.logWithSession(session, "TOGGLE_USER_STATUS",
                    "Thay đổi trạng thái tài khoản: " + user.getFullName() + " (" + user.getEmail() + ")");
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }

        loadUsers(request, response);
    }

    private void loadUsers(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String tab = request.getParameter("tab");
        if (tab == null) {
            tab = "staff";
        }

        UserDAO userDAO = new UserDAO();

        // Lấy danh sách tất cả nhân viên và admin
        List<User> allStaff = userDAO.getAllStaffAndAdmin();

        // Lấy danh sách bệnh nhân
        List<User> patients = userDAO.getPatientList();

        applyPaging(request, tab, allStaff, patients);
        request.setAttribute("currentTab", tab);
        request.setAttribute("currentAction", "list");
        request.setAttribute("filterRole", "all");
        request.setAttribute("filterStatus", "all");
        request.setAttribute("filterPatientStatus", "all");
        request.setAttribute("searchKeyword", "");

        request.getRequestDispatcher("pages/admin/users.jsp").forward(request, response);
    }

    private void handleUpdateRole(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String userIdStr = request.getParameter("userId");
        String roleStr = request.getParameter("role");

        if (userIdStr == null || userIdStr.trim().isEmpty()
                || roleStr == null || roleStr.trim().isEmpty()) {
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

        if (keyword == null) {
            keyword = "";
        }
        if (tab == null) {
            tab = "staff";
        }

        UserDAO userDAO = new UserDAO();

        // Luôn load tất cả data
        List<User> allStaff = userDAO.getAllStaffAndAdmin();
        List<User> patients = userDAO.getPatientList();

        if ("staff".equals(tab) && !keyword.isEmpty()) {
            // Tìm kiếm trong tất cả staff và admin
            List<User> admins = userDAO.searchUsers(keyword, Role.admin);
            List<User> doctors = userDAO.searchUsers(keyword, Role.doctor);
            List<User> receptionists = userDAO.searchUsers(keyword, Role.receptionist);
            List<User> technicians = userDAO.searchUsers(keyword, Role.technician);

            List<User> searchedStaff = new ArrayList<>();
            if (admins != null) {
                searchedStaff.addAll(admins);
            }
            if (doctors != null) {
                searchedStaff.addAll(doctors);
            }
            if (receptionists != null) {
                searchedStaff.addAll(receptionists);
            }
            if (technicians != null) {
                searchedStaff.addAll(technicians);
            }

            allStaff = searchedStaff;
        } else if ("patient".equals(tab) && !keyword.isEmpty()) {
            List<User> searchedPatients = userDAO.searchUsers(keyword, Role.patient);
            patients = searchedPatients != null ? searchedPatients : new ArrayList<>();
        } 

        applyPaging(request, tab, allStaff, patients);
        request.setAttribute("currentTab", tab);
        request.setAttribute("currentAction", "search");
        request.setAttribute("searchKeyword", keyword);
        request.setAttribute("filterRole", "all");
        request.setAttribute("filterStatus", "all");
        request.setAttribute("filterPatientStatus", "all");
        request.getRequestDispatcher("pages/admin/users.jsp").forward(request, response);
    }

    private void handleFilter(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String statusStr = request.getParameter("status");
        String roleStr = request.getParameter("role");
        String tab = request.getParameter("tab");

        if (statusStr == null || statusStr.isEmpty()) {
            statusStr = "all";
        }
        if (roleStr == null || roleStr.isEmpty()) {
            roleStr = "all";
        }
        if (tab == null) {
            tab = "staff";
        }

        UserDAO userDAO = new UserDAO();

        // Luôn load tất cả data
        List<User> allStaff = userDAO.getAllStaffAndAdmin();
        List<User> patients = userDAO.getPatientList();

        if ("staff".equals(tab)) {
            // Filter staff
            if (!"all".equals(statusStr) || !"all".equals(roleStr)) {
                List<User> filteredStaff = new ArrayList<>();

                if ("all".equals(statusStr)) {
                    // Chỉ filter theo role
                    if ("admin".equals(roleStr)) {
                        filteredStaff = userDAO.getUsersByRole(Role.admin);
                    } else if ("doctor".equals(roleStr)) {
                        filteredStaff = userDAO.getUsersByRole(Role.doctor);
                    } else if ("receptionist".equals(roleStr)) {
                        filteredStaff = userDAO.getUsersByRole(Role.receptionist);
                    } else if ("technician".equals(roleStr)) {
                        filteredStaff = userDAO.getUsersByRole(Role.technician);
                    } else {
                        filteredStaff = allStaff;
                    }
                } else if ("all".equals(roleStr)) {
                    // Chỉ filter theo status
                    Status status = Status.valueOf(statusStr);
                    List<User> admins = userDAO.getUsersByRoleAndStatus(Role.admin, status);
                    List<User> doctors = userDAO.getUsersByRoleAndStatus(Role.doctor, status);
                    List<User> receptionists = userDAO.getUsersByRoleAndStatus(Role.receptionist, status);
                    List<User> technicians = userDAO.getUsersByRoleAndStatus(Role.technician, status);

                    filteredStaff.addAll(admins);
                    filteredStaff.addAll(doctors);
                    filteredStaff.addAll(receptionists);
                    filteredStaff.addAll(technicians);
                } else {
                    // Filter cả role và status
                    Status status = Status.valueOf(statusStr);
                    Role role = Role.valueOf(roleStr);
                    filteredStaff = userDAO.getUsersByRoleAndStatus(role, status);
                }

                allStaff = filteredStaff;
            }
        } else {
            // Filter patients
            if (!"all".equals(statusStr)) {
                Status status = Status.valueOf(statusStr);
                List<User> filteredPatients = userDAO.getUsersByRoleAndStatus(Role.patient, status);
                patients = filteredPatients != null ? filteredPatients : new ArrayList<>();
            } 
        }

        applyPaging(request, tab, allStaff, patients);
        request.setAttribute("currentTab", tab);
        request.setAttribute("currentAction", "filter");
        if ("staff".equals(tab)) {
            request.setAttribute("filterRole", roleStr);
            request.setAttribute("filterStatus", statusStr);
            request.setAttribute("filterPatientStatus", "all");
        } else {
            request.setAttribute("filterRole", "all");
            request.setAttribute("filterStatus", "all");
            request.setAttribute("filterPatientStatus", statusStr);
        }
        request.setAttribute("searchKeyword", "");
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

    private int parsePage(String pageParam, int defaultValue) {
        try {
            int page = Integer.parseInt(pageParam);
            return page < 1 ? 1 : page;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private int calculateTotalPages(int totalRecords, int pageSize) {
        if (totalRecords <= 0 || pageSize <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalRecords / pageSize);
    }

    private <T> List<T> paginate(List<T> data, int page, int pageSize) {
        if (data == null || data.isEmpty()) {
            return new ArrayList<>();
        }

        int from = (page - 1) * pageSize;
        if (from < 0 || from >= data.size()) {
            return new ArrayList<>();
        }
        int to = Math.min(from + pageSize, data.size());
        return data.subList(from, to);
    }

    private void applyPaging(HttpServletRequest request, String tab, List<User> allStaff, List<User> patients) {
        List<User> safeStaff = allStaff != null ? allStaff : new ArrayList<>();
        List<User> safePatients = patients != null ? patients : new ArrayList<>();

        int pageFallback = parsePage(request.getParameter("page"), 1);
        int staffPage = parsePage(request.getParameter("staffPage"), "staff".equals(tab) ? pageFallback : 1);
        int patientPage = parsePage(request.getParameter("patientPage"), "patient".equals(tab) ? pageFallback : 1);

        int staffTotalRecords = safeStaff.size();
        int staffTotalPages = calculateTotalPages(staffTotalRecords, PAGE_SIZE);
        if (staffTotalPages == 0) {
            staffPage = 1;
        } else if (staffPage > staffTotalPages) {
            staffPage = staffTotalPages;
        }

        int patientTotalRecords = safePatients.size();
        int patientTotalPages = calculateTotalPages(patientTotalRecords, PAGE_SIZE);
        if (patientTotalPages == 0) {
            patientPage = 1;
        } else if (patientPage > patientTotalPages) {
            patientPage = patientTotalPages;
        }

        request.setAttribute("allStaff", paginate(safeStaff, staffPage, PAGE_SIZE));
        request.setAttribute("patients", paginate(safePatients, patientPage, PAGE_SIZE));

        request.setAttribute("staffCurrentPage", staffPage);
        request.setAttribute("staffTotalPages", staffTotalPages);
        request.setAttribute("staffTotalRecords", staffTotalRecords);

        request.setAttribute("patientCurrentPage", patientPage);
        request.setAttribute("patientTotalPages", patientTotalPages);
        request.setAttribute("patientTotalRecords", patientTotalRecords);

        request.setAttribute("pageSize", PAGE_SIZE);
    }
}
