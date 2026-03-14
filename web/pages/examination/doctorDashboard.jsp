<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
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
                        <h3>${stats.examining}</h3>
                    </div>
                    <div class="summary-icon green">🩺</div>
                </div>

                <div class="card summary-card done">
                    <div class="summary-left">
                        <p>Đã hoàn tất</p>
                        <h3>${stats.done}</h3>
                    </div>
                    <div class="summary-icon green">✅</div>
                </div>

                <div class="card summary-card done">
                    <div class="summary-left">
                        <p>Tỷ lệ hoàn thành</p>
                        <h3><fmt:formatNumber value="${stats.completionRate}" maxFractionDigits="1"/>%</h3>
                    </div>
                    <div class="summary-icon blue">📈</div>
                </div>
            </div>

            <div class="kpi-core-strip">
                <div class="kpi-chip">
                    <span class="kpi-label">Ca hoàn tất hôm nay</span>
                    <strong class="kpi-value">${stats.doneToday}</strong>
                </div>
                <div class="kpi-chip">
                    <span class="kpi-label">Ca hoàn tất tuần này</span>
                    <strong class="kpi-value">${stats.doneThisWeek}</strong>
                </div>
                <div class="kpi-chip">
                    <span class="kpi-label">Ca hoàn tất tháng này</span>
                    <strong class="kpi-value">${stats.doneThisMonth}</strong>
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
                                   value="${keyword}"/>

                            <select name="status">
                                <option value="all" ${selectedStatus=='all'?'selected':''}>Tất cả</option>
                                <option value="waiting" ${selectedStatus=='waiting'?'selected':''}>Đang chờ</option>
                                <option value="examining" ${selectedStatus=='examining'?'selected':''}>Đang khám</option>
                                <option value="done" ${selectedStatus=='done'?'selected':''}>Hoàn tất</option>
                            </select>
                            <input type="hidden" name="page" value="1"/>
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
                    <c:if test="${totalRecords > 0}">
                        <div class="queue-pagination">
                            <span class="pagination-summary">Trang ${currentPage}/${totalPages} • ${totalRecords} bệnh nhân</span>
                            <div class="pagination-actions">
                                <c:url var="prevPageUrl" value="/doctorDashboard">
                                    <c:param name="status" value="${selectedStatus}" />
                                    <c:param name="keyword" value="${keyword}" />
                                    <c:param name="page" value="${currentPage - 1}" />
                                </c:url>
                                <c:url var="nextPageUrl" value="/doctorDashboard">
                                    <c:param name="status" value="${selectedStatus}" />
                                    <c:param name="keyword" value="${keyword}" />
                                    <c:param name="page" value="${currentPage + 1}" />
                                </c:url>

                                <c:choose>
                                    <c:when test="${currentPage > 1}">
                                        <a class="page-btn" href="${prevPageUrl}">← Trước</a>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="page-btn disabled">← Trước</span>
                                    </c:otherwise>
                                </c:choose>

                                <c:choose>
                                    <c:when test="${currentPage < totalPages}">
                                        <a class="page-btn" href="${nextPageUrl}">Sau →</a>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="page-btn disabled">Sau →</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
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
                location.href = '${pageContext.request.contextPath}/doctor/exam?appointmentId=' + appointmentId + '&tab=lab&createLab=1';
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