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

    /**
     * Lấy lab requests với filter
     */
    public List<LabRequest> getLabRequestsWithFilter(String status, String department, String priority, String searchTerm) {
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
        int paramIndex = 1;
        
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

        try (PreparedStatement st = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                st.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = st.executeQuery();
            
            while (rs.next()) {
                LabRequest lr = mapResultSetToLabRequest(rs);
                
                // Filter by priority if needed (assuming priority is stored in notes or separate field)
                // For now, we'll skip priority filter as it's not in the database schema
                // You may need to add a priority field to lab_requests table
                
                list.add(lr);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;
    }

    /**
     * Lấy lab request theo ID
     */
    public LabRequest getLabRequestById(int requestId) {
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
            WHERE lr.request_id = ?
        """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, requestId);
            ResultSet rs = st.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToLabRequest(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Cập nhật trạng thái lab request
     */
    public boolean updateLabRequestStatus(int requestId, String status) {
        String sql = "UPDATE lab_requests SET status = ? WHERE request_id = ?";
        
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, status);
            st.setInt(2, requestId);
            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật ghi chú cho lab request
     */
    public boolean updateLabRequestNotes(int requestId, String notes) {
        // Check if lab_result exists
        String checkSql = "SELECT result_id FROM lab_results WHERE request_id = ?";
        
        try (PreparedStatement checkSt = connection.prepareStatement(checkSql)) {
            checkSt.setInt(1, requestId);
            ResultSet rs = checkSt.executeQuery();
            
            if (rs.next()) {
                // Update existing
                String updateSql = "UPDATE lab_results SET notes = ? WHERE request_id = ?";
                try (PreparedStatement updateSt = connection.prepareStatement(updateSql)) {
                    updateSt.setString(1, notes);
                    updateSt.setInt(2, requestId);
                    updateSt.executeUpdate();
                }
            } else {
                // Insert new
                String insertSql = "INSERT INTO lab_results (request_id, notes) VALUES (?, ?)";
                try (PreparedStatement insertSt = connection.prepareStatement(insertSql)) {
                    insertSt.setInt(1, requestId);
                    insertSt.setString(2, notes);
                    insertSt.executeUpdate();
                }
            }
            
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gửi kết quả xét nghiệm: lưu lab_result, đổi trạng thái lab_request -> completed,
     * đổi trạng thái appointment -> waiting, đưa bệnh nhân trở lại danh sách chờ khám (exam_queue).
     */
    public boolean sendLabResult(int requestId, Integer technicianId, String resultFile, String notes) {
        try {
            connection.setAutoCommit(false);

            // 1) Lấy appointment_id, doctor_id từ lab_requests
            long appointmentId;
            int doctorId;
            String getSql = "SELECT appointment_id, doctor_id FROM lab_requests WHERE request_id = ?";
            try (PreparedStatement st = connection.prepareStatement(getSql)) {
                st.setInt(1, requestId);
                ResultSet rs = st.executeQuery();
                if (!rs.next()) {
                    connection.rollback();
                    return false;
                }
                appointmentId = rs.getLong("appointment_id");
                doctorId = rs.getInt("doctor_id");
            }

            // 2) Insert hoặc update lab_results
            String checkResult = "SELECT result_id FROM lab_results WHERE request_id = ?";
            try (PreparedStatement checkSt = connection.prepareStatement(checkResult)) {
                checkSt.setInt(1, requestId);
                ResultSet rs = checkSt.executeQuery();
                if (rs.next()) {
                    String updateResult = "UPDATE lab_results SET technician_id = ?, result_file = ?, notes = ?, completed_at = NOW() WHERE request_id = ?";
                    try (PreparedStatement up = connection.prepareStatement(updateResult)) {
                        up.setObject(1, technicianId);
                        up.setString(2, resultFile != null ? resultFile : "");
                        up.setString(3, notes != null ? notes : "");
                        up.setInt(4, requestId);
                        up.executeUpdate();
                    }
                } else {
                    String insertResult = "INSERT INTO lab_results (request_id, technician_id, result_file, notes, completed_at) VALUES (?, ?, ?, ?, NOW())";
                    try (PreparedStatement ins = connection.prepareStatement(insertResult)) {
                        ins.setInt(1, requestId);
                        ins.setObject(2, technicianId);
                        ins.setString(3, resultFile != null ? resultFile : "");
                        ins.setString(4, notes != null ? notes : "");
                        ins.executeUpdate();
                    }
                }
            }

            // 3) Cập nhật lab_requests.status = 'completed'
            try (PreparedStatement st = connection.prepareStatement("UPDATE lab_requests SET status = 'completed' WHERE request_id = ?")) {
                st.setInt(1, requestId);
                st.executeUpdate();
            }

            // 4) Cập nhật appointments.status = 'waiting' (trả về danh sách chờ khám)
            try (PreparedStatement st = connection.prepareStatement("UPDATE appointments SET status = 'waiting' WHERE appointment_id = ?")) {
                st.setLong(1, appointmentId);
                st.executeUpdate();
            }

            // 5) Đưa vào exam_queue (chờ khám): INSERT hoặc UPDATE status = 'waiting'
            String checkQueue = "SELECT queue_id FROM exam_queue WHERE appointment_id = ?";
            try (PreparedStatement checkSt = connection.prepareStatement(checkQueue)) {
                checkSt.setLong(1, appointmentId);
                ResultSet rs = checkSt.executeQuery();
                if (rs.next()) {
                    try (PreparedStatement up = connection.prepareStatement("UPDATE exam_queue SET status = 'waiting' WHERE appointment_id = ?")) {
                        up.setLong(1, appointmentId);
                        up.executeUpdate();
                    }
                } else {
                    // Lấy queue_position tiếp theo cho doctor
                    int nextPos = 1;
                    String maxSql = "SELECT COALESCE(MAX(queue_position), 0) + 1 AS np FROM exam_queue WHERE doctor_id = ?";
                    try (PreparedStatement maxSt = connection.prepareStatement(maxSql)) {
                        maxSt.setInt(1, doctorId);
                        ResultSet maxRs = maxSt.executeQuery();
                        if (maxRs.next()) nextPos = maxRs.getInt("np");
                    }
                    try (PreparedStatement ins = connection.prepareStatement("INSERT INTO exam_queue (appointment_id, doctor_id, queue_position, status) VALUES (?, ?, ?, 'waiting')")) {
                        ins.setLong(1, appointmentId);
                        ins.setInt(2, doctorId);
                        ins.setInt(3, nextPos);
                        ins.executeUpdate();
                    }
                }
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /**
     * Lấy danh sách các khoa/phòng (specializations)
     */
    public List<String> getAllSpecializations() {
        List<String> list = new ArrayList<>();
        
        String sql = "SELECT DISTINCT specialization FROM doctors ORDER BY specialization";
        
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            
            while (rs.next()) {
                list.add(rs.getString("specialization"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;
    }

    /**
     * Lấy thống kê lab requests
     */
    public int[] getLabRequestStatistics() {
        return getLabRequestStatisticsWithFilter(null, null, null);
    }

    /**
     * Lấy thống kê lab requests theo bộ lọc (không giới hạn theo ngày)
     * @return stats [total, pending, processing, completed]
     */
    public int[] getLabRequestStatisticsWithFilter(String status, String department, String searchTerm) {
        int[] stats = new int[4]; // [total, pending, processing, completed]

        StringBuilder sql = new StringBuilder("""
            SELECT
                COUNT(*) as total,
                COALESCE(SUM(CASE WHEN lr.status = 'pending' THEN 1 ELSE 0 END), 0) as pending,
                COALESCE(SUM(CASE WHEN lr.status = 'processing' THEN 1 ELSE 0 END), 0) as processing,
                COALESCE(SUM(CASE WHEN lr.status = 'completed' THEN 1 ELSE 0 END), 0) as completed
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
                stats[0] = rs.getInt("total");
                stats[1] = rs.getInt("pending");
                stats[2] = rs.getInt("processing");
                stats[3] = rs.getInt("completed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stats;
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
}

