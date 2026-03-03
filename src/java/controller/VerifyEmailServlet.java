package controller;

import dal.UserDAO;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "VerifyEmailServlet", urlPatterns = {"/verify-email"})
public class VerifyEmailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || RegisterServlet.getPendingData(session) == null) {
            response.sendRedirect(request.getContextPath() + "/pages/auth/register.jsp");
            return;
        }
        request.getRequestDispatcher("/pages/auth/verify-email.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/pages/auth/register.jsp");
            return;
        }

        String action = request.getParameter("action");
        if ("resend".equals(action)) {
            handleResendOtp(request, response, session);
            return;
        }
        if ("cancel".equals(action)) {
            RegisterServlet.clearPendingRegister(session);
            response.sendRedirect(request.getContextPath() + "/pages/auth/register.jsp");
            return;
        }

        Map<String, String> pendingData = RegisterServlet.getPendingData(session);
        String storedOtp = RegisterServlet.getOtp(session);
        Long otpExpires = RegisterServlet.getOtpExpires(session);
        String submittedOtp = request.getParameter("otp");

        if (pendingData == null || storedOtp == null || otpExpires == null) {
            request.setAttribute("error", "Phiên đăng ký đã hết hạn. Vui lòng đăng ký lại.");
            request.getRequestDispatcher("/pages/auth/register.jsp").forward(request, response);
            return;
        }

        if (System.currentTimeMillis() > otpExpires) {
            request.setAttribute("error", "Mã OTP Gmail đã hết hạn. Vui lòng nhấn gửi lại OTP mới.");
            request.getRequestDispatcher("/pages/auth/verify-email.jsp").forward(request, response);
            return;
        }

        if (submittedOtp == null || !submittedOtp.equals(storedOtp)) {
            request.setAttribute("error", "Mã OTP Gmail không đúng.");
            request.getRequestDispatcher("/pages/auth/verify-email.jsp").forward(request, response);
            return;
        }

        try {
            UserDAO dao = new UserDAO();
            dao.registerPatient(
                    pendingData.get("fullName"),
                    pendingData.get("phone"),
                    pendingData.get("email"),
                    pendingData.get("password"),
                    Date.valueOf(pendingData.get("dob")),
                    pendingData.get("address"),
                    pendingData.get("gender")
            );
            RegisterServlet.clearPendingRegister(session);
            response.sendRedirect(request.getContextPath() + "/pages/auth/login.jsp?registered=true");

     
        } catch (Exception e) {
            e.printStackTrace(); 
            request.setAttribute("error", "Lỗi CSDL: " + e.getMessage()); 
            
            request.getRequestDispatcher("/pages/auth/register.jsp").forward(request, response);
        }
    }

    private void handleResendOtp(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        Map<String, String> pendingData = RegisterServlet.getPendingData(session);
        if (pendingData == null) {
            response.sendRedirect(request.getContextPath() + "/pages/auth/register.jsp");
            return;
        }

        String newOtp = RegisterServlet.generateOtp();
        long expiresAt = System.currentTimeMillis() + RegisterServlet.getOtpTtlMs();
        session.setAttribute("registerOtp", newOtp);
        session.setAttribute("registerOtpExpires", expiresAt);

        String sendError = RegisterServlet.sendOtpEmail(pendingData.get("email"), pendingData.get("fullName"), newOtp);
        if (sendError != null) {
            request.setAttribute("error", sendError);
        } else {
            request.setAttribute("success", "Đã gửi OTP Gmail mới. Mã có hiệu lực trong 60 giây.");
        }

        request.getRequestDispatcher("/pages/auth/verify-email.jsp").forward(request, response);
    }
}