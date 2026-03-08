package controller;

import dal.ServiceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import model.Role;
import model.ServicePrice;
import model.User;

public class AdminServiceServlet extends HttpServlet {

    private static final int PAGE_SIZE = 10;
    private final ServiceDAO serviceDAO = new ServiceDAO();

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("account") == null) {
            resp.sendRedirect(req.getContextPath() + "/pages/auth/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("account");
        if (user.getRole() != Role.admin) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Chỉ admin mới được truy cập");
            return;
        }

        String action = req.getParameter("action");

        try {
            if ("add".equals(action)) {
                handleAdd(req);
                req.setAttribute("success", "Thêm dịch vụ thành công");
            } else if ("update".equals(action)) {
                handleUpdate(req);
                req.setAttribute("success", "Cập nhật dịch vụ thành công");
            } else if ("delete".equals(action)) {
                handleDelete(req);
                req.setAttribute("success", "Xóa dịch vụ thành công");
            }

            loadPage(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", "Lỗi: " + e.getMessage());
            loadPage(req, resp);
        }
    }

    private void loadPage(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String search = trim(firstNonBlank(req.getParameter("filterSearch"), req.getParameter("search")));
        String category = trim(firstNonBlank(req.getParameter("filterCategory"), req.getParameter("category")));
        String minPriceStr = trim(firstNonBlank(req.getParameter("filterMinPrice"), req.getParameter("minPrice")));
        String maxPriceStr = trim(firstNonBlank(req.getParameter("filterMaxPrice"), req.getParameter("maxPrice")));
        int page = parsePage(firstNonBlank(req.getParameter("filterPage"), req.getParameter("page")), 1);

        List<ServicePrice> services = serviceDAO.getAllServices();

        if (!search.isEmpty()) {
            String kw = search.toLowerCase();
            services = services.stream()
                    .filter(s -> s.getName() != null && s.getName().toLowerCase().contains(kw))
                    .collect(Collectors.toList());
        }

        if (!category.isEmpty() && !"all".equals(category)) {
            services = services.stream()
                    .filter(s -> category.equals(s.getServiceType()))
                    .collect(Collectors.toList());
        }

        if (!minPriceStr.isEmpty()) {
            try {
                BigDecimal min = new BigDecimal(minPriceStr);
                services = services.stream()
                        .filter(s -> s.getPrice() != null && s.getPrice().compareTo(min) >= 0)
                        .collect(Collectors.toList());
            } catch (Exception ignored) {
            }
        }

        if (!maxPriceStr.isEmpty()) {
            try {
                BigDecimal max = new BigDecimal(maxPriceStr);
                services = services.stream()
                        .filter(s -> s.getPrice() != null && s.getPrice().compareTo(max) <= 0)
                        .collect(Collectors.toList());
            } catch (Exception ignored) {
            }
        }

        req.setAttribute("services", services); // full list after filter/search
        applyPaging(req, services, page);       // paged list for table render

        req.setAttribute("searchKeyword", search);
        req.setAttribute("filterCategory", category.isEmpty() ? "all" : category);
        req.setAttribute("minPriceValue", minPriceStr);
        req.setAttribute("maxPriceValue", maxPriceStr);

        req.getRequestDispatcher("/pages/admin/services.jsp").forward(req, resp);
    }

    private void handleAdd(HttpServletRequest req) {
        ServicePrice s = new ServicePrice();
        s.setName(req.getParameter("name"));
        s.setServiceType(req.getParameter("serviceType"));
        s.setPrice(new BigDecimal(req.getParameter("price")));
        serviceDAO.addService(s);
    }

    private void handleUpdate(HttpServletRequest req) {
        ServicePrice s = new ServicePrice();
        s.setServiceId(Integer.parseInt(req.getParameter("serviceId")));
        s.setName(req.getParameter("name"));
        s.setServiceType(req.getParameter("serviceType"));
        s.setPrice(new BigDecimal(req.getParameter("price")));
        serviceDAO.updateService(s);
    }

    private void handleDelete(HttpServletRequest req) {
        int id = Integer.parseInt(req.getParameter("serviceId"));
        serviceDAO.deleteService(id);
    }

    private void applyPaging(HttpServletRequest req, List<ServicePrice> fullList, int page) {
        List<ServicePrice> safe = fullList != null ? fullList : new ArrayList<>();

        int totalRecords = safe.size();
        int totalPages = calculateTotalPages(totalRecords, PAGE_SIZE);

        int currentPage = page;
        if (totalPages > 0 && currentPage > totalPages) {
            currentPage = totalPages;
        }
        if (totalPages == 0) {
            currentPage = 1;
        }

        req.setAttribute("servicesPaged", paginate(safe, currentPage, PAGE_SIZE));
        req.setAttribute("currentPage", currentPage);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalRecords", totalRecords);
        req.setAttribute("pageSize", PAGE_SIZE);
    }

    private int parsePage(String pageParam, int defaultValue) {
        try {
            int p = Integer.parseInt(pageParam);
            return p < 1 ? 1 : p;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private int calculateTotalPages(int totalRecords, int pageSize) {
        if (totalRecords <= 0 || pageSize <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalRecords / pageSize);
    }

    private <T> List<T> paginate(List<T> data, int page, int pageSize) {
        if (data == null || data.isEmpty()) {
            return new ArrayList<>();
        }
        int from = (page - 1) * pageSize;
        if (from < 0 || from >= data.size()) {
            return new ArrayList<>();
        }
        int to = Math.min(from + pageSize, data.size());
        return data.subList(from, to);
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) {
            return a;
        }
        return b;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    public String getServletInfo() {
        return "Admin Service Management Servlet";
    }
}
