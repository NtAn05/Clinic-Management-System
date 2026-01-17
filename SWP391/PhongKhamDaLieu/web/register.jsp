<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng ký - Phòng khám ABC</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        /* GIỮ NGUYÊN CSS CŨ CỦA BẠN */
        :root { --primary: #0061ff; --bg: #f4f7fe; }
        body { font-family: 'Segoe UI', sans-serif; margin: 0; background: var(--bg); }
        .container { display: flex; height: 100vh; }
        .auth-form-side { flex: 1; padding: 40px 100px; background: white; overflow-y: auto; }
        .logo { margin-bottom: 20px; text-align: center; }
        .logo i { font-size: 35px; color: var(--primary); }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; font-weight: 600; margin-bottom: 5px; font-size: 14px; }
        .form-group input, .form-group select { width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 8px; box-sizing: border-box; }
        .form-group input:focus { border-color: var(--primary); outline: none; }
        .form-row { display: flex; gap: 15px; }
        .form-row .form-group { flex: 1; }
        .btn-submit { width: 100%; padding: 12px; background: var(--primary); color: white; border: none; border-radius: 8px; font-weight: bold; cursor: pointer; margin-top: 10px; font-size: 16px; }
        .btn-submit:hover { background: #0052d6; }
        .back-link { display: block; text-align: center; margin-top: 15px; color: #666; text-decoration: none; font-size: 14px; }
        .auth-banner-side { flex: 1; background: linear-gradient(135deg, #e0f2ff, #fff); display: flex; align-items: center; justify-content: center; padding: 40px; }
        .banner-card { background: white; padding: 30px; border-radius: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.05); max-width: 400px; text-align: center; }
        .banner-card img { width: 100%; border-radius: 15px; margin-bottom: 20px; }
        .features { list-style: none; padding: 0; text-align: left; margin-top: 20px; }
        .features li { margin-bottom: 10px; color: #555; font-size: 14px; }
        .features i { color: #2ecc71; margin-right: 10px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="auth-form-side">
            <div class="logo">
                <i class="fas fa-heartbeat"></i>
                <h2>Đăng ký tài khoản</h2>
                <p style="color: #666;">Tạo tài khoản bệnh nhân mới</p>
            </div>

            <form action="register" method="POST">
                <% if(request.getAttribute("error") != null) { %>
                    <p style="color: red; text-align: center; background: #ffe6e6; padding: 10px; border-radius: 5px; border: 1px solid #ffcccc;">
                        <i class="fas fa-exclamation-triangle"></i> <%= request.getAttribute("error") %>
                    </p>
                <% } %>

                <div class="form-group">
                    <label>Họ và tên *</label>
                    <input type="text" name="fullname" value="${fullname}" placeholder="Nhập họ và tên đầy đủ" required>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Ngày sinh *</label>
                        <input type="date" name="dob" value="${dob}" required>
                    </div>
                    <div class="form-group">
                        <label>Giới tính *</label>
                        <select name="gender">
                            <option value="Nam" ${gender == 'Nam' ? 'selected' : ''}>Nam</option>
                            <option value="Nữ" ${gender == 'Nữ' ? 'selected' : ''}>Nữ</option>
                            <option value="Khác" ${gender == 'Khác' ? 'selected' : ''}>Khác</option>
                        </select>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Số điện thoại * (10 số)</label>
                        <input type="tel" name="phone" value="${phone}" placeholder="0912345678" pattern="\d{10}" title="Số điện thoại phải gồm đúng 10 chữ số" required>
                    </div>
                    <div class="form-group">
                        <label>Email</label>
                        <input type="email" name="email" value="${email}" placeholder="email@example.com">
                    </div>
                </div>

                <div class="form-group">
                    <label>Địa chỉ *</label>
                    <input type="text" name="address" value="${address}" placeholder="Số nhà, đường, phường/xã..." required>
                </div>

                <div class="form-group">
                    <label>Mật khẩu *</label>
                    <input type="password" name="password" required>
                </div>

                <div class="form-group">
                    <label>Nhập lại mật khẩu *</label>
                    <input type="password" name="confirmPassword" required>
                </div>
                
                <div style="font-size: 13px; color: #666; margin-bottom: 20px;">
                    <input type="checkbox" required> Tôi đồng ý với Điều khoản dịch vụ và Chính sách bảo mật
                </div>

                <button type="submit" class="btn-submit">Tiếp tục</button>
                <a href="login.jsp" class="back-link">← Quay lại đăng nhập</a>
            </form>
        </div>

        <div class="auth-banner-side">
            <div class="banner-card">
                <img src="https://img.freepik.com/free-photo/medium-shot-man-getting-vaccine_23-2149230559.jpg" alt="Register Banner">
                <h3>Đăng ký dễ dàng</h3>
                <p style="font-size: 14px; color: #777;">Chỉ cần 2 bước đơn giản để tạo tài khoản và bắt đầu sử dụng dịch vụ.</p>
                <ul class="features">
                    <li><i class="fas fa-check-circle"></i> Đặt lịch khám nhanh chóng</li>
                    <li><i class="fas fa-check-circle"></i> Theo dõi lịch sử khám bệnh</li>
                    <li><i class="fas fa-check-circle"></i> Nhận thông báo nhắc nhở</li>
                </ul>
            </div>
        </div>
    </div>
</body>
</html>