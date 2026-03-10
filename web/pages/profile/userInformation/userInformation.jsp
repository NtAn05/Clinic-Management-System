<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Information</title>
    <link rel="stylesheet"
      href="${pageContext.request.contextPath}/pages/profile/userInformation/userInformation.css">
</head>
<body>

<jsp:include page="/common/header.jsp" />

<div class="page">

    <h3>Thông tin tài khoản</h3>
    <p>Quản lý thông tin tài khoản và hồ sơ cá nhân</p>

    <!-- ================= PROFILE HEADER ================= -->
    <div class="profile-top">

        <div class="profile-left">

            <!-- Avatar -->
            <div class="avatar">
                <c:choose>
                    <c:when test="${not empty user.imageUrl}">
                        <img src="${user.imageUrl}" alt="Avatar">
                    </c:when>
                    <c:otherwise>
                        <img src="${pageContext.request.contextPath}/assets/default-avatar.png" alt="Avatar">
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="profile-name">
                <h2>${user.fullName}</h2>
                <span class="profile-role">${user.role}</span>
            </div>

        </div>

        <div class="profile-right">
            <button type="button" id="btnToggleEdit" class="btn-edit-profile">
                ✏ Chỉnh sửa
            </button>
        </div>
    </div>

    <div class="main">

        <!-- ================= VIEW MODE ================= -->
        <div class="profile-view">

            <div class="info-box">
                <h4>Thông tin cá nhân</h4>

                <div><label>User ID</label><p>${user.userId}</p></div>
                <div><label>Họ và tên</label><p>${user.fullName}</p></div>
                <div><label>Số điện thoại</label><p>${user.phone}</p></div>
                <div><label>Email</label><p>${user.email}</p></div>
                <div><label>Vai trò</label><p>${user.role}</p></div>
                <div><label>Trạng thái</label><p>${user.status}</p></div>
                <div><label>Địa chỉ</label><p>${user.address}</p></div>
            </div>

        </div>

        <!-- ================= EDIT MODE ================= -->
        <div class="profile-edit">

            <form action="userinformationservlet" method="post" >

                <input type="hidden" name="userID" value="${user.userId}">

                <div class="info-box">
                    <h4>Chỉnh sửa thông tin</h4>

                    <div>
                        <label>Họ và tên</label>
                        <input type="text" name="txtName" value="${user.fullName}" required>
                    </div>

                    <div>
                        <label>Số điện thoại</label>
                        <input type="text" name="txtPhone" value="${user.phone}" required>
                    </div>

                    <div>
                        <label>Email</label>
                        <input type="email" name="txtEmail" value="${user.email}" required>
                    </div>

                    <div>
                        <label>Địa chỉ</label>
                        <input type="text" name="txtAddress" value="${user.address}">
                    </div>

                    <div>
                        <label>Avatar</label>
                        <input type="text" value="${user.imageUrl}" name="txtImage">
                    </div>


                </div>

                <button type="submit" name="action" value="updateProfile">
                    💾 Lưu thay đổi
                </button>

            </form>

        </div>
                    <br>
                    
        <!-- ================= CHANGE PASSWORD ================= -->
        <div class="security">

            <form action="userinformationservlet" method="post">

                <input type="hidden" name="userID" value="${user.userId}">

                <h4>Đổi mật khẩu</h4>

                <div>
                    <label>Mật khẩu hiện tại</label>
                    <input type="password" name="txtOldPass" required>
                </div>

                <div>
                    <label>Mật khẩu mới</label>
                    <input type="password" name="txtNewPass" required>
                </div>

                <div>
                    <label>Xác nhận mật khẩu mới</label>
                    <input type="password" name="txtReNewPass" required>
                </div>

                <button type="submit" name="action" value="changePass">
                    Đổi mật khẩu
                </button>

            </form>

        </div>

    </div>
</div>

<jsp:include page="/common/footer.jsp" />

<script>
    const btn = document.getElementById("btnToggleEdit");
    const page = document.querySelector(".page");

    btn.addEventListener("click", function () {
        page.classList.toggle("edit-mode");

        if (page.classList.contains("edit-mode")) {
            btn.innerHTML = "❌ Hủy chỉnh sửa";
        } else {
            btn.innerHTML = "✏ Chỉnh sửa";
        }
    });
</script>

</body>
</html>