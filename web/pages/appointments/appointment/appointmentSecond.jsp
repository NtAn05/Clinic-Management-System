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

                    <form method="post" action="${pageContext.request.contextPath}/appointmentservlet">

                        <div class="card-box">
                            <h3>Thông tin bệnh nhân</h3>

                            <div class="form-grid confirm-view">
                                <div>
                                    <label>Họ và tên</label>
                                    <p >${patient.getFullName()}</p>
                                    <input type="hidden" name="name" value="${patient.getFullName()}">
                                </div>


                                <div>
                                    <label>Số điện thoại</label>
                                    <p>${patient.getPhone()}</p>
                                    <input type="hidden" name="sdt" value="${patient.getPhone()}">
                                </div>

                                <div>
                                    <label>Email</label>
                                    <p>${patient.getEmail()}</p>
                                    <input type="hidden" name="email" value="${patient.getEmail()}">
                                </div>

                                <div>
                                    <label>Ngày sinh</label>
                                    <p>${patient.getDob()}</p>
                                    <input type="hidden" name="dateofbirth" value="${patient.getDob()}">
                                </div>

                                <div>
                                    <label>Giới tính</label>
                                    <p>${patient.getGender()}</p>
                                    <input type="hidden" name="gender" value="${patient.getGender()}">
                                </div>

                                <div>
                                    <label>Địa chỉ</label>
                                    <p>${patient.getAddress()}</p>
                                    <input type="hidden" name="address" value="${patient.getAddress()}">
                                </div>
                            </div>
                            <div>
                                <label>Ghi chú bệnh lý</label>
                                <p  class="note-box">${appointment.getSymptom()}</p>
                                <input type="hidden" name="note" value="${appointment.getSymptom()}">

                            </div>

                            <!-- NGÀY + CA KHÁM -->
                            <div class="card-box">
                                <h3>Thời gian khám</h3>

                                <div class="form-grid confirm-view">
                                    <div>
                                        <label>Ca khám</label>
                                        <c:if test="${appointment.getPatientName() == 'morning'}">
                                            <p>
                                                <strong>Ca sáng</strong>
                                                <span>07:00 - 11:30</span>
                                            </p>
                                        </c:if>
                                        <c:if test="${appointment.getPatientName() == 'afternoon'}">
                                            <p>
                                                <strong>Ca chiều</strong>
                                                <span>13:30 - 17:00</span>
                                            </p>
                                        </c:if>
                                    </div>

                                    <div>
                                        <label>Ngày khám</label>
                                        <p>${appointment.getStatus()}</p>
                                    </div>
                                </div>
                            </div>
                            <input type="hidden" name="time" value="${appointment.getPatientName()}">
                            <input type="hidden" name="date" value="${appointment.getStatus()}">
                            <input type="hidden" name="doctorID" value="${doctor.doctorId}">
                            <input type="hidden" name="userID" value="${sessionScope.account.userId}">
                            </form>

                            <!-- ACTION -->

                            <div class="actions">
                                <button  class="btn-outline"
                                         onclick="history.back()">
                                    Quay lại
                                </button>



                                <button type="submit" name="btnSubmit" value="step2" class="btn-primary">
                                    Xác nhận & Thanh toán
                                </button>

                            </div>

                        </div>
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

