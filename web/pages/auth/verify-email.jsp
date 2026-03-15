<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // JAVA LOGIC: Tính toán số giây còn lại thực tế từ Session
    // LƯU Ý: Chắc chắn trong ForgotPasswordServlet bạn dùng session tên là "forgotOtpExpires" nhé
    Long expiresAt = (Long) session.getAttribute("forgotOtpExpires");
    long remainingSeconds = 0;
    
    if (expiresAt != null) {
        remainingSeconds = (expiresAt - System.currentTimeMillis()) / 1000;
        if (remainingSeconds < 0) {
            remainingSeconds = 0; 
        }
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quên mật khẩu - Phòng khám ABC</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        /* GIỮ NGUYÊN BỘ CSS CHUẨN CỦA BẠN */
        :root {
            --primary: #0061ff;
            --primary-hover: #0052d6;
            --bg: #f4f7fe;
            --text-main: #1f2937;
            --text-muted: #6b7280;
            --error-bg: #fee2e2;
            --error-text: #b91c1c;
            --warning-text: #d97706;
        }
        body {
            font-family: 'Segoe UI', sans-serif;
            margin: 0;
            background: var(--bg);
            display: flex;
            flex-direction: column;
            min-height: 100vh;
        }
        
        .main-content {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 40px 20px;
        }

        .verify-card {
            background: #fff;
            padding: 40px;
            border-radius: 16px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
            width: 100%;
            max-width: 420px;
            box-sizing: border-box;
        }
        
        /* Căn trái tiêu đề cho giống ảnh bạn gửi */
        .verify-card h2 { color: var(--primary); margin-top: 0; margin-bottom: 10px; font-size: 24px; text-align: left; }
        .verify-card p.subtitle { color: var(--text-muted); font-size: 14px; margin-bottom: 20px; line-height: 1.5; text-align: left; }
        
        .timer-display { font-weight: 600; color: var(--warning-text); margin-bottom: 20px; font-size: 15px; text-align: left; display: none; }
        
        .alert-error { background: var(--error-bg); color: var(--error-text); padding: 12px; border-radius: 8px; font-size: 14px; margin-bottom: 20px; text-align: left; border: 1px solid #fca5a5; }
        .alert-success { background: #dcfce7; color: #15803d; padding: 12px; border-radius: 8px; font-size: 14px; margin-bottom: 20px; text-align: left; border: 1px solid #86efac; }
        
        .form-group { text-align: left; margin-bottom: 20px; }
        .form-group label { display: block; font-weight: 600; margin-bottom: 8px; font-size: 14px; color: var(--text-main); }
        
        /* Class mới cho ô nhập Gmail (không bị cách chữ) */
        .form-control {
            width: 100%; padding: 14px; border: 1px solid #d1d5db; border-radius: 8px;
            font-size: 15px; box-sizing: border-box; outline: none; transition: border-color 0.2s;
        }
        .form-control:focus { border-color: var(--primary); box-shadow: 0 0 0 3px rgba(0, 97, 255, 0.1); }

        .otp-input {
            width: 100%; padding: 14px; border: 1px solid #d1d5db; border-radius: 8px;
            font-size: 18px; letter-spacing: 5px; text-align: center; box-sizing: border-box;
            outline: none; transition: border-color 0.2s;
        }
        .otp-input:focus { border-color: var(--primary); box-shadow: 0 0 0 3px rgba(0, 97, 255, 0.1); }
        
        .btn { width: 100%; padding: 14px; border-radius: 8px; font-weight: 600; font-size: 15px; cursor: pointer; transition: all 0.2s; border: none; margin-bottom: 12px; }
        .btn-primary { background: var(--primary); color: white; }
        .btn-primary:hover { background: var(--primary-hover); }
        .btn-secondary { background: white; color: var(--primary); border: 1px solid var(--primary); }
        .btn-secondary:hover:not(:disabled) { background: #f0f5ff; }
        .btn-secondary:disabled { border-color: #9ca3af; color: #9ca3af; cursor: not-allowed; background: #f9fafb; }
        
        /* BỎ GẠCH CHÂN LINK QUAY LẠI */
        .back-link {
            color: #5b21b6;
            text-decoration: none; /* Dòng này xóa gạch chân */
            font-size: 14px;
            font-weight: 500;
            display: inline-block;
            margin-top: 10px;
            transition: 0.2s;
        }
        .back-link:hover {
            color: var(--primary);
            text-decoration: none; /* Đảm bảo lúc di chuột vào cũng không có gạch chân */
        }
    </style>
</head>
<body>

<jsp:include page="/common/header.jsp" />

<div class="main-content">
    <div class="verify-card">
        <h2>Quên mật khẩu</h2>
        <p class="subtitle">Nhập Gmail để nhận OTP và đặt lại mật khẩu.</p>

        <div class="timer-display" id="timerDisplay">Đang tải thời gian...</div>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert-error">
                <i class="fas fa-exclamation-circle"></i> <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <% if (request.getAttribute("success") != null) { %>
            <div class="alert-success">
                <i class="fas fa-check-circle"></i> <%= request.getAttribute("success") %>
            </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/forgot-password" method="POST" id="verifyForm">
            <div class="form-group">
                <label>Gmail</label>
                <input type="email" name="email" class="form-control" value="${email}" placeholder="example@gmail.com" required>
            </div>

            <div class="form-group">
                <label>Mã OTP</label>
                <input type="text" name="otp" class="otp-input" placeholder="******" maxlength="6" autocomplete="off">
            </div>
            
            <button type="submit" name="action" value="verify" class="btn btn-primary" id="submitBtn">Xác thực OTP</button>
            <button type="submit" name="action" value="resend" class="btn btn-secondary" id="resendBtn">Gửi OTP</button>
        </form>

        <div style="text-align: center;">
            <a href="${pageContext.request.contextPath}/login" class="back-link">← Quay lại đăng nhập</a>
        </div>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />

<script>
    let timeLeft = <%= remainingSeconds %>;
    const timerDisplay = document.getElementById("timerDisplay");
    const resendBtn = document.getElementById("resendBtn");

    function updateUI() {
        if (timeLeft <= 0) {
            timerDisplay.style.display = "none"; // Ẩn đồng hồ đi nếu chưa gửi hoặc hết hạn
            resendBtn.innerText = "Gửi OTP";
            resendBtn.disabled = false;
        } else {
            timerDisplay.style.display = "block"; // Hiện đồng hồ lên
            timerDisplay.innerText = "Mã OTP sẽ hết hạn sau: " + timeLeft + "s";
            timerDisplay.style.color = "var(--warning-text)";
            resendBtn.innerText = "Gửi lại OTP (" + timeLeft + "s)";
            resendBtn.disabled = true;
        }
    }

    // Nếu server báo đã gửi OTP xong mà lại bị F5, nếu hết hạn thì báo lỗi đỏ
    <% if (request.getAttribute("success") != null) { %>
        if (timeLeft <= 0) {
            timerDisplay.style.display = "block";
            timerDisplay.innerText = "Mã OTP đã hết hạn";
            timerDisplay.style.color = "var(--error-text)";
        }
    <% } %>

    updateUI();

    if (timeLeft > 0) {
        const countdownTimer = setInterval(function() {
            timeLeft--;
            updateUI();
            if (timeLeft <= 0) {
                clearInterval(countdownTimer);
            }
        }, 1000);
    }
</script>

</body>
</html>