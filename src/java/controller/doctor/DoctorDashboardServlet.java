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
import java.time.LocalDate;
import java.util.List;
import model.Doctor;
import model.DoctorDashboardStats;
import model.DoctorQueueItem;
import model.DoctorShift;
import model.Patient;
import model.User;

/**
 *
 * @author anngu
 */
public class DoctorDashboardServlet extends HttpServlet {

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
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/pages/auth/login.jsp");
            return;
        }

        User account = (User) session.getAttribute("account");
        if (account == null || account.getRole() == null || !"doctor".equalsIgnoreCase(account.getRole().name())) {
            response.sendRedirect(request.getContextPath() + "/pages/auth/login.jsp");
            return;
        }

        DoctorDAO doctorDAO = new DoctorDAO();
        Doctor doctor = doctorDAO.getDoctorByUserId(account.getUserId());
        if (doctor == null) {
            response.sendRedirect(request.getContextPath() + "/pages/auth/login.jsp");
            return;
        }

        int doctorId = doctor.getDoctorId();
        session.setAttribute("doctorName", doctor.getFullName());
        
//        int doctorId = 3;// test 
        //lấy thông tin lọc
        String status = request.getParameter("status");
        String keyword = request.getParameter("keyword");

        if (status == null || status.isBlank()) {
            status = "all";
        }

        //1️⃣ Danh sách bệnh nhân đang chờ khám
        List<DoctorQueueItem> queueList
                = doctorDAO.getQueueByDoctorWithFilter(doctorId, status, keyword);

        // Thống kê số liệu
        DoctorDashboardStats stats
                = doctorDAO.getDashboardStats(doctorId);

        //Ca làm việc trong ngày
        int dayOfWeek = LocalDate.now().getDayOfWeek().getValue() % 7; // CN = 0
//        int dayOfWeek = 1; // test 
        List<DoctorShift> shifts
                = doctorDAO.getShiftsByDoctorAndDay(doctorId, dayOfWeek);

        request.setAttribute("queueList", queueList);
        request.setAttribute("stats", stats);
        request.setAttribute("shifts", shifts);

        request.getRequestDispatcher("/pages/doctors/doctorDashboard.jsp")
                .forward(request, response);
//        String queueId = request.getParameter("queueId");
//        request.setAttribute("queueId", queueId); //  để sau dùng
//
//        request.getRequestDispatcher("/pages/doctors/exam.jsp").forward(request, response);
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
        request.getRequestDispatcher("/pages/doctors/doctorDashboard.jsp")
                .forward(request, response);
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
