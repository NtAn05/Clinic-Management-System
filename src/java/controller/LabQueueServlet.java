package controller;

import dal.LabRequestDAO;
import model.LabRequest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 5,        // 5MB
    maxRequestSize = 1024 * 1024 * 10     // 10MB
)
public class LabQueueServlet extends HttpServlet {

    private LabRequestDAO labRequestDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        labRequestDAO = new LabRequestDAO();
    }

    private static final int PAGE_SIZE = 8;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if viewing send result page
        String action = request.getParameter("action");
        if ("viewSendResult".equals(action)) {
            String requestIdParam = request.getParameter("requestId");
            if (requestIdParam != null) {
                try {
                    int requestId = Integer.parseInt(requestIdParam);
                    LabRequest labRequest = labRequestDAO.getLabRequestById(requestId);
                    if (labRequest != null) {
                        request.setAttribute("labRequest", labRequest);
                    }
                } catch (NumberFormatException e) {
                    // Invalid ID
                }
            }
            request.getRequestDispatcher("/pages/lab/send-result.jsp").forward(request, response);
            return;
        }
        
        // Get filter parameters
        String status = request.getParameter("status");
        String department = request.getParameter("department");
        String search = request.getParameter("search");
        
        // Get page parameter
        int currentPage = 1;
        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            }
        } catch (NumberFormatException e) {
            currentPage = 1;
        }
        
        // Count total records for pagination
        int totalRecords = labRequestDAO.countLabRequestsWithFilter(status, department, null, search);
        int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }
        
        // Get lab requests with filters and pagination
        List<LabRequest> labRequests = labRequestDAO.getLabRequestsWithFilterAndPagination(
            status, department, null, search, currentPage, PAGE_SIZE
        );
        
        // Get statistics
        int[] stats = labRequestDAO.getLabRequestStatisticsWithFilter(status, department, search);
        
        // Get all specializations for filter dropdown
        List<String> specializations = labRequestDAO.getAllSpecializations();
        
        // Set attributes for JSP
        request.setAttribute("labRequests", labRequests);
        request.setAttribute("stats", stats);
        request.setAttribute("specializations", specializations);
        request.setAttribute("filterStatus", status != null ? status : "");
        request.setAttribute("filterDepartment", department != null ? department : "");
        request.setAttribute("searchTerm", search != null ? search : "");
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("pageSize", PAGE_SIZE);
        
        // Forward to JSP
        request.getRequestDispatcher("/pages/lab/lab-queue.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        if ("updateStatus".equals(action)) {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            String newStatus = request.getParameter("status");
            
            boolean success = labRequestDAO.updateLabRequestStatus(requestId, newStatus);
            
            if (success) {
                response.getWriter().write("{\"success\": true}");
            } else {
                response.getWriter().write("{\"success\": false, \"message\": \"Cập nhật thất bại\"}");
            }
            
        } else if ("updateNotes".equals(action)) {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            String notes = request.getParameter("notes");
            
            boolean success = labRequestDAO.updateLabRequestNotes(requestId, notes);
            
            if (success) {
                response.getWriter().write("{\"success\": true}");
            } else {
                response.getWriter().write("{\"success\": false, \"message\": \"Lưu ghi chú thất bại\"}");
            }
            
        } else if ("sendResult".equals(action)) {
            // Get technician ID from session
            jakarta.servlet.http.HttpSession session = request.getSession();
            model.User account = (model.User) session.getAttribute("account");
            
            if (account == null || !RoleHelper.isTechnician(account)) {
                response.getWriter().write("{\"success\": false, \"message\": \"Không có quyền thực hiện\"}");
                return;
            }
            
            try {
                // Parse request ID from request code (LAB-2026-0001) or direct ID
                String requestIdParam = request.getParameter("requestId");
                int requestId;
                
                if (requestIdParam.contains("LAB-")) {
                    // Extract ID from code format LAB-YYYY-XXXX
                    String[] parts = requestIdParam.split("-");
                    if (parts.length >= 3) {
                        requestId = Integer.parseInt(parts[2]);
                    } else {
                        response.getWriter().write("{\"success\": false, \"message\": \"Mã phiếu không hợp lệ\"}");
                        return;
                    }
                } else {
                    requestId = Integer.parseInt(requestIdParam);
                }
                
                String notes = request.getParameter("notes");
                
                // Xử lý upload file kết quả xét nghiệm
                String resultFilePath = null;
                Part filePart = request.getPart("resultFile");
                if (filePart != null && filePart.getSize() > 0) {
                    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                    String uniqueFileName = "LAB_" + requestId + "_" + UUID.randomUUID().toString().substring(0, 8) + fileExtension;
                    
                    // Lưu file vào thư mục uploads/lab-results (tạo thư mục nếu chưa có)
                    String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads" + File.separator + "lab-results";
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }
                    
                    String fullPath = uploadPath + File.separator + uniqueFileName;
                    Files.copy(filePart.getInputStream(), Paths.get(fullPath), StandardCopyOption.REPLACE_EXISTING);
                    
                    // Lưu path tương đối vào database (để có thể truy cập qua URL)
                    resultFilePath = "uploads/lab-results/" + uniqueFileName;
                }
                
                Integer technicianId = account.getUserId();
                
                boolean success = labRequestDAO.sendLabResult(requestId, technicianId, resultFilePath, notes);
                
                if (success) {
                    response.getWriter().write("{\"success\": true, \"message\": \"Gửi kết quả thành công\"}");
                } else {
                    response.getWriter().write("{\"success\": false, \"message\": \"Gửi kết quả thất bại\"}");
                }
            } catch (NumberFormatException e) {
                response.getWriter().write("{\"success\": false, \"message\": \"Mã phiếu không hợp lệ\"}");
            } catch (Exception e) {
                e.printStackTrace();
                response.getWriter().write("{\"success\": false, \"message\": \"Lỗi hệ thống: " + e.getMessage() + "\"}");
            }
            
        } else {
            // Default: redirect to GET
            doGet(request, response);
        }
    }
}

