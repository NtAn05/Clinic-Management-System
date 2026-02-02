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
import model.Doctor;

/**
 *
 * @author Admin
 */
public class AppointmentDAO extends DBContext{
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

    try (PreparedStatement st = connection.prepareStatement(sql);
         ResultSet rs = st.executeQuery()) {

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

    public Doctor getDoctorById(String doctorID) {
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

            return d;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return null; 
}
    
}
