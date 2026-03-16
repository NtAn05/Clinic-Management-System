<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Duyet yeu cau doi lich bac si</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
                min-height: 100vh;
            }

            .container {
                padding: 30px 50px;
                max-width: 1400px;
                margin: 0 auto;
            }

            .panel {
                background: white;
                padding: 20px;
                border-radius: 10px;
                margin-bottom: 20px;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            }

            .head {
                display: flex;
                justify-content: space-between;
                align-items: center;
                gap: 10px;
                margin-bottom: 8px;
            }

            .head h2 {
                margin: 0;
                font-size: 22px;
                color: #1f2937;
            }

            .sub {
                color: #64748b;
                font-size: 14px;
            }

            .alert {
                padding: 15px 20px;
                border-radius: 8px;
                margin-bottom: 20px;
                display: flex;
                align-items: center;
                gap: 10px;
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
                background: white;
                padding: 20px;
                border-radius: 10px;
                margin-bottom: 20px;
                display: grid;
                grid-template-columns: minmax(340px, 1.9fr) minmax(170px, 0.85fr) minmax(170px, 0.85fr) minmax(170px, 0.85fr) auto;
                gap: 12px;
                align-items: end;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            }

            .search-box,
            .filter-box {
                min-width: 0;
            }

            .search-box label,
            .filter-box label {
                display: block;
                font-weight: 600;
                margin-bottom: 8px;
                color: #333;
                font-size: 13px;
            }

            .search-box input,
            .filter-box select {
                width: 100%;
                padding: 10px 15px;
                border: 1px solid #ddd;
                border-radius: 6px;
                font-size: 14px;
                transition: all 0.3s ease;
            }

            .search-box input:focus,
            .filter-box select:focus {
                outline: none;
                border-color: #0061ff;
                box-shadow: 0 0 0 3px rgba(0, 97, 255, 0.1);
            }

            .toolbar-buttons {
                display: flex;
                gap: 10px;
                align-self: end;
            }

            .btn-search {
                padding: 10px 16px;
                border: none;
                border-radius: 6px;
                cursor: pointer;
                font-weight: 600;
                font-size: 14px;
                display: inline-flex;
                align-items: center;
                gap: 6px;
                background: #0061ff;
                color: white;
            }

            .btn-search:hover {
                background: #0052cc;
                transform: translateY(-2px);
                box-shadow: 0 4px 12px rgba(0, 97, 255, 0.3);
            }

            .btn-reset {
                padding: 10px 16px;
                border: none;
                border-radius: 6px;
                cursor: pointer;
                font-weight: 600;
                font-size: 14px;
                display: inline-flex;
                align-items: center;
                gap: 6px;
                text-decoration: none;
                background: #f0f0f0;
                color: #333;
            }

            .btn-reset:hover {
                background: #e0e0e0;
            }

            .btn-muted {
                padding: 10px 16px;
                border: none;
                border-radius: 6px;
                cursor: pointer;
                font-weight: 600;
                font-size: 14px;
                display: inline-flex;
                align-items: center;
                gap: 6px;
                text-decoration: none;
                background: #f1f5f9;
                color: #0f172a;
            }

            .table-container {
                background: white;
                padding: 25px;
                border-radius: 10px;
                box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
                overflow-x: auto;
            }

            table {
                width: 100%;
                border-collapse: collapse;
                min-width: 1000px;
            }

            th {
                background: linear-gradient(135deg, #f8f9fa 0%, #f0f0f0 100%);
                padding: 15px;
                text-align: left;
                font-weight: 600;
                color: #333;
                border-bottom: 2px solid #e0e0e0;
                font-size: 14px;
            }

            td {
                padding: 15px;
                border-bottom: 1px solid #f0f0f0;
                color: #555;
                font-size: 14px;
                vertical-align: top;
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
                white-space: nowrap;
            }

            .status.PENDING {
                background: #fff3cd;
                color: #856404;
            }

            .status.APPROVED {
                background: #e8f5e9;
                color: #388e3c;
            }

            .status.REJECTED {
                background: #ffebee;
                color: #d32f2f;
            }

            .action-col {
                width: 150px;
            }

            .action-stack {
                display: flex;
                flex-direction: column;
                gap: 8px;
            }

            .action-row {
                display: flex;
                gap: 6px;
                align-items: center;
            }

            .action-stack textarea {
                border: 1px solid #ddd;
                border-radius: 6px;
                padding: 8px 10px;
                font-size: 13px;
                resize: vertical;
                min-height: 64px;
            }

            .btn-action {
                border: none;
                background: none;
                cursor: pointer;
                font-size: 16px;
                padding: 6px 10px;
                border-radius: 4px;
                transition: all 0.3s ease;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                text-decoration: none;
            }

            .btn-view {
                color: #FB923C;
            }

            .btn-view:hover {
                background: #FFEDD5;
            }

            .btn-approve {
                color: #16a34a;
            }

            .btn-approve:hover {
                background: #dcfce7;
            }

            .btn-reject {
                color: #dc2626;
            }

            .btn-reject:hover {
                background: #fee2e2;
            }

            .details-panel summary {
                list-style: none;
            }

            .details-panel summary::-webkit-details-marker {
                display: none;
            }

            .empty {
                text-align: center;
                padding: 40px;
                color: #999;
            }

            .modal {
                display: none;
                position: fixed;
                z-index: 1000;
                left: 0;
                top: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0, 0, 0, 0.5);
                animation: fadeIn 0.3s ease;
                overflow-y: auto;
            }

            @keyframes fadeIn {
                from { opacity: 0; }
                to { opacity: 1; }
            }

            .modal-content {
                background-color: white;
                margin: 5% auto;
                padding: 30px;
                border-radius: 10px;
                width: 90%;
                max-width: 680px;
                box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
                animation: slideUp 0.3s ease;
            }

            @keyframes slideUp {
                from { transform: translateY(50px); opacity: 0; }
                to { transform: translateY(0); opacity: 1; }
            }

            .modal-header {
                font-size: 30px;
                font-weight: 600;
                color: #0061ff;
                margin-bottom: 25px;
                display: flex;
                align-items: center;
                gap: 10px;
                border-bottom: 2px solid #f0f0f0;
                padding-bottom: 15px;
            }

            .modal-close {
                margin-left: auto;
                cursor: pointer;
                font-size: 30px;
                line-height: 1;
                background: none;
                border: none;
                color: #999;
                transition: all 0.3s ease;
            }

            .modal-close:hover {
                color: #333;
            }

            .form-info {
                background: #f5f7fa;
                padding: 15px;
                border-radius: 6px;
                margin-bottom: 15px;
                font-size: 14px;
                color: #555;
                line-height: 1.6;
            }

            .form-info-item {
                display: flex;
                justify-content: space-between;
                gap: 12px;
                padding: 8px 0;
                border-bottom: 1px solid #e0e0e0;
            }

            .form-info-item:last-child {
                border-bottom: none;
            }

            .form-info-item strong {
                color: #333;
            }

            .modal-footer {
                display: flex;
                gap: 10px;
                justify-content: flex-end;
                margin-top: 25px;
                padding-top: 15px;
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
                display: flex;
                align-items: center;
                gap: 6px;
            }

            .btn-cancel:hover {
                background: #e0e0e0;
            }

            @media (max-width: 980px) {
                .container {
                    padding: 20px;
                }

                .toolbar {
                    grid-template-columns: 1fr;
                }

                .toolbar-buttons {
                    width: 100%;
                }

                .toolbar-buttons .btn-reset {
                    width: 100%;
                    justify-content: center;
                }
            }
        </style>
    </head>
    <body>
        <jsp:include page="/common/header.jsp" />

        <div class="container">
            <div class="panel">
                <div class="head">
                    <div>
                        <h2>Duyet yeu cau doi lich bac si</h2>
                        <div class="sub">Don cho duyet hien tai: <strong>${pendingCount}</strong></div>
                    </div>
                    <a class="btn-muted" href="${pageContext.request.contextPath}/admin-doctor-schedules">
                        <i class="fas fa-arrow-left"></i> Quay lai lich bac si
                    </a>
                </div>

                <c:if test="${not empty sessionScope.scheduleReviewSuccess}">
                    <div class="alert success">${sessionScope.scheduleReviewSuccess}</div>
                    <c:remove var="scheduleReviewSuccess" scope="session"/>
                </c:if>
                <c:if test="${not empty sessionScope.scheduleReviewError}">
                    <div class="alert error">${sessionScope.scheduleReviewError}</div>
                    <c:remove var="scheduleReviewError" scope="session"/>
                </c:if>
            </div>

            <form id="filterForm" method="GET" action="${pageContext.request.contextPath}/admin-schedule-requests" class="toolbar">
                <div class="search-box">
                    <label>Tim kiem</label>
                    <input type="text" id="keywordInput" name="keyword" value="${keyword}" placeholder="Ten bac si, ma don, ly do...">
                </div>
                <div class="filter-box">
                    <label>Loai</label>
                    <select name="requestType" onchange="this.form.submit()">
                        <option value="ALL" ${requestTypeFilter == 'ALL' ? 'selected' : ''}>Tat ca</option>
                        <option value="TEMPORARY" ${requestTypeFilter == 'TEMPORARY' ? 'selected' : ''}>Tam thoi</option>
                        <option value="PERMANENT" ${requestTypeFilter == 'PERMANENT' ? 'selected' : ''}>Vinh vien</option>
                    </select>
                </div>
                <div class="filter-box">
                    <label>Yeu cau</label>
                    <select name="actionType" onchange="this.form.submit()">
                        <option value="ALL" ${actionTypeFilter == 'ALL' ? 'selected' : ''}>Tat ca</option>
                        <option value="ADD" ${actionTypeFilter == 'ADD' ? 'selected' : ''}>Them ca</option>
                        <option value="UPDATE" ${actionTypeFilter == 'UPDATE' ? 'selected' : ''}>Cap nhat ca</option>
                        <option value="REMOVE" ${actionTypeFilter == 'REMOVE' ? 'selected' : ''}>Xoa ca</option>
                    </select>
                </div>
                <div class="filter-box">
                    <label>Trang thai</label>
                    <select name="status" onchange="this.form.submit()">
                        <option value="ALL" ${statusFilter == 'ALL' ? 'selected' : ''}>Tat ca</option>
                        <option value="PENDING" ${statusFilter == 'PENDING' ? 'selected' : ''}>Cho duyet</option>
                        <option value="APPROVED" ${statusFilter == 'APPROVED' ? 'selected' : ''}>Da duyet</option>
                        <option value="REJECTED" ${statusFilter == 'REJECTED' ? 'selected' : ''}>Da tu choi</option>
                    </select>
                </div>
                <div class="toolbar-buttons">
                    <button class="btn-search" type="submit"><i class="fas fa-search"></i> Tim</button>
                    <a class="btn-reset" href="${pageContext.request.contextPath}/admin-schedule-requests"><i class="fas fa-redo"></i> Dat lai</a>
                </div>
            </form>

            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Bac si</th>
                            <th>Loai</th>
                            <th>Yeu cau</th>
                            <th>Ngay ap dung</th>
                            <th>Gui luc</th>
                            <th>Trang thai</th>
                            <th class="action-col">Thao tac</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty requests}">
                                <tr>
                                    <td colspan="7" class="empty">Khong co don phu hop bo loc hien tai.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="item" items="${requests}">
                                    <tr>
                                        <td>${item.doctorName}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${item.requestType == 'TEMPORARY'}">Tam thoi</c:when>
                                                <c:otherwise>Vinh vien</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${item.actionType == 'REMOVE'}">Xoa ca</c:when>
                                                <c:when test="${item.actionType == 'UPDATE'}">Cap nhat ca</c:when>
                                                <c:otherwise>Them ca</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${item.scopeType == 'ONE_DATE' && not empty item.workDate}">
                                                    <fmt:formatDate value="${item.workDate}" pattern="dd/MM/yyyy" />
                                                </c:when>
                                                <c:when test="${not empty item.dayOfWeek}">
                                                    <c:choose>
                                                        <c:when test="${item.dayOfWeek == 0}">Chu nhat</c:when>
                                                        <c:when test="${item.dayOfWeek == 1}">Thu 2</c:when>
                                                        <c:when test="${item.dayOfWeek == 2}">Thu 3</c:when>
                                                        <c:when test="${item.dayOfWeek == 3}">Thu 4</c:when>
                                                        <c:when test="${item.dayOfWeek == 4}">Thu 5</c:when>
                                                        <c:when test="${item.dayOfWeek == 5}">Thu 6</c:when>
                                                        <c:otherwise>Thu 7</c:otherwise>
                                                    </c:choose>
                                                </c:when>
                                                <c:otherwise>-</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td><fmt:formatDate value="${item.requestedAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                                        <td>
                                            <span class="status ${item.status}">
                                                <c:choose>
                                                    <c:when test="${item.status == 'PENDING'}">Cho duyet</c:when>
                                                    <c:when test="${item.status == 'APPROVED'}">Da duyet</c:when>
                                                    <c:otherwise>Tu choi</c:otherwise>
                                                </c:choose>
                                            </span>
                                        </td>
                                        <td class="action-col">
                                            <c:choose>
                                                <c:when test="${item.status == 'PENDING'}">
                                                    <form method="POST" action="${pageContext.request.contextPath}/admin-schedule-requests" class="action-stack">
                                                        <input type="hidden" name="action" value="review">
                                                        <input type="hidden" name="requestId" value="${item.requestId}">
                                                        <input type="hidden" name="status" value="${statusFilter}">
                                                        <input type="hidden" name="requestType" value="${requestTypeFilter}">
                                                        <input type="hidden" name="actionType" value="${actionTypeFilter}">
                                                        <input type="hidden" name="keyword" value="${keyword}">
                                                        <div class="action-row">
                                                            <button
                                                                class="btn-action btn-view"
                                                                type="button"
                                                                title="Xem chi tiet"
                                                                data-request-id="${item.requestId}"
                                                                data-doctor-name="${fn:escapeXml(item.doctorName)}"
                                                                data-request-type="${item.requestType}"
                                                                data-action-type="${item.actionType}"
                                                                data-scope-type="${item.scopeType}"
                                                                data-work-date="${item.workDate}"
                                                                data-day-of-week="${item.dayOfWeek}"
                                                                data-requested-at="<fmt:formatDate value='${item.requestedAt}' pattern='dd/MM/yyyy HH:mm' />"
                                                                data-status="${item.status}"
                                                                data-reason="${fn:escapeXml(item.reason)}"
                                                                data-target-shift-id="${item.targetShiftId}"
                                                                data-start-time="${item.startTime}"
                                                                data-end-time="${item.endTime}"
                                                                data-max-patients="${item.maxPatients}"
                                                                data-admin-note="${fn:escapeXml(item.adminNote)}"
                                                                onclick="viewRequestDetail(this)">
                                                                <i class="fas fa-eye"></i>
                                                            </button>
                                                            <button class="btn-action btn-approve" type="submit" name="decision" value="APPROVED" title="Dong y">
                                                                <i class="fas fa-check"></i>
                                                            </button>
                                                            <button class="btn-action btn-reject" type="submit" name="decision" value="REJECTED" title="Tu choi">
                                                                <i class="fas fa-xmark"></i>
                                                            </button>
                                                        </div>
                                                        <textarea name="adminNote" placeholder="Ghi chu xu ly (tuy chon)"></textarea>
                                                    </form>
                                                </c:when>
                                                <c:otherwise>
                                                    <button
                                                        class="btn-action btn-view"
                                                        type="button"
                                                        title="Xem chi tiet"
                                                        data-request-id="${item.requestId}"
                                                        data-doctor-name="${fn:escapeXml(item.doctorName)}"
                                                        data-request-type="${item.requestType}"
                                                        data-action-type="${item.actionType}"
                                                        data-scope-type="${item.scopeType}"
                                                        data-work-date="${item.workDate}"
                                                        data-day-of-week="${item.dayOfWeek}"
                                                        data-requested-at="<fmt:formatDate value='${item.requestedAt}' pattern='dd/MM/yyyy HH:mm' />"
                                                        data-status="${item.status}"
                                                        data-reason="${fn:escapeXml(item.reason)}"
                                                        data-target-shift-id="${item.targetShiftId}"
                                                        data-start-time="${item.startTime}"
                                                        data-end-time="${item.endTime}"
                                                        data-max-patients="${item.maxPatients}"
                                                        data-admin-note="${fn:escapeXml(item.adminNote)}"
                                                        onclick="viewRequestDetail(this)">
                                                        <i class="fas fa-eye"></i>
                                                    </button>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>

            <div id="viewRequestModal" class="modal">
                <div class="modal-content">
                    <div class="modal-header">
                        <i class="fas fa-file-circle-check"></i>
                        <span>Chi tiet don doi lich</span>
                        <button class="modal-close" type="button" onclick="closeRequestModal()">×</button>
                    </div>

                    <div class="form-info">
                        <div class="form-info-item"><strong>Ma don:</strong><span id="mRequestId"></span></div>
                        <div class="form-info-item"><strong>Bac si:</strong><span id="mDoctorName"></span></div>
                        <div class="form-info-item"><strong>Loai:</strong><span id="mRequestType"></span></div>
                        <div class="form-info-item"><strong>Yeu cau:</strong><span id="mActionType"></span></div>
                        <div class="form-info-item"><strong>Ngay ap dung:</strong><span id="mApplyDate"></span></div>
                        <div class="form-info-item"><strong>Gui luc:</strong><span id="mRequestedAt"></span></div>
                        <div class="form-info-item"><strong>Trang thai:</strong><span id="mStatus"></span></div>
                        <div class="form-info-item"><strong>Ly do:</strong><span id="mReason"></span></div>
                        <div class="form-info-item" id="mTargetShiftRow"><strong>Ca goc:</strong><span id="mTargetShift"></span></div>
                        <div class="form-info-item" id="mTimeRangeRow"><strong>Khung gio:</strong><span id="mTimeRange"></span></div>
                        <div class="form-info-item" id="mMaxPatientsRow"><strong>So BN toi da:</strong><span id="mMaxPatients"></span></div>
                        <div class="form-info-item"><strong>Ghi chu admin:</strong><span id="mAdminNote"></span></div>
                    </div>

                    <div class="modal-footer">
                        <button class="btn-cancel" type="button" onclick="closeRequestModal()">
                            <i class="fas fa-times"></i> Dong
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <script>
            function mapRequestType(value) {
                return value === 'TEMPORARY' ? 'Tam thoi' : 'Vinh vien';
            }

            function mapActionType(value) {
                if (value === 'REMOVE') return 'Xoa ca';
                if (value === 'UPDATE') return 'Cap nhat ca';
                return 'Them ca';
            }

            function mapDayOfWeek(value) {
                const map = {
                    '0': 'Chu nhat',
                    '1': 'Thu 2',
                    '2': 'Thu 3',
                    '3': 'Thu 4',
                    '4': 'Thu 5',
                    '5': 'Thu 6',
                    '6': 'Thu 7'
                };
                return map[String(value)] || '-';
            }

            function mapStatusText(value) {
                if (value === 'PENDING') return 'Cho duyet';
                if (value === 'APPROVED') return 'Da duyet';
                return 'Tu choi';
            }

            function mapApplyDate(workDate, dayOfWeek) {
                if (workDate && workDate !== 'null') {
                    const parts = workDate.split('-');
                    if (parts.length === 3) {
                        return parts[2] + '/' + parts[1] + '/' + parts[0];
                    }
                    return workDate;
                }
                if (dayOfWeek && dayOfWeek !== 'null') {
                    return mapDayOfWeek(dayOfWeek);
                }
                return '-';
            }

            function viewRequestDetail(btn) {
                const d = btn.dataset;
                document.getElementById('mRequestId').textContent = '#' + (d.requestId || '-');
                document.getElementById('mDoctorName').textContent = d.doctorName || '-';
                document.getElementById('mRequestType').textContent = mapRequestType(d.requestType);
                document.getElementById('mActionType').textContent = mapActionType(d.actionType);
                document.getElementById('mApplyDate').textContent = mapApplyDate(d.workDate, d.dayOfWeek);
                document.getElementById('mRequestedAt').textContent = d.requestedAt || '-';

                const statusNode = document.getElementById('mStatus');
                statusNode.innerHTML = '<span class=\"status ' + (d.status || 'PENDING') + '\">' + mapStatusText(d.status) + '</span>';

                document.getElementById('mReason').textContent = d.reason || '-';

                const targetShiftRow = document.getElementById('mTargetShiftRow');
                const timeRangeRow = document.getElementById('mTimeRangeRow');
                const maxPatientsRow = document.getElementById('mMaxPatientsRow');

                if (d.targetShiftId && d.targetShiftId !== 'null') {
                    targetShiftRow.style.display = 'flex';
                    document.getElementById('mTargetShift').textContent = '#' + d.targetShiftId;
                } else {
                    targetShiftRow.style.display = 'none';
                }

                if (d.startTime && d.endTime && d.startTime !== 'null' && d.endTime !== 'null') {
                    timeRangeRow.style.display = 'flex';
                    document.getElementById('mTimeRange').textContent = d.startTime + ' - ' + d.endTime;
                } else {
                    timeRangeRow.style.display = 'none';
                }

                if (d.maxPatients && d.maxPatients !== 'null') {
                    maxPatientsRow.style.display = 'flex';
                    document.getElementById('mMaxPatients').textContent = d.maxPatients;
                } else {
                    maxPatientsRow.style.display = 'none';
                }
                document.getElementById('mAdminNote').textContent = (d.adminNote && d.adminNote !== 'null') ? d.adminNote : '-';

                document.getElementById('viewRequestModal').style.display = 'block';
            }

            function closeRequestModal() {
                document.getElementById('viewRequestModal').style.display = 'none';
            }

            window.addEventListener('click', function (event) {
                const modal = document.getElementById('viewRequestModal');
                if (event.target === modal) {
                    closeRequestModal();
                }
            });
        </script>
        <jsp:include page="/common/footer.jsp" />
    </body>
</html>
