/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.doctor;

import dal.DoctorDAO;
import dal.LabRequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.text.Normalizer;
import java.util.List;
import model.Doctor;
import model.ExaminationHistoryItem;
import model.ExamLabItem;
import model.DoctorQueueItem;
import model.User;
import model.MedicalRecord;

/**
 *
 * @author anngu
 */
public class DoctorExamServlet extends HttpServlet {

    private static final String SECTION_HISTORY = "TIỀN SỬ";
    private static final String SECTION_CLINICAL_RESULT = "KẾT QUẢ KHÁM LÂM SÀNG";
    private static final String SECTION_DOCTOR_NOTE = "GHI CHÚ BÁC SĨ";
    private static final String SECTION_TREATMENT_PLAN = "PHƯƠNG ÁN ĐIỀU TRỊ";
    private static final String SECTION_LAB_REQUEST = "YÊU CẦU XÉT NGHIỆM";

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
                request.getRequestDispatcher("/pages/examination/exam.jsp").forward(request, response);
                return;
            }

            long appointmentId;
            try {
                appointmentId = Long.parseLong(appointmentParam.trim());
            } catch (NumberFormatException ex) {
                request.setAttribute("pageError", "Mã lịch khám không hợp lệ.");
                request.getRequestDispatcher("/pages/examination/exam.jsp").forward(request, response);
                return;
            }

            DoctorDAO doctorDAO = new DoctorDAO();
            DoctorQueueItem examData = doctorDAO.getQueueItemByAppointment(doctor.getDoctorId(), appointmentId);
            if (examData == null) {
                request.setAttribute("pageError", "Không tìm thấy bệnh nhân trong hàng đợi khám của bác sĩ.");
                request.getRequestDispatcher("/pages/examination/exam.jsp").forward(request, response);
                return;
            }

            if ("waiting".equalsIgnoreCase(examData.getStatus())) {
                doctorDAO.startExamination(appointmentId);
                examData.setStatus("examining");
            }

            request.setAttribute("examData", examData);
            List<ExamLabItem> labResults = doctorDAO.getLabResultsByAppointment(appointmentId);
            request.setAttribute("labResults", labResults);
            MedicalRecord medicalRecord = doctorDAO.getMedicalRecordByAppointment(appointmentId);
            request.setAttribute("medicalRecord", medicalRecord);

            String notes = medicalRecord != null ? medicalRecord.getNotes() : null;
            request.setAttribute("historyAllergies", extractHistoryLine(notes, "Dị ứng"));
            request.setAttribute("historyChronic", extractHistoryLine(notes, "Bệnh mạn tính"));
            request.setAttribute("historyFamily", extractHistoryLine(notes, "Tiền sử gia đình"));
            request.setAttribute("historySocial", extractHistoryLine(notes, "Tiền sử xã hội"));
            request.setAttribute("historyVaccination", extractHistoryLine(notes, "Lịch sử tiêm chủng"));
            request.setAttribute("clinicalResult", extractSection(notes, SECTION_CLINICAL_RESULT));
            request.setAttribute("doctorNote", extractSection(notes, SECTION_DOCTOR_NOTE));
            request.setAttribute("treatmentPlan", extractSection(notes, SECTION_TREATMENT_PLAN));
            request.setAttribute("labRequestInstruction", extractSection(notes, SECTION_LAB_REQUEST));

            List<ExaminationHistoryItem> examinationHistory
                    = doctorDAO.getExaminationHistoryByAppointment(appointmentId);
            request.setAttribute("examData", examData);
            request.setAttribute("historyList", examinationHistory);
            String activeTab = cleanText(request.getParameter("tab"));
            if (activeTab.isEmpty()) {
                activeTab = "info";
            }
            request.setAttribute("activeTab", activeTab);
            request.setAttribute("success", request.getParameter("success"));
            request.setAttribute("error", request.getParameter("error"));
            request.getRequestDispatcher("/pages/examination/exam.jsp").forward(request, response);
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("pageError", "Đã xảy ra lỗi khi tải màn hình khám bệnh. Vui lòng thử lại.");
            request.getRequestDispatcher("/pages/examination/exam.jsp").forward(request, response);
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

        String symptoms = cleanText(request.getParameter("symptoms"));
        String diagnosis = cleanText(request.getParameter("diagnosis"));

        String allergies = cleanText(request.getParameter("historyAllergies"));
        String chronic = cleanText(request.getParameter("historyChronic"));
        String family = cleanText(request.getParameter("historyFamily"));
        String social = cleanText(request.getParameter("historySocial"));
        String vaccination = cleanText(request.getParameter("historyVaccination"));

        String clinicalResult = cleanText(request.getParameter("clinicalResult"));
        String doctorNote = cleanText(request.getParameter("doctorNote"));
        String treatmentPlan = cleanText(request.getParameter("treatmentPlan"));
        String labTestType = cleanText(request.getParameter("labTestType"));
        String labPriority = cleanText(request.getParameter("labPriority"));
        String labCollectionMethod = cleanText(request.getParameter("labCollectionMethod"));
        String labRequestNote = cleanText(request.getParameter("labRequestNote"));

        String labRequestInstruction = cleanText(request.getParameter("labRequestInstruction"));
        if ("createLabRequest".equalsIgnoreCase(action)) {
            StringBuilder labInstructionBuilder = new StringBuilder();
            if (!labTestType.isEmpty()) {
                labInstructionBuilder.append("- Loại xét nghiệm: ").append(labTestType).append("\n");
            }
            if (!labPriority.isEmpty()) {
                labInstructionBuilder.append("- Mức độ ưu tiên: ").append(labPriority).append("\n");
            }
            if (!labCollectionMethod.isEmpty()) {
                labInstructionBuilder.append("- Hình thức lấy mẫu: ").append(labCollectionMethod).append("\n");
            }
            if (!labRequestNote.isEmpty()) {
                labInstructionBuilder.append("- Ghi chú chỉ định: ").append(labRequestNote);
            }
            if (labInstructionBuilder.length() > 0) {
                labRequestInstruction = labInstructionBuilder.toString().trim();
            }
        }
        String requiredFieldError = validateRequiredFields(action, diagnosis, clinicalResult, treatmentPlan,
                labTestType, labPriority, labCollectionMethod);
        if (!requiredFieldError.isEmpty()) {
            String errorTab = "createLabRequest".equalsIgnoreCase(action) ? "lab" : "info";
            response.sendRedirect(request.getContextPath() + "/doctor/exam?appointmentId=" + appointmentId + "&tab=" + errorTab + "&error=" + requiredFieldError);
            return;
        }
        
        String notes = buildMedicalRecordNote(allergies, chronic, family, social, vaccination, clinicalResult, doctorNote, treatmentPlan, labRequestInstruction);

        if ("finish".equalsIgnoreCase(action)) {
            boolean finished = doctorDAO.saveMedicalRecordAndFinishExamination(appointmentId, symptoms, diagnosis, notes);
            if (!finished) {
                response.sendRedirect(request.getContextPath() + "/doctor/exam?appointmentId=" + appointmentId + "&error=saveFailed");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/doctorDashboard?success=examFinished");
            return;
        }

        if ("createLabRequest".equalsIgnoreCase(action)) {
            if ("done".equalsIgnoreCase(examData.getStatus())) {
                response.sendRedirect(request.getContextPath() + "/doctor/exam?appointmentId=" + appointmentId + "&tab=lab&error=labRequestNotAllowed");
                return;
            }

            LabRequestDAO labRequestDAO = new LabRequestDAO();
            int requestId = doctorDAO.saveMedicalRecordAndCreateLabRequest(appointmentId, doctor.getDoctorId(), symptoms, diagnosis, notes);
            if (requestId > 0) {
                response.sendRedirect(request.getContextPath() + "/doctor/exam?appointmentId=" + appointmentId + "&tab=lab&success=labRequested");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/doctor/exam?appointmentId=" + appointmentId + "&tab=lab&error=labRequestFailed");
            return;
        }
        
        boolean saved = doctorDAO.upsertMedicalRecord(appointmentId, symptoms, diagnosis, notes);
        if (!saved) {
            response.sendRedirect(request.getContextPath() + "/doctor/exam?appointmentId=" + appointmentId + "&error=saveFailed");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/doctor/exam?appointmentId=" + appointmentId + "&success=saved");
    }

    private String cleanText(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }

    private String validateRequiredFields(String action,
            String diagnosis,
            String clinicalResult,
            String treatmentPlan,
            String labTestType,
            String labPriority,
            String labCollectionMethod) {
        if ("finish".equalsIgnoreCase(action)) {
            if (diagnosis.isEmpty() || clinicalResult.isEmpty() || treatmentPlan.isEmpty()) {
                return "missingRequiredFinishFields";
            }
        }

        if ("createLabRequest".equalsIgnoreCase(action)) {
            if (diagnosis.isEmpty() || clinicalResult.isEmpty()
                    || labTestType.isEmpty() || labPriority.isEmpty() || labCollectionMethod.isEmpty()) {
                return "missingRequiredLabFields";
            }
        }

        return "";
    }
    
    private String buildMedicalRecordNote(
            String allergies,
            String chronic,
            String family,
            String social,
            String vaccination,
            String clinicalResult,
            String doctorNote,
            String treatmentPlan,
            String labRequestInstruction
    ) {
        StringBuilder sb = new StringBuilder();

        if (!allergies.isEmpty() || !chronic.isEmpty() || !family.isEmpty() || !social.isEmpty() || !vaccination.isEmpty()) {
            sb.append("[").append(SECTION_HISTORY).append("]\n");
            if (!allergies.isEmpty()) {
                sb.append("- Dị ứng: ").append(allergies).append("\n");
            }
            if (!chronic.isEmpty()) {
                sb.append("- Bệnh mãn tính: ").append(chronic).append("\n");
            }
            if (!family.isEmpty()) {
                sb.append("- Tiền sử gia đình: ").append(family).append("\n");
            }
            if (!social.isEmpty()) {
                sb.append("- Tiền sử xã hội: ").append(social).append("\n");
            }
            if (!vaccination.isEmpty()) {
                sb.append("- Lịch sử tiêm chủng: ").append(vaccination).append("\n");
            }
            sb.append("\n");
        }

        appendSection(sb, SECTION_CLINICAL_RESULT, clinicalResult);
        appendSection(sb, SECTION_DOCTOR_NOTE, doctorNote);
        appendSection(sb, SECTION_TREATMENT_PLAN, treatmentPlan);
        appendSection(sb, SECTION_LAB_REQUEST, labRequestInstruction);

        return sb.toString().trim();
    }

    private void appendSection(StringBuilder sb, String title, String value) {
        if (value.isEmpty()) {
            return;
        }

        sb.append("[").append(title).append("]\n");
        sb.append(value).append("\n\n");
    }

    private String extractSection(String notes, String sectionTitle) {
        if (notes == null || notes.isBlank()) {
            return "";
        }

        String marker = "[" + sectionTitle + "]";
        int start = notes.indexOf(marker);
        if (start < 0) {
            return "";
        }

        int contentStart = start + marker.length();
        while (contentStart < notes.length() && (notes.charAt(contentStart) == '\n' || notes.charAt(contentStart) == '\r')) {
            contentStart++;
        }

        int end = notes.length();
        int nextMarker = notes.indexOf("[", contentStart);
        while (nextMarker >= 0) {
            int close = notes.indexOf("]", nextMarker);
            if (close > nextMarker) {
                end = nextMarker;
                break;
            }
            nextMarker = notes.indexOf("[", nextMarker + 1);
        }

        return notes.substring(contentStart, end).trim();
    }

    private String extractHistoryLine(String notes, String label) {
        if (notes == null || notes.isBlank()) {
            return "";
        }

        String targetLabel = normalizeHistoryLabel(label);
        String historySection = extractSection(notes, SECTION_HISTORY);
        String value = extractHistoryLineFromBlock(historySection, targetLabel);
        if (!value.isEmpty()) {
            return value;
        }

        value = extractHistoryLineFromBlock(notes, targetLabel);
        if (!value.isEmpty()) {
            return value;
        }

        if (!notes.contains("[") && !notes.contains(":") && !notes.contains("=")) {
            return notes.trim();
        }

        return "";
    }

    private String extractHistoryLineFromBlock(String block, String targetLabel) {
        if (block == null || block.isBlank()) {
            return "";
        }

        String[] lines = block.split("\\R");
        for (String line : lines) {
            String normalized = line.trim();
            if (normalized.isEmpty()) {
                continue;
            }

            while (normalized.startsWith("-") || normalized.startsWith("•") || normalized.startsWith("*")) {
                normalized = normalized.substring(1).trim();
            }

            int separator = normalized.indexOf(':');
            if (separator < 0) {
                separator = normalized.indexOf('=');
            }
            if (separator < 0) {
                separator = normalized.indexOf('：');
            }
            if (separator < 0) {
                continue;
            }

            String currentLabel = normalized.substring(0, separator).trim();
            if (normalizeHistoryLabel(currentLabel).equals(targetLabel)) {
                return normalized.substring(separator + 1).trim();
            }
        }
        return "";
    }

    private String normalizeHistoryLabel(String label) {
        if (label == null) {
            return "";
        }

        String withoutAccent = Normalizer.normalize(label, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return withoutAccent.toLowerCase().trim();
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
