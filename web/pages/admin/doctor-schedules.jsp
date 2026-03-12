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
                box-shadow: 0 4px 15px rgba(0, 0, 0, .1);
            }
            .alert {
                padding: 15px 20px;
                border-radius: 8px;
                margin-bottom: 20px;
                display: flex;
                gap: 10px;
                align-items: center;
                transition: opacity .35s ease, transform .35s ease;
            }
            .alert.fade-out {
                opacity: 0;
                transform: translateY(-8px);
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
                grid-template-columns: 2.2fr 1.5fr 1.5fr auto;
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
                transition: all 0.3s ease;
            }
            .btn-primary {
                background: #0061ff;
                color: #fff;
            }
            .btn-primary:hover {
                background: #0052cc;
            }
            .btn-filter {
                background: #0061ff;
                color: #fff;
            }
            .btn-filter:hover {
                background: #0052cc;
                transform: translateY(-1px);
            }
            .btn-reset {
                background: #f0f0f0;
                color: #333;
            }
            .btn-reset:hover {
                background: #e0e0e0;
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
                min-height: 360px;
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
            .shift-section {
                margin-top: 10px;
                display: flex;
                flex-direction: column;
            }
            .shift-section.morning {
                height: 150px;
            }
            .shift-section.afternoon {
                height: 150px;
            }
            .shift-section-title {
                font-size: 12px;
                font-weight: 700;
                color: #0f172a;
                margin-bottom: 6px;
                padding: 4px 8px;
                border-radius: 6px;
                background: #f1f5f9;
            }
            .shift-section-body {
                overflow-y: auto;
                padding-right: 2px;
            }
            .empty-shift {
                color: #94a3b8;
                font-size: 12px;
                padding: 4px 2px 8px;
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
            .upcoming-table {
                table-layout: fixed;
            }
            .upcoming-table th:nth-child(1),
            .upcoming-table td:nth-child(1) {
                width: 26%;
            }
            .upcoming-table th:nth-child(2),
            .upcoming-table td:nth-child(2) {
                width: 14%;
            }
            .upcoming-table th:nth-child(3),
            .upcoming-table td:nth-child(3) {
                width: 12%;
            }
            .upcoming-table th:nth-child(4),
            .upcoming-table td:nth-child(4) {
                width: 24%;
            }
            .upcoming-table th:nth-child(5),
            .upcoming-table td:nth-child(5) {
                width: 12%;
            }
            th {
                background: linear-gradient(135deg, #f8f9fa 0%, #f0f0f0 100%);
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
                gap: 20px;
                align-items: center;
                justify-content: center;
                width: auto;
            }
            th.action-col, td.action-col {
                width: 12%;
                text-align: center;
                white-space: nowrap;
            }
            .icon-btn {
                border: none;
                background: none;
                cursor: pointer;
                font-size: 16px;
                color: #1976d2;
                padding: 6px 10px;
                border-radius: 4px;
                transition: all 0.3s ease;
            }
            .icon-btn:hover {
                background: #e3f2fd;
            }
            .icon-btn.delete {
                color: #d32f2f;
            }
            .icon-btn.delete:hover {
                background: #ffebee;
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
                background: rgba(0, 0, 0, .5);
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
                gap: 14px;
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
            .btn-cancel {
                padding: 10px 20px;
                background: #f0f0f0;
                color: #333;
                border: none;
                border-radius: 6px;
                cursor: pointer;
                font-weight: 600;
                transition: all 0.3s ease;
                display: inline-flex;
                align-items: center;
                gap: 6px;
            }
            .btn-cancel:hover {
                background: #e0e0e0;
            }
            .btn-submit-modal {
                padding: 10px 20px;
                background: #0061ff;
                color: #fff;
                border: none;
                border-radius: 6px;
                cursor: pointer;
                font-weight: 600;
                transition: all 0.3s ease;
                display: inline-flex;
                align-items: center;
                gap: 6px;
            }
            .btn-submit-modal:hover {
                background: #0052cc;
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
                        <label><i class="fas fa-search"></i> Tìm kiếm </label>
                        <input id="keywordFilter" type="text" name="keyword" value="${keyword}" placeholder="Nhập tên bác sĩ...">
                    </div>
                    <div class="field">
                        <label><i class="fas fa-sun"></i> Ca làm việc</label>
                        <select id="shiftTypeFilter" name="shiftType">
                            <option value="" ${empty selectedShiftType ? 'selected' : ''}>-- Tất cả --</option>
                            <option value="morning" ${selectedShiftType == 'morning' ? 'selected' : ''}>Ca sáng</option>
                            <option value="afternoon" ${selectedShiftType == 'afternoon' ? 'selected' : ''}>Ca chiều</option>
                        </select>
                    </div>
                    <div class="field">
                        <label><i class="fas fa-filter"></i> Lọc theo thứ</label>
                        <select id="dayOfWeekFilter" name="dayOfWeek">
                            <option value="" ${empty selectedDay ? 'selected' : ''}>-- Tất cả --</option>
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
                        <button class="btn btn-primary" type="submit"><i class="fas fa-search"></i> Tìm</button>
                        <a class="btn btn-reset" href="${pageContext.request.contextPath}/admin-doctor-schedules"><i class="fas fa-redo"></i> Đặt lại</a>
                    </div>
                </form>
            </div>

            <div class="panel">
                <div class="week-head">
                    <h3>Lịch tuần</h3>
                    <div class="week-nav">
                        <c:url var="prevWeekUrl" value="/admin-doctor-schedules">
                            <c:param name="keyword" value="${keyword}" />
                            <c:param name="shiftType" value="${selectedShiftType}" />
                            <c:param name="dayOfWeek" value="${selectedDay}" />
                            <c:param name="weekOffset" value="${weekOffset - 1}" />
                        </c:url>
                        <a class="nav-btn" href="${prevWeekUrl}"><i class="fas fa-chevron-left"></i></a>
                        <div class="week-label">${weekLabel}</div>
                        <c:url var="nextWeekUrl" value="/admin-doctor-schedules">
                            <c:param name="keyword" value="${keyword}" />
                            <c:param name="shiftType" value="${selectedShiftType}" />
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
                                    <c:otherwise>Chủ nhật</c:otherwise>
                                </c:choose>
                            </div>
                            <div class="day-date">${dayDates[dayKey]}</div>
                            <div class="shift-section morning">
                                <div class="shift-section-title">Ca sáng</div>
                                <div class="shift-section-body">
                                    <c:set var="hasMorning" value="false" />
                                    <c:forEach var="item" items="${weekGrid[dayKey]}">
                                        <c:if test="${item.shiftCode == 'morning'}">
                                            <c:set var="hasMorning" value="true" />
                                            <div class="shift-card">
                                                <div class="shift-name">Bác sĩ ${item.doctorName}</div>
                                                <div><i class="fas fa-user-injured"></i> Tối đa ${item.maxPatients} bệnh nhân</div>
                                            </div>
                                        </c:if>
                                    </c:forEach>
                                    <c:if test="${hasMorning == false}">
                                        <div class="empty-shift">Không có lịch ca sáng</div>
                                    </c:if>
                                </div>
                            </div>
                            <div class="shift-section afternoon">
                                <div class="shift-section-title">Ca chiều</div>
                                <div class="shift-section-body">
                                    <c:set var="hasAfternoon" value="false" />
                                    <c:forEach var="item" items="${weekGrid[dayKey]}">
                                        <c:if test="${item.shiftCode == 'afternoon'}">
                                            <c:set var="hasAfternoon" value="true" />
                                            <div class="shift-card">
                                                <div class="shift-name">Bác sĩ ${item.doctorName}</div>
                                                <div><i class="fas fa-user-injured"></i> Tối đa ${item.maxPatients} bệnh nhân</div>
                                            </div>
                                        </c:if>
                                    </c:forEach>
                                    <c:if test="${hasAfternoon == false}">
                                        <div class="empty-shift">Không có lịch ca chiều</div>
                                    </c:if>
                                </div>
                            </div>
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
                    <table class="upcoming-table">
                        <thead>
                            <tr>
                                <th>Bác sĩ</th>
                                <th>Ngày</th>
                                <th>Thứ</th>
                                <th>Ca</th>
                                <th>Số BN tối đa</th>
                                <th class="action-col">Thao tác</th>
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
                                            <td>${item.shiftLabel}</td>
                                            <td>${item.maxPatients}</td>
                                            <td class="action-col">
                                                <div class="row-actions">
                                                    <button class="icon-btn" type="button" title="Sửa"
                                                            data-shift-id="${item.shiftId}"
                                                            data-doctor-id="${item.doctorId}"
                                                            data-day-of-week="${item.dayOfWeek}"
                                                            data-shift-type="${item.shiftCode}"
                                                            data-max-patients="${item.maxPatients}"
                                                            onclick="openEditModal(this)">
                                                        <i class="fas fa-pen-to-square"></i>
                                                    </button>
                                                    <form method="POST" action="${pageContext.request.contextPath}/admin-doctor-schedules" onsubmit="return confirm('Bạn có chắc muốn xóa ca làm việc này?');">
                                                        <input type="hidden" name="action" value="delete">
                                                        <input type="hidden" name="shiftId" value="${item.shiftId}">
                                                        <input type="hidden" name="filterKeyword" value="${keyword}">
                                                        <input type="hidden" name="filterDayOfWeek" value="${selectedDay}">
                                                        <input type="hidden" name="filterShiftType" value="${selectedShiftType}">
                                                        <input type="hidden" name="filterWeekOffset" value="${weekOffset}">
                                                        <button class="icon-btn delete" type="submit" title="Xóa"><i class="fas fa-trash"></i></button>
                                                    </form>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr><td colspan="6" class="no-data">Không có dữ liệu lịch làm việc</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- MODAL THÊM CA LÀM VIỆC -->
        <div class="modal" id="addModal">
            <div class="modal-content">
                <div class="modal-title"><i class="fas fa-plus-circle"></i> Thêm ca làm việc</div>
                <form id="addShiftForm" method="POST" action="${pageContext.request.contextPath}/admin-doctor-schedules">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="filterKeyword" value="${keyword}">
                    <input type="hidden" name="filterDayOfWeek" value="${selectedDay}">
                    <input type="hidden" name="filterShiftType" value="${selectedShiftType}">
                    <input type="hidden" name="filterWeekOffset" value="${weekOffset}">
                    <div class="modal-grid">
                        <div class="field" style="grid-column:1/-1;">
                            <label>Bác sĩ</label>
                            <select id="addDoctorSelect" name="doctorId" required>
                                <option value="" selected disabled>Chọn bác sĩ</option>
                                <c:forEach var="doctor" items="${activeDoctors}">
                                    <option value="${doctor.doctorId}">${doctor.fullName}</option>
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
                        <div class="field">
                            <label>Ca làm việc</label>
                            <select name="shiftType" required>
                                <option value="morning">Ca sáng (07:00 - 11:30)</option>
                                <option value="afternoon">Ca chiều (13:00 - 16:30)</option>
                            </select>
                        </div>
                        <div class="field">
                            <label>Số bệnh nhân tối đa</label>
                            <input type="number" name="maxPatients" min="1" max="200" value="20" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn-cancel" onclick="closeAddModal()"><i class="fas fa-times"></i> Hủy</button>
                        <button type="submit" class="btn-submit-modal"><i class="fas fa-save"></i> Lưu</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- MODAL CẬP NHẬT CA LÀM VIỆC -->
        <div class="modal" id="editModal">
            <div class="modal-content">
                <div class="modal-title"><i class="fas fa-pen-to-square"></i> Cập nhật ca làm việc</div>
                <form method="POST" action="${pageContext.request.contextPath}/admin-doctor-schedules">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="shiftId" id="editShiftId">
                    <input type="hidden" name="doctorId" id="editDoctorId">
                    <input type="hidden" name="filterKeyword" value="${keyword}">
                    <input type="hidden" name="filterDayOfWeek" value="${selectedDay}">
                    <input type="hidden" name="filterShiftType" value="${selectedShiftType}">
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
                        <div class="field">
                            <label>Ca làm việc</label>
                            <select name="shiftType" id="editShiftType" required>
                                <option value="morning">Ca sáng (07:00 - 11:30)</option>
                                <option value="afternoon">Ca chiều (13:00 - 16:30)</option>
                            </select>
                        </div>
                        <div class="field">
                            <label>Số bệnh nhân tối đa</label>
                            <input type="number" name="maxPatients" id="editMaxPatients" min="1" max="200" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn-cancel" onclick="closeEditModal()"><i class="fas fa-times"></i> Hủy</button>
                        <button type="submit" class="btn-submit-modal"><i class="fas fa-save"></i> Lưu thay đổi</button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            // Áp dụng bộ lọc
            function submitFilterForm() {
                var form = document.getElementById("filterForm");
                if (form) form.submit();
            }

            // Mở modal thêm
            function openAddModal() {
                document.getElementById("addModal").style.display = "block";
                var doctorSelect = document.getElementById("addDoctorSelect");
                if (doctorSelect) doctorSelect.focus();
            }

            // Đóng modal thêm
            function closeAddModal() {
                document.getElementById("addModal").style.display = "none";
            }

            // Mở modal chỉnh sửa
            function openEditModal(btn) {
                document.getElementById("editShiftId").value = btn.dataset.shiftId;
                document.getElementById("editDoctorId").value = btn.dataset.doctorId;
                document.getElementById("editDayOfWeek").value = btn.dataset.dayOfWeek;
                document.getElementById("editShiftType").value = btn.dataset.shiftType;
                document.getElementById("editMaxPatients").value = btn.dataset.maxPatients;
                document.getElementById("editModal").style.display = "block";
            }

            // Đóng modal chỉnh sửa
            function closeEditModal() {
                document.getElementById("editModal").style.display = "none";
            }

            // Đóng modal khi click bên ngoài
            window.onclick = function (event) {
                const addModal = document.getElementById("addModal");
                const editModal = document.getElementById("editModal");
                if (event.target === addModal) {
                    addModal.style.display = "none";
                }
                if (event.target === editModal) {
                    editModal.style.display = "none";
                }
            };

            document.addEventListener("DOMContentLoaded", function () {
                var shiftTypeFilter = document.getElementById("shiftTypeFilter");
                var dayFilter = document.getElementById("dayOfWeekFilter");
                var alerts = document.querySelectorAll(".alert");

                if (shiftTypeFilter) shiftTypeFilter.addEventListener("change", submitFilterForm);
                if (dayFilter) dayFilter.addEventListener("change", submitFilterForm);
                // Tự động đóng thông báo sau 5 giây
                if (alerts && alerts.length > 0) {
                    setTimeout(function () {
                        for (var i = 0; i < alerts.length; i++) {
                            alerts[i].classList.add("fade-out");
                        }
                        setTimeout(function () {
                            for (var i = 0; i < alerts.length; i++) {
                                if (alerts[i] && alerts[i].parentNode) {
                                    alerts[i].parentNode.removeChild(alerts[i]);
                                }
                            }
                        }, 300);
                    }, 5000);
                }
            });
        </script>
    </body>
</html>
