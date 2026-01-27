package dal;

import model.LabRequest;
import model.Patient;
import model.Doctor;
import model.Appointment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LabRequestDAO extends DBContext {

    /**
     * Lấy danh sách tất cả lab requests với thông tin đầy đủ
     */
    public List<LabRequest> getAllLabRequests() {
        List<LabRequest> list = new ArrayList<>();
        
        String sql = """
            SELECT 
                lr.request_id,
                lr.appointment_id,
                lr.doctor_id,
                lr.status,
                lr.created_at,
                p.patient_id,
                p.user_id,
                p.full_name AS patient_name,
                p.phone AS patient_phone,
                p.dob,
                p.address,
                p.email AS patient_email,
                p.gender,
                d.doctor_id,
                d.specialization,
                u.full_name AS doctor_name,
                u.phone AS doctor_phone,
                u.email AS doctor_email,
                a.symptom,
                a.status AS appointment_status
            FROM lab_requests lr
            JOIN appointments a ON lr.appointment_id = a.appointment_id
            JOIN patients p ON a.patient_id = p.patient_id
            JOIN doctors d ON lr.doctor_id = d.doctor_id
            JOIN users u ON d.user_id = u.user_id
            ORDER BY lr.created_at DESC
        """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            
            while (rs.next()) {
                LabRequest lr = mapResultSetToLabRequest(rs);
                list.add(lr);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;
    }


    /**
     * Map ResultSet to LabRequest object
     */
    private LabRequest mapResultSetToLabRequest(ResultSet rs) throws SQLException {
        LabRequest lr = new LabRequest();
        lr.setRequestId(rs.getInt("request_id"));
        lr.setAppointmentId(rs.getLong("appointment_id"));
        lr.setDoctorId(rs.getInt("doctor_id"));
        lr.setStatus(rs.getString("status"));
        lr.setCreatedAt(rs.getTimestamp("created_at"));
        
        // Map Patient
        Patient patient = new Patient();
        patient.setPatientId(rs.getLong("patient_id"));
        patient.setUserId(rs.getInt("user_id"));
        patient.setFullName(rs.getString("patient_name"));
        patient.setPhone(rs.getString("patient_phone"));
        patient.setDob(rs.getDate("dob"));
        patient.setAddress(rs.getString("address"));
        patient.setEmail(rs.getString("patient_email"));
        patient.setGender(rs.getString("gender"));
        lr.setPatient(patient);
        
        // Map Doctor
        Doctor doctor = new Doctor();
        doctor.setDoctorId(rs.getInt("doctor_id"));
        doctor.setSpecialization(rs.getString("specialization"));
        doctor.setFullName(rs.getString("doctor_name"));
        doctor.setPhone(rs.getString("doctor_phone"));
        doctor.setEmail(rs.getString("doctor_email"));
        lr.setDoctor(doctor);
        
        // Map Appointment
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(rs.getLong("appointment_id"));
        appointment.setSymptom(rs.getString("symptom"));
        appointment.setStatus(rs.getString("appointment_status"));
        lr.setAppointment(appointment);
        
        // Get notes from lab_results if exists
        String notesSql = "SELECT notes FROM lab_results WHERE request_id = ?";
        try (PreparedStatement notesSt = connection.prepareStatement(notesSql)) {
            notesSt.setInt(1, lr.getRequestId());
            ResultSet notesRs = notesSt.executeQuery();
            if (notesRs.next()) {
                lr.setNotes(notesRs.getString("notes"));
            }
        } catch (SQLException e) {
            // Notes not found, that's okay
        }
        
        return lr;
    }

    /**
     * Đếm tổng số lab requests với filter (cho pagination)
     */
    public int countLabRequestsWithFilter(String status, String department, String priority, String searchTerm) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*) as total
            FROM lab_requests lr
            JOIN appointments a ON lr.appointment_id = a.appointment_id
            JOIN patients p ON a.patient_id = p.patient_id
            JOIN doctors d ON lr.doctor_id = d.doctor_id
            JOIN users u ON d.user_id = u.user_id
            WHERE 1=1
        """);
        
        List<Object> params = new ArrayList<>();
        
        if (status != null && !status.isEmpty()) {
            sql.append(" AND lr.status = ?");
            params.add(status);
        }
        
        if (department != null && !department.isEmpty()) {
            sql.append(" AND d.specialization = ?");
            params.add(department);
        }
        
        if (searchTerm != null && !searchTerm.isEmpty()) {
            sql.append(" AND (p.full_name LIKE ? OR p.phone LIKE ? OR CONCAT('LAB-', YEAR(lr.created_at), '-', LPAD(lr.request_id, 4, '0')) LIKE ?)");
            String searchPattern = "%" + searchTerm + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        try (PreparedStatement st = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                st.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }

    /**
     * Lấy lab requests với filter và pagination
     */
    public List<LabRequest> getLabRequestsWithFilterAndPagination(String status, String department, String priority, String searchTerm, int page, int pageSize) {
        List<LabRequest> list = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder("""
            SELECT 
                lr.request_id,
                lr.appointment_id,
                lr.doctor_id,
                lr.status,
                lr.created_at,
                p.patient_id,
                p.user_id,
                p.full_name AS patient_name,
                p.phone AS patient_phone,
                p.dob,
                p.address,
                p.email AS patient_email,
                p.gender,
                d.doctor_id,
                d.specialization,
                u.full_name AS doctor_name,
                u.phone AS doctor_phone,
                u.email AS doctor_email,
                a.symptom,
                a.status AS appointment_status
            FROM lab_requests lr
            JOIN appointments a ON lr.appointment_id = a.appointment_id
            JOIN patients p ON a.patient_id = p.patient_id
            JOIN doctors d ON lr.doctor_id = d.doctor_id
            JOIN users u ON d.user_id = u.user_id
            WHERE 1=1
        """);
        
        List<Object> params = new ArrayList<>();
        
        if (status != null && !status.isEmpty()) {
            sql.append(" AND lr.status = ?");
            params.add(status);
        }
        
        if (department != null && !department.isEmpty()) {
            sql.append(" AND d.specialization = ?");
            params.add(department);
        }
        
        if (searchTerm != null && !searchTerm.isEmpty()) {
            sql.append(" AND (p.full_name LIKE ? OR p.phone LIKE ? OR CONCAT('LAB-', YEAR(lr.created_at), '-', LPAD(lr.request_id, 4, '0')) LIKE ?)");
            String searchPattern = "%" + searchTerm + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        sql.append(" ORDER BY lr.created_at DESC");
        sql.append(" LIMIT ? OFFSET ?");
        
        int offset = (page - 1) * pageSize;
        params.add(pageSize);
        params.add(offset);

        try (PreparedStatement st = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                st.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = st.executeQuery();
            
            while (rs.next()) {
                LabRequest lr = mapResultSetToLabRequest(rs);
                list.add(lr);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;
    }

}