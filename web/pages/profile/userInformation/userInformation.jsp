<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>User Information</title>
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/pages/user/userInformation.css">
    </head>
    <body>

        <jsp:include page="/common/header.jsp" />

        <div class="page">

            <h3>Thông tin tài khoản</h3>
            <p>Quản lý thông tin tài khoản và hồ sơ cá nhân</p>

            <!-- ================= PROFILE HEADER ================= -->
            <div class="profile-top">
                <div class="profile-left">

                    <c:choose>
                        <c:when test="${not empty doctor and not empty doctor.image}">
                            <img src="${doctor.image}"
                                 onerror="this.src='${pageContext.request.contextPath}/images/default-avatar.png'"
                                 class="profile-avatar">
                        </c:when>
                        <c:otherwise>
                            <img src="${pageContext.request.contextPath}/images/default-avatar.png"
                                 class="profile-avatar">
                        </c:otherwise>
                    </c:choose>

                    <div class="profile-name">
                        <h2>${user.fullName}</h2>
                        <span class="profile-role">${roleName}</span>
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

                    <div class="basic">
                        <div>
                            <h4>Thông tin liên hệ</h4>
                            <div><label>Họ và tên</label><p>${user.fullName}</p></div>
                            <div><label>Số điện thoại</label><p>${user.phone}</p></div>
                            <div><label>Email</label><p>${user.email}</p></div>
                            <div><label>Giới tính</label><p>${user.gender}</p></div>
                            <div><label>Ngày sinh</label><p>${user.dob}</p></div>
                        </div>

                        <div>
                            <h4>Địa chỉ</h4>
                            <div><label>Tỉnh / Thành phố</label><p>${user.city}</p></div>
                            <div><label>Phường / Xã</label><p>${user.commune}</p></div>
                            <div><label>Số nhà, Đường</label><p>${user.house}</p></div>
                        </div>

                        <!-- Professional Info -->
                        <c:if test="${roleName == 'doctor'}">
                            <div class="professional">
                                <h4>Thông tin chuyên môn</h4>
                                <div><label>Bằng cấp</label><p>${doctor.qualification}</p></div>
                                <div><label>Năm kinh nghiệm</label><p>${doctor.experience_years}</p></div>
                                <div><label>Chuyên khoa</label><p>${doctor.specialization}</p></div>
                            </div>
                        </c:if>
   
                    </div>
                </div>

                <!-- ================= EDIT MODE ================= -->
                <div class="profile-edit">

                    <form action="userinformation" method="post">
                        <input type="hidden" name="userID" value="${sessionScope.account.userId}">

                        <c:if test="${roleName == 'doctor'}">
                            <input type="hidden" name="doctorID" value="${doctor.doctorId}">
                        </c:if>

                        <div class="basic">

                            <div>
                                <h4>Thông tin liên hệ</h4>
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
                                    <label>Giới tính</label>
                                    <input type="text" name="txtGender" value="${user.gender}">
                                </div>

                                <div>
                                    <label>Ngày sinh</label>
                                    <input type="date" name="txtDob" value="${user.dob}">
                                </div>
                            </div>

                            <div>
                                <h4>Địa chỉ</h4>
                                <div>
                                    <label>Tỉnh / Thành phố</label>
                                    <input type="text" name="city" value="${user.city}">
                                </div>

                                <div>
                                    <label>Phường / Xã</label>
                                    <input type="text" name="commune" value="${user.commune}">
                                </div>

                                <div>
                                    <label>Số nhà, Đường</label>
                                    <input type="text" name="house" value="${user.house}">
                                </div>
                            </div>

                        </div>

                        <c:if test="${roleName == 'doctor'}">
                            <div class="professional">
                                <h4>Thông tin chuyên môn</h4>
                                <input type="text" name="txtQualification" value="${doctor.qualification}" placeholder="Bằng cấp">
                                <input type="number" name="txtExperience" value="${doctor.experience_years}" placeholder="Kinh nghiệm">
                                <input type="text" name="txtSpecialization" value="${doctor.specialization}" placeholder="Chuyên khoa">
                            </div>
                        </c:if>

                        <button type="submit" name="action" value="updateProfile">
                            💾 Lưu thay đổi
                        </button>

                    </form>

                </div>

                <!-- ================= CHANGE PASSWORD ================= -->
                <div class="security">
                    <form action="userinformation" method="post">
                        <input type="hidden" name="userID" value="${sessionScope.account.userId}">

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