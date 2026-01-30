<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Doctor Dashboard</title>
        <link rel="stylesheet" href="../pages/doctors/doctorDashboard.css">
    </head>
    <body>

        <div class="dashboard-container">

            <!-- ===== HEADER ===== -->
            <div class="dashboard-header">
                <div class="clinic-info">
                    <h2>Phòng Khám ABC</h2>
                    <span>Hệ thống quản lý phòng khám</span>
                </div>

                <div class="doctor-info">
                    <span>Bác sĩ</span>
                    <strong>${sessionScope.doctorName}</strong>
                </div>
            </div>

            <!-- ===== SUMMARY ===== -->
            <div class="summary-cards">
                <div class="card">
                    <p>Tổng bệnh nhân</p>
                    <h3>${stats.total}</h3>
                </div>

                <div class="card">
                    <p>Đang chờ</p>
                    <h3>${stats.waiting}</h3>
                </div>

                <div class="card">
                    <p>Đã khám</p>
                    <h3>${stats.done}</h3>
                </div>
            </div>

            <!-- ===== MAIN CONTENT ===== -->
            <div class="content">

                <!-- ===== QUEUE LIST ===== -->
                <div class="queue-section">
                    <h3>Danh sách bệnh nhân chờ khám</h3>
                    
                    <div class="queue-filter">
                        <form method="get" action="">
                            <input type="text"
                                   name="keyword"
                                   placeholder="Tìm kiếm bệnh nhân theo tên hoặc mã..."
                                   value="${param.keyword}" />

                            <select name="status">
                                <option value="all" ${param.status=='all'?'selected':''}>Tất cả</option>
                                <option value="waiting" ${param.status=='waiting'?'selected':''}>Đang chờ</option>
                                <option value="examining" ${param.status=='examining'?'selected':''}>Đang khám</option>
                                <option value="done" ${param.status=='done'?'selected':''}>Hoàn tất</option>
                            </select>

                            <button type="submit">Lọc</button>
                        </form>
                    </div>

                    <table>
                        <thead>
                            <tr>
                                <th>STT</th>
                                <th>Bệnh nhân</th>
                                <th>Giới tính</th>
                                <th>Ngày sinh</th>
                                <th>Triệu chứng</th>
                                <th>Trạng thái</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="q" items="${queueList}">
                                <tr>
                                    <td>${q.queuePosition}</td>
                                    <td>${q.patientName}</td>
                                    <td>${q.gender}</td>
                                    <td>${q.dob}</td>
                                    <td>${q.symptom}</td>
                                    <td>
                                        <span class="status ${q.status}">
                                            ${q.status}
                                        </span>
                                    </td>
                                </tr>
                            </c:forEach>

                            <c:if test="${empty queueList}">
                                <tr>
                                    <td colspan="6" style="text-align:center">
                                        Không có bệnh nhân chờ khám
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>

                <!-- ===== SHIFT INFO ===== -->
                <div class="shift-section">
                    <h3>Lịch làm việc hôm nay</h3>

                    <c:forEach var="s" items="${shifts}">
                        <div class="shift-item">
                            <span>
                                ${s.startTime} - ${s.endTime}
                            </span>
                            <span>
                                Tối đa ${s.maxPatients} bệnh nhân
                            </span>
                        </div>
                    </c:forEach>

                    <c:if test="${empty shifts}">
                        <p>Hôm nay không có ca làm việc</p>
                    </c:if>
                </div>

            </div>
        </div>

    </body>
</html>
