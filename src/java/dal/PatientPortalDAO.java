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
import model.PatientRecordItem;

/**
 *
 * @author anngu
 */
public class PatientPortalDAO extends DBContext {

    public List<PatientRecordItem> getMedicalRecordsByUserId(int userId) {
        List<PatientRecordItem> list = new ArrayList<>();

        String sql = """
            SELECT
                a.appointment_id,
                a.appointment_date,
                a.appointment_time,
                COALESCE(du.full_name, 'Chưa cập nhật') AS doctor_name,
                COALESCE(mr.symptoms, a.symptom) AS symptoms,
                mr.diagnosis,
                mr.notes,
                mr.updated_at
            FROM patients p
            JOIN appointments a ON a.patient_id = p.patient_id
            LEFT JOIN doctors d ON d.doctor_id = a.doctor_id
            LEFT JOIN users du ON du.user_id = d.user_id
            JOIN medical_records mr ON mr.appointment_id = a.appointment_id
            WHERE p.user_id = ?
            ORDER BY a.appointment_date DESC, a.appointment_time DESC, mr.updated_at DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PatientRecordItem item = new PatientRecordItem();
                item.setAppointmentId(rs.getLong("appointment_id"));
                item.setAppointmentDate(rs.getDate("appointment_date"));
                item.setAppointmentTime(rs.getTime("appointment_time"));
                item.setDoctorName(rs.getString("doctor_name"));
                item.setSymptoms(rs.getString("symptoms"));
                item.setDiagnosis(rs.getString("diagnosis"));
                item.setNotes(rs.getString("notes"));
                item.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
