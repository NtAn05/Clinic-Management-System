package controller;

import dal.LabRequestDAO;
import model.LabRequest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

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
            
            if (account == null || account.getRole() == null || !account.getRole().name().equals("technician")) {
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
                String resultFile = request.getParameter("resultFile"); // In real app, handle file upload
                
                Integer technicianId = account.getId();
                
                boolean success = labRequestDAO.sendLabResult(requestId, technicianId, resultFile, notes);
                
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

