package controller;

import dal.UserDAO;
import model.User;
import java.io.IOException;
import java.sql.Date;
import java.util.regex.Pattern; // Thêm thư viện này để check Email
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        // Lấy dữ liệu
        String fullName = request.getParameter("fullname");
        String dob = request.getParameter("dob");
        String gender = request.getParameter("gender");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        String pass = request.getParameter("password");
        String rePass = request.getParameter("confirmPassword");

        UserDAO dao = new UserDAO();
        String error = null;

        // --- BẮT ĐẦU VALIDATE DỮ LIỆU ---
        
        // 1. Kiểm tra Số điện thoại (Phải là số và đúng 10 ký tự)
        if (phone == null || !phone.matches("\\d{10}")) {
            error = "Số điện thoại không hợp lệ! Phải bao gồm đúng 10 chữ số.";
        }
        
        // 2. Kiểm tra Email (Dùng Regex chuẩn)
        // Nếu email không rỗng thì mới check (vì email là optional), nhưng nếu bạn muốn bắt buộc thì bỏ check rỗng
        else if (email != null && !email.isEmpty() && !isValidEmail(email)) {
            error = "Email không đúng định dạng (VD: example@gmail.com)!";
        }
        
        // 3. Kiểm tra Mật khẩu nhập lại
        else if (!pass.equals(rePass)) {
            error = "Mật khẩu nhập lại không khớp!";
        }
        
        // 4. Kiểm tra SĐT đã tồn tại chưa
        else if (dao.isPhoneExist(phone)) {
            error = "Số điện thoại này đã được đăng ký!";
        }

        // --- XỬ LÝ KẾT QUẢ ---
        if (error != null) {
            // CÓ LỖI -> Quay lại trang đăng ký và báo lỗi
            request.setAttribute("error", error);
            
            // QUAN TRỌNG: Gửi lại dữ liệu cũ để người dùng không phải nhập lại từ đầu
            request.setAttribute("fullname", fullName);
            request.setAttribute("dob", dob);
            request.setAttribute("gender", gender);
            request.setAttribute("phone", phone);
            request.setAttribute("email", email);
            request.setAttribute("address", address);
            
            request.getRequestDispatcher("register.jsp").forward(request, response);
        } else {
            // KHÔNG LỖI -> Lưu vào DB
            User u = new User(fullName, Date.valueOf(dob), gender, phone, email, address, pass);
            dao.register(u);
            
            // Chuyển sang login
            response.sendRedirect("login.jsp");
        }
    }

    // Hàm phụ trợ để check Email bằng Regex
    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }
}