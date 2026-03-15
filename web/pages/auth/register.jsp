<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Đăng ký - Phòng khám ABC</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            :root {
                --primary: #0061ff;
                --bg: #f4f7fe;
                --error: #e11d48;
            }
            body {
                font-family: 'Segoe UI', sans-serif;
                margin: 0;
                background: var(--bg);
            }

            .container {
                display: flex;
                min-height: 100vh;
                width: 100%;
                background: white;
            }

            .btn-back-home {
                position: absolute;
                top: 25px;
                left: 30px;
                text-decoration: none;
                color: #64748b;
                font-size: 14px;
                font-weight: 500;
                display: flex;
                align-items: center;
                gap: 8px;
                transition: 0.3s;
                z-index: 10;
            }
            .btn-back-home:hover {
                color: var(--primary);
                transform: translateX(-3px);
            }

            .auth-form-side {
                flex: 1;
                padding: 60px 100px;
                display: flex;
                flex-direction: column;
                justify-content: center;
                position: relative;
            }

            .alert-error {
                background: #fee2e2;
                color: #b91c1c;
                padding: 15px;
                border-radius: 12px;
                margin-bottom: 25px;
                text-align: center;
                font-size: 14px;
                border: 1px solid #fecaca;
            }

            .form-row {
                display: flex;
                gap: 20px;
            }
            .form-group {
                flex: 1;
                margin-bottom: 20px;
                position: relative;
            }
            .form-group label {
                display: block;
                font-weight: 600;
                margin-bottom: 8px;
                font-size: 14px;
                color: #334155;
            }
            .form-group input {
                width: 100%;
                padding: 14px;
                border: 1px solid #e2e8f0;
                border-radius: 10px;
                box-sizing: border-box;
                outline: none;
                font-size: 15px;
                transition: 0.3s;
            }
            .form-group input:focus {
                border-color: var(--primary);
                box-shadow: 0 0 0 4px rgba(0, 97, 255, 0.1);
            }

            .js-error-text {
                color: var(--error);
                font-size: 12px;
                margin-top: 6px;
                display: none;
                font-weight: 500;
            }

            .btn-submit {
                width: 100%;
                background: var(--primary);
                color: white;
                border: none;
                padding: 16px;
                border-radius: 12px;
                font-weight: 700;
                cursor: pointer;
                font-size: 16px;
                margin-top: 10px;
                transition: 0.3s;
            }
            .btn-submit:hover {
                background: #004ecc;
                transform: translateY(-1px);
            }

            .auth-banner-side {
                flex: 0.8;
                background: #f8fbff;
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                padding: 40px;
                border-left: 1px solid #f1f5f9;
            }
            .banner-img {
                width: 100%;
                max-width: 420px;
                border-radius: 24px;
                margin-bottom: 30px;
            }

            .footer-links {
                text-align: center;
                margin-top: 20px;
                font-size: 15px;
            }
            .footer-links a {
                color: var(--primary);
                text-decoration: none;
                font-weight: 600;
            }

            #loadingOverlay {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(255, 255, 255, 0.9);
                display: none;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                z-index: 9999;
            }
            .spinner {
                width: 50px;
                height: 50px;
                border: 5px solid #f3f3f3;
                border-top: 5px solid var(--primary);
                border-radius: 50%;
                animation: spin 1s linear infinite;
            }
            @keyframes spin {
                0% {
                    transform: rotate(0deg);
                }
                100% {
                    transform: rotate(360deg);
                }
            }
        </style>
    </head>
    <body>
        <div id="loadingOverlay">
            <div class="spinner"></div>
            <p style="margin-top: 15px; font-weight: 600; color: var(--primary); font-size: 18px;">Đang xử lý gửi mã OTP đến Gmail...</p>
            <span style="color: #64748b;">Vui lòng chờ trong giây lát</span>
        </div>

        <div class="container">
            <a href="${pageContext.request.contextPath}/index.jsp" class="btn-back-home">
                <i class="fas fa-arrow-left"></i> Quay lại trang chủ
            </a>

            <div class="auth-form-side">
                <div style="text-align: center; margin-bottom: 30px;">
                    <i class="fas fa-heartbeat" style="font-size: 45px; color: var(--primary); margin-bottom: 10px;"></i>
                    <h2 style="font-size: 32px; color: #1e293b; margin: 0;">Tạo tài khoản</h2>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert-error" id="serverErrorBox"><i class="fas fa-exclamation-circle"></i> ${error}</div>
                </c:if>

                <form action="${pageContext.request.contextPath}/register" method="POST" id="mainRegisterForm">
                    <div class="form-group">
                        <label>Họ và tên *</label>
                        <input type="text" name="fullname" value="${fullname}" placeholder="Nhập tên người dùng / giám hộ" required>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Số điện thoại *</label>
                            <input type="tel" name="phone" value="${phone}" placeholder="09xxxxxxxx" required pattern="0[0-9]{9}">
                        </div>
                        <div class="form-group">
                            <label>Email *</label>
                            <input type="email" name="email" value="${email}" placeholder="example@gmail.com" required>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Mật khẩu *</label>
                            <input type="password" name="password" id="passField" placeholder="Từ 6 ký tự trở lên" required minlength="6">
                        </div>
                        <div class="form-group">
                            <label>Xác nhận mật khẩu *</label>
                            <input type="password" name="confirmPassword" id="confirmPassField" placeholder="Nhập lại mật khẩu" required>
                            <span class="js-error-text" id="passMatchError">Mật khẩu xác nhận không khớp!</span>
                        </div>
                    </div>

                    <div style="font-size: 14px; margin-bottom: 20px; color: #64748b;">
                        <input type="checkbox" id="agreeCheck" required>
                        <label for="agreeCheck" style="cursor:pointer">Tôi đồng ý với Điều khoản sử dụng.</label>
                    </div>

                    <button type="submit" class="btn-submit">Tiếp tục xác thực Email</button>

                    <div class="footer-links">
                        <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
                        <span style="color: #cbd5e1; margin: 0 10px;">|</span>
                        <a href="${pageContext.request.contextPath}/forgot-password">Quên mật khẩu?</a>
                    </div>
                </form>
            </div>

            <div class="auth-banner-side">
                <img src="https://img.freepik.com/free-vector/doctors-concept-illustration_114360-1515.jpg" alt="Medical Illustration" class="banner-img">
                <div style="text-align: center;">
                    <h3 style="font-size: 26px; color: #1e293b; margin-bottom: 15px;">Đăng ký dễ dàng</h3>
                    <p style="color: #64748b; font-size: 16px; max-width: 400px; line-height: 1.6;">Tạo tài khoản một lần để quản lý hồ sơ y tế và đặt lịch khám cho cả gia đình.</p>
                </div>
            </div>
        </div>

        <script>
            const regForm = document.getElementById('mainRegisterForm');

            regForm.addEventListener('submit', function (e) {
                let isValid = true;

                // Kiểm tra khớp mật khẩu
                const p1 = document.getElementById('passField').value;
                const p2 = document.getElementById('confirmPassField').value;
                if (p1 !== p2) {
                    isValid = false;
                    document.getElementById('passMatchError').style.display = 'block';
                }

                if (!isValid) {
                    e.preventDefault();
                } else {
                    // Nếu pass qua hết thì hiện vòng xoay loading
                    document.getElementById('loadingOverlay').style.display = 'flex';
                }
            });
        </script>
    </body>
</html>