<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
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
                <form id="examForm" method="post" action="${pageContext.request.contextPath}/doctor/exam">
                    <input type="hidden" name="appointmentId" value="${examData.appointmentId}">

                    <div class="exam-header">
                        <div>
                            <p class="kicker">Hồ sơ khám</p>
                            <h2>Phiên khám bệnh #${examData.queuePosition} - ${examData.patientName}</h2>
                        </div>
                        <div class="actions">
                            <button class="btn-outline" type="button" onclick="location.href = '${pageContext.request.contextPath}/doctorDashboard'">← Quay lại</button>
                            <button class="btn-primary" type="submit" name="action" value="save">💾 Lưu tạm</button>
                            <button class="btn-success" type="submit" name="action" value="finish" onclick="return confirm('Xác nhận hoàn tất phiên khám này?');">✔ Hoàn thành</button>
                        </div>
                    </div>

                    <c:if test="${not empty success}">
                        <div class="alert-success">Đã lưu thông tin bệnh án.</div>
                    </c:if>

                    <c:if test="${error == 'saveFailed'}">
                        <div class="alert-error">Không thể lưu hồ sơ khám. Vui lòng thử lại.</div>
                    </c:if>

                    <div class="tabs">
                        <button class="tab active" data-target="info" type="button" onclick="showTab('info')">Thông tin</button>
                        <button class="tab" data-target="lab" type="button" onclick="showTab('lab')">Kết quả XN</button>
                        <button class="tab" data-target="prescription" type="button" onclick="showTab('prescription')">Đơn thuốc</button>
                        <button class="tab" data-target="history" type="button" onclick="showTab('history')">Lịch sử</button>
                    </div>

                    <div class="tab-content active" id="info">
                        <div class="card-grid">
                            <section class="card">
                                <h3>Thông tin bệnh nhân</h3>
                                <div class="grid">
                                    <div>
                                        <label>Họ tên</label>
                                        <input value="${examData.patientName}" readonly>
                                    </div>
                                    <div>
                                        <label>Giới tính</label>
                                        <input value="${examData.gender}" readonly>
                                    </div>
                                    <div>
                                        <label>Ngày sinh</label>
                                        <input value="${examData.dob}" readonly>
                                    </div>
                                    <div>
                                        <label>Trạng thái khám</label>
                                        <input value="${examData.status}" readonly>
                                    </div>
                                </div>

                                <label>Triệu chứng ban đầu (đặt lịch)</label>
                                <textarea rows="3" readonly>${examData.symptom}</textarea>

                                <label>Triệu chứng hiện tại</label>
                                <textarea rows="3" name="symptoms" placeholder="Mô tả triệu chứng hiện tại của bệnh nhân..."><c:out value="${medicalRecord != null ? medicalRecord.symptoms : examData.symptom}"/></textarea>

                                <label>Chẩn đoán</label>
                                <textarea rows="3" name="diagnosis" placeholder="Nhập chẩn đoán lâm sàng..."><c:out value="${medicalRecord != null ? medicalRecord.diagnosis : ''}"/></textarea>
                            </section>

                            <section class="card">
                                <h3>Tiền sử bệnh</h3>
                                <label>Dị ứng</label>
                                <textarea rows="2" name="historyAllergies" placeholder="Liệt kê các dị ứng đã biết"><c:out value="${historyAllergies}"/></textarea>

                                <label>Bệnh mãn tính</label>
                                <textarea rows="2" name="historyChronic" placeholder="Liệt kê bệnh mãn tính"><c:out value="${historyChronic}"/></textarea>

                                <label>Tiền sử gia đình</label>
                                <textarea rows="2" name="historyFamily" placeholder="Tiền sử bệnh gia đình liên quan"><c:out value="${historyFamily}"/></textarea>

                                <label>Tiền sử xã hội</label>
                                <textarea rows="2" name="historySocial" placeholder="Thói quen sinh hoạt, hút thuốc, rượu bia..."><c:out value="${historySocial}"/></textarea>

                                <label>Lịch sử tiêm chủng</label>
                                <textarea rows="2" name="historyVaccination" placeholder="Các mũi tiêm và thời điểm"><c:out value="${historyVaccination}"/></textarea>
                            </section>
                        </div>

                        <section class="card section-spacing">
                            <h3>Kết quả khám lâm sàng</h3>
                            <label>Kết quả khám</label>
                            <textarea rows="4" name="clinicalResult" placeholder="Ghi chép quan sát và kết quả khám lâm sàng..."><c:out value="${clinicalResult}"/></textarea>

                            <label>Ghi chú của bác sĩ</label>
                            <textarea rows="4" name="doctorNote" placeholder="Ghi chú và dặn dò bổ sung..."><c:out value="${doctorNote}"/></textarea>

                            <label>Phương án điều trị</label>
                            <textarea rows="4" name="treatmentPlan" placeholder="Mô tả phương án điều trị đề xuất..."><c:out value="${treatmentPlan}"/></textarea>
                        </section>
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
                                        <th>Chẩn đoán</th>
                                        <th>Ghi chú</th>
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
                                            <td>${h.diagnosis}</td>
                                            <td>${h.notes}</td>
                                            <td>${h.appointmentStatus}</td>
                                            <td>${h.queueStatus}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:if>
                    </div>
                </form>
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