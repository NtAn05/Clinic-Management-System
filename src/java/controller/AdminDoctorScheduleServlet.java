package controller;

import dal.DoctorDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import model.Doctor;
import model.DoctorShift;
import model.Role;
import model.ScheduleChangeRequest;
import model.User;
import util.SystemLogService;

public class AdminDoctorScheduleServlet extends HttpServlet {

    private static final String VIEW_PATH = "/pages/admin/doctor-schedules.jsp";
    private static final List<Integer> DAY_ORDER = Arrays.asList(1, 2, 3, 4, 5, 6, 0);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("vi", "VN"));
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private static final String SHIFT_MORNING = "morning";
    private static final String SHIFT_AFTERNOON = "afternoon";
    private static final LocalTime MORNING_START = LocalTime.of(7, 0);
    private static final LocalTime MORNING_END = LocalTime.of(11, 30);
    private static final LocalTime AFTERNOON_START = LocalTime.of(13, 0);
    private static final LocalTime AFTERNOON_END = LocalTime.of(16, 30);
    private static final int MIN_MAX_PATIENTS = 1;
    private static final int MAX_MAX_PATIENTS = 100;

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

        String action = req.getParameter("action");
        DoctorDAO doctorDAO = new DoctorDAO();

        try {
            if ("add".equals(action)) {
                handleAddShift(req, doctorDAO);
            } else if ("update".equals(action)) {
                handleUpdateShift(req, doctorDAO);
            } else if ("delete".equals(action)) {
                handleDeleteShift(req, doctorDAO);
            }
        } catch (Exception e) {
            String actionLabel = "xử lý";
            if ("add".equals(action)) {
                actionLabel = "thêm";
            } else if ("update".equals(action)) {
                actionLabel = "cập nhật";
            } else if ("delete".equals(action)) {
                actionLabel = "xóa";
            }
            req.setAttribute("error", "Không thể " + actionLabel + " lịch làm việc: " + e.getMessage());
        }

        loadPage(req, resp);
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
        List<Doctor> doctors = doctorDAO.getAllDoctorsForSchedule();
        List<Doctor> activeDoctors = doctorDAO.getActiveDoctorsForSchedule();

        boolean isGetRequest = "GET".equalsIgnoreCase(req.getMethod());
        boolean hasActionParam = req.getParameter("action") != null && !req.getParameter("action").isBlank();
        String keyword = trim(firstNonBlank(req.getParameter("filterKeyword"), req.getParameter("keyword")));
        String dayFilterParam = firstNonBlank(
                req.getParameter("filterDayOfWeek"),
                (isGetRequest && !hasActionParam) ? req.getParameter("dayOfWeek") : null
        );
        Integer selectedDay = parseNullableDay(dayFilterParam);
        int weekOffset = parseInt(firstNonBlank(req.getParameter("filterWeekOffset"), req.getParameter("weekOffset")), 0);

        String selectedShiftType = normalizeShiftType(req.getParameter("filterShiftType"));
        if (selectedShiftType.isEmpty() && isGetRequest && !hasActionParam) {
            selectedShiftType = normalizeShiftType(req.getParameter("shiftType"));
        }

        LocalDate weekStart = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .plusWeeks(weekOffset);
        LocalDate weekEnd = weekStart.plusDays(6);

        List<Doctor> filteredDoctors = doctors.stream()
                .filter(d -> keyword.isEmpty() || containsIgnoreCase(d.getFullName(), keyword))
                .collect(Collectors.toList());

        List<ScheduleViewItem> scheduleItems = new ArrayList<>();
        for (Doctor doctor : filteredDoctors) {
            List<DoctorShift> shifts = doctorDAO.getDoctorShifts(doctor.getDoctorId());
            if (shifts == null || shifts.isEmpty()) {
                continue;
            }
            for (DoctorShift shift : shifts) {
                Integer normalizedDayOfWeek = normalizeDayOfWeek(shift.getDayOfWeek());
                if (normalizedDayOfWeek == null) {
                    continue;
                }
                if (shift.getStartTime() == null || shift.getEndTime() == null) {
                    continue;
                }
                if (selectedDay != null && normalizedDayOfWeek.intValue() != selectedDay) {
                    continue;
                }
                if (!selectedShiftType.isEmpty() && !selectedShiftType.equals(getShiftCode(shift.getStartTime(), shift.getEndTime()))) {
                    continue;
                }
                scheduleItems.add(toScheduleItem(doctor, shift, weekStart, normalizedDayOfWeek));
            }
        }

        applyApprovedOverrides(doctorDAO, scheduleItems, filteredDoctors, weekStart, weekEnd);
        for (java.util.Iterator<ScheduleViewItem> iterator = scheduleItems.iterator(); iterator.hasNext();) {
            ScheduleViewItem item = iterator.next();
            if (!matchesSelectedFilters(item, selectedDay, selectedShiftType)) {
                iterator.remove();
            }
        }

        scheduleItems.sort(Comparator
                .comparing(ScheduleViewItem::getWorkDate)
                .thenComparing(ScheduleViewItem::getStartTime)
                .thenComparing(ScheduleViewItem::getDoctorName, String.CASE_INSENSITIVE_ORDER));

        Map<String, String> dayDates = new LinkedHashMap<>();
        Map<String, List<ScheduleViewItem>> weekGrid = new LinkedHashMap<>();
        for (int day : DAY_ORDER) {
            String key = String.valueOf(day);
            dayDates.put(key, getDateForDay(weekStart, day).format(DateTimeFormatter.ofPattern("dd/MM")));
            weekGrid.put(key, new ArrayList<>());
        }
        for (ScheduleViewItem item : scheduleItems) {
            List<ScheduleViewItem> list = weekGrid.get(String.valueOf(item.getDayOfWeek()));
            if (list != null) {
                list.add(item);
            }
        }

        req.setAttribute("doctors", doctors);
        req.setAttribute("activeDoctors", activeDoctors);
        req.setAttribute("filteredDoctors", filteredDoctors);
        req.setAttribute("scheduleItems", scheduleItems);
        req.setAttribute("weekGrid", weekGrid);
        req.setAttribute("dayDates", dayDates);
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedDay", selectedDay == null ? "" : String.valueOf(selectedDay));
        req.setAttribute("selectedShiftType", selectedShiftType);
        req.setAttribute("weekOffset", weekOffset);
        req.setAttribute("weekLabel", weekStart.format(DATE_FMT) + " - " + weekEnd.format(DATE_FMT));
        req.setAttribute("pendingRequestCount", doctorDAO.countPendingScheduleChangeRequests());
        req.getRequestDispatcher(VIEW_PATH).forward(req, resp);
    }

    private void handleAddShift(HttpServletRequest req, DoctorDAO doctorDAO) throws SQLException {
        int doctorId = requiredInt(req.getParameter("doctorId"), "Vui lòng chọn bác sĩ");
        int dayOfWeek = requiredInt(req.getParameter("dayOfWeek"), "Vui lòng chọn thứ");
        ShiftTime shiftTime = resolveShiftType(req.getParameter("shiftType"));
        int maxPatients = requiredInt(req.getParameter("maxPatients"), "Vui lòng nhập số bệnh nhân tối đa");

        if (!doctorDAO.doctorExists(doctorId)) {
            throw new IllegalArgumentException("Bác sĩ không tồn tại trong hệ thống");
        }

        boolean activeDoctorExists = doctorDAO.getActiveDoctorsForSchedule().stream()
                .anyMatch(d -> d.getDoctorId() == doctorId);
        if (!activeDoctorExists) {
            throw new IllegalArgumentException("Chỉ có thể thêm lịch cho bác sĩ có tài khoản đang hoạt động");
        }

        validateShift(dayOfWeek, shiftTime.startTime, shiftTime.endTime, maxPatients);

        if (doctorDAO.hasShiftConflict(doctorId, dayOfWeek, shiftTime.startTime, shiftTime.endTime, null)) {
            throw new IllegalArgumentException("Khung giờ bị trùng với ca làm việc đã có");
        }

        doctorDAO.addDoctorShift(doctorId, dayOfWeek, shiftTime.startTime, shiftTime.endTime, maxPatients);
        HttpSession sessionAdd = req.getSession(false);
        User userAdd = sessionAdd != null ? (User) sessionAdd.getAttribute("account") : null;
        SystemLogService.log(userAdd != null ? userAdd.getUserId() : null, "SHIFT_ADDED",
                "Thêm ca làm việc: doctorId=" + doctorId + ", dayOfWeek=" + dayOfWeek + ", shift=" + shiftTime.startTime + "-" + shiftTime.endTime);
        req.setAttribute("success", "Đã thêm ca làm việc thành công");
    }

    private void handleUpdateShift(HttpServletRequest req, DoctorDAO doctorDAO) throws SQLException {
        int shiftId = requiredInt(req.getParameter("shiftId"), "Thiếu mã ca làm việc");
        int doctorId = requiredInt(req.getParameter("doctorId"), "Vui lòng chọn bác sĩ");
        int dayOfWeek = requiredInt(req.getParameter("dayOfWeek"), "Vui lòng chọn thứ");
        ShiftTime shiftTime = resolveShiftType(req.getParameter("shiftType"));
        int maxPatients = requiredInt(req.getParameter("maxPatients"), "Vui lòng nhập số bệnh nhân tối đa");

        validateShift(dayOfWeek, shiftTime.startTime, shiftTime.endTime, maxPatients);

        if (!doctorDAO.doctorExists(doctorId)) {
            throw new IllegalArgumentException("Bác sĩ không tồn tại trong hệ thống");
        }
        if (!doctorDAO.isDoctorActive(doctorId)) {
            throw new IllegalArgumentException("Chỉ có thể cập nhật lịch cho bác sĩ đang hoạt động");
        }

        if (!doctorDAO.shiftExists(shiftId)) {
            throw new IllegalArgumentException("Ca làm việc không tồn tại");
        }
        if (!doctorDAO.isShiftOwnedByDoctor(shiftId, doctorId)) {
            throw new IllegalArgumentException("Ca làm việc không thuộc bác sĩ được chọn");
        }

        if (doctorDAO.hasShiftConflict(doctorId, dayOfWeek, shiftTime.startTime, shiftTime.endTime, shiftId)) {
            throw new IllegalArgumentException("Khung giờ bị trùng với ca làm việc đã có");
        }

        doctorDAO.updateDoctorShift(shiftId, dayOfWeek, shiftTime.startTime, shiftTime.endTime, maxPatients);
        HttpSession sessionUpd = req.getSession(false);
        User userUpd = sessionUpd != null ? (User) sessionUpd.getAttribute("account") : null;
        SystemLogService.log(userUpd != null ? userUpd.getUserId() : null, "SHIFT_UPDATED",
                "Cập nhật ca làm việc: shiftId=" + shiftId + ", doctorId=" + doctorId + ", dayOfWeek=" + dayOfWeek);
        req.setAttribute("success", "Đã cập nhật ca làm việc thành công");
    }

    private void handleDeleteShift(HttpServletRequest req, DoctorDAO doctorDAO) throws SQLException {
        int shiftId = requiredInt(req.getParameter("shiftId"), "Thiếu mã ca làm việc");
        if (!doctorDAO.shiftExists(shiftId)) {
            throw new IllegalArgumentException("Ca làm việc không tồn tại");
        }
        if (doctorDAO.hasUpcomingAppointmentsForShift(shiftId)) {
            throw new IllegalArgumentException("Không thể xóa ca làm việc vì vẫn còn lịch hẹn hiện tại/tương lai");
        }

        doctorDAO.deleteDoctorShift(shiftId);
        HttpSession sessionDel = req.getSession(false);
        User userDel = sessionDel != null ? (User) sessionDel.getAttribute("account") : null;
        SystemLogService.log(userDel != null ? userDel.getUserId() : null, "SHIFT_DELETED",
                "Xóa ca làm việc: shiftId=" + shiftId);
        req.setAttribute("success", "Đã xóa ca làm việc thành công");
    }

    private ShiftTime resolveShiftType(String shiftType) {
        if (SHIFT_MORNING.equals(shiftType)) {
            return new ShiftTime(MORNING_START, MORNING_END);
        }
        if (SHIFT_AFTERNOON.equals(shiftType)) {
            return new ShiftTime(AFTERNOON_START, AFTERNOON_END);
        }
        throw new IllegalArgumentException("Vui lòng chọn ca làm việc hợp lệ");
    }

    private void validateShift(int dayOfWeek, LocalTime startTime, LocalTime endTime, int maxPatients) {
        if (dayOfWeek < 0 || dayOfWeek > 6) {
            throw new IllegalArgumentException("Thứ không hợp lệ");
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Khung giờ ca làm việc không hợp lệ");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Giờ bắt đầu phải nhỏ hơn giờ kết thúc");
        }
        if (!isDefinedShiftTime(startTime, endTime)) {
            throw new IllegalArgumentException("Khung giờ phải thuộc ca sáng (07:00 - 11:30) hoặc ca chiều (13:00 - 16:30)");
        }
        if (maxPatients < MIN_MAX_PATIENTS || maxPatients > MAX_MAX_PATIENTS) {
            throw new IllegalArgumentException("Số bệnh nhân tối đa phải trong khoảng 1 đến 100");
        }
    }

    private boolean isDefinedShiftTime(LocalTime startTime, LocalTime endTime) {
        return (MORNING_START.equals(startTime) && MORNING_END.equals(endTime))
                || (AFTERNOON_START.equals(startTime) && AFTERNOON_END.equals(endTime));
    }

    private int requiredInt(String value, String error) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(error);
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(error);
        }
    }

    private int parseInt(String value, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private Integer parseNullableDay(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            int day = Integer.parseInt(value);
            return normalizeDayOfWeek(day);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer normalizeDayOfWeek(int day) {
        if (day == 7) {
            return 0;
        }
        return (day >= 0 && day <= 6) ? day : null;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        return second;
    }

    private String normalizeShiftType(String shiftType) {
        if (SHIFT_MORNING.equals(shiftType) || SHIFT_AFTERNOON.equals(shiftType)) {
            return shiftType;
        }
        return "";
    }

    private boolean containsIgnoreCase(String source, String keyword) {
        if (source == null) {
            return false;
        }
        return source.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    private boolean matchesSelectedFilters(ScheduleViewItem item, Integer selectedDay, String selectedShiftType) {
        if (item == null) {
            return false;
        }
        if (selectedDay != null && item.getDayOfWeek() != selectedDay.intValue()) {
            return false;
        }
        return selectedShiftType == null
                || selectedShiftType.isBlank()
                || selectedShiftType.equals(item.getShiftCode());
    }

    private LocalDate getDateForDay(LocalDate weekStart, int dayOfWeek) {
        return dayOfWeek == 0 ? weekStart.plusDays(6) : weekStart.plusDays(dayOfWeek - 1);
    }

    private ScheduleViewItem toScheduleItem(Doctor doctor, DoctorShift shift, LocalDate weekStart, int dayOfWeek) {
        LocalDate workDate = getDateForDay(weekStart, dayOfWeek);
        String shiftCode = getShiftCode(shift.getStartTime(), shift.getEndTime());
        return new ScheduleViewItem(
                shift.getShiftId(),
                doctor.getDoctorId(),
                doctor.getFullName(),
                doctor.getSpecialization(),
                dayOfWeek,
                getDayLabel(dayOfWeek),
                workDate,
                workDate.format(DATE_FMT),
                shift.getStartTime(),
                shift.getStartTime().format(TIME_FMT),
                shift.getEndTime(),
                shift.getEndTime().format(TIME_FMT),
                shiftCode,
                getShiftLabel(shiftCode),
                shift.getMaxPatients(),
                "Da xep",
                false
        );
    }

    private void applyApprovedOverrides(
            DoctorDAO doctorDAO,
            List<ScheduleViewItem> scheduleItems,
            List<Doctor> filteredDoctors,
            LocalDate weekStart,
            LocalDate weekEnd
    ) {
        List<ScheduleChangeRequest> approvedRequests
                = doctorDAO.getScheduleChangeRequestsForAdmin("APPROVED", "ALL", "ALL", "");
        if (approvedRequests == null || approvedRequests.isEmpty()) {
            return;
        }

        Map<Integer, Doctor> doctorById = new HashMap<>();
        Map<String, Integer> doctorIdByName = new HashMap<>();
        for (Doctor doctor : filteredDoctors) {
            doctorById.put(doctor.getDoctorId(), doctor);
            if (doctor.getFullName() != null) {
                doctorIdByName.put(doctor.getFullName().trim().toLowerCase(Locale.ROOT), doctor.getDoctorId());
            }
        }

        for (ScheduleChangeRequest request : approvedRequests) {
            String requestType = request.getRequestType() == null ? "" : request.getRequestType().trim().toUpperCase(Locale.ROOT);
            String scopeType = request.getScopeType() == null ? "" : request.getScopeType().trim().toUpperCase(Locale.ROOT);
            boolean temporary = "TEMPORARY".equals(requestType) || "ONE_DATE".equals(scopeType);

            LocalDate newDate = null;
            LocalDate oldDate = null;
            if (temporary) {
                if (request.getWorkDate() == null) {
                    continue;
                }
                newDate = request.getWorkDate().toLocalDate();
                if (newDate.isBefore(weekStart) || newDate.isAfter(weekEnd)) {
                    continue;
                }
                oldDate = request.getOldWorkDate() != null ? request.getOldWorkDate().toLocalDate() : newDate;
            } else {
                if (request.getDayOfWeek() == null) {
                    continue;
                }
                newDate = getDateForDay(weekStart, request.getDayOfWeek());
                int oldDay = request.getOldDayOfWeek() != null ? request.getOldDayOfWeek() : request.getDayOfWeek();
                oldDate = getDateForDay(weekStart, oldDay);
            }

            if (newDate == null || oldDate == null) {
                continue;
            }

            Doctor requester = doctorById.get(request.getDoctorId());
            if (requester == null) {
                continue;
            }

            String actionType = request.getActionType() == null
                    ? "" : request.getActionType().trim().toUpperCase(Locale.ROOT);
            String newShiftCode = getShiftCode(request.getStartTime(), request.getEndTime());
            String oldShiftCode = getShiftCode(request.getOldStartTime(), request.getOldEndTime());
            if ("ADD".equals(actionType)) {
                removeShift(scheduleItems, request.getDoctorId(), newDate, newShiftCode);
                addOverlayShift(scheduleItems, requester, newDate,
                        request.getStartTime(), request.getEndTime(), request.getMaxPatients(), temporary);
                continue;
            }

            if ("REMOVE".equals(actionType)) {
                String removeShiftCode = !oldShiftCode.isEmpty() ? oldShiftCode : newShiftCode;
                removeShift(scheduleItems, request.getDoctorId(), newDate, removeShiftCode);
                continue;
            }

            if ("UPDATE".equals(actionType)) {
                if (!oldShiftCode.isEmpty()) {
                    removeShift(scheduleItems, request.getDoctorId(), oldDate, oldShiftCode);
                }
                addOverlayShift(scheduleItems, requester, newDate,
                        request.getStartTime(), request.getEndTime(), request.getMaxPatients(), temporary);

                Integer counterpartDoctorId = findDoctorIdByName(doctorIdByName, request.getNewDoctorName());
                Doctor counterpart = counterpartDoctorId == null ? null : doctorById.get(counterpartDoctorId);
                if (counterpart != null) {
                    if (!newShiftCode.isEmpty()) {
                        removeShift(scheduleItems, counterpartDoctorId, newDate, newShiftCode);
                    }
                    if (!oldShiftCode.isEmpty()) {
                        addOverlayShift(scheduleItems, counterpart, oldDate,
                                request.getOldStartTime(), request.getOldEndTime(), request.getMaxPatients(), temporary);
                    }
                }
            }
        }
    }

    private Integer findDoctorIdByName(Map<String, Integer> doctorIdByName, String doctorName) {
        if (doctorName == null || doctorName.isBlank()) {
            return null;
        }
        return doctorIdByName.get(doctorName.trim().toLowerCase(Locale.ROOT));
    }

    private void removeShift(List<ScheduleViewItem> scheduleItems, int doctorId, LocalDate workDate, String shiftCode) {
        if (workDate == null || shiftCode == null || shiftCode.isBlank()) {
            return;
        }
        scheduleItems.removeIf(item
                -> item.getDoctorId() == doctorId
                && workDate.equals(item.getWorkDate())
                && shiftCode.equals(item.getShiftCode()));
    }

    private void addOverlayShift(
            List<ScheduleViewItem> scheduleItems,
            Doctor doctor,
            LocalDate workDate,
            LocalTime startTime,
            LocalTime endTime,
            Integer maxPatients,
            boolean temporary
    ) {
        if (doctor == null || workDate == null || startTime == null || endTime == null || !startTime.isBefore(endTime)) {
            return;
        }
        int dayOfWeek = normalizeDayOfWeek(workDate.getDayOfWeek().getValue());
        if (dayOfWeek < 0 || dayOfWeek > 6) {
            return;
        }
        String shiftCode = getShiftCode(startTime, endTime);
        removeShift(scheduleItems, doctor.getDoctorId(), workDate, shiftCode);
        scheduleItems.add(new ScheduleViewItem(
                -1,
                doctor.getDoctorId(),
                doctor.getFullName(),
                doctor.getSpecialization(),
                dayOfWeek,
                getDayLabel(dayOfWeek),
                workDate,
                workDate.format(DATE_FMT),
                startTime,
                startTime.format(TIME_FMT),
                endTime,
                endTime.format(TIME_FMT),
                shiftCode,
                getShiftLabel(shiftCode),
                maxPatients != null ? maxPatients : 0,
                temporary ? "Tam thoi" : "Da xep",
                temporary
        ));
    }

    private String getShiftCode(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null || !startTime.isBefore(endTime)) {
            return "";
        }
        if (MORNING_START.equals(startTime) && MORNING_END.equals(endTime)) {
            return SHIFT_MORNING;
        }
        if (AFTERNOON_START.equals(startTime) && AFTERNOON_END.equals(endTime)) {
            return SHIFT_AFTERNOON;
        }
        long morningMinutes = overlapMinutes(startTime, endTime, MORNING_START, MORNING_END);
        long afternoonMinutes = overlapMinutes(startTime, endTime, AFTERNOON_START, AFTERNOON_END);
        if (morningMinutes == 0 && afternoonMinutes == 0) {
            return startTime.isBefore(AFTERNOON_START) ? SHIFT_MORNING : SHIFT_AFTERNOON;
        }
        return morningMinutes >= afternoonMinutes ? SHIFT_MORNING : SHIFT_AFTERNOON;
    }

    private long overlapMinutes(LocalTime aStart, LocalTime aEnd, LocalTime bStart, LocalTime bEnd) {
        LocalTime maxStart = aStart.isAfter(bStart) ? aStart : bStart;
        LocalTime minEnd = aEnd.isBefore(bEnd) ? aEnd : bEnd;
        if (!maxStart.isBefore(minEnd)) {
            return 0;
        }
        return java.time.Duration.between(maxStart, minEnd).toMinutes();
    }

    private String getShiftLabel(String shiftCode) {
        return SHIFT_AFTERNOON.equals(shiftCode)
                ? "Ca chiều (13:00 - 16:30)"
                : "Ca sáng (07:00 - 11:30)";
    }

    private String getDayLabel(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1: return "Thứ 2";
            case 2: return "Thứ 3";
            case 3: return "Thứ 4";
            case 4: return "Thứ 5";
            case 5: return "Thứ 6";
            case 6: return "Thứ 7";
            default: return "Chủ nhật";
        }
    }

    public static class ScheduleViewItem {

        private final int shiftId;
        private final int doctorId;
        private final String doctorName;
        private final String specialization;
        private final int dayOfWeek;
        private final String dayLabel;
        private final LocalDate workDate;
        private final String workDateText;
        private final LocalTime startTime;
        private final String startTimeText;
        private final LocalTime endTime;
        private final String endTimeText;
        private final String shiftCode;
        private final String shiftLabel;
        private final int maxPatients;
        private final String status;
        private final boolean temporary;

        public ScheduleViewItem(int shiftId, int doctorId, String doctorName, String specialization, int dayOfWeek,
                String dayLabel, LocalDate workDate, String workDateText, LocalTime startTime, String startTimeText,
                LocalTime endTime, String endTimeText, String shiftCode, String shiftLabel, int maxPatients,
                String status, boolean temporary) {
            this.shiftId = shiftId;
            this.doctorId = doctorId;
            this.doctorName = doctorName;
            this.specialization = specialization;
            this.dayOfWeek = dayOfWeek;
            this.dayLabel = dayLabel;
            this.workDate = workDate;
            this.workDateText = workDateText;
            this.startTime = startTime;
            this.startTimeText = startTimeText;
            this.endTime = endTime;
            this.endTimeText = endTimeText;
            this.shiftCode = shiftCode;
            this.shiftLabel = shiftLabel;
            this.maxPatients = maxPatients;
            this.status = status;
            this.temporary = temporary;
        }

        public int getShiftId() { return shiftId; }
        public int getDoctorId() { return doctorId; }
        public String getDoctorName() { return doctorName; }
        public String getSpecialization() { return specialization; }
        public int getDayOfWeek() { return dayOfWeek; }
        public String getDayLabel() { return dayLabel; }
        public LocalDate getWorkDate() { return workDate; }
        public String getWorkDateText() { return workDateText; }
        public LocalTime getStartTime() { return startTime; }
        public String getStartTimeText() { return startTimeText; }
        public LocalTime getEndTime() { return endTime; }
        public String getEndTimeText() { return endTimeText; }
        public String getShiftCode() { return shiftCode; }
        public String getShiftLabel() { return shiftLabel; }
        public int getMaxPatients() { return maxPatients; }
        public String getStatus() { return status; }
        public boolean isTemporary() { return temporary; }
    }

    private static class ShiftTime {

        private final LocalTime startTime;
        private final LocalTime endTime;

        private ShiftTime(LocalTime startTime, LocalTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }
}
