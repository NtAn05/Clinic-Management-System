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

                <div class="main">

                    <div class="steps-wrapper">
                        <div class="step-circle done">
                            <span>✓</span>
                            <p>Thông tin bệnh nhân</p>
                        </div>
                        <div class="step-line"></div>

                        <div class="step-circle done">
                            <span>✓</span>
                            <p>Xác nhận</p>
                        </div>
                        <div class="step-line"></div>

                        <div class="step-circle done">
                            <span>✓</span>
                            <p>Thanh toán</p>
                        </div>
                        <div class="step-line"></div>

                        <div class="step-circle active">
                            <span>4</span>
                            <p>Hoàn tất</p>
                        </div>
                    </div>

                    <div class="card-box">
                        <h3>Cảm ơn đã đặt lịch hẹn</h3>

                        <div class="success-wrapper">
                            <div class="success-icon-box">
                                <img src="https://png.pngtree.com/png-vector/20250322/ourlarge/pngtree-check-mark-voting-checklist-png-image_15845205.png"
                                     alt="Thành công"
                                     class="success-icon">
                            </div>
                        </div>

                        <label style="color: red">Cuộc hẹn của bạn đã được đặt thành công !!!</label>
                        <br>
                        <label style="color: red">Vui lòng đến cơ sở khám đúng thời gian</label>

                        <div class="actions">
                            <a href="${pageContext.request.contextPath}/index.jsp" class="btn-primary">
                                Về trang chủ
                            </a>
                        </div>
                    </div>

                </div>

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