<%-- 
    Document   : prescription
    Created on : 8 Mar 2026, 11:06:42 pm
    Author     : anngu
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Đơn thuốc</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/pages/profile/prescriptions.css">
    </head>
    <body>
        <jsp:include page="/common/header.jsp" />

        <main class="prescription-page">
            <section class="page-hero">
                <h1>Tra cứu đơn thuốc</h1>
                <p>Xem lại các đơn thuốc đã được bác sĩ kê trong các lần khám trước.</p>
            </section>

            <c:if test="${empty prescriptions}">
                <div class="empty-card">Bạn chưa có đơn thuốc nào.</div>
            </c:if>

            <c:if test="${not empty prescriptions}">
                <section class="rx-list">
                    <c:forEach var="rx" items="${prescriptions}">
                        <article class="rx-card">
                            <div class="rx-head">
                                <div>
                                    <span class="rx-id">Đơn #${rx.prescriptionId}</span>
                                    <span class="doctor-chip">Bác sĩ: ${rx.doctorName}</span>
                                </div>
                                <div class="rx-date">
                                    <fmt:formatDate value="${rx.updatedAt}" pattern="dd/MM/yyyy HH:mm" />
                                </div>
                            </div>

                            <div class="rx-meta">Lịch khám #${rx.appointmentId} · <fmt:formatDate value="${rx.appointmentDate}" pattern="dd/MM/yyyy" /> ${rx.appointmentTime}</div>
                            <div class="rx-diagnosis ${empty rx.diagnosis ? 'muted' : ''}">Chẩn đoán: ${empty rx.diagnosis ? 'Chưa cập nhật' : rx.diagnosis}</div>
                            <div class="rx-note ${empty rx.prescriptionNote ? 'muted' : ''}">Ghi chú: ${empty rx.prescriptionNote ? 'Không có' : rx.prescriptionNote}</div>

                            <table class="rx-table">
                                <thead>
                                    <tr>
                                        <th>Thuốc</th>
                                        <th>Liều dùng</th>
                                        <th>Số lần/ngày</th>
                                        <th>Số ngày</th>
                                        <th>Số lượng</th>
                                        <th>Hướng dẫn</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="item" items="${rx.prescriptionItems}">
                                        <tr>
                                            <td>${item.medicineName}${empty item.unit ? '' : ' ('}${empty item.unit ? '' : item.unit}${empty item.unit ? '' : ')'}</td>
                                            <td>${empty item.dosage ? '---' : item.dosage}</td>
                                            <td>${empty item.frequency ? '---' : item.frequency}</td>
                                            <td>${empty item.durationDays ? '---' : item.durationDays}</td>
                                            <td>${empty item.quantity ? '---' : item.quantity}</td>
                                            <td>${empty item.instruction ? '---' : item.instruction}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </article>
                    </c:forEach>
                </section>
            </c:if>
        </main>
    </body>
</html>
