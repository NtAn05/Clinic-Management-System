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

            .toolbar-users {
                grid-template-columns: minmax(340px, 1.9fr) minmax(170px, 0.85fr) minmax(170px, 0.85fr) auto;
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
                display: inline-flex;
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

            .locked-readonly {
                background: #f3f4f6 !important;
                color: #6b7280 !important;
                cursor: not-allowed;
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
                color: #FB923C;
            }

            .btn-view:hover {
                background: #FFEDD5;
            }

            .btn-edit {
                color: #1976d2;
            }

            .btn-edit:hover {
                background: #e3f2fd;
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

            <div class="toolbar toolbar-users">
                <div class="search-box">
                    <label><i class="fas fa-search"></i> Tìm kiếm</label>
                    <input type="text" id="userSearch" placeholder="Nhập tên, số điện thoại hoặc email..." value="${searchKeyword}">
                </div>
                <div class="filter-box">
                    <label><i class="fas fa-filter"></i> Vai trò</label>
                    <select id="userRoleFilter">
                        <option value="all" ${filterRole == 'all' ? 'selected' : ''}>-- Tất cả --</option>
                        <option value="admin" ${filterRole == 'admin' ? 'selected' : ''}>Admin</option>
                        <option value="doctor" ${filterRole == 'doctor' ? 'selected' : ''}>Bác sĩ</option>
                        <option value="receptionist" ${filterRole == 'receptionist' ? 'selected' : ''}>Tiếp tân</option>
                        <option value="technician" ${filterRole == 'technician' ? 'selected' : ''}>Kỹ thuật viên</option>
                        <option value="patient" ${filterRole == 'patient' ? 'selected' : ''}>Bệnh nhân</option>
                    </select>
                </div>
                <div class="filter-box">
                    <label><i class="fas fa-filter"></i> Trạng thái</label>
                    <select id="userStatusFilter">
                        <option value="all" ${filterStatus == 'all' ? 'selected' : ''}>-- Tất cả --</option>
                        <option value="active" ${filterStatus == 'active' ? 'selected' : ''}>Hoạt động</option>
                        <option value="inactive" ${filterStatus == 'inactive' ? 'selected' : ''}>Khóa</option>
                    </select>
                </div>
                <div class="toolbar-buttons">
                    <button class="btn-search" onclick="searchUsers()">
                        <i class="fas fa-search"></i> Tìm
                    </button>
                    <button class="btn-reset" onclick="resetUserFilter()">
                        <i class="fas fa-redo"></i> Đặt lại
                    </button>
                    <button class="btn-add" onclick="openAddModal()">
                        <i class="fas fa-user-plus"></i> Thêm tài khoản
                    </button>
                </div>
            </div>

            <div class="table-container">
                <c:choose>
                    <c:when test="${not empty users}">
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
                                <c:forEach items="${users}" var="user">
                                    <tr>
                                        <td><strong>${user.fullName}</strong></td>
                                        <td>${user.phone}</td>
                                        <td>${not empty user.email ? user.email : '<em>Chưa cập nhật</em>'}</td>
                                        <td>
                                            <span class="badge ${user.role.toString() == 'admin' ? 'badge-admin' : user.role.toString() == 'doctor' ? 'badge-doctor' : user.role.toString() == 'receptionist' ? 'badge-receptionist' : user.role.toString() == 'technician' ? 'badge-technician' : 'badge-patient'}">
                                                ${user.role.toString() == 'admin' ? 'Admin' : user.role.toString() == 'doctor' ? 'Bác sĩ' : user.role.toString() == 'receptionist' ? 'Tiếp tân' : user.role.toString() == 'technician' ? 'Kỹ thuật viên' : 'Bệnh nhân'}
                                            </span>
                                        </td>
                                        <td>
                                            <span class="badge ${user.status.toString() == 'active' ? 'badge-active' : 'badge-inactive'}">
                                                ${user.status.toString() == 'active' ? 'Hoạt động' : 'Khóa'}
                                            </span>
                                        </td>
                                        <td>
                                            <div class="action-buttons" style="justify-content: center;">
                                                <button class="btn-action btn-view" onclick="viewAccount(${user.userId}, '${user.fullName}', '${user.phone}', '${user.email}', '${user.role}', '${user.status}')" title="Xem chi tiết">
                                                    <i class="fas fa-eye"></i>
                                                </button>
                                                <c:if test="${user.role.toString() != 'admin'}">
                                                    <button class="btn-action btn-toggle" onclick="toggleStatus(${user.userId}, '${user.fullName}')" title="Kích hoạt/Vô hiệu hóa">
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
                            <p>Chưa có tài khoản nào</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <c:if test="${totalPages > 1}">
                <div class="pagination-wrapper">
                    <c:choose>
                        <c:when test="${currentPage > 1}">
                            <a class="page-link"
                               href="admin-users?action=${currentAction}&keyword=${searchKeyword}&role=${filterRole}&status=${filterStatus}&page=${currentPage - 1}">
                                ‹ Trước
                            </a>
                        </c:when>
                        <c:otherwise>
                            <span class="page-link disabled">‹ Trước</span>
                        </c:otherwise>
                    </c:choose>

                    <c:forEach var="i" begin="1" end="${totalPages}">
                        <a class="page-link ${i == currentPage ? 'active' : ''}"
                           href="admin-users?action=${currentAction}&keyword=${searchKeyword}&role=${filterRole}&status=${filterStatus}&page=${i}">
                            ${i}
                        </a>
                    </c:forEach>

                    <c:choose>
                        <c:when test="${currentPage < totalPages}">
                            <a class="page-link"
                               href="admin-users?action=${currentAction}&keyword=${searchKeyword}&role=${filterRole}&status=${filterStatus}&page=${currentPage + 1}">
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

        <!-- MODAL XEM CHI TIẾT TÀI KHOẢN -->
        <div id="viewAccountModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <i class="fas fa-user-circle"></i>
                    <span>Chi tiết tài khoản</span>
                    <button class="modal-close" onclick="closeModal('viewAccountModal')">×</button>
                </div>

                <form action="admin-users" method="POST" id="viewAccountForm" onsubmit="return handleViewUserFormSubmit()">
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" name="userId" id="viewUserIdInput" value="${editUserId}">
                    <input type="hidden" name="editType" id="viewEditTypeInput" value="${editModalType}">

                    <c:if test="${not empty error and editModalOpen}">
                        <div class="alert error">
                            <i class="fas fa-exclamation-circle"></i>
                            ${error}
                        </div>
                    </c:if>

                    <div class="form-group">
                        <label>Họ và tên <span style="color: red;">*</span></label>
                        <input type="text" name="fullname" id="viewFullNameInput" required maxlength="100" value="${editFullName}">
                        <c:if test="${not empty editFullNameError}">
                            <div class="field-error">${editFullNameError}</div>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label>Số điện thoại <span style="color: red;">*</span></label>
                        <input type="tel" name="phone" id="viewPhoneInput" required maxlength="10" pattern="0[0-9]{9}" title="Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0" value="${editPhone}">
                        <c:if test="${not empty editPhoneError}">
                            <div class="field-error">${editPhoneError}</div>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label>Email <span style="color: red;">*</span></label>
                        <input type="email" name="email" id="viewEmailInput" required maxlength="100" pattern="^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$" title="Email không đúng định dạng" value="${editEmail}">
                        <c:if test="${not empty editEmailError}">
                            <div class="field-error">${editEmailError}</div>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label>Vai trò</label>
                        <input type="text" id="viewRoleInput" readonly>
                        <select name="role" id="viewRoleSelect" style="display: none;">
                            <option value="">--Chọn vai trò--</option>
                            <option value="patient">Bệnh nhân</option>
                            <option value="doctor">Bác sĩ</option>
                            <option value="receptionist">Tiếp tân</option>
                            <option value="technician">Kỹ thuật viên</option>
                        </select>
                        <c:if test="${not empty editRoleError}">
                            <div class="field-error">${editRoleError}</div>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label>Trạng thái</label>
                        <input type="text" id="viewStatusText" readonly>
                        <input type="hidden" id="viewStatusValue" value="active">
                    </div>

                    <div id="viewDoctorFieldsGroup" style="display: none;">
                        <div class="form-group">
                            <label>Chuyên môn bác sĩ <span style="color: red;">*</span></label>
                            <select name="doctorSpecialization" id="viewDoctorSpecialization">
                                <option value="">--Chọn chuyên môn--</option>
                                <option value="Da liễu dị ứng">Da liễu dị ứng</option>
                                <option value="Da liễu nhiễm trùng">Da liễu nhiễm trùng</option>
                                <option value="Da liễu tổng quát">Da liễu tổng quát</option>
                                <option value="Điều trị mụn">Điều trị mụn</option>
                            </select>
                            <c:if test="${not empty editDoctorSpecializationError}">
                                <div class="field-error">${editDoctorSpecializationError}</div>
                            </c:if>
                        </div>
                        <div class="form-group">
                            <label>Bằng cấp <span style="color: red;">*</span></label>
                            <select name="doctorQualification" id="viewDoctorQualification">
                                <option value="">--Chọn bằng cấp--</option>
                                <option value="Giáo sư / Phó Giáo sư">Giáo sư / Phó Giáo sư</option>
                                <option value="Tiến sĩ / Bác sĩ CK II">Tiến sĩ / Bác sĩ CK II</option>
                                <option value="Thạc sĩ / Bác sĩ CK I / BS nội trú">Thạc sĩ / Bác sĩ CK I / BS nội trú</option>
                            </select>
                            <c:if test="${not empty editDoctorQualificationError}">
                                <div class="field-error">${editDoctorQualificationError}</div>
                            </c:if>
                        </div>
                        <div class="form-group">
                            <label>Kinh nghiệm (năm) <span style="color: red;">*</span></label>
                            <input type="number" name="doctorExperienceYears" id="viewDoctorExperienceYears" min="0" max="50">
                            <c:if test="${not empty editDoctorExperienceError}">
                                <div class="field-error">${editDoctorExperienceError}</div>
                            </c:if>
                        </div>
                        <div class="form-group">
                            <label>Giá khám <span style="color: red;">*</span></label>
                            <input type="number" name="doctorPriceBooking" id="viewDoctorPriceBooking" min="0" max="10000000">
                            <c:if test="${not empty editDoctorPriceError}">
                                <div class="field-error">${editDoctorPriceError}</div>
                            </c:if>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn-cancel" id="viewCloseBtn" onclick="onViewUserCloseOrCancel()">
                            <i id="viewCloseBtnIcon" class="fas fa-times"></i> <span id="viewCloseBtnText">Đóng</span>
                        </button>
                        <button type="button" class="btn-submit" id="viewEditBtn" onclick="onViewUserEditToggle()">
                            <i id="viewEditBtnIcon" class="fas fa-pen-to-square"></i> <span id="viewEditBtnText">Sửa</span>
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- MODAL THÊM TÀI KHOẢN -->
        <div id="addAccountModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <i class="fas fa-user-plus"></i>
                    <span id="addModalTitle">Thêm tài khoản</span>
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
                        <input type="text" name="fullname" id="addFullName" required maxlength="100" placeholder="Nhập họ và tên" value="${addFullName}">
                        <c:if test="${not empty addFullNameError}">
                            <div class="field-error">${addFullNameError}</div>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label>Số điện thoại <span style="color: red;">*</span></label>
                        <input type="tel" name="phone" id="addPhone" required maxlength="10" pattern="0[0-9]{9}" title="Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0" placeholder="Nhập số điện thoại" value="${addPhone}">
                        <c:if test="${not empty addPhoneError}">
                            <div class="field-error">${addPhoneError}</div>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label>Email <span style="color: red;">*</span></label>
                        <input type="email" name="email" id="addEmail" required maxlength="100" pattern="^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$" title="Email không đúng định dạng" placeholder="Nhập email" value="${addEmail}">
                        <c:if test="${not empty addEmailError}">
                            <div class="field-error">${addEmailError}</div>
                        </c:if>
                    </div>

                                        <div id="addRoleGroup" class="form-group">
                        <label>Vai trò <span style="color: red;">*</span></label>
                        <select name="staffRole" id="addStaffRole">
                            <option value="">--Chọn vai trò--</option>
                            <option value="patient">Bệnh nhân</option>
                            <option value="doctor">Bác sĩ</option>
                            <option value="receptionist">Tiếp tân</option>
                            <option value="technician">Kỹ thuật viên</option>
                        </select>
                        <c:if test="${not empty addRoleError}">
                            <div class="field-error">${addRoleError}</div>
                        </c:if>
                    </div>

                    <div id="addDoctorFieldsGroup" style="display: none;">
                        <div class="form-group">
                            <label>Chuyên môn bác sĩ <span style="color: red;">*</span></label>
                            <select name="doctorSpecialization" id="addDoctorSpecialization">
                                <option value="">--Chọn chuyên môn--</option>
                                <option value="Da liễu dị ứng">Da liễu dị ứng</option>
                                <option value="Da liễu nhiễm trùng">Da liễu nhiễm trùng</option>
                                <option value="Da liễu tổng quát">Da liễu tổng quát</option>
                                <option value="Điều trị mụn">Điều trị mụn</option>
                            </select>
                            <c:if test="${not empty addDoctorSpecializationError}">
                                <div class="field-error">${addDoctorSpecializationError}</div>
                            </c:if>
                        </div>
                        <div class="form-group">
                            <label>Bằng cấp <span style="color: red;">*</span></label>
                            <select name="doctorQualification" id="addDoctorQualification">
                                <option value="">--Chọn bằng cấp--</option>
                                <option value="Giáo sư / Phó Giáo sư">Giáo sư / Phó Giáo sư</option>
                                <option value="Tiến sĩ / Bác sĩ CK II">Tiến sĩ / Bác sĩ CK II</option>
                                <option value="Thạc sĩ / Bác sĩ CK I / BS nội trú">Thạc sĩ / Bác sĩ CK I / BS nội trú</option>
                            </select>
                            <c:if test="${not empty addDoctorQualificationError}">
                                <div class="field-error">${addDoctorQualificationError}</div>
                            </c:if>
                        </div>
                        <div class="form-group">
                            <label>Kinh nghiệm (năm) <span style="color: red;">*</span></label>
                            <input type="number" name="doctorExperienceYears" id="addDoctorExperienceYears" min="0" max="50" value="${addDoctorExperienceYears}">
                            <c:if test="${not empty addDoctorExperienceError}">
                                <div class="field-error">${addDoctorExperienceError}</div>
                            </c:if>
                        </div>
                        <div class="form-group">
                            <label>Giá khám <span style="color: red;">*</span></label>
                            <input type="number" name="doctorPriceBooking" id="addDoctorPriceBooking" min="0" max="10000000" value="${addDoctorPriceBooking}">
                            <c:if test="${not empty addDoctorPriceError}">
                                <div class="field-error">${addDoctorPriceError}</div>
                            </c:if>
                        </div>
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
                    <i class="fas fa-pen-to-square"></i>
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
                            <option value="">--Chọn vai trò--</option>
                            <option value="doctor">Bác sĩ</option>
                            <option value="receptionist">Tiếp tân</option>
                            <option value="technician">Kỹ thuật viên</option>
                        </select>
                        <c:if test="${not empty editRoleError}">
                            <div class="field-error">${editRoleError}</div>
                        </c:if>
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

        <jsp:include page="../../common/footer.jsp" />
                        
        <script>
            let isViewUserEditMode = false;
            let viewUserSnapshot = null;
            let isViewUserRoleLocked = false;
            let viewOriginalRole = 'patient';

            function defaultPriceByQualificationIndex(selectEl) {
                if (!selectEl) return '';
                switch (selectEl.selectedIndex) {
                    case 1:
                        return '400000';
                    case 2:
                        return '300000';
                    case 3:
                        return '200000';
                    default:
                        return '';
                }
            }

            function applyDefaultDoctorPrice(selectId, priceInputId, force) {
                const selectEl = document.getElementById(selectId);
                const priceEl = document.getElementById(priceInputId);
                if (!selectEl || !priceEl) {
                    return;
                }
                const defaultPrice = defaultPriceByQualificationIndex(selectEl);
                if (!defaultPrice) {
                    return;
                }
                const currentPrice = (priceEl.value || '').trim();
                if (force || currentPrice === '') {
                    priceEl.value = defaultPrice;
                }
            }

            function roleTextFromValue(role) {
                return role === 'admin'
                        ? 'Admin'
                        : role === 'doctor'
                        ? 'Bác sĩ'
                        : role === 'receptionist'
                        ? 'Tiếp tân'
                        : role === 'technician'
                        ? 'Kỹ thuật viên'
                        : 'Bệnh nhân';
            }

            function statusTextFromValue(status) {
                return status === 'active' ? 'Hoạt động' : 'Khóa';
            }

            function applyViewUserEditPermission(role) {
                isViewUserRoleLocked = role === 'admin';
                const editBtn = document.getElementById('viewEditBtn');
                if (editBtn) {
                    editBtn.style.display = isViewUserRoleLocked ? 'none' : '';
                }
            }

            function setViewUserEditMode(enabled) {
                if (enabled && isViewUserRoleLocked) {
                    enabled = false;
                }
                isViewUserEditMode = enabled;
                document.getElementById('viewFullNameInput').readOnly = !enabled;
                document.getElementById('viewPhoneInput').readOnly = !enabled;
                document.getElementById('viewEmailInput').readOnly = !enabled;

                const roleInput = document.getElementById('viewRoleInput');
                const roleSelect = document.getElementById('viewRoleSelect');
                if (enabled) {
                    roleInput.style.display = 'none';
                    roleSelect.style.display = '';
                    roleSelect.disabled = false;
                } else {
                    roleSelect.disabled = true;
                    roleSelect.style.display = 'none';
                    roleInput.style.display = '';
                    roleInput.value = roleTextFromValue(roleSelect.value);
                }
                toggleDoctorFieldsVisibility();

                document.getElementById('viewEditBtnIcon').className = enabled ? 'fas fa-save' : 'fas fa-pen-to-square';
                document.getElementById('viewEditBtnText').innerText = enabled ? 'Lưu' : 'Sửa';
                document.getElementById('viewCloseBtnIcon').className = enabled ? 'fas fa-rotate-left' : 'fas fa-times';
                document.getElementById('viewCloseBtnText').innerText = enabled ? 'Hủy' : 'Đóng';
            }

            function captureViewUserSnapshot() {
                viewUserSnapshot = {
                    fullName: document.getElementById('viewFullNameInput').value || '',
                    phone: document.getElementById('viewPhoneInput').value || '',
                    email: document.getElementById('viewEmailInput').value || '',
                    role: document.getElementById('viewRoleSelect').value || 'patient',
                    status: document.getElementById('viewStatusValue').value || 'active',
                    doctorSpecialization: document.getElementById('viewDoctorSpecialization').value || '',
                    doctorQualification: document.getElementById('viewDoctorQualification').value || '',
                    doctorExperienceYears: document.getElementById('viewDoctorExperienceYears').value || '',
                    doctorPriceBooking: document.getElementById('viewDoctorPriceBooking').value || ''
                };
            }

            function restoreViewUserSnapshot() {
                if (!viewUserSnapshot) return;
                document.getElementById('viewFullNameInput').value = viewUserSnapshot.fullName;
                document.getElementById('viewPhoneInput').value = viewUserSnapshot.phone;
                document.getElementById('viewEmailInput').value = viewUserSnapshot.email;
                document.getElementById('viewRoleSelect').value = viewUserSnapshot.role;
                document.getElementById('viewRoleInput').value = roleTextFromValue(viewUserSnapshot.role);
                document.getElementById('viewStatusValue').value = viewUserSnapshot.status;
                document.getElementById('viewStatusText').value = statusTextFromValue(viewUserSnapshot.status);
                document.getElementById('viewDoctorSpecialization').value = viewUserSnapshot.doctorSpecialization;
                document.getElementById('viewDoctorQualification').value = viewUserSnapshot.doctorQualification;
                document.getElementById('viewDoctorExperienceYears').value = viewUserSnapshot.doctorExperienceYears;
                document.getElementById('viewDoctorPriceBooking').value = viewUserSnapshot.doctorPriceBooking;
                toggleDoctorFieldsVisibility();
            }

            function onViewUserEditToggle() {
                if (isViewUserRoleLocked) {
                    return;
                }
                if (!isViewUserEditMode) {
                    setViewUserEditMode(true);
                    return;
                }
                const form = document.getElementById('viewAccountForm');
                if (form) {
                    if (typeof form.requestSubmit === 'function') {
                        form.requestSubmit();
                    } else if (form.reportValidity()) {
                        form.submit();
                    }
                }
            }

            function onViewUserCloseOrCancel() {
                if (isViewUserEditMode) {
                    restoreViewUserSnapshot();
                    setViewUserEditMode(false);
                    return;
                }
                closeModal('viewAccountModal');
            }

            function handleViewUserFormSubmit() {
                trimUserFormInputs();
                return isViewUserEditMode && !isViewUserRoleLocked;
            }

            function trimUserFormInputs() {
                ['viewFullNameInput', 'viewPhoneInput', 'viewEmailInput', 'viewDoctorExperienceYears', 'viewDoctorPriceBooking'].forEach(id => {
                    const node = document.getElementById(id);
                    if (node && typeof node.value === 'string') {
                        node.value = node.value.trim();
                    }
                });
            }

            function toggleDoctorFieldsVisibility() {
                const group = document.getElementById('viewDoctorFieldsGroup');
                const targetRole = document.getElementById('viewRoleSelect').value;
                const shouldShow = isViewUserEditMode
                        && targetRole === 'doctor'
                        && viewOriginalRole !== 'doctor';
                group.style.display = shouldShow ? 'block' : 'none';
                if (shouldShow) {
                    applyDefaultDoctorPrice('viewDoctorQualification', 'viewDoctorPriceBooking', false);
                }
            }

                        // Xem chi tiết tài khoản (gộp sửa trong modal xem)
            function viewAccount(userId, fullName, phone, email, role, status) {
                const editType = role === 'patient' ? 'patient' : 'staff';
                document.getElementById('viewUserIdInput').value = userId;
                document.getElementById('viewEditTypeInput').value = editType;
                document.getElementById('viewFullNameInput').value = fullName || '';
                document.getElementById('viewPhoneInput').value = phone || '';
                document.getElementById('viewEmailInput').value = email || '';
                document.getElementById('viewRoleSelect').value = role || 'patient';
                document.getElementById('viewRoleInput').value = roleTextFromValue(role || 'patient');
                viewOriginalRole = role || 'patient';
                document.getElementById('viewStatusValue').value = status || 'active';
                document.getElementById('viewStatusText').value = statusTextFromValue(status || 'active');
                document.getElementById('viewDoctorSpecialization').value = '';
                document.getElementById('viewDoctorQualification').value = '';
                document.getElementById('viewDoctorExperienceYears').value = '';
                document.getElementById('viewDoctorPriceBooking').value = '';

                applyViewUserEditPermission(role || 'patient');
                setViewUserEditMode(false);
                captureViewUserSnapshot();
                openModal('viewAccountModal');
            }

            function clearFieldErrors(modalId) {
                const modal = document.getElementById(modalId);
                if (!modal) return;
                modal.querySelectorAll('.field-error').forEach(el => el.remove());
            }

            // Mở modal thêm tài khoản
            function openAddModal(preserveData = false) {
                const form = document.getElementById('addAccountForm');
                if (!preserveData) {
                    form.reset();
                    document.getElementById('addFullName').value = '';
                    document.getElementById('addPhone').value = '';
                    document.getElementById('addEmail').value = '';
                    document.getElementById('addPassword').value = '123456';
                    document.getElementById('addDoctorSpecialization').value = '';
                    document.getElementById('addDoctorQualification').value = '';
                    document.getElementById('addDoctorExperienceYears').value = '';
                    document.getElementById('addDoctorPriceBooking').value = '';
                    clearFieldErrors('addAccountModal');
                }

                const roleGroup = document.getElementById('addRoleGroup');
                const staffRole = document.getElementById('addStaffRole');
                document.getElementById('addModalTitle').innerText = 'Thêm tài khoản';
                if (!preserveData) {
                    staffRole.value = '';
                }
                document.getElementById('addRoleInput').value = staffRole.value;
                roleGroup.style.display = 'block';
                toggleAddDoctorFieldsVisibility();

                openModal('addAccountModal');
            }

            function toggleAddDoctorFieldsVisibility() {
                const role = document.getElementById('addStaffRole').value;
                const group = document.getElementById('addDoctorFieldsGroup');
                group.style.display = role === 'doctor' ? 'block' : 'none';
                if (role === 'doctor') {
                    applyDefaultDoctorPrice('addDoctorQualification', 'addDoctorPriceBooking', false);
                }
            }

            function trimAddFormInputs() {
                ['addFullName', 'addPhone', 'addEmail', 'addDoctorExperienceYears', 'addDoctorPriceBooking'].forEach(id => {
                    const node = document.getElementById(id);
                    if (node && typeof node.value === 'string') {
                        node.value = node.value.trim();
                    }
                });
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
            function toggleStatus(userId, name) {
                if (confirm(`Thay đổi trạng thái của ${name}?`)) {
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = 'admin-users';

                    const actionInput = document.createElement('input');
                    actionInput.type = 'hidden';
                    actionInput.name = 'action';
                    actionInput.value = 'toggleStatus';

                    const userIdInput = document.createElement('input');
                    userIdInput.type = 'hidden';
                    userIdInput.name = 'userId';
                    userIdInput.value = userId;

                    form.appendChild(actionInput);
                    form.appendChild(userIdInput);
                    document.body.appendChild(form);
                    form.submit();
                }
            }

            function filterUsers() {
                const role = document.getElementById('userRoleFilter').value;
                const status = document.getElementById('userStatusFilter').value;
                const keyword = document.getElementById('userSearch').value.trim();

                let url = 'admin-users?action=filter&page=1';
                if (role !== 'all') {
                    url += '&role=' + role;
                }
                if (status !== 'all') {
                    url += '&status=' + status;
                }
                if (keyword) {
                    url += '&keyword=' + encodeURIComponent(keyword);
                }
                window.location.href = url;
            }

            function searchUsers() {
                const keyword = document.getElementById('userSearch').value.trim();
                const role = document.getElementById('userRoleFilter').value;
                const status = document.getElementById('userStatusFilter').value;

                if (!keyword) {
                    filterUsers();
                    return;
                }

                let url = 'admin-users?action=search&page=1&keyword=' + encodeURIComponent(keyword);
                if (role !== 'all') {
                    url += '&role=' + role;
                }
                if (status !== 'all') {
                    url += '&status=' + status;
                }
                window.location.href = url;
            }

            function resetUserFilter() {
                document.getElementById('userSearch').value = '';
                document.getElementById('userRoleFilter').value = 'all';
                document.getElementById('userStatusFilter').value = 'all';
                window.location.href = 'admin-users';
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

                document.getElementById('userSearch').addEventListener('keypress', function(e) {
                    if (e.key === 'Enter') {
                        searchUsers();
                    }
                });
                document.getElementById('userRoleFilter').addEventListener('change', filterUsers);
                document.getElementById('userStatusFilter').addEventListener('change', filterUsers);
                document.getElementById('addStaffRole').addEventListener('change', function() {
                    document.getElementById('addRoleInput').value = this.value;
                    toggleAddDoctorFieldsVisibility();
                });
                document.getElementById('addDoctorQualification').addEventListener('change', function() {
                    applyDefaultDoctorPrice('addDoctorQualification', 'addDoctorPriceBooking', true);
                });
                document.getElementById('addAccountForm').addEventListener('submit', trimAddFormInputs);
                document.getElementById('viewRoleSelect').addEventListener('change', toggleDoctorFieldsVisibility);
                document.getElementById('viewDoctorQualification').addEventListener('change', function() {
                    applyDefaultDoctorPrice('viewDoctorQualification', 'viewDoctorPriceBooking', true);
                });

                const shouldOpenAddModal = '${addModalOpen}' === 'true';
                const shouldOpenEditModal = '${editModalOpen}' === 'true';

                if (shouldOpenAddModal) {
                    openAddModal(true);
                    const addRoleValue = '${not empty addRoleValue ? addRoleValue : ""}';
                    document.getElementById('addStaffRole').value = addRoleValue;
                    document.getElementById('addRoleInput').value = addRoleValue;
                    document.getElementById('addDoctorSpecialization').value = '${not empty addDoctorSpecialization ? addDoctorSpecialization : ""}';
                    document.getElementById('addDoctorQualification').value = '${not empty addDoctorQualification ? addDoctorQualification : ""}';
                    document.getElementById('addDoctorExperienceYears').value = '${not empty addDoctorExperienceYears ? addDoctorExperienceYears : ""}';
                    document.getElementById('addDoctorPriceBooking').value = '${not empty addDoctorPriceBooking ? addDoctorPriceBooking : ""}';
                    toggleAddDoctorFieldsVisibility();
                    applyDefaultDoctorPrice('addDoctorQualification', 'addDoctorPriceBooking', false);
                }

                if (shouldOpenEditModal) {
                    document.getElementById('viewUserIdInput').value = '${editUserId}';
                    document.getElementById('viewEditTypeInput').value = '${editModalType}';
                    document.getElementById('viewFullNameInput').value = '${editFullName}';
                    document.getElementById('viewPhoneInput').value = '${editPhone}';
                    document.getElementById('viewEmailInput').value = '${editEmail}';
                    const fallbackRole = ('${editRoleValue}' && '${editRoleValue}' !== 'null') ? '${editRoleValue}' : ('${editModalType}' === 'patient' ? 'patient' : 'receptionist');
                    document.getElementById('viewRoleSelect').value = fallbackRole;
                    document.getElementById('viewRoleInput').value = roleTextFromValue(fallbackRole);
                    viewOriginalRole = fallbackRole;
                    const statusValue = '${not empty editStatusValue ? editStatusValue : "active"}';
                    document.getElementById('viewStatusValue').value = statusValue;
                    document.getElementById('viewStatusText').value = statusTextFromValue(statusValue);
                    document.getElementById('viewDoctorSpecialization').value = '${not empty editDoctorSpecialization ? editDoctorSpecialization : ""}';
                    document.getElementById('viewDoctorQualification').value = '${not empty editDoctorQualification ? editDoctorQualification : ""}';
                    document.getElementById('viewDoctorExperienceYears').value = '${not empty editDoctorExperienceYears ? editDoctorExperienceYears : ""}';
                    document.getElementById('viewDoctorPriceBooking').value = '${not empty editDoctorPriceBooking ? editDoctorPriceBooking : ""}';
                    applyViewUserEditPermission(fallbackRole);
                    setViewUserEditMode(!isViewUserRoleLocked);
                    applyDefaultDoctorPrice('viewDoctorQualification', 'viewDoctorPriceBooking', false);
                    captureViewUserSnapshot();
                    openModal('viewAccountModal');
                }
            });
        </script>
    </body>
</html>








