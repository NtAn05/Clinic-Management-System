/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Appointment;
import model.AppointmentDetail;
import model.Doctor;
import model.Patient;

/**
 *
 * @author Admin
 */
public class AppointmentDAO extends DBContext {

    
    public boolean addAppointment(Appointment a) {

        String sql = "INSERT INTO appointments "
                + "(patient_id, doctor_id, shift_id, booking_type, "
                + "appointment_date, appointment_time, status, symptom) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement st = connection.prepareStatement(sql)) {

            st.setLong(1, a.getPatientId());
            st.setInt(2, a.getDoctorId());
            st.setInt(3, a.getShiftId());
            st.setString(4, a.getBookingType());
            st.setDate(5, (Date) a.getAppointmentDate());
            st.setTime(6, a.getAppointmentTime());
            st.setString(7, a.getStatus());
            st.setString(8, a.getSymptom());

            return st.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public long addPatient(Patient p) {
        String sql = "INSERT INTO patients (user_id, full_name, phone, dob, email, gender) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement st = connection.prepareStatement(sql)) {

            st.setInt(1, p.getUserId());
            st.setString(2, p.getFullName());
            st.setString(3, p.getPhone());
            st.setDate(4, new java.sql.Date(p.getDob().getTime()));
            st.setString(5, p.getEmail());
            st.setString(6, p.getGender());

            st.executeUpdate();

            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public long getPatientID(Patient patient) {
        String sql = "SELECT patient_id FROM patients "
                + "WHERE full_name = ? AND phone = ? AND email = ?";

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, patient.getFullName());
            st.setString(2, patient.getPhone());
            st.setString(3, patient.getEmail());

            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                return rs.getLong("patient_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public List<AppointmentDetail> getAppointmentsByPatientUserId(int userId) {

        List<AppointmentDetail> list = new ArrayList<>();

        String sql = "SELECT "
                + "a.appointment_id, "
                + "a.patient_id, "
                + "a.doctor_id, "
                + "a.shift_id, "
                + "a.booking_type, "
                + "a.appointment_date, "
                + "a.appointment_time, "
                + "a.status, "
                + "a.symptom, "
                + "d.specialization, "
                + "sp.academic_degree AS qualification, "
                + "d.price_booking, "
                + "du.user_id AS doctor_user_id, "
                + "du.full_name AS doctor_name, "
                + "du.image_url, "
                + "p.full_name, "
                + "p.phone, "
                + "p.email "
                + "FROM appointments a "
                + "JOIN patients p ON a.patient_id = p.patient_id "
                + "JOIN doctors d ON a.doctor_id = d.doctor_id "
                + "LEFT JOIN staff_profiles sp ON sp.user_id = d.user_id "
                + "JOIN users du ON d.user_id = du.user_id "
                + "WHERE p.user_id = ? "
                + "ORDER BY a.appointment_date DESC, a.appointment_time DESC";

        try (PreparedStatement st = connection.prepareStatement(sql)) {

            st.setInt(1, userId);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {

                AppointmentDetail ad = new AppointmentDetail();

                // appointment
                ad.setAppointmentId(rs.getLong("appointment_id"));
                ad.setPatientId(rs.getLong("patient_id"));
                ad.setDoctorId(rs.getInt("doctor_id"));
                ad.setShiftId(rs.getInt("shift_id"));
                ad.setBookingType(rs.getString("booking_type"));
                ad.setAppointmentDate(rs.getDate("appointment_date"));
                ad.setAppointmentTime(rs.getTime("appointment_time"));
                ad.setStatus(rs.getString("status"));
                ad.setSymptom(rs.getString("symptom"));

                // doctor
                ad.setSpecialization(rs.getString("specialization"));
                ad.setQualification(rs.getString("qualification"));
                ad.setPrice(rs.getDouble("price_booking"));
                ad.setUserId(rs.getInt("doctor_user_id"));
                ad.setImage(rs.getString("image_url"));

                // patient
                ad.setFullName(rs.getString("full_name"));
                ad.setPhone(rs.getString("phone"));
                ad.setEmail(rs.getString("email"));
                ad.setDoctorName(rs.getString("doctor_name"));
                list.add(ad);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // lấy toàn bộ appointment
    public List<AppointmentDetail> getAllAppointments() {

        List<AppointmentDetail> list = new ArrayList<>();

        String sql = "SELECT "
                + "a.appointment_id, "
                + "a.patient_id, "
                + "a.doctor_id, "
                + "a.shift_id, "
                + "a.booking_type, "
                + "a.appointment_date, "
                + "a.appointment_time, "
                + "a.status, "
                + "a.symptom, "
                + "d.specialization, "
                + "sp.academic_degree AS qualification, "
                + "d.price_booking, "
                + "du.user_id AS doctor_user_id, "
                + "du.full_name AS doctor_name, "
                + "du.image_url, "
                + "p.full_name, "
                + "p.phone, "
                + "p.email "
                + "FROM appointments a "
                + "JOIN patients p ON a.patient_id = p.patient_id "
                + "JOIN doctors d ON a.doctor_id = d.doctor_id "
                + "LEFT JOIN staff_profiles sp ON sp.user_id = d.user_id "
                + "JOIN users du ON d.user_id = du.user_id "
                + "ORDER BY a.appointment_date DESC, a.appointment_time DESC";

        try (PreparedStatement st = connection.prepareStatement(sql)) {

            ResultSet rs = st.executeQuery();

            while (rs.next()) {

                AppointmentDetail ad = new AppointmentDetail();

                ad.setAppointmentId(rs.getLong("appointment_id"));
                ad.setPatientId(rs.getLong("patient_id"));
                ad.setDoctorId(rs.getInt("doctor_id"));
                ad.setShiftId(rs.getInt("shift_id"));
                ad.setBookingType(rs.getString("booking_type"));
                ad.setAppointmentDate(rs.getDate("appointment_date"));
                ad.setAppointmentTime(rs.getTime("appointment_time"));
                ad.setStatus(rs.getString("status"));
                ad.setSymptom(rs.getString("symptom"));

                // doctor
                ad.setSpecialization(rs.getString("specialization"));
                ad.setQualification(rs.getString("qualification"));
                ad.setPrice(rs.getDouble("price_booking"));
                ad.setUserId(rs.getInt("doctor_user_id"));
                ad.setImage(rs.getString("image_url"));
                ad.setDoctorName(rs.getString("doctor_name"));

                // patient
                ad.setFullName(rs.getString("full_name"));
                ad.setPhone(rs.getString("phone"));
                ad.setEmail(rs.getString("email"));

                list.add(ad);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void updateStatus(long appointmentId, String status) {

        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {

            st.setString(1, status);
            st.setLong(2, appointmentId);

            st.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelPastBookedAppointments() {

        String sql = """
        UPDATE appointments
        SET status = 'cancelled'
        WHERE appointment_date < CURDATE()
        AND status = 'booked'
    """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {

            st.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getPatientIdByEmail(String email) {

        String sql = "SELECT patient_id FROM patients WHERE email = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {

            st.setString(1, email);

            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                return rs.getLong("patient_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public List<LocalDate> getAvailableDates(int doctorId) {

        List<LocalDate> list = new ArrayList<>();



        String sql = """
            SELECT ds.day_of_week, ds.max_patients
            FROM doctor_shifts ds
            JOIN doctors d ON d.doctor_id = ds.doctor_id
            JOIN users u ON u.user_id = d.user_id
            WHERE ds.doctor_id = ?
              AND ds.status = 'active'
              AND u.status = 'active'
        """;


        try (PreparedStatement st = connection.prepareStatement(sql)) {

            st.setInt(1, doctorId);
            ResultSet rs = st.executeQuery();

            List<Integer> days = new ArrayList<>();
            int maxPatients = 20;

            while (rs.next()) {
                days.add(rs.getInt("day_of_week"));
                maxPatients = rs.getInt("max_patients"); // tạm giữ
            }

            LocalDate today = LocalDate.now();
            int i = 0;

            while (list.size() < 7 && i < 30) { // ✅ lấy đủ 7 ngày
                LocalDate date = today.plusDays(i);
                int dayOfWeek = date.getDayOfWeek().getValue();

                if (days.contains(dayOfWeek)) {

                    int booked = countPatients(doctorId, Date.valueOf(date));

                    if (booked < maxPatients) {
                        list.add(date);
                    }
                }

                i++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public int countPatients(int doctorId, Date date) {

        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {

            st.setInt(1, doctorId);
            st.setDate(2, date);

            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getDoctorIdByAppointment(long appointmentId) {

        String sql = "SELECT doctor_id FROM appointments WHERE appointment_id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, appointmentId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt("doctor_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
    
    public void addQueueWithPriority(long appointmentId, int doctorId) {
        String getAppointmentSql = """
            SELECT booking_type, appointment_date
            FROM appointments
            WHERE appointment_id = ?
            FOR UPDATE
        """;

        String findInsertPosOnlineSql = """
            SELECT COALESCE(MAX(q.queue_position), 0) + 1 AS insert_pos
            FROM exam_queue q
            JOIN appointments a ON a.appointment_id = q.appointment_id
            WHERE q.doctor_id = ?
              AND a.appointment_date = ?
              AND q.status IN ('waiting', 'examining')
              AND a.booking_type = 'online'
        """;

        String findInsertPosWalkInSql = """
            SELECT COALESCE(MAX(q.queue_position), 0) + 1 AS insert_pos
            FROM exam_queue q
            JOIN appointments a ON a.appointment_id = q.appointment_id
            WHERE q.doctor_id = ?
              AND a.appointment_date = ?
              AND q.status IN ('waiting', 'examining')
        """;

        String shiftQueueSql = """
            UPDATE exam_queue q
            JOIN appointments a ON a.appointment_id = q.appointment_id
            SET q.queue_position = q.queue_position + 1
            WHERE q.doctor_id = ?
              AND a.appointment_date = ?
              AND q.status IN ('waiting', 'examining')
              AND q.queue_position >= ?
        """;

        String insertQueueSql = """
            INSERT INTO exam_queue (appointment_id, doctor_id, queue_position, status)
            VALUES (?, ?, ?, 'waiting')
        """;

        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            String bookingType;
            Date appointmentDate;
            try (PreparedStatement getAppt = connection.prepareStatement(getAppointmentSql)) {
                getAppt.setLong(1, appointmentId);
                try (ResultSet rs = getAppt.executeQuery()) {
                    if (!rs.next()) {
                        connection.rollback();
                        return;
                    }
                    bookingType = rs.getString("booking_type");
                    appointmentDate = rs.getDate("appointment_date");
                }
            }

            int insertPos = 1;
            String findInsertPosSql = "online".equalsIgnoreCase(bookingType)
                    ? findInsertPosOnlineSql
                    : findInsertPosWalkInSql;

            try (PreparedStatement findPos = connection.prepareStatement(findInsertPosSql)) {
                findPos.setInt(1, doctorId);
                findPos.setDate(2, appointmentDate);
                try (ResultSet posRs = findPos.executeQuery()) {
                    if (posRs.next()) {
                        insertPos = posRs.getInt("insert_pos");
                    }
                }
            }

            try (PreparedStatement shift = connection.prepareStatement(shiftQueueSql)) {
                shift.setInt(1, doctorId);
                shift.setDate(2, appointmentDate);
                shift.setInt(3, insertPos);
                shift.executeUpdate();
            }

            try (PreparedStatement insert = connection.prepareStatement(insertQueueSql)) {
                insert.setLong(1, appointmentId);
                insert.setInt(2, doctorId);
                insert.setInt(3, insertPos);
                insert.executeUpdate();
            }

            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(originalAutoCommit);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
