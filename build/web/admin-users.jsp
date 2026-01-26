<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý Nhân sự</title>
        <link rel="stylesheet" href="css/style.css"> <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            .table-container {
                background: white;
                padding: 20px;
                border-radius: 8px;
                margin-bottom: 30px;
            }
            .table-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 15px;
            }
            table {
                width: 100%;
                border-collapse: collapse;
            }
            th, td {
                padding: 12px;
                border-bottom: 1px solid #eee;
                text-align: left;
            }
            th {
                background: #f8f9fa;
                color: #333;
            }
            .badge-doc {
                background: #e8f5e9;
                color: green;
                padding: 4px 8px;
                border-radius: 4px;
                font-size: 12px;
            }
            .badge-staff {
                background: #fff3e0;
                color: orange;
                padding: 4px 8px;
                border-radius: 4px;
                font-size: 12px;
            }
        </style>
    </head>
    <body>
        <header style="padding: 15px 50px; background: white; border-bottom: 1px solid #eee;">
            <div style="font-weight: bold; color: var(--primary);">ADMIN - QUẢN LÝ NHÂN SỰ</div>
            <a href="home.jsp">Về trang chủ</a>
        </header>

        <div class="container" style="display: block; padding: 30px 100px; height: auto;">

            <div class="table-container">
                <div class="table-header">
                    <h3>👨‍⚕️ Danh sách Bác sĩ</h3>
                    <button class="btn-submit" style="width: auto;" onclick="openModal(2)">+ Thêm Bác sĩ</button>
                </div>
                <table>
                    <tr><th>Họ tên</th><th>SĐT</th><th>Vai trò</th><th>Hành động</th></tr>
                            <c:forEach items="${doctors}" var="d">
                        <tr>
                            <td>${d.fullName}</td>
                            <td>${d.phone}</td>
                            <td><span class="badge-doc">Bác sĩ</span></td>
                            <td>
                                <form action="admin-users" method="POST" style="display:inline;" onsubmit="return confirm('Xóa bác sĩ này?');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="phone" value="${d.phone}">
                                    <button style="border:none; background:none; color:red; cursor:pointer;"><i class="fas fa-trash"></i></button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </div>

            <div class="table-container">
                <div class="table-header">
                    <h3>👩‍💼 Danh sách Nhân viên</h3>
                    <button class="btn-submit" style="width: auto;" onclick="openModal(3)">+ Thêm Nhân viên</button>
                </div>
                <table>
                    <tr><th>Họ tên</th><th>SĐT</th><th>Vai trò</th><th>Hành động</th></tr>
                            <c:forEach items="${staffs}" var="s">
                        <tr>
                            <td>${s.fullName}</td>
                            <td>${s.phone}</td>
                            <td><span class="badge-staff">Nhân viên</span></td>
                            <td>
                                <form action="admin-users" method="POST" style="display:inline;" onsubmit="return confirm('Xóa nhân viên này?');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="phone" value="${s.phone}">
                                    <button style="border:none; background:none; color:red; cursor:pointer;"><i class="fas fa-trash"></i></button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
        </div>

        <div id="userModal" class="modal" style="display: none; position: fixed; z-index: 100; left: 0; top: 0; width: 100%; height: 100%; background-color: rgba(0,0,0,0.5);">
            <div class="modal-content" style="background-color: white; margin: 10% auto; padding: 20px; border: 1px solid #888; width: 400px; border-radius: 10px;">

                <h3 id="modalTitle" style="text-align: center; color: #0061ff;">Thêm Tài Khoản</h3>

                <form action="admin-users" method="POST">
                    <input type="hidden" name="action" value="add">

                    <input type="hidden" name="role" id="roleInput"> 

                    <div class="form-group" style="margin-bottom: 15px;">
                        <label style="font-weight: bold;">Họ và tên</label>
                        <input type="text" name="fullname" required style="width: 100%; padding: 8px; margin-top: 5px;">
                    </div>

                    <div class="form-group" style="margin-bottom: 15px;">
                        <label style="font-weight: bold;">Số điện thoại (Tài khoản)</label>
                        <input type="text" name="phone" required style="width: 100%; padding: 8px; margin-top: 5px;">
                    </div>

                    <div class="form-group" style="margin-bottom: 20px;">
                        <label style="font-weight: bold;">Mật khẩu mặc định</label>
                        <input type="text" name="password" value="123456" required style="width: 100%; padding: 8px; margin-top: 5px;">
                    </div>

                    <div style="text-align: right;">
                        <button type="button" onclick="document.getElementById('userModal').style.display = 'none'" style="padding: 8px 15px; cursor: pointer; background: #ccc; border: none; border-radius: 4px; margin-right: 5px;">Hủy</button>
                        <button type="submit" style="padding: 8px 15px; cursor: pointer; background: #0061ff; color: white; border: none; border-radius: 4px;">Tạo tài khoản</button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            function openModal(role) {
                // 1. Hiện Modal
                document.getElementById('userModal').style.display = 'block';

                // 2. Điền số 2 hoặc 3 vào ô input ẩn
                // Đảm bảo ID này trùng với ID của thẻ input bên trên
                var input = document.getElementById('roleInput');
                if (input) {
                    input.value = role;
                    console.log("Đã set RoleID = " + role); // Kiểm tra trong Console trình duyệt
                } else {
                    alert("LỖI: Không tìm thấy ô nhập Role! Hãy kiểm tra lại code JSP.");
                }

                // 3. Đổi tên tiêu đề
                document.getElementById('modalTitle').innerText = (role === 2) ? "Thêm Bác Sĩ Mới" : "Thêm Nhân Viên Mới";
            }
        </script>
    </body>
</html>