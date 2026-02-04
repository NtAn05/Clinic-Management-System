<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Trang Chủ</title>
    </head>
    <style>
        :root {
            --primary: #0061ff;       /* Màu xanh chủ đạo */
            --primary-dark: #004ecc;
            --text-dark: #333;
            --text-gray: #666;
            --bg-light: #f9fbff;      /* Màu nền nhạt */
        }

        body {
            font-family: 'Segoe UI', sans-serif;
            margin: 0;
            padding: 0;
            color: var(--text-dark);
            overflow-x: hidden; /* Tránh thanh cuộn ngang */
        }

        .container-custom {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 20px;
        }

        /* --- PHẦN 1: HERO SECTION (Banner) --- */
        .hero-section {
            padding: 60px 0;
            background: linear-gradient(to right, #ffffff 50%, #f0f7ff 50%); /* Hiệu ứng nền 2 màu */
            display: flex;
            align-items: center;
            min-height: 500px;
        }

        .hero-wrap {
            display: flex;
            align-items: center;
            gap: 50px;
        }

        /* Cột bên trái: Nội dung */
        .hero-content {
            flex: 1;
        }

        .badge-blue {
            display: inline-block;
            background: #e6f0ff;
            color: var(--primary);
            padding: 8px 16px;
            border-radius: 20px;
            font-weight: 600;
            font-size: 14px;
            margin-bottom: 20px;
        }

        .hero-title {
            font-size: 48px;
            line-height: 1.2;
            color: var(--primary);
            margin-bottom: 20px;
            font-weight: 800;
        }

        .hero-desc {
            font-size: 16px;
            color: var(--text-gray);
            line-height: 1.6;
            margin-bottom: 30px;
            max-width: 90%;
        }

        .hero-btns {
            display: flex;
            gap: 15px;
            margin-bottom: 40px;
        }

        .btn {
            padding: 12px 25px;
            border-radius: 8px;
            font-weight: 600;
            text-decoration: none;
            transition: 0.3s;
            display: inline-flex;
            align-items: center;
            gap: 8px;
        }

        .btn-primary {
            background: var(--primary);
            color: white;
            border: 2px solid var(--primary);
        }
        .btn-primary:hover {
            background: var(--primary-dark);
        }

        .btn-outline {
            background: transparent;
            color: var(--primary);
            border: 2px solid #dde7ff;
        }
        .btn-outline:hover {
            border-color: var(--primary);
        }

        /* Danh sách tính năng (Grid 2 cột) */
        .features-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px;
        }
        .feature-item {
            display: flex;
            align-items: center;
            gap: 10px;
            font-size: 14px;
            font-weight: 500;
            color: var(--text-dark);
        }
        .feature-item i {
            color: var(--primary); /* Icon màu xanh */
        }

        /* Cột bên phải: Ảnh bác sĩ */
        .hero-image {
            flex: 1;
            position: relative;
        }

        .hero-img-main {
            width: 100%;
            border-radius: 20px;
            /*box-shadow: 20px 20px 0px #e6f0ff;  Hiệu ứng bóng đổ khối */
            display: block;
        }

        /* Thẻ nổi (Floating Cards) */
        .float-card {
            position: absolute;
            background: white;
            padding: 15px 20px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            display: flex;
            align-items: center;
            gap: 10px;
            font-weight: bold;
            font-size: 14px;
            animation: float 3s ease-in-out infinite;
        }

        .float-card span {
            display: block;
            color: var(--text-gray);
            font-size: 12px;
            font-weight: normal;
        }

        .card-exp {
            top: 30px;
            left: -30px;
        }
        .card-user {
            bottom: 40px;
            right: -20px;
        }

        @keyframes float {
            0%, 100% {
                transform: translateY(0);
            }
            50% {
                transform: translateY(-10px);
            }
        }

        /* --- PHẦN 2: ABOUT SECTION (Giới thiệu) --- */
        .about-section {
            padding: 80px 0;
            background: white;
        }

        .section-badge {
            background: var(--primary);
            color: white;
            padding: 5px 15px;
            border-radius: 15px;
            font-size: 13px;
            font-weight: bold;
            margin-bottom: 15px;
            display: inline-block;
        }

        /* --- PHẦN 4: THỐNG KÊ (STATS BANNER) --- */
        .stats-section {
            padding: 60px 0;
            background: var(--primary); /* Nền xanh đậm */
            color: white;
        }

        .stats-grid {
            display: flex;
            justify-content: space-around;
            text-align: center;
        }

        .stat-number {
            font-size: 48px;
            font-weight: 800;
            margin-bottom: 5px;
        }

        .stat-label {
            font-size: 16px;
            opacity: 0.9;
        }

        /* --- PHẦN 5: QUY TRÌNH (STEPS) --- */
        .steps-section {
            padding: 80px 0;
            background: white;
            text-align: center;
        }

        .steps-container {
            display: flex;
            justify-content: space-between;
            margin-top: 50px;
            position: relative;
        }

        /* Đường kẻ nối các bước */
        .steps-container::before {
            content: '';
            position: absolute;
            top: 40px;
            left: 100px;
            right: 100px;
            height: 2px;
            background: #eee;
            z-index: 0;
        }

        .step-item {
            position: relative;
            z-index: 1;
            flex: 1;
            padding: 0 20px;
        }

        .step-number {
            width: 80px;
            height: 80px;
            background: white;
            border: 2px solid var(--primary);
            color: var(--primary);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            font-weight: bold;
            margin: 0 auto 20px auto;
        }

        .step-item:hover .step-number {
            background: var(--primary);
            color: white;
        }
    </style>
    <body>

        <jsp:include page="common/header.jsp" />
        <div class="hero-section">
            <div class="container-custom hero-wrap">
                <div class="hero-content">
                    <span class="badge-blue">Phòng khám uy tín hàng đầu</span>
                    <h1 class="hero-title">Chăm Sóc Sức Khỏe <br> Toàn Diện</h1>
                    <p class="hero-desc">
                        Phòng khám ABC với đội ngũ y bác sĩ chuyên môn cao, trang thiết bị hiện đại, cam kết mang đến dịch vụ y tế chất lượng tốt nhất cho bạn và gia đình.
                    </p>

                    <div class="hero-btns">
                        <a href="${pageContext.request.contextPath}/listofdoctorservlet" class="btn btn-primary">
                            <i class="fas fa-calendar-check"></i> Đặt lịch khám
                        </a>
                        <%--<c:choose>
                            <c:when test="${sessionScope.account == null}">
                                <a href="${pageContext.request.contextPath}/pages/auth/login.jsp" class="btn btn-primary">
                                    <i class="fas fa-calendar-check"></i> Đặt lịch khám
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/appointmentservlet" class="btn btn-primary">
                                    <i class="fas fa-calendar-check"></i> Đặt lịch khám
                                </a>
                            </c:otherwise>
                        </c:choose> --%>
                        <a href="#" class="btn btn-outline">
                            <i class="fas fa-list-ul"></i> Xem dịch vụ
                        </a>
                    </div>

                    <div class="features-grid">
                        <div class="feature-item"><i class="fas fa-check-circle"></i> Đội ngũ bác sĩ chuyên môn cao</div>
                        <div class="feature-item"><i class="fas fa-check-circle"></i> Trang thiết bị hiện đại</div>
                        <div class="feature-item"><i class="fas fa-check-circle"></i> Dịch vụ 24/7</div>
                        <div class="feature-item"><i class="fas fa-check-circle"></i> Chi phí hợp lý</div>
                    </div>
                </div>

                <div class="hero-image">
                    <img src="https://img.freepik.com/free-photo/doctor-working-laptop-medical-office_23-2148980721.jpg?w=900" alt="Doctor" class="hero-img-main">

                    <div class="float-card card-exp">
                        <div style="font-size: 24px; color: var(--primary); font-weight: 800;">15+</div>
                        <div>
                            Năm kinh nghiệm<br>
                            <span>Trong lĩnh vực y tế</span>
                        </div>
                    </div>

                    <div class="float-card card-user">
                        <div style="font-size: 24px; color: var(--primary); font-weight: 800;">10k+</div>
                        <div>
                            Bệnh nhân<br>
                            <span>Hài lòng mỗi năm</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="about-section">
            <div class="container-custom hero-wrap">
                <div class="hero-image">
                    <img src="https://img.freepik.com/free-photo/blur-hospital_1203-7972.jpg?w=900" alt="Hospital" class="hero-img-main" style="border-radius: 20px 20px 20px 20px;">
                </div>

                <div class="hero-content" style="padding-left: 50px;">
                    <span class="section-badge">Về chúng tôi</span>
                    <h2 style="font-size: 36px; color: var(--primary); margin: 15px 0;">Phòng Khám ABC - Nơi Chăm Sóc Sức Khỏe Toàn Diện</h2>
                    <p style="color: var(--text-gray); line-height: 1.6; margin-bottom: 25px;">
                        Với hơn 15 năm kinh nghiệm trong lĩnh vực y tế, chúng tôi tự hào là địa chỉ tin cậy cho việc chăm sóc sức khỏe của bạn và gia đình. Chúng tôi không ngừng nỗ lực nâng cao chất lượng dịch vụ.
                    </p>

                    <div style="display: flex; gap: 30px;">
                        <div>
                            <i class="fas fa-user-md" style="font-size: 30px; color: var(--primary); margin-bottom: 10px;"></i>
                            <h4 style="margin: 0;">Đội ngũ chuyên môn</h4>
                            <p style="font-size: 13px; color: #666;">Hội tụ các bác sĩ đầu ngành</p>
                        </div>
                        <div>
                            <i class="fas fa-hospital" style="font-size: 30px; color: var(--primary); margin-bottom: 10px;"></i>
                            <h4 style="margin: 0;">Cơ sở vật chất</h4>
                            <p style="font-size: 13px; color: #666;">Trang thiết bị hiện đại chuẩn 5*</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="stats-section">
            <div class="container-custom stats-grid">
                <div>
                    <div class="stat-number">15+</div>
                    <div class="stat-label">Năm kinh nghiệm</div>
                </div>
                <div>
                    <div class="stat-number">50+</div>
                    <div class="stat-label">Bác sĩ chuyên khoa</div>
                </div>
                <div>
                    <div class="stat-number">24/7</div>
                    <div class="stat-label">Hỗ trợ y tế</div>
                </div>
                <div>
                    <div class="stat-number">10k+</div>
                    <div class="stat-label">Bệnh nhân tin dùng</div>
                </div>
            </div>
        </div>

        <div class="steps-section">
            <div class="container-custom">
                <span class="badge-blue">Quy trình làm việc</span>
                <h2 class="section-title">Đặt Lịch Khám Đơn Giản</h2>

                <div class="steps-container">
                    <div class="step-item">
                        <div class="step-number">01</div>
                        <h3>Đặt Lịch Online</h3>
                        <p style="color: #666; font-size: 14px;">Đăng ký tài khoản và chọn bác sĩ, giờ khám mong muốn.</p>
                    </div>

                    <div class="step-item">
                        <div class="step-number">02</div>
                        <h3>Đến Phòng Khám</h3>
                        <p style="color: #666; font-size: 14px;">Đến đúng giờ hẹn, không cần chờ đợi lấy số.</p>
                    </div>

                    <div class="step-item">
                        <div class="step-number">03</div>
                        <h3>Khám & Điều Trị</h3>
                        <p style="color: #666; font-size: 14px;">Được bác sĩ thăm khám tận tình và nhận phác đồ điều trị.</p>
                    </div>
                </div>
            </div>
        </div>
        <jsp:include page="common/footer.jsp" />

    </body>
</html>