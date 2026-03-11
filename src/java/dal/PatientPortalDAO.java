package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.MedicalRecord;
import model.PrescriptionItem;

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
public List<MedicalRecord> getPrescriptionsByUserId(int userId) {
        List<MedicalRecord> list = new ArrayList<>();

        String sql = """
            SELECT
                p.prescription_id,
                p.notes AS prescription_note,
                p.created_at,
                a.appointment_id,
                a.appointment_date,
                a.appointment_time,
                COALESCE(du.full_name, 'Chưa cập nhật') AS doctor_name,
                COALESCE(mr.diagnosis, '') AS diagnosis
            FROM patients pt
            JOIN appointments a ON a.patient_id = pt.patient_id
            JOIN prescriptions p ON p.appointment_id = a.appointment_id
            LEFT JOIN medical_records mr ON mr.appointment_id = a.appointment_id
            LEFT JOIN doctors d ON d.doctor_id = p.doctor_id
            LEFT JOIN users du ON du.user_id = d.user_id
            WHERE pt.user_id = ?
            ORDER BY p.created_at DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MedicalRecord item = new MedicalRecord();
                item.setPrescriptionId(rs.getInt("prescription_id"));
                item.setPrescriptionNote(rs.getString("prescription_note"));
                item.setAppointmentId(rs.getLong("appointment_id"));
                item.setAppointmentDate(rs.getDate("appointment_date"));
                item.setAppointmentTime(rs.getTime("appointment_time"));
                item.setDoctorName(rs.getString("doctor_name"));
                item.setDiagnosis(rs.getString("diagnosis"));
                item.setUpdatedAt(rs.getTimestamp("created_at"));
                item.setPrescriptionItems(getPrescriptionItemsByPrescriptionId(item.getPrescriptionId()));
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private List<PrescriptionItem> getPrescriptionItemsByPrescriptionId(int prescriptionId) {
        List<PrescriptionItem> list = new ArrayList<>();

        String sql = """
            SELECT
                pi.item_id,
                pi.prescription_id,
                pi.medicine_id,
                m.medicine_name,
                m.unit,
                pi.dosage,
                pi.frequency,
                pi.duration_days,
                pi.instruction,
                pi.quantity
            FROM prescription_items pi
            JOIN medicines m ON m.medicine_id = pi.medicine_id
            WHERE pi.prescription_id = ?
            ORDER BY pi.item_id
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, prescriptionId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PrescriptionItem item = new PrescriptionItem();
                item.setItemId(rs.getInt("item_id"));
                item.setPrescriptionId(rs.getInt("prescription_id"));
                item.setMedicineId(rs.getInt("medicine_id"));
                item.setMedicineName(rs.getString("medicine_name"));
                item.setUnit(rs.getString("unit"));
                item.setDosage(rs.getString("dosage"));
                item.setFrequency(rs.getString("frequency"));
                item.setDurationDays(rs.getString("duration_days"));
                item.setInstruction(rs.getString("instruction"));
                item.setQuantity(rs.getString("quantity"));
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

}