package controller;

import dal.DoctorDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import model.Role;
import model.ScheduleChangeRequest;
import model.User;
import util.SystemLogService;

public class AdminScheduleRequestServlet extends HttpServlet {

    private static final String VIEW_PATH = "/pages/admin/schedule-requests.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req, resp)) {
            return;
        }
        loadPage(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req, resp)) {
            return;
        }

        String action = trimOrEmpty(req.getParameter("action"));
        String statusFilter = normalizeStatusFilter(req.getParameter("status"));
        String requestTypeFilter = normalizeRequestTypeFilter(req.getParameter("requestType"));
        String actionTypeFilter = normalizeActionTypeFilter(req.getParameter("actionType"));
        String keyword = trimOrEmpty(req.getParameter("keyword"));

        if ("review".equalsIgnoreCase(action)) {
            handleReview(req);
        }

        resp.sendRedirect(req.getContextPath() + "/admin-schedule-requests"
                + buildFilterQuery(statusFilter, requestTypeFilter, actionTypeFilter, keyword));
    }

    private void loadPage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DoctorDAO doctorDAO = new DoctorDAO();
        String statusFilter = normalizeStatusFilter(req.getParameter("status"));
        String requestTypeFilter = normalizeRequestTypeFilter(req.getParameter("requestType"));
        String actionTypeFilter = normalizeActionTypeFilter(req.getParameter("actionType"));
        String keyword = trimOrEmpty(req.getParameter("keyword"));

        List<ScheduleChangeRequest> requests = doctorDAO.getScheduleChangeRequestsForAdmin(
                statusFilter, requestTypeFilter, actionTypeFilter, keyword
        );
        int pendingCount = doctorDAO.countPendingScheduleChangeRequests();

        req.setAttribute("statusFilter", statusFilter);
        req.setAttribute("requestTypeFilter", requestTypeFilter);
        req.setAttribute("actionTypeFilter", actionTypeFilter);
        req.setAttribute("keyword", keyword);
        req.setAttribute("requests", requests);
        req.setAttribute("pendingCount", pendingCount);
        req.getRequestDispatcher(VIEW_PATH).forward(req, resp);
    }

    private void handleReview(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        DoctorDAO doctorDAO = new DoctorDAO();

        int requestId = parseInt(req.getParameter("requestId"), -1);
        String decision = normalizeDecision(req.getParameter("decision"));
        String adminNote = req.getParameter("adminNote");

        if (requestId <= 0 || decision.isEmpty()) {
            session.setAttribute("scheduleReviewError", "Dữ liệu duyệt đơn chưa hợp lệ.");
            return;
        }

        boolean reviewed = doctorDAO.reviewScheduleChangeRequest(requestId, decision, adminNote);
        if (!reviewed) {
            session.setAttribute("scheduleReviewError", "Không thể xử lý đơn. Đơn có thể đã được duyệt trước đó.");
            return;
        }

        User admin = (User) session.getAttribute("account");
        String actionName = "APPROVED".equals(decision) ? "ADMIN_APPROVE_SCHEDULE_REQUEST" : "ADMIN_REJECT_SCHEDULE_REQUEST";
        String details = "Admin " + admin.getFullName() + " đã " + ("APPROVED".equals(decision) ? "duyệt" : "từ chối")
                + " đơn đổi lịch #" + requestId + ".";
        SystemLogService.logWithSession(session, actionName, details);

        String successMessage = "APPROVED".equals(decision)
                ? "Đã duyệt đơn thành công."
                : "Đã từ chối đơn thành công.";
        session.setAttribute("scheduleReviewSuccess", successMessage);
    }

    private boolean isAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("account") == null) {
            resp.sendRedirect(req.getContextPath() + "/pages/auth/login.jsp");
            return false;
        }

        User user = (User) session.getAttribute("account");
        if (user.getRole() != Role.admin) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Chỉ admin mới được truy cập");
            return false;
        }
        return true;
    }

    private int parseInt(String raw, int fallback) {
        try {
            if (raw == null || raw.isBlank()) {
                return fallback;
            }
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private String normalizeDecision(String value) {
        String normalized = trimOrEmpty(value).toUpperCase();
        if ("APPROVED".equals(normalized) || "REJECTED".equals(normalized)) {
            return normalized;
        }
        return "";
    }

    private String normalizeStatusFilter(String value) {
        String normalized = trimOrEmpty(value).toUpperCase();
        if ("PENDING".equals(normalized) || "APPROVED".equals(normalized) || "REJECTED".equals(normalized)) {
            return normalized;
        }
        return "ALL";
    }

    private String normalizeRequestTypeFilter(String value) {
        String normalized = trimOrEmpty(value).toUpperCase();
        if ("TEMPORARY".equals(normalized) || "PERMANENT".equals(normalized)) {
            return normalized;
        }
        return "ALL";
    }

    private String normalizeActionTypeFilter(String value) {
        String normalized = trimOrEmpty(value).toUpperCase();
        if ("ADD".equals(normalized) || "UPDATE".equals(normalized) || "REMOVE".equals(normalized)) {
            return normalized;
        }
        return "ALL";
    }

    private String buildFilterQuery(String statusFilter, String requestTypeFilter, String actionTypeFilter, String keyword) {
        StringBuilder query = new StringBuilder("?status=").append(encode(statusFilter));
        query.append("&requestType=").append(encode(requestTypeFilter));
        query.append("&actionType=").append(encode(actionTypeFilter));
        query.append("&keyword=").append(encode(keyword));
        return query.toString();
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private String trimOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
