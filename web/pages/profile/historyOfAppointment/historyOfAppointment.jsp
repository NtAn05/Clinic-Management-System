<%-- 
    Document   : HistoryOfAppointment
    Created on : Mar 3, 2026, 11:55:52 PM
    Author     : Admin
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Các cuộc hẹn</title>
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/pages/profile/historyOfAppointment/historyOfAppointment.css">
    </head>
    <body>

        <jsp:include page="/common/header.jsp" />

        <div class="page">

            <h3>Lịch sử các cuộc hẹn</h3>
            <p>Chi tiết các cuộc hẹn</p>

            <!-- ================= PROFILE HEADER ================= -->
            <div class="appointment-list">

                <c:forEach var="a" items="${appointmentList}">

                    <div class="appointment-card"
                         onclick="openModal(this)"

                         data-name="${a.fullName}"
                         data-phone="${a.phone}"
                         data-email="${a.email}"
                         data-date="${a.appointmentDate}"
                         data-time="${a.appointmentTime}"
                         data-status="${a.status}"
                         data-symptom="${a.symptom}"
                         data-price="${a.price}"
                         data-doctor="${a.specialization}"
                         data-qualification="${a.qualification}"
                         >

                        <div class="doctor-info">
                            <img src="images/${a.image}" class="doctor-img">

                            <div>
                                <h3>${a.specialization}</h3>
                                <p class="price">${a.price} đ</p>
                            </div>
                        </div>

                        <div class="appointment-meta">
                            <p><strong>Ngày:</strong> ${a.appointmentDate}</p>
                            <p><strong>Giờ:</strong> ${a.appointmentTime}</p>
                        </div>

                        <button class="status-btn">${a.status}</button>

                    </div>

                </c:forEach>

            </div>

            <div class="modal-overlay" id="modal">

                <div class="modal">

                    <div class="modal-header">
                        <h2>Chi tiết cuộc hẹn</h2>
                        <span class="close" onclick="closeModal()">✕</span>
                    </div>

                    <div class="modal-body">

                        <h3>Thông tin bệnh nhân</h3>
                        <p><strong>Họ tên:</strong> <span id="mName"></span></p>
                        <p><strong>SĐT:</strong> <span id="mPhone"></span></p>
                        <p><strong>Email:</strong> <span id="mEmail"></span></p>

                        <hr>

                        <h3>Thông tin cuộc hẹn</h3>
                        <p><strong>Ngày:</strong> <span id="mDate"></span></p>
                        <p><strong>Giờ:</strong> <span id="mTime"></span></p>
                        <p><strong>Dịch vụ:</strong> <span id="mDoctor"></span></p>
                        <p><strong>Ghi chú:</strong> <span id="mSymptom"></span></p>

                        <hr>

                        <h3>Tổng chi phí</h3>
                        <p class="total-price"><span id="mPrice"></span> đ</p>

                    </div>

                </div>

            </div>
        </div>
        <jsp:include page="/common/footer.jsp" />


        <script>
            function openModal(card) {

                document.getElementById("modal").style.display = "flex";

                document.getElementById("mName").innerText = card.dataset.name;
                document.getElementById("mPhone").innerText = card.dataset.phone;
                document.getElementById("mEmail").innerText = card.dataset.email;
                document.getElementById("mDate").innerText = card.dataset.date;
                document.getElementById("mTime").innerText = card.dataset.time;
                document.getElementById("mDoctor").innerText = card.dataset.doctor;
                document.getElementById("mSymptom").innerText = card.dataset.symptom;
                document.getElementById("mPrice").innerText = card.dataset.price;
            }

            function closeModal() {
                document.getElementById("modal").style.display = "none";
            }
        </script>

    </body>
</html>
