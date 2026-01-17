<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Trang Chủ - Phòng khám ABC</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <style>
        /* CSS NHÚNG TRỰC TIẾP */
        :root { --primary: #0061ff; --bg: #f4f7fe; --text: #333; }
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; background: var(--bg); color: var(--text); }
        
        /* Header */
        header { display: flex; justify-content: space-between; align-items: center; padding: 15px 50px; background: white; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
        .logo { font-weight: bold; color: var(--primary); font-size: 18px; display: flex; align-items: center; gap: 10px; }
        .nav a { text-decoration: none; color: var(--text); margin: 0 20px; font-weight: 500; }
        .btn-header { text-decoration: none; background: var(--primary); color: white; padding: 8px 20px; border-radius: 5px; }
        
        /* Main Content */
        .welcome-container { height: calc(100vh - 70px); display: flex; justify-content: center; align-items: center; background: #fcfdff; }
        .welcome-card { background: white; padding: 50px; border-radius: 12px; text-align: center; box-shadow: 0 4px 20px rgba(0,0,0,0.05); max-width: 500px; border: 1px solid #eee; }
        .lock-icon { font-size: 40px; color: var(--primary); background: #e6f0ff; width: 80px; height: 80px; line-height: 80px; border-radius: 50%; margin: 0 auto 20px; }
        h2 { margin: 10px 0; font-size: 22px; }
        p { color: #666; margin-bottom: 30px; }
        .btn-large { display: inline-block; text-decoration: none; background: var(--primary); color: white; padding: 12px 40px; border-radius: 6px; font-weight: bold; transition: 0.3s; }
        .btn-large:hover { background: #0052d6; }
        .register-link { margin-top: 20px; font-size: 14px; }
        .register-link a { color: var(--primary); text-decoration: none; }
    </style>
</head>
<body>
    <header>
        <div class="logo"><i class="fas fa-heartbeat"></i> Phòng Khám ABC</div>
        <nav class="nav">
            <a href="#">Trang chủ</a>
            <a href="#">Đặt lịch</a>
            <a href="#">Tra cứu hồ sơ</a>
        </nav>
        <a href="login.jsp" class="btn-header">Đăng nhập</a>
    </header>

    <div class="welcome-container">
        <div class="welcome-card">
            <div class="lock-icon"><i class="fas fa-lock"></i></div>
            <h2>Chào mừng đến với Phòng Khám ABC</h2>
            <p>Vui lòng đăng nhập để truy cập hệ thống quản lý phòng khám</p>
            <a href="login.jsp" class="btn-large">Đăng nhập ngay</a>
            <div class="register-link">
                <a href="register.jsp">Hoặc đăng ký tài khoản mới</a>
            </div>
        </div>
    </div>
</body>
</html>