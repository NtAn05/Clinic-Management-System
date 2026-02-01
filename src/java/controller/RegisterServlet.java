package controller;

import dal.UserDAO;
import java.io.IOException;
import java.sql.Date;
import java.util.regex.Pattern;
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
        String gender = "other";
        if ("Nam".equals(genderRaw)) gender = "male";
        if ("Nữ".equals(genderRaw)) gender = "female";
        
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        
        
        String city = request.getParameter("city");     
        String ward = request.getParameter("ward");     
        String street = request.getParameter("street"); 
        
        
        if (city == null) city = "";
        if (ward == null) ward = "";
        if (street == null) street = "";
        String finalAddress = street + " - " + ward + " - " + city;
        

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
            request.setAttribute("fullname", fullName);
            request.setAttribute("phone", phone);
            request.setAttribute("email", email);
            request.setAttribute("dob", dob);
            request.setAttribute("city", city);
            request.setAttribute("ward", ward);
            request.setAttribute("street", street);
            request.getRequestDispatcher("/pages/auth/register.jsp").forward(request, response);
            return;
        }

        try {
            Date dobSql = null;
            if (dob != null && !dob.isEmpty()) {
                dobSql = Date.valueOf(dob);
            }
            dao.registerPatient(
                    fullName,
                    phone,
                    email,
                    password,
                    dobSql,
                    finalAddress, // Truyền chuỗi đã ghép vào đây
                    gender
            );
            
            response.sendRedirect(request.getContextPath() + "/pages/auth/login.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("/pages/auth/register.jsp").forward(request, response);
        }
    }

    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }
}