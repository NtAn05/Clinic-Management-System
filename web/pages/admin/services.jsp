<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý Giá Dịch vụ</title>
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
                max-width: 1200px;
                margin: 0 auto;
            }

            .alert {
                padding: 15px 20px;
                border-radius: 8px;
                margin-bottom: 20px;
                display: flex;
                align-items: center;
                gap: 10px;
                animation: slideIn 0.3s ease-out;
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

            @keyframes slideIn {
                from {
                    transform: translateY(-20px);
                    opacity: 0;
                }
                to {
                    transform: translateY(0);
                    opacity: 1;
                }
            }

            .table-container {
                background: white;
                padding: 25px;
                border-radius: 10px;
                box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
                overflow-x: auto;
            }

            .table-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 20px;
                border-bottom: 2px solid #f0f0f0;
                padding-bottom: 15px;
            }

            .table-header h3 {
                font-size: 18px;
                color: #333;
            }

            .btn-submit {
                background: #4caf50;
                color: white;
                border: none;
                padding: 10px 20px;
                border-radius: 6px;
                cursor: pointer;
                font-size: 14px;
                font-weight: 600;
                transition: all 0.3s ease;
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .btn-submit:hover {
                background: #45a049;
                transform: translateY(-2px);
                box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
            }

            .add-form {
                background: #f9f9f9;
                padding: 20px;
                border-radius: 8px;
                margin-bottom: 20px;
            }

            .form-grid {
                display: grid;
                grid-template-columns: 1fr 1fr 1fr auto;
                gap: 10px;
                align-items: end;
            }

            .form-group {
                display: flex;
                flex-direction: column;
            }

            .form-group label {
                font-weight: 600;
                margin-bottom: 5px;
                font-size: 13px;
                color: #333;
            }

            .form-group input {
                padding: 8px;
                border: 1px solid #ddd;
                border-radius: 4px;
                font-family: inherit;
                font-size: 13px;
            }

            .form-group input:focus {
                outline: none;
                border-color: #0061ff;
                box-shadow: 0 0 0 3px rgba(0, 97, 255, 0.1);
            }

            /* Search & Filter Styles */
            .search-filter-section {
                background: #f8f9fa;
                padding: 20px;
                border-radius: 8px;
                margin-bottom: 20px;
                border: 1px solid #e0e0e0;
            }

            .search-form {
                margin: 0;
            }

            .search-grid {
                display: grid;
                grid-template-columns: repeat(4, minmax(180px, 1fr)) auto;
                gap: 12px;
                align-items: end;
            }

            .search-group {
                display: flex;
                flex-direction: column;
                min-width: 0;
            }

            .search-group label {
                font-weight: 600;
                margin-bottom: 5px;
                font-size: 13px;
                color: #333;
                display: flex;
                align-items: center;
                gap: 5px;
            }

            .filter-box {
                min-width: 0;
            }

            .filter-box label {
                display: block;
                font-weight: 600;
                margin-bottom: 8px;
                color: #333;
                font-size: 13px;
            }

            .filter-box select {
                width: 100%;
                padding: 10px 15px;
                border: 1px solid #ddd;
                border-radius: 6px;
                font-size: 14px;
                cursor: pointer;
                background: white;
                transition: all 0.3s ease;
            }

            .filter-box select:focus {
                outline: none;
                border-color: #0061ff;
                box-shadow: 0 0 0 3px rgba(0, 97, 255, 0.1);
            }

            .search-input, .filter-select, .price-input {
                padding: 10px 15px;
                border: 1px solid #ddd;
                border-radius: 6px;
                font-family: inherit;
                font-size: 14px;
                width: 100%;
            }

            .search-input:focus, .filter-select:focus, .price-input:focus {
                outline: none;
                border-color: #0061ff;
                box-shadow: 0 0 0 2px rgba(0, 97, 255, 0.1);
            }

            .button-group {
                display: flex;
                gap: 8px;
                align-self: end;
            }

            .btn-filter {
                background: #0061ff;
                color: white;
                border: none;
                padding: 10px 16px;
                border-radius: 6px;
                cursor: pointer;
                font-size: 14px;
                font-weight: 600;
                display: flex;
                align-items: center;
                gap: 5px;
                transition: all 0.3s ease;
            }

            .btn-filter:hover {
                background: #0052cc;
                transform: translateY(-1px);
            }

            .btn-clear {
                color: #666;
                text-decoration: none;
                padding: 10px 16px;
                border-radius: 6px;
                font-size: 14px;
                font-weight: 700;
                display: flex;
                align-items: center;
                gap: 5px;
                transition: all 0.3s ease;
                border: 1px solid #ddd;
            }

            .btn-clear:hover {
                background: #f0f0f0;
                color: #333;
            }

            .pagination-wrapper {
                margin-top: 16px;
                display: flex;
                justify-content: center;
                align-items: center;
                gap: 8px;
                flex-wrap: wrap;
            }

            .page-link {
                min-width: 36px;
                padding: 10px 14px;
                border: 1px solid #d9d9d9;
                border-radius: 10px;
                background: #fff;
                color: #333;
                text-decoration: none;
                font-weight: 500;
                text-align: center;
                display: inline-flex;
                align-items: center;
                justify-content: center;
            }

            .page-link:hover {
                background: #f5f5f5;
            }

            .page-link.active {
                background: #0061ff;
                color: #fff;
                border-color: #0061ff;
                pointer-events: none;
            }

            .page-link.disabled {
                opacity: .5;
                cursor: not-allowed;
                pointer-events: none;
            }

            @media (max-width: 768px) {
                .search-grid {
                    grid-template-columns: 1fr;
                    gap: 10px;
                }
                
                .button-group {
                    justify-content: stretch;
                    width: 100%;
                }

                .button-group .btn-filter,
                .button-group .btn-clear {
                    flex: 1;
                    justify-content: center;
                }
            }

            table {
                width: 100%;
                border-collapse: collapse;
            }

            th {
                background: linear-gradient(135deg, #f8f9fa 0%, #f0f0f0 100%);
                padding: 15px;
                text-align: left;
                font-weight: 600;
                color: #333;
                border-bottom: 2px solid #e0e0e0;
            }

            td {
                padding: 15px;
                border-bottom: 1px solid #f0f0f0;
                color: #555;
            }

            tr:hover {
                background: #f9f9f9;
            }

            .action-buttons {
                display: flex;
                gap: 8px;
            }

            .btn-edit, .btn-delete {
                border: none;
                background: none;
                cursor: pointer;
                font-size: 16px;
                padding: 6px 10px;
                border-radius: 4px;
                transition: all 0.3s ease;
            }

            .btn-edit {
                color: #1976d2;
            }

            .btn-edit:hover {
                background: #e3f2fd;
            }

            .btn-delete {
                color: #d32f2f;
            }

            .btn-delete:hover {
                background: #ffebee;
            }

            .no-data {
                text-align: center;
                padding: 30px;
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
            }

            @keyframes fadeIn {
                from {
                    opacity: 0;
                }
                to {
                    opacity: 1;
                }
            }

            .modal-content {
                background-color: white;
                margin: 5% auto;
                padding: 30px;
                border-radius: 10px;
                width: 90%;
                max-width: 500px;
                box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
                animation: slideUp 0.3s ease;
            }

            @keyframes slideUp {
                from {
                    transform: translateY(50px);
                    opacity: 0;
                }
                to {
                    transform: translateY(0);
                    opacity: 1;
                }
            }

            .modal-header {
                font-size: 20px;
                font-weight: 600;
                color: #0061ff;
                margin-bottom: 20px;
                display: flex;
                align-items: center;
                gap: 10px;
            }

            .form-group-modal {
                margin-bottom: 15px;
            }

            .form-group-modal label {
                display: block;
                font-weight: 600;
                margin-bottom: 8px;
                color: #333;
            }

            .form-group-modal input {
                width: 100%;
                padding: 10px;
                border: 1px solid #ddd;
                border-radius: 6px;
                font-size: 14px;
                font-family: inherit;
            }

            .form-group-modal input:focus {
                outline: none;
                border-color: #0061ff;
                box-shadow: 0 0 0 3px rgba(0, 97, 255, 0.1);
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
            }

            .btn-cancel:hover {
                background: #e0e0e0;
            }

            .btn-submit-modal {
                padding: 10px 20px;
                background: #0061ff;
                color: white;
                border: none;
                border-radius: 6px;
                cursor: pointer;
                font-weight: 600;
                transition: all 0.3s ease;
            }

            .btn-submit-modal:hover {
                background: #0052cc;
            }
        </style>
    </head>
    <body>
        <jsp:include page="/common/header.jsp" />

        <div class="container">
            <!-- Thông báo thành công -->
            <c:if test="${not empty success}">
                <div class="alert success">
                    <i class="fas fa-check-circle"></i>
                    ${success}
                </div>
            </c:if>

            <!-- Thông báo lỗi -->
            <c:if test="${not empty error}">
                <div class="alert error">
                    <i class="fas fa-exclamation-circle"></i>
                    ${error}
                </div>
            </c:if>

            <div class="table-container">
                <div class="table-header">
                    <h3><i class="fas fa-list"></i> Danh sách Dịch vụ</h3>
                    <button class="btn-submit" onclick="openAddModal()">
                        <i class="fas fa-plus"></i> Thêm Dịch vụ
                    </button>
                </div>

                <!-- Search & Filter -->
                <div class="search-filter-section">
                    <form method="GET" action="${pageContext.request.contextPath}/admin-services" class="search-form">
                        <div class="search-grid">
                            <div class="search-group">
                                <label><i class="fas fa-search"></i> Tìm kiếm</label>
                                <input type="text" name="search" value="${searchKeyword}" placeholder="Tên dịch vụ..." class="search-input" onkeypress="if(event.keyCode==13) this.form.submit()">
                            </div>
                            <div class="filter-box">
                                <label><i class="fas fa-filter"></i> Danh mục</label>
                                <select name="category" class="filter-select" onchange="this.form.submit()">
                                    <option value="all" ${filterCategory == 'all' ? 'selected' : ''}>-- Tất cả --</option>
                                    <option value="booking_fee" ${filterCategory == 'booking_fee' ? 'selected' : ''}>Khám & tư vấn</option>
                                    <option value="lab" ${filterCategory == 'lab' ? 'selected' : ''}>Kiểm tra chuyên sâu</option>
                                </select>
                            </div>
                            <div class="search-group">
                                <label><i class="fas fa-dollar-sign"></i> Giá từ</label>
                                <input type="number" name="minPrice" value="${minPriceValue}" min="0" placeholder="0" class="price-input" onchange="this.form.submit()">
                            </div>
                            <div class="search-group">
                                <label><i class="fas fa-dollar-sign"></i> Giá đến</label>
                                <input type="number" name="maxPrice" value="${maxPriceValue}" min="0" placeholder="Không giới hạn" class="price-input" onchange="this.form.submit()">
                            </div>
                            <input type="hidden" name="page" value="1">
                            <div class="search-group">
                                <label>&nbsp;</label>
                                <div class="button-group">
                                    <button type="submit" class="btn-filter">
                                        <i class="fas fa-search"></i> Tìm
                                    </button>
                                    <a href="${pageContext.request.contextPath}/admin-services" class="btn-clear">
                                        <i class="fas fa-redo"></i> Đặt lại
                                    </a>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>

                <!-- Danh sách dịch vụ -->
                <c:set var="pageSize" value="${not empty requestScope.pageSize ? requestScope.pageSize : 10}" />
                <c:set var="currentPage" value="${not empty requestScope.currentPage ? requestScope.currentPage : (empty param.page ? 1 : param.page)}" />
                <c:set var="totalRecords" value="${not empty requestScope.totalRecords ? requestScope.totalRecords : (not empty services ? fn:length(services) : 0)}" />
                <c:set var="totalPages" value="${not empty requestScope.totalPages ? requestScope.totalPages : ((totalRecords + pageSize - 1) div pageSize)}" />
                <c:set var="isServerPaged" value="${not empty requestScope.totalPages}" />
                <c:if test="${totalPages == 0}">
                    <c:set var="currentPage" value="1" />
                </c:if>
                <c:if test="${totalPages > 0 and currentPage > totalPages}">
                    <c:set var="currentPage" value="${totalPages}" />
                </c:if>
                <c:choose>
                    <c:when test="${not empty services}">
                        <table>
                            <thead>
                                <tr>
                                    <th style="width: 40%;">Tên Dịch vụ</th>
                                    <th style="width: 30%;">Danh mục</th>
                                    <th style="width: 20%;">Giá (VNĐ)</th>
                                    <th style="width: 10%;">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="service" items="${not empty servicesPaged ? servicesPaged : services}" varStatus="st">
                                    <c:if test="${not empty servicesPaged or (st.index >= (currentPage - 1) * pageSize and st.index < currentPage * pageSize)}">
                                        <c:set var="displayType" value="${service.serviceType}"/>
                                        <c:if test="${service.serviceType eq 'booking_fee'}">
                                            <c:set var="displayType" value="Khám & tư vấn"/>
                                        </c:if>
                                        <c:if test="${service.serviceType eq 'lab'}">
                                            <c:set var="displayType" value="Kiểm tra chuyên sâu"/>
                                        </c:if>
                                        <tr>
                                            <td><strong>${service.name}</strong></td>
                                            <td>${displayType}</td>
                                            <td>${service.price}</td>
                                            <td>
                                                <div class="action-buttons">
                                                    <button class="btn-edit" onclick="openEditModal(${service.serviceId}, &quot;${service.name}&quot;, &quot;${service.serviceType}&quot;, &quot;${service.price}&quot;)" title="Chỉnh sửa">
                                                        <i class="fas fa-edit"></i>
                                                    </button>
                                                    <form method="POST" action="${pageContext.request.contextPath}/admin-services" style="display: inline;" onsubmit="return confirm('Bạn chắc chắn muốn xóa dịch vụ này?');">
                                                        <input type="hidden" name="action" value="delete">
                                                        <input type="hidden" name="serviceId" value="${service.serviceId}">
                                                        <input type="hidden" name="filterSearch" value="${not empty searchKeyword ? searchKeyword : param.search}">
                                                        <input type="hidden" name="filterCategory" value="${not empty filterCategory ? filterCategory : param.category}">
                                                        <input type="hidden" name="filterMinPrice" value="${not empty minPriceValue ? minPriceValue : param.minPrice}">
                                                        <input type="hidden" name="filterMaxPrice" value="${not empty maxPriceValue ? maxPriceValue : param.maxPrice}">
                                                        <input type="hidden" name="filterPage" value="${currentPage}">
                                                        <button type="submit" class="btn-delete" title="Xóa">
                                                            <i class="fas fa-trash"></i>
                                                        </button>
                                                    </form>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:if>
                                </c:forEach>
                            </tbody>
                        </table>

                        <c:if test="${totalPages > 1}">
                            <div class="pagination-wrapper">
                                <c:url var="prevUrl" value="/admin-services">
                                    <c:param name="search" value="${not empty searchKeyword ? searchKeyword : param.search}" />
                                    <c:param name="category" value="${not empty filterCategory ? filterCategory : param.category}" />
                                    <c:param name="minPrice" value="${not empty minPriceValue ? minPriceValue : param.minPrice}" />
                                    <c:param name="maxPrice" value="${not empty maxPriceValue ? maxPriceValue : param.maxPrice}" />
                                    <c:param name="page" value="${currentPage - 1}" />
                                </c:url>
                                <c:url var="nextUrl" value="/admin-services">
                                    <c:param name="search" value="${not empty searchKeyword ? searchKeyword : param.search}" />
                                    <c:param name="category" value="${not empty filterCategory ? filterCategory : param.category}" />
                                    <c:param name="minPrice" value="${not empty minPriceValue ? minPriceValue : param.minPrice}" />
                                    <c:param name="maxPrice" value="${not empty maxPriceValue ? maxPriceValue : param.maxPrice}" />
                                    <c:param name="page" value="${currentPage + 1}" />
                                </c:url>

                                <c:choose>
                                    <c:when test="${currentPage > 1}">
                                        <a class="page-link" href="${prevUrl}">‹ Trước</a>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="page-link disabled">‹ Trước</span>
                                    </c:otherwise>
                                </c:choose>

                                <c:forEach var="i" begin="1" end="${totalPages}">
                                    <c:url var="pageUrl" value="/admin-services">
                                        <c:param name="search" value="${not empty searchKeyword ? searchKeyword : param.search}" />
                                        <c:param name="category" value="${not empty filterCategory ? filterCategory : param.category}" />
                                        <c:param name="minPrice" value="${not empty minPriceValue ? minPriceValue : param.minPrice}" />
                                        <c:param name="maxPrice" value="${not empty maxPriceValue ? maxPriceValue : param.maxPrice}" />
                                        <c:param name="page" value="${i}" />
                                    </c:url>
                                    <c:choose>
                                        <c:when test="${i == currentPage}">
                                            <span class="page-link active">${i}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <a class="page-link" href="${pageUrl}">${i}</a>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>

                                <c:choose>
                                    <c:when test="${currentPage < totalPages}">
                                        <a class="page-link" href="${nextUrl}">Sau ›</a>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="page-link disabled">Sau ›</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <div class="no-data">
                            <i class="fas fa-box"></i>
                            <p>Chưa có dịch vụ nào</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- MODAL THÊM DỊCH VỤ -->
        <div id="addModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <i class="fas fa-plus-circle"></i>
                    <span>Thêm Dịch vụ Mới</span>
                </div>

                <form method="POST" action="${pageContext.request.contextPath}/admin-services">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="filterSearch" value="${searchKeyword}">
                    <input type="hidden" name="filterCategory" value="${filterCategory}">
                    <input type="hidden" name="filterMinPrice" value="${minPriceValue}">
                    <input type="hidden" name="filterMaxPrice" value="${maxPriceValue}">
                    <input type="hidden" name="filterPage" value="${currentPage}">

                    <div class="form-group-modal">
                        <label>Tên dịch vụ <span style="color: red;">*</span></label>
                        <input type="text" name="name" required placeholder="Nhập tên dịch vụ">
                    </div>

                    <div class="form-group-modal">
                        <label>Danh mục <span style="color: red;">*</span></label>
                        <select name="serviceType" class="filter-select" required>
                            <option value="">-- Chọn danh mục --</option>
                            <option value="booking_fee">Khám & tư vấn</option>
                            <option value="lab">Kiểm tra chuyên sâu</option>
                        </select>
                    </div>

                    <div class="form-group-modal">
                        <label>Giá (VNĐ) <span style="color: red;">*</span></label>
                        <input type="number" name="price" min="0" required placeholder="0">
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn-cancel" onclick="closeAddModal()">
                            <i class="fas fa-times"></i> Hủy
                        </button>
                        <button type="submit" class="btn-submit-modal">
                            <i class="fas fa-save"></i> Thêm dịch vụ
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- MODAL CHỈNH SỬA DỊCH VỤ -->
        <div id="editModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <i class="fas fa-edit"></i>
                    <span>Chỉnh sửa Dịch vụ</span>
                </div>

                <form method="POST" action="${pageContext.request.contextPath}/admin-services">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="serviceId" id="editServiceId">
                    <input type="hidden" name="filterSearch" value="${searchKeyword}">
                    <input type="hidden" name="filterCategory" value="${filterCategory}">
                    <input type="hidden" name="filterMinPrice" value="${minPriceValue}">
                    <input type="hidden" name="filterMaxPrice" value="${maxPriceValue}">
                    <input type="hidden" name="filterPage" value="${currentPage}">

                    <div class="form-group-modal">
                        <label>Tên dịch vụ <span style="color: red;">*</span></label>
                        <input type="text" name="name" id="editServiceName" required placeholder="Nhập tên dịch vụ">
                    </div>

                    <div class="form-group-modal">
                        <label>Danh mục <span style="color: red;">*</span></label>
                        <select name="serviceType" id="editServiceType" class="filter-select" required>
                            <option value="">-- Chọn danh mục --</option>
                            <option value="booking_fee">Khám & tư vấn</option>
                            <option value="lab">Kiểm tra chuyên sâu</option>
                        </select>
                    </div>

                    <div class="form-group-modal">
                        <label>Giá (VNĐ) <span style="color: red;">*</span></label>
                        <input type="number" name="price" id="editServicePrice" min="0" required placeholder="0">
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn-cancel" onclick="closeEditModal()">
                            <i class="fas fa-times"></i> Hủy
                        </button>
                        <button type="submit" class="btn-submit-modal">
                            <i class="fas fa-save"></i> Lưu thay đổi
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            // Mở modal thêm
            function openAddModal() {
                document.getElementById('addModal').style.display = 'block';
            }

            // Đóng modal thêm
            function closeAddModal() {
                document.getElementById('addModal').style.display = 'none';
            }

            // Mở modal chỉnh sửa
            function openEditModal(serviceId, name, serviceType, price) {
                document.getElementById('editServiceId').value = serviceId;
                document.getElementById('editServiceName').value = name;
                document.getElementById('editServiceType').value = serviceType;
                document.getElementById('editServicePrice').value = price;
                document.getElementById('editModal').style.display = 'block';
            }

            // Đóng modal chỉnh sửa
            function closeEditModal() {
                document.getElementById('editModal').style.display = 'none';
            }

            // Đóng modal khi click bên ngoài
            window.onclick = function(event) {
                const addModal = document.getElementById('addModal');
                const editModal = document.getElementById('editModal');
                if (event.target === addModal) {
                    addModal.style.display = 'none';
                }
                if (event.target === editModal) {
                    editModal.style.display = 'none';
                }
            }

            // Tự động đóng thông báo sau 5 giây
            document.addEventListener('DOMContentLoaded', function() {
                const alerts = document.querySelectorAll('.alert');
                alerts.forEach(alert => {
                    setTimeout(() => {
                        alert.style.animation = 'slideIn 0.3s ease-out reverse';
                        setTimeout(() => alert.remove(), 300);
                    }, 5000);
                });
            });
        </script>
    </body>
</html>
