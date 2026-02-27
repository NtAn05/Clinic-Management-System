<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Doctor Dashboard</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/pages/examination/doctorDashboard.css">
    </head>
    <body>
        <div class="dashboard-container">
            <jsp:include page="/common/header.jsp" />

            <div class="summary-cards">
                <div class="card summary-card total">
                    <div class="summary-left">
                        <p>Tổng bệnh nhân</p>
                        <h3>${stats.total}</h3>
                    </div>
                    <div class="summary-icon blue">👥</div>
                </div>

                <div class="card summary-card waiting">
                    <div class="summary-left">
                        <p>Đang chờ</p>
                        <h3>${stats.waiting}</h3>
                    </div>
                    <div class="summary-icon yellow">⏰</div>
                </div>

                <div class="card summary-card examining">
                    <div class="summary-left">
                        <p>Đang khám</p>
                        <h3>${stats.done}</h3>
                    </div>
                    <div class="summary-icon green">❤️</div>
                </div>
            </div>

            <div class="content">
                <div class="queue-section">
                    <div class="section-heading">
                        <h3>Danh sách bệnh nhân hôm nay</h3>
<!--                        <p>Click vào bệnh nhân để mở popup bảng điều khiển.</p>-->
                    </div>

                    <div class="queue-filter">
                        <form method="get">
                            <input type="text" name="keyword"
                                   placeholder="Tìm kiếm bệnh nhân theo tên hoặc mã..."
                                   value="${param.keyword}"/>

                            <select name="status">
                                <option value="all" ${param.status=='all'?'selected':''}>Tất cả</option>
                                <option value="waiting" ${param.status=='waiting'?'selected':''}>Đang chờ</option>
                                <option value="examining" ${param.status=='examining'?'selected':''}>Đang khám</option>
                                <option value="done" ${param.status=='done'?'selected':''}>Hoàn tất</option>
                            </select>

                            <button type="submit">Lọc</button>
                        </form>
                    </div>

                    <div class="queue-list">
                        <div class="queue-table-header" aria-hidden="true">
                            <span>STT</span>
                            <span>Họ tên</span>
                            <span>Giới tính</span>
                            <span>Ngày sinh</span>
                            <span>Triệu chứng</span>
                            <span>Trạng thái</span>
                            <span>Thao tác</span>
                        </div>

                        <c:forEach var="q" items="${queueList}">
                            <div class="queue-card queue-row"
                                 data-appointment-id="${q.appointmentId}"
                                 data-name="${q.patientName}"
                                 data-gender="${q.gender}"
                                 data-dob="${q.dob}"
                                 data-symptom="${q.symptom}"
                                 data-status="${q.status}"
                                 data-position="${q.queuePosition}">

                                <span class="queue-col queue-position">#${q.queuePosition}</span>
                                <span class="queue-col queue-name">${q.patientName}</span>
                                <span class="queue-col queue-info">${q.gender}</span>
                                <span class="queue-col queue-dob">${q.dob}</span>
                                <span class="queue-col queue-symptom">${q.symptom}</span>
                                <span class="queue-col queue-state"><span class="queue-status ${q.status}">${q.status}</span></span>
                                <span class="queue-col queue-action"><span class="queue-cta">Mở điều khiển →</span></span>

                            </div>
                        </c:forEach>

                        <c:if test="${empty queueList}">
                            <p class="empty-state">Không có bệnh nhân chờ khám</p>
                        </c:if>
                    </div>
                </div>

                <div class="shift-section">
                    <h3>Lịch làm việc hôm nay</h3>

                    <c:forEach var="s" items="${shifts}">
                        <div class="shift-item">
                            <span>${s.startTime} - ${s.endTime}</span>
                            <span>Tối đa ${s.maxPatients}</span>
                        </div>
                    </c:forEach>
                    <c:if test="${empty shifts}">
                        <p>Hôm nay không có ca làm việc</p>
                    </c:if>
                </div>
            </div>
        </div>

        <div id="controlPanelBackdrop" class="popup-backdrop"></div>
        <div id="controlPanel" class="control-panel-popup" role="dialog" aria-modal="true" aria-hidden="true">
            <div class="control-panel-header">
                <div>
                    <p class="panel-kicker">Bảng điều khiển khám</p>
                    <h3 id="d-name">Chưa chọn bệnh nhân</h3>
                </div>
                <button type="button" class="btn-close" onclick="closeControlPanel()">✕</button>
            </div>

            <div class="panel-meta">
                <p><b>STT:</b> <span id="d-position"></span></p>
                <p><b>Giới tính:</b> <span id="d-gender"></span></p>
                <p><b>Ngày sinh:</b> <span id="d-dob"></span></p>
                <p><b>Trạng thái:</b> <span id="d-status" class="status-badge"></span></p>
            </div>

            <div class="panel-symptom">
                <p><b>Triệu chứng:</b></p>
                <p id="d-symptom"></p>
            </div>

            <div class="actions">
                <button id="btnStart" class="btn btn-primary" type="button">▶ Bắt đầu khám</button>
                <button id="btnLab" class="btn btn-lab" type="button">🧪 Chỉ định xét nghiệm</button>
            </div>
        </div>

        <jsp:include page="/common/modal-alert.jsp" />
        <script>
            function orderLab(appointmentId) {
                showConfirm('Chỉ định xét nghiệm cho bệnh nhân này? Bệnh nhân sẽ chuyển sang hàng đợi xét nghiệm.', function () {
                    var formData = new FormData();
                    formData.append('action', 'createLabRequest');
                    formData.append('appointmentId', appointmentId);
                    fetch('${pageContext.request.contextPath}/lab-queue', {
                        method: 'POST',
                        body: formData
                    })
                            .then(function (r) {
                                return r.json();
                            })
                            .then(function (data) {
                                if (data.success) {
                                    showAlert(data.message || 'Đã chỉ định xét nghiệm.', 'success', function () {
                                        location.reload();
                                    });
                                } else {
                                    showAlert(data.message || 'Thất bại.', 'error');
                                }
                            })
                            .catch(function () {
                                showAlert('Lỗi kết nối.', 'error');
                            });
                });
            }

            const panel = document.getElementById('controlPanel');
            const backdrop = document.getElementById('controlPanelBackdrop');
            const dName = document.getElementById('d-name');
            const dGender = document.getElementById('d-gender');
            const dDob = document.getElementById('d-dob');
            const dSymptom = document.getElementById('d-symptom');
            const dStatus = document.getElementById('d-status');
            const dPosition = document.getElementById('d-position');
            const btnStart = document.getElementById('btnStart');
            const btnLab = document.getElementById('btnLab');

            function closeControlPanel() {
                panel.classList.remove('open');
                backdrop.classList.remove('open');
                panel.setAttribute('aria-hidden', 'true');
            }

            document.querySelectorAll('.queue-row').forEach(row => {
                row.addEventListener('click', function () {
                    const appointmentId = this.dataset.appointmentId;
                    const status = this.dataset.status;

                    dName.innerText = this.dataset.name;
                    dGender.innerText = this.dataset.gender;
                    dDob.innerText = this.dataset.dob;
                    dSymptom.innerText = this.dataset.symptom;
                    dStatus.innerText = status;
                    dPosition.innerText = this.dataset.position;
                    dStatus.className = 'status-badge ' + status;

                    btnStart.onclick = function () {
                        location.href = '${pageContext.request.contextPath}/doctor/exam?appointmentId=' + appointmentId;
                    };

                    btnLab.disabled = !(status === 'waiting' || status === 'examining');
                    btnLab.onclick = function () {
                        orderLab(appointmentId);
                    };

                    panel.classList.add('open');
                    backdrop.classList.add('open');
                    panel.setAttribute('aria-hidden', 'false');
                });
            });

            backdrop.addEventListener('click', closeControlPanel);
            document.addEventListener('keydown', function (e) {
                if (e.key === 'Escape') {
                    closeControlPanel();
                }
            });
        </script>

        <jsp:include page="/common/footer.jsp" />
    </body>
</html>