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
        int[] stats = labRequestDAO.getLabRequestStatistics();
        
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
        request.getRequestDispatcher("/lab-queue.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("updateStatus".equals(action)) {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            String newStatus = request.getParameter("status");
            
            boolean success = labRequestDAO.updateLabRequestStatus(requestId, newStatus);
            
            if (success) {
                response.getWriter().write("{\"success\": true}");
            } else {
                response.getWriter().write("{\"success\": false, \"message\": \"Cập nhật thất bại\"}");
            }
            response.setContentType("application/json");
            
        } else if ("updateNotes".equals(action)) {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            String notes = request.getParameter("notes");
            
            boolean success = labRequestDAO.updateLabRequestNotes(requestId, notes);
            
            if (success) {
                response.getWriter().write("{\"success\": true}");
            } else {
                response.getWriter().write("{\"success\": false, \"message\": \"Lưu ghi chú thất bại\"}");
            }
            response.setContentType("application/json");
            
        } else {
            // Default: redirect to GET
            doGet(request, response);
        }
    }
}

