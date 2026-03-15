package dal;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportDAO extends DBContext {

    public int[] getAppointmentStatusStats() {
        int[] stats = new int[6]; // [total, booked, checked_in, waiting, completed, cancelled]
        String sql = """
            SELECT
                COUNT(*) AS total,
                COALESCE(SUM(CASE WHEN status = 'booked' THEN 1 ELSE 0 END), 0) AS booked,
                COALESCE(SUM(CASE WHEN status = 'checked_in' THEN 1 ELSE 0 END), 0) AS checked_in,
                COALESCE(SUM(CASE WHEN status = 'waiting' THEN 1 ELSE 0 END), 0) AS waiting,
                COALESCE(SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END), 0) AS completed,
                COALESCE(SUM(CASE WHEN status = 'cancelled' THEN 1 ELSE 0 END), 0) AS cancelled
            FROM appointments
        """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                stats[0] = rs.getInt("total");
                stats[1] = rs.getInt("booked");
                stats[2] = rs.getInt("checked_in");
                stats[3] = rs.getInt("waiting");
                stats[4] = rs.getInt("completed");
                stats[5] = rs.getInt("cancelled");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public int[] getLabRequestStatusStats() {
        int[] stats = new int[5]; // [total, pending, processing, completed, cancelled]
        String sql = """
            SELECT
                COUNT(*) AS total,
                COALESCE(SUM(CASE WHEN status = 'pending' THEN 1 ELSE 0 END), 0) AS pending,
                COALESCE(SUM(CASE WHEN status = 'processing' THEN 1 ELSE 0 END), 0) AS processing,
                COALESCE(SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END), 0) AS completed,
                COALESCE(SUM(CASE WHEN status = 'cancelled' THEN 1 ELSE 0 END), 0) AS cancelled
            FROM lab_requests
        """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                stats[0] = rs.getInt("total");
                stats[1] = rs.getInt("pending");
                stats[2] = rs.getInt("processing");
                stats[3] = rs.getInt("completed");
                stats[4] = rs.getInt("cancelled");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public BigDecimal[] getPaymentSummary() {
        BigDecimal[] summary = new BigDecimal[3]; // [totalAmount, paidAmount, pendingAmount]
        String sql = """
            SELECT
                COALESCE(SUM(amount), 0) AS totalAmount,
                COALESCE(SUM(CASE WHEN status = 'paid' THEN amount ELSE 0 END), 0) AS paidAmount,
                COALESCE(SUM(CASE WHEN status = 'pending' THEN amount ELSE 0 END), 0) AS pendingAmount
            FROM payments
        """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                summary[0] = rs.getBigDecimal("totalAmount");
                summary[1] = rs.getBigDecimal("paidAmount");
                summary[2] = rs.getBigDecimal("pendingAmount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summary;
    }

    public java.util.List<model.DoctorProductivity> getDoctorProductivity() {
        java.util.List<model.DoctorProductivity> list = new java.util.ArrayList<>();
        String sql = """
            SELECT d.doctor_id, u.full_name, 
                   COUNT(*) AS completed_count
            FROM appointments a
            JOIN doctors d ON a.doctor_id = d.doctor_id
            JOIN users u ON d.user_id = u.user_id
            WHERE a.status = 'completed'
            GROUP BY d.doctor_id, u.full_name
            ORDER BY completed_count DESC
            LIMIT 10
        """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                model.DoctorProductivity dp = new model.DoctorProductivity();
                dp.setDoctorId(rs.getInt("doctor_id"));
                dp.setDoctorName(rs.getString("full_name"));
                dp.setTotalCompletedAppointments(rs.getInt("completed_count"));
                list.add(dp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

