<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.sql.*"%>
<%@page import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%!
    class HelperDAO extends dal.DoctorDAO {
        public Connection getDbConnection() { return this.connection; }
    }
%>

<%
    List<Map<String, Object>> directDoctors = new ArrayList<>();
    try {
        HelperDAO dao = new HelperDAO();
        Connection conn = dao.getDbConnection();
        // Lấy dữ liệu bác sĩ (bao gồm qualification và clinic_address)
        String sql = "SELECT d.doctor_id, u.full_name, d.specialization, d.qualification, d.clinic_address, d.experience_years, d.rating, u.image_url " +
                     "FROM doctors d JOIN users u ON d.user_id = u.user_id " +
                     "WHERE u.status = 'active' ORDER BY d.rating DESC LIMIT 3";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            Map<String, Object> doc = new HashMap<>();
            doc.put("doctorId", rs.getInt("doctor_id"));
            doc.put("fullName", rs.getString("full_name"));
            doc.put("specialization", rs.getString("specialization"));
            doc.put("qualification", rs.getString("qualification"));
            doc.put("clinicAddress", rs.getString("clinic_address"));
            doc.put("exp", rs.getInt("experience_years"));
            doc.put("rating", rs.getDouble("rating"));
            doc.put("image", rs.getString("image_url"));
            directDoctors.add(doc);
        }
        request.setAttribute("homeDoctors", directDoctors);
        rs.close(); ps.close();
    } catch(Exception e) {
        request.setAttribute("homeDoctorError", "Lỗi: " + e.getMessage());
    }
%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Phòng Khám ABC - Chuyên Gia Hàng Đầu</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            :root {
                --primary: #0061ff;
                --dark: #1e293b;
                --accent: #38bdf8;
                --bg: #f8fafc;
            }
            body {
                font-family: 'Inter', 'Segoe UI', sans-serif;
                background-color: var(--bg);
                margin: 0;
                color: var(--dark);
            }
            .container {
                max-width: 1100px;
                margin: 0 auto;
                padding: 0 20px;
            }

            /* HERO SECTION */
            .hero {
                background: linear-gradient(135deg, #ffffff 0%, #e0f2fe 100%);
                padding: 100px 0;
                position: relative;
                overflow: hidden;
            }
            .hero-flex {
                display: flex;
                align-items: center;
                gap: 60px;
            }
            .hero-text {
                flex: 1.2;
            }
            .hero-title {
                font-size: 56px;
                font-weight: 800;
                line-height: 1.1;
                margin: 0 0 24px;
                color: var(--primary);
            }
            .hero-title span {
                color: var(--dark);
            }
            .hero-sub {
                font-size: 19px;
                color: #475569;
                margin-bottom: 40px;
                line-height: 1.6;
            }

            .btn-main {
                background: var(--primary);
                color: white;
                padding: 18px 36px;
                border-radius: 50px;
                font-weight: 700;
                text-decoration: none;
                display: inline-flex;
                align-items: center;
                gap: 12px;
                box-shadow: 0 10px 25px rgba(0, 97, 255, 0.3);
                transition: 0.4s;
                border: none;
                cursor: pointer;
            }
            .btn-main:hover {
                transform: translateY(-3px);
                box-shadow: 0 15px 30px rgba(0, 97, 255, 0.4);
            }

            /* DOCTOR SECTION */
            .doctor-section {
                padding: 100px 0;
            }
            .section-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 50px;
            }
            .tag {
                background: #dbeafe;
                color: var(--primary);
                padding: 6px 14px;
                border-radius: 50px;
                font-size: 14px;
                font-weight: 700;
            }

            .doctor-card {
                background: white;
                border-radius: 24px;
                padding: 30px;
                margin-bottom: 30px;
                display: flex;
                gap: 40px;
                align-items: center;
                transition: 0.4s;
                border: 1px solid #f1f5f9;
                box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05);
            }
            .doctor-card:hover {
                transform: scale(1.02);
                box-shadow: 0 20px 40px rgba(0,0,0,0.08);
                border-color: var(--primary);
            }

            .doc-avatar-wrap {
                position: relative;
            }
            .doc-img {
                width: 180px;
                height: 180px;
                border-radius: 24px;
                object-fit: cover;
            }
            .badge-exp {
                position: absolute;
                bottom: -10px;
                right: -10px;
                background: white;
                padding: 8px 12px;
                border-radius: 12px;
                font-size: 12px;
                font-weight: 800;
                box-shadow: 0 4px 10px rgba(0,0,0,0.1);
                border: 1px solid #eee;
            }

            .doc-info {
                flex: 1;
            }
            .doc-name {
                font-size: 26px;
                font-weight: 800;
                margin: 0 0 8px;
                color: var(--dark);
            }
            .doc-spec {
                color: var(--primary);
                font-weight: 700;
                font-size: 16px;
                margin-bottom: 12px;
                display: block;
            }
            .doc-desc {
                color: #64748b;
                line-height: 1.6;
                margin-bottom: 20px;
                font-size: 15px;
            }

            .doc-meta {
                display: flex;
                gap: 20px;
                align-items: center;
            }
            .meta-item {
                display: flex;
                align-items: center;
                gap: 6px;
                font-size: 14px;
                color: #475569;
                font-weight: 600;
            }

            .btn-book {
                background: #f1f5f9;
                color: var(--dark);
                padding: 14px 28px;
                border-radius: 14px;
                text-decoration: none;
                font-weight: 700;
                transition: 0.3s;
                display: inline-block;
            }
            .btn-book:hover {
                background: var(--primary);
                color: white;
            }

            .view-all-container {
                text-align: right;
            }
            .link-all {
                color: var(--primary);
                text-decoration: none;
                font-weight: 700;
                display: inline-flex;
                align-items: center;
                gap: 8px;
                cursor: pointer;
            }
        </style>
    </head>
    <body>
        <jsp:include page="common/header.jsp" />

        <section class="hero">
            <div class="container hero-flex">
                <div class="hero-text">
                    <span class="tag">Hệ thống Y tế Chất lượng cao</span>
                    <h1 class="hero-title"><span>Sức Khỏe Của Bạn,</span><br>Trách Nhiệm Của Chúng Tôi.</h1>
                    <p class="hero-sub">Trải nghiệm dịch vụ y tế đẳng cấp với đội ngũ bác sĩ chuyên khoa đầu ngành. Chúng tôi cam kết mang lại sự an tâm tuyệt đối cho bạn và gia đình.</p>

                   <a href="${pageContext.request.contextPath}/${empty sessionScope.account ? 'login' : 'listofdoctorservlet'}" class="btn-main">
                    <i class="fas fa-calendar-check"></i> Đặt lịch hẹn ngay
                </a>
                </div>
                <div style="flex: 1;">
                    <img src="https://img.freepik.com/free-photo/doctor-working-laptop-medical-office_23-2148980721.jpg" style="width: 110%; border-radius: 40px; transform: rotate(2deg); box-shadow: 20px 20px 60px rgba(0,0,0,0.1);" alt="Medical Team">
                </div>
            </div>
        </section>

        <section class="doctor-section">
            <div class="container">
                <div class="section-header">
                    <div>
                        <h2 style="font-size: 36px; margin: 0; font-weight: 800;">Chuyên Gia Ưu Tú</h2>
                        <p style="color: #64748b; margin-top: 10px;">Đội ngũ y bác sĩ tận tâm với kinh nghiệm chuyên môn sâu sắc.</p>
                    </div>
                    <div class="view-all-container">
                         <a href="${pageContext.request.contextPath}/${empty sessionScope.account ? 'login' : 'listofdoctorservlet'}" class="link-all">Xem tất cả bác sĩ <i class="fas fa-chevron-right"></i></a>
                    </div>
                </div>

                <c:forEach items="${homeDoctors}" var="doctor">
                    <div class="doctor-card">
                        <div class="doc-avatar-wrap">
                            <img src="${fn:startsWith(doctor.image, 'http') ? doctor.image : pageContext.request.contextPath.concat(doctor.image)}" 
                                 class="doc-img" onerror="this.src='https://cdn-icons-png.flaticon.com/512/3774/3774299.png'">
                            <div class="badge-exp">${doctor.exp}+ Năm KN</div>
                        </div>

                        <div class="doc-info">
                            <span class="doc-spec">${doctor.specialization}</span>
                            <h3 class="doc-name">${doctor.fullName}</h3>
                            <p class="doc-desc">
                                Trình độ chuyên môn: <strong>${doctor.qualification}</strong>. Với kinh nghiệm dày dặn, bác sĩ luôn được bệnh nhân đánh giá cao về phác đồ điều trị và thái độ chăm sóc tận tình.
                            </p>
                            <div class="doc-meta">
                                <div class="meta-item"><i class="fas fa-star" style="color: #f59e0b;"></i> ${doctor.rating} / 5.0</div>
                                
                            </div>
                        </div>

                        <div>
                            <c:choose>
                                <c:when test="${empty sessionScope.account}">
                                    <a href="${pageContext.request.contextPath}/login" class="btn-book">Hẹn gặp bác sĩ</a>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/${empty sessionScope.account ? 'login' : 'listofdoctorservlet'}" class="btn-book">Hẹn gặp bác sĩ</a>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </section>

        

        <script>
            function showToast(event) {
                if (event)
                    event.preventDefault();
                const toast = document.getElementById('loginToast');
                toast.style.display = 'block';
                setTimeout(() => {
                    toast.style.display = 'none';
                }, 3000);
            }
        </script>

        <jsp:include page="common/footer.jsp" />
    </body>
</html>