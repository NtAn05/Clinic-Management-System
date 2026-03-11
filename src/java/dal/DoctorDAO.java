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

    //thống kê số liệu 
    public DoctorDashboardStats getDashboardStats(int doctorId) {
        DoctorDashboardStats stats = new DoctorDashboardStats();
        String sql = """
        SELECT 
            COUNT(*) AS total,
            SUM(CASE WHEN status = 'waiting' THEN 1 ELSE 0 END) AS waiting,
            SUM(CASE WHEN status = 'examining' THEN 1 ELSE 0 END) AS examining,
            SUM(CASE WHEN status = 'done' THEN 1 ELSE 0 END) AS done
        FROM exam_queue
        WHERE doctor_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                stats.setTotal(rs.getInt("total"));
                stats.setWaiting(rs.getInt("waiting"));
                stats.setExamining(rs.getInt("examining"));
                stats.setDone(rs.getInt("done"));
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
                INSERT INTO medical_records (appointment_id, symptoms, diagnosis, notes, updated_at)
                VALUES (?, ?, ?, ?, NOW())
            """;

            try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
                insert.setLong(1, appointmentId);
                insert.setString(2, symptoms);
                insert.setString(3, diagnosis);
                insert.setString(4, notes);
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
            INSERT INTO medical_records (appointment_id, symptoms, diagnosis, notes, updated_at)
            VALUES (?, ?, ?, ?, NOW())
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
                    insert.setString(2, symptoms);
                    insert.setString(3, diagnosis);
                    insert.setString(4, notes);
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
            INSERT INTO medical_records (appointment_id, symptoms, diagnosis, notes, updated_at)
            VALUES (?, ?, ?, ?, NOW())
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
                    insert.setString(2, symptoms);
                    insert.setString(3, diagnosis);
                    insert.setString(4, notes);
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
        String upsertPrescriptionSql = """
            INSERT INTO prescriptions (appointment_id, doctor_id, notes, created_at)
            VALUES (?, ?, ?, NOW())
            ON DUPLICATE KEY UPDATE
                doctor_id = VALUES(doctor_id),
                notes = VALUES(notes),
                created_at = NOW()
        """;
        String findPrescriptionSql = "SELECT prescription_id FROM prescriptions WHERE appointment_id = ? LIMIT 1";
        String insertItemSql = """
            INSERT INTO prescription_items
                (prescription_id, medicine_id, dosage, frequency, duration_days, instruction, quantity)
            VALUES (?, ?, ?, ?, ?, ?, ?)
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

            try (PreparedStatement upsert = connection.prepareStatement(upsertPrescriptionSql)) {
                upsert.setLong(1, appointmentId);
                upsert.setInt(2, doctorId);
                upsert.setString(3, prescriptionNote);
                upsert.executeUpdate();
            }

            int prescriptionId;
            try (PreparedStatement find = connection.prepareStatement(findPrescriptionSql)) {
                find.setLong(1, appointmentId);
                try (ResultSet rs = find.executeQuery()) {
                    if (!rs.next()) {
                        connection.rollback();
                        return false;
                    }
                    prescriptionId = rs.getInt("prescription_id");
                }
            }

            try (PreparedStatement deleteItems = connection.prepareStatement(deleteItemsSql)) {
                deleteItems.setInt(1, prescriptionId);
                deleteItems.executeUpdate();
            }

            try (PreparedStatement insertItem = connection.prepareStatement(insertItemSql)) {
                for (PrescriptionItem item : prescriptionItems) {
                    insertItem.setInt(1, prescriptionId);
                    insertItem.setInt(2, item.getMedicineId());
                    insertItem.setString(3, item.getDosage());
                    insertItem.setString(4, item.getFrequency());
                    insertItem.setString(5, item.getDurationDays());
                    insertItem.setString(6, item.getInstruction());
                    insertItem.setString(7, item.getQuantity());
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
                pi.medicine_id,
                m.medicine_name,
                m.unit,
                pi.dosage,
                pi.frequency,
                pi.duration_days,
                pi.instruction,
                pi.quantity
            FROM prescriptions p
            JOIN prescription_items pi ON pi.prescription_id = p.prescription_id
            JOIN medicines m ON m.medicine_id = pi.medicine_id
            WHERE p.appointment_id = ?
            ORDER BY pi.item_id
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, appointmentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PrescriptionItem item = new PrescriptionItem();
                item.setItemId(rs.getInt("item_id"));
                item.setPrescriptionId(rs.getInt("prescription_id"));
                item.setMedicineId(rs.getInt("medicine_id"));
                item.setMedicineName(rs.getString("medicine_name"));
                item.setUnit(rs.getString("unit"));
                item.setDosage(rs.getString("dosage"));
                item.setFrequency(rs.getString("frequency"));
                item.setDurationDays(rs.getString("duration_days"));
                item.setInstruction(rs.getString("instruction"));
                item.setQuantity(rs.getString("quantity"));
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
}
