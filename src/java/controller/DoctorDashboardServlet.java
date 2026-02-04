/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import model.Appointment;
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
import model.DoctorQueueItem;
import model.DoctorShift;
import model.Doctor;
import model.User;
import model.doctorExamination.DoctorDashboardStats;

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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet DoctorDashboardServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet DoctorDashboardServlet at " + request.getContextPath() + "</h1>");
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
//        HttpSession session = request.getSession(false);
//        if (session == null || session.getAttribute("doctorId") == null) {
//            response.sendRedirect(request.getContextPath() + "/login");
//            return;
//        }
//
//        int doctorId = (int) session.getAttribute("doctorId");

        int doctorId = 1;// test 
        //lấy thông tin lọc
        String status = request.getParameter("status");
        String keyword = request.getParameter("keyword");

        if (status == null || status.isBlank()) {
            status = "all";
        }
        DoctorDAO doctorDAO = new DoctorDAO();

        //1️⃣ Danh sách bệnh nhân đang chờ khám
        List<DoctorQueueItem> queueList
                = doctorDAO.getQueueByDoctorWithFilter(doctorId, status, keyword);

        // Thống kê số liệu
        DoctorDashboardStats stats
                = doctorDAO.getDashboardStats(doctorId);

        //Ca làm việc trong ngày
//        int dayOfWeek = LocalDate.now().getDayOfWeek().getValue() % 7; // CN = 0
        int dayOfWeek = 1; // test 
        List<DoctorShift> shifts
                = doctorDAO.getShiftsByDoctorAndDay(doctorId, dayOfWeek);

        request.setAttribute("queueList", queueList);
        request.setAttribute("stats", stats);
        request.setAttribute("shifts", shifts);

        request.getRequestDispatcher("pages/doctors/doctorDashboard.jsp")
                .forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("pages/doctors/doctorDashboard.jsp")
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
