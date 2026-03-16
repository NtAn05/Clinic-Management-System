<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.sql.*"%>
<%@page import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%!
    // Khai báo Class phụ trợ ở mức toàn cục để lấy Connection an toàn từ DBContext
    class HelperDAO extends dal.DoctorDAO {
        public Connection getDbConnection() {
            return this.connection; 
        }
    }
%>

<%
    // Kéo dữ liệu thẳng lên JSP
    List<Map<String, Object>> directDoctors = new ArrayList<>();
    try {
        HelperDAO dao = new HelperDAO();
        Connection conn = dao.getDbConnection();
        
        String keyword = request.getParameter("doctorKeyword");
        
        String sql = "SELECT d.doctor_id, u.full_name, d.specialization, d.experience_years, d.rating, d.price_booking, d.image_url " +
                     "FROM doctors d JOIN users u ON d.user_id = u.user_id " +
                     "WHERE u.role = 'doctor' AND u.status = 'active' ";
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " AND (u.full_name LIKE ? OR d.specialization LIKE ?) ";
        }
        sql += " ORDER BY d.rating DESC LIMIT 4";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        if (keyword != null && !keyword.trim().isEmpty()) {
            ps.setString(1, "%" + keyword.trim() + "%");
            ps.setString(2, "%" + keyword.trim() + "%");
        }
        
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            Map<String, Object> doc = new HashMap<>();
            doc.put("doctorId", rs.getInt("doctor_id"));
            doc.put("fullName", rs.getString("full_name"));
            doc.put("specialization", rs.getString("specialization"));
            doc.put("experience_years", rs.getInt("experience_years"));
            doc.put("rating", rs.getDouble("rating"));
            doc.put("price", rs.getDouble("price_booking"));
            doc.put("image", rs.getString("image_url"));
            directDoctors.add(doc);
        }
        rs.close(); ps.close(); 
        
        request.setAttribute("homeDoctors", directDoctors);
    } catch(Exception e) {
        request.setAttribute("homeDoctorError", "Lỗi tải danh sách: " + e.getMessage());
    }
%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Trang Chủ - Phòng Khám ABC</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

        <style>
            :root {
                --primary: #0061ff;
                --primary-dark: #004ecc;
                --text-dark: #333;
                --text-gray: #666;
                --bg-light: #f9fbff;
            }
            body {
                font-family: 'Segoe UI', sans-serif;
                margin: 0;
                padding: 0;
                color: var(--text-dark);
                overflow-x: hidden;
            }
            .container-custom {
                max-width: 1200px;
                margin: 0 auto;
                padding: 0 20px;
            }

            /* --- HERO SECTION --- */
            .hero-section {
                padding: 80px 0 100px 0;
                background: linear-gradient(to right, #ffffff 50%, #f0f7ff 50%);
                display: flex;
                align-items: center;
                min-height: 500px;
                position: relative;
            }
            .hero-wrap { display: flex; align-items: center; gap: 50px; }
            .hero-content { flex: 1; }
            .badge-blue {
                display: inline-block; background: #e6f0ff; color: var(--primary);
                padding: 8px 16px; border-radius: 20px; font-weight: 600; font-size: 14px; margin-bottom: 20px;
            }
            .hero-title { font-size: 48px; line-height: 1.2; color: var(--primary); margin-bottom: 20px; font-weight: 800; }
            .hero-desc { font-size: 16px; color: var(--text-gray); line-height: 1.6; margin-bottom: 30px; max-width: 90%; }
            .hero-btns { display: flex; gap: 15px; margin-bottom: 35px; }
            .btn {
                padding: 12px 25px; border-radius: 8px; font-weight: 600; text-decoration: none;
                transition: 0.3s; display: inline-flex; align-items: center; gap: 8px;
            }
            .btn-primary { background: var(--primary); color: white; border: 2px solid var(--primary); }
            .btn-primary:hover { background: var(--primary-dark); }
            .btn-outline { background: transparent; color: var(--primary); border: 2px solid #dde7ff; }
            .btn-outline:hover { border-color: var(--primary); }

            .features-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
            .feature-item { display: flex; align-items: center; gap: 10px; font-size: 14px; font-weight: 500; }
            .feature-item i { color: var(--primary); }

            .hero-image { flex: 1; position: relative; }
            .hero-img-main { width: 100%; border-radius: 20px; display: block; }
            .float-card {
                position: absolute; background: white; padding: 15px 20px; border-radius: 12px;
                box-shadow: 0 10px 30px rgba(0,0,0,0.1); display: flex; align-items: center; gap: 10px;
                font-weight: bold; font-size: 14px; animation: float 3s ease-in-out infinite;
            }
            .float-card span { display: block; color: var(--text-gray); font-size: 12px; font-weight: normal; }
            .card-exp { top: 30px; left: -30px; }
            .card-user { bottom: 40px; right: -20px; }
            @keyframes float { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-10px); } }

            /* --- TÌM KIẾM NỔI --- */
            .floating-search-wrapper { margin-top: -35px; position: relative; z-index: 10; display: flex; justify-content: center; padding: 0 20px; }
            .floating-search-box {
                background: #fff; padding: 10px 10px 10px 25px; border-radius: 50px;
                box-shadow: 0 15px 35px rgba(0, 97, 255, 0.12); display: flex; gap: 15px;
                width: 100%; max-width: 850px; align-items: center; border: 1px solid #eef2ff;
            }
            .search-icon-wrapper { color: #94a3b8; font-size: 20px; }
            .floating-search-input { flex: 1; border: none; padding: 12px 10px; font-size: 15px; outline: none; background: transparent; color: #334155; }
            .floating-search-submit {
                background: linear-gradient(135deg, #0061ff, #2d8cff); color: #fff; border: none;
                padding: 14px 35px; border-radius: 50px; font-weight: 700; font-size: 15px; cursor: pointer;
                transition: 0.3s; display: flex; align-items: center; gap: 8px;
            }
            .floating-search-submit:hover { transform: translateY(-2px); box-shadow: 0 8px 20px rgba(0, 97, 255, 0.3); }

            .section-title { font-size: 32px; color: var(--primary); margin-bottom: 20px; }
            .steps-section { padding: 80px 0; background: white; text-align: center; }
            .steps-container { display: flex; justify-content: space-between; margin-top: 50px; position: relative; }
            .steps-container::before { content: ''; position: absolute; top: 40px; left: 100px; right: 100px; height: 2px; background: #eee; z-index: 0; }
            .step-item { position: relative; z-index: 1; flex: 1; padding: 0 20px; }
            .step-number {
                width: 80px; height: 80px; background: white; border: 2px solid var(--primary); color: var(--primary);
                border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 24px; font-weight: bold; margin: 0 auto 20px auto;
            }
            .step-item:hover .step-number { background: var(--primary); color: white; }

            /* --- DANH SÁCH BÁC SĨ --- */
            .doctors-section { padding: 80px 0; background: #f8fbff; }
            .doctors-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 40px; }
            .doctor-grid-v2 { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 24px; }
            .doctor-card-v2 {
                background: #fff; border-radius: 16px; box-shadow: 0 4px 15px rgba(0,0,0,0.04);
                padding: 25px 20px 20px 20px; transition: all 0.3s ease; border: 1px solid #f1f5f9;
                display: flex; flex-direction: column; align-items: center; text-align: center;
            }
            .doctor-card-v2:hover { transform: translateY(-6px); box-shadow: 0 15px 30px rgba(0,97,255,0.08); border-color: #dbe8ff; }
            .doc-img-v2 { width: 120px; height: 120px; border-radius: 50%; object-fit: cover; border: 4px solid #f0f7ff; margin-bottom: 16px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); }
            .doc-name-v2 { font-size: 19px; color: #1e293b; font-weight: 700; margin: 0 0 8px 0; }
            .doc-spec-v2 { color: var(--primary); background: #eff6ff; padding: 5px 14px; border-radius: 20px; font-size: 13px; font-weight: 600; margin-bottom: 16px; }
            .doc-stats-v2 { display: flex; justify-content: center; gap: 15px; width: 100%; margin-bottom: 16px; padding-bottom: 16px; border-bottom: 1px dashed #e2e8f0; }
            .stat-item-v2 { font-size: 13px; color: #64748b; display: flex; align-items: center; gap: 6px; }
            .stat-item-v2 i { color: #94a3b8; }
            .stat-item-v2 i.fa-star { color: #f59e0b; }
            .doc-footer-v2 { width: 100%; display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
            .doc-price-v2 { font-size: 17px; font-weight: 700; color: var(--primary); }

            .btn-book-v2 {
                width: 100%; background: var(--primary); color: #fff; border: none; padding: 12px;
                border-radius: 10px; font-weight: 600; font-size: 14px; cursor: pointer; transition: 0.3s;
                text-decoration: none; display: block; box-sizing: border-box; text-align: center;
            }
            .btn-book-v2:hover { background: var(--primary-dark); box-shadow: 0 4px 12px rgba(0, 97, 255, 0.2); }
            .doctor-empty { background: #fff; padding: 20px; border-radius: 12px; color: #475569; text-align: center; width: 100%; grid-column: 1 / -1; }
        </style>
    </head>
    <body>
        <jsp:include page="common/header.jsp" />

        <div class="hero-section">
            <div class="container-custom hero-wrap">
                <div class="hero-content">
                    <span class="badge-blue">Phòng khám uy tín hàng đầu</span>
                    <h1 class="hero-title">Chăm Sóc Sức Khỏe <br> Toàn Diện</h1>
                    <p class="hero-desc">Phòng khám ABC với đội ngũ y bác sĩ chuyên môn cao, trang thiết bị hiện đại, cam kết mang đến dịch vụ y tế chất lượng tốt nhất.</p>
                    <div class="hero-btns">
                        <a href="${pageContext.request.contextPath}/listofdoctorservlet" class="btn btn-primary"><i class="fas fa-calendar-check"></i> Đặt lịch khám</a>
                        <a href="#" class="btn btn-outline"><i class="fas fa-list-ul"></i> Xem dịch vụ</a>
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
                        <div>Năm kinh nghiệm<br><span>Trong lĩnh vực y tế</span></div>
                    </div>
                    <div class="float-card card-user">
                        <div style="font-size: 24px; color: var(--primary); font-weight: 800;">10k+</div>
                        <div>Bệnh nhân<br><span>Hài lòng mỗi năm</span></div>
                    </div>
                </div>
            </div>
        </div>

        <div class="floating-search-wrapper">
            <form class="floating-search-box" action="${pageContext.request.contextPath}/index.jsp" method="get">
                <div class="search-icon-wrapper"><i class="fas fa-search"></i></div>
                <input type="text" class="floating-search-input" name="doctorKeyword" value="${param.doctorKeyword}" placeholder="Tìm kiếm bác sĩ theo tên hoặc chuyên khoa...">
                <button type="submit" class="floating-search-submit">Tìm Bác Sĩ</button>
            </form>
        </div>

        <div class="steps-section">
            <div class="container-custom">
                <span class="badge-blue">Quy trình làm việc</span>
                <h2 class="section-title">Đặt Lịch Khám Đơn Giản</h2>
                <div class="steps-container">
                    <div class="step-item"><div class="step-number">01</div><h3>Đặt Lịch Online</h3><p style="color: #666; font-size: 14px;">Đăng ký tài khoản và chọn bác sĩ, giờ khám mong muốn.</p></div>
                    <div class="step-item"><div class="step-number">02</div><h3>Đến Phòng Khám</h3><p style="color: #666; font-size: 14px;">Đến đúng giờ hẹn, không cần chờ đợi lấy số.</p></div>
                    <div class="step-item"><div class="step-number">03</div><h3>Khám & Điều Trị</h3><p style="color: #666; font-size: 14px;">Được bác sĩ thăm khám tận tình và nhận phác đồ điều trị.</p></div>
                </div>
            </div>
        </div>

        <div class="doctors-section">
            <div class="container-custom">
                <div class="doctors-header">
                    <div>
                        <span class="badge-blue">Đội ngũ chuyên gia</span>
                        <h2 class="section-title" style="margin: 10px 0 0; color: var(--primary);">Danh sách bác sĩ nổi bật</h2>
                    </div>
                    <a href="${pageContext.request.contextPath}/listofdoctorservlet" class="btn btn-outline">Xem tất cả</a>
                </div>

                <c:if test="${not empty homeDoctorError}">
                    <div class="doctor-empty" style="margin-bottom: 15px;">${homeDoctorError}</div>
                </c:if>

                <c:choose>
                    <c:when test="${empty homeDoctors}">
                        <div class="doctor-empty">Chưa có bác sĩ phù hợp với từ khóa tìm kiếm.</div>
                    </c:when>
                    <c:otherwise>
                        <div class="doctor-grid-v2">
                            <c:forEach items="${homeDoctors}" var="doctor">
                                <div class="doctor-card-v2">
                                    <img src="${pageContext.request.contextPath}${doctor.image}" alt="${doctor.fullName}" class="doc-img-v2" onerror="this.src='https://cdn-icons-png.flaticon.com/512/3774/3774299.png'">

                                    <h3 class="doc-name-v2">${doctor.fullName}</h3>
                                    <span class="doc-spec-v2">${doctor.specialization}</span>

                                    <div class="doc-stats-v2">
                                        <div class="stat-item-v2"><i class="fas fa-briefcase-medical"></i> ${doctor.experience_years} năm KN</div>
                                        <div class="stat-item-v2"><i class="fas fa-star"></i> ${doctor.rating}/5.0</div>
                                    </div>

                                    <div class="doc-footer-v2">
                                        <span style="font-size: 13px; color: #64748b;">Giá khám:</span>
                                        <span class="doc-price-v2"><fmt:formatNumber value="${doctor.price}" type="number"/> đ</span>
                                    </div>

                                    <c:choose>
                                        <c:when test="${empty sessionScope.account}">
                                            <a href="${pageContext.request.contextPath}/login" class="btn-book-v2">Đặt khám ngay</a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="${pageContext.request.contextPath}/createpatientsservlet?btnDoctorID=${doctor.doctorId}" class="btn-book-v2">Đặt khám ngay</a>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <jsp:include page="common/footer.jsp" />
    </body>
</html>