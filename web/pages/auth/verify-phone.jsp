<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Xác thực SĐT - Phòng khám ABC</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            body {
                margin: 0;
                font-family: 'Segoe UI', sans-serif;
                background: #f4f7fe;
                display: flex;
                justify-content: center;
                align-items: center;
                min-height: 100vh;
            }
            .card {
                width: 420px;
                background: #fff;
                border-radius: 16px;
                box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
                padding: 30px;
            }
            h2 {
                margin-top: 0;
                text-align: center;
                color: #0061ff;
            }
            .hint {
                color: #666;
                font-size: 14px;
                text-align: center;
                margin-bottom: 20px;
            }
            .form-group label {
                display: block;
                font-size: 14px;
                margin-bottom: 8px;
                font-weight: 600;
            }
            .form-group input {
                width: 100%;
                box-sizing: border-box;
                padding: 12px;
                border: 1px solid #ddd;
                border-radius: 8px;
                font-size: 16px;
                letter-spacing: 3px;
                text-align: center;
            }
            button {
                margin-top: 16px;
                width: 100%;
                border: none;
                border-radius: 8px;
                background: #0061ff;
                color: #fff;
                padding: 12px;
                font-size: 16px;
                font-weight: 600;
                cursor: pointer;
            }
            .error {
                background: #ffe6e6;
                color: #b02a37;
                border: 1px solid #ffcccc;
                padding: 10px;
                border-radius: 8px;
                margin-bottom: 12px;
                font-size: 14px;
            }
            .success {
                background: #d1e7dd;
                color: #0f5132;
                border: 1px solid #badbcc;
                padding: 10px;
                border-radius: 8px;
                margin-bottom: 12px;
                font-size: 14px;
            }
            .demo {
                margin-top: 12px;
                font-size: 13px;
                color: #444;
                background: #f8f9fa;
                border-radius: 8px;
                padding: 10px;
            }
            .back {
                display: inline-block;
                margin-top: 12px;
                text-decoration: none;
                color: #0061ff;
                font-size: 14px;
            }
        </style>
    </head>
    <body>
        <div class="card">
            <h2><i class="fas fa-mobile-screen-button"></i> Xác thực SĐT</h2>
            <p class="hint">Nhập mã OTP gửi tới SĐT để hoàn tất đăng ký tài khoản.</p>

            <% if(request.getAttribute("error") != null) { %>
            <div class="error"><i class="fas fa-exclamation-circle"></i> <%= request.getAttribute("error") %></div>
            <% } %>

            <% if(request.getAttribute("success") != null) { %>
            <div class="success"><i class="fas fa-check-circle"></i> <%= request.getAttribute("success") %></div>
            <% } %>

            <form action="${pageContext.request.contextPath}/verify-phone" method="POST">
                <div class="form-group">
                    <label>Mã OTP</label>
                    <input type="text" name="otp" maxlength="6" pattern="\d{6}" placeholder="123456" required>
                </div>
                <button type="submit">Xác thực & tạo tài khoản</button>
            </form>

            <% if(request.getAttribute("demoOtp") != null) { %>
            <div class="demo">
                <strong>OTP SĐT demo:</strong> <%= request.getAttribute("demoOtp") %>
            </div>
            <% } %>

            <a class="back" href="${pageContext.request.contextPath}/pages/auth/register.jsp">← Quay lại đăng ký</a>
        </div>
    </body>
</html>