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
import model.Role;
import model.Status;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward to login page
        request.getRequestDispatcher("/pages/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Lấy dữ liệu từ form gửi lên
        String phone = request.getParameter("phone");
        String pass = request.getParameter("password");
        String selectedRole = request.getParameter("role"); // Nhận biết tab nào đang được chọn ("staff" hoặc "patient")

        UserDAO dao = new UserDAO();
        User u = dao.checkLogin(phone, pass);

        String error = null;

        if (u == null) {
            error = "Sai số điện thoại hoặc mật khẩu!";
        } else {

            // 1: Đang đứng ở tab nhân viên / bác sĩ (staff)
            if (selectedRole.equals("staff")) {
                // Tab này chấp nhận: Admin (0), Bác sĩ (2), Nhân viên (3)
                // CHỈ CHẶN Bệnh nhân (1)
                if (u.getRole() == Role.patient) {
                    error = "Tài khoản Bệnh nhân không được đăng nhập ở khu vực này!";
                }
            } // 2: Đang đứng ở tab "Bệnh nhân" (patient)
            else if (selectedRole.equals("patient")) {
                // Chỉ cho phép Role 1 vào
                if (u.getRole() != Role.patient) {
                    error = "Admin/Nhân viên vui lòng đăng nhập ở tab bên cạnh!";
                }
            }

            // 3. Kiểm tra xem tài khoản có bị khóa không (Status = 0)
            if (u.getStatus() == Status.inactive) {
                error = "Tài khoản đã bị khóa";
            }
        }

        if (error != null) {
            // nếu có lỗi
            request.setAttribute("error", error);
            request.setAttribute("phone", phone); // Giữ lại SĐT để không phải nhập lại
            request.getRequestDispatcher("/pages/auth/login.jsp").forward(request, response);
        } else {
            // đăng nhập thành công
            HttpSession session = request.getSession();
            session.setAttribute("account", u);

            // (Tuỳ chọn) Điều hướng riêng cho Nhân viên nếu muốn
            // if (u.getRoleId() == 3) response.sendRedirect("staff-dashboard.jsp");
            // else 
            if (u.getRole() == Role.technician) {
                response.sendRedirect(request.getContextPath() + "/technician-dashboard");
            } else {
                response.sendRedirect("home.jsp");
            }
        }
    }
}
