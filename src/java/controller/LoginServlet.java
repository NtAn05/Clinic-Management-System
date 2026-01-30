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
            error = "Sai thông tin đăng nhập!";
        } else {
            
            
            // Lấy role ra (đang là String)
            String userRole = u.getRole(); 

            if (selectedRole.equals("staff")) {
                // Nếu là patient -> Chặn
                if (userRole.equals("patient")) {
                    error = "Bệnh nhân vui lòng sang tab Bệnh nhân!";
                }
            } else if (selectedRole.equals("patient")) {
                // Nếu KHÔNG PHẢI patient -> Chặn
                if (!userRole.equals("patient")) {
                    error = "Nhân viên vui lòng sang tab Nhân viên!";
                }
            }

            // So sánh status (String)
            if (u.getStatus().equals("inactive")) {
                error = "Tài khoản bị khóa!";
            }
        }

        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("phone", phone);
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            HttpSession session = request.getSession();
            session.setAttribute("account", u);
            
            // Điều hướng
            if (u.getRole().equals("admin")) {
                 response.sendRedirect("admin-dashboard.jsp");
            } else {
                 response.sendRedirect("home.jsp");
            }
        }
    }
}