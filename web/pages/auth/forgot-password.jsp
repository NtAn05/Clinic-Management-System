<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="otpSent" value="${requestScope.otpSent or sessionScope.forgotPasswordEmail != null}" />
<c:set var="verified" value="${requestScope.verified or sessionScope.forgotPasswordVerified == true}" />
<c:set var="emailValue" value="${not empty requestScope.email ? requestScope.email : sessionScope.forgotPasswordEmail}" />
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quên mật khẩu - Phòng khám ABC</title>
    <style>
        body { font-family: 'Segoe UI', sans-serif; margin: 0; background: #f4f7fe; }
        .wrap { min-height: calc(100vh - 160px); display:flex; align-items:center; justify-content:center; padding: 20px; }
        .card { background:#fff; width:100%; max-width:430px; border-radius:14px; box-shadow:0 8px 24px rgba(0,0,0,.08); padding:24px; }
        .card h2 { margin:0 0 8px; color:#0061ff; }
        .muted { color:#666; font-size:14px; margin-bottom:16px; }
        .group { margin-bottom:12px; }
        label { display:block; font-weight:600; margin-bottom:6px; }
        input { width:100%; padding:11px; border:1px solid #ddd; border-radius:8px; box-sizing:border-box; }
        button { width:100%; padding:11px; border:none; background:#0061ff; color:#fff; border-radius:8px; font-weight:600; cursor:pointer; }
        .sub-btn { background:#fff; color:#0061ff; border:1px solid #0061ff; margin-top:8px; }
        .alert-error { background:#ffe6e6; color:#b91c1c; padding:10px; border-radius:8px; margin-bottom:12px; }
        .alert-success { background:#dff7e6; color:#166534; padding:10px; border-radius:8px; margin-bottom:12px; }
        .link { margin-top:12px; text-align:center; }
    </style>
</head>
<body>
    <jsp:include page="/common/header.jsp" />
    <div class="wrap">
        <div class="card">
            <h2>Quên mật khẩu</h2>
            <p class="muted">Nhập Gmail để nhận OTP và đặt lại mật khẩu.</p>

            <c:if test="${not empty error}"><div class="alert-error">${error}</div></c:if>
            <c:if test="${not empty success}"><div class="alert-success">${success}</div></c:if>

            <c:if test="${not otpSent}">
                <form method="post" action="${pageContext.request.contextPath}/forgot-password">
                    <input type="hidden" name="action" value="sendOtp">
                    <div class="group">
                        <label>Gmail</label>
                        <input type="email" name="email" value="${emailValue}" required>
                    </div>
                    <button type="submit">Gửi OTP</button>
                </form>
            </c:if>

            <c:if test="${otpSent and not verified}">
                <form method="post" action="${pageContext.request.contextPath}/forgot-password">
                    <input type="hidden" name="action" value="verifyOtp">
                    <div class="group">
                        <label>Gmail</label>
                        <input type="email" value="${emailValue}" disabled>
                    </div>
                    <div class="group">
                        <label>OTP</label>
                        <input type="text" name="otp" maxlength="6" required>
                    </div>
                    <button type="submit">Xác thực OTP</button>
                </form>
                <form method="post" action="${pageContext.request.contextPath}/forgot-password">
                    <input type="hidden" name="action" value="sendOtp">
                    <input type="hidden" name="email" value="${emailValue}">
                    <button type="submit" class="sub-btn">Gửi lại OTP</button>
                </form>
            </c:if>

            <c:if test="${verified}">
                <form method="post" action="${pageContext.request.contextPath}/forgot-password">
                    <input type="hidden" name="action" value="resetPassword">
                    <div class="group">
                        <label>Mật khẩu mới</label>
                        <input type="password" name="newPassword" minlength="6" required>
                    </div>
                    <div class="group">
                        <label>Nhập lại mật khẩu</label>
                        <input type="password" name="confirmPassword" minlength="6" required>
                    </div>
                    <button type="submit">Đổi mật khẩu</button>
                </form>
            </c:if>

            <div class="link">
                <a href="${pageContext.request.contextPath}/login">← Quay lại đăng nhập</a>
            </div>
        </div>
    </div>
    <jsp:include page="/common/footer.jsp" />
</body>
</html>