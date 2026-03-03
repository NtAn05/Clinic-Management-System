<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Thanh toán thất bại</title>

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
                        <h3>❌ Thanh toán thất bại hoặc đã huỷ!</h2</h3>

                        <div class="success-wrapper">
                            <div class="success-icon-box">
                                <img src="https://tse1.mm.bing.net/th/id/OIP.MD5CapzfmiFzbwPNCYDVvwHaGr?pid=ImgDet&w=187&h=168&c=7&dpr=1.3&o=7&rm=3"
                                     alt="Thất bại"
                                     class="success-icon">
                            </div>
                        </div>

                        <label style="color: red">Cuộc hẹn của bạn bị hủy !!!</label>
                        <br>

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