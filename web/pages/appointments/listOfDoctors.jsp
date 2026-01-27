<%-- 
    Document   : listOfDoctors
    Created on : Jan 26, 2026, 10:46:29 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link rel="stylesheet" href="appointment_css/listOfDoctors.css">
    </head>
    <body>
        <div class="container">
            <!-- Danh sách bác sĩ -->
            <div class="doctor-list">
                <h2>Đội ngũ bác sĩ da liễu</h2>

                <div class="cards">
                    <c:forEach items="${doctors}" var="d">
                        <div class="card">
                            <img src="https://via.placeholder.com/100">

                            <h3>${d.name}</h3>
                            <p class="degree">${d.degree}</p>
                            <p class="desc">${d.specialty}</p>

                            <div class="info">
                                <span>⏱ ${d.experience} năm</span>
                                <span>⭐ ${d.rating} (${d.reviews})</span>
                            </div>

                            <p class="price">
                            <fmt:formatNumber value="${d.price}" type="number"/>đ
                            </p>

                            <button>Đặt dịch vụ</button>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <!-- Bộ lọc -->
            <div class="filter">
                <h3>Tìm kiếm & Lọc</h3>

                <label>Tìm kiếm theo tên bác sĩ</label>
                <input type="text" placeholder="Nhập tên bác sĩ">

                <label>Khoảng giá</label>
                <div class="price-range">
                    <input type="number" placeholder="Từ">
                    <input type="number" placeholder="Đến">
                </div>

                <label>Năm kinh nghiệm</label>
                <select>
                    <option>Tất cả</option>
                    <option>5+ năm</option>
                    <option>10+ năm</option>
                </select>

                <label>Sắp xếp theo</label>
                <select>
                    <option>Bác sĩ nổi bật</option>
                    <option>Giá thấp → cao</option>
                    <option>Giá cao → thấp</option>
                </select>

                <button class="filter-btn">Đặt lại bộ lọc</button>
            </div>
        </div>
    </body>
</html>