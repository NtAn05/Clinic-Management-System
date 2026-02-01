package controller;

import dal.UserDAO;
import model.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String phone = request.getParameter("phone");
        String pass = request.getParameter("password");
        String selectedRole = request.getParameter("role");

        UserDAO dao = new UserDAO();
        User u = dao.checkLogin(phone, pass);
        String error = null;

        if (u == null) {
            error = "Sai số điện thoại hoặc mật khẩu!";
        } else {
            
            String roleName = u.getRole().name().toLowerCase(); 
            String status = u.getStatus().name().toLowerCase(); 
            if (selectedRole.equals("staff")) {
                if (roleName.equals("patient")) {
                    error = "Bệnh nhân vui lòng đăng nhập bên tab Bệnh nhân!";
                }
            } else if (selectedRole.equals("patient")) {
                if (!roleName.equals("patient")) {
                    error = "Nhân viên vui lòng đăng nhập bên tab Nhân viên!";
                }
            }

            if (!status.equals("active")) {
                error = "Tài khoản đang bị khóa!";
            }
        }

        if (error != null) {
            
            request.setAttribute("error", error);
            request.setAttribute("phone", phone);
            request.getRequestDispatcher("/pages/auth/login.jsp").forward(request, response);
        } else {
            HttpSession session = request.getSession();
            session.setAttribute("account", u);
            
           
            response.sendRedirect(request.getContextPath() + "/pages/home/home.jsp");
            // ----------------------------------------------------
        }
    }
}