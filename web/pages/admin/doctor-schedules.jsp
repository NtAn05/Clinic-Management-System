<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý lịch làm việc bác sĩ</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }
            body {
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Arial, sans-serif;
                background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
                min-height: 100vh;
            }
            .container {
                padding: 30px 50px;
                max-width: 1400px;
                margin: 0 auto;
            }
            .panel {
                background: #fff;
                padding: 22px;
                border-radius: 10px;
                margin-bottom: 20px;
                box-shadow: 0 4px 15px rgba(0,0,0,.1);
            }
            .alert {
                padding: 15px 20px;
                border-radius: 8px;
                margin-bottom: 20px;
                display: flex;
                gap: 10px;
                align-items: center;
            }
            .alert.success {
                background: #e8f5e9;
                color: #2e7d32;
                border-left: 4px solid #4caf50;
            }
            .alert.error {
                background: #ffebee;
                color: #c62828;
                border-left: 4px solid #f44336;
            }

            .toolbar {
                display: grid;
                grid-template-columns: 2fr 1.2fr 1.2fr auto;
                gap: 12px;
                align-items: end;
            }
            .field label {
                display: block;
                font-weight: 600;
                margin-bottom: 8px;
                color: #333;
                font-size: 13px;
            }
            .field input, .field select {
                width: 100%;
                padding: 10px 15px;
                border: 1px solid #ddd;
                border-radius: 6px;
                font-size: 14px;
            }
            .actions {
                display: flex;
                gap: 8px;
            }
            .btn {
                border: none;
                border-radius: 6px;
                cursor: pointer;
                font-weight: 600;
                font-size: 14px;
                padding: 10px 16px;
                display: inline-flex;
                align-items: center;
                gap: 6px;
                text-decoration: none;
            }
            .btn-primary {
                background: #0061ff;
                color: #fff;
            }
            .btn-reset {
                background: #f0f0f0;
                color: #333;
            }

            .week-head, .section-head {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 16px;
            }
            .week-nav {
                display: flex;
                align-items: center;
                gap: 8px;
            }
            .week-label {
                min-width: 240px;
                text-align: center;
                font-weight: 600;
                color: #555;
                font-size: 14px;
            }
            .nav-btn {
                width: 34px;
                height: 34px;
                border: 1px solid #ddd;
                border-radius: 6px;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                color: #555;
                text-decoration: none;
            }

            .week-grid {
                display: grid;
                grid-template-columns: repeat(7, minmax(160px, 1fr));
                gap: 10px;
                overflow-x: auto;
            }
            .day-col {
                border: 1px solid #e2e8f0;
                border-radius: 10px;
                min-height: 220px;
                padding: 10px;
            }
            .day-title {
                text-align: center;
                font-weight: 700;
                color: #1f2937;
            }
            .day-date {
                text-align: center;
                font-size: 12px;
                color: #64748b;
                margin-bottom: 10px;
            }
            .shift-card {
                background: #eaf2ff;
                border: 1px solid #c8dcff;
                border-radius: 8px;
                padding: 8px;
                margin-bottom: 8px;
                font-size: 13px;
                color: #1e40af;
            }
            .shift-name {
                font-weight: 700;
                margin-bottom: 4px;
            }
            .empty-day {
                text-align: center;
                color: #94a3b8;
                margin-top: 35px;
                font-size: 13px;
            }

            .table-container {
                overflow-x: auto;
            }
            table {
                width: 100%;
                border-collapse: collapse;
            }
            th {
                background: linear-gradient(135deg,#f8f9fa 0%,#f0f0f0 100%);
                padding: 14px;
                text-align: left;
                border-bottom: 2px solid #e0e0e0;
                font-size: 14px;
            }
            td {
                padding: 14px;
                border-bottom: 1px solid #f0f0f0;
                color: #555;
                font-size: 14px;
            }
            tr:hover {
                background: #f9f9f9;
            }
            .status {
                display: inline-block;
                padding: 6px 12px;
                border-radius: 20px;
                font-size: 12px;
                font-weight: 600;
                background: #e8f5e9;
                color: #388e3c;
            }
            .row-actions {
                display: flex;
                gap: 8px;
            }
            .icon-btn {
                border: none;
                background: none;
                cursor: pointer;
                font-size: 15px;
                color: #1976d2;
            }
            .icon-btn.delete {
                color: #d32f2f;
            }
            .no-data {
                text-align: center;
                color: #999;
                padding: 24px;
            }

            .modal {
                display: none;
                position: fixed;
                z-index: 1000;
                inset: 0;
                background: rgba(0,0,0,.5);
            }
            .modal-content {
                background: #fff;
                margin: 5% auto;
                padding: 28px;
                border-radius: 10px;
                width: 90%;
                max-width: 560px;
            }
            .modal-title {
                font-size: 20px;
                font-weight: 600;
                color: #0061ff;
                margin-bottom: 20px;
                display: flex;
                gap: 8px;
                align-items: center;
            }
            .modal-grid {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 12px;
            }
            .modal-footer {
                display: flex;
                justify-content: flex-end;
                gap: 10px;
                margin-top: 20px;
                padding-top: 12px;
                border-top: 1px solid #f0f0f0;
            }

            @media (max-width: 1024px) {
                .toolbar {
                    grid-template-columns: 1fr 1fr;
                }
                .actions {
                    grid-column: 1/-1;
                }
            }
            @media (max-width: 768px) {
                .container {
                    padding: 18px;
                }
                .toolbar, .modal-grid {
                    grid-template-columns: 1fr;
                }
            }
        </style>
    </head>
    <body>
        <jsp:include page="/common/header.jsp" />

        <div class="container">
            <c:if test="${not empty success}">
                <div class="alert success"><i class="fas fa-check-circle"></i>${success}</div>
                </c:if>
                <c:if test="${not empty error}">
                <div class="alert error"><i class="fas fa-exclamation-circle"></i>${error}</div>
                </c:if>

            <div class="panel">
                <form id="filterForm" method="GET" action="${pageContext.request.contextPath}/admin-doctor-schedules" class="toolbar">
                    <div class="field">
                        <label><i class="fas fa-search"></i> Tìm kiếm bác sĩ</label>
                        <input type="text" name="keyword" value="${keyword}" placeholder="Nhập tên bác sĩ...">
                    </div>
                    <div class="field">
                        <label><i class="fas fa-user-md"></i> Bác sĩ</label>
                        <select id="doctorIdFilter" name="doctorId">
                            <option value="0" ${selectedDoctorId == 0 ? 'selected' : ''}>Tất cả bác sĩ</option>
                            <c:forEach var="doctor" items="${doctors}">
                                <option value="${doctor.doctorId}" ${doctor.doctorId == selectedDoctorId ? 'selected' : ''}>${doctor.fullName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="field">
                        <label><i class="fas fa-filter"></i> Lọc theo thứ</label>
                        <select id="dayOfWeekFilter" name="dayOfWeek">
                            <option value="" ${empty selectedDay ? 'selected' : ''}>Tất cả</option>
                            <option value="1" ${selectedDay == '1' ? 'selected' : ''}>Thứ 2</option>
                            <option value="2" ${selectedDay == '2' ? 'selected' : ''}>Thứ 3</option>
                            <option value="3" ${selectedDay == '3' ? 'selected' : ''}>Thứ 4</option>
                            <option value="4" ${selectedDay == '4' ? 'selected' : ''}>Thứ 5</option>
                            <option value="5" ${selectedDay == '5' ? 'selected' : ''}>Thứ 6</option>
                            <option value="6" ${selectedDay == '6' ? 'selected' : ''}>Thứ 7</option>
                            <option value="0" ${selectedDay == '0' ? 'selected' : ''}>Chủ nhật</option>
                        </select>
                    </div>
                    <div class="actions">
                        <input type="hidden" name="weekOffset" value="${weekOffset}">
                        <button class="btn btn-primary" type="submit"><i class="fas fa-search"></i> Tìm kiếm</button>
                        <a class="btn btn-reset" href="${pageContext.request.contextPath}/admin-doctor-schedules"><i class="fas fa-rotate-left"></i> Đặt lại</a>
                    </div>
                </form>
            </div>

            <div class="panel">
                <div class="week-head">
                    <h3>Lịch tuần</h3>
                    <div class="week-nav">
                        <c:url var="prevWeekUrl" value="/admin-doctor-schedules">
                            <c:param name="keyword" value="${keyword}" />
                            <c:param name="doctorId" value="${selectedDoctorId}" />
                            <c:param name="dayOfWeek" value="${selectedDay}" />
                            <c:param name="weekOffset" value="${weekOffset - 1}" />
                        </c:url>
                        <a class="nav-btn" href="${prevWeekUrl}"><i class="fas fa-chevron-left"></i></a>
                        <div class="week-label">${weekLabel}</div>
                        <c:url var="nextWeekUrl" value="/admin-doctor-schedules">
                            <c:param name="keyword" value="${keyword}" />
                            <c:param name="doctorId" value="${selectedDoctorId}" />
                            <c:param name="dayOfWeek" value="${selectedDay}" />
                            <c:param name="weekOffset" value="${weekOffset + 1}" />
                        </c:url>
                        <a class="nav-btn" href="${nextWeekUrl}"><i class="fas fa-chevron-right"></i></a>
                    </div>
                </div>

                <div class="week-grid">
                    <c:set var="dayKeys" value="${'1,2,3,4,5,6,0'}" />
                    <c:forTokens var="dayKey" items="${dayKeys}" delims=",">
                        <div class="day-col">
                            <div class="day-title">
                                <c:choose>
                                    <c:when test="${dayKey == '1'}">Thứ 2</c:when>
                                    <c:when test="${dayKey == '2'}">Thứ 3</c:when>
                                    <c:when test="${dayKey == '3'}">Thứ 4</c:when>
                                    <c:when test="${dayKey == '4'}">Thứ 5</c:when>
                                    <c:when test="${dayKey == '5'}">Thứ 6</c:when>
                                    <c:when test="${dayKey == '6'}">Thứ 7</c:when>
                                    <c:otherwise>CN</c:otherwise>
                                </c:choose>
                            </div>
                            <div class="day-date">${dayDates[dayKey]}</div>
                            <c:choose>
                                <c:when test="${not empty weekGrid[dayKey]}">
                                    <c:forEach var="item" items="${weekGrid[dayKey]}">
                                        <div class="shift-card">
                                            <div class="shift-name">BS. ${item.doctorName}</div>
                                            <div><i class="far fa-clock"></i> ${item.startTimeText} - ${item.endTimeText}</div>
                                            <div><i class="fas fa-user-injured"></i> Tối đa ${item.maxPatients} bệnh nhân</div>
                                        </div>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <div class="empty-day">Không có lịch</div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:forTokens>
                </div>
            </div>

            <div class="panel">
                <div class="section-head">
                    <h3>Lịch sắp tới</h3>
                    <button class="btn btn-primary" type="button" onclick="openAddModal()"><i class="fas fa-plus"></i> Thêm ca làm việc</button>
                </div>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr>
                                <th>Bác sĩ</th>
                                <th>Ngày</th>
                                <th>Thứ</th>
                                <th>Giờ bắt đầu</th>
                                <th>Giờ kết thúc</th>
                                <th>Số BN tối đa</th>
                                <th>Trạng thái</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty scheduleItems}">
                                    <c:forEach var="item" items="${scheduleItems}">
                                        <tr>
                                            <td>${item.doctorName}</td>
                                            <td>${item.workDateText}</td>
                                            <td>${item.dayLabel}</td>
                                            <td>${item.startTimeText}</td>
                                            <td>${item.endTimeText}</td>
                                            <td>${item.maxPatients}</td>
                                            <td><span class="status">${item.status}</span></td>
                                            <td>
                                                <div class="row-actions">
                                                    <button class="icon-btn" type="button" title="Sửa"
                                                            data-shift-id="${item.shiftId}"
                                                            data-doctor-id="${item.doctorId}"
                                                            data-day-of-week="${item.dayOfWeek}"
                                                            data-start-time="${item.startTimeText}"
                                                            data-end-time="${item.endTimeText}"
                                                            data-max-patients="${item.maxPatients}"
                                                            onclick="openEditModal(this)">
                                                        <i class="fas fa-pen"></i>
                                                    </button>
                                                    <form method="POST" action="${pageContext.request.contextPath}/admin-doctor-schedules" onsubmit="return confirm('Bạn có chắc muốn xóa ca làm việc này?');">
                                                        <input type="hidden" name="action" value="delete">
                                                        <input type="hidden" name="shiftId" value="${item.shiftId}">
                                                        <input type="hidden" name="filterKeyword" value="${keyword}">
                                                        <input type="hidden" name="filterDoctorId" value="${selectedDoctorId}">
                                                        <input type="hidden" name="filterDayOfWeek" value="${selectedDay}">
                                                        <input type="hidden" name="filterWeekOffset" value="${weekOffset}">
                                                        <button class="icon-btn delete" type="submit" title="Xóa"><i class="fas fa-trash"></i></button>
                                                    </form>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr><td colspan="8" class="no-data">Không có dữ liệu lịch làm việc</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="modal" id="addModal">
            <div class="modal-content">
                <div class="modal-title"><i class="fas fa-plus-circle"></i> Thêm ca làm việc</div>
                <form method="POST" action="${pageContext.request.contextPath}/admin-doctor-schedules">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="filterKeyword" value="${keyword}">
                    <input type="hidden" name="filterDoctorId" value="${selectedDoctorId}">
                    <input type="hidden" name="filterDayOfWeek" value="${selectedDay}">
                    <input type="hidden" name="filterWeekOffset" value="${weekOffset}">
                    <div class="modal-grid">
                        <div class="field" style="grid-column:1/-1;">
                            <label>Bác sĩ</label>
                            <select name="doctorId" required>
                                <c:forEach var="doctor" items="${doctors}">
                                    <option value="${doctor.doctorId}" ${doctor.doctorId == selectedDoctorId ? 'selected' : ''}>${doctor.fullName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="field">
                            <label>Thứ</label>
                            <select name="dayOfWeek" required>
                                <option value="1">Thứ 2</option>
                                <option value="2">Thứ 3</option>
                                <option value="3">Thứ 4</option>
                                <option value="4">Thứ 5</option>
                                <option value="5">Thứ 6</option>
                                <option value="6">Thứ 7</option>
                                <option value="0">Chủ nhật</option>
                            </select>
                        </div>
                        <div class="field"><label>Số bệnh nhân tối đa</label><input type="number" name="maxPatients" min="1" max="200" value="20" required></div>
                        <div class="field"><label>Giờ bắt đầu</label><input type="time" name="startTime" required></div>
                        <div class="field"><label>Giờ kết thúc</label><input type="time" name="endTime" required></div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-reset" onclick="closeAddModal()"><i class="fas fa-times"></i> Hủy</button>
                        <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Lưu</button>
                    </div>
                </form>
            </div>
        </div>

        <div class="modal" id="editModal">
            <div class="modal-content">
                <div class="modal-title"><i class="fas fa-edit"></i> Cập nhật ca làm việc</div>
                <form method="POST" action="${pageContext.request.contextPath}/admin-doctor-schedules">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="shiftId" id="editShiftId">
                    <input type="hidden" name="doctorId" id="editDoctorId">
                    <input type="hidden" name="filterKeyword" value="${keyword}">
                    <input type="hidden" name="filterDoctorId" value="${selectedDoctorId}">
                    <input type="hidden" name="filterDayOfWeek" value="${selectedDay}">
                    <input type="hidden" name="filterWeekOffset" value="${weekOffset}">
                    <div class="modal-grid">
                        <div class="field">
                            <label>Thứ</label>
                            <select name="dayOfWeek" id="editDayOfWeek" required>
                                <option value="1">Thứ 2</option>
                                <option value="2">Thứ 3</option>
                                <option value="3">Thứ 4</option>
                                <option value="4">Thứ 5</option>
                                <option value="5">Thứ 6</option>
                                <option value="6">Thứ 7</option>
                                <option value="0">Chủ nhật</option>
                            </select>
                        </div>
                        <div class="field"><label>Số bệnh nhân tối đa</label><input type="number" name="maxPatients" id="editMaxPatients" min="1" max="200" required></div>
                        <div class="field"><label>Giờ bắt đầu</label><input type="time" name="startTime" id="editStartTime" required></div>
                        <div class="field"><label>Giờ kết thúc</label><input type="time" name="endTime" id="editEndTime" required></div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-reset" onclick="closeEditModal()"><i class="fas fa-times"></i> Hủy</button>
                        <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Lưu thay đổi</button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            function submitFilterForm() {
                var form = document.getElementById("filterForm");
                if (form)
                    form.submit();
            }

            function openAddModal() {
                document.getElementById("addModal").style.display = "block";
            }
            function closeAddModal() {
                document.getElementById("addModal").style.display = "none";
            }
            function openEditModal(btn) {
                document.getElementById("editShiftId").value = btn.dataset.shiftId;
                document.getElementById("editDoctorId").value = btn.dataset.doctorId;
                document.getElementById("editDayOfWeek").value = btn.dataset.dayOfWeek;
                document.getElementById("editStartTime").value = btn.dataset.startTime;
                document.getElementById("editEndTime").value = btn.dataset.endTime;
                document.getElementById("editMaxPatients").value = btn.dataset.maxPatients;
                document.getElementById("editModal").style.display = "block";
            }
            function closeEditModal() {
                document.getElementById("editModal").style.display = "none";
            }
            window.onclick = function (event) {
                if (event.target === document.getElementById("addModal"))
                    closeAddModal();
                if (event.target === document.getElementById("editModal"))
                    closeEditModal();
            };

            document.addEventListener("DOMContentLoaded", function () {
                var doctorFilter = document.getElementById("doctorIdFilter");
                var dayFilter = document.getElementById("dayOfWeekFilter");
                if (doctorFilter)
                    doctorFilter.addEventListener("change", submitFilterForm);
                if (dayFilter)
                    dayFilter.addEventListener("change", submitFilterForm);
            });
        </script>
    </body>
</html>
