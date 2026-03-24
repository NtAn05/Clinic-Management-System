<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <title>Patient List</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/pages/profile/createPatients/listOfPatients.css">

</head>

<body>

<jsp:include page="/common/header.jsp" />

<div class="patient-page">

    <div class="patient-container">

        <!-- header -->
        <div class="patient-header">

            <h2 class="patient-title">
                Danh sách đăng kí bệnh nhân
            </h2>

            <a href="${pageContext.request.contextPath}/pages/profile/createPatients/createPatients.jsp"
               class="btn-add-patiaent">
                +
            </a>

        </div>


        <!-- bảng danh sách -->
        <div class="patient-table-wrapper">

            <table class="patient-table">

                <thead>
                    <tr>
                        <th class="col-name">Họ tên</th>
                        <th class="col-dob">Ngày sinh</th>
                        <th class="col-gender">Giới tính</th>
                        <th class="col-phone">Điện thoại</th>
                        <th></th>
                        <th></th>
                    </tr>
                </thead>

                <tbody>

                     <c:forEach items="${patientList}" var="p">

                        <tr class="patient-row">

                            <td class="patient-name">
                                ${p.fullName}
                            </td>

                            <td class="patient-dob">
                                ${p.dob}
                            </td>

                            <td class="patient-gender">
                                ${p.gender}
                            </td>

                            <td class="patient-phone">
                                ${p.phone}
                            </td>

                            <td class="patient-action">

                                <a class="btn-edit"
                                   href="${pageContext.request.contextPath}/createpatientsservlet?DoctorID=${DoctorID}&action=edit&id=${p.patientId}">
                                    Sửa
                                </a>

                            </td>
                             <c:if test="${DoctorID != null}">
                                <td class="patient-action">

                                <a class="btn-edit"
                                   href="${pageContext.request.contextPath}/appointmentservlet?doctor=${DoctorID}&patientid=${p.patientId}">
                                    Chọn
                                </a>

                            </td>
                            </c:if>
                            
                            

                        </tr>

                    </c:forEach>

                </tbody>

            </table>

        </div>

    </div>

</div>

<jsp:include page="/common/footer.jsp" />

</body>
</html>