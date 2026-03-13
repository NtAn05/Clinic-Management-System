package controller.medicalRecord;

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

public class PatientRecordsServlet extends HttpServlet {

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

        List<MedicalRecord> records = patientPortalDAO.getMedicalRecordsByUserId(account.getUserId(), selectedPatientId);
        request.setAttribute("patients", patients);
        request.setAttribute("selectedPatientId", selectedPatientId);
        request.setAttribute("records", records);

        request.getRequestDispatcher("/pages/profile/medicalRecords.jsp").forward(request, response);
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
}
