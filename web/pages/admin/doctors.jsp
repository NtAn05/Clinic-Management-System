<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý bác sĩ</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background: linear-gradient(135deg,#f5f7fa 0%,#c3cfe2 100%); min-height: 100vh; }
        .container { padding: 30px 50px; max-width: 1400px; margin: 0 auto; }
        .alert { padding: 15px 20px; border-radius: 8px; margin-bottom: 20px; display: flex; align-items: center; gap: 10px; }
        .alert.success { background:#e8f5e9; color:#2e7d32; border-left:4px solid #4caf50; }
        .alert.error { background:#ffebee; color:#c62828; border-left:4px solid #f44336; }
        .table-container { background: #fff; padding: 25px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,.1); overflow-x: auto; }
        .table-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:20px; }
        .table-header h3 { font-size:18px; color:#333; }
        .toolbar { background: #fff; padding: 20px; border-radius: 10px; margin-bottom: 20px; display:grid; grid-template-columns:minmax(340px,1.9fr) minmax(220px,1fr) minmax(220px,1fr) auto; gap: 12px; align-items:end; box-shadow: 0 2px 10px rgba(0,0,0,.1); }
        .search-box label, .filter-box label { display:block; font-weight:600; margin-bottom:8px; color:#333; font-size:13px; }
        .search-box input, .filter-box select { width:100%; padding:10px 15px; border:1px solid #ddd; border-radius:6px; font-size:14px; }
        .toolbar-buttons { display:flex; gap:10px; }
        .btn-search,.btn-reset,.btn-add { padding:10px 16px; border:none; border-radius:6px; cursor:pointer; font-weight:600; font-size:14px; display:flex; align-items:center; gap:6px; text-decoration:none; }
        .btn-search { background:#0061ff; color:#fff; }
        .btn-reset { background:#f0f0f0; color:#333; }
        .btn-add { background:#4caf50; color:#fff; }
        table { width:100%; border-collapse:collapse; }
        th { background: linear-gradient(135deg,#f8f9fa 0%,#f0f0f0 100%); padding:15px; text-align:left; font-weight:600; color:#333; border-bottom:2px solid #e0e0e0; }
        td { padding:15px; border-bottom:1px solid #f0f0f0; color:#555; }
        tr:hover { background:#f9f9f9; }
        .doctor-meta { font-size:12px; color:#6b7280; margin-top:4px; }
        .action-buttons { display:flex; gap:8px; flex-wrap:nowrap; align-items:center; white-space:nowrap; }
        .btn-action { border:none; background:none; cursor:pointer; font-size:14px; padding:6px 10px; border-radius:4px; display:inline-flex; align-items:center; gap:0px; text-decoration:none; }
        .btn-view { color:#FB923C; }
        .btn-calendar { color:#5b21b6; }
        .btn-view:hover { background:#FFEDD5; }
        .btn-calendar:hover { background:#f3e8ff; }
        .nowrap { white-space: nowrap; }
        .no-data { text-align:center; padding:30px; color:#999; }
        .modal { display:none; position:fixed; z-index:1000; left:0; top:0; width:100%; height:100%; background:rgba(0,0,0,.5); overflow-y:auto; }
        .modal-content { background:#fff; margin:5% auto; padding:30px; border-radius:10px; width:90%; max-width:650px; box-shadow:0 10px 40px rgba(0,0,0,.3); }
        .modal-header { font-size:20px; font-weight:600; color:#0061ff; margin-bottom:20px; display:flex; align-items:center; gap:10px; border-bottom:2px solid #f0f0f0; padding-bottom:14px; }
        .modal-close { margin-left:auto; cursor:pointer; font-size:24px; border:none; background:none; color:#999; }
        .form-grid { display:grid; grid-template-columns: repeat(2, minmax(0,1fr)); gap:14px; }
        .form-full { grid-column:1/-1; }
        .form-group label { display:block; font-weight:600; margin-bottom:8px; color:#333; font-size:14px; }
        .form-group input,.form-group select { width:100%; padding:10px 15px; border:1px solid #ddd; border-radius:6px; font-size:14px; }
        .field-error { margin-top:6px; color:#dc3545; font-size:13px; font-weight:600; }
        .modal-footer { display:flex; gap:10px; justify-content:flex-end; margin-top:24px; padding-top:15px; border-top:1px solid #f0f0f0; }
        .btn-cancel,.btn-submit { padding:10px 20px; border:none; border-radius:6px; cursor:pointer; font-weight:600; display:flex; align-items:center; gap:6px; }
        .btn-cancel { background:#f0f0f0; color:#333; }
        .btn-submit { background:#0061ff; color:#fff; }
        .locked-readonly { background:#f3f4f6; color:#6b7280; cursor:not-allowed; }
        @media (max-width: 992px){ .container{padding:20px;} .toolbar{grid-template-columns:1fr;} .toolbar-buttons .btn-search,.toolbar-buttons .btn-reset,.toolbar-buttons .btn-add{flex:1; justify-content:center;} .form-grid{grid-template-columns:1fr;} }
    </style>
</head>
<body>
<jsp:include page="/common/header.jsp" />

<div class="container">
    <c:if test="${not empty success}"><div class="alert success"><i class="fas fa-check-circle"></i>${success}</div></c:if>
    <c:if test="${not empty error and not addModalOpen and not editModalOpen}"><div class="alert error"><i class="fas fa-exclamation-circle"></i>${error}</div></c:if>

    <div class="table-container">
        <div class="table-header">
            <h3><i class="fas fa-user-doctor"></i> Danh sách bác sĩ</h3>
            <button class="btn-add" type="button" onclick="openAddModal()"><i class="fas fa-plus"></i> Thêm bác sĩ</button>
        </div>

        <form method="GET" action="${pageContext.request.contextPath}/admin-doctors" class="toolbar">
            <div class="search-box">
                <label><i class="fas fa-search"></i> Tìm kiếm</label>
                <input type="text" name="keyword" value="${keyword}" placeholder="Nhập tên, số điện thoại hoặc email...">
            </div>
            <div class="filter-box">
                <label><i class="fas fa-stethoscope"></i> Chuyên môn</label>
                <select name="specialization" onchange="this.form.submit()">
                    <option value="">-- Tất cả --</option>
                    <c:forEach var="sp" items="${specializationOptions}"><option value="${sp}" ${selectedSpecialization == sp ? 'selected' : ''}>${sp}</option></c:forEach>
                </select>
            </div>
            <div class="filter-box">
                <label><i class="fas fa-graduation-cap"></i> Bằng cấp</label>
                <select name="qualification" onchange="this.form.submit()">
                    <option value="">-- Tất cả --</option>
                    <c:forEach var="q" items="${qualificationOptions}"><option value="${q}" ${selectedQualification == q ? 'selected' : ''}>${q}</option></c:forEach>
                </select>
            </div>
            <div class="toolbar-buttons">
                <button class="btn-search" type="submit"><i class="fas fa-search"></i> Tìm</button>
                <a class="btn-reset" href="${pageContext.request.contextPath}/admin-doctors"><i class="fas fa-redo"></i> Đặt lại</a>
            </div>
        </form>

        <c:choose>
            <c:when test="${not empty doctors}">
                <table>
                    <thead><tr><th>Họ tên</th><th>Chuyên môn</th><th>Bằng cấp</th><th>Kinh nghiệm</th><th>Giá khám</th><th>Đánh giá</th><th style="width:120px;">Thao tác</th></tr></thead>
                    <tbody>
                        <c:forEach var="d" items="${doctors}">
                            <c:url var="doctorScheduleUrl" value="/admin-doctor-schedules"><c:param name="keyword" value="${d.fullName}" /></c:url>
                            <tr>
                                <td><strong>${d.fullName}</strong></td>
                                <td>${d.specialization}</td>
                                <td>${d.qualification}</td>
                                <td>${d.experience_years} năm</td>
                                <td><span class="nowrap"><fmt:formatNumber value="${d.price}" type="number"/> đ</span></td>
                                <td><fmt:formatNumber value="${d.rating}" type="number" minFractionDigits="1" maxFractionDigits="1"/></td>
                                <td>
                                    <div class="action-buttons">
                                        <button type="button" class="btn-action btn-view" title="Xem chi tiết"
                                                data-doctor-id="${d.doctorId}"
                                                data-full-name="${fn:escapeXml(d.fullName)}"
                                                data-phone="${fn:escapeXml(d.phone)}"
                                                data-email="${fn:escapeXml(d.email)}"
                                                data-status="${fn:escapeXml(d.status)}"
                                                data-specialization="${fn:escapeXml(d.specialization)}"
                                                data-qualification="${fn:escapeXml(d.qualification)}"
                                                data-experience="${d.experience_years}"
                                                data-rating="${d.rating}"
                                                data-price-booking="${d.price}"
                                                onclick="openViewModal(this)"><i class="fas fa-eye"></i></button>
                                        <a class="btn-action btn-calendar" title="Xem lịch" href="${doctorScheduleUrl}"><i class="fas fa-calendar-days"></i></a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise><div class="no-data"><i class="fas fa-inbox"></i><p>Chưa có bác sĩ nào</p></div></c:otherwise>
        </c:choose>
    </div>
</div>
<div id="addDoctorModal" class="modal">
    <div class="modal-content">
        <div class="modal-header"><i class="fas fa-user-plus"></i><span>Thêm bác sĩ</span><button class="modal-close" type="button" onclick="closeAddModal()">×</button></div>
        <form method="POST" action="${pageContext.request.contextPath}/admin-doctors">
            <input type="hidden" name="action" value="add">
            <input type="hidden" name="listKeyword" value="${keyword}">
            <input type="hidden" name="listSpecialization" value="${selectedSpecialization}">
            <input type="hidden" name="listQualification" value="${selectedQualification}">

            <c:if test="${not empty error and addModalOpen}"><div class="alert error" style="margin-bottom:12px;"><i class="fas fa-exclamation-circle"></i>${error}</div></c:if>

            <div class="form-grid">
                <div class="form-group form-full"><label>Họ tên <span style="color:red;">*</span></label><input type="text" name="fullName" value="${addFullName}" required><c:if test="${not empty addFullNameError}"><div class="field-error">${addFullNameError}</div></c:if></div>
                <div class="form-group"><label>Số điện thoại <span style="color:red;">*</span></label><input type="tel" name="phone" value="${addPhone}" required><c:if test="${not empty addPhoneError}"><div class="field-error">${addPhoneError}</div></c:if></div>
                <div class="form-group"><label>Email <span style="color:red;">*</span></label><input type="email" name="email" value="${addEmail}" required><c:if test="${not empty addEmailError}"><div class="field-error">${addEmailError}</div></c:if></div>
                <div class="form-group form-full"><label>Mật khẩu <span style="color:red;">*</span></label><input type="text" name="password" required><c:if test="${not empty addPasswordError}"><div class="field-error">${addPasswordError}</div></c:if></div>
                <div class="form-group form-full"><label>Chuyên môn <span style="color:red;">*</span></label><select name="specialization" required><option value="">-- Chọn chuyên môn --</option><c:forEach var="sp" items="${specializationOptions}"><option value="${sp}" ${addSpecialization == sp ? 'selected' : ''}>${sp}</option></c:forEach></select><c:if test="${not empty addSpecializationError}"><div class="field-error">${addSpecializationError}</div></c:if></div>
                <div class="form-group"><label>Bằng cấp <span style="color:red;">*</span></label><select id="addQualification" name="qualification" required onchange="applyDefaultPrice('addQualification','addPriceBooking')"><option value="">-- Chọn bằng cấp --</option><c:forEach var="q" items="${qualificationOptions}"><option value="${q}" ${addQualification == q ? 'selected' : ''}>${q}</option></c:forEach></select><c:if test="${not empty addQualificationError}"><div class="field-error">${addQualificationError}</div></c:if></div>
                <div class="form-group"><label>Kinh nghiệm (năm) <span style="color:red;">*</span></label><input type="number" name="experienceYears" min="0" max="50" value="${addExperience}" required><c:if test="${not empty addExperienceError}"><div class="field-error">${addExperienceError}</div></c:if></div>
                <div class="form-group form-full"><label>Giá khám <span style="color:red;">*</span></label><input id="addPriceBooking" type="number" name="priceBooking" min="0" max="10000000" value="${addPrice}" required><c:if test="${not empty addPriceError}"><div class="field-error">${addPriceError}</div></c:if></div>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn-cancel" onclick="closeAddModal()"><i class="fas fa-times"></i> Hủy</button>
                <button type="submit" class="btn-submit"><i class="fas fa-save"></i> Lưu</button>
            </div>
        </form>
    </div>
</div>

<div id="viewDoctorModal" class="modal">
    <div class="modal-content">
        <div class="modal-header"><i class="fas fa-circle-info"></i><span>Chi tiết bác sĩ</span><button class="modal-close" type="button" onclick="closeViewModal()">×</button></div>
        <form id="viewDoctorForm" method="POST" action="${pageContext.request.contextPath}/admin-doctors" onsubmit="return handleViewFormSubmit()">
            <input type="hidden" name="action" value="edit">
            <input type="hidden" name="doctorId" id="viewDoctorId" value="${editDoctorId}">
            <input type="hidden" name="listKeyword" value="${keyword}">
            <input type="hidden" name="listSpecialization" value="${selectedSpecialization}">
            <input type="hidden" name="listQualification" value="${selectedQualification}">

            <c:if test="${not empty error and editModalOpen}"><div class="alert error" style="margin-bottom:12px;"><i class="fas fa-exclamation-circle"></i>${error}</div></c:if>

            <div class="form-grid">
                <div class="form-group form-full"><label>Họ tên <span style="color:red;">*</span></label><input type="text" id="viewFullName" name="fullName" value="${editFullName}" required><c:if test="${not empty editFullNameError}"><div class="field-error">${editFullNameError}</div></c:if></div>
                <div class="form-group"><label>Số điện thoại <span style="color:red;">*</span></label><input type="tel" id="viewPhone" name="phone" value="${editPhone}" required><c:if test="${not empty editPhoneError}"><div class="field-error">${editPhoneError}</div></c:if></div>
                <div class="form-group"><label>Email <span style="color:red;">*</span></label><input type="email" id="viewEmail" name="email" value="${editEmail}" required><c:if test="${not empty editEmailError}"><div class="field-error">${editEmailError}</div></c:if></div>
                <div class="form-group"><label>Trạng thái tài khoản</label><input type="text" id="viewStatus" value="${editStatus}" readonly></div>
                <div class="form-group"><label>Chuyên môn <span style="color:red;">*</span></label><input type="text" id="viewSpecializationText" readonly><select id="viewSpecialization" name="specialization" required><option value="">-- Chọn chuyên môn --</option><c:forEach var="sp" items="${specializationOptions}"><option value="${sp}" ${editSpecialization == sp ? 'selected' : ''}>${sp}</option></c:forEach></select><c:if test="${not empty editSpecializationError}"><div class="field-error">${editSpecializationError}</div></c:if></div>
                <div class="form-group"><label>Bằng cấp <span style="color:red;">*</span></label><input type="text" id="viewQualificationText" readonly><select id="viewQualification" name="qualification" required onchange="applyDefaultPrice('viewQualification','viewPriceBooking')"><option value="">-- Chọn bằng cấp --</option><c:forEach var="q" items="${qualificationOptions}"><option value="${q}" ${editQualification == q ? 'selected' : ''}>${q}</option></c:forEach></select><c:if test="${not empty editQualificationError}"><div class="field-error">${editQualificationError}</div></c:if></div>
                <div class="form-group"><label>Kinh nghiệm (năm) <span style="color:red;">*</span></label><input type="number" id="viewExperienceYears" name="experienceYears" min="0" max="50" value="${editExperience}" required><c:if test="${not empty editExperienceError}"><div class="field-error">${editExperienceError}</div></c:if></div>
                <div class="form-group"><label>Đánh giá</label><input type="text" id="viewRating" value="${editRating}" readonly></div>
                <div class="form-group"><label>Giá khám <span style="color:red;">*</span></label><input type="text" id="viewPriceBooking" name="priceBooking" inputmode="numeric" value="${editPrice}" required><c:if test="${not empty editPriceError}"><div class="field-error">${editPriceError}</div></c:if></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn-cancel" id="viewCloseBtn" onclick="onViewCloseOrCancel()"><i id="viewCloseBtnIcon" class="fas fa-times"></i> <span id="viewCloseBtnText">Đóng</span></button>
                <button type="button" id="viewEditToggleBtn" class="btn-submit" onclick="onViewEditToggle()"><i class="fas fa-pen-to-square"></i> <span id="viewEditToggleText">Sửa</span></button>
            </div>
        </form>
    </div>
</div>
<jsp:include page="../../common/footer.jsp" />

<script>
    const qualificationPriceMap = {
        "Giáo sư / Phó Giáo sư": 400000,
        "Tiến sĩ / Bác sĩ CK II": 300000,
        "Thạc sĩ / Bác sĩ CK I / BS nội trú": 200000
    };

    function applyDefaultPrice(selectId, priceInputId) {
        const select = document.getElementById(selectId);
        const priceInput = document.getElementById(priceInputId);
        if (!select || !priceInput) return;
        const defaultPrice = qualificationPriceMap[select.value];
        if (defaultPrice !== undefined) {
            const rawPrice = normalizeNumberString(defaultPrice);
            priceInput.dataset.rawPrice = rawPrice;
            priceInput.value = rawPrice;
            if (!isViewEditMode && priceInputId === 'viewPriceBooking') {
                priceInput.value = formatPriceDisplay(rawPrice);
            }
        }
    }

    function openAddModal() {
        document.getElementById('addDoctorModal').style.display = 'block';
    }

    function closeAddModal() {
        document.getElementById('addDoctorModal').style.display = 'none';
    }

    function formatStatusDisplay(rawStatus) {
        const value = (rawStatus || '').toString().trim().toLowerCase();
        if (value === 'active') return 'Hoạt động';
        return 'Khóa';
    }

    function normalizeNumberString(value) {
        const raw = (value || '').toString().trim();
        if (!raw) return '';
        const parsed = Number(raw.replace(/,/g, ''));
        if (Number.isFinite(parsed)) return String(Math.round(parsed));
        return raw.replace(/[^\d]/g, '');
    }

    function formatPriceDisplay(value) {
        const normalized = normalizeNumberString(value);
        if (!normalized) return '';
        return Number(normalized).toLocaleString('en-US');
    }

    function getRawPriceForEdit() {
        const priceField = document.getElementById('viewPriceBooking');
        if (!priceField) return '';
        const dataRaw = normalizeNumberString(priceField.dataset.rawPrice || '');
        if (dataRaw) return dataRaw;
        return normalizeNumberString(priceField.value);
    }

    function syncViewSelectText() {
        const spSelect = document.getElementById('viewSpecialization');
        const spText = document.getElementById('viewSpecializationText');
        const qSelect = document.getElementById('viewQualification');
        const qText = document.getElementById('viewQualificationText');
        if (spSelect && spText) {
            const spLabel = spSelect.selectedIndex >= 0 ? spSelect.options[spSelect.selectedIndex].text : '';
            spText.value = (spLabel && spLabel !== '-- Chọn chuyên môn --') ? spLabel : (spSelect.value || '');
        }
        if (qSelect && qText) {
            const qLabel = qSelect.selectedIndex >= 0 ? qSelect.options[qSelect.selectedIndex].text : '';
            qText.value = (qLabel && qLabel !== '-- Chọn bằng cấp --') ? qLabel : (qSelect.value || '');
        }
    }

    const viewEditableFieldIds = [
        'viewFullName',
        'viewPhone',
        'viewEmail',
        'viewSpecialization',
        'viewQualification',
        'viewExperienceYears',
        'viewPriceBooking'
    ];
    let originalViewSnapshot = null;
    let isViewEditMode = false;

    function captureViewSnapshot() {
        originalViewSnapshot = {
            fullName: document.getElementById('viewFullName').value || '',
            phone: document.getElementById('viewPhone').value || '',
            email: document.getElementById('viewEmail').value || '',
            specialization: document.getElementById('viewSpecialization').value || '',
            qualification: document.getElementById('viewQualification').value || '',
            experienceYears: document.getElementById('viewExperienceYears').value || '',
            priceBooking: normalizeNumberString(document.getElementById('viewPriceBooking').value || '')
        };
    }

    function restoreViewSnapshot() {
        if (!originalViewSnapshot) return;
        document.getElementById('viewFullName').value = originalViewSnapshot.fullName;
        document.getElementById('viewPhone').value = originalViewSnapshot.phone;
        document.getElementById('viewEmail').value = originalViewSnapshot.email;
        document.getElementById('viewSpecialization').value = originalViewSnapshot.specialization;
        document.getElementById('viewSpecializationText').value = originalViewSnapshot.specialization;
        document.getElementById('viewQualification').value = originalViewSnapshot.qualification;
        document.getElementById('viewQualificationText').value = originalViewSnapshot.qualification;
        document.getElementById('viewExperienceYears').value = originalViewSnapshot.experienceYears;
        document.getElementById('viewPriceBooking').dataset.rawPrice = normalizeNumberString(originalViewSnapshot.priceBooking);
        document.getElementById('viewPriceBooking').value = document.getElementById('viewPriceBooking').dataset.rawPrice;
    }

    function setViewCloseButton(editMode) {
        const icon = document.getElementById('viewCloseBtnIcon');
        const text = document.getElementById('viewCloseBtnText');
        if (!icon || !text) return;
        if (editMode) {
            icon.className = 'fas fa-rotate-left';
            text.textContent = 'Hủy';
        } else {
            icon.className = 'fas fa-times';
            text.textContent = 'Đóng';
        }
    }

    function setLockedFieldsVisual(editMode) {
        const statusField = document.getElementById('viewStatus');
        const ratingField = document.getElementById('viewRating');
        [statusField, ratingField].forEach(function (field) {
            if (!field) return;
            if (editMode) {
                field.classList.add('locked-readonly');
            } else {
                field.classList.remove('locked-readonly');
            }
        });
    }

    function setViewEditMode(enabled) {
        isViewEditMode = enabled;
        viewEditableFieldIds.forEach(function (id) {
            const field = document.getElementById(id);
            if (!field) return;
            if (field.tagName.toLowerCase() === 'select') {
                field.disabled = !enabled;
            } else {
                field.readOnly = !enabled;
            }
        });

        const priceField = document.getElementById('viewPriceBooking');
        if (priceField) {
            if (enabled) {
                priceField.type = 'number';
                priceField.min = '0';
                priceField.max = '10000000';
                priceField.step = '1';
                const rawPrice = getRawPriceForEdit();
                priceField.dataset.rawPrice = rawPrice;
                priceField.value = rawPrice;
            } else {
                const rawPrice = normalizeNumberString(priceField.value);
                priceField.dataset.rawPrice = rawPrice;
                priceField.type = 'text';
                priceField.value = formatPriceDisplay(rawPrice);
            }
        }

        const spSelect = document.getElementById('viewSpecialization');
        const spText = document.getElementById('viewSpecializationText');
        const qSelect = document.getElementById('viewQualification');
        const qText = document.getElementById('viewQualificationText');
        if (spSelect && spText && qSelect && qText) {
            if (enabled) {
                spSelect.style.display = '';
                qSelect.style.display = '';
                spText.style.display = 'none';
                qText.style.display = 'none';
            } else {
                syncViewSelectText();
                spSelect.style.display = 'none';
                qSelect.style.display = 'none';
                spText.style.display = '';
                qText.style.display = '';
            }
        }

        const btn = document.getElementById('viewEditToggleBtn');
        const text = document.getElementById('viewEditToggleText');
        const icon = btn ? btn.querySelector('i') : null;
        if (!btn || !text || !icon) return;

        if (enabled) {
            icon.className = 'fas fa-save';
            text.textContent = 'Lưu';
        } else {
            icon.className = 'fas fa-pen-to-square';
            text.textContent = 'Sửa';
        }
        setViewCloseButton(enabled);
        setLockedFieldsVisual(enabled);
    }

    function onViewEditToggle() {
        if (!isViewEditMode) {
            setViewEditMode(true);
            return;
        }
        const form = document.getElementById('viewDoctorForm');
        if (form) {
            if (typeof form.requestSubmit === 'function') {
                form.requestSubmit();
            } else if (form.reportValidity()) {
                form.submit();
            }
        }
    }

    function onViewCloseOrCancel() {
        if (isViewEditMode) {
            restoreViewSnapshot();
            setViewEditMode(false);
            return;
        }
        closeViewModal();
    }

    function handleViewFormSubmit() {
        if (!isViewEditMode) return false;
        const priceField = document.getElementById('viewPriceBooking');
        if (priceField) {
            const rawPrice = normalizeNumberString(priceField.value);
            priceField.dataset.rawPrice = rawPrice;
            priceField.value = rawPrice;
        }
        return isViewEditMode;
    }

    function openViewModal(btn) {
        document.getElementById('viewDoctorId').value = btn.dataset.doctorId || '';
        document.getElementById('viewFullName').value = btn.dataset.fullName || '';
        document.getElementById('viewPhone').value = btn.dataset.phone || '';
        document.getElementById('viewEmail').value = btn.dataset.email || '';
        document.getElementById('viewStatus').value = formatStatusDisplay(btn.dataset.status);
        document.getElementById('viewSpecialization').value = btn.dataset.specialization || '';
        document.getElementById('viewSpecializationText').value = btn.dataset.specialization || '';
        document.getElementById('viewQualification').value = btn.dataset.qualification || '';
        document.getElementById('viewQualificationText').value = btn.dataset.qualification || '';
        document.getElementById('viewExperienceYears').value = btn.dataset.experience || '';
        document.getElementById('viewRating').value = btn.dataset.rating || '0.0';
        document.getElementById('viewPriceBooking').dataset.rawPrice = normalizeNumberString(btn.dataset.priceBooking || '');
        document.getElementById('viewPriceBooking').value = document.getElementById('viewPriceBooking').dataset.rawPrice;
        setViewEditMode(false);
        captureViewSnapshot();
        document.getElementById('viewDoctorModal').style.display = 'block';
    }

    function closeViewModal() {
        setViewEditMode(false);
        document.getElementById('viewDoctorModal').style.display = 'none';
    }

    window.onclick = function (event) {
        const addModal = document.getElementById('addDoctorModal');
        const viewModal = document.getElementById('viewDoctorModal');
        if (event.target === addModal) closeAddModal();
        if (event.target === viewModal) closeViewModal();
    };

    <c:if test="${addModalOpen}">
    openAddModal();
    </c:if>

    <c:if test="${editModalOpen}">
    document.getElementById('viewDoctorModal').style.display = 'block';
    document.getElementById('viewDoctorId').value = '${fn:escapeXml(editDoctorId)}';
    document.getElementById('viewFullName').value = '${fn:escapeXml(editFullName)}';
    document.getElementById('viewPhone').value = '${fn:escapeXml(editPhone)}';
    document.getElementById('viewEmail').value = '${fn:escapeXml(editEmail)}';
    document.getElementById('viewStatus').value = formatStatusDisplay('${fn:escapeXml(editStatus)}');
    document.getElementById('viewSpecialization').value = '${fn:escapeXml(editSpecialization)}';
    document.getElementById('viewSpecializationText').value = '${fn:escapeXml(editSpecialization)}';
    document.getElementById('viewQualification').value = '${fn:escapeXml(editQualification)}';
    document.getElementById('viewQualificationText').value = '${fn:escapeXml(editQualification)}';
    document.getElementById('viewExperienceYears').value = '${fn:escapeXml(editExperience)}';
    document.getElementById('viewRating').value = '${fn:escapeXml(editRating)}';
    document.getElementById('viewPriceBooking').dataset.rawPrice = normalizeNumberString('${fn:escapeXml(editPrice)}');
    document.getElementById('viewPriceBooking').value = document.getElementById('viewPriceBooking').dataset.rawPrice;
    originalViewSnapshot = {
        fullName: '${fn:escapeXml(editOriginalFullName)}',
        phone: '${fn:escapeXml(editOriginalPhone)}',
        email: '${fn:escapeXml(editOriginalEmail)}',
        specialization: '${fn:escapeXml(editOriginalSpecialization)}',
        qualification: '${fn:escapeXml(editOriginalQualification)}',
        experienceYears: '${fn:escapeXml(editOriginalExperience)}',
        priceBooking: normalizeNumberString('${fn:escapeXml(editOriginalPrice)}')
    };
    setViewEditMode(true);
    </c:if>

    setTimeout(function () {
        document.querySelectorAll('.alert.success').forEach(function (el) {
            el.style.transition = 'opacity .35s ease';
            el.style.opacity = '0';
            setTimeout(function () { el.style.display = 'none'; }, 360);
        });
    }, 3000);
</script>
</body>
</html>
