<%-- 
    Document   : appointmentSecond
    Created on : Feb 3, 2026, 2:00:08 PM
    Author     : Admin
--%>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Xác nhận thông tin</title>


        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/pages/appointments/appointment/appointment.css">
    </head>
    <body>

        <jsp:include page="/common/header.jsp" />

        <div class="page">
            <div class="content">

                <!-- LEFT -->
                <div class="main">

                    <!-- STEPS -->
                    <div class="steps-wrapper">
                        <div class="step-circle done">
                            <span>✓</span>
                            <p>Thông tin bệnh nhân</p>
                        </div>
                        <div class="step-line"></div>

                        <div class="step-circle active">
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

                    <!-- THÔNG TIN XÁC NHẬN -->
                    <div class="card-box">
                        <h3>Thông tin bệnh nhân</h3>

                        <div class="form-grid confirm-view">
                            <div>
                                <label>Họ và tên</label>
                                <p>${name}</p>
                            </div>

                            <div>
                                <label>Số điện thoại</label>
                                <p>${sdt}</p>
                            </div>

                            <div>
                                <label>Email</label>
                                <p>${email}</p>
                            </div>

                            <div>
                                <label>Ngày sinh</label>
                                <p>${dateofbirth}</p>
                            </div>

                            <div>
                                <label>Giới tính</label>
                                <p>
                                <c:choose>
                                    <c:when test="${gender == 'MALE'}">Nam</c:when>
                                    <c:otherwise>Nữ</c:otherwise>
                                </c:choose>
                                </p>
                            </div>

                            <div>
                                <label>Địa chỉ</label>
                                <p>${address}</p>
                            </div>
                        </div>

                        <label>Ghi chú bệnh lý</label>
                        <p class="note-box">${note}</p>
                    </div>

                    <!-- NGÀY + CA KHÁM -->
                    <div class="card-box">
                        <h3>Thời gian khám</h3>

                        <div class="form-grid confirm-view">
                            <div>
                                <label>Ngày khám</label>
                                <p>${appointmentDate}</p>
                            </div>

                            <div>
                                <label>Ca khám</label>
                                <p>${timeSlot}</p>
                            </div>
                        </div>
                    </div>

                    <!-- ACTION -->
                    <div class="actions">
                        <button class="btn-outline"
                                onclick="history.back()">
                            Quay lại
                        </button>

                        <button class="btn-primary"
                                onclick="location.href = '${pageContext.request.contextPath}/payment'">
                            Xác nhận & Thanh toán
                        </button>
                    </div>
                </div>

                <!-- RIGHT (GIỮ NGUYÊN) -->
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

        <jsp:include page="/common/footer.jsp" />

    </body>
</html>

