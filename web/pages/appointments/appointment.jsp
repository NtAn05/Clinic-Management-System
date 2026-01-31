<%-- 
    Document   : appointment
    Created on : Jan 26, 2026, 6:25:11 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link rel="stylesheet" href="appointment_css/appointment.css">
</head>
<body>

<div class="page">

 <jsp:include page="/common/header.jsp" />


  <div class="content">

    <!-- LEFT -->
    <div class="main">

      <!-- STEPS -->
      <div class="steps">
        <div class="step active">1<br><span>Thông tin bệnh nhân</span></div>
        <div class="step">2<br><span>Xác nhận</span></div>
        <div class="step">3<br><span>Thanh toán</span></div>
        <div class="step">4<br><span>Hoàn tất</span></div>
      </div>

      <!-- FORM -->
      <div class="card-box">
        <h3>Chọn đối tượng đặt khám</h3>
        <label><input type="radio" name="type" checked> Đặt cho tôi</label>
        <label><input type="radio" name="type"> Đặt cho người thân</label>
      </div>

      <div class="card-box">
        <h3>Thông tin của bạn</h3>

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

      <div class="actions">
        <button class="btn-outline">Quay lại</button>
        <button class="btn-primary">Tiếp tục</button>
      </div>

    </div>

    <!-- RIGHT -->
    <aside class="doctor-box">
      <img src="https://via.placeholder.com/80">
      <h4>PGS.TS.BS Nguyễn Minh Hải</h4>
      <p>Chuyên khoa Da liễu</p>
      <p class="clinic">📍 Phòng khám Chuyên khoa Da liễu</p>
      <div class="price">500.000 VND</div>
    </aside>

  </div>
 <jsp:include page="/common/footer.jsp" />

</div>

</body>
</html>