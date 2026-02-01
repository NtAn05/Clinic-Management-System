<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <title>Technician – Lab Queue</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
  <style>
    :root {
      --primary: #2563eb;
      --primary-soft: #e0edff;
      --bg: #f3f4f6;
      --card: #ffffff;
      --text-main: #111827;
      --text-sub: #6b7280;
      --border: #e5e7eb;
      --danger: #ef4444;
      --success: #16a34a;
      --warning: #f59e0b;
      --radius-lg: 14px;
      --radius-md: 10px;
    }

    * {
      box-sizing: border-box;
      font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI",
        sans-serif;
    }

    body {
      margin: 0;
      padding-top: 60px; /* Space for fixed header */
      background: var(--bg);
      color: var(--text-main);
      display: flex;
      flex-direction: column;
      min-height: 100vh;
    }

    .main-container {
      display: flex;
      flex: 1;
      margin-top: 0;
    }

    .content-wrapper {
      flex: 1;
      margin-left: 260px; /* Sidebar width */
      min-height: calc(100vh - 60px);
    }

    /* Responsive: Hide sidebar on mobile */
    @media (max-width: 768px) {
      .content-wrapper {
        margin-left: 0;
      }
    }

    .page {
      padding: 20px 24px 32px;
    }

    .page-title {
      display: flex;
      flex-direction: column;
      gap: 6px;
    }

    .page-title h1 {
      margin: 0;
      font-size: 22px;
      font-weight: 600;
    }

    .page-title span {
      font-size: 13px;
      color: var(--text-sub);
    }

    .page-actions {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .btn {
      border-radius: 999px;
      border: 1px solid transparent;
      padding: 8px 16px;
      font-size: 13px;
      font-weight: 500;
      cursor: pointer;
      display: inline-flex;
      align-items: center;
      gap: 6px;
      background: #fff;
      color: var(--text-main);
      text-decoration: none;
    }

    .btn-primary {
      background: var(--primary);
      color: #fff;
      box-shadow: 0 8px 18px rgba(37, 99, 235, 0.35);
    }

    .btn-outline {
      background: transparent;
      border-color: var(--border);
    }

    .btn-success {
      background: var(--success);
      color: #fff;
      border-color: transparent;
    }

    .btn-success:hover {
      filter: brightness(1.05);
    }

    .btn i {
      margin-right: 2px;
    }

    .layout {
      display: block;
      width: 100%;
    }

    /* Card chung */
    .card {
      background: var(--card);
      border-radius: var(--radius-lg);
      padding: 16px 18px 18px;
      box-shadow: 0 12px 35px rgba(15, 23, 42, 0.05);
      border: 1px solid rgba(148, 163, 184, 0.18);
    }

    .card-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 12px;
    }

    .card-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 15px;
      font-weight: 600;
    }

    .card-title-icon {
      width: 26px;
      height: 26px;
      border-radius: 999px;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      font-size: 14px;
      background: var(--primary-soft);
      color: var(--primary);
    }

    .card-subtitle {
      font-size: 12px;
      color: var(--text-sub);
    }

    /* Bộ lọc, tìm kiếm */
    .filters {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;
      margin-bottom: 12px;
    }

    .field-group {
      display: flex;
      flex-direction: column;
      gap: 4px;
      flex: 1 1 130px;
      min-width: 130px;
    }

    .field-label {
      font-size: 11px;
      color: var(--text-sub);
    }

    .select,
    .input {
      border-radius: var(--radius-md);
      border: 1px solid var(--border);
      padding: 7px 10px;
      font-size: 13px;
      outline: none;
      background: #f9fafb;
    }

    .select:focus,
    .input:focus {
      border-color: var(--primary);
      background: #fff;
      box-shadow: 0 0 0 1px rgba(37, 99, 235, 0.12);
    }

    .queue-summary {
      display: flex;
      gap: 10px;
      margin-bottom: 8px;
      flex-wrap: wrap;
    }

    .chip {
      padding: 5px 9px;
      border-radius: 999px;
      font-size: 11px;
      display: inline-flex;
      align-items: center;
      gap: 6px;
      background: #f9fafb;
      color: var(--text-sub);
    }

    .chip-dot {
      width: 8px;
      height: 8px;
      border-radius: 999px;
    }

    .dot-all {
      background: var(--primary);
    }

    .dot-pending {
      background: var(--warning);
    }

    .dot-inprogress {
      background: var(--primary);
    }

    .dot-done {
      background: var(--success);
    }

    /* Bảng queue */
    .table-wrapper {
      border-radius: 12px;
      border: 1px solid var(--border);
      overflow: hidden;
      background: #f9fafb;
    }

    table {
      width: 100%;
      border-collapse: collapse;
      font-size: 13px;
    }

    thead {
      background: #eef2ff;
      color: #4b5563;
    }

    th,
    td {
      padding: 8px 10px;
      text-align: left;
      border-bottom: 1px solid #e5e7eb;
    }

    th {
      font-weight: 600;
      font-size: 12px;
      white-space: nowrap;
    }

    tbody tr:nth-child(even) {
      background: #fdfdfd;
    }

    tbody tr:hover {
      background: #e0edff;
    }

    tbody tr:hover td button {
      box-shadow: 0 2px 8px rgba(37, 99, 235, 0.2);
    }

    .status-pill {
      padding: 4px 8px;
      border-radius: 999px;
      font-size: 11px;
      display: inline-flex;
      align-items: center;
      gap: 5px;
    }

    .status-pending {
      background: #fef3c7;
      color: #92400e;
    }

    .status-inprogress {
      background: #dbeafe;
      color: #1d4ed8;
    }

    .status-done {
      background: #dcfce7;
      color: #166534;
    }

    .badge-priority {
      padding: 2px 6px;
      border-radius: 8px;
      font-size: 10px;
      font-weight: 600;
      color: #b91c1c;
      background: #fee2e2;
    }

    .text-muted {
      color: var(--text-sub);
      font-size: 12px;
    }

    .text-right {
      text-align: right;
    }

    /* Panel chi tiết bên phải */
    .section {
      margin-bottom: 16px;
    }

    .section-title {
      font-size: 13px;
      font-weight: 600;
      margin-bottom: 6px;
      display: flex;
      align-items: center;
      gap: 6px;
    }

    .section-body {
      border-radius: 12px;
      border: 1px dashed var(--border);
      background: #f9fafb;
      padding: 10px 12px;
      font-size: 13px;
    }

    .detail-row {
      display: flex;
      justify-content: space-between;
      gap: 6px;
      margin-bottom: 6px;
    }

    .detail-label {
      font-size: 12px;
      color: var(--text-sub);
    }

    .detail-value {
      font-size: 13px;
      font-weight: 500;
      text-align: right;
    }

    .tag-list {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
      margin-top: 4px;
    }

    .tag {
      font-size: 11px;
      padding: 3px 7px;
      border-radius: 999px;
      background: #e5e7eb;
      color: #374151;
    }

    .tag-blue {
      background: #dbeafe;
      color: #1d4ed8;
    }

    .tag-green {
      background: #dcfce7;
      color: #166534;
    }

    .note-box {
      width: 100%;
      min-height: 80px;
      padding: 8px 10px;
      border-radius: var(--radius-md);
      border: 1px solid var(--border);
      font-size: 13px;
      resize: vertical;
      outline: none;
      background: #f9fafb;
    }

    .note-box:focus {
      border-color: var(--primary);
      background: #fff;
    }

    .detail-footer {
      display: flex;
      justify-content: flex-end;
      gap: 8px;
      margin-top: 10px;
    }

    @media (max-width: 960px) {
      .layout {
        grid-template-columns: 1fr;
      }
    }
  </style>
</head>
<body>
  <jsp:include page="../../common/header.jsp" />

  <div class="main-container">
    <!-- Include Sidebar -->
    <jsp:include page="../../common/sidebar.jsp" />

    <!-- Content Wrapper -->
    <div class="content-wrapper">
      <div class="page" style="max-width: 1280px; margin: 0 auto;">

    <!-- Danh sách Lab Queue Full Width -->
    <div class="card">
        <div class="card-header">
          <div>
            <div class="card-title">
              <span class="card-title-icon">🔬</span>
              <span>Danh sách xét nghiệm chờ</span>
            </div>
            <div class="card-subtitle">
              Nhấp vào phiếu để xem chi tiết và gửi kết quả xét nghiệm
            </div>
          </div>
          <div class="text-right text-muted">
            Tổng: <strong id="totalCount">${totalRecords}</strong> phiếu
            <c:if test="${totalPages > 1}">
              <span style="margin-left: 10px;">(Trang ${currentPage}/${totalPages})</span>
            </c:if>
          </div>
        </div>

        <!-- BỘ LỌC -->
        <form method="GET" action="${pageContext.request.contextPath}/lab-queue" id="filterForm">
          <input type="hidden" name="page" value="1" />
          <div class="filters">
            <div class="field-group">
              <label class="field-label">Trạng thái</label>
              <select class="select" name="status" id="filterStatus">
                <option value="">Tất cả</option>
                <option value="pending" ${filterStatus == 'pending' ? 'selected' : ''}>Chờ lấy mẫu</option>
                <option value="processing" ${filterStatus == 'processing' ? 'selected' : ''}>Đang xét nghiệm</option>
                <option value="completed" ${filterStatus == 'completed' ? 'selected' : ''}>Đã có kết quả</option>
              </select>
            </div>

            <div class="field-group">
              <label class="field-label">Khoa / Phòng gửi</label>
              <select class="select" name="department" id="filterDepartment">
                <option value="">Tất cả</option>
                <c:forEach var="spec" items="${specializations}">
                  <option value="${spec}" ${filterDepartment == spec ? 'selected' : ''}>${spec}</option>
                </c:forEach>
              </select>
            </div>

            <div class="field-group" style="flex: 2 1 220px;">
              <label class="field-label">Tìm theo tên BN / Mã BN / Mã phiếu</label>
              <input class="input" name="search" id="searchInput" placeholder="Nhập từ khóa tìm kiếm..." value="${searchTerm}" />
            </div>

            <div class="field-group" style="flex: 0 0 auto; align-self: flex-end;">
              <button type="button" id="clearFiltersBtn" class="btn btn-outline" style="white-space: nowrap;">
                <i class="fas fa-times-circle"></i> Xóa bộ lọc
              </button>
            </div>
          </div>
        </form>

        <!-- TÓM TẮT -->
        <div class="queue-summary">
          <div class="chip">
            <span class="chip-dot dot-all"></span>
            <span><strong>${stats[0]}</strong> phiếu</span>
          </div>
          <div class="chip">
            <span class="chip-dot dot-pending"></span>
            <span>Chờ lấy mẫu: <strong>${stats[1]}</strong></span>
          </div>
          <div class="chip">
            <span class="chip-dot dot-inprogress"></span>
            <span>Đang xét nghiệm: <strong>${stats[2]}</strong></span>
          </div>
          <div class="chip">
            <span class="chip-dot dot-done"></span>
            <span>Đã có kết quả: <strong>${stats[3]}</strong></span>
          </div>
        </div>

        <!-- BẢNG HÀNG ĐỢI -->
        <div class="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Mã phiếu</th>
                <th>Bệnh nhân</th>
                <th>Tuổi / Giới</th>
                <th>Khoa gửi</th>
                <th>Triệu chứng</th>
                <th>Giờ chỉ định</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody id="labQueueTableBody">
              <c:choose>
                <c:when test="${empty labRequests}">
                  <tr>
                    <td colspan="8" style="text-align: center; padding: 40px; color: var(--text-sub);">
                      Không có dữ liệu
                    </td>
                  </tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="request" items="${labRequests}">
                    <fmt:formatDate value="${request.createdAt}" pattern="yyyy" var="year" />
                    <c:set var="requestCode" value="LAB-${year}-${request.requestId}" />
                    <c:set var="patientCode" value="BN${request.patient.patientId}" />
                    <c:set var="age" value="-" />
                    <c:if test="${request.patient.dob != null}">
                      <jsp:useBean id="now" class="java.util.Date" />
                      <fmt:formatDate value="${request.patient.dob}" pattern="yyyy" var="birthYear" />
                      <fmt:formatDate value="${now}" pattern="yyyy" var="currentYear" />
                      <c:set var="age" value="${currentYear - birthYear}" />
                    </c:if>
                    <c:set var="genderText" value="${request.patient.gender == 'male' ? 'Nam' : (request.patient.gender == 'female' ? 'Nữ' : 'Khác')}" />
                    <c:set var="statusText" value="${request.status == 'pending' ? 'Chờ lấy mẫu' : (request.status == 'processing' ? 'Đang xét nghiệm' : 'Đã có kết quả')}" />
                    <c:set var="statusClass" value="${request.status == 'pending' ? 'status-pending' : (request.status == 'processing' ? 'status-inprogress' : 'status-done')}" />
                    <tr class="queue-row" data-request-id="${request.requestId}">
                      <td onclick="window.location.href='${pageContext.request.contextPath}/lab-queue?action=viewSendResult&requestId=${request.requestId}'" style="cursor: pointer;">${requestCode}</td>
                      <td onclick="window.location.href='${pageContext.request.contextPath}/lab-queue?action=viewSendResult&requestId=${request.requestId}'" style="cursor: pointer;">
                        ${request.patient.fullName}<br />
                        <span class="text-muted">${patientCode}</span>
                      </td>
                      <td onclick="window.location.href='${pageContext.request.contextPath}/lab-queue?action=viewSendResult&requestId=${request.requestId}'" style="cursor: pointer;">${age} / ${genderText}</td>
                      <td onclick="window.location.href='${pageContext.request.contextPath}/lab-queue?action=viewSendResult&requestId=${request.requestId}'" style="cursor: pointer;">${request.doctor.specialization}</td>
                      <td onclick="window.location.href='${pageContext.request.contextPath}/lab-queue?action=viewSendResult&requestId=${request.requestId}'" style="cursor: pointer;">${request.appointment.symptom != null ? request.appointment.symptom : '-'}</td>
                      <td onclick="window.location.href='${pageContext.request.contextPath}/lab-queue?action=viewSendResult&requestId=${request.requestId}'" style="cursor: pointer;">
                        <fmt:formatDate value="${request.createdAt}" pattern="HH:mm" />
                      </td>
                      <td onclick="window.location.href='${pageContext.request.contextPath}/lab-queue?action=viewSendResult&requestId=${request.requestId}'" style="cursor: pointer;">
                        <span class="status-pill ${statusClass}">
                          ● ${statusText}
                        </span>
                      </td>
                      <td>
                        <c:choose>
                          <c:when test="${request.status == 'pending'}">
                            <button class="btn btn-success" onclick="event.stopPropagation(); updateStatusToProcessing(${request.requestId});" style="font-size: 12px; padding: 6px 12px;">
                              <i class="fas fa-play"></i> Bắt đầu XN
                            </button>
                          </c:when>
                          <c:when test="${request.status == 'processing'}">
                            <button class="btn btn-primary" onclick="event.stopPropagation(); window.location.href='${pageContext.request.contextPath}/lab-queue?action=viewSendResult&requestId=${request.requestId}';" style="font-size: 12px; padding: 6px 12px;">
                              <i class="fas fa-paper-plane"></i> Gửi KQ
                            </button>
                          </c:when>
                          <c:otherwise>
                            <span class="text-muted" style="font-size: 12px;">Đã hoàn thành</span>
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

        <!-- PHÂN TRANG -->
        <c:if test="${totalPages > 1}">
          <div class="pagination-wrapper" style="margin-top: 16px; display: flex; justify-content: center; align-items: center; gap: 8px;">
            <c:set var="baseUrl" value="${pageContext.request.contextPath}/lab-queue" />
            <c:set var="queryParams" value="" />
            <c:if test="${not empty filterStatus}">
              <c:set var="queryParams" value="${queryParams}status=${filterStatus}&" />
            </c:if>
            <c:if test="${not empty filterDepartment}">
              <c:set var="queryParams" value="${queryParams}department=${filterDepartment}&" />
            </c:if>
            <c:if test="${not empty searchTerm}">
              <c:set var="queryParams" value="${queryParams}search=${searchTerm}&" />
            </c:if>
            
            <!-- Nút Previous -->
            <c:if test="${currentPage > 1}">
              <a href="${baseUrl}?${queryParams}page=${currentPage - 1}" class="btn btn-outline" style="text-decoration: none;">
                ‹ Trước
              </a>
            </c:if>
            <c:if test="${currentPage <= 1}">
              <span class="btn btn-outline" style="opacity: 0.5; cursor: not-allowed;">‹ Trước</span>
            </c:if>
            
            <!-- Số trang -->
            <c:choose>
              <c:when test="${totalPages <= 7}">
                <!-- Hiển thị tất cả nếu <= 7 trang -->
                <c:forEach var="i" begin="1" end="${totalPages}">
                  <c:choose>
                    <c:when test="${i == currentPage}">
                      <span class="btn btn-primary" style="min-width: 36px;">${i}</span>
                    </c:when>
                    <c:otherwise>
                      <a href="${baseUrl}?${queryParams}page=${i}" class="btn btn-outline" style="text-decoration: none; min-width: 36px;">${i}</a>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </c:when>
              <c:otherwise>
                <!-- Hiển thị thông minh nếu > 7 trang -->
                <!-- Trang đầu -->
                <c:if test="${currentPage > 3}">
                  <a href="${baseUrl}?${queryParams}page=1" class="btn btn-outline" style="text-decoration: none; min-width: 36px;">1</a>
                  <c:if test="${currentPage > 4}">
                    <span style="padding: 8px 4px;">...</span>
                  </c:if>
                </c:if>
                
                <!-- Các trang xung quanh trang hiện tại -->
                <c:forEach var="i" begin="${currentPage > 3 ? currentPage - 1 : 1}" end="${currentPage < totalPages - 2 ? currentPage + 1 : totalPages}">
                  <c:choose>
                    <c:when test="${i == currentPage}">
                      <span class="btn btn-primary" style="min-width: 36px;">${i}</span>
                    </c:when>
                    <c:otherwise>
                      <a href="${baseUrl}?${queryParams}page=${i}" class="btn btn-outline" style="text-decoration: none; min-width: 36px;">${i}</a>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
                
                <!-- Trang cuối -->
                <c:if test="${currentPage < totalPages - 2}">
                  <c:if test="${currentPage < totalPages - 3}">
                    <span style="padding: 8px 4px;">...</span>
                  </c:if>
                  <a href="${baseUrl}?${queryParams}page=${totalPages}" class="btn btn-outline" style="text-decoration: none; min-width: 36px;">${totalPages}</a>
                </c:if>
              </c:otherwise>
            </c:choose>
            
            <!-- Nút Next -->
            <c:if test="${currentPage < totalPages}">
              <a href="${baseUrl}?${queryParams}page=${currentPage + 1}" class="btn btn-outline" style="text-decoration: none;">
                Sau ›
              </a>
            </c:if>
            <c:if test="${currentPage >= totalPages}">
              <span class="btn btn-outline" style="opacity: 0.5; cursor: not-allowed;">Sau ›</span>
            </c:if>
          </div>
        </c:if>
    </div>
  </div>

  <script>
    // Filter and search handlers

    // Update status to processing
    function updateStatusToProcessing(requestId) {
      if (!confirm('Bạn có chắc chắn muốn bắt đầu xét nghiệm cho phiếu này?')) {
        return;
      }

      fetch('${pageContext.request.contextPath}/lab-queue', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'action=updateStatus&requestId=' + requestId + '&status=processing'
      })
      .then(response => response.json())
      .then(data => {
        if (data.success) {
          alert('Đã cập nhật trạng thái thành công!');
          location.reload();
        } else {
          alert('Cập nhật thất bại: ' + (data.message || 'Lỗi không xác định'));
        }
      })
      .catch(error => {
        console.error('Error:', error);
        alert('Đã xảy ra lỗi khi cập nhật trạng thái');
      });
    }

    // Utility functions
    function formatDate(dateString) {
      if (!dateString) return '-';
      const date = new Date(dateString);
      return date.toLocaleDateString('vi-VN', { 
        day: '2-digit', 
        month: '2-digit', 
        year: 'numeric' 
      });
    }

    function formatTime(dateString) {
      if (!dateString) return '-';
      const date = new Date(dateString);
      return date.toLocaleTimeString('vi-VN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      });
    }

    function calculateAge(dob) {
      if (!dob) return '-';
      const birthDate = new Date(dob);
      const today = new Date();
      let age = today.getFullYear() - birthDate.getFullYear();
      const monthDiff = today.getMonth() - birthDate.getMonth();
      if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
        age--;
      }
      return age;
    }

    function getStatusText(status) {
      const statusMap = {
        'pending': 'Chờ lấy mẫu',
        'processing': 'Đang xét nghiệm',
        'completed': 'Đã có kết quả'
      };
      return statusMap[status] || status;
    }

    function getStatusClass(status) {
      const classMap = {
        'pending': 'status-pending',
        'processing': 'status-inprogress',
        'completed': 'status-done'
      };
      return classMap[status] || 'status-pending';
    }

    function getGenderText(gender) {
      const genderMap = {
        'male': 'Nam',
        'female': 'Nữ',
        'other': 'Khác'
      };
      return genderMap[gender] || '-';
    }

    // Select a request and show details
    function selectRequest(requestId) {
      selectedRequest = labRequestsData.find(r => r.requestId === requestId);
      
      if (!selectedRequest) return;

      // Highlight selected row
      document.querySelectorAll('.queue-row').forEach(row => {
        row.style.background = '';
      });
      const selectedRow = document.querySelector('[data-request-id="' + requestId + '"]');
      if (selectedRow) {
        selectedRow.style.background = '#e0edff';
      }

      // Update detail panel
      updateDetailPanel();
    }

    // Update detail panel
    function updateDetailPanel() {
      if (!selectedRequest) return;

      const createdAtDate = new Date(selectedRequest.createdAt);
      const year = createdAtDate.getFullYear();
      const requestCode = 'LAB-' + year + '-' + selectedRequest.requestId.toString().padStart(4, '0');
      const patientCode = 'BN' + selectedRequest.patient.patientId.toString().padStart(6, '0');
      const age = calculateAge(selectedRequest.patient.dob);
      const gender = getGenderText(selectedRequest.patient.gender);
      const dateTime = formatTime(selectedRequest.createdAt) + ' - ' + formatDate(selectedRequest.createdAt);
      const statusText = getStatusText(selectedRequest.status);
      const statusClass = getStatusClass(selectedRequest.status);
      const symptom = selectedRequest.appointment.symptom || '-';

      // Update request ID
      document.getElementById('detailRequestId').textContent = requestCode;
      document.getElementById('requestIdInput').value = selectedRequest.requestId;

      // Update patient info
      const patientPhone = selectedRequest.patient.phone || '-';
      document.getElementById('patientInfoSection').innerHTML = 
        '<div class="detail-row">' +
          '<div>' +
            '<div class="detail-label">Họ tên</div>' +
            '<div class="detail-value" style="text-align:left;">' + selectedRequest.patient.fullName + '</div>' +
          '</div>' +
          '<div>' +
            '<div class="detail-label">Mã BN</div>' +
            '<div class="detail-value">' + patientCode + '</div>' +
          '</div>' +
        '</div>' +
        '<div class="detail-row">' +
          '<div>' +
            '<div class="detail-label">Tuổi / Giới tính</div>' +
            '<div class="detail-value" style="text-align:left;">' + age + ' tuổi, ' + gender + '</div>' +
          '</div>' +
          '<div>' +
            '<div class="detail-label">Số điện thoại</div>' +
            '<div class="detail-value">' + patientPhone + '</div>' +
          '</div>' +
        '</div>' +
        '<div class="detail-row">' +
          '<div>' +
            '<div class="detail-label">Khoa gửi</div>' +
            '<div class="detail-value" style="text-align:left;">' + selectedRequest.doctor.specialization + '</div>' +
          '</div>' +
          '<div>' +
            '<div class="detail-label">Thời gian chỉ định</div>' +
            '<div class="detail-value">' + dateTime + '</div>' +
          '</div>' +
        '</div>';

      // Update lab test info
      document.getElementById('labTestSection').innerHTML = 
        '<div class="detail-row">' +
          '<div style="width: 100%;">' +
            '<div class="detail-label">Triệu chứng / Chỉ định xét nghiệm</div>' +
            '<div class="detail-value" style="text-align:left; margin-top: 4px;">' +
              symptom +
            '</div>' +
          '</div>' +
        '</div>' +
        '<div class="detail-row" style="margin-top:10px;">' +
          '<div>' +
            '<div class="detail-label">Trạng thái hiện tại</div>' +
            '<div class="status-pill ' + statusClass + '">' +
              '● ' + statusText +
            '</div>' +
          '</div>' +
          '<div>' +
            '<div class="detail-label">Dự kiến hoàn thành</div>' +
            '<div class="detail-value">' + getExpectedCompletionTime(selectedRequest) + '</div>' +
          '</div>' +
        '</div>';

      // Update notes
      document.getElementById('noteTextarea').value = selectedRequest.notes || '';
      document.getElementById('noteTextarea').disabled = false;
      document.getElementById('markSampleBtn').disabled = selectedRequest.status !== 'pending';
      document.getElementById('updateStatusBtn').disabled = false;
      
      const sendResultLink = document.getElementById('sendResultLink');
      if (selectedRequest.status === 'processing') {
        sendResultLink.href = '${pageContext.request.contextPath}/lab-queue?action=viewSendResult&requestId=' + selectedRequest.requestId;
        sendResultLink.style.pointerEvents = 'auto';
        sendResultLink.style.opacity = '1';
      } else {
        sendResultLink.href = '#';
        sendResultLink.style.pointerEvents = 'none';
        sendResultLink.style.opacity = '0.5';
      }
    }

    function getExpectedCompletionTime(request) {
      const createdDate = new Date(request.createdAt);
      const expectedTime = new Date(createdDate);
      expectedTime.setHours(11, 0, 0, 0);
      
      if (request.status === 'completed') {
        return 'Đã hoàn thành';
      }
      
      return 'Trước ' + formatTime(expectedTime.toISOString()) + ' hôm nay';
    }

    // Filter change handlers
    document.getElementById('filterStatus').addEventListener('change', () => {
      document.getElementById('filterForm').submit();
    });
    
    document.getElementById('filterDepartment').addEventListener('change', () => {
      document.getElementById('filterForm').submit();
    });
    
    // Search handlers
    const searchInput = document.getElementById('searchInput');
    searchInput.addEventListener('keypress', (e) => {
      if (e.key === 'Enter') {
        document.getElementById('filterForm').submit();
      }
    });
    
    // Auto-submit when search is cleared
    searchInput.addEventListener('input', (e) => {
      if (!e.target.value.trim()) {
        document.getElementById('filterForm').submit();
      }
    });

    // Clear all filters button
    document.getElementById('clearFiltersBtn').addEventListener('click', () => {
      // Reset all filter fields
      document.getElementById('filterStatus').value = '';
      document.getElementById('filterDepartment').value = '';
      document.getElementById('searchInput').value = '';
      
      // Redirect to base URL without parameters
      window.location.href = '${pageContext.request.contextPath}/lab-queue';
    });
  </script>
  
  <jsp:include page="../../common/footer.jsp" />
</body>
</html>
