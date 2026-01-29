<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style>
    :root {
        --sidebar-width: 260px;
        --sidebar-bg: #1e293b;
        --sidebar-text: #e2e8f0;
        --sidebar-active: #2563eb;
    }

    /* Sidebar */
    .sidebar {
        width: var(--sidebar-width);
        background: var(--sidebar-bg);
        color: var(--sidebar-text);
        padding: 20px 0;
        position: fixed;
        height: calc(100vh - 60px);
        overflow-y: auto;
        top: 60px;
        left: 0;
        z-index: 100;
    }

    .sidebar-header {
        padding: 0 20px 20px;
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        margin-bottom: 20px;
    }

    .sidebar-title {
        font-size: 14px;
        font-weight: 600;
        color: #94a3b8;
        text-transform: uppercase;
        letter-spacing: 0.5px;
    }

    .sidebar-menu {
        list-style: none;
        padding: 0;
    }

    .sidebar-menu-item {
        margin: 4px 0;
    }

    .sidebar-menu-link {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 12px 20px;
        color: var(--sidebar-text);
        text-decoration: none;
        transition: all 0.2s;
        border-left: 3px solid transparent;
    }

    .sidebar-menu-link:hover {
        background: rgba(255, 255, 255, 0.05);
        border-left-color: var(--sidebar-active);
    }

    .sidebar-menu-link.active {
        background: rgba(37, 99, 235, 0.15);
        border-left-color: var(--sidebar-active);
        color: #fff;
    }

    .sidebar-menu-link i {
        width: 20px;
        text-align: center;
        font-size: 16px;
    }

    /* Responsive */
    @media (max-width: 768px) {
        .sidebar {
            transform: translateX(-100%);
            transition: transform 0.3s;
        }

        .sidebar.open {
            transform: translateX(0);
        }
    }
</style>

<!-- Sidebar -->
<aside class="sidebar">
    <div class="sidebar-header">
        <div class="sidebar-title">Menu</div>
    </div>
    <ul class="sidebar-menu">
        <c:choose>
            <c:when test="${sessionScope.account.role.name() == 'technician'}">
                <!-- Technician Menu -->
                <li class="sidebar-menu-item">
                    <a href="${pageContext.request.contextPath}/lab-queue" 
                       class="sidebar-menu-link ${pageContext.request.requestURI.contains('lab-queue') ? 'active' : ''}">
                        <i class="fas fa-list"></i>
                        <span>Xem danh sách yêu cầu xét nghiệm</span>
                    </a>
                </li>
            </c:when>
            <c:when test="${sessionScope.account.role.name() == 'doctor'}">
                <!-- Doctor Menu -->
                <li class="sidebar-menu-item">
                    <a href="${pageContext.request.contextPath}/DoctorDashboardServlet" 
                       class="sidebar-menu-link ${pageContext.request.requestURI.contains('DoctorDashboardServlet') ? 'active' : ''}">
                        <i class="fas fa-user-md"></i>
                        <span>Danh sách bệnh nhân</span>
                    </a>
                </li>
            </c:when>
            <c:when test="${sessionScope.account.role.name() == 'admin'}">
                <!-- Admin Menu -->
                <li class="sidebar-menu-item">
                    <a href="${pageContext.request.contextPath}/admin-users" 
                       class="sidebar-menu-link ${pageContext.request.requestURI.contains('admin-users') ? 'active' : ''}">
                        <i class="fas fa-users-cog"></i>
                        <span>Quản lý người dùng</span>
                    </a>
                </li>
            </c:when>
            <c:when test="${sessionScope.account.role.name() == 'receptionist'}">
                <!-- Receptionist Menu -->
                <li class="sidebar-menu-item">
                    <a href="${pageContext.request.contextPath}/receptionist-dashboard" 
                       class="sidebar-menu-link">
                        <i class="fas fa-clipboard-check"></i>
                        <span>Tiếp đón / Thu ngân</span>
                    </a>
                </li>
            </c:when>
        </c:choose>
    </ul>
</aside>
