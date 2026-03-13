package controller.medicalRecord;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

import java.io.IOException;
import java.io.PrintWriter;
import dal.PatientPortalDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import model.MedicalRecord;
import model.Patient;
import model.User;


/**
 *
 * @author anngu
 */
public class PatientPrescriptionServlet extends HttpServlet {

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
            out.println("<title>Servlet PatientPrescriptionServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet PatientPrescriptionServlet at " + request.getContextPath() + "</h1>");
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
        if (account == null || account.getRole() == null || !"patient".equalsIgnoreCase(account.getRole().name())) {
            response.sendRedirect(request.getContextPath() + "/pages/auth/login.jsp");
            return;
        }

        PatientPortalDAO patientPortalDAO = new PatientPortalDAO();
        List<Patient> patients = patientPortalDAO.getPatientsByUserId(account.getUserId());

        Long selectedPatientId = parsePatientId(request.getParameter("patientId"));
        if (selectedPatientId != null && !isPatientOwnedByAccount(patients, selectedPatientId)) {
            selectedPatientId = null;
        }

        List<MedicalRecord> prescriptions = patientPortalDAO.getPrescriptionsByUserId(account.getUserId(), selectedPatientId);
        request.setAttribute("patients", patients);
        request.setAttribute("selectedPatientId", selectedPatientId);
        request.setAttribute("prescriptions", prescriptions);
        request.getRequestDispatcher("/pages/profile/prescriptions.jsp").forward(request, response);
    }
    
    private Long parsePatientId(String rawPatientId) {
        if (rawPatientId == null || rawPatientId.isBlank()) {
            return null;
        }

        try {
            long patientId = Long.parseLong(rawPatientId);
            return patientId > 0 ? patientId : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private boolean isPatientOwnedByAccount(List<Patient> patients, long selectedPatientId) {
        for (Patient patient : patients) {
            if (patient.getPatientId() == selectedPatientId) {
                return true;
            }
        }
        return false;
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
