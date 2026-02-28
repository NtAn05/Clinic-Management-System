/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller.appointments;

import dal.AppointmentDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Appointment;
import model.Patient;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;

/**
 *
 * @author Admin
 */
public class AppointmentPaymentServlet extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
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
            out.println("<title>Servlet AppointmentPaymentServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AppointmentPaymentServlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
      String code = request.getParameter("code");
        String status = request.getParameter("status");

        if ("00".equals(code) && "PAID".equals(status)) {
            // ✅ Thanh toán thành công → lấy data từ session và lưu DB
            HttpSession session = request.getSession();
            Patient patient = (Patient) session.getAttribute("pendingPatient");
            Appointment appointment = (Appointment) session.getAttribute("pendingAppointment");

            if (patient != null && appointment != null) {
                AppointmentDAO dao = new AppointmentDAO();
                //dao.addAppointment(patient, appointment); // gọi method lưu DB của bạn

                // Xóa session sau khi lưu xong
                session.removeAttribute("pendingPatient");
                session.removeAttribute("pendingAppointment");
            }

            request.setAttribute("message", "Đặt lịch và thanh toán thành công!");
            request.getRequestDispatcher("/pages/appointments/appointment/appointmentCompleted.jsp")
                    .forward(request, response);
        } else {
            // ❌ Thanh toán thất bại → không lưu DB
            request.setAttribute("message", "Thanh toán thất bại hoặc đã huỷ!");
            request.getRequestDispatcher("/pages/appointments/appointment/appointmentfailPayment.jsp")
                    .forward(request, response);
        }
    }
     

    /** 
     * Handles the HTTP <code>POST</code> method.
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
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
