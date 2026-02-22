<%-- 
    Document   : appointmentPayment
    Created on : Feb 22, 2026, 9:39:38 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
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

                        <div class="step-circle done">
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

                    <!-- Hiện QR thanh toán -->
                    <div class="card-box">
                        <h3>QR thanh toán</h3>

                        <div class="form-grid confirm-view">
                            <div class="payment-qr-box">
                                <h3>Quét mã để thanh toán</h3>
                                <img src="https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=ThanhToan123"
                                     alt="QR Thanh Toán"
                                     class="payment-qr">
                                <p class="amount">Số tiền: 500.000 VNĐ</p>
                            </div>


                        </div>

                        <label style="color: red">Vui lòng thanh toán</label>
                    </div>


                    <!-- ACTION -->
                    <div class="actions">
                        <button  class="btn-outline"
                                 onclick="history.back()">
                            Quay lại
                        </button>



                        <button type="submit" name="btnSubmit" value="step2" class="btn-primary">
                            Thanh toán hoàn tất
                        </button>

                    </div>
                </div>

                <!-- RIGHT (GIỮ NGUYÊN) -->
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
