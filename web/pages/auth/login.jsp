<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Đăng nhập - Phòng khám ABC</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

        <style>
            :root {
                --primary: #0061ff;
                --bg: #f4f7fe;
            }
            body {
                font-family: 'Segoe UI', sans-serif;
                margin: 0;
                background: var(--bg);
            }
            .container {
                display: flex;
                height: 100vh;
            }

            
            .auth-form-side {
                flex: 1;
                padding: 0 100px;
                background: white;
                display: flex;
                flex-direction: column;
                justify-content: center;
            }
            .logo-area {
                text-align: center;
                margin-bottom: 30px;
            }
            .logo-area i {
                font-size: 40px;
                color: var(--primary);
                margin-bottom: 10px;
            }

            
            .role-switch {
                display: flex;
                background: #f0f2f5;
                padding: 4px;
                border-radius: 8px;
                margin-bottom: 25px;
            }

            
            .role-switch button {
                flex: 1;
                padding: 10px;
                border: none;
                background: transparent;
                cursor: pointer;
                border-radius: 6px;
                font-weight: 600;
                color: #666;
                transition: all 0.3s ease;
            }

          
            .role-switch button.active {
                background: white;
                color: var(--primary);
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }

            
            .form-group {
                margin-bottom: 15px;
            }
            .form-group label {
                display: block;
                font-weight: 600;
                margin-bottom: 8px;
                font-size: 14px;
                color: #333;
            }
            .form-group input {
                width: 100%;
                padding: 12px;
                border: 1px solid #ddd;
                border-radius: 8px;
                box-sizing: border-box;
                outline: none;
                transition: 0.3s;
            }
            .form-group input:focus {
                border-color: var(--primary);
            }

            .options {
                display: flex;
                justify-content: space-between;
                font-size: 13px;
                margin-bottom: 25px;
                color: #555;
            }
            .options a {
                color: var(--primary);
                text-decoration: none;
            }

            .btn-submit {
                width: 100%;
                padding: 12px;
                background: var(--primary);
                color: white;
                border: none;
                border-radius: 8px;
                font-weight: bold;
                cursor: pointer;
                font-size: 16px;
            }
            .btn-submit:hover {
                background: #0052d6;
            }

            .footer-link {
                text-align: center;
                margin-top: 20px;
                font-size: 14px;
            }
            .footer-link a {
                color: var(--primary);
                text-decoration: none;
                font-weight: bold;
            }

            
            .auth-banner-side {
                flex: 1;
                background: linear-gradient(135deg, #e0f2ff 0%, #ffffff 100%);
                display: flex;
                align-items: center;
                justify-content: center;
                text-align: center;
                padding: 50px;
            }
            .banner-content img {
                width: 100%;
                max-width: 400px;
                border-radius: 20px;
                box-shadow: 0 10px 30px rgba(0,0,0,0.1);
                margin-bottom: 20px;
            }
            .banner-content h3 {
                color: #333;
                margin-bottom: 10px;
            }
            .banner-content p {
                color: #666;
                line-height: 1.5;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="auth-form-side">
                <div class="logo-area">
                    <i class="fas fa-heartbeat"></i>
                    <h2>Phòng Khám ABC</h2>
                    <p style="color: #666; font-size: 14px;">Hệ thống quản lý phòng khám</p>
                </div>

                <form action="login" method="POST">
                    <div class="role-switch">
                        <button type="button" id="btn-staff" class="active" onclick="selectRole('staff')">Nhân viên / Bác sĩ</button>
                        <button type="button" id="btn-patient" onclick="selectRole('patient')">Bệnh nhân</button>
                    </div>

                    <input type="hidden" name="role" id="selected-role" value="staff">

                    <% if(request.getAttribute("error") != null) { %>
                    <p style="color: red; text-align: center; font-size: 14px; background: #ffe6e6; padding: 10px; border-radius: 5px;">
                        <i class="fas fa-exclamation-circle"></i> <%= request.getAttribute("error") %>
                    </p>
                    <% } %>

                    <div class="form-group">
                        <label>Số điện thoại</label>
                        <input type="text" name="phone" placeholder="Nhập số điện thoại" required>
                    </div>
                    <div class="form-group">
                        <label>Mật khẩu</label>
                        <input type="password" name="password" placeholder="Nhập mật khẩu" required>
                    </div>

                    <div class="options">
                        <label><input type="checkbox"> Ghi nhớ đăng nhập</label>
                        <a href="#">Quên mật khẩu?</a>
                    </div>

                    <button type="submit" class="btn-submit">Đăng nhập</button>
                </form>
                <div class="footer-link">
                    Chưa có tài khoản? <a href="register.jsp">Đăng ký ngay</a>
                </div>
            </div>

            <div class="auth-banner-side">
                <div class="banner-content">
                    <img src="https://img.freepik.com/free-photo/doctor-nurses-special-equipment_23-2148980721.jpg" alt="Doctor Banner"> 
                    <h3>Chào mừng đến với Phòng Khám ABC</h3>
                    <p>Hệ thống quản lý hiện đại, giúp tối ưu hóa quy trình làm việc và nâng cao chất lượng chăm sóc sức khỏe.</p>
                </div>
            </div>
        </div>

        <script>
            function selectRole(role) {
                // Lấy 2 cái nút
                var btnStaff = document.getElementById("btn-staff");
                var btnPatient = document.getElementById("btn-patient");

                // Lấy cái input ẩn
                var inputRole = document.getElementById("selected-role");

                if (role === 'staff') {
                    // Nếu chọn Nhân viên
                    btnStaff.classList.add("active");
                    btnPatient.classList.remove("active");
                    inputRole.value = "staff"; // Cập nhật giá trị gửi đi
                } else {
                    // Nếu chọn Bệnh nhân
                    btnPatient.classList.add("active");
                    btnStaff.classList.remove("active");
                    inputRole.value = "patient"; // Cập nhật giá trị gửi đi
                }
            }
        </script>
    </body>
</html>