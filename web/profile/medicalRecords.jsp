<%-- 
    Document   : medicalRecords
    Created on : 26 Feb 2026, 5:26:24 pm
    Author     : anngu
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Hồ sơ bệnh án</title>
        <style>
            .page-wrap {max-width: 1100px; margin: 0 auto; padding: 24px;}
            .card {background: #fff; border-radius: 14px; border: 1px solid #e6edff; box-shadow: 0 10px 24px rgba(15,44,110,.08); padding: 18px; margin-bottom: 16px;}
            .meta {color: #64748b; font-size: 14px; margin-bottom: 8px;}
            h1 {margin-top: 0;}
            p {white-space: pre-line; margin: 8px 0;}
            .label {font-weight: 700; color: #1e3a8a;}
        </style>
    </head>
    <body>
        <jsp:include page="/common/header.jsp" />
        <div class="page-wrap">
            <h1>Hồ sơ bệnh án</h1>

            <c:if test="${empty records}">
                <div class="card">Bạn chưa có hồ sơ bệnh án nào.</div>
            </c:if>

            <c:forEach var="item" items="${records}">
                <div class="card">
                    <div class="meta">
                        Mã lịch khám #${item.appointmentId} · Bác sĩ: ${item.doctorName} ·
                        <fmt:formatDate value="${item.appointmentDate}" pattern="dd/MM/yyyy" /> ${item.appointmentTime}
                    </div>
                    <p><span class="label">Triệu chứng:</span> ${empty item.symptoms ? '---' : item.symptoms}</p>
                    <p><span class="label">Chẩn đoán:</span> ${empty item.diagnosis ? '---' : item.diagnosis}</p>
                    <p><span class="label">Ghi chú:</span> ${empty item.notes ? '---' : item.notes}</p>
                </div>
            </c:forEach>
        </div>
    </body>
</html>
