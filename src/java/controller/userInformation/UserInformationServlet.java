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
import model.Doctors;
import model.User;
import model.Users;

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
        processRequest(request, response);
        HttpSession session = request.getSession(false);
        UserDAO users = new UserDAO();
        DoctorDAO doctor = new DoctorDAO();
        Users user = (Users) session.getAttribute("account");
        int userId = user.getUserId();
        request.setAttribute("user", users.getUserById2(userId));
        request.setAttribute("doctor", doctor.getDoctorByUserId2(userId));

        request.getRequestDispatcher("/pages/user/userInformation.jsp")
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
        processRequest(request, response);
        HttpSession session = request.getSession(false);
UserDAO users = new UserDAO();
            DoctorDAO doctors = new DoctorDAO();
        if (session == null || session.getAttribute("account") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Users userSession = (Users) session.getAttribute("account");
        int userId = userSession.getUserId();

        String action = request.getParameter("action");

        if ("updateProfile".equals(action)) {

            String name = request.getParameter("txtName");
            String phone = request.getParameter("txtPhone");
            String email = request.getParameter("txtEmail");
            String gender = request.getParameter("txtGender");
            String dob = request.getParameter("txtDob");
            String city = request.getParameter("city");
            String commune = request.getParameter("commune");
            String house = request.getParameter("house");
            
            users.updateUser(userId, name, phone, email, gender, dob, city, commune, house);

            // Update doctor if exists
            Doctors doctor= (Doctors) doctors.getDoctorByUserId2(userId);
            if ( doctors.getDoctorByUserId(userId) != null) {

                String qualification = request.getParameter("txtQualification");
                int experience = Integer.parseInt(request.getParameter("txtExperience"));
                String specialization = request.getParameter("txtSpecialization");

                 doctors.updateDoctor(
                        doctor.getDoctorId(),
                        qualification,
                        experience,
                        specialization
                );
            }

            response.sendRedirect("userinformation");
        }

        if ("changePass".equals(action)) {

            String oldPass = request.getParameter("txtOldPass");
            String newPass = request.getParameter("txtNewPass");
            String rePass = request.getParameter("txtReNewPass");

            boolean check = users.checkOldPassword(userId, oldPass);

            if (check && newPass.equals(rePass)) {
                users.updatePassword(userId, newPass);
            }

            response.sendRedirect("userinformation");
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
