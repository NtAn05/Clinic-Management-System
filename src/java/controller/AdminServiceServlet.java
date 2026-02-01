/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dal.ServiceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import model.ServicePrice;

/**
 *
 * @author admin
 */
@WebServlet("/admin/services")
public class AdminServiceServlet extends HttpServlet {
    
    private final ServiceDAO serviceDAO = new ServiceDAO();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("services", serviceDAO.getAllServices());
        req.getRequestDispatcher("/pages/admin/services.jsp").forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("add".equals(action)) {
            ServicePrice s = new ServicePrice();
            s.setName(req.getParameter("name"));
            s.setServiceType(req.getParameter("serviceType"));
            s.setPrice(new BigDecimal(req.getParameter("price")));
            serviceDAO.addService(s);
        } else if ("update".equals(action)) {
            ServicePrice s = new ServicePrice();
            s.setServiceId(Integer.parseInt(req.getParameter("serviceId")));
            s.setName(req.getParameter("name"));
            s.setServiceType(req.getParameter("serviceType"));
            s.setPrice(new BigDecimal(req.getParameter("price")));
            serviceDAO.updateService(s);
        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(req.getParameter("serviceId"));
            serviceDAO.deleteService(id);
        }
        resp.sendRedirect(req.getContextPath() + "/admin/services");
    }
    
}
