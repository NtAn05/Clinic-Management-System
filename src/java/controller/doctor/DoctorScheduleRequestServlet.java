package controller.doctor;

import dal.DoctorDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import model.Doctor;
import model.DoctorShift;
import model.ScheduleChangeRequest;
import model.ScheduleSwapShiftOption;
import model.User;
import util.SystemLogService;

public class DoctorScheduleRequestServlet extends HttpServlet {

    private static final Map<String, LocalTime[]> SHIFT_TIME_BY_PERIOD = Map.of(
            "MORNING", new LocalTime[]{LocalTime.of(8, 0), LocalTime.of(12, 0)},
            "AFTERNOON", new LocalTime[]{LocalTime.of(13, 0), LocalTime.of(17, 0)}
    );
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

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

        String mode = safeUpper(request.getParameter("mode"));
        if ("SWAP_OPTIONS".equals(mode)) {
            writeSwapOptionsResponse(request, response, doctorDAO, doctor.getDoctorId());
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
        String actionType = safeUpper(request.getParameter("actionType"));
        String reason = trimOrEmpty(request.getParameter("reason"));
        String scopeType = deriveScopeType(requestType, actionType);

        String error = validateInput(requestType, actionType, reason);

        Integer targetShiftId = parseInteger(request.getParameter("targetShiftId"));
        Integer dayOfWeek = parseInteger(request.getParameter("dayOfWeek"));
        Integer maxPatients = parseInteger(request.getParameter("maxPatients"));
        Integer swapShiftId = parseInteger(request.getParameter("swapShiftId"));
        Date workDate = parseDate(request.getParameter("workDate"));
        String shiftPeriod = safeUpper(request.getParameter("shiftPeriod"));

        LocalTime startTime = null;
        LocalTime endTime = null;
        if ("ADD".equals(actionType)) {
            LocalTime[] shiftTime = SHIFT_TIME_BY_PERIOD.get(shiftPeriod);
            if (shiftTime != null) {
                startTime = shiftTime[0];
                endTime = shiftTime[1];
            }
        }

        if (error == null && "ONE_DATE".equals(scopeType) && workDate == null) {
            error = "Vui lòng chọn ngày áp dụng cho yêu cầu tạm thời.";
        }

        if (error == null && "WEEKLY_TEMPLATE".equals(scopeType) && "ADD".equals(actionType) && dayOfWeek == null) {
            error = "Vui lòng chọn thứ áp dụng cho yêu cầu thay đổi lịch tuần.";
        }

        if (error == null && "ADD".equals(actionType)) {
            if (!SHIFT_TIME_BY_PERIOD.containsKey(shiftPeriod)) {
                error = "Vui lòng chọn ca làm việc (sáng/chiều).";
            }
            if (maxPatients == null || maxPatients <= 0) {
                error = "Số bệnh nhân tối đa phải lớn hơn 0.";
            }
        }

        if (error == null && "UPDATE".equals(actionType)) {
            if (!"ONE_DATE".equals(scopeType)) {
                error = "Đổi ca chỉ hỗ trợ phạm vi một ngày cụ thể.";
            } else if (workDate == null) {
                error = "Vui lòng chọn ngày để tìm ca bác sĩ muốn đổi.";
            } else if (swapShiftId == null) {
                error = "Vui lòng chọn ca của bác sĩ khác để đổi.";
            }
        }

        if (error == null && ("UPDATE".equals(actionType)
                || ("REMOVE".equals(actionType) && "WEEKLY_TEMPLATE".equals(scopeType)))
                && targetShiftId == null) {
            error = "Vui lòng chọn ca gốc cần cập nhật hoặc hủy.";
        }

        if (error == null && targetShiftId != null && !doctorDAO.isShiftOwnedByDoctor(targetShiftId, doctor.getDoctorId())) {
            error = "Ca gốc không thuộc lịch làm việc của bạn.";
        }

        if (error == null && "REMOVE".equals(actionType) && "WEEKLY_TEMPLATE".equals(scopeType) && targetShiftId != null) {
            DoctorShift currentShift = doctorDAO.getDoctorShiftById(targetShiftId);
            if (currentShift != null) {
                dayOfWeek = currentShift.getDayOfWeek();
            }
        }

        if (error == null && "UPDATE".equals(actionType)) {
            DoctorShift swapShift = doctorDAO.getDoctorShiftById(swapShiftId);
            if (swapShift == null) {
                error = "Không tìm thấy ca bác sĩ muốn đổi.";
            } else {
                int workDateDay = normalizeDayOfWeek(workDate.toLocalDate().getDayOfWeek());
                if (swapShift.getDoctorId() == doctor.getDoctorId()) {
                    error = "Bạn chỉ có thể chọn ca của bác sĩ khác.";
                } else if (swapShift.getDayOfWeek() != workDateDay) {
                    error = "Ca được chọn không nằm trong ngày áp dụng.";
                } else {
                    startTime = swapShift.getStartTime();
                    endTime = swapShift.getEndTime();
                    dayOfWeek = workDateDay;
                    if (targetShiftId != null) {
                        DoctorShift currentShift = doctorDAO.getDoctorShiftById(targetShiftId);
                        if (currentShift != null) {
                            maxPatients = currentShift.getMaxPatients();
                        }
                    }
                }
            }
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

    private String validateInput(String requestType, String actionType, String reason) {
        if (!("TEMPORARY".equals(requestType) || "PERMANENT".equals(requestType))) {
            return "Loại yêu cầu không hợp lệ.";
        }
        if (!("ADD".equals(actionType) || "UPDATE".equals(actionType) || "REMOVE".equals(actionType))) {
            return "Hành động thay đổi ca không hợp lệ.";
        }
        if (reason.isBlank()) {
            return "Vui lòng nhập lý do gửi đơn.";
        }
        return null;
    }


    private String deriveScopeType(String requestType, String actionType) {
        if ("UPDATE".equals(actionType)) {
            return "ONE_DATE";
        }
        if ("PERMANENT".equals(requestType)) {
            return "WEEKLY_TEMPLATE";
        }
        return "ONE_DATE";
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

    private void writeSwapOptionsResponse(HttpServletRequest request, HttpServletResponse response, DoctorDAO doctorDAO, int requesterDoctorId) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        Date workDate = parseDate(request.getParameter("workDate"));
        if (workDate == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("[]");
            return;
        }

        int dayOfWeek = normalizeDayOfWeek(workDate.toLocalDate().getDayOfWeek());
        List<ScheduleSwapShiftOption> options = doctorDAO.getSwapShiftOptionsByDate(requesterDoctorId, dayOfWeek);

        try (PrintWriter out = response.getWriter()) {
            out.write("[");
            for (int i = 0; i < options.size(); i++) {
                ScheduleSwapShiftOption option = options.get(i);
                if (i > 0) {
                    out.write(",");
                }
                String label = option.getDoctorName() + " - " + getDayLabel(option.getDayOfWeek())
                        + " (" + option.getStartTime().format(TIME_FMT) + " - " + option.getEndTime().format(TIME_FMT) + ")";
                out.write("{\"shiftId\":" + option.getShiftId()
                        + ",\"doctorId\":" + option.getDoctorId()
                        + ",\"doctorName\":\"" + escapeJson(option.getDoctorName()) + "\""
                        + ",\"label\":\"" + escapeJson(label) + "\"}");
            }
            out.write("]");
        }
    }

    private int normalizeDayOfWeek(DayOfWeek dayOfWeek) {
        return dayOfWeek == DayOfWeek.SUNDAY ? 0 : dayOfWeek.getValue();
    }

    private String getDayLabel(int dayOfWeek) {
        return switch (dayOfWeek) {
            case 0 -> "Chủ nhật";
            case 1 -> "Thứ 2";
            case 2 -> "Thứ 3";
            case 3 -> "Thứ 4";
            case 4 -> "Thứ 5";
            case 5 -> "Thứ 6";
            case 6 -> "Thứ 7";
            default -> "Không xác định";
        };
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}