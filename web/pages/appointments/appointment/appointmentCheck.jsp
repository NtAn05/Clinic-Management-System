<%-- 
    Document   : appointmentSecond
    Created on : Feb 3, 2026, 2:00:08 PM
    Author     : Admin
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Check information</title>
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
                        <div class="step-circle active">
                            <span>1</span>
                            <p>Xác nhận và chọn ca</p>
                        </div>
                        <div class="step-line"></div>
                        <div class="step-circle">
                            <span>2</span>
                            <p>Thanh toán</p>
                        </div>
                        <div class="step-line"></div>
                        <div class="step-circle">
                            <span>3</span>
                            <p>Hoàn tất</p>
                        </div>                        
                    </div>

                    <!-- THÔNG TIN XÁC NHẬN -->

                    <form method="post" action="${pageContext.request.contextPath}/appointmentservlet">

                        <div class="card-box">
                            <h3>Thông tin bệnh nhân</h3>

                            <div class="form-grid confirm-view">
                                <div>
                                    <label>Họ và tên</label>
                                    <p >${patient.getFullName()}</p>
                                </div>


                                <div>
                                    <label>Số điện thoại</label>
                                    <p>${patient.getPhone()}</p>
                                </div>

                                <div>
                                    <label>Email</label>
                                    <p>${patient.getEmail()}</p>
                                </div>

                                <div>
                                    <label>Ngày sinh</label>
                                    <p>${patient.getDob()}</p>
                                </div>

                                <div>
                                    <label>Giới tính</label>
                                    <p>${patient.getGender()}</p>
                                </div>


                            </div>
                            <div>
                                <label>Ghi chú bệnh lý</label>
                                <input type="text" name="note" >

                            </div>

                            <!-- NGÀY + CA KHÁM -->
                            <div class="card-box">
                                <h3>Chọn ngày và ca khám</h3>

                                <label>Ngày khám *</label>

                                <div class="time-slots" id="dateRadios">

                                    <c:forEach items="${dates}" var="d" varStatus="s">

                                        <input type="radio" 
                                               name="appointment_date"
                                               id="date${s.index}"
                                               value="${d}"
                                               ${s.index == 0 ? "checked" : ""}>

                                        <label for="date${s.index}" class="slot">
                                            <strong>${d}</strong>
                                        </label>

                                    </c:forEach>

                                </div>

                                <div class="time-slots">

                                    <input type="radio" name="time" id="morning" value="07:00" checked>
                                    <label for="morning" class="slot">
                                        <strong>Ca sáng</strong>
                                        <span>07:00 - 11:30</span>
                                    </label>

                                    <input type="radio" name="time" id="afternoon" value="13:00">
                                    <label for="afternoon" class="slot">
                                        <strong>Ca chiều</strong>
                                        <span>13:00 - 16:30</span>
                                    </label>

                                </div>

                            </div>


                            <input type="hidden" name="doctorID" value="${doctor.getDoctorId()}">
                            <input type="hidden" name="patientID" value="${patient.getPatientId()}">
                            <input type="hidden" name="userID" value="${sessionScope.account.userId}">
                            <input type="hidden" name="pricePay" value="${doctor.price}">
                            <input type="hidden" name="bookingStyle"
                                   value="${sessionScope.roleName == 'receptionist' ? 'walk_in' : 'online'}">
                            <!-- ACTION -->

                            <div class="actions">
                                <button type="button" class="btn-outline"
                                        onclick="location.href = '${pageContext.request.contextPath}/listofdoctorservlet'">
                                    Quay lại
                                </button>



                                <button type="submit" name="btnSubmit" value="thanhtoan" class="btn-primary">
                                    Xác nhận & Thanh toán
                                </button>

                            </div>
                        </div>
                    </form>


                </div>

                <!-- RIGHT  -->
                <div class="card">
                    <img src="${doctor.getImage()}" alt="Doctor">

                    <h3>${doctor.getFullName()}</h3>
                    <p class="degree">${doctor.getQualification()}</p>
                    <p class="desc">${doctor.getSpecialization()}</p>
                    <p class="desc">${doctor.getClinic_address()}</p>
                    <br>
                    <div class="info">
                        <span>⏱ ${doctor.getExperience_years()} năm</span>
                        <span>⭐ ${doctor.getRating()}</span>
                    </div>
                    <br>
                    <p class="price">
                        <fmt:formatNumber value="${doctor.price}" type="number"/>đ
                    </p>


                </div>


            </div>
        </div>
        <jsp:include page="/common/footer.jsp" />

    </body>
</html>