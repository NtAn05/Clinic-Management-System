<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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

    .site-header .header-inner {
        max-width: 1200px;
        margin: 0 auto;
        padding: 14px 20px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 18px;
    }

    .site-header .brand a {
        text-decoration: none;
        font-size: 20px;
        font-weight: 800;
        letter-spacing: 0.2px;
        color: #1f2937;
        display: flex;
        align-items: center;
        gap: 10px;
    }

    .site-header .brand-mark {
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

    .site-header .header-menu {
        display: flex;
        align-items: center;
        gap: 10px;
        flex-wrap: wrap;
        justify-content: flex-end;
    }

    .site-header .header-link {
        text-decoration: none;
        color: #334155;
        font-weight: 600;
        font-size: 14px;
        padding: 9px 14px;
        border-radius: 999px;
        transition: 0.25s ease;
    }

    .site-header .header-link:hover {
        background: #eaf2ff;
        color: #0055e6;
    }

    .site-header .header-link.primary {
        background: linear-gradient(135deg, #0061ff, #2d8cff);
        color: white;
        box-shadow: 0 8px 16px rgba(0, 97, 255, 0.25);
    }

    .site-header .header-link.primary:hover {
        background: linear-gradient(135deg, #0058e8, #2376d6);
        color: white;
    }

    .site-header .profile-menu-wrap {
        position: relative;
    }

    .site-header .profile-trigger {
        border: 1px solid #d8e5ff;
        background: #f1f5ff;
        color: #1e3a8a;
        border-radius: 999px;
        padding: 8px 12px;
        display: flex;
        align-items: center;
        gap: 8px;
        font-weight: 600;
        cursor: pointer;
    }

    .site-header .profile-avatar {
        width: 28px;
        height: 28px;
        border-radius: 50%;
        background: linear-gradient(135deg, #1d4ed8, #2563eb);
        color: #fff;
        font-size: 13px;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        font-weight: 700;
    }

    .site-header .profile-popup {
        position: absolute;
        top: calc(100% + 12px);
        right: 0;
        width: 280px;
        background: #fff;
        border: 1px solid #e6edff;
        border-radius: 14px;
        box-shadow: 0 18px 45px rgba(15, 44, 110, 0.18);
        padding: 10px;
        display: none;
    }

    .site-header .profile-popup.open {
        display: block;
    }

    .site-header .profile-popup-title {
        font-size: 12px;
        color: #64748b;
        text-transform: uppercase;
        letter-spacing: 0.35px;
        margin: 6px 8px;
    }

    .site-header .profile-item {
        display: flex;
        width: 100%;
        padding: 10px 12px;
        border-radius: 10px;
        text-decoration: none;
        color: #1f2937;
        font-size: 14px;
        font-weight: 600;
        margin-bottom: 4px;
        box-sizing: border-box;
    }

    .site-header .profile-item:hover {
        background: #eef4ff;
        color: #0b4ed4;
    }

    .site-header .profile-divider {
        border: 0;
        border-top: 1px solid #ebf1ff;
        margin: 8px 4px;
    }

    .site-header .btn-logout {
        color: #dc2626;
    }

    .site-header .btn-logout:hover {
        background: #fee2e2;
        color: #b91c1c;
    }


    .site-header .profile-trigger:hover {
        background: #e7efff;
    }

    .site-header .profile-trigger:focus {
        outline: none;
        box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.25);
    }

    @media (max-width: 900px) {
        .site-header .header-inner {
            padding: 10px 14px;
            flex-wrap: wrap;
        }

        .site-header .header-menu {
            width: 100%;
            justify-content: flex-start;
        }
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

                <div class="profile-menu-wrap" id="profileMenuWrap">
                    <button type="button" class="profile-trigger" id="profileTrigger">
                        <span class="profile-avatar">${fn:substring(sessionScope.account.fullName, 0, 1)}</span>
                        <span>${sessionScope.account.fullName}</span>
                    </button>

                    <div class="profile-popup" id="profilePopup">
                        <div class="profile-popup-title">Quản lý cá nhân</div>
                        <a class="profile-item" href="#">Tài khoản</a>
                        <a class="profile-item" href="#">Hồ sơ bệnh án</a>
                        <a class="profile-item" href="#">Lịch sử khám</a>
                        <a class="profile-item" href="#">Đơn thuốc</a>
                        <a class="profile-item" href="${pageContext.request.contextPath}/appointmentservlet">Thông tin đặt lịch</a>
                        <hr class="profile-divider" />
                        <a href="${pageContext.request.contextPath}/logout" class="profile-item btn-logout">Đăng xuất</a>
                    </div>
                </div>
            </c:if>
        </nav>
    </div>
</header>

<script>
    (function () {
        var trigger = document.getElementById('profileTrigger');
        var popup = document.getElementById('profilePopup');
        var wrap = document.getElementById('profileMenuWrap');

        if (!trigger || !popup || !wrap) {
            return;
        }

        trigger.addEventListener('click', function (event) {
            event.stopPropagation();
            popup.classList.toggle('open');
        });

        document.addEventListener('click', function (event) {
            if (!wrap.contains(event.target)) {
                popup.classList.remove('open');
            }
        });
    })();
</script>