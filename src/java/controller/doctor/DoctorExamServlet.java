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
import java.util.List;
import model.Doctor;
import model.ExaminationHistoryItem;
import model.ExamLabItem;
import model.DoctorQueueItem;
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
        try {
            Doctor doctor = validateDoctor(request, response);
            if (doctor == null) {
                return;
            }

            String appointmentParam = request.getParameter("appointmentId");
            if (appointmentParam == null || appointmentParam.trim().isEmpty()) {
                request.setAttribute("pageError", "Thiếu mã lịch khám. Vui lòng quay lại danh sách chờ khám.");
                request.getRequestDispatcher("/pages/doctors/exam.jsp").forward(request, response);
                return;
            }

            long appointmentId;
            try {
                appointmentId = Long.parseLong(appointmentParam.trim());
            } catch (NumberFormatException ex) {
                request.setAttribute("pageError", "Mã lịch khám không hợp lệ.");
                request.getRequestDispatcher("/pages/doctors/exam.jsp").forward(request, response);
                return;
            }

            DoctorDAO doctorDAO = new DoctorDAO();
            DoctorQueueItem examData = doctorDAO.getQueueItemByAppointment(doctor.getDoctorId(), appointmentId);
            if (examData == null) {
                request.setAttribute("pageError", "Không tìm thấy bệnh nhân trong hàng đợi khám của bác sĩ.");
                request.getRequestDispatcher("/pages/doctors/exam.jsp").forward(request, response);
                return;
            }

            if ("waiting".equalsIgnoreCase(examData.getStatus())) {
                doctorDAO.startExamination(appointmentId);
                examData.setStatus("examining");
            }

            request.setAttribute("examData", examData);
            List<ExamLabItem> labResults = doctorDAO.getLabResultsByAppointment(appointmentId);
            request.setAttribute("labResults", labResults);

             List<ExaminationHistoryItem> examinationHistory
                = doctorDAO.getExaminationHistoryByAppointment(appointmentId);
            request.setAttribute("examData", examData);
            request.setAttribute("historyList", examinationHistory);
            request.setAttribute("success", request.getParameter("success"));
            request.getRequestDispatcher("/pages/doctors/exam.jsp").forward(request, response);
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("pageError", "Đã xảy ra lỗi khi tải màn hình khám bệnh. Vui lòng thử lại.");
            request.getRequestDispatcher("/pages/doctors/exam.jsp").forward(request, response);
        }
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
        if (appointmentParam == null || appointmentParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/doctorDashboard?error=missingAppointment");
            return;
        }

        long appointmentId;
        try {
            appointmentId = Long.parseLong(appointmentParam.trim());
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