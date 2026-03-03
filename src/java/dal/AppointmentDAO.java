/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Appointment;
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
                + "(patient_id, doctor_id, shift_id, booking_type, status, symptom) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement st = connection.prepareStatement(sql)) {

            st.setLong(1, a.getPatientId());
            st.setInt(2, a.getDoctorId());
            st.setInt(3, a.getShiftId());
            st.setString(4, a.getBookingType());
            st.setString(5, a.getStatus());
            st.setString(6, a.getSymptom());

            return st.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public long addPatient(Patient p) {
        String sql = "INSERT INTO patients (user_id, full_name, phone, dob, email, gender) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

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

}
