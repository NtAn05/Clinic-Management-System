package dal;

import model.Doctor;
import model.DoctorShift;
import model.QueuePatient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO extends DBContext {

    /* =========================
       LẤY DOCTOR THEO USER_ID
       ========================= */
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

    /* =========================
       LỊCH LÀM VIỆC BÁC SĨ
       ========================= */
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

    /* =========================
       DANH SÁCH CHỜ KHÁM
       (Doctor Dashboard)
       ========================= */
    public List<QueuePatient> getWaitingQueue(int doctorId) {
        List<QueuePatient> list = new ArrayList<>();

        String sql = """
            SELECT q.appointment_id,
                   p.full_name AS patient_name,
                   q.status,
                   q.queue_position,
                   q.created_at
            FROM exam_queue q
            JOIN appointments a ON q.appointment_id = a.appointment_id
            JOIN patients p ON a.patient_id = p.patient_id
            WHERE q.doctor_id = ?
              AND q.status IN ('waiting', 'examining')
            ORDER BY q.queue_position
        """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, doctorId);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                QueuePatient qp = new QueuePatient();
                qp.setAppointmentId(rs.getLong("appointment_id"));
                qp.setPatientName(rs.getString("patient_name"));
                qp.setStatus(rs.getString("status"));
                qp.setQueuePosition(rs.getInt("queue_position"));
                qp.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                list.add(qp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /* =========================
       CẬP NHẬT TRẠNG THÁI HÀNG ĐỢI
       ========================= */
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

    /* =========================
       KẾT THÚC KHÁM
       ========================= */
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

    public List<QueuePatient> getTodayQueueByDoctor(int doctorId) {
        List<QueuePatient> list = new ArrayList<>();

        String sql = """
        SELECT 
            eq.queue_id,
            eq.queue_position,
            eq.status,
            eq.created_at,
            a.appointment_id,
            p.full_name AS patient_name,
            p.phone
        FROM exam_queue eq
        JOIN appointments a ON eq.appointment_id = a.appointment_id
        JOIN patients p ON a.patient_id = p.patient_id
        WHERE eq.doctor_id = ?
        ORDER BY eq.queue_position
    """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, doctorId);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                QueuePatient qp = new QueuePatient();
                qp.setQueueId(rs.getInt("queue_id"));
                qp.setAppointmentId(rs.getLong("appointment_id"));
                qp.setPatientName(rs.getString("patient_name"));
                qp.setQueuePosition(rs.getInt("queue_position"));
                qp.setStatus(rs.getString("status"));
                qp.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                list.add(qp);
            }
        } catch (SQLException e) {
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

}
