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

@WebServlet(name = "VerifyPhoneServlet", urlPatterns = {"/verify-phone"})
public class VerifyPhoneServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || RegisterServlet.getPendingData(session) == null) {
            response.sendRedirect(request.getContextPath() + "/pages/auth/register.jsp");
            return;
        }
        request.getRequestDispatcher("/pages/auth/verify-phone.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
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
            RegisterServlet.clearPendingRegister(session);
            request.setAttribute("error", "Mã xác thực SĐT đã hết hạn. Vui lòng đăng ký lại.");
            request.getRequestDispatcher("/pages/auth/register.jsp").forward(request, response);
            return;
        }

        if (submittedOtp == null || !submittedOtp.equals(storedOtp)) {
            request.setAttribute("error", "Mã OTP SĐT không đúng.");
            request.setAttribute("demoOtp", storedOtp);
            request.getRequestDispatcher("/pages/auth/verify-phone.jsp").forward(request, response);
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

        } catch (SQLException | IllegalArgumentException e) {
            request.setAttribute("error", "Có lỗi khi tạo tài khoản. Vui lòng thử lại.");
            request.getRequestDispatcher("/pages/auth/register.jsp").forward(request, response);
        }
    }
}