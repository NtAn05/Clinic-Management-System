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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Chuyển hướng đến trang đăng nhập khi gõ URL trực tiếp
        request.getRequestDispatcher("/pages/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Lấy dữ liệu từ form login.jsp
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String formRole = request.getParameter("role"); // Sẽ nhận giá trị "patient" hoặc "staff"

        // 2. Gọi DAO để kiểm tra thông tin trong Database
        UserDAO dao = new UserDAO();
        User user = dao.checkLogin(phone, password);
        String error = null;

        if (user != null) {
            // 3. Lấy role thực tế từ Database
            String dbRole = user.getRole().toString().toLowerCase(); 

            // 4. Kiểm tra xem người dùng có chọn đúng Tab (Bệnh nhân / Nhân viên) không
            boolean isTabValid = false;
            if (formRole.equals("patient") && dbRole.equals("patient")) {
                isTabValid = true; // Bệnh nhân đăng nhập tab Bệnh nhân -> Hợp lệ
            } else if (formRole.equals("staff") && !dbRole.equals("patient")) {
                // Nhóm Nhân viên (admin, doctor, receptionist, technician) đăng nhập tab Nhân viên -> Hợp lệ
                isTabValid = true; 
            }

            // 5. Xử lý sau khi kiểm tra Tab
            if (isTabValid) {
                // Kiểm tra xem tài khoản có bị khóa không
                if (user.getStatus().toString().equalsIgnoreCase("inactive")) {
                    error = "Tài khoản của bạn đã bị khóa! Vui lòng liên hệ Admin.";
                } else {
                    // ĐĂNG NHẬP THÀNH CÔNG -> Lưu thông tin vào Session
                    HttpSession session = request.getSession();
                    session.setAttribute("account", user);

                    // 6. PHÂN QUYỀN ĐIỀU HƯỚNG TÙY THEO ROLE THỰC TẾ
                    if (dbRole.equals("admin")) {
                        response.sendRedirect(request.getContextPath() + "/admin-users");
                    } else if (dbRole.equals("doctor")) {
                        response.sendRedirect(request.getContextPath() + "/doctorDashboard");
                    } else if (dbRole.equals("technician")) {
                        response.sendRedirect(request.getContextPath() + "/lab-queue");
                    } else if (dbRole.equals("receptionist")) {
                        response.sendRedirect(request.getContextPath() + "/lab-payment"); 
                    } else { 
                       
                        response.sendRedirect(request.getContextPath() + "/index.jsp");
                    }
                    return; 
                }
            } else {
                error = "Vui lòng chọn đúng tab (Nhân viên hoặc Bệnh nhân) để đăng nhập!";
            }
        } else {
            error = "Số điện thoại hoặc mật khẩu không chính xác!";
        }

        
        if (error != null) {
            request.setAttribute("error", error);
            
           
            request.setAttribute("phone", phone); 
            request.setAttribute("role", formRole); 
            
         
            request.getRequestDispatcher("/pages/auth/login.jsp").forward(request, response);
                    // Redirect technician directly to Lab Queue page
                    // Receptionist: chuyển thẳng tới trang kiểm tra thanh toán
        }
    }
}