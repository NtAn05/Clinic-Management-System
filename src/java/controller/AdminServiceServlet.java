package controller;

import dal.ServiceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
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
        
        req.setAttribute("services", serviceDAO.getAllServices());
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
