/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public List<Doctor> getAllDoctors() {

        List<Doctor> list = new ArrayList<>();

        String sql = """
        SELECT 
            d.doctor_id,
            u.full_name,
            d.specialization,
            d.qualification,
            d.experience_years,
            d.rating,
            d.price_booking,
            d.clinic_address,
            u.image_url
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
                d.setClinic_address(rs.getString("clinic_address"));
                d.setImage(rs.getString("image_url"));

                list.add(d);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Doctor> filterDoctors(
            String name,
            String priceFrom,
            String priceTo,
            String experience,
            String sort) {

        List<Doctor> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
        SELECT 
            d.doctor_id,
            u.full_name,
            d.specialization,
            d.qualification,
            d.experience_years,
            d.rating,
            d.price_booking,
            d.clinic_address,
            u.image_url
        FROM doctors d
        JOIN users u ON d.user_id = u.user_id
        WHERE 1 = 1
    """);

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
                d.setClinic_address(rs.getString("clinic_address"));
                d.setImage(rs.getString("image_url"));

                list.add(d);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public Doctor getDoctorById(String doctorID) {

        String sql = """
        SELECT 
            d.doctor_id,
            u.full_name,
            d.specialization,
            d.qualification,
            d.experience_years,
            d.rating,
            d.price_booking,
            d.clinic_address,
            u.image_url
        FROM doctors d
        JOIN users u ON d.user_id = u.user_id
        WHERE d.doctor_id = ?
    """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {

            st.setInt(1, Integer.parseInt(doctorID));
            ResultSet rs = st.executeQuery();

            if (rs.next()) {

                Doctor d = new Doctor();
                d.setDoctorId(rs.getInt("doctor_id"));
                d.setFullName(rs.getString("full_name"));
                d.setSpecialization(rs.getString("specialization"));
                d.setQualification(rs.getString("qualification"));
                d.setExperience_years(rs.getInt("experience_years"));
                d.setRating(rs.getDouble("rating"));
                d.setPrice(rs.getDouble("price_booking"));
                d.setClinic_address(rs.getString("clinic_address"));
                d.setImage(rs.getString("image_url"));

                return d;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

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
                + "d.qualification, "
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
                + "d.qualification, "
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
}
