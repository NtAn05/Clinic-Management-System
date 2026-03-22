package controller;

import dal.DoctorDAO;
import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import model.Doctor;
import model.Role;
import model.User;
import util.SystemLogService;

public class AdminDoctorServlet extends HttpServlet {

    private static final String VIEW_PATH = "/pages/admin/doctors.jsp";
    private static final int MAX_SPECIALIZATION_LENGTH = 100;
    private static final int MIN_EXPERIENCE = 0;
    private static final int MAX_EXPERIENCE = 50;
    private static final int MIN_PRICE = 0;
    private static final int MAX_PRICE = 10_000_000;
    private static final List<String> QUALIFICATION_OPTIONS = Arrays.asList(
            "Giáo sư / Phó Giáo sư",
            "Tiến sĩ / Bác sĩ CK II",
            "Thạc sĩ / Bác sĩ CK I / BS nội trú"
    );

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("account") == null) {
            resp.sendRedirect(req.getContextPath() + "/pages/auth/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("account");
        if (user.getRole() != Role.admin) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Chỉ admin mới được truy cập");
            return;
        }

        String action = trim(req.getParameter("action"));
        try {
            if ("add".equals(action) && "POST".equalsIgnoreCase(req.getMethod())) {
                if (handleAdd(req)) {
                    redirectSuccess(resp, req, "Thêm bác sĩ thành công");
                    return;
                }
            } else if ("edit".equals(action) && "POST".equalsIgnoreCase(req.getMethod())) {
                if (handleEdit(req)) {
                    redirectSuccess(resp, req, "Cập nhật bác sĩ thành công");
                    return;
                }
            }
            loadPage(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Lỗi xử lý quản lý bác sĩ: " + e.getMessage());
            loadPage(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    private void loadPage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DoctorDAO doctorDAO = new DoctorDAO();
        String keyword = trim(req.getParameter("keyword"));
        String specialization = trim(req.getParameter("specialization"));
        String qualification = trim(req.getParameter("qualification"));
        String success = trim(req.getParameter("success"));

        List<Doctor> doctors = doctorDAO.getDoctorsForAdmin(keyword, specialization, qualification);
        List<String> specializationOptions = doctorDAO.getDistinctDoctorSpecializations();

        req.setAttribute("doctors", doctors);
        req.setAttribute("specializationOptions", specializationOptions);
        req.setAttribute("qualificationOptions", QUALIFICATION_OPTIONS);
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedSpecialization", specialization);
        req.setAttribute("selectedQualification", qualification);
        if (!success.isEmpty()) {
            req.setAttribute("success", success);
        }

        req.getRequestDispatcher(VIEW_PATH).forward(req, resp);
    }

    private boolean handleAdd(HttpServletRequest req) throws SQLException {
        String fullName = trim(req.getParameter("fullName"));
        String phone = trim(req.getParameter("phone"));
        String email = trim(req.getParameter("email"));
        String password = trim(req.getParameter("password"));
        String specialization = trim(req.getParameter("specialization"));
        String qualification = trim(req.getParameter("qualification"));
        String experienceRaw = trim(req.getParameter("experienceYears"));
        String priceRaw = trim(req.getParameter("priceBooking"));

        keepAddForm(req, fullName, phone, email, specialization, qualification, experienceRaw, priceRaw);

        boolean valid = true;
        if (fullName.isEmpty()) {
            req.setAttribute("addFullNameError", "Họ tên không được để trống");
            valid = false;
        }
        if (phone.isEmpty()) {
            req.setAttribute("addPhoneError", "Số điện thoại không được để trống");
            valid = false;
        }
        if (email.isEmpty()) {
            req.setAttribute("addEmailError", "Email không được để trống");
            valid = false;
        }
        if (password.isEmpty()) {
            req.setAttribute("addPasswordError", "Mật khẩu không được để trống");
            valid = false;
        }
        valid = validateDoctorFields(req, specialization, qualification, experienceRaw, priceRaw, true) && valid;

        UserDAO userDAO = new UserDAO();
        if (valid && userDAO.isPhoneExist(phone)) {
            req.setAttribute("addPhoneError", "Số điện thoại đã tồn tại");
            valid = false;
        }
        if (valid && userDAO.isEmailExist(email)) {
            req.setAttribute("addEmailError", "Email đã tồn tại");
            valid = false;
        }

        if (!valid) {
            req.setAttribute("error", "Dữ liệu thêm bác sĩ không hợp lệ");
            req.setAttribute("addModalOpen", true);
            return false;
        }

        int experienceYears = Integer.parseInt(experienceRaw);
        int priceBooking = Integer.parseInt(priceRaw);

        DoctorDAO doctorDAO = new DoctorDAO();
        int userId = doctorDAO.createDoctorWithUser(fullName, phone, email, password, specialization, qualification, experienceYears, priceBooking);
        if (userId > 0) {
            HttpSession sessionLog = req.getSession(false);
            User userLog = sessionLog != null ? (User) sessionLog.getAttribute("account") : null;
            SystemLogService.log(userLog != null ? userLog.getUserId() : null, "DOCTOR_ADDED",
                    "Thêm bác sĩ: fullName=" + fullName + ", email=" + email + ", specialization=" + specialization);
            return true;
        }

        req.setAttribute("error", "Không thể thêm bác sĩ");
        req.setAttribute("addModalOpen", true);
        return false;
    }

    private boolean handleEdit(HttpServletRequest req) throws SQLException {
        String doctorIdRaw = trim(req.getParameter("doctorId"));
        String fullName = trim(req.getParameter("fullName"));
        String phone = trim(req.getParameter("phone"));
        String email = trim(req.getParameter("email"));
        String specialization = trim(req.getParameter("specialization"));
        String qualification = trim(req.getParameter("qualification"));
        String experienceRaw = trim(req.getParameter("experienceYears"));
        String priceRaw = trim(req.getParameter("priceBooking"));

        keepEditForm(req, doctorIdRaw, fullName, phone, email, specialization, qualification, experienceRaw, priceRaw);

        int doctorId;
        try {
            doctorId = Integer.parseInt(doctorIdRaw);
        } catch (Exception e) {
            req.setAttribute("error", "Bác sĩ không hợp lệ");
            req.setAttribute("editModalOpen", true);
            return false;
        }

        DoctorDAO doctorDAO = new DoctorDAO();
        Doctor existing = doctorDAO.getDoctorByIdForAdmin(doctorId);
        if (existing == null) {
            req.setAttribute("error", "Bác sĩ không tồn tại");
            req.setAttribute("editModalOpen", true);
            return false;
        }

        boolean valid = true;
        if (fullName.isEmpty()) {
            req.setAttribute("editFullNameError", "Họ tên không được để trống");
            valid = false;
        }
        if (phone.isEmpty()) {
            req.setAttribute("editPhoneError", "Số điện thoại không được để trống");
            valid = false;
        }
        if (email.isEmpty()) {
            req.setAttribute("editEmailError", "Email không được để trống");
            valid = false;
        }
        valid = validateDoctorFields(req, specialization, qualification, experienceRaw, priceRaw, false) && valid;

        UserDAO userDAO = new UserDAO();
        User phoneOwner = userDAO.getUserByPhone(phone);
        if (valid && phoneOwner != null && phoneOwner.getUserId() != existing.getUserId()) {
            req.setAttribute("editPhoneError", "Số điện thoại đã tồn tại");
            valid = false;
        }
        User emailOwner = userDAO.getUserByEmail(email);
        if (valid && emailOwner != null && emailOwner.getUserId() != existing.getUserId()) {
            req.setAttribute("editEmailError", "Email đã tồn tại");
            valid = false;
        }

        if (!valid) {
            req.setAttribute("error", "Dữ liệu cập nhật bác sĩ không hợp lệ");
            req.setAttribute("editModalOpen", true);
            return false;
        }

        int experienceYears = Integer.parseInt(experienceRaw);
        int priceBooking = Integer.parseInt(priceRaw);
        boolean updated = doctorDAO.updateDoctorAndUser(
                doctorId, fullName, phone, email, specialization, qualification, experienceYears, priceBooking
        );

        if (updated) {
            HttpSession sessionLog = req.getSession(false);
            User userLog = sessionLog != null ? (User) sessionLog.getAttribute("account") : null;
            SystemLogService.log(userLog != null ? userLog.getUserId() : null, "DOCTOR_UPDATED",
                    "Cập nhật bác sĩ: doctorId=" + doctorId + ", fullName=" + fullName + ", email=" + email);
            return true;
        }

        req.setAttribute("error", "Không thể cập nhật bác sĩ");
        req.setAttribute("editModalOpen", true);
        return false;
    }

    private boolean validateDoctorFields(HttpServletRequest req, String specialization, String qualification,
            String experienceRaw, String priceRaw, boolean isAdd) {
        boolean valid = true;
        String prefix = isAdd ? "add" : "edit";

        if (specialization.isEmpty()) {
            req.setAttribute(prefix + "SpecializationError", "Chuyên môn là bắt buộc");
            valid = false;
        } else if (specialization.length() > MAX_SPECIALIZATION_LENGTH) {
            req.setAttribute(prefix + "SpecializationError", "Chuyên môn tối đa " + MAX_SPECIALIZATION_LENGTH + " ký tự");
            valid = false;
        }

        if (qualification.isEmpty()) {
            req.setAttribute(prefix + "QualificationError", "Bằng cấp là bắt buộc");
            valid = false;
        } else if (!QUALIFICATION_OPTIONS.contains(qualification)) {
            req.setAttribute(prefix + "QualificationError", "Bằng cấp không hợp lệ");
            valid = false;
        }

        if (experienceRaw.isEmpty()) {
            req.setAttribute(prefix + "ExperienceError", "Kinh nghiệm là bắt buộc");
            valid = false;
        } else if (!experienceRaw.matches("\\d+")) {
            req.setAttribute(prefix + "ExperienceError", "Kinh nghiệm phải là số nguyên");
            valid = false;
        } else {
            int experience = Integer.parseInt(experienceRaw);
            if (experience < MIN_EXPERIENCE || experience > MAX_EXPERIENCE) {
                req.setAttribute(prefix + "ExperienceError", "Kinh nghiệm phải từ " + MIN_EXPERIENCE + " đến " + MAX_EXPERIENCE);
                valid = false;
            }
        }

        if (priceRaw.isEmpty()) {
            req.setAttribute(prefix + "PriceError", "Giá khám là bắt buộc");
            valid = false;
        } else if (!priceRaw.matches("\\d+")) {
            req.setAttribute(prefix + "PriceError", "Giá khám phải là số nguyên không âm");
            valid = false;
        } else {
            int price = Integer.parseInt(priceRaw);
            if (price < MIN_PRICE || price > MAX_PRICE) {
                req.setAttribute(prefix + "PriceError", "Giá khám phải từ " + MIN_PRICE + " đến " + MAX_PRICE);
                valid = false;
            }
        }

        return valid;
    }

    private void keepAddForm(HttpServletRequest req, String fullName, String phone, String email,
            String specialization, String qualification, String experienceRaw, String priceRaw) {
        req.setAttribute("addModalOpen", true);
        req.setAttribute("addFullName", fullName);
        req.setAttribute("addPhone", phone);
        req.setAttribute("addEmail", email);
        req.setAttribute("addSpecialization", specialization);
        req.setAttribute("addQualification", qualification);
        req.setAttribute("addExperience", experienceRaw);
        req.setAttribute("addPrice", priceRaw);
    }

    private void keepEditForm(HttpServletRequest req, String doctorIdRaw, String fullName, String phone, String email,
            String specialization, String qualification, String experienceRaw, String priceRaw) {
        req.setAttribute("editModalOpen", true);
        req.setAttribute("editDoctorId", doctorIdRaw);
        req.setAttribute("editFullName", fullName);
        req.setAttribute("editPhone", phone);
        req.setAttribute("editEmail", email);
        req.setAttribute("editSpecialization", specialization);
        req.setAttribute("editQualification", qualification);
        req.setAttribute("editExperience", experienceRaw);
        req.setAttribute("editPrice", priceRaw);
    }

    private void redirectSuccess(HttpServletResponse resp, HttpServletRequest req, String message) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/admin-doctors?success="
                + URLEncoder.encode(message, StandardCharsets.UTF_8));
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
