<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.User"%>

<%
    // Kiểm tra đăng nhập
    if (session.getAttribute("account") == null) {
        response.sendRedirect("pages/auth/login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Hệ thống quản lý</title>
        <style>
            body {
                font-family: 'Segoe UI', sans-serif;
                background: #f4f7fe;
                padding: 50px;
                display: flex;
                justify-content: center;
            }
            .dashboard-card {
                background: white;
                padding: 40px;
                border-radius: 12px;
                box-shadow: 0 5px 20px rgba(0,0,0,0.1);
                width: 500px;
                text-align: center;
            }
            h1 { color: #0061ff; }
            .role-badge {
                display: inline-block;
                padding: 5px 15px;
                border-radius: 20px;
                font-size: 14px;
                font-weight: bold;
                margin-top: 10px;
            }
            .role-admin { background: #000; color: #fff; }
            .role-patient { background: #e3f2fd; color: #0d47a1; } 
            .role-doctor { background: #e8f5e9; color: #1b5e20; }
            .role-staff { background: #fff3e0; color: #e65100; }    

            .btn-logout {
                display: inline-block;
                margin-top: 20px;
                padding: 10px 30px;
                background: #ff4757;
                color: white;
                text-decoration: none;
                border-radius: 6px;
            }
            .btn-logout:hover { background: #e84118; }
        </style>
    </head>
    <body>
        <div class="dashboard-card">
            <h1>Đăng nhập thành công!</h1>
            <p>Xin chào, <strong>${sessionScope.account.fullName}</strong></p>

            <div class="role-badge ${sessionScope.account.role == 'admin' ? 'role-admin' : 
                                    (sessionScope.account.role == 'patient' ? 'role-patient' : 
                                    (sessionScope.account.role == 'doctor' ? 'role-doctor' : 'role-staff'))}">
                
                Vai trò: 
                ${sessionScope.account.role == 'admin' ? "Quản Trị Viên (Admin)" : 
                 (sessionScope.account.role == 'patient' ? "Bệnh nhân" : 
                 (sessionScope.account.role == 'doctor' ? "Bác sĩ" : "Nhân viên"))}
            </div>

            <hr style="border: 0; border-top: 1px solid #eee; margin: 20px 0;">
            
            <div style="text-align: left; margin-bottom: 20px;">
                <% 
                    // PHẦN 2: LOGIC HIỂN THỊ MENU (SỬA LẠI JAVA CODE)
                    User u = (User)session.getAttribute("account");
                    String role = u.getRole().name();
                    if(role.equals("admin")) { 
                %>
                    <p>⭐️ <a href="${pageContext.request.contextPath}/admin-users">Quản lý Nhân sự (Bác sĩ/Nhân viên)</a></p>
                    <p>⭐️ <a href="${pageContext.request.contextPath}/service-manager">Quản lý Dịch vụ & Giá tiền</a></p>
                    <p>⭐️ <a href="#">Quản lý Lịch làm việc</a></p>

                <% } else if(role.equals("patient")) { %>
                    <p>👉 <a href="#">Đặt lịch khám mới</a></p>
                    <p>👉 <a href="#">Xem hồ sơ bệnh án</a></p>
                    
                <% } else if(role.equals("doctor")) { %>
                    <p>👉 <a href="#">Danh sách bệnh nhân</a></p>
                    <p>👉 <a href="#">Lịch làm việc cá nhân</a></p>
                    
                <% } else {  %>
                    <p>👉 <a href="#">Tiếp đón / Thu ngân</a></p>
                <% } %>
            </div>

            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Đăng xuất</a>
        </div>
    </body>
</html>