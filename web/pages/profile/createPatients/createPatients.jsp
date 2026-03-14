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
        <title>Appointment</title>
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/pages/appointments/appointment/appointment.css">
    </head>
    <body>

        <jsp:include page="/common/header.jsp" />

        <div class="page">
            <div class="content">

                

                    <!-- FORM thông tin -->
                    <form method="post" action="${pageContext.request.contextPath}/createpatientsservlet">
                        <input type="hidden" name="userID" value="${sessionScope.account.userId}">
                       
                        <div class="card-box">
                            <h3>Thông tin của bạn</h3>

                            <div class="form-grid">
                                <div>
                                    <label>Họ và tên *</label>
                                    <input type="text" name="name" value="${patient.fullName}" required>
                                    <span style="color:red">${errorName}</span>
                                </div>

                                <div>
                                    <label>Số điện thoại ( Không bắt buộc )</label>
                                    <input type="text" name="sdt" value="${patient.phone}" >
                                    <span style="color:red">${errorPhone}</span>
                                </div>

                                <div>
                                    <label>Email ( Không bắt buộc )</label>
                                    <input type="email" name="email" value="${patient.email}" >
                                    <span style="color:red">${errorEmail}</span>
                                </div>

                                <div>
                                    <label>Ngày sinh *</label>
                                    <input type="date" name="dateofbirth" value="${patient.dob}" >
                                    <span style="color:red">${errorDOB}</span>
                                </div>

                                <div>
                                    <label>Giới tính *</label>
                                    <select name="gender" required>
                                        <option value="MALE">Nam</option>
                                        <option value="FEMALE">Nữ</option>
                                    </select>
                                </div>

                            </div>

                           
                        </div>

                       
                        <div class="actions">
                            <button type="button" class="btn-outline"
                                    onclick="location.href = '${pageContext.request.contextPath}/index.jsp'">
                                Hủy
                            </button>

                            <button type="submit" name="btnSubmit" value="create" class="btn-primary">
                                Tạo hồ sơ
                            </button>
                        </div>

                    </form>


                
            </div>
        </div>

       

        <jsp:include page="/common/footer.jsp" />
    </body>
</html>