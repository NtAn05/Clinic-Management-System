/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.Date;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        return addAppointmentAndReturnId(a) > 0;
    }

    public long addAppointmentAndReturnId(Appointment a) {
        String sql = "INSERT INTO appointments "
                + "(patient_id, doctor_id, shift_id, booking_type, "
                + "appointment_date, appointment_time, status, symptom) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement st = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setLong(1, a.getPatientId());
            st.setInt(2, a.getDoctorId());
            st.setInt(3, a.getShiftId());
            st.setString(4, a.getBookingType());
            st.setDate(5, (Date) a.getAppointmentDate());
            st.setTime(6, a.getAppointmentTime());
            st.setString(7, a.getStatus());
            st.setString(8, a.getSymptom());

            int affected = st.executeUpdate();
            if (affected <= 0) {
                return -1;
            }

            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            return -1;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
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

            Map<Integer, Integer> shiftCountByDay = new HashMap<>();
            Map<Integer, Integer> capacityByDay = new HashMap<>();

            while (rs.next()) {
                int day = rs.getInt("day_of_week");
                int maxPatients = rs.getInt("max_patients");
                shiftCountByDay.merge(day, 1, Integer::sum);
                capacityByDay.merge(day, maxPatients, Integer::sum);
            }

            LocalDate today = LocalDate.now();
            LocalDate endDate = today.plusDays(29);
            List<TemporarySwapEffect> effects = getApprovedTemporarySwapEffects(
                    doctorId,
                    Date.valueOf(today),
                    Date.valueOf(endDate)
            );

            int i = 0;
            while (list.size() < 7 && i < 30) {
                LocalDate date = today.plusDays(i);
                int dayOfWeek = date.getDayOfWeek().getValue() % 7; // CN = 0

                int shiftCount = shiftCountByDay.getOrDefault(dayOfWeek, 0);
                for (TemporarySwapEffect effect : effects) {
                    if (date.equals(effect.workDate)) {
                        shiftCount += effect.delta;
                    }
                }

                if (shiftCount > 0) {
                    int booked = countPatients(doctorId, Date.valueOf(date));
                    int maxPatients = Math.max(1, capacityByDay.getOrDefault(dayOfWeek, 20));
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

    private List<TemporarySwapEffect> getApprovedTemporarySwapEffects(int doctorId, Date fromDate, Date toDate) {
        List<TemporarySwapEffect> effects = new ArrayList<>();
        String sql = """
            SELECT r.doctor_id AS requester_doctor_id,
                   i.work_date AS new_work_date,
                   CASE
                       WHEN i.work_date IS NOT NULL AND s_old.day_of_week IS NOT NULL AND i.day_of_week IS NOT NULL
                       THEN DATE_ADD(i.work_date, INTERVAL ((s_old.day_of_week - i.day_of_week + 7) % 7) DAY)
                       ELSE NULL
                   END AS old_work_date,
                   s_new.doctor_id AS counterpart_doctor_id
            FROM schedule_change_requests r
            JOIN schedule_change_request_items i ON r.request_id = i.request_id
            LEFT JOIN doctor_shifts s_old ON i.target_shift_id = s_old.shift_id
            LEFT JOIN doctor_shifts s_new ON s_new.shift_id = COALESCE(
                i.swap_shift_id,
                (
                    SELECT s2.shift_id
                    FROM doctor_shifts s2
                    WHERE s2.day_of_week = i.day_of_week
                      AND s2.start_time = i.start_time
                      AND s2.end_time = i.end_time
                      AND s2.doctor_id <> r.doctor_id
                    ORDER BY s2.shift_id
                    LIMIT 1
                )
            )
            WHERE r.status = 'APPROVED'
              AND r.request_type = 'TEMPORARY'
              AND r.scope_type = 'ONE_DATE'
              AND i.action_type = 'UPDATE'
              AND i.work_date BETWEEN ? AND ?
            ORDER BY r.requested_at ASC
        """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setDate(1, fromDate);
            st.setDate(2, toDate);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    int requesterId = rs.getInt("requester_doctor_id");
                    Date newWorkDate = rs.getDate("new_work_date");
                    Date oldWorkDate = rs.getDate("old_work_date");
                    int counterpartId = rs.getInt("counterpart_doctor_id");
                    boolean hasCounterpart = !rs.wasNull();

                    if (newWorkDate == null || oldWorkDate == null) {
                        continue;
                    }

                    if (requesterId == doctorId) {
                        effects.add(new TemporarySwapEffect(oldWorkDate.toLocalDate(), -1));
                        effects.add(new TemporarySwapEffect(newWorkDate.toLocalDate(), +1));
                    }

                    if (hasCounterpart && counterpartId == doctorId) {
                        effects.add(new TemporarySwapEffect(newWorkDate.toLocalDate(), -1));
                        effects.add(new TemporarySwapEffect(oldWorkDate.toLocalDate(), +1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return effects;
    }

    private static class TemporarySwapEffect {
        private final LocalDate workDate;
        private final int delta;

        private TemporarySwapEffect(LocalDate workDate, int delta) {
            this.workDate = workDate;
            this.delta = delta;
        }
    }

    public boolean isDoctorWorkingInSlot(int doctorId, Date workDate, LocalTime appointmentTime) {
        if (doctorId <= 0 || workDate == null || appointmentTime == null) {
            return false;
        }

        String period = appointmentTime.isBefore(LocalTime.NOON) ? "MORNING" : "AFTERNOON";
        int dayOfWeek = workDate.toLocalDate().getDayOfWeek().getValue() % 7;

        int baseShiftCount = countBaseShiftsForPeriod(doctorId, dayOfWeek, period);
        int temporaryDelta = getTemporarySwapDeltaForPeriod(doctorId, workDate, period);
        return baseShiftCount + temporaryDelta > 0;
    }

    private int countBaseShiftsForPeriod(int doctorId, int dayOfWeek, String period) {
        String sql = """
            SELECT COUNT(*)
            FROM doctor_shifts ds
            JOIN doctors d ON d.doctor_id = ds.doctor_id
            JOIN users u ON u.user_id = d.user_id
            WHERE ds.doctor_id = ?
              AND ds.day_of_week = ?
              AND ds.status = 'active'
              AND u.status = 'active'
              AND (
                    (? = 'MORNING' AND ds.start_time < '12:00:00')
                 OR (? = 'AFTERNOON' AND ds.start_time >= '12:00:00')
              )
        """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, doctorId);
            st.setInt(2, dayOfWeek);
            st.setString(3, period);
            st.setString(4, period);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getTemporarySwapDeltaForPeriod(int doctorId, Date workDate, String period) {
        int delta = 0;
        String sql = """
            SELECT r.doctor_id AS requester_doctor_id,
                   i.work_date AS new_work_date,
                   CASE
                       WHEN i.work_date IS NOT NULL AND s_old.day_of_week IS NOT NULL AND i.day_of_week IS NOT NULL
                       THEN DATE_ADD(i.work_date, INTERVAL ((s_old.day_of_week - i.day_of_week + 7) % 7) DAY)
                       ELSE NULL
                   END AS old_work_date,
                   s_new.doctor_id AS counterpart_doctor_id,
                   CASE WHEN s_old.start_time < '12:00:00' THEN 'MORNING' ELSE 'AFTERNOON' END AS old_period,
                   CASE WHEN i.start_time < '12:00:00' THEN 'MORNING' ELSE 'AFTERNOON' END AS new_period
            FROM schedule_change_requests r
            JOIN schedule_change_request_items i ON r.request_id = i.request_id
            LEFT JOIN doctor_shifts s_old ON i.target_shift_id = s_old.shift_id
            LEFT JOIN doctor_shifts s_new ON s_new.shift_id = COALESCE(
                i.swap_shift_id,
                (
                    SELECT s2.shift_id
                    FROM doctor_shifts s2
                    WHERE s2.day_of_week = i.day_of_week
                      AND s2.start_time = i.start_time
                      AND s2.end_time = i.end_time
                      AND s2.doctor_id <> r.doctor_id
                    ORDER BY s2.shift_id
                    LIMIT 1
                )
            )
            WHERE r.status = 'APPROVED'
              AND r.request_type = 'TEMPORARY'
              AND r.scope_type = 'ONE_DATE'
              AND i.action_type = 'UPDATE'
              AND (i.work_date = ? OR (
                    i.work_date IS NOT NULL AND s_old.day_of_week IS NOT NULL AND i.day_of_week IS NOT NULL
                    AND DATE_ADD(i.work_date, INTERVAL ((s_old.day_of_week - i.day_of_week + 7) % 7) DAY) = ?
              ))
        """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setDate(1, workDate);
            st.setDate(2, workDate);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    int requesterId = rs.getInt("requester_doctor_id");
                    Date newWorkDate = rs.getDate("new_work_date");
                    Date oldWorkDate = rs.getDate("old_work_date");
                    int counterpartId = rs.getInt("counterpart_doctor_id");
                    boolean hasCounterpart = !rs.wasNull();
                    String oldPeriod = rs.getString("old_period");
                    String newPeriod = rs.getString("new_period");

                    if (newWorkDate == null || oldWorkDate == null) {
                        continue;
                    }

                    if (requesterId == doctorId) {
                        if (workDate.equals(oldWorkDate) && period.equalsIgnoreCase(oldPeriod)) {
                            delta -= 1;
                        }
                        if (workDate.equals(newWorkDate) && period.equalsIgnoreCase(newPeriod)) {
                            delta += 1;
                        }
                    }

                    if (hasCounterpart && counterpartId == doctorId) {
                        if (workDate.equals(newWorkDate) && period.equalsIgnoreCase(newPeriod)) {
                            delta -= 1;
                        }
                        if (workDate.equals(oldWorkDate) && period.equalsIgnoreCase(oldPeriod)) {
                            delta += 1;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return delta;
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
