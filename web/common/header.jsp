<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String roleName = "";
    if (session.getAttribute("account") != null) {
        Object r = ((model.User) session.getAttribute("account")).getRole();
        roleName = r != null ? r.toString().toLowerCase() : "";
    }
    pageContext.setAttribute("roleName", roleName);
%>
<style>
    body {
        font-family: sans-serif;
        margin: 0;
        padding: 0;
        padding-top: 60px; /* Space for fixed header */
        background: #f4f7fe;
    }
    .header {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        background: white;
        padding: 15px 40px;
        border-bottom: 1px solid #ddd;
        display: flex;
        justify-content: space-between;
        align-items: center;
        z-index: 1000;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
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
        <a href="${pageContext.request.contextPath}/index.jsp">PHÒNG KHÁM ABC</a>
    </div>

    <div class="menu">
        <a href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a>

        <c:if test="${sessionScope.account == null}">
            <a href="pages/auth/login.jsp">Đăng nhập</a>
            <a href="pages/auth/register.jsp" style="background: #0061ff; color: white; padding: 8px 15px; border-radius: 20px;">Đăng ký</a>
        </c:if>

        <c:if test="${sessionScope.account != null}">
            <c:if test="${roleName == 'admin'}">
                <a href="${pageContext.request.contextPath}/admin-users">Quản lý</a>
            </c:if>

            <c:if test="${roleName == 'doctor'}">
                <a href="${pageContext.request.contextPath}/doctor-schedule.jsp">Lịch làm việc</a>
            </c:if>

            <c:if test="${roleName == 'technician'}">
                <a href="${pageContext.request.contextPath}/technician-dashboard">Dashboard</a>
            </c:if>

            <span style="margin-left: 15px; color: #666;">Hi, ${sessionScope.account.fullName}</span>
            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">(Thoát)</a>
        </c:if>
    </div>
</div>