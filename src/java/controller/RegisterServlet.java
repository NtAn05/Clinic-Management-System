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
import static org.apache.tomcat.jakartaee.commons.lang3.StringUtils.normalizeSpace;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private static final String OTP_SESSION_KEY = "registerOtp";
    private static final String OTP_EXPIRES_SESSION_KEY = "registerOtpExpires";
    private static final String PENDING_REGISTER_SESSION_KEY = "pendingRegisterData";
    private static final long OTP_TTL_MS = 60 * 1000; // Thời gian sống của OTP là 60 giây

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/pages/auth/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // 1. CHỈ LẤY 4 TRƯỜNG CƠ BẢN CỦA TÀI KHOẢN GIÁM HỘ
        String fullName = normalizeSpace(request.getParameter("fullname"));
        String phone = normalizeSpace(request.getParameter("phone"));
        String email = normalizeSpace(request.getParameter("email"));
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirmPassword");

        UserDAO dao = new UserDAO();
        String error = null;

        // 2. KIỂM TRA LỖI (VALIDATION)
        if (fullName == null || fullName.isBlank() || fullName.length() < 2) {
            error = "Họ tên không hợp lệ";
        } else if (phone == null || !phone.matches("0\\d{9}")) {
            error = "Số điện thoại không hợp lệ (Phải bắt đầu bằng số 0 và đủ 10 số)";
        } else if (!password.equals(confirm)) {
            error = "Mật khẩu không khớp";
        } else if (dao.isPhoneExist(phone)) {
            error = "Số điện thoại đã được đăng ký";
        } else if (email != null && !email.isBlank() && !Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email)) {
            error = "Email không hợp lệ";
        } else if (email != null && !email.isBlank() && dao.isEmailExist(email)) {
            error = "Email đã tồn tại trong hệ thống";
        }

        // Nếu có lỗi thì đẩy về trang đăng ký
        if (error != null) {
            setBackData(request, fullName, phone, email);
            request.setAttribute("error", error);
            request.getRequestDispatcher("/pages/auth/register.jsp").forward(request, response);
            return;
        }

        // 3. LƯU TẠM DỮ LIỆU VÀO SESSION CHỜ XÁC THỰC
        HttpSession session = request.getSession();
        Map<String, String> pendingData = new HashMap<>();
        pendingData.put("fullName", fullName);
        pendingData.put("phone", phone);
        pendingData.put("email", email);
        pendingData.put("password", password);

        String otpCode = generateOtp();
        long expiredAt = System.currentTimeMillis() + OTP_TTL_MS;

        session.setAttribute(PENDING_REGISTER_SESSION_KEY, pendingData);
        session.setAttribute(OTP_SESSION_KEY, otpCode);
        session.setAttribute(OTP_EXPIRES_SESSION_KEY, expiredAt);

        // 4. GỌI HÀM GỬI MAIL (CHẠY NGẦM)
        String sentError = sendOtpEmail(email, fullName, otpCode);
        
        if (sentError != null) {
            request.setAttribute("error", sentError);
        } else {
            request.setAttribute("success", "Đã gửi OTP đến Gmail của bạn. Mã có hiệu lực trong 60 giây.");
        }
        
        // 5. CHUYỂN TRANG NGAY LẬP TỨC
        request.getRequestDispatcher("/pages/auth/verify-email.jsp").forward(request, response);
    }

    public static String generateOtp() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    public static long getOtpTtlMs() {
        return OTP_TTL_MS;
    }

    // ĐÃ TỐI ƯU: SỬ DỤNG ĐA LUỒNG (THREAD) ĐỂ GỬI MAIL KHÔNG LÀM TREO TRANG
    public static String sendOtpEmail(String email, String fullName, String otpCode) {
        if (email == null || email.isBlank()) {
            return "Bạn cần nhập Gmail để nhận OTP.";
        }

        // Tạo luồng mới chạy ngầm phía sau
        new Thread(() -> {
            try {
                // Gọi tới hàm gửi mail thật của bạn
                EmailOtpService.sendOtp(email, fullName, otpCode, OTP_TTL_MS / 1000);
                System.out.println("Đã gửi OTP ngầm thành công tới: " + email);
            } catch (IllegalStateException ex) {
                System.out.println("Lỗi cấu hình Gmail OTP: " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("Lỗi mạng khi gửi Gmail OTP tới " + email);
                ex.printStackTrace();
            }
        }).start(); // Bắt đầu chạy luồng

        // Trả về null ngay lập tức để hệ thống đi tiếp mà không chờ gửi mail xong
        return null; 
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
            String email) {
        request.setAttribute("fullname", fullName);
        request.setAttribute("phone", phone);
        request.setAttribute("email", email);
    }

    private String normalizeSpace(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().replaceAll("\\s+", " ");
    }
}