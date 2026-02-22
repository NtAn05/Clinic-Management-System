package controller;

import dal.ServiceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import model.ServicePrice;
import model.User;
import model.Role;

public class AdminServiceServlet extends HttpServlet {
    
    private final ServiceDAO serviceDAO = new ServiceDAO();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        
        // Kiểm tra quyền admin
        if (session == null || session.getAttribute("account") == null) {
            resp.sendRedirect(req.getContextPath() + "/pages/auth/login.jsp");
            return;
        }
        
        User user = (User) session.getAttribute("account");
        if (user.getRole() != Role.admin) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Chỉ admin mới được truy cập");
            return;
        }
        
        // Xử lý search và filter
        String search = req.getParameter("search");
        String category = req.getParameter("category");
        String minPriceStr = req.getParameter("minPrice");
        String maxPriceStr = req.getParameter("maxPrice");
        
        List<ServicePrice> services = serviceDAO.getAllServices();
        
        // Áp dụng filter
        if (search != null && !search.trim().isEmpty()) {
            services = services.stream()
                .filter(s -> s.getName().toLowerCase().contains(search.toLowerCase().trim()))
                .collect(Collectors.toList());
        }
        
        if (category != null && !category.trim().isEmpty() && !"all".equals(category)) {
            services = services.stream()
                .filter(s -> category.equals(s.getServiceType()))
                .collect(Collectors.toList());
        }
        
        if (minPriceStr != null && !minPriceStr.trim().isEmpty()) {
            try {
                java.math.BigDecimal minPrice = new java.math.BigDecimal(minPriceStr);
                services = services.stream()
                    .filter(s -> s.getPrice().compareTo(minPrice) >= 0)
                    .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                // Ignore invalid minPrice
            }
        }
        
        if (maxPriceStr != null && !maxPriceStr.trim().isEmpty()) {
            try {
                java.math.BigDecimal maxPrice = new java.math.BigDecimal(maxPriceStr);
                services = services.stream()
                    .filter(s -> s.getPrice().compareTo(maxPrice) <= 0)
                    .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                // Ignore invalid maxPrice
            }
        }
        
        req.setAttribute("filterCategory", category != null && !category.trim().isEmpty() ? category : "all");
        req.setAttribute("services", services);
        req.getRequestDispatcher("/pages/admin/services.jsp").forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        
        // Kiểm tra quyền admin
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
                ServicePrice s = new ServicePrice();
                s.setName(req.getParameter("name"));
                s.setServiceType(req.getParameter("serviceType"));
                s.setPrice(new BigDecimal(req.getParameter("price")));
                serviceDAO.addService(s);
                req.setAttribute("success", "Thêm dịch vụ thành công");
            } else if ("update".equals(action)) {
                ServicePrice s = new ServicePrice();
                s.setServiceId(Integer.parseInt(req.getParameter("serviceId")));
                s.setName(req.getParameter("name"));
                s.setServiceType(req.getParameter("serviceType"));
                s.setPrice(new BigDecimal(req.getParameter("price")));
                serviceDAO.updateService(s);
                req.setAttribute("success", "Cập nhật dịch vụ thành công");
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("serviceId"));
                serviceDAO.deleteService(id);
                req.setAttribute("success", "Xóa dịch vụ thành công");
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        req.setAttribute("services", serviceDAO.getAllServices());
        req.getRequestDispatcher("/pages/admin/services.jsp").forward(req, resp);
    }
}
