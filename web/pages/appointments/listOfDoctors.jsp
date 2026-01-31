<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Danh sách bác sĩ</title>
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/pages/appointments/appointment_css/listOfDoctors.css">
    </head>
    <body>

        <jsp:include page="/common/header.jsp" />

        <div class="container">

            <!-- DANH SÁCH BÁC SĨ -->
            <div class="doctor-list">
                <h2>Đội ngũ bác sĩ da liễu</h2>

                <div class="cards">
                    <c:forEach items="${doctors}" var="d">
                        <div class="card">
                            <img src="${d.image}" alt="Doctor">

                            <h3>${d.fullName}</h3>
                            <p class="degree">${d.qualification}</p>
                            <p class="desc">${d.specialization}</p>
                            <p class="desc">${d.clinic_address}</p>

                            <div class="info">
                                <span>⏱ ${d.experience_years} năm</span>
                                <span>⭐ ${d.rating}</span>
                            </div>

                            <p class="price">
                                <fmt:formatNumber value="${d.price}" type="number"/>đ
                            </p>

                            <button>Đặt dịch vụ</button>
                        </div>
                    </c:forEach>
                </div>
            </div>
            <form method="get" action="${pageContext.request.contextPath}/listofdoctorservlet">

                <!--  FILTER -->
                <div class="filter">
                    <h3>Tìm kiếm & Lọc</h3>

                    <label>Tìm theo tên bác sĩ</label>
                    <input type="text" name="doctorName" placeholder="Nhập tên bác sĩ"   value="${doctorName}">


                    <label>Khoảng giá</label>
                    <div class="price-range">
                        <input type="number" name="priceFrom" placeholder="Từ"   value="${priceFrom}">
                        <input type="number" name="priceTo" placeholder="Đến"   value="${priceTo}">
                        ${error}
                    </div>

                    <label>Năm kinh nghiệm</label>
                    <select name="experience">
                        <option value="">Tất cả</option>
                        <option value="5">5+ năm</option>
                        <option value="10">10+ năm</option>
                    </select>

                    <label>Sắp xếp theo</label>
                    <select name="sort">
                        <option value="">Bác sĩ nổi bật</option>
                        <option value="priceAsc">Giá thấp → cao</option>
                        <option value="priceDesc">Giá cao → thấp</option>
                    </select>
                    <div>
                        <button type="submit" class="filter-btn">
                            🔍 Lọc kết quả
                        </button>

                    </div>
                </div>

            </form>
        </div>

        <jsp:include page="/common/footer.jsp" />

    </body>
</html>
