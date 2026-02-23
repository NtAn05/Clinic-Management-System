/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.doctor;

import dal.DoctorDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import model.Doctor;
import model.DoctorQueueItem;
import model.Patient;
import model.User;

/**
 *
 * @author anngu
 */
public class DoctorExamServlet extends HttpServlet {

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
        doGet(request, response);
    }

    private Doctor validateDoctor(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/pages/auth/login.jsp");
            return null;
        }

        User account = (User) session.getAttribute("account");
        if (account == null || account.getRole() == null || !"doctor".equalsIgnoreCase(account.getRole().name())) {
            response.sendRedirect(request.getContextPath() + "/pages/auth/login.jsp");
            return null;
        }

        DoctorDAO doctorDAO = new DoctorDAO();
        Doctor doctor = doctorDAO.getDoctorByUserId(account.getUserId());
        if (doctor == null) {
            response.sendRedirect(request.getContextPath() + "/pages/auth/login.jsp");
            return null;
        }

        session.setAttribute("doctorName", doctor.getFullName());
        return doctor;
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
        Doctor doctor = validateDoctor(request, response);
        if (doctor == null) {
            return;
        }

        String appointmentParam = request.getParameter("appointmentId");
        if (appointmentParam == null || appointmentParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/doctorDashboard?error=missingAppointment");
            return;
        }

        long appointmentId;
        try {
            appointmentId = Long.parseLong(appointmentParam);
        } catch (NumberFormatException ex) {
            response.sendRedirect(request.getContextPath() + "/doctorDashboard?error=invalidAppointment");
            return;
        }

        DoctorDAO doctorDAO = new DoctorDAO();
        DoctorQueueItem examData = doctorDAO.getQueueItemByAppointment(doctor.getDoctorId(), appointmentId);
        if (examData == null) {
            response.sendRedirect(request.getContextPath() + "/doctorDashboard?error=notInQueue");
            return;
        }

        if ("waiting".equalsIgnoreCase(examData.getStatus())) {
            doctorDAO.startExamination(appointmentId);
            examData.setStatus("examining");
        }

        request.setAttribute("examData", examData);
        request.setAttribute("success", request.getParameter("success"));
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
        Doctor doctor = validateDoctor(request, response);
        if (doctor == null) {
            return;
        }

        String appointmentParam = request.getParameter("appointmentId");
        String action = request.getParameter("action");
        if (appointmentParam == null || appointmentParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/doctorDashboard?error=missingAppointment");
            return;
        }

        long appointmentId;
        try {
            appointmentId = Long.parseLong(appointmentParam);
        } catch (NumberFormatException ex) {
            response.sendRedirect(request.getContextPath() + "/doctorDashboard?error=invalidAppointment");
            return;
        }

        DoctorDAO doctorDAO = new DoctorDAO();
        DoctorQueueItem examData = doctorDAO.getQueueItemByAppointment(doctor.getDoctorId(), appointmentId);
        if (examData == null) {
            response.sendRedirect(request.getContextPath() + "/doctorDashboard?error=notInQueue");
            return;
        }

        if ("finish".equalsIgnoreCase(action)) {
            doctorDAO.finishExamination(appointmentId);
            response.sendRedirect(request.getContextPath() + "/doctorDashboard?success=examFinished");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/doctor/exam?appointmentId=" + appointmentId + "&success=saved");
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
