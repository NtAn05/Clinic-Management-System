<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
<%
    String roleName = "";
    java.util.List<model.InAppNotification> headerNotifications = java.util.Collections.emptyList();
    int unreadNotificationCount = 0;
    if (session.getAttribute("account") != null) {
        model.User account = (model.User) session.getAttribute("account");
        Object r = account.getRole();
        roleName = r != null ? r.toString().toLowerCase() : "";
         if ("patient".equals(roleName)) {
            dal.NotificationDAO notificationDAO = new dal.NotificationDAO();
            headerNotifications = notificationDAO.getLatestNotifications(account.getUserId(), 5);
            unreadNotificationCount = notificationDAO.countUnreadNotifications(account.getUserId());
        }
    }
    pageContext.setAttribute("roleName", roleName);
    pageContext.setAttribute("headerNotifications", headerNotifications);
    pageContext.setAttribute("unreadNotificationCount", unreadNotificationCount);
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
        flex-wrap: nowrap;
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
        flex-wrap: nowrap;
        justify-content: flex-end;
        flex: 1;
        min-width: 0;
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
    
.site-header .notification-wrap {
        position: relative;
        margin-left: 5px;
    }

    .site-header .notification-btn {
        width: 42px;
        height: 42px;
        border-radius: 50%;
        border: none;
        background: #f1f5f9;
        color: #475569;
        cursor: pointer;
        font-size: 18px;
        position: relative;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.2s ease;
    }

    .site-header .notification-btn:hover {
        background: #e2e8f0;
        color: #0061ff;
    }

    .site-header .notification-badge {
        position: absolute;
        top: -2px;
        right: -2px;
        min-width: 18px;
        height: 18px;
        border-radius: 50px;
        background: #ef4444;
        color: #fff;
        font-size: 11px;
        font-weight: 800;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        padding: 0 4px;
        border: 2px solid #fff; /* Viền trắng cắt vào nền */
        box-shadow: 0 2px 4px rgba(239, 68, 68, 0.3);
    }

    .site-header .notification-popup {
        position: absolute;
        top: calc(100% + 12px);
        right: 0;
        width: 360px;
        background: #fff;
        border: 1px solid #e2e8f0;
        border-radius: 14px;
        box-shadow: 0 15px 40px rgba(0,0,0,0.12);
        display: none;
        z-index: 1002;
        overflow: hidden; /* Bo tròn viền trọn vẹn */
    }

    .site-header .notification-popup.open {
        display: block;
    }

    /* Phần Header của bảng thông báo */
    .site-header .notif-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 14px 16px;
        background: #f8fafc;
        border-bottom: 1px solid #e2e8f0;
    }
    
    .site-header .notif-header .title {
        font-weight: 700;
        color: #1e293b;
        font-size: 14px;
    }

    /* Phần Scroll chứa các items */
    .site-header .notif-body {
        max-height: 350px;
        overflow-y: auto;
    }
    
    /* Custom thanh cuộn */
    .site-header .notif-body::-webkit-scrollbar { width: 6px; }
    .site-header .notif-body::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
    .site-header .notif-body::-webkit-scrollbar-track { background: transparent; }

    .site-header .notification-item {
        display: flex;
        gap: 12px;
        padding: 14px 16px;
        background: #fff;
        border-bottom: 1px solid #f1f5f9;
        text-decoration: none;
        transition: background 0.2s;
        cursor: pointer;
    }

    .site-header .notification-item:last-child {
        border-bottom: none;
    }

    .site-header .notification-item:hover {
        background: #f8fafc;
    }

    /* Nổi bật khi chưa đọc */
    .site-header .notification-item.unread {
        background: #f0f7ff; 
    }
    .site-header .notification-item.unread .notification-title {
        color: #0061ff;
    }

    /* Icon đại diện cho mỗi thông báo */
    .site-header .notif-icon {
        width: 36px;
        height: 36px;
        border-radius: 50%;
        background: #e0e7ff;
        color: #0061ff;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 16px;
        flex-shrink: 0;
    }

    .site-header .notif-content {
        flex: 1;
    }

    .site-header .notification-title {
        font-weight: 600;
        color: #334155;
        font-size: 14px;
        margin-bottom: 4px;
    }

    .site-header .notification-message {
        color: #475569;
        font-size: 13px;
        line-height: 1.4;
    }

    .site-header .notification-time {
        color: #94a3b8;
        font-size: 11px;
        margin-top: 6px;
        display: flex;
        align-items: center;
        gap: 4px;
    }

    .site-header .notification-empty {
        padding: 30px 15px;
        text-align: center;
        color: #94a3b8;
        font-size: 14px;
    }
    @media (max-width: 900px) {
        .site-header .header-inner {
            padding: 10px 14px;
            flex-wrap: nowrap;
        }

        .site-header .header-menu {
            justify-content: flex-end;
        }
    }
</style>

<header class="site-header">
    <div class="header-inner">
        <div class="brand">
            <a href="${pageContext.request.contextPath}/home">
                <span class="brand-mark">❤</span>
                <span>Phòng Khám ABC</span>
            </a>
        </div>

        <nav class="header-menu">
            <a class="header-link" href="${pageContext.request.contextPath}/home">Trang chủ</a>

            <c:if test="${sessionScope.account == null}">
                <a class="header-link" href="${pageContext.request.contextPath}/pages/auth/login.jsp">Đăng nhập</a>
                <a class="header-link primary" href="${pageContext.request.contextPath}/pages/auth/register.jsp">Đăng ký</a>
            </c:if>

            <c:if test="${sessionScope.account != null}">

                <c:if test="${roleName == 'admin'}">
                    <a class="header-link" href="${pageContext.request.contextPath}/admin-users">Quản lý tài khoản</a>
                    <a class="header-link" href="${pageContext.request.contextPath}/admin-services">Quản lý dịch vụ</a>
<!--                    <a class="header-link" href="${pageContext.request.contextPath}/admin-doctors">Quản lý bác sĩ</a>-->
                    <a class="header-link" href="${pageContext.request.contextPath}/admin-doctor-schedules">Lịch làm việc bác sĩ</a>
                    <a class="header-link" href="${pageContext.request.contextPath}/admin-system-logs">Nhật ký hệ thống</a>
                    <a class="header-link" href="${pageContext.request.contextPath}/admin-reports">Báo cáo phòng khám</a>
                </c:if>

                <c:if test="${roleName == 'doctor'}">
                    <a class="header-link" href="${pageContext.request.contextPath}/doctorDashboard">Quản lý khám bệnh</a>
                    <a class="header-link" href="${pageContext.request.contextPath}/doctor/schedule-request">Lịch làm việc & đổi lịch</a>
                </c:if>

                <c:if test="${roleName == 'technician'}">
                    <a class="header-link" href="${pageContext.request.contextPath}/technician-dashboard">Quản lý xét nghiệm</a>
                </c:if>

                <c:if test="${roleName == 'receptionist'}">
                    <a class="header-link" href="${pageContext.request.contextPath}/lab-payment">Quản lý tiếp nhận</a>
                    <a class="header-link" href="${pageContext.request.contextPath}/listofappointment">Quản lý cuộc hẹn</a>
                </c:if>

                <c:if test="${roleName == 'patient'}">
                    <a class="header-link" href="${pageContext.request.contextPath}/listofdoctorservlet">Đặt lịch khám</a>
                    <div class="notification-wrap" id="notificationWrap">
                        <button type="button" class="notification-btn" id="notificationTrigger" aria-label="Thông báo">
                            <i class="fas fa-bell"></i>
                            <c:if test="${unreadNotificationCount > 0}">
                                <span class="notification-badge">${unreadNotificationCount > 99 ? '99+' : unreadNotificationCount}</span>
                            </c:if>
                        </button>
                        
                        <div class="notification-popup" id="notificationPopup">
                            <div class="notif-header">
                                <div class="title">Thông báo của bạn</div>
                            </div>
                            
                            <div class="notif-body">
                                <c:choose>
                                    <c:when test="${empty headerNotifications}">
                                        <div class="notification-empty">
                                            <i class="fas fa-box-open" style="font-size: 24px; color: #cbd5e1; margin-bottom: 8px; display: block;"></i>
                                            Chưa có thông báo mới.
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="n" items="${headerNotifications}">
                                            <div class="notification-item ${!n.read ? 'unread' : ''}">
                                                <div class="notif-icon">
                                                    <i class="fas fa-notes-medical"></i>
                                                </div>
                                                <div class="notif-content">
                                                    <div class="notification-title"><c:out value="${n.title}"/></div>
                                                    <div class="notification-message"><c:out value="${n.message}"/></div>
                                                    <div class="notification-time">
                                                        <i class="far fa-clock"></i> <c:out value="${n.createdAt}"/>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </c:if>
                     


                <div class="profile-menu-wrap" id="profileMenuWrap">
                    <button type="button" class="profile-trigger" id="profileTrigger">
                        <span class="profile-avatar">${fn:substring(sessionScope.account.fullName, 0, 1)}</span>
                        <span>${sessionScope.account.fullName}</span>
                    </button>

                    <div class="profile-popup" id="profilePopup">
                        <div class="profile-popup-title">Quản lý cá nhân</div>

                        <a class="profile-item" href="${pageContext.request.contextPath}/userinformationservlet">Tài khoản của tôi</a>

                        <c:if test="${roleName == 'patient'}">
                            <a class="profile-item" href="${pageContext.request.contextPath}/patient-medical-records">Hồ sơ bệnh án</a>
                            <a class="profile-item" href="${pageContext.request.contextPath}/patient-prescription">Đơn thuốc</a>
                            <a class="profile-item" href="${pageContext.request.contextPath}/historyofappointmentservlet">Thông tin đặt lịch</a>
                            <a class="profile-item" href="${pageContext.request.contextPath}/createpatientsservlet">Danh sách bệnh nhân</a>
                        </c:if>

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
        var notificationTrigger = document.getElementById('notificationTrigger');
        var notificationPopup = document.getElementById('notificationPopup');
        var notificationWrap = document.getElementById('notificationWrap');
        if (!trigger || !popup || !wrap) {
            return;
        }

        trigger.addEventListener('click', function (event) {
            event.stopPropagation();
            popup.classList.toggle('open');
            if (notificationPopup) {
                notificationPopup.classList.remove('open');
            }
        });
        if (notificationTrigger && notificationPopup && notificationWrap) {
            notificationTrigger.addEventListener('click', function (event) {
                event.stopPropagation();
                notificationPopup.classList.toggle('open');
                popup.classList.remove('open');
            });
        }


        document.addEventListener('click', function (event) {
            if (!wrap.contains(event.target)) {
                popup.classList.remove('open');
            }
            if (notificationWrap && !notificationWrap.contains(event.target)) {
                notificationPopup.classList.remove('open');
            }
        });
    })();
</script>
