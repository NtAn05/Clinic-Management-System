/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.ratingDoctors;

import dal.DoctorDAO;
import dal.RatingDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.Doctor;
import model.Rating_note;
import model.Rating_review;

/**
 *
 * @author Admin
 */
public class ListOfRating extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ListOfRating</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ListOfRating at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      String doc= request.getParameter("btnDoctorID");
        int doctorId = Integer.parseInt(doc);
        DoctorDAO doctorDAO = new DoctorDAO();
        RatingDAO ratingDAO = new RatingDAO();
        Doctor doctor = doctorDAO.getDoctorById(doc);
        System.out.println("===================" +doctor.getFullName());
        List<Rating_review> questions = ratingDAO.getQuestions();
        for (Rating_review q : questions) {
            if (q.getId() != 5) {
                double avg = ratingDAO.getAverageRating(q.getId(), doctorId);
                int total = ratingDAO.getTotalReview(q.getId(), doctorId);

                q.setAvgRating(avg);
                q.setTotalReviews(total);
            }
        }
        List<Rating_note> notes = ratingDAO.getNotesByDoctor(doctorId);

        request.setAttribute("doctor", doctor);
        request.setAttribute("questions", questions);
        request.setAttribute("notes", notes);

        
request.getRequestDispatcher("/pages/rating/ListRatingOfDoctor/ListOfRating.jsp")
                .forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
