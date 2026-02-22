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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import model.Appointment;
import model.Doctor;
import model.Patient;
import static org.apache.coyote.http11.Constants.a;

/**
 *
 * @author Admin
 */
public class AppointmentServlet extends HttpServlet {

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
            out.println("<title>Servlet AppointmentServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AppointmentServlet at " + request.getContextPath() + "</h1>");
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
        String doctorID = request.getParameter("btnDoctorID");

        AppointmentDAO dao = new AppointmentDAO();
        Doctor doctor;
        doctor = dao.getDoctorById(doctorID);
        request.setAttribute("doctor", doctor);

        request.getRequestDispatcher("/pages/appointments/appointment/appointmentFirst.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//nhận thông tin
        String userID = request.getParameter("userID");
        int useId = Integer.parseInt(userID);
        
        String doctorID = request.getParameter("doctorID");
        long doctorId = Long.parseLong(doctorID);
        
        String name = request.getParameter("name");
        String sdt = request.getParameter("sdt");
        String email = request.getParameter("email");
        
        String dateofbirth = request.getParameter("dateofbirth");
        LocalDate localDate = LocalDate.parse(dateofbirth);
        java.sql.Date birthDate = java.sql.Date.valueOf(localDate);
        
        String gender = request.getParameter("gender");
        String address = request.getParameter("address");
        String note = request.getParameter("note");
        String date = request.getParameter("date");
        String time = request.getParameter("time");
        String submit = request.getParameter("btnSubmit");
        
// tạo đối tượng 
        Patient patient = new Patient(doctorId, useId, name, sdt, birthDate, address, email, gender);
        Appointment appointment = new Appointment(0, time, date, note);
        AppointmentDAO dao = new AppointmentDAO();
        String errorPhone = "";
        String errorEmail = "";
        String status = "";
// check thông tin 
        if (!checkPhone(sdt)) {
            errorPhone = "Phone must form 0xxx xxx xxx";
        } else if (!checkEmail(email)) {
            errorEmail = "abc@xxx.com";
        } else{
            status= "yes";
        }
        request.setAttribute("errorPhone", errorPhone);
        request.setAttribute("errorEmail", errorEmail);
        request.setAttribute("appointment", appointment);
        request.setAttribute("patient", patient);   
// tạo đơn appointment
            if(submit !=null && submit.equalsIgnoreCase("step2")){
            Appointment ap = dao.addAppointment(appointment);
            Patient p = dao.addPatient(patient);
            status = "set";
            }
// Doctor
        Doctor doctor;
        doctor = dao.getDoctorById(doctorID);
        request.setAttribute("doctor", doctor);
// chuyển thông tin sang để xác nhận
        if (status.equals("yes")) {          
            request.getRequestDispatcher("/pages/appointments/appointment/appointmentSecond.jsp")      
                    .forward(request, response);
         }else if (status.equals("set")) {          
            request.getRequestDispatcher("/pages/appointments/appointment/appointmentPayment.jsp")      
                    .forward(request, response);
         }
          else {
            request.getRequestDispatcher("/pages/appointments/appointment/appointmentFirst.jsp")
                    .forward(request, response);
        }

    }

   
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private boolean checkPhone(String sdt) {
        if (sdt == null) {
            return false;
        }
        return sdt.matches("^0\\d{9}$");
    }

    private boolean checkEmail(String email) {
        if (email == null) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

}
