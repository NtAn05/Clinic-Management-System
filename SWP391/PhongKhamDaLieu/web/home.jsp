<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    if (session.getAttribute("account") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Hệ thống quản lý</title>
    <style>
        body { font-family: 'Segoe UI', sans-serif; background: #f4f7fe; padding: 50px; display: flex; justify-content: center; }
        .dashboard-card { background: white; padding: 40px; border-radius: 12px; box-shadow: 0 5px 20px rgba(0,0,0,0.1); width: 500px; text-align: center; }
        h1 { color: #0061ff; }
        .role-badge {
            display: inline-block;
            padding: 5px 15px;
            border-radius: 20px;
            font-size: 14px;
            font-weight: bold;
            margin-top: 10px;
        }
        /* Màu sắc cho từng vai trò */
        .role-patient { background: #e3f2fd; color: #0d47a1; } /* Xanh dương */
        .role-doctor { background: #e8f5e9; color: #1b5e20; }  /* Xanh lá */
        .role-staff { background: #fff3e0; color: #e65100; }   /* Cam */
        
        .btn-logout { display: inline-block; margin-top: 20px; padding: 10px 30px; background: #ff4757; color: white; text-decoration: none; border-radius: 6px; }
        .btn-logout:hover { background: #e84118; }
    </style>
</head>
<body>
    <div class="dashboard-card">
        <h1>Đăng nhập thành công!</h1>
        <p>Xin chào, <strong>${sessionScope.account.fullName}</strong></p>
        
        <div class="role-badge ${sessionScope.account.roleId == 1 ? 'role-patient' : (sessionScope.account.roleId == 2 ? 'role-doctor' : 'role-staff')}">
            Vai trò: 
            ${sessionScope.account.roleId == 1 ? "Bệnh nhân" : 
             (sessionScope.account.roleId == 2 ? "Bác sĩ" : "Nhân viên")}
        </div>

        <hr style="border: 0; border-top: 1px solid #eee; margin: 20px 0;">
        
        <div style="text-align: left; margin-bottom: 20px;">
            <% 
                // Lấy RoleID ra để check bằng Java cho dễ
                model.User u = (model.User)session.getAttribute("account");
                if(u.getRoleId() == 1) { 
            %>
                <p>👉 <a href="#">Đặt lịch khám mới</a></p>
                <p>👉 <a href="#">Xem hồ sơ bệnh án</a></p>
            <% } else if(u.getRoleId() == 2) { %>
                <p>👉 <a href="#">Danh sách bệnh nhân chờ khám</a></p>
                <p>👉 <a href="#">Lịch làm việc tuần này</a></p>
            <% } else if(u.getRoleId() == 3) { %>
                <p>👉 <a href="#">Quản lý tiếp đón</a></p>
                <p>👉 <a href="#">Thu ngân / Hóa đơn</a></p>
            <% } %>
        </div>

        <a href="logout" class="btn-logout">Đăng xuất</a>
    </div>
</body>
</html>