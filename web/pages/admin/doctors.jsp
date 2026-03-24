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
        .btn-edit { color:#FB923C; }
        .btn-calendar { color:#5b21b6; }
        .btn-edit:hover { background:#FFEDD5; }
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
        .input-action-row { display:flex; gap:10px; align-items:stretch; }
        .input-action-row input { flex:1; }
        .btn-inline { border:none; border-radius:6px; padding:0 14px; font-size:13px; font-weight:600; background:#f59e0b; color:#fff; cursor:pointer; white-space:nowrap; display:inline-flex; align-items:center; justify-content:center; align-self:stretch; min-height:42px; }
        .btn-inline:hover { background:#d97706; }
        .field-error { margin-top:6px; color:#dc3545; font-size:13px; font-weight:600; }
        .modal-footer { display:flex; gap:10px; justify-content:flex-end; margin-top:24px; padding-top:15px; border-top:1px solid #f0f0f0; }
        .btn-cancel,.btn-submit { padding:10px 20px; border:none; border-radius:6px; cursor:pointer; font-weight:600; display:flex; align-items:center; gap:6px; }
        .btn-cancel { background:#f0f0f0; color:#333; }
        .btn-submit { background:#0061ff; color:#fff; }
        .readonly-field { background:#f3f4f6; color:#6b7280; cursor:not-allowed; }
        @media (max-width: 992px){ .container{padding:20px;} .toolbar{grid-template-columns:1fr;} .toolbar-buttons .btn-search,.toolbar-buttons .btn-reset,.toolbar-buttons .btn-add{flex:1; justify-content:center;} .form-grid{grid-template-columns:1fr;} .input-action-row{flex-direction:column;} .btn-inline{padding:10px 14px;} }
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
                                        <button type="button" class="btn-action btn-edit" title="Chỉnh sửa"
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
                                                data-pending-resend="${pendingResendMap[d.userId] ? 'true' : 'false'}"
                                                onclick="openEditModal(this)"><i class="fas fa-pen-to-square"></i></button>
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

<div id="editDoctorModal" class="modal">
    <div class="modal-content">
        <div class="modal-header"><i class="fas fa-pen-to-square"></i><span>Chỉnh sửa bác sĩ</span><button class="modal-close" type="button" onclick="closeEditModal()">×</button></div>
        <form id="editDoctorForm" method="POST" action="${pageContext.request.contextPath}/admin-doctors">
            <input type="hidden" name="action" value="edit">
            <input type="hidden" name="doctorId" id="editDoctorId" value="${editDoctorId}">
            <input type="hidden" name="listKeyword" value="${keyword}">
            <input type="hidden" name="listSpecialization" value="${selectedSpecialization}">
            <input type="hidden" name="listQualification" value="${selectedQualification}">

            <c:if test="${not empty error and editModalOpen}"><div class="alert error" style="margin-bottom:12px;"><i class="fas fa-exclamation-circle"></i>${error}</div></c:if>

            <div class="form-grid">
                <div class="form-group form-full"><label>Họ tên <span style="color:red;">*</span></label><input type="text" id="editFullName" name="fullName" value="${editFullName}" required><c:if test="${not empty editFullNameError}"><div class="field-error">${editFullNameError}</div></c:if></div>
                <div class="form-group"><label>Số điện thoại <span style="color:red;">*</span></label><input type="tel" id="editPhone" name="phone" value="${editPhone}" required><c:if test="${not empty editPhoneError}"><div class="field-error">${editPhoneError}</div></c:if></div>
                <div class="form-group"><label>Email <span style="color:red;">*</span></label><div class="input-action-row"><input type="email" id="editEmail" name="email" value="${editEmail}" required><button type="button" class="btn-inline" id="editResendButton" onclick="resendPasswordFromEditModal()" style="display:none;">Gửi lại email</button></div><c:if test="${not empty editEmailError}"><div class="field-error">${editEmailError}</div></c:if></div>
                <div class="form-group"><label>Trạng thái tài khoản</label><input class="readonly-field" type="text" id="editStatus" value="${editStatus}" readonly></div>
                <div class="form-group"><label>Chuyên môn <span style="color:red;">*</span></label><select id="editSpecialization" name="specialization" required><option value="">-- Chọn chuyên môn --</option><c:forEach var="sp" items="${specializationOptions}"><option value="${sp}" ${editSpecialization == sp ? 'selected' : ''}>${sp}</option></c:forEach></select><c:if test="${not empty editSpecializationError}"><div class="field-error">${editSpecializationError}</div></c:if></div>
                <div class="form-group"><label>Bằng cấp <span style="color:red;">*</span></label><select id="editQualification" name="qualification" required onchange="applyDefaultPrice('editQualification','editPriceBooking')"><option value="">-- Chọn bằng cấp --</option><c:forEach var="q" items="${qualificationOptions}"><option value="${q}" ${editQualification == q ? 'selected' : ''}>${q}</option></c:forEach></select><c:if test="${not empty editQualificationError}"><div class="field-error">${editQualificationError}</div></c:if></div>
                <div class="form-group"><label>Kinh nghiệm (năm) <span style="color:red;">*</span></label><input type="number" id="editExperienceYears" name="experienceYears" min="0" max="50" value="${editExperience}" required><c:if test="${not empty editExperienceError}"><div class="field-error">${editExperienceError}</div></c:if></div>
                <div class="form-group"><label>Đánh giá</label><input class="readonly-field" type="text" id="editRating" value="${editRating}" readonly></div>
                <div class="form-group"><label>Giá khám <span style="color:red;">*</span></label><input type="number" id="editPriceBooking" name="priceBooking" inputmode="numeric" min="0" max="10000000" value="${editPrice}" required><c:if test="${not empty editPriceError}"><div class="field-error">${editPriceError}</div></c:if></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn-cancel" onclick="closeEditModal()"><i class="fas fa-times"></i> Hủy</button>
                <button type="submit" class="btn-submit"><i class="fas fa-save"></i> Lưu</button>
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
            priceInput.value = String(defaultPrice);
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

    function toggleEditResendButton(show) {
        const button = document.getElementById('editResendButton');
        if (button) {
            button.style.display = show ? 'inline-flex' : 'none';
        }
    }

    function resendPasswordFromEditModal() {
        const doctorId = document.getElementById('editDoctorId').value;
        if (!doctorId) return;
        if (!confirm('Gửi lại mật khẩu tạm qua email cho bác sĩ này?')) return;

        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '${pageContext.request.contextPath}/admin-doctors';
        form.innerHTML =
                '<input type=\"hidden\" name=\"action\" value=\"resendPassword\">'
                + '<input type=\"hidden\" name=\"doctorId\" value=\"' + doctorId + '\">'
                + '<input type=\"hidden\" name=\"listKeyword\" value=\"${fn:escapeXml(keyword)}\">'
                + '<input type=\"hidden\" name=\"listSpecialization\" value=\"${fn:escapeXml(selectedSpecialization)}\">'
                + '<input type=\"hidden\" name=\"listQualification\" value=\"${fn:escapeXml(selectedQualification)}\">';
        document.body.appendChild(form);
        form.submit();
    }

    function openEditModal(btn) {
        document.getElementById('editDoctorId').value = btn.dataset.doctorId || '';
        document.getElementById('editFullName').value = btn.dataset.fullName || '';
        document.getElementById('editPhone').value = btn.dataset.phone || '';
        document.getElementById('editEmail').value = btn.dataset.email || '';
        document.getElementById('editStatus').value = formatStatusDisplay(btn.dataset.status);
        document.getElementById('editSpecialization').value = btn.dataset.specialization || '';
        document.getElementById('editQualification').value = btn.dataset.qualification || '';
        document.getElementById('editExperienceYears').value = btn.dataset.experience || '';
        document.getElementById('editRating').value = btn.dataset.rating || '0.0';
        document.getElementById('editPriceBooking').value = btn.dataset.priceBooking || '';
        toggleEditResendButton(btn.dataset.pendingResend === 'true');
        document.getElementById('editDoctorModal').style.display = 'block';
    }

    function closeEditModal() {
        toggleEditResendButton(false);
        document.getElementById('editDoctorModal').style.display = 'none';
    }

    window.onclick = function (event) {
        const addModal = document.getElementById('addDoctorModal');
        const editModal = document.getElementById('editDoctorModal');
        if (event.target === addModal) closeAddModal();
        if (event.target === editModal) closeEditModal();
    };

    <c:if test="${addModalOpen}">
    openAddModal();
    </c:if>

    <c:if test="${editModalOpen}">
    document.getElementById('editDoctorModal').style.display = 'block';
    document.getElementById('editDoctorId').value = '${fn:escapeXml(editDoctorId)}';
    document.getElementById('editFullName').value = '${fn:escapeXml(editFullName)}';
    document.getElementById('editPhone').value = '${fn:escapeXml(editPhone)}';
    document.getElementById('editEmail').value = '${fn:escapeXml(editEmail)}';
    document.getElementById('editStatus').value = formatStatusDisplay('${fn:escapeXml(editStatus)}');
    document.getElementById('editSpecialization').value = '${fn:escapeXml(editSpecialization)}';
    document.getElementById('editQualification').value = '${fn:escapeXml(editQualification)}';
    document.getElementById('editExperienceYears').value = '${fn:escapeXml(editExperience)}';
    document.getElementById('editRating').value = '${fn:escapeXml(editRating)}';
    document.getElementById('editPriceBooking').value = '${fn:escapeXml(editPrice)}';
    toggleEditResendButton('${editResendAvailable}' === 'true');
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

