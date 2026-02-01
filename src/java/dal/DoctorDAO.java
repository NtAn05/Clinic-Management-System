package dal;

import model.Doctor;
import model.DoctorShift;
import model.DoctorQueueItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.doctorExamination.DoctorDashboardStats;

public class DoctorDAO extends DBContext {

    /* =========================
       LẤY DOCTOR THEO USER_ID
       ========================= */
    public List<Doctor> getAllDoctors() {
        List<Doctor> list = new ArrayList<>();

        String sql = """
        SELECT 
            d.doctor_id,
            d.specialization,
            d.qualification,
            d.experience_years,
            d.rating,
            d.price_booking,
            d.image_url,
            d.clinic_address,
            u.full_name
        FROM doctors d
        JOIN users u ON d.user_id = u.user_id
    """;

        try (PreparedStatement st = connection.prepareStatement(sql); ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                Doctor d = new Doctor();
                d.setDoctorId(rs.getInt("doctor_id"));
                d.setFullName(rs.getString("full_name"));
                d.setSpecialization(rs.getString("specialization"));
                d.setQualification(rs.getString("qualification"));
                d.setExperience_years(rs.getInt("experience_years"));
                d.setRating(rs.getDouble("rating"));
                d.setPrice(rs.getDouble("price_booking"));
                d.setImage(rs.getString("image_url"));
                d.setClinic_address(rs.getString("clinic_address"));

                list.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public Doctor getDoctorById(String doctorID) {
    Doctor d = null;

    String sql = """
        SELECT 
            d.doctor_id,
            d.specialization,
            d.qualification,
            d.experience_years,
            d.rating,
            d.price_booking,
            d.image_url,
            d.clinic_address,
            u.full_name
        FROM doctors d
        JOIN users u ON d.user_id = u.user_id
        WHERE d.doctor_id = ?
    """;

    try (PreparedStatement st = connection.prepareStatement(sql)) {
        st.setString(1, doctorID);          
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            d = new Doctor();
            d.setDoctorId(rs.getInt("doctor_id"));
            d.setFullName(rs.getString("full_name"));
            d.setSpecialization(rs.getString("specialization"));
            d.setQualification(rs.getString("qualification"));
            d.setExperience_years(rs.getInt("experience_years"));
            d.setRating(rs.getDouble("rating"));
            d.setPrice(rs.getDouble("price_booking"));
            d.setImage(rs.getString("image_url"));
            d.setClinic_address(rs.getString("clinic_address"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return d;
}


    public List<Doctor> filterDoctors(
            String name,
            String priceFrom,
            String priceTo,
            String experience,
            String sort
    ) {

        List<Doctor> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
        SELECT 
            d.doctor_id,
            d.specialization,
            d.qualification,
            d.experience_years,
            d.rating,
            d.price_booking,
            d.image_url,
            d.clinic_address,
            u.full_name
        FROM doctors d
        JOIN users u ON d.user_id = u.user_id
        WHERE 1 = 1
    """);

        // ===== build điều kiện =====
        if (name != null && !name.trim().isEmpty()) {
            sql.append(" AND u.full_name LIKE ? ");
        }

        if (priceFrom != null && !priceFrom.isEmpty()) {
            sql.append(" AND d.price_booking >= ? ");
        }

        if (priceTo != null && !priceTo.isEmpty()) {
            sql.append(" AND d.price_booking <= ? ");
        }

        if (experience != null && !experience.isEmpty()) {
            sql.append(" AND d.experience_years >= ? ");
        }

        if ("priceAsc".equals(sort)) {
            sql.append(" ORDER BY d.price_booking ASC ");
        } else if ("priceDesc".equals(sort)) {
            sql.append(" ORDER BY d.price_booking DESC ");
        } else if ("rating".equals(sort)) {
            sql.append(" ORDER BY d.rating DESC ");
        }

        try (PreparedStatement st = connection.prepareStatement(sql.toString())) {

            int index = 1;

            if (name != null && !name.trim().isEmpty()) {
                st.setString(index++, "%" + name + "%");
            }
            if (priceFrom != null && !priceFrom.isEmpty()) {
                st.setDouble(index++, Double.parseDouble(priceFrom));
            }
            if (priceTo != null && !priceTo.isEmpty()) {
                st.setDouble(index++, Double.parseDouble(priceTo));
            }
            if (experience != null && !experience.isEmpty()) {
                st.setInt(index++, Integer.parseInt(experience));
            }

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Doctor d = new Doctor();
                d.setDoctorId(rs.getInt("doctor_id"));
                d.setFullName(rs.getString("full_name"));
                d.setSpecialization(rs.getString("specialization"));
                d.setQualification(rs.getString("qualification"));
                d.setExperience_years(rs.getInt("experience_years"));
                d.setRating(rs.getDouble("rating"));
                d.setPrice(rs.getDouble("price_booking"));
                d.setImage(rs.getString("image_url"));
                d.setClinic_address(rs.getString("clinic_address"));

                list.add(d);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
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

    public List<DoctorQueueItem> getQueueByDoctorWithFilter(
            int doctorId,
            String status,
            String keyword
    ) {
        List<DoctorQueueItem> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
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

   
}
