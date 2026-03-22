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
import java.util.Arrays;
import java.util.regex.Pattern;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


public class AdminUserServlet extends HttpServlet {

    private static final int PAGE_SIZE = 10;
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_EMAIL_LENGTH = 100;
    private static final int MIN_EXPERIENCE = 0;
    private static final int MAX_EXPERIENCE = 50;
    private static final int MIN_PRICE = 0;
    private static final int MAX_PRICE = 10_000_000;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^0\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern HAS_LETTER_PATTERN = Pattern.compile(".*\\p{L}+.*");
    private static final Pattern ONLY_NUMBER_OR_SYMBOL_PATTERN = Pattern.compile("^[\\d\\p{Punct}\\s]+$");

    private static final List<String> SPECIALIZATION_OPTIONS = Arrays.asList(
            "Da liễu dị ứng",
            "Da liễu nhiễm trùng",
            "Da liễu tổng quát",
            "Điều trị mụn"
    );
    private static final List<String> QUALIFICATION_OPTIONS = Arrays.asList(
            "Giáo sư / Phó Giáo sư",
            "Tiến sĩ / Bác sĩ CK II",
            "Thạc sĩ / Bác sĩ CK I / BS nội trú"
    );

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
            request.setAttribute("error", "Lỗi cơ sở dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("pages/admin/users.jsp").forward(request, response);
        }
    }

    private void handleAddUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String fullName = trim(request.getParameter("fullname"));
        String phone = trim(request.getParameter("phone"));
        String email = trim(request.getParameter("email"));
        String password = trim(request.getParameter("password"));
        String roleStr = trim(request.getParameter("role"));
        String specialization = trim(request.getParameter("doctorSpecialization"));
        String qualification = trim(request.getParameter("doctorQualification"));
        String experienceRaw = trim(request.getParameter("doctorExperienceYears"));
        String priceRaw = trim(request.getParameter("doctorPriceBooking"));

        Role targetRole = parseRole(roleStr);
        if (targetRole == null || targetRole == Role.admin) {
            keepAddForm(request, fullName, phone, email, roleStr,
                    specialization, qualification, experienceRaw, priceRaw);
            request.setAttribute("error", "Vai trò không hợp lệ");
            request.setAttribute("addRoleError", "Vai trò không hợp lệ");
            loadUsers(request, response);
            return;
        }

        boolean valid = validateUserCommonFields(request, "add", fullName, phone, email);
        if (password.isEmpty()) {
            request.setAttribute("addPasswordError", "Mật khẩu không được để trống");
            valid = false;
        }

        DoctorTransitionData doctorData = null;
        if (targetRole == Role.doctor) {
            doctorData = validateDoctorTransitionFields(request, "add",
                    specialization, qualification, experienceRaw, priceRaw);
            if (!doctorData.valid) {
                valid = false;
            }
        }

        UserDAO userDAO = new UserDAO();
        if (valid && userDAO.isPhoneExist(phone)) {
            request.setAttribute("addPhoneError", "Số điện thoại này đã tồn tại");
            valid = false;
        }
        if (valid && userDAO.isEmailExist(email)) {
            request.setAttribute("addEmailError", "Email này đã tồn tại");
            valid = false;
        }

        if (!valid) {
            keepAddForm(request, fullName, phone, email, roleStr,
                    specialization, qualification, experienceRaw, priceRaw);
            request.setAttribute("error", "Dữ liệu tạo tài khoản không hợp lệ");
            loadUsers(request, response);
            return;
        }

        try {
            User newUser = new User();
            newUser.setFullName(fullName);
            newUser.setPhone(phone);
            newUser.setEmail(email);
            newUser.setPasswordHash(password);
            newUser.setRole(targetRole);
            newUser.setStatus(Status.active);

            userDAO.createUser(newUser);
            if (targetRole == Role.doctor && doctorData != null && doctorData.valid) {
                User createdUser = userDAO.getUserByEmail(email);
                if (createdUser == null || createdUser.getUserId() <= 0) {
                    throw new SQLException("Không tìm thấy tài khoản vừa tạo để cập nhật hồ sơ bác sĩ");
                }
                new DoctorDAO().upsertDoctorProfileByUserId(
                        createdUser.getUserId(),
                        doctorData.specialization,
                        doctorData.qualification,
                        doctorData.experienceYears,
                        doctorData.priceBooking
                );
            }
            request.setAttribute("success", "Tạo tài khoản thành công");

            // Ghi system log
            HttpSession session = request.getSession(false);
            SystemLogService.logWithSession(session, "CREATE_USER",
                    "Tạo tài khoản: " + fullName + " (" + email + "), role=" + targetRole.name());
        } catch (Exception e) {
            keepAddForm(request, fullName, phone, email, roleStr,
                    specialization, qualification, experienceRaw, priceRaw);
            request.setAttribute("error", "Lỗi khi tạo tài khoản: " + e.getMessage());
            request.setAttribute("addModalOpen", true);
        }

        loadUsers(request, response);
    }

    private void handleEditUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String userIdStr = trim(request.getParameter("userId"));
        String editType = trim(request.getParameter("editType"));
        String fullName = trim(request.getParameter("fullname"));
        String phone = trim(request.getParameter("phone"));
        String email = trim(request.getParameter("email"));
        String roleStr = trim(request.getParameter("role"));

        String specialization = trim(request.getParameter("doctorSpecialization"));
        String qualification = trim(request.getParameter("doctorQualification"));
        String experienceRaw = trim(request.getParameter("doctorExperienceYears"));
        String priceRaw = trim(request.getParameter("doctorPriceBooking"));

        int userId = parsePositiveId(userIdStr);
        if (userId <= 0) {
            request.setAttribute("error", "Người dùng không hợp lệ");
            loadUsers(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();
        DoctorDAO doctorDAO = new DoctorDAO();
        User existingUser = userDAO.getUserById(userId);
        if (existingUser == null) {
            request.setAttribute("error", "Không tìm thấy người dùng");
            loadUsers(request, response);
            return;
        }
        if (existingUser.getRole() == Role.admin) {
            request.setAttribute("error", "Không được chỉnh sửa tài khoản admin");
            loadUsers(request, response);
            return;
        }

        Role targetRole = parseRole(roleStr);
        if (targetRole == null || targetRole == Role.admin) {
            keepEditForm(request, userIdStr, editType, fullName, phone, email, roleStr,
                    specialization, qualification, experienceRaw, priceRaw);
            request.setAttribute("error", "Vai trò không hợp lệ");
            request.setAttribute("editRoleError", "Vai trò không hợp lệ");
            loadUsers(request, response);
            return;
        }

        boolean valid = validateUserCommonFields(request, "edit", fullName, phone, email);

        User phoneOwner = userDAO.getUserByPhone(phone);
        if (valid && phoneOwner != null && phoneOwner.getUserId() != userId) {
            request.setAttribute("editPhoneError", "Số điện thoại này đã tồn tại");
            valid = false;
        }

        User emailOwner = userDAO.getUserByEmail(email);
        if (valid && emailOwner != null && emailOwner.getUserId() != userId) {
            request.setAttribute("editEmailError", "Email này đã tồn tại");
            valid = false;
        }

        if (existingUser.getRole() == Role.doctor && targetRole != Role.doctor
                && doctorDAO.hasFutureUnfinishedAppointmentsByUserId(userId)) {
            request.setAttribute("error", "Không thể đổi vai trò bác sĩ khi vẫn còn lịch khám tương lai chưa hoàn tất");
            valid = false;
        }

        DoctorTransitionData doctorData = null;
        if (existingUser.getRole() != Role.doctor && targetRole == Role.doctor) {
            doctorData = validateDoctorTransitionFields(request, "edit", specialization, qualification, experienceRaw, priceRaw);
            if (!doctorData.valid) {
                valid = false;
            }
        }

        if (!valid) {
            keepEditForm(request, userIdStr, editType, fullName, phone, email, roleStr,
                    specialization, qualification, experienceRaw, priceRaw);
            request.setAttribute("error", "Dữ liệu cập nhật không hợp lệ");
            loadUsers(request, response);
            return;
        }

        try {
            User user = new User();
            user.setUserId(userId);
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setEmail(email);
            userDAO.updateUser(user);

            if (targetRole != existingUser.getRole()) {
                userDAO.updateUserRole(userId, targetRole);
            }

            if (existingUser.getRole() != Role.doctor && targetRole == Role.doctor && doctorData != null && doctorData.valid) {
                doctorDAO.upsertDoctorProfileByUserId(
                        userId,
                        doctorData.specialization,
                        doctorData.qualification,
                        doctorData.experienceYears,
                        doctorData.priceBooking
                );
            }

            if (targetRole == Role.doctor || existingUser.getRole() == Role.doctor) {
                doctorDAO.syncDoctorProfilesForAllDoctorUsers();
            }

            request.setAttribute("success", "Cập nhật tài khoản thành công");

            // Ghi system log
            HttpSession session = request.getSession(false);
            SystemLogService.logWithSession(session, "UPDATE_USER",
                    "Cập nhật tài khoản userId=" + userId + ", fullName=" + fullName + ", role=" + targetRole.name());
        } catch (SQLException e) {
            keepEditForm(request, userIdStr, editType, fullName, phone, email, roleStr,
                    specialization, qualification, experienceRaw, priceRaw);
            request.setAttribute("error", "Lỗi khi cập nhật thông tin: " + e.getMessage());
            request.setAttribute("editModalOpen", true);
        } catch (Exception e) {
            keepEditForm(request, userIdStr, editType, fullName, phone, email, roleStr,
                    specialization, qualification, experienceRaw, priceRaw);
            request.setAttribute("error", "Lỗi khi cập nhật: " + e.getMessage());
            request.setAttribute("editModalOpen", true);
        }

        loadUsers(request, response);
    }

    private void handleToggleStatus(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String userIdStr = trim(request.getParameter("userId"));
        int userId = parsePositiveId(userIdStr);
        if (userId <= 0) {
            request.setAttribute("error", "Người dùng không hợp lệ");
            loadUsers(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();
        try {
            User user = userDAO.getUserById(userId);
            if (user == null) {
                request.setAttribute("error", "Không tìm thấy người dùng");
                loadUsers(request, response);
                return;
            }

            if (user.getRole() == Role.admin) {
                request.setAttribute("error", "Không được thay đổi trạng thái tài khoản admin");
                loadUsers(request, response);
                return;
            }

            userDAO.toggleUserStatusById(userId);
            request.setAttribute("success", "Cập nhật trạng thái của " + user.getFullName() + " thanh cong");

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
        UserDAO userDAO = new UserDAO();
        List<User> users = getUsersByRoleAndKeyword(userDAO, "all", "");

        applyPaging(request, users);
        request.setAttribute("currentAction", "list");
        request.setAttribute("filterRole", "all");
        request.setAttribute("filterStatus", "all");
        request.setAttribute("searchKeyword", "");

        request.getRequestDispatcher("pages/admin/users.jsp").forward(request, response);
    }

    private void handleUpdateRole(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String userIdStr = trim(request.getParameter("userId"));
        String roleStr = trim(request.getParameter("role"));
        String specialization = trim(request.getParameter("doctorSpecialization"));
        String qualification = trim(request.getParameter("doctorQualification"));
        String experienceRaw = trim(request.getParameter("doctorExperienceYears"));
        String priceRaw = trim(request.getParameter("doctorPriceBooking"));

        int userId = parsePositiveId(userIdStr);
        if (userId <= 0) {
            request.setAttribute("error", "Người dùng không hợp lệ");
            loadUsers(request, response);
            return;
        }

        Role role = parseRole(roleStr);
        if (role == null || role == Role.admin) {
            request.setAttribute("error", "Vai trò không hợp lệ");
            loadUsers(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();
        DoctorDAO doctorDAO = new DoctorDAO();
        User existingUser = userDAO.getUserById(userId);
        if (existingUser == null) {
            request.setAttribute("error", "Không tìm thấy người dùng");
            loadUsers(request, response);
            return;
        }
        if (existingUser.getRole() == Role.admin) {
            request.setAttribute("error", "Không được đổi vai trò của admin");
            loadUsers(request, response);
            return;
        }
        if (existingUser.getRole() == Role.doctor && role != Role.doctor
                && doctorDAO.hasFutureUnfinishedAppointmentsByUserId(userId)) {
            request.setAttribute("error", "Không thể đổi vai trò bác sĩ khi vẫn còn lịch khám tương lai chưa hoàn tất");
            loadUsers(request, response);
            return;
        }

        DoctorTransitionData doctorData = null;
        if (existingUser.getRole() != Role.doctor && role == Role.doctor) {
            doctorData = validateDoctorTransitionFields(request, "edit", specialization, qualification, experienceRaw, priceRaw);
            if (!doctorData.valid) {
                request.setAttribute("error", "Cần hoàn thiện thông tin bác sĩ trước khi đổi vai trò");
                loadUsers(request, response);
                return;
            }
        }

        try {
            userDAO.updateUserRole(userId, role);
            if (existingUser.getRole() != Role.doctor && role == Role.doctor && doctorData != null && doctorData.valid) {
                doctorDAO.upsertDoctorProfileByUserId(
                        userId,
                        doctorData.specialization,
                        doctorData.qualification,
                        doctorData.experienceYears,
                        doctorData.priceBooking
                );
            }
            if (role == Role.doctor || existingUser.getRole() == Role.doctor) {
                doctorDAO.syncDoctorProfilesForAllDoctorUsers();
            }
            request.setAttribute("success", "Cập nhật vai trò thành công");
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi khi cập nhật vai trò: " + e.getMessage());
        }

        loadUsers(request, response);
    }

    private void handleSearch(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String roleStr = request.getParameter("role");
        String statusStr = request.getParameter("status");

        if (keyword == null) {
            keyword = "";
        }
        if (roleStr == null || roleStr.isEmpty()) {
            roleStr = "all";
        }
        if (statusStr == null || statusStr.isEmpty()) {
            statusStr = "all";
        }

        UserDAO userDAO = new UserDAO();
        List<User> users = getUsersByRoleAndKeyword(userDAO, roleStr, keyword);
        users = filterByStatus(users, statusStr);

        applyPaging(request, users);
        request.setAttribute("currentAction", "search");
        request.setAttribute("searchKeyword", keyword);
        request.setAttribute("filterRole", roleStr);
        request.setAttribute("filterStatus", statusStr);
        request.getRequestDispatcher("pages/admin/users.jsp").forward(request, response);
    }

    private void handleFilter(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String statusStr = request.getParameter("status");
        String roleStr = request.getParameter("role");
        String keyword = request.getParameter("keyword");

        if (statusStr == null || statusStr.isEmpty()) {
            statusStr = "all";
        }
        if (roleStr == null || roleStr.isEmpty()) {
            roleStr = "all";
        }
        if (keyword == null) {
            keyword = "";
        }

        UserDAO userDAO = new UserDAO();
        List<User> users = getUsersByRoleAndKeyword(userDAO, roleStr, keyword);
        users = filterByStatus(users, statusStr);

        applyPaging(request, users);
        request.setAttribute("currentAction", "filter");
        request.setAttribute("filterRole", roleStr);
        request.setAttribute("filterStatus", statusStr);
        request.setAttribute("searchKeyword", keyword);
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

    private void applyPaging(HttpServletRequest request, List<User> users) {
        List<User> safeUsers = users != null ? users : new ArrayList<>();

        int currentPage = parsePage(request.getParameter("page"), 1);
        int totalRecords = safeUsers.size();
        int totalPages = calculateTotalPages(totalRecords, PAGE_SIZE);

        if (totalPages == 0) {
            currentPage = 1;
        } else if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        request.setAttribute("users", paginate(safeUsers, currentPage, PAGE_SIZE));
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("pageSize", PAGE_SIZE);
    }

    private List<User> getUsersByRoleAndKeyword(UserDAO userDAO, String roleStr, String keyword) {
        List<User> users = new ArrayList<>();
        String safeRole = roleStr != null ? roleStr : "all";
        String safeKeyword = keyword != null ? keyword.trim() : "";
        boolean hasKeyword = !safeKeyword.isEmpty();

        if ("all".equals(safeRole)) {
            users.addAll(getUsersByRoleWithKeyword(userDAO, Role.admin, safeKeyword, hasKeyword));
            users.addAll(getUsersByRoleWithKeyword(userDAO, Role.doctor, safeKeyword, hasKeyword));
            users.addAll(getUsersByRoleWithKeyword(userDAO, Role.receptionist, safeKeyword, hasKeyword));
            users.addAll(getUsersByRoleWithKeyword(userDAO, Role.technician, safeKeyword, hasKeyword));
            users.addAll(getUsersByRoleWithKeyword(userDAO, Role.patient, safeKeyword, hasKeyword));
            return users;
        }

        try {
            Role role = Role.valueOf(safeRole);
            users.addAll(getUsersByRoleWithKeyword(userDAO, role, safeKeyword, hasKeyword));
        } catch (Exception e) {
            users.addAll(getUsersByRoleAndKeyword(userDAO, "all", safeKeyword));
        }
        return users;
    }

    private List<User> getUsersByRoleWithKeyword(UserDAO userDAO, Role role, String keyword, boolean hasKeyword) {
        List<User> data;
        if (hasKeyword) {
            data = userDAO.searchUsers(keyword, role);
        } else {
            data = userDAO.getUsersByRole(role);
        }
        return data != null ? data : new ArrayList<>();
    }

    private List<User> filterByStatus(List<User> users, String statusStr) {
        List<User> safeUsers = users != null ? users : new ArrayList<>();
        if (statusStr == null || "all".equals(statusStr)) {
            return safeUsers;
        }

        List<User> filteredUsers = new ArrayList<>();
        try {
            Status status = Status.valueOf(statusStr);
            for (User user : safeUsers) {
                if (user != null && user.getStatus() == status) {
                    filteredUsers.add(user);
                }
            }
        } catch (Exception e) {
            return safeUsers;
        }
        return filteredUsers;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private int parsePositiveId(String raw) {
        try {
            int id = Integer.parseInt(trim(raw));
            return id > 0 ? id : -1;
        } catch (Exception e) {
            return -1;
        }
    }

    private Role parseRole(String roleStr) {
        String safeRole = trim(roleStr).toLowerCase();
        if ("doctor".equals(safeRole)) {
            return Role.doctor;
        }
        if ("receptionist".equals(safeRole)) {
            return Role.receptionist;
        }
        if ("technician".equals(safeRole)) {
            return Role.technician;
        }
        if ("patient".equals(safeRole)) {
            return Role.patient;
        }
        return null;
    }

    private boolean validateUserCommonFields(HttpServletRequest request, String prefix,
            String fullName, String phone, String email) {
        boolean valid = true;

        if (fullName.isEmpty()) {
            request.setAttribute(prefix + "FullNameError", "Họ tên không được để trống");
            valid = false;
        } else if (fullName.length() < MIN_NAME_LENGTH || fullName.length() > MAX_NAME_LENGTH) {
            request.setAttribute(prefix + "FullNameError", "Họ tên phải từ 2 đến 100 ký tự");
            valid = false;
        } else if (!isMeaningfulFullName(fullName)) {
            request.setAttribute(prefix + "FullNameError", "Họ tên không hợp lệ");
            valid = false;
        }

        if (phone.isEmpty()) {
            request.setAttribute(prefix + "PhoneError", "Số điện thoại không được để trống");
            valid = false;
        } else if (!isValidPhone(phone)) {
            request.setAttribute(prefix + "PhoneError", "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0");
            valid = false;
        }

        if (email.isEmpty()) {
            request.setAttribute(prefix + "EmailError", "Email không được để trống");
            valid = false;
        } else if (email.length() > MAX_EMAIL_LENGTH) {
            request.setAttribute(prefix + "EmailError", "Email tối đa 100 ký tự");
            valid = false;
        } else if (!isValidEmail(email)) {
            request.setAttribute(prefix + "EmailError", "Email không đúng định dạng");
            valid = false;
        }
        return valid;
    }

    private boolean isValidPhone(String phone) {
        return PHONE_PATTERN.matcher(phone).matches();
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isMeaningfulFullName(String fullName) {
        if (!HAS_LETTER_PATTERN.matcher(fullName).matches()) {
            return false;
        }
        return !ONLY_NUMBER_OR_SYMBOL_PATTERN.matcher(fullName).matches();
    }

    private DoctorTransitionData validateDoctorTransitionFields(HttpServletRequest request, String prefix,
            String specialization, String qualification, String experienceRaw, String priceRaw) {
        DoctorTransitionData data = new DoctorTransitionData();
        data.specialization = specialization;
        data.qualification = qualification;
        data.valid = true;

        if (specialization.isEmpty()) {
            request.setAttribute(prefix + "DoctorSpecializationError", "Chuyên môn là bắt buộc");
            data.valid = false;
        } else if (!SPECIALIZATION_OPTIONS.contains(specialization)) {
            request.setAttribute(prefix + "DoctorSpecializationError", "Chuyên môn không hợp lệ");
            data.valid = false;
        }

        if (qualification.isEmpty()) {
            request.setAttribute(prefix + "DoctorQualificationError", "Bằng cấp là bắt buộc");
            data.valid = false;
        } else if (!QUALIFICATION_OPTIONS.contains(qualification)) {
            request.setAttribute(prefix + "DoctorQualificationError", "Bằng cấp không hợp lệ");
            data.valid = false;
        }

        if (experienceRaw.isEmpty()) {
            request.setAttribute(prefix + "DoctorExperienceError", "Kinh nghiệm là bắt buộc");
            data.valid = false;
        } else if (!experienceRaw.matches("\\d+")) {
            request.setAttribute(prefix + "DoctorExperienceError", "Kinh nghiệm phải là số nguyên");
            data.valid = false;
        } else {
            int exp = Integer.parseInt(experienceRaw);
            if (exp < MIN_EXPERIENCE || exp > MAX_EXPERIENCE) {
                request.setAttribute(prefix + "DoctorExperienceError", "Kinh nghiệm phải từ 0 đến 50");
                data.valid = false;
            } else {
                data.experienceYears = exp;
            }
        }

        if (priceRaw.isEmpty()) {
            request.setAttribute(prefix + "DoctorPriceError", "Giá khám là bắt buộc");
            data.valid = false;
        } else if (!priceRaw.matches("\\d+")) {
            request.setAttribute(prefix + "DoctorPriceError", "Giá khám phải là số nguyên không âm");
            data.valid = false;
        } else {
            int price = Integer.parseInt(priceRaw);
            if (price < MIN_PRICE || price > MAX_PRICE) {
                request.setAttribute(prefix + "DoctorPriceError", "Giá khám phải từ 0 đến 10000000");
                data.valid = false;
            } else {
                data.priceBooking = price;
            }
        }

        return data;
    }

    private void keepAddForm(HttpServletRequest request, String fullName, String phone, String email, String role,
            String specialization, String qualification, String experienceRaw, String priceRaw) {
        request.setAttribute("addModalOpen", true);
        request.setAttribute("addRoleValue", role);
        request.setAttribute("addFullName", fullName);
        request.setAttribute("addPhone", phone);
        request.setAttribute("addEmail", email);
        request.setAttribute("addDoctorSpecialization", specialization);
        request.setAttribute("addDoctorQualification", qualification);
        request.setAttribute("addDoctorExperienceYears", experienceRaw);
        request.setAttribute("addDoctorPriceBooking", priceRaw);
    }

    private void keepEditForm(HttpServletRequest request, String userId, String editType, String fullName,
            String phone, String email, String role, String specialization, String qualification,
            String experienceRaw, String priceRaw) {
        request.setAttribute("editModalOpen", true);
        request.setAttribute("editModalType", "patient".equals(editType) ? "patient" : "staff");
        request.setAttribute("editUserId", userId);
        request.setAttribute("editFullName", fullName);
        request.setAttribute("editPhone", phone);
        request.setAttribute("editEmail", email);
        request.setAttribute("editRoleValue", role);
        request.setAttribute("editDoctorSpecialization", specialization);
        request.setAttribute("editDoctorQualification", qualification);
        request.setAttribute("editDoctorExperienceYears", experienceRaw);
        request.setAttribute("editDoctorPriceBooking", priceRaw);
    }

    private static class DoctorTransitionData {

        private boolean valid;
        private String specialization;
        private String qualification;
        private int experienceYears;
        private int priceBooking;
    }
}
