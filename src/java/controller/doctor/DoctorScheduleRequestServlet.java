package controller.doctor;

import dal.DoctorDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import model.Doctor;
import model.DoctorShift;
import model.ScheduleChangeRequest;
import model.User;
import util.SystemLogService;

public class DoctorScheduleRequestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/pages/auth/login.jsp");
            return;
        }

        User account = (User) session.getAttribute("account");
        if (account == null || account.getRole() == null || !"doctor".equalsIgnoreCase(account.getRole().name())) {
            response.sendRedirect(request.getContextPath() + "/pages/auth/login.jsp");
            return;
        }

        DoctorDAO doctorDAO = new DoctorDAO();
        Doctor doctor = doctorDAO.getDoctorByUserId(account.getUserId());
        if (doctor == null) {
            response.sendRedirect(request.getContextPath() + "/pages/auth/login.jsp");
            return;
        }

        int doctorId = doctor.getDoctorId();
        List<DoctorShift> weeklyShifts = doctorDAO.getDoctorShifts(doctorId);
        List<ScheduleChangeRequest> recentRequests = doctorDAO.getScheduleChangeRequestsByDoctor(doctorId, 20);

        request.setAttribute("weeklyShifts", weeklyShifts);
        request.setAttribute("recentRequests", recentRequests);

        request.getRequestDispatcher("/pages/examination/doctorScheduleRequest.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/pages/auth/login.jsp");
            return;
        }

        User account = (User) session.getAttribute("account");
        if (account == null || account.getRole() == null || !"doctor".equalsIgnoreCase(account.getRole().name())) {
            response.sendRedirect(request.getContextPath() + "/pages/auth/login.jsp");
            return;
        }

        DoctorDAO doctorDAO = new DoctorDAO();
        Doctor doctor = doctorDAO.getDoctorByUserId(account.getUserId());
        if (doctor == null) {
            response.sendRedirect(request.getContextPath() + "/pages/auth/login.jsp");
            return;
        }

        String requestType = safeUpper(request.getParameter("requestType"));
        String scopeType = safeUpper(request.getParameter("scopeType"));
        String actionType = safeUpper(request.getParameter("actionType"));
        String reason = trimOrEmpty(request.getParameter("reason"));

        String error = validateInput(requestType, scopeType, actionType, reason);

        Integer targetShiftId = parseInteger(request.getParameter("targetShiftId"));
        Integer dayOfWeek = parseInteger(request.getParameter("dayOfWeek"));
        Integer maxPatients = parseInteger(request.getParameter("maxPatients"));
        Date workDate = parseDate(request.getParameter("workDate"));
        LocalTime startTime = parseTime(request.getParameter("startTime"));
        LocalTime endTime = parseTime(request.getParameter("endTime"));

        if (error == null && "ONE_DATE".equals(scopeType) && workDate == null) {
            error = "Vui lòng chọn ngày áp dụng cho yêu cầu tạm thời.";
        }


        if (error == null && "WEEKLY_TEMPLATE".equals(scopeType) && dayOfWeek == null) {
            error = "Vui lòng chọn thứ áp dụng cho yêu cầu thay đổi lịch tuần.";
        }

        if (error == null && ("ADD".equals(actionType) || "UPDATE".equals(actionType))) {
            if (startTime == null || endTime == null || !endTime.isAfter(startTime)) {
                error = "Khung giờ chưa hợp lệ. Giờ kết thúc phải sau giờ bắt đầu.";
            }
            if (maxPatients == null || maxPatients <= 0) {
                error = "Số bệnh nhân tối đa phải lớn hơn 0.";
            }
        }

        if (error == null && ("UPDATE".equals(actionType) || "REMOVE".equals(actionType)) && targetShiftId == null) {
            error = "Vui lòng chọn ca gốc cần cập nhật hoặc hủy.";
        }

        if (error == null && targetShiftId != null && !doctorDAO.isShiftOwnedByDoctor(targetShiftId, doctor.getDoctorId())) {
            error = "Ca gốc không thuộc lịch làm việc của bạn.";
        }

        if (error != null) {
            request.getSession().setAttribute("scheduleRequestError", error);
            response.sendRedirect(request.getContextPath() + "/doctor/schedule-request");
            return;
        }

        boolean created = doctorDAO.createScheduleChangeRequest(
                doctor.getDoctorId(),
                requestType,
                scopeType,
                reason,
                actionType,
                targetShiftId,
                workDate,
                dayOfWeek,
                startTime,
                endTime,
                maxPatients
        );

        if (created) {
            SystemLogService.logWithSession(session, "DOCTOR_CREATE_SCHEDULE_CHANGE_REQUEST",
                    "Bác sĩ " + doctor.getFullName() + " gửi yêu cầu đổi lịch loại "
                    + requestType + " - " + actionType + ".");
            request.getSession().setAttribute("scheduleRequestSuccess", "Đã gửi yêu cầu đổi lịch thành công. Vui lòng chờ quản trị viên duyệt.");
        } else {
            request.getSession().setAttribute("scheduleRequestError", "Không thể tạo yêu cầu lúc này. Vui lòng thử lại.");
        }

        response.sendRedirect(request.getContextPath() + "/doctor/schedule-request");
    }

    private String validateInput(String requestType, String scopeType, String actionType, String reason) {
        if (!("TEMPORARY".equals(requestType) || "PERMANENT".equals(requestType))) {
            return "Loại yêu cầu không hợp lệ.";
        }
        if (!("ONE_DATE".equals(scopeType) || "WEEKLY_TEMPLATE".equals(scopeType))) {
            return "Phạm vi áp dụng không hợp lệ.";
        }
        if ("TEMPORARY".equals(requestType) && !"ONE_DATE".equals(scopeType)) {
            return "Yêu cầu tạm thời chỉ được áp dụng theo ONE_DATE.";
        }
        if ("PERMANENT".equals(requestType) && !"WEEKLY_TEMPLATE".equals(scopeType)) {
            return "Yêu cầu dài hạn chỉ được áp dụng theo WEEKLY_TEMPLATE.";
        }
        if (!("ADD".equals(actionType) || "UPDATE".equals(actionType) || "REMOVE".equals(actionType))) {
            return "Hành động thay đổi ca không hợp lệ.";
        }
        if (reason.isBlank()) {
            return "Vui lòng nhập lý do gửi đơn.";
        }
        return null;
    }

    private String safeUpper(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    private String trimOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private Integer parseInteger(String value) {
        try {
            if (value == null || value.isBlank()) {
                return null;
            }
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Date parseDate(String value) {
        try {
            if (value == null || value.isBlank()) {
                return null;
            }
            return Date.valueOf(LocalDate.parse(value.trim()));
        } catch (Exception ex) {
            return null;
        }
    }

    private LocalTime parseTime(String value) {
        try {
            if (value == null || value.isBlank()) {
                return null;
            }
            return LocalTime.parse(value.trim());
        } catch (Exception ex) {
            return null;
        }
    }
}
