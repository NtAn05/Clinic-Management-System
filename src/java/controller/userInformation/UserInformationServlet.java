/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.userInformation;

import dal.DoctorDAO;
import dal.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

/**
 *
 * @author Admin
 */
public class UserInformationServlet extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet UserInformationServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UserInformationServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
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
        HttpSession session = request.getSession(false);
//        System.out.println("Test display session : ",session);
        if (session == null || session.getAttribute("account") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
     
        User userSession = (User) session.getAttribute("account");
        int userId = userSession.getUserId();
        
 
        
        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserById1(userId);
       
        request.setAttribute("user", user);
        request.setAttribute("roleName", user.getRole());
        DoctorDAO doc = new DoctorDAO();
        if ("doctor".equals(user.getRole())) {
        var doctor = doc.getDoctorByUserId(userId);
        request.setAttribute("doctor", doctor);
    }

    request.getRequestDispatcher("/pages/profile/userInformation/userInformation.jsp")
            .forward(request, response);
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
        HttpSession session = request.getSession(false);
        UserDAO users = new UserDAO();
        DoctorDAO doctors = new DoctorDAO();
        if (session == null || session.getAttribute("account") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User userSession = (User) session.getAttribute("account");
        int userId = userSession.getUserId();
        String action = request.getParameter("action");

        if ("updateProfile".equals(action)) {

            String name = request.getParameter("txtName");
            String userID = request.getParameter("userID");
            int userIDD = Integer.parseInt(userID);
            String phone = request.getParameter("txtPhone");
            String email = request.getParameter("txtEmail");

            String address = request.getParameter("txtAddress");
            String txtImage = request.getParameter("txtImage");

            users.updateUser(userIDD, name, phone, email);
            User updatedUser = users.getUserById1(userIDD);
            session.setAttribute("account", updatedUser);
response.sendRedirect(request.getContextPath() + "/userinformationservlet");        }

        if ("changePass".equals(action)) {

            String oldPass = request.getParameter("txtOldPass");
            String newPass = request.getParameter("txtNewPass");
            String rePass = request.getParameter("txtReNewPass");

            boolean check = users.checkOldPassword(userId, oldPass);

            if (check && newPass.equals(rePass)) {
                users.updatePassword(userId, newPass);
            }

response.sendRedirect(request.getContextPath() + "/userinformationservlet");
        }

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
