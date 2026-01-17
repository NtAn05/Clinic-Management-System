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
            // 2. LOGIC KIỂM TRA QUYỀN (Đã sửa)
            
            // TRƯỜNG HỢP 1: Đang đứng ở tab "Nhân viên / Bác sĩ"
            if (selectedRole.equals("staff")) {
                // Chỉ chặn nếu là Bệnh nhân (Role = 1)
                // Còn Role 2 (Bác sĩ) và Role 3 (Nhân viên) đều CHO PHÉP đi tiếp
                if (u.getRoleId() == 1) {
                    error = "Tài khoản Bệnh nhân không được đăng nhập ở khu vực này!";
                }
            }
            
            // TRƯỜNG HỢP 2: Đang đứng ở tab "Bệnh nhân"
            else if (selectedRole.equals("patient")) {
                // Chỉ cho phép Role 1 vào
                if (u.getRoleId() != 1) {
                    error = "Nhân viên/Bác sĩ vui lòng đăng nhập ở tab bên cạnh!";
                }
            }
            
            // 3. Kiểm tra xem tài khoản có bị khóa không (Status = 0)
            if (error == null && !u.isStatus()) {
                error = "Tài khoản của bạn đã bị khóa! Vui lòng liên hệ Admin.";
            }
        }

        if (error != null) {
            // --- NẾU CÓ LỖI ---
            request.setAttribute("error", error);
            request.setAttribute("phone", phone); // Giữ lại SĐT để không phải nhập lại
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            // --- ĐĂNG NHẬP THÀNH CÔNG ---
            HttpSession session = request.getSession();
            session.setAttribute("account", u);
            
            // (Tuỳ chọn) Điều hướng riêng cho Nhân viên nếu muốn
            // if (u.getRoleId() == 3) response.sendRedirect("staff-dashboard.jsp");
            // else 
            response.sendRedirect("home.jsp");
        }
    }
}