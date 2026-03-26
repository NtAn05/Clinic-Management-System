<%-- 
    Document   : appointment
    Created on : Jan 26, 2026, 6:25:11 PM
    Author     : Admin
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Create Patient</title>
        <link rel="stylesheet"
              href="<c:url value='/pages/appointments/appointment/appointment.css'/>">
    </head>
    <body>

        <jsp:include page="/common/header.jsp" />

        <div class="page">
            <div class="content">



                <!-- FORM thông tin -->

                <form method="post" action="${pageContext.request.contextPath}/createpatientsservlet">
                    <input type="hidden" name="userID" value="${sessionScope.account.userId}">
                    <input type="hidden" name="patientID" value="${patient.getPatientId()}">
                    <input type="hidden" name="DoctorID" value="${DoctorID}">
                    <div class="card-box">
                        <h3>Đăng kí thông tin của bệnh nhân</h3>

                        <div class="form-grid">
                            <div>
                                <label>Họ và tên *</label>
                                <input type="text" name="name" value="${patient.fullName}" required>
                                <span class="error-message">${errorName}</span>                            </div>

                            <div>
                                <label>Số điện thoại ( Không bắt buộc )</label>
                                <input type="text" name="sdt" value="${patient.phone}" >
                                <span class="error-message"">${errorPhone}</span>
                            </div>

                            <div>
                                <label>Email ( Không bắt buộc )</label>
                                <input type="email" name="email" value="${patient.email}" >
                                <span class="error-message">${errorEmail}</span>
                            </div>

                            <div>
                                <label>Ngày sinh *</label>
                                <fmt:formatDate value="${patient.dob}" pattern="yyyy-MM-dd" var="formattedDob"/>
                                <input type="date" name="dateofbirth"
                                       value="${not empty dob ? dob : formattedDob}">
                                <span class="error-message">${errorDOB}</span>
                            </div>

                            <div>
                                <label>Giới tính *</label>
                                <select name="gender" required>
                                    <option value="MALE"
                                            ${gender == 'MALE' ? 'selected' : ''}>
                                        Nam
                                    </option>

                                    <option value="FEMALE"
                                            ${gender == 'FEMALE' ? 'selected' : ''}>
                                        Nữ
                                    </option>
                                </select>
                            </div>

                        </div>


                    </div>


                    <div class="actions">
                        <button type="button" class="btn-outline"
                                onclick="location.href = '${pageContext.request.contextPath}/createpatientsservlet?DoctorID=${DoctorID}'">
                            Hủy
                        </button>
                        <c:if test="${user.getRole() eq 'receptionist'}">
                            <button type="submit" name="btnSubmit" value="create" class="btn-primary">
                                Chọn ca
                            </button>
                        </c:if>

                        <c:if test="${user.getRole() != 'receptionist'}">


                            <c:if test="${patient.getPatientId() != null}">
                                <button type="submit" name="btnSubmit" value="edit" class="btn-primary">
                                    Lưu hồ sơ
                                </button>
                            </c:if>
                            <c:if test="${patient.getPatientId() == null}">
                                <button type="submit" name="btnSubmit" value="create" class="btn-primary">
                                    Tạo hồ sơ
                                </button>
                            </c:if>
                        </c:if>
                    </div>

                </form>



            </div>
        </div>



        <jsp:include page="/common/footer.jsp" />
    </body>
</html>