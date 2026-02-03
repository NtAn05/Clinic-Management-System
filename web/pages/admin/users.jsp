<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý Nhân sự</title>
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

            header {
                padding: 20px 50px;
                background: white;
                border-bottom: 2px solid #0061ff;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            header .title {
                font-weight: bold;
                color: #0061ff;
                font-size: 18px;
            }

            header a {
                color: #0061ff;
                text-decoration: none;
                font-size: 14px;
                padding: 8px 12px;
                border-radius: 4px;
                transition: all 0.3s ease;
            }

            header a:hover {
                background: #f0f5ff;
            }

            .container {
                padding: 30px 50px;
                max-width: 1200px;
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

            .table-container {
                background: white;
                padding: 25px;
                border-radius: 10px;
                margin-bottom: 30px;
                box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
                overflow-x: auto;
            }

            .table-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 20px;
                border-bottom: 2px solid #f0f0f0;
                padding-bottom: 15px;
            }

            .table-header h3 {
                font-size: 18px;
                color: #333;
            }

            .btn-submit {
                background: #0061ff;
                color: white;
                border: none;
                padding: 10px 20px;
                border-radius: 6px;
                cursor: pointer;
                font-size: 14px;
                font-weight: 600;
                transition: all 0.3s ease;
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .btn-submit:hover {
                background: #0052cc;
                transform: translateY(-2px);
                box-shadow: 0 4px 12px rgba(0, 97, 255, 0.3);
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
            }

            td {
                padding: 15px;
                border-bottom: 1px solid #f0f0f0;
                color: #555;
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
                gap: 8px;
                flex-wrap: wrap;
            }

            .btn-edit, .btn-delete, .btn-toggle {
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

            .btn-edit {
                color: #1976d2;
            }

            .btn-edit:hover {
                background: #e3f2fd;
            }

            .btn-toggle {
                color: #f57c00;
            }

            .btn-toggle:hover {
                background: #fff3e0;
            }

            .btn-delete {
                color: #d32f2f;
            }

            .btn-delete:hover {
                background: #ffebee;
            }

            .empty-state {
                text-align: center;
                padding: 40px;
                color: #999;
            }

            .empty-state i {
                font-size: 48px;
                margin-bottom: 15px;
                opacity: 0.5;
            }

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
                max-width: 500px;
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
                margin-bottom: 20px;
                display: flex;
                align-items: center;
                gap: 10px;
            }

            .form-group {
                margin-bottom: 15px;
            }

            .form-group label {
                display: block;
                font-weight: 600;
                margin-bottom: 8px;
                color: #333;
            }

            .form-group input,
            .form-group select {
                width: 100%;
                padding: 10px;
                border: 1px solid #ddd;
                border-radius: 6px;
                font-size: 14px;
                font-family: inherit;
                transition: all 0.3s ease;
            }

            .form-group input:focus,
            .form-group select:focus {
                outline: none;
                border-color: #0061ff;
                box-shadow: 0 0 0 3px rgba(0, 97, 255, 0.1);
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
            }

            .btn-cancel:hover {
                background: #e0e0e0;
            }

            .btn-submit-modal {
                padding: 10px 20px;
                background: #0061ff;
                color: white;
                border: none;
                border-radius: 6px;
                cursor: pointer;
                font-weight: 600;
                transition: all 0.3s ease;
            }

            .btn-submit-modal:hover {
                background: #0052cc;
            }

            .no-data {
                text-align: center;
                padding: 30px;
                color: #999;
            }

            .admin-menu {
                display: flex;
                gap: 0;
                margin-bottom: 30px;
                background: white;
                border-radius: 10px;
                overflow: hidden;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            }

            .menu-item {
                flex: 1;
                padding: 15px 20px;
                text-align: center;
                background: #f5f5f5;
                border: none;
                cursor: pointer;
                font-size: 14px;
                font-weight: 600;
                color: #666;
                transition: all 0.3s ease;
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 8px;
            }

            .menu-item:hover {
                background: #e8e8e8;
            }

            .menu-item.active {
                background: #0061ff;
                color: white;
            }

            .content-section {
                display: none;
            }

            .content-section.active {
                display: block;
            }
        </style>
    </head>
    <body>
        <header>
            <div class="title">👨‍💼 ADMIN - QUẢN LÝ NHÂN SỰ</div>
            <div>
                <a href="${pageContext.request.contextPath}/index.jsp"><i class="fas fa-arrow-left"></i> Quay lại</a>
                <a href="${pageContext.request.contextPath}/"><i class="fas fa-home"></i> Về trang chủ</a>
            </div>
        </header>

        <div class="container">
            <!-- Thông báo thành công -->
            <c:if test="${not empty success}">
                <div class="alert success">
                    <i class="fas fa-check-circle"></i>
                    ${success}
                </div>
            </c:if>

            <!-- Thông báo lỗi -->
            <c:if test="${not empty error}">
                <div class="alert error">
                    <i class="fas fa-exclamation-circle"></i>
                    ${error}
                </div>
            </c:if>

            <!-- DANH SÁCH BÁC SĨ -->
            <div class="table-container">
                <div class="table-header">
                    <h3><i class="fas fa-stethoscope"></i> Danh sách Bác sĩ</h3>
                    <button class="btn-submit" onclick="openAddModal(2)">
                        <i class="fas fa-plus"></i> Thêm Bác sĩ
                    </button>
                </div>
                <c:choose>
                    <c:when test="${not empty doctors}">
                        <table>
                            <thead>
                                <tr>
                                    <th>Họ và tên</th>
                                    <th>Số điện thoại</th>
                                    <th>Trạng thái</th>
                                    <th style="width: 120px;">Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${doctors}" var="d">
                                    <tr>
                                        <td><strong>${d.fullName}</strong></td>
                                        <td>${d.phone}</td>
                                        <td>
                                            <span class="badge ${d.status.toString() == 'active' ? 'badge-active' : 'badge-inactive'}">
                                                ${d.status.toString() == 'active' ? 'Hoạt động' : 'Khóa'}
                                            </span>
                                        </td>
                                        <td>
                                            <div class="action-buttons">
                                                <button class="btn-toggle" onclick="toggleStatus('${d.phone}', '${d.fullName}')" title="Bật/Tắt">
                                                    <i class="fas fa-toggle-on"></i>
                                                </button>
                                                <button class="btn-delete" onclick="deleteUser('${d.phone}', '${d.fullName}')" title="Xóa">
                                                    <i class="fas fa-trash"></i>
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
                            <i class="fas fa-user-md"></i>
                            <p>Chưa có bác sĩ nào</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- DANH SÁCH NHÂN VIÊN -->
            <div class="table-container">
                <div class="table-header">
                    <h3><i class="fas fa-users"></i> Danh sách Nhân viên</h3>
                    <button class="btn-submit" onclick="openAddModal(3)">
                        <i class="fas fa-plus"></i> Thêm Nhân viên
                    </button>
                </div>
                <c:choose>
                    <c:when test="${not empty staffs}">
                        <table>
                            <thead>
                                <tr>
                                    <th>Họ và tên</th>
                                    <th>Số điện thoại</th>
                                    <th>Trạng thái</th>
                                    <th style="width: 120px;">Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${staffs}" var="s">
                                    <tr>
                                        <td><strong>${s.fullName}</strong></td>
                                        <td>${s.phone}</td>
                                        <td>
                                            <span class="badge ${s.status.toString() == 'active' ? 'badge-active' : 'badge-inactive'}">
                                                ${s.status.toString() == 'active' ? 'Hoạt động' : 'Khóa'}
                                            </span>
                                        </td>
                                        <td>
                                            <div class="action-buttons">
                                                <button class="btn-toggle" onclick="toggleStatus('${s.phone}', '${s.fullName}')" title="Bật/Tắt">
                                                    <i class="fas fa-toggle-on"></i>
                                                </button>
                                                <button class="btn-delete" onclick="deleteUser('${s.phone}', '${s.fullName}')" title="Xóa">
                                                    <i class="fas fa-trash"></i>
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
                            <i class="fas fa-user-tie"></i>
                            <p>Chưa có nhân viên nào</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- MODAL THÊM NGƯỜI DÙNG MỚI -->
        <div id="addUserModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <i class="fas fa-user-plus"></i>
                    <span id="modalTitle">Thêm Bác sĩ Mới</span>
                </div>

                <form action="admin-users" method="POST" id="addUserForm">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="role" id="roleInput">

                    <div class="form-group">
                        <label>Họ và tên <span style="color: red;">*</span></label>
                        <input type="text" name="fullname" id="fullname" required placeholder="Nhập họ và tên">
                    </div>

                    <div class="form-group">
                        <label>Số điện thoại <span style="color: red;">*</span></label>
                        <input type="tel" name="phone" id="phone" required placeholder="Nhập số điện thoại">
                    </div>

                    <div class="form-group">
                        <label>Mật khẩu mặc định</label>
                        <input type="text" name="password" id="password" value="123456" placeholder="Mật khẩu">
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn-cancel" onclick="closeAddModal()">
                            <i class="fas fa-times"></i> Hủy
                        </button>
                        <button type="submit" class="btn-submit-modal">
                            <i class="fas fa-save"></i> Tạo tài khoản
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- MODAL CHỈNH SỬA DỊCH VỤ -->
        <div id="editServiceModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <i class="fas fa-edit"></i>
                    <span>Chỉnh sửa Dịch vụ</span>
                </div>

                <form action="${pageContext.request.contextPath}/admin/services" method="POST">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="serviceId" id="editServiceId">

                    <div class="form-group">
                        <label>Tên dịch vụ <span style="color: red;">*</span></label>
                        <input type="text" name="name" id="editServiceName" required placeholder="Nhập tên dịch vụ">
                    </div>

                    <div class="form-group">
                        <label>Danh mục <span style="color: red;">*</span></label>
                        <input type="text" name="serviceType" id="editServiceType" required placeholder="Ví dụ: Khám tổng quát">
                    </div>

                    <div class="form-group">
                        <label>Giá (VNĐ) <span style="color: red;">*</span></label>
                        <input type="number" name="price" id="editServicePrice" min="0" required placeholder="0">
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn-cancel" onclick="closeEditServiceModal()">
                            <i class="fas fa-times"></i> Hủy
                        </button>
                        <button type="submit" class="btn-submit-modal">
                            <i class="fas fa-save"></i> Lưu thay đổi
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            // Mở modal thêm người dùng
            function openAddModal(role) {
                document.getElementById('roleInput').value = role;
                if (role === 2) {
                    document.getElementById('modalTitle').innerHTML = '<i class="fas fa-stethoscope"></i> Thêm Bác sĩ Mới';
                } else if (role === 3) {
                    document.getElementById('modalTitle').innerHTML = '<i class="fas fa-user-tie"></i> Thêm Nhân viên Mới';
                }
                document.getElementById('addUserModal').style.display = 'block';
            }

            // Đóng modal
            function closeAddModal() {
                document.getElementById('addUserModal').style.display = 'none';
                document.getElementById('addUserForm').reset();
            }

            // Đóng modal khi click bên ngoài
            window.onclick = function(event) {
                const modal = document.getElementById('addUserModal');
                if (event.target === modal) {
                    modal.style.display = 'none';
                }
            }

            // Xóa người dùng
            function deleteUser(phone, name) {
                if (confirm(`Bạn chắc chắn muốn xóa tài khoản của ${name}?`)) {
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = 'admin-users';

                    const actionInput = document.createElement('input');
                    actionInput.type = 'hidden';
                    actionInput.name = 'action';
                    actionInput.value = 'delete';

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

            // Bật/Tắt trạng thái
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

            // Tự động đóng thông báo sau 5 giây
            document.addEventListener('DOMContentLoaded', function() {
                const alerts = document.querySelectorAll('.alert');
                alerts.forEach(alert => {
                    setTimeout(() => {
                        alert.style.animation = 'slideIn 0.3s ease-out reverse';
                        setTimeout(() => alert.remove(), 300);
                    }, 5000);
                });
            });
        </script>
    </body>
</html>