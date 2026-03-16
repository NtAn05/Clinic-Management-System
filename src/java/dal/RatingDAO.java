package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Rating_review;

public class RatingDAO extends DBContext {

    public List<Rating_review> getQuestions() {
        List<Rating_review> list = new ArrayList<>();
        String sql = "SELECT id, question_text FROM rating_questions";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Rating_review r = new Rating_review();
                r.setId(rs.getInt("id"));
                r.setQuestion_text(rs.getString("question_text"));

                list.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void insertReviewAnswer(int questionId, int rating, int userId, int doctorId) {

        String sql = "INSERT INTO review_answers(question_id, rating, users_id, doctor_id) VALUES (?,?,?,?)";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, questionId);
            ps.setInt(2, rating);
            ps.setInt(3, userId);
            ps.setInt(4, doctorId);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Double getAverageRating(int doctorID) {
        String sql = "SELECT AVG(rating) FROM review_answers WHERE doctor_id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, doctorID);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updateDoctorRating(int doctorID, Double avg) {
        String sql = "UPDATE doctors SET rating = ? WHERE doctor_id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setDouble(1, avg);
            ps.setInt(2, doctorID);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
