<%-- 
    Document   : appointment
    Created on : Jan 26, 2026, 6:25:11 PM
    Author     : Admin
--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Appointment</title>
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/pages/appointments/appointment/appointment.css">
    </head>
    <body>

        <jsp:include page="/common/header.jsp" />

        <div class="page">
            <div class="content">

                <!-- Trái và giữa -->
                <div class="main">

                    <!-- thanh các bước -->
                    <div class="steps-wrapper">
                        <div class="step-circle active">
                            <span>1</span>
                            <p>Thông tin bệnh nhân</p>
                        </div>
                        <div class="step-line"></div>
                        <div class="step-circle">
                            <span>2</span>
                            <p>Xác nhận</p>
                        </div>
                        <div class="step-line"></div>
                        <div class="step-circle">
                            <span>3</span>
                            <p>Thanh toán</p>
                        </div>
                        <div class="step-line"></div>
                        <div class="step-circle">
                            <span>4</span>
                            <p>Hoàn tất</p>
                        </div>
                    </div>

                    <!-- FORM thông tin -->
                    <form method="post" action="${pageContext.request.contextPath}/appointmentservlet">

                        <!-- hidden an toàn -->
                        <input type="hidden" name="doctorID" value="${doctor.doctorId}">
                        <input type="hidden" name="userID" value="${sessionScope.account.userId}">

                        <div class="card-box">
                            <h3>Thông tin của bạn</h3>

                            <div class="form-grid">
                                <div>
                                    <label>Họ và tên *</label>
                                    <input type="text" name="name" value="${patient.fullName}" required>
                                </div>

                                <div>
                                    <label>Số điện thoại *</label>
                                    <input type="text" name="sdt" value="${patient.phone}" required>
                                </div>

                                <div>
                                    <label>Email *</label>
                                    <input type="email" name="email" value="${patient.email}" required>
                                </div>

                                <div>
                                    <label>Ngày sinh *</label>
                                    <input type="date" name="dateofbirth" value="${patient.dateofbirth}">
                                </div>

                                <div>
                                    <label>Giới tính *</label>
                                    <select name="gender" required>
                                        <option value="MALE">Nam</option>
                                        <option value="FEMALE">Nữ</option>
                                    </select>
                                </div>

                                <div>
                                    <label>Địa chỉ</label>
                                    <input type="text" name="address" value="${patient.address}">
                                </div>
                            </div>

                            <label>Ghi chú bệnh lý</label>
                            <textarea name="note" placeholder="Nhập triệu chứng nếu có">${patient.note}</textarea>
                        </div>

                        <!-- DATE & TIME -->
                        <div class="card-box">
                            <h3>Chọn ngày và ca khám</h3>

                            <label>Ngày khám *</label>
                            <div class="time-slots" id="dateRadios"></div>

                            <div class="time-slots">
                                <input type="radio" name="time" id="morning" value="morning" checked>
                                <label for="morning" class="slot">
                                    <strong>Ca sáng</strong>
                                    <span>07:00 - 11:30</span>
                                </label>

                                <input type="radio" name="time" id="afternoon" value="afternoon">
                                <label for="afternoon" class="slot">
                                    <strong>Ca chiều</strong>
                                    <span>13:30 - 17:00</span>
                                </label>
                            </div>
                        </div>

                        <div class="actions">
                            <button type="button" class="btn-outline"
                                    onclick="location.href = '${pageContext.request.contextPath}/listofdoctorservlet'">
                                Quay lại
                            </button>

                            <button type="submit" name="btnSubmit" value="step1" class="btn-primary">
                                Tiếp tục
                            </button>
                        </div>

                    </form>
                </div>
            

            <!-- bãc sĩ -->
            <div class="card">
                <img src="${doctor.image}" alt="Doctor">

                <h3>${doctor.fullName}</h3>
                <p class="degree">${doctor.qualification}</p>
                <p class="desc">${doctor.specialization}</p>
                <p class="desc">${doctor.clinic_address}</p>

                <div class="info">
                    <span>⏱ ${doctor.experience_years} năm</span>
                    <span>⭐ ${doctor.rating}</span>
                </div>

                <p class="price">
                    <fmt:formatNumber value="${doctor.price}" type="number"/>đ
                </p>
            </div>
                </div>
        </div>

        <script>
            const container = document.getElementById("dateRadios");
            const today = new Date();

            for (let i = 0; i < 7; i++) {
                const d = new Date(today);
                d.setDate(today.getDate() + i);

                const value = d.toISOString().split("T")[0];
                const label = d.toLocaleDateString("vi-VN");

                container.innerHTML +=
                        '<input type="radio" name="date" id="d' + i + '" value="' + value + '"' +
                        (i === 0 ? ' checked' : '') + '>' +
                        '<label for="d' + i + '" class="slot"><strong>' + label + '</strong></label>';
            }
        </script>

        <jsp:include page="/common/footer.jsp" />
    </body>
</html>