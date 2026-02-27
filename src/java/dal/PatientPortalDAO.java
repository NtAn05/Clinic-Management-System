package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.MedicalRecord;

public class PatientPortalDAO extends DBContext {

    private static final String SECTION_HISTORY = "TIỀN SỬ";
    private static final String SECTION_DOCTOR_NOTE = "GHI CHÚ BÁC SĨ";
    private static final String SECTION_TREATMENT_PLAN = "PHƯƠNG ÁN ĐIỀU TRỊ";

    public List<MedicalRecord> getMedicalRecordsByUserId(int userId) {
        List<MedicalRecord> list = new ArrayList<>();

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
                MedicalRecord item = new MedicalRecord();
                String notes = rs.getString("notes");
                String history = extractSection(notes, SECTION_HISTORY);

                item.setAppointmentId(rs.getLong("appointment_id"));
                item.setAppointmentDate(rs.getDate("appointment_date"));
                item.setAppointmentTime(rs.getTime("appointment_time"));
                item.setDoctorName(rs.getString("doctor_name"));
                item.setSymptoms(rs.getString("symptoms"));
                item.setDiagnosis(rs.getString("diagnosis"));
                item.setNotes(notes);
                item.setHistory(history);
                item.setHistoryAllergies(extractHistoryLine(history, "Dị ứng"));
                item.setHistoryChronic(extractHistoryLine(history, "Bệnh mãn tính"));
                item.setHistoryFamily(extractHistoryLine(history, "Tiền sử gia đình"));
                item.setHistorySocial(extractHistoryLine(history, "Tiền sử xã hội"));
                item.setHistoryVaccination(extractHistoryLine(history, "Lịch sử tiêm chủng"));
                item.setDoctorNote(extractSection(notes, SECTION_DOCTOR_NOTE));
                item.setTreatmentPlan(extractSection(notes, SECTION_TREATMENT_PLAN));
                item.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private String extractSection(String notes, String sectionTitle) {
        if (notes == null || notes.isBlank()) {
            return "";
        }

        String marker = "[" + sectionTitle + "]";
        int start = notes.indexOf(marker);
        if (start < 0) {
            return "";
        }

        int contentStart = start + marker.length();
        while (contentStart < notes.length() && (notes.charAt(contentStart) == '\n' || notes.charAt(contentStart) == '\r')) {
            contentStart++;
        }

        int end = notes.length();
        int nextMarker = notes.indexOf("[", contentStart);
        while (nextMarker >= 0) {
            int close = notes.indexOf("]", nextMarker);
            if (close > nextMarker) {
                end = nextMarker;
                break;
            }
            nextMarker = notes.indexOf("[", nextMarker + 1);
        }

        return notes.substring(contentStart, end).trim();
    }

    private String extractHistoryLine(String historySection, String label) {
        if (historySection == null || historySection.isBlank()) {
            return "";
        }

        String[] lines = historySection.split("\\R");
        String prefix = label + ":";

        for (String line : lines) {
            String normalized = line.trim();
            if (normalized.startsWith("-")) {
                normalized = normalized.substring(1).trim();
            }

            if (normalized.regionMatches(true, 0, prefix, 0, prefix.length())) {
                return normalized.substring(prefix.length()).trim();
            }
        }

        return "";
    }
}