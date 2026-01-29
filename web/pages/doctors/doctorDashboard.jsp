<%-- 
    Document   : doctorDashboard
    Created on : 25 Jan 2026, 4:20:08 pm
    Author     : anngu
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        
        <table border="1">
            <tr>
                <th>STT</th>
                <th>Bệnh nhân</th>
                <th>Giới tính</th>
                <th>Ngày sinh</th>
                <th>Triệu chứng</th>
                <th>Trạng thái</th>
            </tr>

            <c:forEach var="q" items="${queueList}">
                <tr>
                    <td>${q.queuePosition}</td>
                    <td>${q.patientName}</td>
                    <td>${q.gender}</td>
                    <td>${q.dob}</td>
                    <td>${q.symptom}</td>
                    <td>${q.status}</td>
                </tr>
            </c:forEach>
        </table>

    </body>
</html>
