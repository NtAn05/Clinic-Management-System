package controller;

import dal.UserDAO;
import model.EmailOtpService;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private static final String OTP_SESSION_KEY = "registerOtp";
    private static final String OTP_EXPIRES_SESSION_KEY = "registerOtpExpires";
    private static final String PENDING_REGISTER_SESSION_KEY = "pendingRegisterData";
    private static final long OTP_TTL_MS = 60 * 1000;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/pages/auth/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String fullName = request.getParameter("fullname");
        String dob = request.getParameter("dob");
        String genderRaw = request.getParameter("gender");
        String gender = "other";
        if ("Nam".equals(genderRaw)) {
            gender = "male";
        }
        if ("Nữ".equals(genderRaw)) {
            gender = "female";
        }

        // ================= ĐOẠN NÀY VỪA BỊ MẤT ĐÃ ĐƯỢC PHỤC HỒI =================
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
        } else if (email != null && !email.isBlank() && !Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email)) {
            error = "Email không hợp lệ";
        } else if (email != null && !email.isBlank() && dao.isEmailExist(email)) {
            error = "Email đã tồn tại";
        }

        if (error != null) {
            setBackData(request, fullName, phone, email, dob, city, ward, street, genderRaw);
            request.setAttribute("error", error);
            request.getRequestDispatcher("/pages/auth/register.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        Map<String, String> pendingData = new HashMap<>();
        pendingData.put("fullName", fullName);
        pendingData.put("phone", phone);
        pendingData.put("email", email);
        pendingData.put("password", password);
        pendingData.put("dob", dob);
        pendingData.put("address", finalAddress);
        pendingData.put("gender", gender);

        String otpCode = generateOtp();
        long expiredAt = System.currentTimeMillis() + OTP_TTL_MS;

        session.setAttribute(PENDING_REGISTER_SESSION_KEY, pendingData);
        session.setAttribute(OTP_SESSION_KEY, otpCode);
        session.setAttribute(OTP_EXPIRES_SESSION_KEY, expiredAt);

        String sentError = sendOtpEmail(email, fullName, otpCode);
        if (sentError != null) {
            request.setAttribute("error", sentError);
        } else {
            request.setAttribute("success", "Đã gửi OTP đến Gmail của bạn. Mã có hiệu lực trong 60 giây.");
        }
        request.getRequestDispatcher("/pages/auth/verify-email.jsp").forward(request, response);
    }

    public static String generateOtp() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    public static long getOtpTtlMs() {
        return OTP_TTL_MS;
    }

    public static String sendOtpEmail(String email, String fullName, String otpCode) {
        if (email == null || email.isBlank()) {
            return "Bạn cần nhập Gmail để nhận OTP.";
        }

        try {
            EmailOtpService.sendOtp(email, fullName, otpCode, OTP_TTL_MS / 1000);
            return null;
        } catch (IllegalStateException ex) {
            return "Hệ thống gửi Gmail OTP chưa cấu hình. Vui lòng thiết lập GMAIL_OTP_SENDER và GMAIL_OTP_APP_PASSWORD.";
        } catch (Exception ex) {
            return "Không thể gửi OTP qua Gmail. Vui lòng thử gửi lại.";
        }
    }

    public static Map<String, String> getPendingData(HttpSession session) {
        Object data = session.getAttribute(PENDING_REGISTER_SESSION_KEY);
        if (data instanceof Map<?, ?>) {
            return (Map<String, String>) data;
        }
        return null;
    }

    public static String getOtp(HttpSession session) {
        Object otp = session.getAttribute(OTP_SESSION_KEY);
        return otp == null ? null : otp.toString();
    }

    public static Long getOtpExpires(HttpSession session) {
        Object expires = session.getAttribute(OTP_EXPIRES_SESSION_KEY);
        return expires instanceof Long ? (Long) expires : null;
    }

    public static void clearPendingRegister(HttpSession session) {
        session.removeAttribute(PENDING_REGISTER_SESSION_KEY);
        session.removeAttribute(OTP_SESSION_KEY);
        session.removeAttribute(OTP_EXPIRES_SESSION_KEY);
    }

    private void setBackData(HttpServletRequest request,
            String fullName,
            String phone,
            String email,
            String dob,
            String city,
            String ward,
            String street,
            String genderRaw) {
        request.setAttribute("fullname", fullName);
        request.setAttribute("phone", phone);
        request.setAttribute("email", email);
        request.setAttribute("dob", dob);
        request.setAttribute("city", city);
        request.setAttribute("ward", ward);
        request.setAttribute("street", street);
        request.setAttribute("gender", genderRaw);
    }
}