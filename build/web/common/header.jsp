<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    body {
        font-family: sans-serif;
        margin: 0;
        padding: 0;
        background: #f4f7fe;
    }
    .header {
        background: white;
        padding: 15px 40px;
        border-bottom: 1px solid #ddd;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
    .logo a {
        font-weight: bold;
        font-size: 20px;
        color: #0061ff;
        text-decoration: none;
    }
    .menu a {
        text-decoration: none;
        color: #333;
        margin-left: 20px;
        font-weight: 500;
    }
    .menu a:hover {
        color: #0061ff;
    }
    .btn-logout {
        color: red !important;
    }
</style>

<div class="header">
    <div class="logo">
        <a href="index.jsp">PHÒNG KHÁM ABC</a>
    </div>

    <div class="menu">
        <a href="index.jsp">Trang chủ</a>

        <c:if test="${sessionScope.account == null}">
            <a href="login.jsp">Đăng nhập</a>
            <a href="register.jsp" style="background: #0061ff; color: white; padding: 8px 15px; border-radius: 20px;">Đăng ký</a>
        </c:if>

        <c:if test="${sessionScope.account != null}">
            <c:if test="${sessionScope.account.roleId == 0}">
                <a href="admin-users">Quản lý</a>
            </c:if>

            <c:if test="${sessionScope.account.roleId == 2}">
                <a href="doctor-schedule.jsp">Lịch làm việc</a>
            </c:if>

            <span style="margin-left: 15px; color: #666;">Hi, ${sessionScope.account.fullName}</span>
            <a href="logout" class="btn-logout">(Thoát)</a>
        </c:if>
    </div>
</div>