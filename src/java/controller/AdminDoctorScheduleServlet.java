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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import model.Doctor;
import model.DoctorShift;
import model.Role;
import model.User;

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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req, resp)) {
            return;
        }
        loadPage(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req, resp)) {
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
            req.setAttribute("error", "Không thể cập nhật lịch làm việc: " + e.getMessage());
        }

        loadPage(req, resp);
    }

    private boolean isAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("account") == null) {
            resp.sendRedirect(req.getContextPath() + "/pages/auth/login.jsp");
            return false;
        }

        User account = (User) session.getAttribute("account");
        if (account.getRole() != Role.admin) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Chỉ quản trị viên mới được truy cập");
            return false;
        }
        return true;
    }

    private void loadPage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DoctorDAO doctorDAO = new DoctorDAO();
        List<Doctor> doctors = doctorDAO.getAllDoctorsForSchedule();
        List<Doctor> activeDoctors = doctorDAO.getActiveDoctorsForSchedule();

        String keyword = trim(firstNonBlank(req.getParameter("filterKeyword"), req.getParameter("keyword")));
        Integer selectedDay = parseNullableDay(firstNonBlank(req.getParameter("filterDayOfWeek"), req.getParameter("dayOfWeek")));
        int weekOffset = parseInt(firstNonBlank(req.getParameter("filterWeekOffset"), req.getParameter("weekOffset")), 0);

        String selectedShiftType = normalizeShiftType(req.getParameter("filterShiftType"));
        if (selectedShiftType.isEmpty() && "GET".equalsIgnoreCase(req.getMethod())) {
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
        req.getRequestDispatcher(VIEW_PATH).forward(req, resp);
    }

    private void handleAddShift(HttpServletRequest req, DoctorDAO doctorDAO) throws SQLException {
        int doctorId = requiredInt(req.getParameter("doctorId"), "Vui lòng chọn bác sĩ");
        int dayOfWeek = requiredInt(req.getParameter("dayOfWeek"), "Vui lòng chọn thứ");
        ShiftTime shiftTime = resolveShiftType(req.getParameter("shiftType"));
        int maxPatients = requiredInt(req.getParameter("maxPatients"), "Vui lòng nhập số bệnh nhân tối đa");

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
        req.setAttribute("success", "Đã thêm ca làm việc thành công");
    }

    private void handleUpdateShift(HttpServletRequest req, DoctorDAO doctorDAO) throws SQLException {
        int shiftId = requiredInt(req.getParameter("shiftId"), "Thiếu mã ca làm việc");
        int doctorId = requiredInt(req.getParameter("doctorId"), "Vui lòng chọn bác sĩ");
        int dayOfWeek = requiredInt(req.getParameter("dayOfWeek"), "Vui lòng chọn thứ");
        ShiftTime shiftTime = resolveShiftType(req.getParameter("shiftType"));
        int maxPatients = requiredInt(req.getParameter("maxPatients"), "Vui lòng nhập số bệnh nhân tối đa");

        validateShift(dayOfWeek, shiftTime.startTime, shiftTime.endTime, maxPatients);

        if (doctorDAO.hasShiftConflict(doctorId, dayOfWeek, shiftTime.startTime, shiftTime.endTime, shiftId)) {
            throw new IllegalArgumentException("Khung giờ bị trùng với ca làm việc đã có");
        }

        doctorDAO.updateDoctorShift(shiftId, dayOfWeek, shiftTime.startTime, shiftTime.endTime, maxPatients);
        req.setAttribute("success", "Đã cập nhật ca làm việc thành công");
    }

    private void handleDeleteShift(HttpServletRequest req, DoctorDAO doctorDAO) throws SQLException {
        int shiftId = requiredInt(req.getParameter("shiftId"), "Thiếu mã ca làm việc");
        doctorDAO.deleteDoctorShift(shiftId);
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
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Giờ bắt đầu phải nhỏ hơn giờ kết thúc");
        }
        if (maxPatients <= 0) {
            throw new IllegalArgumentException("Số bệnh nhân tối đa phải lớn hơn 0");
        }
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
        // Legacy data may store Sunday as 7 (java.time), while current UI uses 0.
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
                "Đã xếp"
        );
    }

    private String getShiftCode(LocalTime startTime, LocalTime endTime) {
        if (MORNING_START.equals(startTime) && MORNING_END.equals(endTime)) {
            return SHIFT_MORNING;
        }
        if (AFTERNOON_START.equals(startTime) && AFTERNOON_END.equals(endTime)) {
            return SHIFT_AFTERNOON;
        }
        return SHIFT_MORNING;
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

        public ScheduleViewItem(int shiftId, int doctorId, String doctorName, String specialization, int dayOfWeek,
                String dayLabel, LocalDate workDate, String workDateText, LocalTime startTime, String startTimeText,
                LocalTime endTime, String endTimeText, String shiftCode, String shiftLabel, int maxPatients, String status) {
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
