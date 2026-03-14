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
        User account = (User) session.getAttribute("account");
        String action = request.getParameter("action");
        String patientID = request.getParameter("id");
        String DoctorID = request.getParameter("btnDoctorID");

        PatientPortalDAO dao= new PatientPortalDAO();
        
        if(action.equals("edit")){
            Patient p = dao.getPatientsByPatientID(patientID);
            request.setAttribute("p", p);
            request.getRequestDispatcher("/pages/profile/createPatients/createPatients.jsp")
                    .forward(request, response);
        }
        if( DoctorID != null){
            request.setAttribute("DoctorID", DoctorID);
            
        }
        List<Patient> list = dao.getPatientsByUserId(account.getUserId());
    request.setAttribute("patientList", list);
            request.getRequestDispatcher("/pages/profile/createPatients/createPatients.jsp")
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
        String userID = request.getParameter("userID");
        int useId = Integer.parseInt(userID);
        String name = request.getParameter("name");
        String sdt = request.getParameter("sdt");
        String email = request.getParameter("email");
        String gender = request.getParameter("gender");
        String dateofbirth = request.getParameter("dateofbirth");
        LocalDate localDate = null;
        Date birthDate = null;
        if (dateofbirth != null && !dateofbirth.isEmpty()) {
            localDate = LocalDate.parse(dateofbirth); // yyyy-MM-dd, parse thẳng được
            birthDate = Date.valueOf(localDate);
        }
        String submit = request.getParameter("btnSubmit");
        String errorPhone = "";
        String errorName = "";
        String errorEmail = "";
        String errorDOB = "";
        String status = "";
        if (!checkPhone(sdt)) {
            errorPhone = "Phone must form 0xxx xxx xxx(10 number)";
        } else if (!checkEmail(email)) {
            errorEmail = "abc@xxx.com";
        } else if (!checkName(name)) {
            errorName = "Name not null ";
        } else if (!checkDOB(localDate)) {
            errorDOB = "Date of birth must be between 1900 and today";
            
        } else {
            status = "yes";
        }
        PatientPortalDAO dao= new PatientPortalDAO();
        Patient patient =new Patient(useId, name, name, birthDate, email, gender);
        if(submit.equals("edit")){
            dao.editPatient(patient);
             request.getRequestDispatcher("/createpatientsservlet").forward(request, response);}
        
        if (status.equals("yes")) {
            dao.addPatient(patient);
            request.getRequestDispatcher("/createpatientsservlet")
                    .forward(request, response);
        } else {
            request.getRequestDispatcher("/pages/profile/createPatients/createPatients.jsp")
                    .forward(request, response);
        }
    }

   
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private boolean checkPhone(String sdt) {
        if (sdt == null) {
            return true;
        }
        return sdt.matches("^0\\d{9}$");
    }

    private boolean checkEmail(String email) {
        if (email == null) {
            return true;
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
    if (dob == null) return false;
    LocalDate minDate = LocalDate.of(1990, 1, 1);
    LocalDate today = LocalDate.now();
    
    return (dob.compareTo(minDate) >= 0) && (dob.compareTo(today) <= 0);
}

}
