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
        font-family: 'Segoe UI', sans-serif;
        margin: 0;
        padding: 0;
        padding-top: 78px;
        background: #f4f7fe;
    }

    .site-header {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        z-index: 1000;
        background: rgba(255, 255, 255, 0.95);
        backdrop-filter: blur(10px);
        border-bottom: 1px solid #e9efff;
        box-shadow: 0 10px 25px rgba(5, 33, 88, 0.08);
    }

    .header-inner {
        max-width: 1200px;
        margin: 0 auto;
        padding: 14px 20px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 18px;
    }

    .brand a {
        text-decoration: none;
        font-size: 20px;
        font-weight: 800;
        letter-spacing: 0.2px;
        color: #1f2937;
        display: flex;
        align-items: center;
        gap: 10px;
    }

    .brand-mark {
        width: 34px;
        height: 34px;
        border-radius: 10px;
        background: linear-gradient(135deg, #0061ff, #2ca8ff);
        color: white;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        font-size: 14px;
        box-shadow: 0 8px 18px rgba(0, 97, 255, 0.28);
    }

    .header-menu {
        display: flex;
        align-items: center;
        gap: 10px;
        flex-wrap: wrap;
        justify-content: flex-end;
    }

    .header-link {
        text-decoration: none;
        color: #334155;
        font-weight: 600;
        font-size: 14px;
        padding: 9px 14px;
        border-radius: 999px;
        transition: 0.25s ease;
    }

    .header-link:hover {
        background: #eaf2ff;
        color: #0055e6;
    }

    .header-link.primary {
        background: linear-gradient(135deg, #0061ff, #2d8cff);
        color: white;
        box-shadow: 0 8px 16px rgba(0, 97, 255, 0.25);
    }

    .header-link.primary:hover {
        background: linear-gradient(135deg, #0058e8, #2376d6);
        color: white;
    }

    .user-pill {
        padding: 8px 14px;
        border-radius: 999px;
        background: #f1f5ff;
        color: #1e3a8a;
        font-weight: 600;
        font-size: 13px;
        margin-left: 10px;
    }

    .btn-logout {
        color: #dc2626 !important;
    }
    
    .btn-logout:hover {
        background: #fee2e2;
        color: #b91c1c !important;
    }
</style>

<header class="site-header">
    <div class="header-inner">
        <div class="brand">
            <a href="${pageContext.request.contextPath}/index.jsp">
                <span class="brand-mark">❤</span>
                <span>Phòng Khám ABC</span>
            </a>
        </div>

        <nav class="header-menu">
            <a class="header-link" href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a>

            <c:if test="${sessionScope.account == null}">
                <a class="header-link" href="${pageContext.request.contextPath}/pages/auth/login.jsp">Đăng nhập</a>
                <a class="header-link primary" href="${pageContext.request.contextPath}/pages/auth/register.jsp">Đăng ký</a>
            </c:if>

            <c:if test="${sessionScope.account != null}">
                <c:if test="${roleName == 'admin'}">
                    <a class="header-link" href="${pageContext.request.contextPath}/admin-users">Quản lý tài khoản</a>
                    <a class="header-link" href="${pageContext.request.contextPath}/admin-services">Quản lý dịch vụ</a>
                </c:if>

                <c:if test="${roleName == 'doctor'}">
                    <a class="header-link" href="${pageContext.request.contextPath}/doctorDashboard">Dashboard</a>
                </c:if>

                <c:if test="${roleName == 'technician'}">
                    <a class="header-link" href="${pageContext.request.contextPath}/technician-dashboard">Dashboard</a>
                </c:if>

                <c:if test="${roleName == 'patient'}">
                    <a class="header-link" href="${pageContext.request.contextPath}/listofdoctorservlet">Đặt lịch khám</a>
                </c:if>

                <span class="user-pill">Xin chào, ${sessionScope.account.fullName}</span>
                <a href="${pageContext.request.contextPath}/logout" class="header-link btn-logout">Đăng xuất</a>
            </c:if>
        </nav>
    </div>
</header>