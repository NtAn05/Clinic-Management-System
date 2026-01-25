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

        String fullName = request.getParameter("fullname");
        String dob = request.getParameter("dob");
        String genderRaw = request.getParameter("gender");
        String gender;
        switch (genderRaw) {
            case "Nam":
                gender = "male";
                break;
            case "Nữ":
                gender = "female";
                break;
            default:
                gender = "other";
        }
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirmPassword");

        UserDAO dao = new UserDAO();
        String error = null;

        if (!phone.matches("\\d{10}")) {
            error = "SĐT không hợp lệ";
        } else if (!password.equals(confirm)) {
            error = "Mật khẩu không khớp";
        } else if (dao.isPhoneExist(phone)) {
            error = "SĐT đã tồn tại";
        } else if (email != null && !email.isEmpty() && dao.isEmailExist(email)) {
            error = "Email đã tồn tại";
        }

        if (error != null) {
            request.setAttribute("error", error);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        Date dobSql = null;
        try {
            if (dob != null && !dob.isEmpty()) {
                dobSql = Date.valueOf(dob);
            }
            dao.registerPatient(
                    fullName,
                    phone,
                    email,
                    password,
                    Date.valueOf(dob),
                    address,
                    gender
            );
            response.sendRedirect("login.jsp");
        } catch (Exception e) {
            System.out.println(" LỖI KHI ĐĂNG KÝ");
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }

    // Hàm phụ trợ để check Email bằng Regex
    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }
}
