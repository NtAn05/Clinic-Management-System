<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý Tài khoản</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
                min-height: 100vh;
            }

            .container {
                padding: 30px 50px;
                max-width: 1400px;
                margin: 0 auto;
            }

            .alert {
                padding: 15px 20px;
                border-radius: 8px;
                margin-bottom: 20px;
                display: flex;
                align-items: center;
                gap: 10px;
                animation: slideIn 0.3s ease-out;
            }

            .alert.success {
                background: #e8f5e9;
                color: #2e7d32;
                border-left: 4px solid #4caf50;
            }

            .alert.error {
                background: #ffebee;
                color: #c62828;
                border-left: 4px solid #f44336;
            }

            @keyframes slideIn {
                from {
                    transform: translateY(-20px);
                    opacity: 0;
                }
                to {
                    transform: translateY(0);
                    opacity: 1;
                }
            }

            /* TAB NAVIGATION */
            .tab-navigation {
                display: flex;
                gap: 0;
                margin-bottom: 30px;
                background: white;
                border-radius: 10px;
                overflow: hidden;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            }

            .tab-button {
                flex: 1;
                padding: 18px 20px;
                text-align: center;
                background: #f5f5f5;
                border: none;
                cursor: pointer;
                font-size: 15px;
                font-weight: 600;
                color: #666;
                transition: all 0.3s ease;
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 8px;
                border-bottom: 3px solid transparent;
            }

            .tab-button:hover {
                background: #e8e8e8;
            }

            .tab-button.active {
                background: #0061ff;
                color: white;
                border-bottom-color: #0061ff;
            }

            .tab-content {
                display: none;
            }

            .tab-content.active {
                display: block;
            }

            /* TOOLBAR */
            .toolbar {
                background: white;
                padding: 20px;
                border-radius: 10px;
                margin-bottom: 20px;
                display: grid;
                grid-template-columns: repeat(3, minmax(220px, 1fr)) auto;
                gap: 12px;
                align-items: end;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            }

            .toolbar-staff {
                grid-template-columns: minmax(340px, 1.9fr) minmax(170px, 0.85fr) minmax(170px, 0.85fr) auto;
            }

            .toolbar-patient {
                grid-template-columns: minmax(360px, 2fr) minmax(220px, 1fr) auto;
            }

            .search-box {
                min-width: 0;
            }

            .search-box label {
                display: block;
                font-weight: 600;
                margin-bottom: 8px;
                color: #333;
                font-size: 13px;
            }

            .search-box input {
                width: 100%;
                padding: 10px 15px;
                border: 1px solid #ddd;
                border-radius: 6px;
                font-size: 14px;
                transition: all 0.3s ease;
            }

            .search-box input:focus {
                outline: none;
                border-color: #0061ff;
                box-shadow: 0 0 0 3px rgba(0, 97, 255, 0.1);
            }

            .filter-box {
                min-width: 0;
            }

            .filter-box label {
                display: block;
                font-weight: 600;
                margin-bottom: 8px;
                color: #333;
                font-size: 13px;
            }

            .filter-box select {
                width: 100%;
                padding: 10px 15px;
                border: 1px solid #ddd;
                border-radius: 6px;
                font-size: 14px;
                cursor: pointer;
                background: white;
                transition: all 0.3s ease;
            }

            .filter-box select:focus {
                outline: none;
                border-color: #0061ff;
                box-shadow: 0 0 0 3px rgba(0, 97, 255, 0.1);
            }

            .toolbar-buttons {
                display: flex;
                gap: 10px;
                align-self: end;
            }

            .btn-search, .btn-reset, .btn-add {
                padding: 10px 16px;
                border: none;
                border-radius: 6px;
                cursor: pointer;
                font-weight: 600;
                font-size: 14px;
                transition: all 0.3s ease;
                display: flex;
                align-items: center;
                gap: 6px;
            }

            .btn-search {
                background: #0061ff;
                color: white;
            }

            .btn-search:hover {
                background: #0052cc;
                transform: translateY(-2px);
                box-shadow: 0 4px 12px rgba(0, 97, 255, 0.3);
            }

            .btn-reset {
                background: #f0f0f0;
                color: #333;
            }

            .btn-reset:hover {
                background: #e0e0e0;
            }

            .btn-add {
                background: #4caf50;
                color: white;
            }

            .btn-add:hover {
                background: #45a049;
                transform: translateY(-2px);
                box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
            }

            /* TABLE */
            .table-container {
                background: white;
                padding: 25px;
                border-radius: 10px;
                box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
                overflow-x: auto;
            }

            table {
                width: 100%;
                border-collapse: collapse;
            }

            th {
                background: linear-gradient(135deg, #f8f9fa 0%, #f0f0f0 100%);
                padding: 15px;
                text-align: left;
                font-weight: 600;
                color: #333;
                border-bottom: 2px solid #e0e0e0;
                font-size: 14px;
            }

            td {
                padding: 15px;
                border-bottom: 1px solid #f0f0f0;
                color: #555;
                font-size: 14px;
            }

            tr:hover {
                background: #f9f9f9;
            }

            .badge {
                display: inline-block;
                padding: 6px 12px;
                border-radius: 20px;
                font-size: 12px;
                font-weight: 600;
                white-space: nowrap;
            }

            .badge-admin {
                background: #ffebee;
                color: #c62828;
            }

            .badge-doctor {
                background: #e3f2fd;
                color: #1976d2;
            }

            .badge-receptionist {
                background: #fff3e0;
                color: #f57c00;
            }

            .badge-technician {
                background: #f3e5f5;
                color: #7b1fa2;
            }

            .badge-patient {
                background: #e1f5fe;
                color: #0277bd;
            }

            .badge-active {
                background: #e8f5e9;
                color: #388e3c;
            }

            .badge-inactive {
                background: #ffebee;
                color: #d32f2f;
            }

            .action-buttons {
                display: flex;
                flex-wrap: wrap;
            }

            .btn-action {
                border: none;
                background: none;
                cursor: pointer;
                font-size: 16px;
                padding: 6px 10px;
                border-radius: 4px;
                transition: all 0.3s ease;
                display: flex;
                align-items: center;
                gap: 4px;
            }

            .btn-view {
                color: #1976d2;
            }

            .btn-view:hover {
                background: #e3f2fd;
            }

            .btn-edit {
                color: #f57c00;
            }

            .btn-edit:hover {
                background: #fff3e0;
            }

            .btn-toggle {
                color: #7b1fa2;
            }

            .btn-toggle:hover {
                background: #f3e5f5;
            }

            .no-data {
                text-align: center;
                padding: 40px;
                color: #999;
            }

            .no-data i {
                font-size: 48px;
                margin-bottom: 15px;
                opacity: 0.5;
            }

            /* MODAL */
            .modal {
                display: none;
                position: fixed;
                z-index: 1000;
                left: 0;
                top: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0, 0, 0, 0.5);
                animation: fadeIn 0.3s ease;
                overflow-y: auto;
            }

            @keyframes fadeIn {
                from {
                    opacity: 0;
                }
                to {
                    opacity: 1;
                }
            }

            .modal-content {
                background-color: white;
                margin: 5% auto;
                padding: 30px;
                border-radius: 10px;
                width: 90%;
                max-width: 550px;
                box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
                animation: slideUp 0.3s ease;
            }

            @keyframes slideUp {
                from {
                    transform: translateY(50px);
                    opacity: 0;
                }
                to {
                    transform: translateY(0);
                    opacity: 1;
                }
            }

            .modal-header {
                font-size: 20px;
                font-weight: 600;
                color: #0061ff;
                margin-bottom: 25px;
                display: flex;
                align-items: center;
                gap: 10px;
                border-bottom: 2px solid #f0f0f0;
                padding-bottom: 15px;
            }

            .modal-close {
                margin-left: auto;
                cursor: pointer;
                font-size: 24px;
                line-height: 1;
                background: none;
                border: none;
                color: #999;
                transition: all 0.3s ease;
            }

            .modal-close:hover {
                color: #333;
            }

            .form-group {
                margin-bottom: 18px;
            }

            .form-group label {
                display: block;
                font-weight: 600;
                margin-bottom: 8px;
                color: #333;
                font-size: 14px;
            }

            .form-group input,
            .form-group select,
            .form-group textarea {
                width: 100%;
                padding: 10px 15px;
                border: 1px solid #ddd;
                border-radius: 6px;
                font-size: 14px;
                font-family: inherit;
                transition: all 0.3s ease;
            }

            .form-group input:focus,
            .form-group select:focus,
            .form-group textarea:focus {
                outline: none;
                border-color: #0061ff;
                box-shadow: 0 0 0 3px rgba(0, 97, 255, 0.1);
            }

            .form-group textarea {
                resize: vertical;
                min-height: 80px;
            }

            .field-error {
                color: #d32f2f;
                font-size: 12px;
                margin-top: 6px;
                font-weight: 500;
            }

            .modal-footer {
                display: flex;
                gap: 10px;
                justify-content: flex-end;
                margin-top: 25px;
                padding-top: 15px;
                border-top: 1px solid #f0f0f0;
            }

            .btn-cancel {
                padding: 10px 20px;
                background: #f0f0f0;
                color: #333;
                border: none;
                border-radius: 6px;
                cursor: pointer;
                font-weight: 600;
                transition: all 0.3s ease;
                display: flex;
                align-items: center;
                gap: 6px;
            }

            .btn-cancel:hover {
                background: #e0e0e0;
            }

            .btn-submit {
                padding: 10px 20px;
                background: #0061ff;
                color: white;
                border: none;
                border-radius: 6px;
                cursor: pointer;
                font-weight: 600;
                transition: all 0.3s ease;
                display: flex;
                align-items: center;
                gap: 6px;
            }

            .btn-submit:hover {
                background: #0052cc;
            }

            .form-info {
                background: #f5f7fa;
                padding: 15px;
                border-radius: 6px;
                margin-bottom: 15px;
                font-size: 14px;
                color: #555;
                line-height: 1.6;
            }

            .form-info-item {
                display: flex;
                justify-content: space-between;
                padding: 8px 0;
                border-bottom: 1px solid #e0e0e0;
            }

            .form-info-item:last-child {
                border-bottom: none;
            }

            .form-info-item strong {
                color: #333;
            }

            @media (max-width: 768px) {
                .container {
                    padding: 20px;
                }

                .toolbar {
                    grid-template-columns: 1fr;
                }

                .search-box,
                .filter-box {
                    width: 100%;
                    min-width: unset;
                }

                .toolbar-buttons {
                    justify-content: stretch;
                    width: 100%;
                }

                .btn-add {
                    margin-left: 0;
                }

                .toolbar-buttons .btn-search,
                .toolbar-buttons .btn-reset,
                .toolbar-buttons .btn-add {
                    flex: 1;
                    justify-content: center;
                }

                table {
                    font-size: 13px;
                }

                th, td {
                    padding: 10px;
                }
            }

            /* paging */
            .pagination-wrapper {
                margin-top: 16px;
                display: flex;
                justify-self: center;
                gap: 8px;
                flex-wrap: wrap;
            }
            .page-link {
                min-width: 34px;
                padding: 8px 12px;
                border: 1px solid #dcdcdc;
                border-radius: 6px;
                text-decoration: none;
                color: #333;
                background: #fff;
                text-align: center;
            }
            .page-link:hover {
                background: #f5f5f5;
            }
            .page-link.active {
                background: #0061ff;
                color: #fff;
                border-color: #0061ff;
                pointer-events: none;
            }
            .page-link.disabled {
                opacity: .5;
                pointer-events: none;
            }
        </style>
    </head>
    <body>
        <jsp:include page="/common/header.jsp" />

        <div class="container">
            <!-- THÔNG BÁO THÀNH CÔNG -->
            <c:if test="${not empty success}">
                <div class="alert success">
                    <i class="fas fa-check-circle"></i>
                    ${success}
                </div>
            </c:if>

            <!-- THÔNG BÁO LỖI -->
            <c:if test="${not empty error and not addModalOpen and not editModalOpen}">
                <div class="alert error">
                    <i class="fas fa-exclamation-circle"></i>
                    ${error}
                </div>
            </c:if>

            <!-- TAB NAVIGATION -->
            <div class="tab-navigation">
                <button class="tab-button ${empty currentTab or currentTab == 'staff' ? 'active' : ''}" onclick="switchTab(event, 'staff')">
                    <i class="fas fa-users"></i> Tài khoản Nhân viên
                </button>
                <button class="tab-button ${currentTab == 'patient' ? 'active' : ''}" onclick="switchTab(event, 'patient')">
                    <i class="fas fa-user-md"></i> Tài khoản Bệnh nhân
                </button>
            </div>

            <!-- TAB 1: NHÂN VIÊN -->
            <div id="staff" class="tab-content ${empty currentTab or currentTab == 'staff' ? 'active' : ''}">
                <!-- TOOLBAR -->
                <div class="toolbar toolbar-staff">
                    <div class="search-box">
                        <label><i class="fas fa-search"></i> Tìm kiếm</label>
                        <input type="text" id="staffSearch" placeholder="Nhập tên, số điện thoại hoặc email..." value="${searchKeyword}">
                    </div>
                    <div class="filter-box">
                        <label><i class="fas fa-filter"></i> Lọc theo vai trò</label>
                        <select id="staffRoleFilter">
                            <option value="all" ${filterRole == 'all' ? 'selected' : ''}>-- Tất cả --</option>
                            <option value="admin" ${filterRole == 'admin' ? 'selected' : ''}>Admin</option>
                            <option value="doctor" ${filterRole == 'doctor' ? 'selected' : ''}>Bác sĩ</option>
                            <option value="receptionist" ${filterRole == 'receptionist' ? 'selected' : ''}>Tiếp tân</option>
                            <option value="technician" ${filterRole == 'technician' ? 'selected' : ''}>Kỹ thuật viên</option>
                        </select>
                    </div>
                    <div class="filter-box">
                        <label><i class="fas fa-filter"></i> Lọc theo trạng thái</label>
                        <select id="staffStatusFilter">
                            <option value="all" ${filterStatus == 'all' ? 'selected' : ''}>-- Tất cả --</option>
                            <option value="active" ${filterStatus == 'active' ? 'selected' : ''}>Hoạt động</option>
                            <option value="inactive" ${filterStatus == 'inactive' ? 'selected' : ''}>Khóa</option>
                        </select>
                    </div>
                    <div class="toolbar-buttons">
                        <button class="btn-search" onclick="searchStaff()">
                            <i class="fas fa-search"></i> Tìm
                        </button>
                        <button class="btn-reset" onclick="resetStaffFilter()">
                            <i class="fas fa-redo"></i> Đặt lại
                        </button>
                        <button class="btn-add" onclick="openAddModal('staff')">
                            <i class="fas fa-plus"></i> Thêm tài khoản
                        </button>
                    </div>
                </div>

                <!-- TABLE NHÂN VIÊN -->
                <div class="table-container">
                    <c:choose>
                        <c:when test="${not empty allStaff}">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Tên</th>
                                        <th>Số điện thoại</th>
                                        <th>Email</th>
                                        <th>Vai trò</th>
                                        <th>Trạng thái</th>
                                        <th style="width: 150px; text-align: center;">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${allStaff}" var="staff">
                                        <tr>
                                            <td><strong>${staff.fullName}</strong></td>
                                            <td>${staff.phone}</td>
                                            <td>${not empty staff.email ? staff.email : '<em>Chưa cập nhật</em>'}</td>
                                            <td>
                                                <span class="badge ${staff.role.toString() == 'admin' ? 'badge-admin' : staff.role.toString() == 'doctor' ? 'badge-doctor' : staff.role.toString() == 'receptionist' ? 'badge-receptionist' : 'badge-technician'}">
                                                    ${staff.role.toString() == 'admin' ? 'Admin' : staff.role.toString() == 'doctor' ? 'Bác sĩ' : staff.role.toString() == 'receptionist' ? 'Tiếp tân' : 'Kỹ thuật viên'}
                                                </span>
                                            </td>
                                            <td>
                                                <span class="badge ${staff.status.toString() == 'active' ? 'badge-active' : 'badge-inactive'}">
                                                    ${staff.status.toString() == 'active' ? 'Hoạt động' : 'Khóa'}
                                                </span>
                                            </td>
                                            <td>
                                                <div class="action-buttons" style="justify-content: center;">
                                                    <button class="btn-action btn-view" onclick="viewAccount(${staff.userId}, '${staff.fullName}', '${staff.phone}', '${staff.email}', '${staff.role}', '${staff.status}')" title="Xem chi tiết">
                                                        <i class="fas fa-eye"></i>
                                                    </button>
                                                    <c:if test="${staff.role.toString() != 'admin'}">
                                                        <button class="btn-action btn-edit" onclick="openEditModal(${staff.userId}, '${staff.fullName}', '${staff.phone}', '${staff.email}', '${staff.role}', '${staff.status}', 'staff')" title="Sửa">
                                                            <i class="fas fa-edit"></i>
                                                        </button>
                                                        <button class="btn-action btn-toggle" onclick="toggleStatus('${staff.phone}', '${staff.fullName}')" title="Kích hoạt/Vô hiệu hóa">
                                                            <i class="fas fa-toggle-on"></i>
                                                        </button>
                                                    </c:if>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <div class="no-data">
                                <i class="fas fa-inbox"></i>
                                <p>Chưa có tài khoản nhân viên nào</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- PAGINATION -->
                 <c:if test="${staffTotalPages > 1}">
                    <div class="pagination-wrapper">
                        <c:choose>
                            <c:when test="${staffCurrentPage > 1}">
                                <a class="page-link"
                                href="admin-users?tab=staff&action=${currentAction}&keyword=${searchKeyword}&role=${filterRole}&status=${filterStatus}&staffPage=${staffCurrentPage - 1}&patientPage=${patientCurrentPage}">
                                    ‹ Trước
                                </a>
                            </c:when>
                            <c:otherwise>
                                <span class="page-link disabled">‹ Trước</span>
                            </c:otherwise>
                        </c:choose>

                        <c:forEach var="i" begin="1" end="${staffTotalPages}">
                            <a class="page-link ${i == staffCurrentPage ? 'active' : ''}"
                            href="admin-users?tab=staff&action=${currentAction}&keyword=${searchKeyword}&role=${filterRole}&status=${filterStatus}&staffPage=${i}&patientPage=${patientCurrentPage}">
                                ${i}
                            </a>
                        </c:forEach>

                        <c:choose>
                            <c:when test="${staffCurrentPage < staffTotalPages}">
                                <a class="page-link"
                                href="admin-users?tab=staff&action=${currentAction}&keyword=${searchKeyword}&role=${filterRole}&status=${filterStatus}&staffPage=${staffCurrentPage + 1}&patientPage=${patientCurrentPage}">
                                    Sau ›
                                </a>
                            </c:when>
                            <c:otherwise>
                                <span class="page-link disabled">Sau ›</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:if>
            </div>

            <!-- TAB 2: BỆNH NHÂN -->
            <div id="patient" class="tab-content ${currentTab == 'patient' ? 'active' : ''}">
                <!-- TOOLBAR -->
                <div class="toolbar toolbar-patient">
                    <div class="search-box">
                        <label><i class="fas fa-search"></i> Tìm kiếm</label>
                        <input type="text" id="patientSearch" placeholder="Nhập tên, số điện thoại hoặc email..." value="${searchKeyword}">
                    </div>
                    <div class="filter-box">
                        <label><i class="fas fa-filter"></i> Lọc theo trạng thái</label>
                        <select id="patientFilter">
                            <option value="all" ${filterPatientStatus == 'all' ? 'selected' : ''}>-- Tất cả --</option>
                            <option value="active" ${filterPatientStatus == 'active' ? 'selected' : ''}>Hoạt động</option>
                            <option value="inactive" ${filterPatientStatus == 'inactive' ? 'selected' : ''}>Khóa</option>
                        </select>
                    </div>
                    <div class="toolbar-buttons">
                        <button class="btn-search" onclick="searchPatient()">
                            <i class="fas fa-search"></i> Tìm
                        </button>
                        <button class="btn-reset" onclick="resetPatientFilter()">
                            <i class="fas fa-redo"></i> Đặt lại
                        </button>
                        <button class="btn-add" onclick="openAddModal('patient')">
                            <i class="fas fa-plus"></i> Thêm tài khoản
                        </button>
                    </div>
                </div>

                <!-- TABLE BỆNH NHÂN -->
                <div class="table-container">
                    <c:choose>
                        <c:when test="${not empty patients}">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Tên</th>
                                        <th>Số điện thoại</th>
                                        <th>Email</th>
                                        <th>Trạng thái</th>
                                        <th style="width: 150px; text-align: center;">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${patients}" var="patient">
                                        <tr>
                                            <td><strong>${patient.fullName}</strong></td>
                                            <td>${patient.phone}</td>
                                            <td>${not empty patient.email ? patient.email : '<em>Chưa cập nhật</em>'}</td>
                                            <td>
                                                <span class="badge ${patient.status.toString() == 'active' ? 'badge-active' : 'badge-inactive'}">
                                                    ${patient.status.toString() == 'active' ? 'Hoạt động' : 'Khóa'}
                                                </span>
                                            </td>
                                            <td>
                                                <div class="action-buttons" style="justify-content: center;">
                                                    <button class="btn-action btn-view" onclick="viewAccount(${patient.userId}, '${patient.fullName}', '${patient.phone}', '${patient.email}', '${patient.role}', '${patient.status}')" title="Xem chi tiết">
                                                        <i class="fas fa-eye"></i>
                                                    </button>
                                                    <button class="btn-action btn-edit" onclick="openEditModal(${patient.userId}, '${patient.fullName}', '${patient.phone}', '${patient.email}', '${patient.role}', '${patient.status}', 'patient')" title="Sửa">
                                                        <i class="fas fa-edit"></i>
                                                    </button>
                                                    <button class="btn-action btn-toggle" onclick="toggleStatus('${patient.phone}', '${patient.fullName}')" title="Kích hoạt/Vô hiệu hóa">
                                                        <i class="fas fa-toggle-on"></i>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <div class="no-data">
                                <i class="fas fa-inbox"></i>
                                <p>Chưa có tài khoản bệnh nhân nào</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- PAGINATION -->
                <c:if test="${patientTotalPages > 1}">
                    <div class="pagination-wrapper">
                        <c:choose>
                            <c:when test="${patientCurrentPage > 1}">
                                <a class="page-link"
                                href="admin-users?tab=patient&action=${currentAction}&keyword=${searchKeyword}&status=${filterPatientStatus}&staffPage=${staffCurrentPage}&patientPage=${patientCurrentPage - 1}">
                                    ‹ Trước
                                </a>
                            </c:when>
                            <c:otherwise>
                                <span class="page-link disabled">‹ Trước</span>
                            </c:otherwise>
                        </c:choose>

                        <c:forEach var="i" begin="1" end="${patientTotalPages}">
                            <a class="page-link ${i == patientCurrentPage ? 'active' : ''}"
                            href="admin-users?tab=patient&action=${currentAction}&keyword=${searchKeyword}&status=${filterPatientStatus}&staffPage=${staffCurrentPage}&patientPage=${i}">
                                ${i}
                            </a>
                        </c:forEach>

                        <c:choose>
                            <c:when test="${patientCurrentPage < patientTotalPages}">
                                <a class="page-link"
                                href="admin-users?tab=patient&action=${currentAction}&keyword=${searchKeyword}&status=${filterPatientStatus}&staffPage=${staffCurrentPage}&patientPage=${patientCurrentPage + 1}">
                                    Sau ›
                                </a>
                            </c:when>
                            <c:otherwise>
                                <span class="page-link disabled">Sau ›</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:if>
            </div>
        </div>

        <!-- MODAL XEM CHI TIẾT TÀI KHOẢN -->
        <div id="viewAccountModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <i class="fas fa-user-circle"></i>
                    <span>Chi tiết tài khoản</span>
                    <button class="modal-close" onclick="closeModal('viewAccountModal')">×</button>
                </div>

                <div class="form-info">
                    <div class="form-info-item">
                        <strong>Họ và tên:</strong>
                        <span id="viewFullName"></span>
                    </div>
                    <div class="form-info-item">
                        <strong>Số điện thoại:</strong>
                        <span id="viewPhone"></span>
                    </div>
                    <div class="form-info-item">
                        <strong>Email:</strong>
                        <span id="viewEmail"></span>
                    </div>
                    <div class="form-info-item" id="viewRoleItem" style="display: none;">
                        <strong>Vai trò:</strong>
                        <span id="viewRole"></span>
                    </div>
                    <div class="form-info-item">
                        <strong>Trạng thái:</strong>
                        <span id="viewStatus"></span>
                    </div>
                </div>

                <div class="modal-footer">
                    <button class="btn-cancel" onclick="closeModal('viewAccountModal')">
                        <i class="fas fa-times"></i> Đóng
                    </button>
                </div>
            </div>
        </div>

        <!-- MODAL THÊM TÀI KHOẢN -->
        <div id="addAccountModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <i class="fas fa-user-plus"></i>
                    <span id="addModalTitle">Thêm tài khoản Nhân viên</span>
                    <button class="modal-close" onclick="closeModal('addAccountModal')">×</button>
                </div>

                <form action="admin-users" method="POST" id="addAccountForm">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="role" id="addRoleInput">

                    <c:if test="${not empty error and addModalOpen}">
                        <div class="alert error" style="margin-bottom: 12px;">
                            <i class="fas fa-exclamation-circle"></i>
                            ${error}
                        </div>
                    </c:if>

                    <div class="form-group">
                        <label>Họ và tên <span style="color: red;">*</span></label>
                        <input type="text" name="fullname" id="addFullName" required placeholder="Nhập họ và tên" value="${addFullName}">
                    </div>

                    <div class="form-group">
                        <label>Số điện thoại <span style="color: red;">*</span></label>
                        <input type="tel" name="phone" id="addPhone" required pattern="0[0-9]{9}" title="Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0" placeholder="Nhập số điện thoại" value="${addPhone}">
                        <c:if test="${not empty addPhoneError}">
                            <div class="field-error">${addPhoneError}</div>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label>Email <span style="color: red;">*</span></label>
                        <input type="email" name="email" id="addEmail" required pattern="^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$" title="Email không đúng định dạng" placeholder="Nhập email" value="${addEmail}">
                        <c:if test="${not empty addEmailError}">
                            <div class="field-error">${addEmailError}</div>
                        </c:if>
                    </div>

                    <div id="addRoleGroup" class="form-group" style="display: none;">
                        <label>Vai trò <span style="color: red;">*</span></label>
                        <select name="staffRole" id="addStaffRole">
                            <option value="doctor">Bác sĩ</option>
                            <option value="receptionist">Tiếp tân</option>
                            <option value="technician">Kỹ thuật viên</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Mật khẩu <span style="color: red;">*</span></label>
                        <input type="password" name="password" id="addPassword" value="123456" placeholder="Mật khẩu">
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn-cancel" onclick="closeModal('addAccountModal')">
                            <i class="fas fa-times"></i> Hủy
                        </button>
                        <button type="submit" class="btn-submit">
                            <i class="fas fa-save"></i> Tạo tài khoản
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- MODAL CHỈNH SỬA TÀI KHOẢN -->
        <div id="editAccountModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <i class="fas fa-edit"></i>
                    <span>Chỉnh sửa tài khoản</span>
                    <button class="modal-close" onclick="closeModal('editAccountModal')">×</button>
                </div>

                <form action="admin-users" method="POST" id="editAccountForm">
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" name="userId" id="editUserId" value="${editUserId}">
                    <input type="hidden" name="editType" id="editType" value="${editModalType}">

                    <c:if test="${not empty error and editModalOpen}">
                        <div class="alert error">
                            <i class="fas fa-exclamation-circle"></i>
                            ${error}
                        </div>
                    </c:if>

                    <div class="form-group">
                        <label>Họ và tên <span style="color: red;">*</span></label>
                        <input type="text" name="fullname" id="editFullName" required value="${editFullName}">
                    </div>

                    <div class="form-group">
                        <label>Số điện thoại <span style="color: red;">*</span></label>
                        <input type="tel" name="phone" id="editPhone" required pattern="0[0-9]{9}" title="Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0" value="${editPhone}">
                        <c:if test="${not empty editPhoneError}">
                            <div class="field-error">${editPhoneError}</div>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label>Email <span style="color: red;">*</span></label>
                        <input type="email" name="email" id="editEmail" required pattern="^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$" title="Email không đúng định dạng" value="${editEmail}">
                        <c:if test="${not empty editEmailError}">
                            <div class="field-error">${editEmailError}</div>
                        </c:if>
                    </div>

                    <div id="editRoleGroup" class="form-group" style="display: none;">
                        <label>Vai trò <span style="color: red;">*</span></label>
                        <select name="role" id="editRole">
                            <option value="doctor">Bác sĩ</option>
                            <option value="receptionist">Tiếp tân</option>
                            <option value="technician">Kỹ thuật viên</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Trạng thái <span style="color: red;">*</span></label>
                        <select name="status" id="editStatus">
                            <option value="active" ${editStatusValue == 'active' ? 'selected' : ''}>Hoạt động</option>
                            <option value="inactive" ${editStatusValue == 'inactive' ? 'selected' : ''}>Khóa</option>
                        </select>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn-cancel" onclick="closeModal('editAccountModal')">
                            <i class="fas fa-times"></i> Hủy
                        </button>
                        <button type="submit" class="btn-submit">
                            <i class="fas fa-save"></i> Lưu thay đổi
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            let currentTab = '${currentTab}' || 'staff';

            // Chuyển tab
            function switchTab(event, tab) {
                currentTab = tab;
                document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));
                document.querySelectorAll('.tab-button').forEach(el => el.classList.remove('active'));

                document.getElementById(tab).classList.add('active');
                event.target.classList.add('active');
            }

            // Xem chi tiết tài khoản
            function viewAccount(userId, fullName, phone, email, role, status) {
                document.getElementById('viewFullName').innerText = fullName;
                document.getElementById('viewPhone').innerText = phone;
                document.getElementById('viewEmail').innerText = email || 'Chưa cập nhật';

                const roleItem = document.getElementById('viewRoleItem');
                if (role !== 'patient') {
                    roleItem.style.display = 'flex';
                    const roleText = role === 'admin' ? 'Admin' : role === 'doctor' ? 'Bác sĩ' : role === 'receptionist' ? 'Tiếp tân' : 'Kỹ thuật viên';
                    document.getElementById('viewRole').innerText = roleText;
                } else {
                    roleItem.style.display = 'none';
                }

                const statusBadge = status === 'active' ? '<span class="badge badge-active">Hoạt động</span>' : '<span class="badge badge-inactive">Khóa</span>';
                document.getElementById('viewStatus').innerHTML = statusBadge;

                openModal('viewAccountModal');
            }

            function clearFieldErrors(modalId) {
                const modal = document.getElementById(modalId);
                if (!modal) return;
                modal.querySelectorAll('.field-error').forEach(el => el.remove());
            }

            // Mở modal thêm tài khoản
            function openAddModal(type, preserveData = false) {
                const form = document.getElementById('addAccountForm');
                if (!preserveData) {
                    form.reset();
                    document.getElementById('addFullName').value = '';
                    document.getElementById('addPhone').value = '';
                    document.getElementById('addEmail').value = '';
                    document.getElementById('addPassword').value = '123456';
                    clearFieldErrors('addAccountModal');
                }

                const roleGroup = document.getElementById('addRoleGroup');
                if (type === 'staff') {
                    document.getElementById('addModalTitle').innerText = 'Thêm tài khoản Nhân viên';
                    document.getElementById('addRoleInput').value = '3'; // receptionist
                    roleGroup.style.display = 'block';
                } else {
                    document.getElementById('addModalTitle').innerText = 'Thêm tài khoản Bệnh nhân';
                    document.getElementById('addRoleInput').value = 'patient';
                    roleGroup.style.display = 'none';
                }

                openModal('addAccountModal');
            }

            // Mở modal chỉnh sửa
            function openEditModal(userId, fullName, phone, email, role, status, type) {
                clearFieldErrors('editAccountModal');
                document.getElementById('editUserId').value = userId;
                document.getElementById('editType').value = type;
                document.getElementById('editFullName').value = fullName;
                document.getElementById('editPhone').value = phone;
                document.getElementById('editEmail').value = email;
                document.getElementById('editStatus').value = status;

                const roleGroup = document.getElementById('editRoleGroup');
                if (type === 'staff') {
                    roleGroup.style.display = 'block';
                    document.getElementById('editRole').value = role;
                } else {
                    roleGroup.style.display = 'none';
                }

                openModal('editAccountModal');
            }

            // Mở modal
            function openModal(modalId) {
                document.getElementById(modalId).style.display = 'block';
            }

            // Đóng modal
            function closeModal(modalId) {
                document.getElementById(modalId).style.display = 'none';
            }

            // Đóng modal khi click bên ngoài
            window.onclick = function(event) {
                const modals = document.querySelectorAll('.modal');
                modals.forEach(modal => {
                    if (event.target === modal) {
                        modal.style.display = 'none';
                    }
                });
            }

            // Kích hoạt/Vô hiệu hóa
            function toggleStatus(phone, name) {
                if (confirm(`Thay đổi trạng thái của ${name}?`)) {
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = 'admin-users';

                    const actionInput = document.createElement('input');
                    actionInput.type = 'hidden';
                    actionInput.name = 'action';
                    actionInput.value = 'toggleStatus';

                    const phoneInput = document.createElement('input');
                    phoneInput.type = 'hidden';
                    phoneInput.name = 'phone';
                    phoneInput.value = phone;

                    form.appendChild(actionInput);
                    form.appendChild(phoneInput);
                    document.body.appendChild(form);
                    form.submit();
                }
            }

            // Filter nhân viên
            function filterStaff() {
                const role = document.getElementById('staffRoleFilter').value;
                const status = document.getElementById('staffStatusFilter').value;
                
                let url = 'admin-users?action=filter&tab=staff&staffPage=1';
                if (role !== 'all') {
                    url += '&role=' + role;
                }
                if (status !== 'all') {
                    url += '&status=' + status;
                }
                
                window.location.href = url;
            }

            // Filter bệnh nhân
            function filterPatient() {
                const status = document.getElementById('patientFilter').value;
                
                let url = 'admin-users?action=filter&tab=patient&patientPage=1';
                if (status !== 'all') {
                    url += '&status=' + status;
                }
                
                window.location.href = url;
            }

            // Search nhân viên
            function searchStaff() {
                const keyword = document.getElementById('staffSearch').value.trim();
                
                let url = 'admin-users?action=search&tab=staff&staffPage=1';
                if (keyword) {
                    url += '&keyword=' + encodeURIComponent(keyword);
                }
                
                window.location.href = url;
            }

            // Search bệnh nhân
            function searchPatient() {
                const keyword = document.getElementById('patientSearch').value.trim();
                
                let url = 'admin-users?action=search&tab=staff&staffPage=1';
                if (keyword) {
                    url += '&keyword=' + encodeURIComponent(keyword);
                }
                
                window.location.href = url;
            }

            // Đặt lại bộ lọc nhân viên
            function resetStaffFilter() {
                document.getElementById('staffSearch').value = '';
                document.getElementById('staffRoleFilter').value = 'all';
                document.getElementById('staffStatusFilter').value = 'all';
                window.location.href = 'admin-users?tab=staff';
            }

            // Đặt lại bộ lọc bệnh nhân
            function resetPatientFilter() {
                document.getElementById('patientSearch').value = '';
                document.getElementById('patientFilter').value = 'all';
                window.location.href = 'admin-users?tab=patient';
            }

            // Tự động đóng thông báo sau 5 giây và thêm event listeners cho filter
            document.addEventListener('DOMContentLoaded', function() {
                const alerts = document.querySelectorAll('.alert');
                alerts.forEach(alert => {
                    setTimeout(() => {
                        alert.style.animation = 'slideIn 0.3s ease-out reverse';
                        setTimeout(() => alert.remove(), 300);
                    }, 5000);
                });

                // Thêm event listeners cho filter dropdowns
                document.getElementById('staffRoleFilter').addEventListener('change', filterStaff);
                document.getElementById('staffStatusFilter').addEventListener('change', filterStaff);
                document.getElementById('patientFilter').addEventListener('change', filterPatient);
                
                // Thêm event listeners cho search inputs
                document.getElementById('staffSearch').addEventListener('keypress', function(e) {
                    if (e.key === 'Enter') {
                        searchStaff();
                    }
                });
                document.getElementById('patientSearch').addEventListener('keypress', function(e) {
                    if (e.key === 'Enter') {
                        searchPatient();
                    }
                });

                const shouldOpenAddModal = '${addModalOpen}' === 'true';
                const shouldOpenEditModal = '${editModalOpen}' === 'true';

                if (shouldOpenAddModal) {
                    const addType = '${not empty addModalType ? addModalType : "staff"}';
                    openAddModal(addType, true);
                }

                if (shouldOpenEditModal) {
                    const roleGroup = document.getElementById('editRoleGroup');
                    if ('${editModalType}' === 'staff') {
                        roleGroup.style.display = 'block';
                    } else {
                        roleGroup.style.display = 'none';
                    }
                    openModal('editAccountModal');
                }
            });
        </script>
    </body>
</html>
