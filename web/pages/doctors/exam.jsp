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
                <p>Chưa có kết quả xét nghiệm cho lịch khám này.</p>
            </div>

            <div class="tab-content" id="prescription">
                <p>Chức năng đơn thuốc sẽ được tích hợp ở bước tiếp theo.</p>
            </div>

            <div class="tab-content" id="history">
                <p>Lịch sử khám của bệnh nhân đang được cập nhật.</p>
            </div>
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
        </script>

    </body>
</html>