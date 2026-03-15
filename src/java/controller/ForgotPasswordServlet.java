package controller;

import dal.UserDAO;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.EmailOtpService;

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {

    private static final String FP_EMAIL = "forgotPasswordEmail";
    private static final String FP_OTP = "forgotPasswordOtp";
    private static final String FP_EXPIRES = "forgotPasswordOtpExpires";
    private static final String FP_VERIFIED = "forgotPasswordVerified";
    private static final long OTP_TTL_MS = 60 * 1000;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/pages/auth/forgot-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        if ("sendOtp".equals(action)) {
            handleSendOtp(request, session);
        } else if ("verifyOtp".equals(action)) {
            handleVerifyOtp(request, session);
        } else if ("resetPassword".equals(action)) {
            handleResetPassword(request, response, session);
            return;
        }

        request.getRequestDispatcher("/pages/auth/forgot-password.jsp").forward(request, response);
    }

    private void handleSendOtp(HttpServletRequest request, HttpSession session) {
        String email = normalizeSpace(request.getParameter("email"));
        request.setAttribute("email", email);

        if (email == null || email.isBlank() || !Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email)) {
            request.setAttribute("error", "Vui lòng nhập Gmail hợp lệ.");
            return;
        }

        UserDAO dao = new UserDAO();
        if (!dao.isEmailExist(email)) {
            request.setAttribute("error", "Gmail chưa tồn tại trong hệ thống.");
            return;
        }

        String otpCode = generateOtp();
        long expiresAt = System.currentTimeMillis() + OTP_TTL_MS;

        try {
            EmailOtpService.sendOtp(email, "", otpCode, OTP_TTL_MS / 1000);
            session.setAttribute(FP_EMAIL, email);
            session.setAttribute(FP_OTP, otpCode);
            session.setAttribute(FP_EXPIRES, expiresAt);
            session.setAttribute(FP_VERIFIED, Boolean.FALSE);
            request.setAttribute("success", "Đã gửi OTP đặt lại mật khẩu. Mã có hiệu lực 60 giây.");
            request.setAttribute("otpSent", true);
        } catch (Exception ex) {
            request.setAttribute("error", "Không thể gửi OTP. Vui lòng thử lại.");
        }
    }

    private void handleVerifyOtp(HttpServletRequest request, HttpSession session) {
        String submittedOtp = normalizeSpace(request.getParameter("otp"));
        String storedEmail = (String) session.getAttribute(FP_EMAIL);
        String storedOtp = (String) session.getAttribute(FP_OTP);
        Long expiresAt = (Long) session.getAttribute(FP_EXPIRES);

        request.setAttribute("email", storedEmail);
        request.setAttribute("otpSent", storedEmail != null);

        if (storedEmail == null || storedOtp == null || expiresAt == null) {
            request.setAttribute("error", "Phiên quên mật khẩu đã hết hạn. Vui lòng gửi lại OTP.");
            return;
        }

        if (System.currentTimeMillis() > expiresAt) {
            request.setAttribute("error", "OTP đã hết hạn. Vui lòng gửi lại OTP mới.");
            return;
        }

        if (submittedOtp == null || !submittedOtp.equals(storedOtp)) {
            request.setAttribute("error", "OTP không chính xác.");
            return;
        }

        session.setAttribute(FP_VERIFIED, Boolean.TRUE);
        request.setAttribute("verified", true);
        request.setAttribute("success", "Xác thực OTP thành công. Bạn có thể đặt mật khẩu mới.");
    }

    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException {
        String storedEmail = (String) session.getAttribute(FP_EMAIL);
        Boolean verified = (Boolean) session.getAttribute(FP_VERIFIED);
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (storedEmail == null || verified == null || !verified) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        if (newPassword == null || newPassword.length() < 6) {
            request.setAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự.");
            request.setAttribute("verified", true);
            request.setAttribute("otpSent", true);
            request.setAttribute("email", storedEmail);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp.");
            request.setAttribute("verified", true);
            request.setAttribute("otpSent", true);
            request.setAttribute("email", storedEmail);
            return;
        }

        UserDAO dao = new UserDAO();
        boolean updated = dao.updatePasswordByEmail(storedEmail, newPassword);
        if (updated) {
            clearSession(session);
            response.sendRedirect(request.getContextPath() + "/login?reset=true");
            return;
        }

        request.setAttribute("error", "Không thể đặt lại mật khẩu. Vui lòng thử lại.");
        request.setAttribute("verified", true);
        request.setAttribute("otpSent", true);
        request.setAttribute("email", storedEmail);
    }

    private String normalizeSpace(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().replaceAll("\\s+", " ");
    }

    private String generateOtp() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    private void clearSession(HttpSession session) {
        session.removeAttribute(FP_EMAIL);
        session.removeAttribute(FP_OTP);
        session.removeAttribute(FP_EXPIRES);
        session.removeAttribute(FP_VERIFIED);
    }
}