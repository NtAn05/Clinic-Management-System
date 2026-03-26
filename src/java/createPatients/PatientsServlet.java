/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package createPatients;

import dal.PatientPortalDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import model.Patient;
import model.User;

/**
 *
 * @author Admin
 */
public class PatientsServlet extends HttpServlet {

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
            out.println("<title>Servlet CreatePatientsServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CreatePatientsServlet at " + request.getContextPath() + "</h1>");
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

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/pages/auth/login.jsp");
            return;
        }
        User account = (User) session.getAttribute("account");
        if (account == null) {
            response.sendRedirect(request.getContextPath() + "/pages/auth/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        String patientID = request.getParameter("id");
        String DoctorID = request.getParameter("btnDoctorID");
        String DoctorId = request.getParameter("DoctorID");

        PatientPortalDAO dao = new PatientPortalDAO();

        if ("edit".equals(action) && patientID != null) {

            int patientId = Integer.parseInt(patientID);

            Patient p = dao.getPatientsByPatientID(patientId);

            request.setAttribute("patient", p);
            request.setAttribute("DoctorID", DoctorId);

            request.getRequestDispatcher("/pages/profile/createPatients/createPatients.jsp")
                    .forward(request, response);
            return;
        }
        request.setAttribute("DoctorID", DoctorId);

        if (DoctorID != null) {
            request.setAttribute("DoctorID", DoctorID);
        }

        List<Patient> list = dao.getPatientsByUserId(account.getUserId());

        request.setAttribute("patientList", list);

        request.getRequestDispatcher("/pages/profile/createPatients/listOfPatients.jsp")
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

        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("account");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/pages/auth/login.jsp");
            return;
        }
        String userID = request.getParameter("userID");
        int useId = Integer.parseInt(userID);

        String patientID = request.getParameter("patientID");
        String DoctorID = request.getParameter("DoctorID");
        String sdt = request.getParameter("sdt");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String gender = request.getParameter("gender");
        String dateofbirth = request.getParameter("dateofbirth");

        LocalDate localDate = null;
        Date birthDate = null;

        if (dateofbirth != null && !dateofbirth.isEmpty()) {
            localDate = LocalDate.parse(dateofbirth);
            birthDate = Date.valueOf(localDate);
        }

        String submit = request.getParameter("btnSubmit");

        String errorPhone = "";
        String errorName = "";
        String errorEmail = "";
        String errorDOB = "";

        boolean valid = true;

        if (!checkPhone(sdt)) {
            errorPhone = "Phone must form 0xxx xxx xxx (10 numbers)";
            valid = false;
        }

        if (!checkEmail(email)) {
            errorEmail = "abc@xxx.com";
            valid = false;
        }

        if (!checkName(name)) {
            errorName = "Name not null";
            valid = false;
        }

        if (!checkDOB(localDate)) {
            errorDOB = "Date of birth must be between 1900 and today";
            valid = false;
        }

        PatientPortalDAO dao = new PatientPortalDAO();
        request.setAttribute("DoctorID", DoctorID);
        Patient patient = new Patient(useId, name, sdt, birthDate, email, gender);

        int newPatientId;
        if (valid) {

            if ("edit".equals(submit)) {
                dao.editPatient(patientID, patient);
            } else {
                newPatientId = dao.addPatient(patient);

                if ("receptionist".equals(user.getRole().toString())) {

                    response.sendRedirect(request.getContextPath()
                            + "/appointmentservlet?doctor=" + DoctorID
                            + "&patientid=" + newPatientId);
                    return;
                }
            }
            response.sendRedirect(request.getContextPath() + "/createpatientsservlet?DoctorID=" + DoctorID);
        } else {
            request.setAttribute("errorPhone", errorPhone);
            request.setAttribute("errorEmail", errorEmail);
            request.setAttribute("errorName", errorName);
            request.setAttribute("errorDOB", errorDOB);
            request.setAttribute("patient", patient);

            request.setAttribute("dob", request.getParameter("dateofbirth"));

            request.getRequestDispatcher("/pages/profile/createPatients/createPatients.jsp")
                    .forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private boolean checkPhone(String sdt) {
        if (sdt == null || sdt.trim().isEmpty()) {
            return true; // cho phép null
        }
        return sdt.matches("^0\\d{9}$");
    }

    private boolean checkEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true; // cho phép null
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private boolean checkName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return name.trim().length() >= 2 && name.trim().length() <= 50;
    }

    private boolean checkDOB(LocalDate dob) {
        if (dob == null) {
            return false;
        }
        LocalDate minDate = LocalDate.of(1990, 1, 1);
        LocalDate today = LocalDate.now();

        return (dob.compareTo(minDate) >= 0) && (dob.compareTo(today) <= 0);
    }

}
