<%-- 
    Document   : doctorScheduleRequest
    Created on : 14 Mar 2026, 4:38:28 am
    Author     : anngu
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Yêu cầu đổi lịch làm việc</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/pages/examination/doctorScheduleRequest.css">
    </head>
    <body>
        <jsp:include page="/common/header.jsp" />

        <div class="schedule-request-container">
            <div class="hero-card">
                <h2>Quản lý lịch làm việc / Gửi yêu cầu đổi lịch</h2>
            </div>

            <c:if test="${not empty sessionScope.scheduleRequestSuccess}">
                <div class="alert success">${sessionScope.scheduleRequestSuccess}</div>
                <c:remove var="scheduleRequestSuccess" scope="session"/>
            </c:if>
            <c:if test="${not empty sessionScope.scheduleRequestError}">
                <div class="alert error">${sessionScope.scheduleRequestError}</div>
                <c:remove var="scheduleRequestError" scope="session"/>
            </c:if>

            <div class="content-grid">
                <section class="panel">
                    <h3>Lịch làm việc tuần hiện tại</h3>
                    <c:choose>
                        <c:when test="${empty weeklyShifts}">
                            <p class="empty">Bạn chưa có ca làm việc nào được cấu hình.</p>
                        </c:when>
                        <c:otherwise>
                            <table class="shift-table">
                                <thead>
                                    <tr>
                                        <th>Mã ca</th>
                                        <th>Thứ</th>
                                        <th>Giờ làm</th>
                                        <th>Số bệnh nhân tối đa</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="shift" items="${weeklyShifts}">
                                        <tr>
                                            <td>#${shift.shiftId}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${shift.dayOfWeek == 0}">Chủ nhật</c:when>
                                                    <c:when test="${shift.dayOfWeek == 1}">Thứ 2</c:when>
                                                    <c:when test="${shift.dayOfWeek == 2}">Thứ 3</c:when>
                                                    <c:when test="${shift.dayOfWeek == 3}">Thứ 4</c:when>
                                                    <c:when test="${shift.dayOfWeek == 4}">Thứ 5</c:when>
                                                    <c:when test="${shift.dayOfWeek == 5}">Thứ 6</c:when>
                                                    <c:when test="${shift.dayOfWeek == 6}">Thứ 7</c:when>
                                                    <c:otherwise>-</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>${shift.startTime} - ${shift.endTime}</td>
                                            <td>${shift.maxPatients}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:otherwise>
                    </c:choose>
                </section>

                <section class="panel">
                    <h3>Tạo đơn yêu cầu đổi lịch</h3>
                    <form method="post" action="${pageContext.request.contextPath}/doctor/schedule-request" class="request-form" id="scheduleRequestForm">
                        <div class="form-row two-col">
                            <label>Loại yêu cầu
                                <select name="requestType" id="requestType" required>
                                    <option value="TEMPORARY">Đổi lịch làm việc tạm thời</option>
                                    <option value="PERMANENT">Đổi lịch làm việc dài hạn</option>
                                </select>
                            </label>
                            <label>Phạm vi áp dụng
                                <select name="scopeType" id="scopeType" required>
                                    <option value="ONE_DATE">Một ngày cụ thể</option>
                                    <option value="WEEKLY_TEMPLATE">Theo lịch tuần</option>
                                </select>
                            </label>
                        </div>

                        <p class="form-hint" id="scopeHint"></p>

                        <div class="form-row two-col">
                            <label>Hành động
                                <select name="actionType" id="actionType" required>
                                    <option value="ADD">Thêm ca</option>
                                    <option value="UPDATE">Sửa ca</option>
                                    <option value="REMOVE">Hủy ca</option>
                                </select>
                            </label>
                        </div>

                        <div class="form-row" id="targetShiftGroup">
                            <label>Ca gốc cần sửa/hủy
                                <select name="targetShiftId" id="targetShiftId">
                                    <option value="">-- Chọn ca gốc --</option>
                                    <c:forEach var="shift" items="${weeklyShifts}">
                                        <option value="${shift.shiftId}">#${shift.shiftId} - Thứ ${shift.dayOfWeek+1} (${shift.startTime} - ${shift.endTime})</option>
                                    </c:forEach>
                                </select>
                            </label>
                        </div>

                        <div class="form-row" id="oneDateGroup">
                            <label>Ngày áp dụng
                                <input type="date" name="workDate" id="workDate">
                            </label>
                        </div>

                        <div class="form-row" id="weeklyTemplateGroup">
                            <label>Thứ áp dụng
                                <select name="dayOfWeek" id="dayOfWeek">
                                    <option value="">-- Chọn thứ --</option>
                                    <option value="0">Chủ nhật</option>
                                    <option value="1">Thứ 2</option>
                                    <option value="2">Thứ 3</option>
                                    <option value="3">Thứ 4</option>
                                    <option value="4">Thứ 5</option>
                                    <option value="5">Thứ 6</option>
                                    <option value="6">Thứ 7</option>
                                </select>
                            </label>
                        </div>

                        <div id="timeAndCapacityGroup">
                            <div class="form-row two-col">
                                <label>Giờ bắt đầu
                                    <input type="time" name="startTime" id="startTime">
                                </label>
                                <label>Giờ kết thúc
                                    <input type="time" name="endTime" id="endTime">
                                </label>
                            </div>
                            <div class="form-row">
                                <label>Số bệnh nhân tối đa
                                    <input type="number" min="1" name="maxPatients" id="maxPatients" placeholder="VD: 20">
                                </label>
                            </div>
                        </div>

                        <div class="form-row">
                            <label>Lý do gửi yêu cầu
                                <textarea name="reason" rows="4" required placeholder="Nhập lý do đổi lịch, bối cảnh và thời gian cần áp dụng..."></textarea>
                            </label>
                        </div>

                        <button type="submit" class="btn-submit">Gửi yêu cầu duyệt</button>
                    </form>
                </section>
            </div>

            <section class="panel">
                <h3>Lịch sử đơn đổi lịch gần đây</h3>
                <c:choose>
                    <c:when test="${empty recentRequests}">
                        <p class="empty">Chưa có yêu cầu nào được gửi.</p>
                    </c:when>
                    <c:otherwise>
                        <table class="request-table">
                            <thead>
                                <tr>
                                    <th>Mã đơn</th>
                                    <th>Loại</th>
                                    <th>Phạm vi</th>
                                    <th>Hành động</th>
                                    <th>Thời gian</th>
                                    <th>Trạng thái</th>
                                    <th>Ghi chú admin</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${recentRequests}">
                                    <tr>
                                        <td>#${item.requestId}</td>
                                        <td>${item.requestType}</td>
                                        <td>${item.scopeType}</td>
                                        <td>${item.actionType}</td>
                                        <td><fmt:formatDate value="${item.requestedAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                        <td>
                                            <span class="status ${item.status}">${item.status}</span>
                                        </td>
                                        <td>${empty item.adminNote ? '-' : item.adminNote}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </section>
        </div>

        <script>
            (function () {
                const requestType = document.getElementById('requestType');
                const scopeType = document.getElementById('scopeType');
                const actionType = document.getElementById('actionType');

                const scopeHint = document.getElementById('scopeHint');
                const oneDateGroup = document.getElementById('oneDateGroup');
                const weeklyTemplateGroup = document.getElementById('weeklyTemplateGroup');
                const targetShiftGroup = document.getElementById('targetShiftGroup');
                const timeAndCapacityGroup = document.getElementById('timeAndCapacityGroup');

                const oneDateInput = document.getElementById('workDate');
                const weeklyInput = document.getElementById('dayOfWeek');
                const targetShiftInput = document.getElementById('targetShiftId');
                const startInput = document.getElementById('startTime');
                const endInput = document.getElementById('endTime');
                const maxPatientsInput = document.getElementById('maxPatients');

                function toggleGroup(group, input, visible) {
                    group.classList.toggle('hidden', !visible);
                    input.disabled = !visible;
                    if (!visible) {
                        input.value = '';
                    }
                }

                function toggleTimeCapacity(visible) {
                    timeAndCapacityGroup.classList.toggle('hidden', !visible);
                    [startInput, endInput, maxPatientsInput].forEach(function (input) {
                        input.disabled = !visible;
                        if (!visible) {
                            input.value = '';
                        }
                    });
                }

                function applyTypeScopeRules() {
                    if (requestType.value === 'TEMPORARY') {
                        scopeType.value = 'ONE_DATE';
                        scopeType.querySelector('option[value="WEEKLY_TEMPLATE"]').disabled = true;
                        scopeHint.textContent = 'Yêu cầu tạm thời chỉ áp dụng cho 1 ngày cụ thể.';
                    } else {
                        scopeType.value = 'WEEKLY_TEMPLATE';
                        scopeType.querySelector('option[value="WEEKLY_TEMPLATE"]').disabled = false;
                        scopeType.querySelector('option[value="ONE_DATE"]').disabled = true;
                        scopeHint.textContent = 'Yêu cầu dài hạn sẽ thay đổi lịch tuần chuẩn.';
                        return;
                    }
                    scopeType.querySelector('option[value="ONE_DATE"]').disabled = false;
                }

                function renderFormBySelection() {
                    applyTypeScopeRules();

                    const scope = scopeType.value;
                    const action = actionType.value;

                    toggleGroup(oneDateGroup, oneDateInput, scope === 'ONE_DATE');
                    toggleGroup(weeklyTemplateGroup, weeklyInput, scope === 'WEEKLY_TEMPLATE');
                    toggleGroup(targetShiftGroup, targetShiftInput, action === 'UPDATE' || action === 'REMOVE');
                    toggleTimeCapacity(action === 'ADD' || action === 'UPDATE');
                }

                requestType.addEventListener('change', renderFormBySelection);
                scopeType.addEventListener('change', renderFormBySelection);
                actionType.addEventListener('change', renderFormBySelection);
                renderFormBySelection();
            })();
        </script>
        <jsp:include page="/common/footer.jsp" />
    </body>
</html>