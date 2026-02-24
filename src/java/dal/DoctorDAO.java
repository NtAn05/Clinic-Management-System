package dal;

import model.Doctor;
import model.DoctorShift;
import model.DoctorQueueItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.DoctorDashboardStats;
import model.ExamLabItem;
import model.ExaminationHistoryItem;

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

    /* =========================
       BẮT ĐẦU KHÁM
       ========================= */
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
    public List<DoctorQueueItem> getTodayQueueByDoctor(int doctorId) { // được thay thế bởi getqueuewithfilter
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
    List<DoctorQueueItem> list = new ArrayList<>();

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

    if (status != null && !status.equals("all")) {
        sql.append(" AND q.status = ? ");
    }

    if (keyword != null && !keyword.isBlank()) {
        sql.append(" AND p.full_name LIKE ? ");
    }

    sql.append(" ORDER BY q.queue_position ");

    try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
        int index = 1;
        ps.setInt(index++, doctorId);

        if (status != null && !status.equals("all")) {
            ps.setString(index++, status);
        }

        if (keyword != null && !keyword.isBlank()) {
            ps.setString(index++, "%" + keyword + "%");
        }

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
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}

    
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
                a.symptom,
                a.status AS appointment_status,
                COALESCE(eq.status, 'N/A') AS queue_status
            FROM appointments current_ap
            JOIN appointments a ON a.patient_id = current_ap.patient_id
            LEFT JOIN exam_queue eq ON eq.appointment_id = a.appointment_id
            WHERE current_ap.appointment_id = ?
              AND a.appointment_id <> current_ap.appointment_id
            ORDER BY a.appointment_date DESC, a.appointment_time DESC
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
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    
}
