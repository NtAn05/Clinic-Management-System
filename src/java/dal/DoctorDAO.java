package dal;

import model.Doctor;
import model.DoctorShift;
import model.DoctorQueueItem;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import model.DoctorDashboardStats;
import model.ExamLabItem;
import model.ExaminationHistoryItem;
import model.MedicalRecord;
import model.Medicine;
import model.PrescriptionItem;
import model.ScheduleChangeRequest;
import model.ScheduleSwapShiftOption;

public class DoctorDAO extends DBContext {

    /* get doctor by id*/
    public Doctor getDoctorByUserId(int userId) {
        String sql = """
            SELECT d.doctor_id, d.user_id, d.specialization,
                   u.full_name, u.phone, u.email
            FROM doctors d
            JOIN users u ON d.user_id = u.user_id
            WHERE d.user_id = ?
        """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, userId);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                Doctor d = new Doctor();
                d.setDoctorId(rs.getInt("doctor_id"));
                d.setUserId(rs.getInt("user_id"));
                d.setSpecialization(rs.getString("specialization"));
                d.setFullName(rs.getString("full_name"));
                d.setPhone(rs.getString("phone"));
                d.setEmail(rs.getString("email"));
                return d;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // lịch làm việc
    public List<DoctorShift> getDoctorShifts(int doctorId) {
        List<DoctorShift> list = new ArrayList<>();

        String sql = """
            SELECT shift_id, doctor_id, day_of_week,
                   start_time, end_time, max_patients
            FROM doctor_shifts
            WHERE doctor_id = ?
            ORDER BY day_of_week, start_time
        """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, doctorId);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                DoctorShift s = new DoctorShift();
                s.setShiftId(rs.getInt("shift_id"));
                s.setDoctorId(rs.getInt("doctor_id"));
                s.setDayOfWeek(rs.getInt("day_of_week"));
                s.setStartTime(rs.getTime("start_time").toLocalTime());
                s.setEndTime(rs.getTime("end_time").toLocalTime());
                s.setMaxPatients(rs.getInt("max_patients"));
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Doctor> getAllDoctorsForSchedule() {
        syncDoctorProfilesForAllDoctorUsers();
        List<Doctor> list = new ArrayList<>();
        String sql = """
            SELECT d.doctor_id, d.user_id, d.specialization, u.full_name
            FROM doctors d
            JOIN users u ON d.user_id = u.user_id
            WHERE u.role = 'doctor'
            ORDER BY u.full_name
        """;

        try (PreparedStatement st = connection.prepareStatement(sql); ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Doctor d = new Doctor();
                d.setDoctorId(rs.getInt("doctor_id"));
                d.setUserId(rs.getInt("user_id"));
                d.setSpecialization(rs.getString("specialization"));
                d.setFullName(rs.getString("full_name"));
                list.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Doctor> getActiveDoctorsForSchedule() {
        syncDoctorProfilesForAllDoctorUsers();
        List<Doctor> list = new ArrayList<>();
        String sql = """
            SELECT d.doctor_id, d.user_id, d.specialization, u.full_name
            FROM doctors d
            JOIN users u ON d.user_id = u.user_id
            WHERE u.role = 'doctor' AND u.status = 'active'
            ORDER BY u.full_name
        """;

        try (PreparedStatement st = connection.prepareStatement(sql); ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Doctor d = new Doctor();
                d.setDoctorId(rs.getInt("doctor_id"));
                d.setUserId(rs.getInt("user_id"));
                d.setSpecialization(rs.getString("specialization"));
                d.setFullName(rs.getString("full_name"));
                list.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void syncDoctorProfilesForAllDoctorUsers() {
        String sql = """
            INSERT INTO doctors (user_id, specialization)
            SELECT u.user_id, 'Chưa cập nhật'
            FROM users u
            LEFT JOIN doctors d ON d.user_id = u.user_id
            WHERE u.role = 'doctor' AND d.doctor_id IS NULL
        """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.executeUpdate();
        } catch (SQLException e) {
            // Keep schedule page working even if sync fails on existing schemas.
            e.printStackTrace();
        }
    }

    public List<Doctor> getDoctorsForAdmin(String keyword, String specializationFilter, String qualificationFilter) {
        syncDoctorProfilesForAllDoctorUsers();
        List<Doctor> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT d.doctor_id, d.user_id, d.specialization, d.qualification, d.experience_years, d.price_booking, d.rating,
                   u.full_name, u.phone, u.email
            FROM doctors d
            JOIN users u ON d.user_id = u.user_id
            WHERE u.role = 'doctor'
        """);

        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (u.full_name LIKE ? OR u.phone LIKE ? OR u.email LIKE ?)");
            String like = "%" + keyword.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (specializationFilter != null && !specializationFilter.isBlank()) {
            sql.append(" AND d.specialization = ?");
            params.add(specializationFilter.trim());
        }
        if (qualificationFilter != null && !qualificationFilter.isBlank()) {
            sql.append(" AND d.qualification = ?");
            params.add(qualificationFilter.trim());
        }
        sql.append(" ORDER BY u.full_name");

        try (PreparedStatement st = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Object param : params) {
                st.setObject(idx++, param);
            }
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Doctor d = new Doctor();
                    d.setDoctorId(rs.getInt("doctor_id"));
                    d.setUserId(rs.getInt("user_id"));
                    d.setSpecialization(rs.getString("specialization"));
                    d.setQualification(rs.getString("qualification"));
                    d.setExperience_years(rs.getInt("experience_years"));
                    d.setPrice(rs.getDouble("price_booking"));
                    d.setRating(rs.getDouble("rating"));
                    d.setFullName(rs.getString("full_name"));
                    d.setPhone(rs.getString("phone"));
                    d.setEmail(rs.getString("email"));
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getDistinctDoctorSpecializations() {
        List<String> list = new ArrayList<>();
        String sql = """
            SELECT DISTINCT specialization
            FROM doctors
            WHERE specialization IS NOT NULL AND specialization <> ''
            ORDER BY specialization
        """;
        try (PreparedStatement st = connection.prepareStatement(sql); ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("specialization"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getDistinctDoctorQualifications() {
        List<String> list = new ArrayList<>();
        String sql = """
            SELECT DISTINCT qualification
            FROM doctors
            WHERE qualification IS NOT NULL AND qualification <> ''
            ORDER BY qualification
        """;
        try (PreparedStatement st = connection.prepareStatement(sql); ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("qualification"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Doctor getDoctorByIdForAdmin(int doctorId) {
        String sql = """
            SELECT d.doctor_id, d.user_id, d.specialization, d.qualification, d.experience_years, d.price_booking, d.rating,
                   u.full_name, u.phone, u.email
            FROM doctors d
            JOIN users u ON d.user_id = u.user_id
            WHERE d.doctor_id = ? AND u.role = 'doctor'
            LIMIT 1
        """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, doctorId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Doctor d = new Doctor();
                    d.setDoctorId(rs.getInt("doctor_id"));
                    d.setUserId(rs.getInt("user_id"));
                    d.setSpecialization(rs.getString("specialization"));
                    d.setQualification(rs.getString("qualification"));
                    d.setExperience_years(rs.getInt("experience_years"));
                    d.setPrice(rs.getDouble("price_booking"));
                    d.setRating(rs.getDouble("rating"));
                    d.setFullName(rs.getString("full_name"));
                    d.setPhone(rs.getString("phone"));
                    d.setEmail(rs.getString("email"));
                    return d;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int createDoctorWithUser(String fullName, String phone, String email, String password,
            String specialization, String qualification, int experienceYears, int priceBooking) throws SQLException {
        String sqlUser = """
            INSERT INTO users (full_name, phone, email, password_hash, role, status)
            VALUES (?, ?, ?, ?, 'doctor', 'active')
        """;
        String sqlDoctor = """
            INSERT INTO doctors (user_id, specialization, qualification, experience_years, price_booking)
            VALUES (?, ?, ?, ?, ?)
        """;

        boolean originalAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            int userId;

            try (PreparedStatement userSt = connection.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                userSt.setString(1, fullName);
                userSt.setString(2, phone);
                userSt.setString(3, email);
                userSt.setString(4, password);
                int affected = userSt.executeUpdate();
                if (affected == 0) {
                    connection.rollback();
                    return 0;
                }
                try (ResultSet keys = userSt.getGeneratedKeys()) {
                    if (!keys.next()) {
                        connection.rollback();
                        return 0;
                    }
                    userId = keys.getInt(1);
                }
            }

            try (PreparedStatement doctorSt = connection.prepareStatement(sqlDoctor)) {
                doctorSt.setInt(1, userId);
                doctorSt.setString(2, specialization);
                doctorSt.setString(3, qualification);
                doctorSt.setInt(4, experienceYears);
                doctorSt.setInt(5, priceBooking);
                int affected = doctorSt.executeUpdate();
                if (affected == 0) {
                    connection.rollback();
                    return 0;
                }
            }

            connection.commit();
            return userId;
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    public boolean updateDoctorAndUser(int doctorId, String fullName, String phone, String email,
            String specialization, String qualification, int experienceYears, int priceBooking) throws SQLException {
        String sqlGet = "SELECT user_id FROM doctors WHERE doctor_id = ? LIMIT 1";
        String sqlUser = "UPDATE users SET full_name = ?, phone = ?, email = ? WHERE user_id = ? AND role = 'doctor'";
        String sqlDoctor = """
            UPDATE doctors
            SET specialization = ?, qualification = ?, experience_years = ?, price_booking = ?
            WHERE doctor_id = ?
        """;

        boolean originalAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            Integer userId = null;
            try (PreparedStatement st = connection.prepareStatement(sqlGet)) {
                st.setInt(1, doctorId);
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("user_id");
                    }
                }
            }
            if (userId == null) {
                connection.rollback();
                return false;
            }

            try (PreparedStatement userSt = connection.prepareStatement(sqlUser)) {
                userSt.setString(1, fullName);
                userSt.setString(2, phone);
                userSt.setString(3, email);
                userSt.setInt(4, userId);
                if (userSt.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
            }

            try (PreparedStatement doctorSt = connection.prepareStatement(sqlDoctor)) {
                doctorSt.setString(1, specialization);
                doctorSt.setString(2, qualification);
                doctorSt.setInt(3, experienceYears);
                doctorSt.setInt(4, priceBooking);
                doctorSt.setInt(5, doctorId);
                if (doctorSt.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
            }

            connection.commit();
            return true;
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    public void addDoctorShift(int doctorId, int dayOfWeek, LocalTime startTime, LocalTime endTime, int maxPatients) throws SQLException {
        String sql = """
            INSERT INTO doctor_shifts (doctor_id, day_of_week, start_time, end_time, max_patients)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, doctorId);
            st.setInt(2, dayOfWeek);
            st.setTime(3, Time.valueOf(startTime));
            st.setTime(4, Time.valueOf(endTime));
            st.setInt(5, maxPatients);
            st.executeUpdate();
        }
    }

    public boolean doctorExists(int doctorId) {
        String sql = "SELECT 1 FROM doctors WHERE doctor_id = ? LIMIT 1";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, doctorId);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isDoctorActive(int doctorId) {
        String sql = """
            SELECT 1
            FROM doctors d
            JOIN users u ON d.user_id = u.user_id
            WHERE d.doctor_id = ? AND u.role = 'doctor' AND u.status = 'active'
            LIMIT 1
        """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, doctorId);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public DoctorShift getDoctorShiftById(int shiftId) {
        String sql = """
            SELECT shift_id, doctor_id, day_of_week, start_time, end_time, max_patients
            FROM doctor_shifts
            WHERE shift_id = ?
            LIMIT 1
        """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, shiftId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    DoctorShift s = new DoctorShift();
                    s.setShiftId(rs.getInt("shift_id"));
                    s.setDoctorId(rs.getInt("doctor_id"));
                    s.setDayOfWeek(rs.getInt("day_of_week"));
                    s.setStartTime(rs.getTime("start_time").toLocalTime());
                    s.setEndTime(rs.getTime("end_time").toLocalTime());
                    s.setMaxPatients(rs.getInt("max_patients"));
                    return s;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean shiftExists(int shiftId) {
        String sql = "SELECT 1 FROM doctor_shifts WHERE shift_id = ? LIMIT 1";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, shiftId);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isShiftOwnedByDoctor(int shiftId, int doctorId) {
        String sql = """
            SELECT 1
            FROM doctor_shifts
            WHERE shift_id = ? AND doctor_id = ?
            LIMIT 1
        """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, shiftId);
            st.setInt(2, doctorId);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasUpcomingAppointmentsForShift(int shiftId) {
        String sql = """
            SELECT 1
            FROM appointments
            WHERE shift_id = ?
              AND (
                  appointment_date > CURRENT_DATE
                  OR (appointment_date = CURRENT_DATE AND appointment_time >= CURRENT_TIME)
              )
            LIMIT 1
        """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, shiftId);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasAnyAppointmentsForShift(int shiftId) {
        String sql = "SELECT 1 FROM appointments WHERE shift_id = ? LIMIT 1";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, shiftId);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasAppointmentsForShiftOnDate(int shiftId, Date workDate) {
        String sql = "SELECT 1 FROM appointments WHERE shift_id = ? AND appointment_date = ? LIMIT 1";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, shiftId);
            st.setDate(2, workDate);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public void updateDoctorShift(int shiftId, int dayOfWeek, LocalTime startTime, LocalTime endTime, int maxPatients) throws SQLException {
        String sql = """
            UPDATE doctor_shifts
            SET day_of_week = ?, start_time = ?, end_time = ?, max_patients = ?
            WHERE shift_id = ?
        """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, dayOfWeek);
            st.setTime(2, Time.valueOf(startTime));
            st.setTime(3, Time.valueOf(endTime));
            st.setInt(4, maxPatients);
            st.setInt(5, shiftId);
            st.executeUpdate();
        }
    }

    public void deleteDoctorShift(int shiftId) throws SQLException {
        String sql = "DELETE FROM doctor_shifts WHERE shift_id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, shiftId);
            st.executeUpdate();
        }
    }

    public boolean hasShiftConflict(int doctorId, int dayOfWeek, LocalTime startTime, LocalTime endTime, Integer excludeShiftId) {
        StringBuilder sql = new StringBuilder("""
            SELECT 1
            FROM doctor_shifts
            WHERE doctor_id = ?
              AND day_of_week = ?
              AND start_time < ?
              AND end_time > ?
        """);
        if (excludeShiftId != null) {
            sql.append(" AND shift_id <> ? ");
        }
        sql.append(" LIMIT 1 ");

        try (PreparedStatement st = connection.prepareStatement(sql.toString())) {
            int index = 1;
            st.setInt(index++, doctorId);
            st.setInt(index++, dayOfWeek);
            st.setTime(index++, Time.valueOf(endTime));
            st.setTime(index++, Time.valueOf(startTime));
            if (excludeShiftId != null) {
                st.setInt(index, excludeShiftId);
            }

            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* CẬP NHẬT TRẠNG THÁI HÀNG ĐỢI */
    public void updateQueueStatus(long appointmentId, String status) {
        String sql = """
            UPDATE exam_queue
            SET status = ?
            WHERE appointment_id = ?
        """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, status);
            st.setLong(2, appointmentId);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* BẮT ĐẦU KHÁM */
    public void startExamination(long appointmentId) {
        String sql = """
            UPDATE exam_queue
            SET status = 'examining'
            WHERE appointment_id = ?
        """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, appointmentId);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void finishExamination(long appointmentId) {
        String sql = """
            UPDATE exam_queue
            SET status = 'done'
            WHERE appointment_id = ?
        """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, appointmentId);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DANH SÁCH CHỜ KHÁM CỦA BÁC SĨ
    public List<DoctorQueueItem> getTodayQueueByDoctor(int doctorId) { // được thay thế bởi getqueuewithfilterpaging
        List<DoctorQueueItem> list = new ArrayList<>();

        String sql = """
        SELECT 
            q.queue_position,
            p.full_name AS patient_name,
            p.gender,
            p.dob,
            a.symptom,
            q.status
        FROM exam_queue q
        JOIN appointments a ON q.appointment_id = a.appointment_id
        JOIN patients p ON a.patient_id = p.patient_id
        WHERE q.doctor_id = ?
        ORDER BY q.queue_position
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                DoctorQueueItem item = new DoctorQueueItem();
                item.setQueuePosition(rs.getInt("queue_position"));
                item.setPatientName(rs.getString("patient_name"));
                item.setGender(rs.getString("gender"));
                item.setDob(rs.getDate("dob"));
                item.setSymptom(rs.getString("symptom"));
                item.setStatus(rs.getString("status"));
                list.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<DoctorShift> getShiftsByDoctorAndDay(int doctorId, int dayOfWeek) {
        List<DoctorShift> list = new ArrayList<>();

        String sql = """
        SELECT shift_id, start_time, end_time, max_patients
        FROM doctor_shifts
        WHERE doctor_id = ? AND day_of_week = ?
    """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, doctorId);
            st.setInt(2, dayOfWeek);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                DoctorShift s = new DoctorShift();
                s.setShiftId(rs.getInt("shift_id"));
                s.setStartTime(rs.getTime("start_time").toLocalTime());
                s.setEndTime(rs.getTime("end_time").toLocalTime());
                s.setMaxPatients(rs.getInt("max_patients"));
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    
    public List<ScheduleSwapShiftOption> getSwapShiftOptionsByDate(int requesterDoctorId, int dayOfWeek) {
        List<ScheduleSwapShiftOption> list = new ArrayList<>();
        String sql = """
            SELECT s.shift_id, s.doctor_id, s.day_of_week, s.start_time, s.end_time,
                   u.full_name AS doctor_name
            FROM doctor_shifts s
            JOIN doctors d ON d.doctor_id = s.doctor_id
            JOIN users u ON u.user_id = d.user_id
            WHERE s.day_of_week = ?
              AND s.doctor_id <> ?
              AND u.role = 'doctor'
              AND u.status = 'active'
            ORDER BY u.full_name, s.start_time
        """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, dayOfWeek);
            st.setInt(2, requesterDoctorId);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                ScheduleSwapShiftOption option = new ScheduleSwapShiftOption();
                option.setShiftId(rs.getInt("shift_id"));
                option.setDoctorId(rs.getInt("doctor_id"));
                option.setDoctorName(rs.getString("doctor_name"));
                option.setDayOfWeek(rs.getInt("day_of_week"));
                option.setStartTime(rs.getTime("start_time").toLocalTime());
                option.setEndTime(rs.getTime("end_time").toLocalTime());
                list.add(option);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<ScheduleChangeRequest> getScheduleChangeRequestsByDoctor(int doctorId, int limit) {
        List<ScheduleChangeRequest> list = new ArrayList<>();
        String sql = """
            SELECT r.request_id, r.doctor_id, r.request_type, r.scope_type,
                   r.reason, r.status, r.requested_at, r.admin_note,
                   i.action_type, i.target_shift_id, i.work_date, i.day_of_week,
                   i.start_time, i.end_time, i.max_patients
            FROM schedule_change_requests r
            LEFT JOIN schedule_change_request_items i ON r.request_id = i.request_id
            WHERE r.doctor_id = ?
            ORDER BY r.requested_at DESC
            LIMIT ?
        """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, doctorId);
            st.setInt(2, limit);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                ScheduleChangeRequest request = new ScheduleChangeRequest();
                request.setRequestId(rs.getInt("request_id"));
                request.setDoctorId(rs.getInt("doctor_id"));
                request.setRequestType(rs.getString("request_type"));
                request.setScopeType(rs.getString("scope_type"));
                request.setReason(rs.getString("reason"));
                request.setStatus(rs.getString("status"));
                request.setRequestedAt(rs.getTimestamp("requested_at"));
                request.setAdminNote(rs.getString("admin_note"));
                request.setActionType(rs.getString("action_type"));

                int targetShiftId = rs.getInt("target_shift_id");
                request.setTargetShiftId(rs.wasNull() ? null : targetShiftId);

                request.setWorkDate(rs.getDate("work_date"));
                int dayOfWeek = rs.getInt("day_of_week");
                request.setDayOfWeek(rs.wasNull() ? null : dayOfWeek);

                Time startTime = rs.getTime("start_time");
                if (startTime != null) {
                    request.setStartTime(startTime.toLocalTime());
                }

                Time endTime = rs.getTime("end_time");
                if (endTime != null) {
                    request.setEndTime(endTime.toLocalTime());
                }

                int maxPatients = rs.getInt("max_patients");
                request.setMaxPatients(rs.wasNull() ? null : maxPatients);
                list.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<ScheduleChangeRequest> getScheduleChangeRequestsForAdmin(String statusFilter) {
        return getScheduleChangeRequestsForAdmin(statusFilter, "ALL", "ALL", "");
    }

    public List<ScheduleChangeRequest> getScheduleChangeRequestsForAdmin(
            String statusFilter,
            String requestTypeFilter,
            String actionTypeFilter,
            String keyword
    ) {
        List<ScheduleChangeRequest> list = new ArrayList<>();
        boolean hasStatusFilter = statusFilter != null && !statusFilter.isBlank() && !"ALL".equalsIgnoreCase(statusFilter);
        boolean hasRequestTypeFilter = requestTypeFilter != null && !requestTypeFilter.isBlank() && !"ALL".equalsIgnoreCase(requestTypeFilter);
        boolean hasActionTypeFilter = actionTypeFilter != null && !actionTypeFilter.isBlank() && !"ALL".equalsIgnoreCase(actionTypeFilter);
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        StringBuilder sql = new StringBuilder("""
            SELECT r.request_id, r.doctor_id, r.request_type, r.scope_type,
                   r.reason, r.status, r.requested_at, r.admin_note,
                   i.action_type, i.target_shift_id, i.work_date, i.day_of_week,
                   i.start_time, i.end_time, i.max_patients,
                   u.full_name AS doctor_name,
                   u_old.full_name AS old_doctor_name,
                   s_old.day_of_week AS old_day_of_week,
                   s_old.start_time AS old_start_time,
                   s_old.end_time AS old_end_time,
                   u_new.full_name AS new_doctor_name,
                   CASE
                       WHEN i.work_date IS NOT NULL AND s_old.day_of_week IS NOT NULL AND i.day_of_week IS NOT NULL
                       THEN DATE_ADD(i.work_date, INTERVAL (s_old.day_of_week - i.day_of_week) DAY)
                       ELSE NULL
                   END AS old_work_date
            FROM schedule_change_requests r
            JOIN doctors d ON r.doctor_id = d.doctor_id
            JOIN users u ON d.user_id = u.user_id
            LEFT JOIN schedule_change_request_items i ON r.request_id = i.request_id
            LEFT JOIN doctor_shifts s_old ON i.target_shift_id = s_old.shift_id
            LEFT JOIN doctors d_old ON s_old.doctor_id = d_old.doctor_id
            LEFT JOIN users u_old ON d_old.user_id = u_old.user_id
            LEFT JOIN doctor_shifts s_new ON s_new.shift_id = (
                SELECT s2.shift_id
                FROM doctor_shifts s2
                WHERE i.action_type = 'UPDATE'
                  AND s2.day_of_week = i.day_of_week
                  AND s2.start_time = i.start_time
                  AND s2.end_time = i.end_time
                  AND s2.doctor_id <> r.doctor_id
                ORDER BY s2.shift_id
                LIMIT 1
            )
            LEFT JOIN doctors d_new ON s_new.doctor_id = d_new.doctor_id
            LEFT JOIN users u_new ON d_new.user_id = u_new.user_id
        """);

        sql.append(" WHERE 1=1 ");
        if (hasStatusFilter) {
            sql.append(" AND r.status = ? ");
        }
        if (hasRequestTypeFilter) {
            sql.append(" AND r.request_type = ? ");
        }
        if (hasActionTypeFilter) {
            sql.append(" AND i.action_type = ? ");
        }
        if (hasKeyword) {
            sql.append(" AND (u.full_name LIKE ? OR r.reason LIKE ?) ");
        }
        sql.append(" ORDER BY r.requested_at DESC ");

        try (PreparedStatement st = connection.prepareStatement(sql.toString())) {
            int index = 1;
            if (hasStatusFilter) {
                st.setString(index++, statusFilter.trim().toUpperCase());
            }
            if (hasRequestTypeFilter) {
                st.setString(index++, requestTypeFilter.trim().toUpperCase());
            }
            if (hasActionTypeFilter) {
                st.setString(index++, actionTypeFilter.trim().toUpperCase());
            }
            if (hasKeyword) {
                String keywordLike = "%" + keyword.trim() + "%";
                st.setString(index++, keywordLike);
                st.setString(index++, keywordLike);
            }
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                ScheduleChangeRequest request = new ScheduleChangeRequest();
                request.setRequestId(rs.getInt("request_id"));
                request.setDoctorId(rs.getInt("doctor_id"));
                request.setDoctorName(rs.getString("doctor_name"));
                request.setRequestType(rs.getString("request_type"));
                request.setScopeType(rs.getString("scope_type"));
                request.setReason(rs.getString("reason"));
                request.setStatus(rs.getString("status"));
                request.setRequestedAt(rs.getTimestamp("requested_at"));
                request.setAdminNote(rs.getString("admin_note"));
                request.setActionType(rs.getString("action_type"));

                int targetShiftId = rs.getInt("target_shift_id");
                request.setTargetShiftId(rs.wasNull() ? null : targetShiftId);

                request.setWorkDate(rs.getDate("work_date"));
                int dayOfWeek = rs.getInt("day_of_week");
                request.setDayOfWeek(rs.wasNull() ? null : dayOfWeek);

                Time startTime = rs.getTime("start_time");
                if (startTime != null) {
                    request.setStartTime(startTime.toLocalTime());
                }

                Time endTime = rs.getTime("end_time");
                if (endTime != null) {
                    request.setEndTime(endTime.toLocalTime());
                }

                int maxPatients = rs.getInt("max_patients");
                request.setMaxPatients(rs.wasNull() ? null : maxPatients);

                request.setOldDoctorName(rs.getString("old_doctor_name"));
                int oldDayOfWeek = rs.getInt("old_day_of_week");
                request.setOldDayOfWeek(rs.wasNull() ? null : oldDayOfWeek);

                Time oldStartTime = rs.getTime("old_start_time");
                if (oldStartTime != null) {
                    request.setOldStartTime(oldStartTime.toLocalTime());
                }

                Time oldEndTime = rs.getTime("old_end_time");
                if (oldEndTime != null) {
                    request.setOldEndTime(oldEndTime.toLocalTime());
                }

                request.setNewDoctorName(rs.getString("new_doctor_name"));
                request.setOldWorkDate(rs.getDate("old_work_date"));
                list.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public int countPendingScheduleChangeRequests() {
        String sql = "SELECT COUNT(*) FROM schedule_change_requests WHERE status = 'PENDING'";
        try (PreparedStatement st = connection.prepareStatement(sql); ResultSet rs = st.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean reviewScheduleChangeRequest(int requestId, String decision, String adminNote) {
        String normalizedDecision = decision == null ? "" : decision.trim().toUpperCase();
        boolean shouldApply = "APPROVED".equals(normalizedDecision);
        String reviewSql = """
            UPDATE schedule_change_requests
            SET status = ?, admin_note = ?
            WHERE request_id = ? AND status = 'PENDING'
        """;

        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            PendingScheduleReview request = getPendingScheduleReviewForUpdate(requestId);
            if (request == null) {
                connection.rollback();
                return false;
            }

            if (shouldApply && !applyApprovedScheduleRequest(request)) {
                connection.rollback();
                return false;
            }

            try (PreparedStatement st = connection.prepareStatement(reviewSql)) {
                st.setString(1, normalizedDecision);
                if (adminNote == null || adminNote.isBlank()) {
                    st.setNull(2, Types.VARCHAR);
                } else {
                    st.setString(2, adminNote.trim());
                }
                st.setInt(3, requestId);
                if (st.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(originalAutoCommit);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private PendingScheduleReview getPendingScheduleReviewForUpdate(int requestId) throws SQLException {
        String sql = """
            SELECT r.request_id, r.doctor_id, r.request_type, r.scope_type,
                   i.action_type, i.target_shift_id, i.work_date, i.day_of_week,
                   i.start_time, i.end_time, i.max_patients
            FROM schedule_change_requests r
            LEFT JOIN schedule_change_request_items i ON r.request_id = i.request_id
            WHERE r.request_id = ? AND r.status = 'PENDING'
            LIMIT 1
            FOR UPDATE
        """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, requestId);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                PendingScheduleReview review = new PendingScheduleReview();
                review.requestId = rs.getInt("request_id");
                review.doctorId = rs.getInt("doctor_id");
                review.requestType = rs.getString("request_type");
                review.scopeType = rs.getString("scope_type");
                review.actionType = rs.getString("action_type");

                int targetShiftId = rs.getInt("target_shift_id");
                review.targetShiftId = rs.wasNull() ? null : targetShiftId;

                review.workDate = rs.getDate("work_date");
                int dayOfWeek = rs.getInt("day_of_week");
                review.dayOfWeek = rs.wasNull() ? null : dayOfWeek;

                Time startTime = rs.getTime("start_time");
                review.startTime = startTime == null ? null : startTime.toLocalTime();

                Time endTime = rs.getTime("end_time");
                review.endTime = endTime == null ? null : endTime.toLocalTime();

                int maxPatients = rs.getInt("max_patients");
                review.maxPatients = rs.wasNull() ? null : maxPatients;
                return review;
            }
        }
    }

    private boolean applyApprovedScheduleRequest(PendingScheduleReview request) throws SQLException {
        if (request.actionType == null || request.actionType.isBlank()) {
            return true;
        }

        // One-date/temporary requests are rendered as weekly overlays on schedule view,
        // so they should not mutate fixed weekly templates in doctor_shifts.
        if ("TEMPORARY".equalsIgnoreCase(request.requestType)
                || "ONE_DATE".equalsIgnoreCase(request.scopeType)) {
            return true;
        }

        String actionType = request.actionType.trim().toUpperCase();
        switch (actionType) {
            case "ADD":
                return applyApprovedAddRequest(request);
            case "REMOVE":
                return applyApprovedRemoveRequest(request);
            case "UPDATE":
                return applyApprovedUpdateRequest(request);
            default:
                return true;
        }
    }

    private boolean applyApprovedAddRequest(PendingScheduleReview request) throws SQLException {
        if (request.dayOfWeek == null || request.startTime == null || request.endTime == null || request.maxPatients == null) {
            return false;
        }
        if (hasShiftConflict(request.doctorId, request.dayOfWeek, request.startTime, request.endTime, null)) {
            return false;
        }

        String sql = """
            INSERT INTO doctor_shifts (doctor_id, day_of_week, start_time, end_time, max_patients)
            VALUES (?, ?, ?, ?, ?)
        """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, request.doctorId);
            st.setInt(2, request.dayOfWeek);
            st.setTime(3, Time.valueOf(request.startTime));
            st.setTime(4, Time.valueOf(request.endTime));
            st.setInt(5, request.maxPatients);
            return st.executeUpdate() > 0;
        }
    }

    private boolean applyApprovedRemoveRequest(PendingScheduleReview request) throws SQLException {
        if (request.targetShiftId == null) {
            // Temporary remove without a weekly shift anchor cannot be persisted to doctor_shifts.
            return true;
        }

        DoctorShift shift = getDoctorShiftByIdForUpdate(request.targetShiftId);
        if (shift == null || shift.getDoctorId() != request.doctorId) {
            return false;
        }

        String deleteSql = "DELETE FROM doctor_shifts WHERE shift_id = ?";
        try (PreparedStatement st = connection.prepareStatement(deleteSql)) {
            st.setInt(1, request.targetShiftId);
            return st.executeUpdate() > 0;
        }
    }

    private boolean applyApprovedUpdateRequest(PendingScheduleReview request) throws SQLException {
        if (request.targetShiftId == null || request.dayOfWeek == null || request.startTime == null || request.endTime == null) {
            return false;
        }

        DoctorShift requesterShift = getDoctorShiftByIdForUpdate(request.targetShiftId);
        if (requesterShift == null || requesterShift.getDoctorId() != request.doctorId) {
            return false;
        }

        DoctorShift counterpart = findCounterpartShiftForSwap(
                request.doctorId, request.targetShiftId, request.dayOfWeek, request.startTime, request.endTime
        );

        int requesterMaxPatients = request.maxPatients != null ? request.maxPatients : requesterShift.getMaxPatients();
        if (!updateShiftById(
                requesterShift.getShiftId(),
                request.dayOfWeek,
                request.startTime,
                request.endTime,
                requesterMaxPatients
        )) {
            return false;
        }

        if (counterpart != null) {
            return updateShiftById(
                    counterpart.getShiftId(),
                    requesterShift.getDayOfWeek(),
                    requesterShift.getStartTime(),
                    requesterShift.getEndTime(),
                    counterpart.getMaxPatients()
            );
        }
        return true;
    }

    private DoctorShift getDoctorShiftByIdForUpdate(int shiftId) throws SQLException {
        String sql = """
            SELECT shift_id, doctor_id, day_of_week, start_time, end_time, max_patients
            FROM doctor_shifts
            WHERE shift_id = ?
            LIMIT 1
            FOR UPDATE
        """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, shiftId);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                DoctorShift s = new DoctorShift();
                s.setShiftId(rs.getInt("shift_id"));
                s.setDoctorId(rs.getInt("doctor_id"));
                s.setDayOfWeek(rs.getInt("day_of_week"));
                s.setStartTime(rs.getTime("start_time").toLocalTime());
                s.setEndTime(rs.getTime("end_time").toLocalTime());
                s.setMaxPatients(rs.getInt("max_patients"));
                return s;
            }
        }
    }

    private DoctorShift findCounterpartShiftForSwap(int requesterDoctorId, int requesterShiftId, int dayOfWeek, LocalTime startTime, LocalTime endTime) throws SQLException {
        String sql = """
            SELECT shift_id, doctor_id, day_of_week, start_time, end_time, max_patients
            FROM doctor_shifts
            WHERE day_of_week = ?
              AND start_time = ?
              AND end_time = ?
              AND doctor_id <> ?
              AND shift_id <> ?
            ORDER BY shift_id
            LIMIT 1
            FOR UPDATE
        """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, dayOfWeek);
            st.setTime(2, Time.valueOf(startTime));
            st.setTime(3, Time.valueOf(endTime));
            st.setInt(4, requesterDoctorId);
            st.setInt(5, requesterShiftId);

            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                DoctorShift s = new DoctorShift();
                s.setShiftId(rs.getInt("shift_id"));
                s.setDoctorId(rs.getInt("doctor_id"));
                s.setDayOfWeek(rs.getInt("day_of_week"));
                s.setStartTime(rs.getTime("start_time").toLocalTime());
                s.setEndTime(rs.getTime("end_time").toLocalTime());
                s.setMaxPatients(rs.getInt("max_patients"));
                return s;
            }
        }
    }

    private boolean updateShiftById(int shiftId, int dayOfWeek, LocalTime startTime, LocalTime endTime, int maxPatients) throws SQLException {
        String sql = """
            UPDATE doctor_shifts
            SET day_of_week = ?, start_time = ?, end_time = ?, max_patients = ?
            WHERE shift_id = ?
        """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, dayOfWeek);
            st.setTime(2, Time.valueOf(startTime));
            st.setTime(3, Time.valueOf(endTime));
            st.setInt(4, maxPatients);
            st.setInt(5, shiftId);
            return st.executeUpdate() > 0;
        }
    }

    private static final class PendingScheduleReview {

        private int requestId;
        private int doctorId;
        private String requestType;
        private String scopeType;
        private String actionType;
        private Integer targetShiftId;
        private Date workDate;
        private Integer dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
        private Integer maxPatients;
    }

    public boolean createScheduleChangeRequest(
            int doctorId,
            String requestType,
            String scopeType,
            String reason,
            String actionType,
            Integer targetShiftId,
            Date workDate,
            Integer dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            Integer maxPatients
    ) {
        String insertRequestSql = """
            INSERT INTO schedule_change_requests
            (doctor_id, request_type, scope_type, reason, status, requested_at)
            VALUES (?, ?, ?, ?, 'PENDING', NOW())
        """;

        String insertItemSql = """
            INSERT INTO schedule_change_request_items
            (request_id, action_type, target_shift_id, work_date, day_of_week, start_time, end_time, max_patients)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try {
            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            int requestId;
            try (PreparedStatement insertRequest = connection.prepareStatement(insertRequestSql, Statement.RETURN_GENERATED_KEYS)) {
                insertRequest.setInt(1, doctorId);
                insertRequest.setString(2, requestType);
                insertRequest.setString(3, scopeType);
                insertRequest.setString(4, reason);
                if (insertRequest.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }

                try (ResultSet keys = insertRequest.getGeneratedKeys()) {
                    if (!keys.next()) {
                        connection.rollback();
                        return false;
                    }
                    requestId = keys.getInt(1);
                }
            }

            try (PreparedStatement insertItem = connection.prepareStatement(insertItemSql)) {
                insertItem.setInt(1, requestId);
                insertItem.setString(2, actionType);

                if (targetShiftId == null) {
                    insertItem.setNull(3, Types.INTEGER);
                } else {
                    insertItem.setInt(3, targetShiftId);
                }

                if (workDate == null) {
                    insertItem.setNull(4, Types.DATE);
                } else {
                    insertItem.setDate(4, workDate);
                }

                if (dayOfWeek == null) {
                    insertItem.setNull(5, Types.TINYINT);
                } else {
                    insertItem.setInt(5, dayOfWeek);
                }

                if (startTime == null) {
                    insertItem.setNull(6, Types.TIME);
                } else {
                    insertItem.setTime(6, Time.valueOf(startTime));
                }

                if (endTime == null) {
                    insertItem.setNull(7, Types.TIME);
                } else {
                    insertItem.setTime(7, Time.valueOf(endTime));
                }

                if (maxPatients == null) {
                    insertItem.setNull(8, Types.INTEGER);
                } else {
                    insertItem.setInt(8, maxPatients);
                }

                if (insertItem.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
            }

            connection.commit();
            connection.setAutoCommit(originalAutoCommit);
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }

    //thống kê số liệu 
    public DoctorDashboardStats getDashboardStats(int doctorId) {
        DoctorDashboardStats stats = new DoctorDashboardStats();
        String sql = """
        SELECT 
            COUNT(*) AS total,
            SUM(CASE WHEN q.status = 'waiting' THEN 1 ELSE 0 END) AS waiting,
            SUM(CASE WHEN q.status = 'examining' THEN 1 ELSE 0 END) AS examining,
            SUM(CASE WHEN q.status = 'done' THEN 1 ELSE 0 END) AS done
            FROM exam_queue q
            JOIN appointments a ON q.appointment_id = a.appointment_id
            WHERE q.doctor_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("total");
                int done = rs.getInt("done");
                stats.setTotal(total);
                stats.setWaiting(rs.getInt("waiting"));
                stats.setExamining(rs.getInt("examining"));
                stats.setDone(done);
                stats.setCompletionRate(total == 0 ? 0 : (done * 100.0) / total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    // lọc theo keyword , trạng thái
    public List<DoctorQueueItem> getQueueByDoctorWithFilter(
            int doctorId,
            String status,
            String keyword
    ) {
        return getQueueByDoctorWithFilterPaging(doctorId, status, keyword, 1, Integer.MAX_VALUE);
    }

    // tính tổng số bản ghi theo bộ lọc để controller tính totalPages cho phân trang.
    // dùng query COUNT(*) cùng điều kiện status/keyword giống query lấy dữ liệu trang.
    public int countQueueByDoctorWithFilter(int doctorId, String status, String keyword) {
        StringBuilder sql = new StringBuilder("""
        SELECT COUNT(*)
        FROM exam_queue q
        JOIN appointments a ON q.appointment_id = a.appointment_id
        JOIN patients p ON a.patient_id = p.patient_id
        WHERE q.doctor_id = ?
    """);

        boolean hasStatusFilter = status != null && !status.equals("all");
        boolean hasKeywordFilter = keyword != null && !keyword.isBlank();

        if (hasStatusFilter) {
            sql.append(" AND q.status = ? ");
        }

        if (hasKeywordFilter) {
            sql.append(" AND p.full_name LIKE ? ");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int index = 1;
            ps.setInt(index++, doctorId);

            if (hasStatusFilter) {
                ps.setString(index++, status);
            }

            if (hasKeywordFilter) {
                ps.setString(index, "%" + keyword + "%");
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // Pagination, hiển thị theo phân trang và bộ lọc.
    // LIMIT/OFFSET sau khi chuẩn hóa page/pageSize và bind điều kiện động.
    public List<DoctorQueueItem> getQueueByDoctorWithFilterPaging(
            int doctorId,
            String status,
            String keyword,
            int page,
            int pageSize
    ) {
        List<DoctorQueueItem> list = new ArrayList<>();

        int safePage = Math.max(page, 1);
        int safePageSize = pageSize <= 0 ? 10 : pageSize;
        int offset = (safePage - 1) * safePageSize;

        StringBuilder sql = new StringBuilder("""
        SELECT 
            q.queue_position,
            q.appointment_id,
            p.patient_id,
            p.full_name AS patient_name,
            p.gender,
            p.dob,
            a.symptom,
            q.status
        FROM exam_queue q
        JOIN appointments a ON q.appointment_id = a.appointment_id
        JOIN patients p ON a.patient_id = p.patient_id
        WHERE q.doctor_id = ?
    """);

        boolean hasStatusFilter = status != null && !status.equals("all");
        boolean hasKeywordFilter = keyword != null && !keyword.isBlank();

        if (hasStatusFilter) {
            sql.append(" AND q.status = ? ");
        }

        if (hasKeywordFilter) {
            sql.append(" AND p.full_name LIKE ? ");
        }

        sql.append(" ORDER BY q.queue_position LIMIT ? OFFSET ? ");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int index = 1;
            ps.setInt(index++, doctorId);

            if (hasStatusFilter) {
                ps.setString(index++, status);
            }

            if (hasKeywordFilter) {
                ps.setString(index++, "%" + keyword + "%");
            }

            ps.setInt(index++, safePageSize);
            ps.setInt(index, offset);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DoctorQueueItem item = new DoctorQueueItem();
                item.setQueuePosition(rs.getInt("queue_position"));
                item.setAppointmentId(rs.getLong("appointment_id"));
                item.setPatientId(rs.getLong("patient_id"));
                item.setPatientName(rs.getString("patient_name"));
                item.setGender(rs.getString("gender"));
                item.setDob(rs.getDate("dob"));
                item.setSymptom(rs.getString("symptom"));
                item.setStatus(rs.getString("status"));
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Xác thực appointment có nằm trong queue của chính bác sĩ hay không.
    // Join exam_queue/appointments/patients và lọc theo doctor_id + appointment_id.
    public DoctorQueueItem getQueueItemByAppointment(int doctorId, long appointmentId) {
        String sql = """
            SELECT
                q.queue_position,
                q.appointment_id,
                p.patient_id,
                p.full_name AS patient_name,
                p.gender,
                p.dob,
                a.symptom,
                q.status
            FROM exam_queue q
            JOIN appointments a ON q.appointment_id = a.appointment_id
            JOIN patients p ON a.patient_id = p.patient_id
            WHERE q.doctor_id = ? AND q.appointment_id = ?
            LIMIT 1
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setLong(2, appointmentId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                DoctorQueueItem item = new DoctorQueueItem();
                item.setQueuePosition(rs.getInt("queue_position"));
                item.setAppointmentId(rs.getLong("appointment_id"));
                item.setPatientId(rs.getLong("patient_id"));
                item.setPatientName(rs.getString("patient_name"));
                item.setGender(rs.getString("gender"));
                item.setDob(rs.getDate("dob"));
                item.setSymptom(rs.getString("symptom"));
                item.setStatus(rs.getString("status"));
                return item;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Nnạp timeline các phiếu xét nghiệm và kết quả theo appointment hiện tại.
    // LEFT JOIN lab_requests với lab_results.
    public List<ExamLabItem> getLabResultsByAppointment(long appointmentId) {
        List<ExamLabItem> list = new ArrayList<>();
        String sql = """
            SELECT
                lr.request_id,
                lr.status,
                lr.created_at,
                res.result_file,
                res.notes,
                res.completed_at
            FROM lab_requests lr
            LEFT JOIN lab_results res ON lr.request_id = res.request_id
            WHERE lr.appointment_id = ?
            ORDER BY lr.created_at DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, appointmentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ExamLabItem item = new ExamLabItem();
                item.setRequestId(rs.getInt("request_id"));
                item.setStatus(rs.getString("status"));
                item.setRequestedAt(rs.getTimestamp("created_at"));
                item.setResultFile(rs.getString("result_file"));
                item.setNotes(rs.getString("notes"));
                item.setCompletedAt(rs.getTimestamp("completed_at"));
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<ExaminationHistoryItem> getExaminationHistoryByAppointment(long appointmentId) {
        List<ExaminationHistoryItem> list = new ArrayList<>();

        String sql = """
            SELECT
                a.appointment_id,
                a.appointment_date,
                a.appointment_time,
                COALESCE(mr.symptoms, a.symptom) AS symptom,
                a.status AS appointment_status,
                COALESCE(eq.status, 'N/A') AS queue_status,
                    mr.diagnosis,
                    mr.notes,
                    mr.updated_at AS record_updated_at
            FROM appointments current_ap
            JOIN appointments a ON a.patient_id = current_ap.patient_id
            LEFT JOIN exam_queue eq ON eq.appointment_id = a.appointment_id
            JOIN medical_records mr ON mr.appointment_id = a.appointment_id
            WHERE current_ap.appointment_id = ?
            AND a.appointment_id <> current_ap.appointment_id
            ORDER BY a.appointment_date DESC, a.appointment_time DESC, mr.updated_at DESC
            LIMIT 10
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, appointmentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ExaminationHistoryItem item = new ExaminationHistoryItem();
                item.setAppointmentId(rs.getLong("appointment_id"));
                item.setAppointmentDate(rs.getDate("appointment_date"));
                item.setAppointmentTime(rs.getTime("appointment_time"));
                item.setSymptom(rs.getString("symptom"));
                item.setAppointmentStatus(rs.getString("appointment_status"));
                item.setQueueStatus(rs.getString("queue_status"));
                item.setDiagnosis(rs.getString("diagnosis"));
                item.setNotes(rs.getString("notes"));
                item.setRecordUpdatedAt(rs.getTimestamp("record_updated_at"));
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public MedicalRecord getMedicalRecordByAppointment(long appointmentId) {
        String sql = """
            SELECT appointment_id, symptoms, diagnosis, notes, updated_at
            FROM medical_records
            WHERE appointment_id = ?
            LIMIT 1
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, appointmentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                MedicalRecord record = new MedicalRecord();
                record.setAppointmentId(rs.getLong("appointment_id"));
                record.setSymptoms(rs.getString("symptoms"));
                record.setDiagnosis(rs.getString("diagnosis"));
                record.setNotes(rs.getString("notes"));
                record.setUpdatedAt(rs.getTimestamp("updated_at"));
                return record;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean upsertMedicalRecord(long appointmentId, String symptoms, String diagnosis, String notes) {
        String checkSql = "SELECT 1 FROM medical_records WHERE appointment_id = ? LIMIT 1";

        try (PreparedStatement check = connection.prepareStatement(checkSql)) {
            check.setLong(1, appointmentId);
            boolean exists;
            try (ResultSet rs = check.executeQuery()) {
                exists = rs.next();
            }

            if (exists) {
                String updateSql = """
                    UPDATE medical_records
                    SET symptoms = ?, diagnosis = ?, notes = ?, updated_at = NOW()
                    WHERE appointment_id = ?
                """;

                try (PreparedStatement update = connection.prepareStatement(updateSql)) {
                    update.setString(1, symptoms);
                    update.setString(2, diagnosis);
                    update.setString(3, notes);
                    update.setLong(4, appointmentId);
                    return update.executeUpdate() > 0;
                }
            }

            String insertSql = """
                INSERT INTO medical_records (appointment_id, doctor_id, symptoms, diagnosis, notes, updated_at)
                VALUES (?, (SELECT doctor_id FROM appointments WHERE appointment_id = ?), ?, ?, ?, NOW())
            """;

            try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
                insert.setLong(1, appointmentId);
                insert.setLong(2, appointmentId);
                insert.setString(3, symptoms);
                insert.setString(4, diagnosis);
                insert.setString(5, notes);
                return insert.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean saveMedicalRecordAndFinishExamination(long appointmentId, String symptoms, String diagnosis, String notes) {
        String checkSql = "SELECT 1 FROM medical_records WHERE appointment_id = ? LIMIT 1";
        String updateRecordSql = """
            UPDATE medical_records
            SET symptoms = ?, diagnosis = ?, notes = ?, updated_at = NOW()
            WHERE appointment_id = ?
        """;
        String insertRecordSql = """
            INSERT INTO medical_records (appointment_id, doctor_id, symptoms, diagnosis, notes, updated_at)
            VALUES (?, (SELECT doctor_id FROM appointments WHERE appointment_id = ?), ?, ?, ?, NOW())
        """;
        String finishSql = """
            UPDATE exam_queue
            SET status = 'done'
            WHERE appointment_id = ?
        """;

        try {
            connection.setAutoCommit(false);

            boolean exists;
            try (PreparedStatement check = connection.prepareStatement(checkSql)) {
                check.setLong(1, appointmentId);
                try (ResultSet rs = check.executeQuery()) {
                    exists = rs.next();
                }
            }

            if (exists) {
                try (PreparedStatement update = connection.prepareStatement(updateRecordSql)) {
                    update.setString(1, symptoms);
                    update.setString(2, diagnosis);
                    update.setString(3, notes);
                    update.setLong(4, appointmentId);
                    if (update.executeUpdate() == 0) {
                        connection.rollback();
                        return false;
                    }
                }
            } else {
                try (PreparedStatement insert = connection.prepareStatement(insertRecordSql)) {
                    insert.setLong(1, appointmentId);
                    insert.setLong(2, appointmentId);
                    insert.setString(3, symptoms);
                    insert.setString(4, diagnosis);
                    insert.setString(5, notes);
                    if (insert.executeUpdate() == 0) {
                        connection.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement finish = connection.prepareStatement(finishSql)) {
                finish.setLong(1, appointmentId);
                if (finish.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int saveMedicalRecordAndCreateLabRequest(long appointmentId, int doctorId, String symptoms, String diagnosis, String notes) {
        String checkSql = "SELECT 1 FROM medical_records WHERE appointment_id = ? LIMIT 1";
        String updateRecordSql = """
            UPDATE medical_records
            SET symptoms = ?, diagnosis = ?, notes = ?, updated_at = NOW()
            WHERE appointment_id = ?
        """;
        String insertRecordSql = """
            INSERT INTO medical_records (appointment_id, doctor_id, symptoms, diagnosis, notes, updated_at)
            VALUES (?, (SELECT doctor_id FROM appointments WHERE appointment_id = ?), ?, ?, ?, NOW())
        """;
        String insertLabSql = "INSERT INTO lab_requests (appointment_id, doctor_id, status, created_at) VALUES (?, ?, 'pending', NOW())";
        String deleteQueueSql = "DELETE FROM exam_queue WHERE appointment_id = ?";

        try {
            connection.setAutoCommit(false);

            boolean exists;
            try (PreparedStatement check = connection.prepareStatement(checkSql)) {
                check.setLong(1, appointmentId);
                try (ResultSet rs = check.executeQuery()) {
                    exists = rs.next();
                }
            }

            if (exists) {
                try (PreparedStatement update = connection.prepareStatement(updateRecordSql)) {
                    update.setString(1, symptoms);
                    update.setString(2, diagnosis);
                    update.setString(3, notes);
                    update.setLong(4, appointmentId);
                    if (update.executeUpdate() == 0) {
                        connection.rollback();
                        return 0;
                    }
                }
            } else {
                try (PreparedStatement insert = connection.prepareStatement(insertRecordSql)) {
                    insert.setLong(1, appointmentId);
                    insert.setLong(2, appointmentId);
                    insert.setString(3, symptoms);
                    insert.setString(4, diagnosis);
                    insert.setString(5, notes);
                    if (insert.executeUpdate() == 0) {
                        connection.rollback();
                        return 0;
                    }
                }
            }

            int requestId;
            try (PreparedStatement insertLab = connection.prepareStatement(insertLabSql, Statement.RETURN_GENERATED_KEYS)) {
                insertLab.setLong(1, appointmentId);
                insertLab.setInt(2, doctorId);
                if (insertLab.executeUpdate() == 0) {
                    connection.rollback();
                    return 0;
                }
                try (ResultSet keys = insertLab.getGeneratedKeys()) {
                    if (!keys.next()) {
                        connection.rollback();
                        return 0;
                    }
                    requestId = keys.getInt(1);
                }
            }

            try (PreparedStatement del = connection.prepareStatement(deleteQueueSql)) {
                del.setLong(1, appointmentId);
                del.executeUpdate();
            }

            connection.commit();
            return requestId;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return 0;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateDoctor(int doctorId, String qualification, int experience, String specialization) {
        String sql = "UPDATE doctors SET qualification=?, experience_years=?, specialization=? "
                + "WHERE doctor_id=?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {

            st.setString(1, qualification);
            st.setInt(2, experience);
            st.setString(3, specialization);
            st.setInt(4, doctorId);

            st.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getDoctorByUserId2(int userId) {
        String sql = "SELECT * FROM doctors WHERE user_id=?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {

            st.setInt(1, userId);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                Doctor d = new Doctor();
                d.setDoctorId(rs.getInt("doctor_id"));
                d.setQualification(rs.getString("qualification"));
                d.setExperience_years(rs.getInt("experience_years"));
                d.setSpecialization(rs.getString("specialization"));
                return d;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean savePrescription(long appointmentId, int doctorId, String prescriptionNote, List<PrescriptionItem> prescriptionItems) {
        if (prescriptionItems == null || prescriptionItems.isEmpty()) {
            return false;
        }

        String deleteItemsSql = "DELETE FROM prescription_items WHERE prescription_id = ?";
        String findRecordSql = "SELECT record_id FROM medical_records WHERE appointment_id = ? LIMIT 1";
        String findPrescriptionSql = "SELECT prescription_id FROM prescriptions WHERE record_id = ? LIMIT 1";
        String insertPrescriptionSql = "INSERT INTO prescriptions (record_id, doctor_id, created_at) VALUES (?, ?, NOW())";
        String updatePrescriptionSql = "UPDATE prescriptions SET doctor_id = ?, created_at = NOW() WHERE prescription_id = ?";
        String insertItemSql = """
            INSERT INTO prescription_items
                (prescription_id, medicine_name, dosage, frequency, duration)
            VALUES (?, ?, ?, ?, ?)
        """;

        boolean originalAutoCommit;
        try {
            originalAutoCommit = connection.getAutoCommit();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }

        try {
            connection.setAutoCommit(false);
            int recordId;
            try (PreparedStatement findRecord = connection.prepareStatement(findRecordSql)) {
                findRecord.setLong(1, appointmentId);
                try (ResultSet rs = findRecord.executeQuery()) {
                    if (!rs.next()) {
                        connection.rollback();
                        return false;
                    }
                    recordId = rs.getInt("record_id");
                }
            }

            int prescriptionId;
            try (PreparedStatement find = connection.prepareStatement(findPrescriptionSql)) {
                find.setInt(1, recordId);
                try (ResultSet rs = find.executeQuery()) {
                    if (rs.next()) {
                        prescriptionId = rs.getInt("prescription_id");
                    } else {
                        try (PreparedStatement insertPrescription = connection.prepareStatement(insertPrescriptionSql, Statement.RETURN_GENERATED_KEYS)) {
                            insertPrescription.setInt(1, recordId);
                            insertPrescription.setInt(2, doctorId);
                            if (insertPrescription.executeUpdate() == 0) {
                                connection.rollback();
                                return false;
                            }
                            try (ResultSet keys = insertPrescription.getGeneratedKeys()) {
                                if (!keys.next()) {
                                    connection.rollback();
                                    return false;
                                }
                                prescriptionId = keys.getInt(1);
                            }
                        }
                    }
                }
            }

            try (PreparedStatement updatePrescription = connection.prepareStatement(updatePrescriptionSql)) {
                updatePrescription.setInt(1, doctorId);
                updatePrescription.setInt(2, prescriptionId);
                updatePrescription.executeUpdate();
            }

            try (PreparedStatement deleteItems = connection.prepareStatement(deleteItemsSql)) {
                deleteItems.setInt(1, prescriptionId);
                deleteItems.executeUpdate();
            }

            try (PreparedStatement insertItem = connection.prepareStatement(insertItemSql)) {
                for (PrescriptionItem item : prescriptionItems) {
                    String medicineName = item.getMedicineName();
                    if (medicineName == null || medicineName.isBlank()) {
                        medicineName = item.getMedicineId() > 0
                                ? "Medicine #" + item.getMedicineId()
                                : "Chưa cập nhật";
                    }
                    insertItem.setInt(1, prescriptionId);
                    insertItem.setString(2, medicineName);
                    insertItem.setString(3, item.getDosage());
                    insertItem.setString(4, item.getFrequency());
                    insertItem.setString(5, item.getDurationDays());
                    insertItem.addBatch();
                }
                insertItem.executeBatch();
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(originalAutoCommit);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

    }

    public List<PrescriptionItem> getPrescriptionItemsByAppointment(long appointmentId) {
        List<PrescriptionItem> list = new ArrayList<>();
        String sql = """
            SELECT
                pi.item_id,
                pi.prescription_id,
                pi.medicine_name,
                pi.dosage,
                pi.frequency,
                pi.`duration` AS duration_value
            FROM prescriptions p
            JOIN medical_records mr ON mr.record_id = p.record_id
            JOIN prescription_items pi ON pi.prescription_id = p.prescription_id
            WHERE mr.appointment_id = ?
            ORDER BY pi.item_id
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, appointmentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PrescriptionItem item = new PrescriptionItem();
                item.setItemId(rs.getInt("item_id"));
                item.setPrescriptionId(rs.getInt("prescription_id"));
                item.setMedicineName(rs.getString("medicine_name"));
                item.setDosage(rs.getString("dosage"));
                item.setFrequency(rs.getString("frequency"));
                item.setDurationDays(rs.getString("duration_value"));
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Medicine> getAllMedicines() {
        List<Medicine> list = new ArrayList<>();
        String sql = """
            SELECT medicine_id, medicine_name, unit, default_dosage
            FROM medicines
            ORDER BY medicine_name
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Medicine medicine = new Medicine();
                medicine.setMedicineId(rs.getInt("medicine_id"));
                medicine.setMedicineName(rs.getString("medicine_name"));
                medicine.setUnit(rs.getString("unit"));
                medicine.setDefaultDosage(rs.getString("default_dosage"));
                list.add(medicine);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    public Doctor getDoctorById(int doctorID) {
    
    String sql = """
        SELECT d.*, 
               u.full_name,
               u.phone,
               u.email
        FROM doctors d
        JOIN users u ON d.user_id = u.id
        WHERE d.doctor_id = ?
        """;

    try (PreparedStatement st = connection.prepareStatement(sql)) {

        st.setInt(1, doctorID);

        ResultSet rs = st.executeQuery();

        if (rs.next()) {

            Doctor d = new Doctor();

            d.setDoctorId(rs.getInt("doctor_id"));
            d.setUserId(rs.getInt("user_id"));
            d.setSpecialization(rs.getString("specialization"));
            d.setImage(rs.getString("image"));
            d.setQualification(rs.getString("qualification"));
            d.setClinic_address(rs.getString("clinic_address"));
            d.setExperience_years(rs.getInt("experience_years"));
            d.setRating(rs.getDouble("rating"));
            d.setPrice(rs.getDouble("price"));

            d.setFullName(rs.getString("full_name"));
            d.setPhone(rs.getString("phone"));
            d.setEmail(rs.getString("email"));

            return d;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;}
}
