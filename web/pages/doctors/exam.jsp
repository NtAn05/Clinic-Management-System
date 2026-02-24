<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Khám bệnh</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/pages/doctors/exam.css">
    </head>
    <body>

        <div class="exam-container">
            <c:if test="${not empty pageError}">
                <div class="alert-error">${pageError}</div>
                <div class="actions">
                    <button class="btn-outline" type="button" onclick="location.href = '${pageContext.request.contextPath}/doctorDashboard'">← Quay lại danh sách</button>
                </div>
            </c:if>

            <c:if test="${empty pageError}">
            <div class="exam-header">
                <div>
                    <p class="kicker">Hồ sơ khám</p>
                    <h2>Phiên khám bệnh #${examData.queuePosition} - ${examData.patientName}</h2>
                </div>
                <div class="actions">
                    <button class="btn-outline" type="button" onclick="location.href = '${pageContext.request.contextPath}/doctorDashboard'">← Quay lại</button>
                    <form method="post" action="${pageContext.request.contextPath}/doctor/exam">
                        <input type="hidden" name="appointmentId" value="${examData.appointmentId}">
                        <input type="hidden" name="action" value="save">
                        <button class="btn-primary" type="submit">💾 Lưu tạm</button>
                    </form>
                    <form method="post" action="${pageContext.request.contextPath}/doctor/exam" onsubmit="return confirm('Xác nhận hoàn tất phiên khám này?');">
                        <input type="hidden" name="appointmentId" value="${examData.appointmentId}">
                        <input type="hidden" name="action" value="finish">
                        <button class="btn-success" type="submit">✔ Hoàn thành</button>
                    </form>
                </div>
            </div>

            <c:if test="${not empty success}">
                <div class="alert-success">Đã lưu tạm thông tin khám.</div>
            </c:if>

            <div class="tabs">
                <button class="tab active" data-target="info" onclick="showTab('info')">Thông tin</button>
                <button class="tab" data-target="lab" onclick="showTab('lab')">Kết quả XN</button>
                <button class="tab" data-target="prescription" onclick="showTab('prescription')">Đơn thuốc</button>
                <button class="tab" data-target="history" onclick="showTab('history')">Lịch sử</button>
            </div>

            <div class="tab-content active" id="info">
                <div class="card-grid">
                    <section class="card">
                        <h3>Thông tin bệnh nhân</h3>
                        <div class="grid">
                            <input value="${examData.patientName}" readonly>
                            <input value="${examData.gender}" readonly>
                            <input value="${examData.dob}" readonly>
                            <input value="${examData.status}" readonly>
                        </div>
                        <textarea rows="3" readonly>${examData.symptom}</textarea>
                    </section>

                    <section class="card">
                        <h3>Đánh giá lâm sàng</h3>
                        <textarea rows="4" placeholder="Nhập dấu hiệu sinh tồn, kết quả khám tổng quát..."></textarea>
                        <textarea rows="4" placeholder="Nhập chẩn đoán sơ bộ và hướng xử trí..."></textarea>
                    </section>
                </div>
            </div>

            <div class="tab-content" id="lab">
                <c:if test="${empty labResults}">
                    <p>Chưa có kết quả xét nghiệm cho lịch khám này.</p>
                </c:if>

                <c:forEach var="lab" items="${labResults}">
                    <div class="card lab-item">
                        <h4>Phiếu xét nghiệm #${lab.requestId}</h4>
                        <p><b>Trạng thái:</b> ${lab.status}</p>
                        <p><b>Thời gian chỉ định:</b> ${lab.requestedAt}</p>
                        <p><b>Hoàn tất:</b> ${lab.completedAt}</p>
                        <p><b>Ghi chú kỹ thuật:</b> ${empty lab.notes ? '---' : lab.notes}</p>
                        <p>
                            <b>File kết quả:</b>
                            <c:choose>
                                <c:when test="${not empty lab.resultFile}">
                                    <a href="${pageContext.request.contextPath}/${lab.resultFile}" target="_blank">Xem file</a>
                                </c:when>
                                <c:otherwise>Chưa có file</c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                </c:forEach>
            </div>

            <div class="tab-content" id="prescription">
                <div class="card">
                    <h3>Đơn thuốc tạm</h3>
                    
                    <div id="rxList" class="rx-list">
                        <div class="rx-row">
                            <input placeholder="Tên thuốc">
                            <input placeholder="Liều dùng">
                            <input placeholder="Số lần/ngày">
                            <input placeholder="Số ngày">
                        </div>
                    </div>
                    <button type="button" class="btn-outline" onclick="addRxRow()">+ Thêm thuốc</button>
                </div>
            </div>

            <div class="tab-content" id="history">
                <c:if test="${empty historyList}">
                    <p>Chưa có lịch sử khám trước đó của bệnh nhân.</p>
                </c:if>

                <c:if test="${not empty historyList}">
                    <table class="history-table" border="1" cellpadding="8" cellspacing="0" width="100%">
                        <thead>
                            <tr>
                                <th>Mã lịch khám</th>
                                <th>Ngày khám</th>
                                <th>Giờ khám</th>
                                <th>Triệu chứng</th>
                                <th>Trạng thái lịch</th>
                                <th>Trạng thái khám</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="h" items="${historyList}">
                                <tr>
                                    <td>#${h.appointmentId}</td>
                                    <td><fmt:formatDate value="${h.appointmentDate}" pattern="dd/MM/yyyy" /></td>
                                    <td>${h.appointmentTime}</td>
                                    <td>${h.symptom}</td>
                                    <td>${h.appointmentStatus}</td>
                                    <td>${h.queueStatus}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
                </c:if>
        </div>

        <script>
            function showTab(id) {
                document.querySelectorAll('.tab').forEach(t => {
                    t.classList.toggle('active', t.dataset.target === id);
                });
                document.querySelectorAll('.tab-content').forEach(c => {
                    c.classList.toggle('active', c.id === id);
                });
            }

            function addRxRow() {
                const wrap = document.getElementById('rxList');
                const row = document.createElement('div');
                row.className = 'rx-row';
                row.innerHTML = `
                    <input placeholder="Tên thuốc">
                    <input placeholder="Liều dùng">
                    <input placeholder="Số lần/ngày">
                    <input placeholder="Số ngày">
                `;
                wrap.appendChild(row);
            }
        </script>

    </body>
</html>