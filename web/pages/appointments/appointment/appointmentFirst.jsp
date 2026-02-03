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


                    <!-- FORM -->
                    <div class="section-box">
                        <h3>Chọn đối tượng đặt khám</h3>
                        <label><input type="radio" name="type" checked> Đặt cho tôi</label>
                        <label><input type="radio" name="type"> Đặt cho người thân</label>
                    </div>
                    <form method="get" action="${pageContext.request.contextPath}/appointmentservlet">
                        <!-- FORM ĐẶT CHO TÔI -->
                        <div id="formSelf">
                            <div class="card-box">
                                <h3>Thông tin của bạn</h3>

                                <div class="form-grid">
                                    <div>
                                        <label>Họ và tên *</label>
                                        <input type="text" name="name" value="${user.fullName}" required>
                                    </div>
                                    <div>
                                        <label>Số điện thoại *</label>
                                        <input type="text" name="sdt" value="${user.phone}" required>
                                    </div>

                                    <div>
                                        <label>Email</label>
                                        <input type="email" name="email" value="${user.email}" required>
                                    </div>
                                    <div>
                                        <label>Ngày sinh *</label>
                                        <input type="date" name="dateofbirth"  >
                                    </div>

                                    <div>
                                        <label >Giới tính *</label>
                                        <select name="gender" required>
                                            <option value="MALE">Nam</option>
                                            <option value="FEMALE">Nữ</option>
                                        </select>
                                    </div>

                                    <div>
                                        <label>Địa chỉ</label>
                                        <input type="text" name="address">
                                    </div>
                                </div>

                                <label>Ghi chú bệnh lý</label>
                                <textarea placeholder="Nhập triệu chứng nếu có" name="note"></textarea>
                            </div>
                        </div>
                        <!-- FORM ĐẶT CHO NGƯỜI THÂN -->
                        <div id="formRelative" style="display:none">
                            <div class="card-box">
                                <h3>Thông tin người đặt</h3>

                                <div class="form-grid">
                                    <div>
                                        <label>Họ và tên *</label>
                                        <input type="text" value="Nguyễn Văn An">
                                    </div>
                                    <div>
                                        <label>Số điện thoại *</label>
                                        <input type="text" value="0912345678">
                                    </div>

                                    <div>
                                        <label>Email</label>
                                        <input type="email" value="nguyenvanan@gmail.com">
                                    </div>

                                </div>

                                <div class="card-box">
                                    <h3>Thông tin bệnh nhân</h3>

                                    <div class="form-grid">
                                        <div>
                                            <label>Họ và tên *</label>
                                            <input type="text" value="Nguyễn Văn An">
                                        </div>
                                        <div>
                                            <label>Số điện thoại *</label>
                                            <input type="text" value="0912345678">
                                        </div>

                                        <div>
                                            <label>Email</label>
                                            <input type="email" value="nguyenvanan@gmail.com">
                                        </div>
                                        <div>
                                            <label>Ngày sinh *</label>
                                            <input type="date">
                                        </div>

                                        <div>
                                            <label>Giới tính *</label>
                                            <select>
                                                <option>Nam</option>
                                                <option>Nữ</option>
                                            </select>
                                        </div>

                                        <div>
                                            <label>Địa chỉ</label>
                                            <input type="text" value="123 Lê Lợi, Q1, TP.HCM">
                                        </div>
                                    </div>

                                    <label>Ghi chú bệnh lý</label>
                                    <textarea placeholder="Nhập triệu chứng nếu có"></textarea>
                                </div>
                            </div>
                        </div>


                        <div class="card-box">
                            <h3>Chọn ngày và ca khám</h3>

                            <label>Ngày khám *</label>
                            <input type="date">

                            <div class="time-slots">
                                <div class="slot active">
                                    <strong>Ca sáng</strong>
                                    <span>07:00 - 11:30</span>
                                </div>
                                <div class="slot">
                                    <strong>Ca chiều</strong>
                                    <span>13:30 - 17:00</span>
                                </div>
                            </div>
                        </div>
                    </form>
                    <div class="actions">
                        <button class="btn-outline"
                                onclick="location.href = '${pageContext.request.contextPath}/listofdoctorservlet'">
                            Quay lại
                        </button>

                        <button class="btn-primary">
                            Tiếp tục
                        </button>
                    </div>

                </div>

                <!-- RIGHT -->
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
            <jsp:include page="/common/footer.jsp" />

            <script>
                function toggleForm() {
                    const type = document.querySelector('input[name="type"]:checked').value;

                    document.getElementById("formSelf").style.display =
                            type === "self" ? "block" : "none";

                    document.getElementById("formRelative").style.display =
                            type === "relative" ? "block" : "none";
                }
            </script>

    </body>
</html>