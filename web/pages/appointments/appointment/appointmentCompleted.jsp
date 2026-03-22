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

                    <div class="main">

                    <!-- STEPS -->
                    <div class="steps-wrapper">
                        <div class="step-circle active">
                            <span>✓</span>
                            <p>Xác nhận và chọn ca</p>
                        </div>
                        <div class="step-line"></div>
                        <div class="step-circle active">
                            <span>✓</span>
                            <p>Thanh toán</p>
                        </div>
                        <div class="step-line"></div>
                        <div class="step-circle active">
                            <span>✓</span>
                            <p>Hoàn tất</p>
                        </div>                        
                    </div>
                    <div class="card-box">
                        <h3>Cảm ơn đã đặt lịch hẹn</h3>

                        <div class="success-wrapper">
                            <div class="success-icon-box">
                                <img src="https://tse1.mm.bing.net/th/id/OIP.dSGCRzF6aLogIpu-UJt7gAHaF4?pid=Api&h=220&P=0"
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

                

            </div>
        </div>

        <jsp:include page="/common/footer.jsp" />

    </body>
</html>