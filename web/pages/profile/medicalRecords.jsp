<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Hồ sơ bệnh án</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/pages/profile/medicalRecords.css">
    </head>
    <body>
        <jsp:include page="/common/header.jsp" />

        <main class="medical-page">
            <section class="page-hero">
                <h1>Hồ sơ bệnh án cá nhân</h1>
                <p>Theo dõi lại các lần khám trước đây: triệu chứng, chẩn đoán, tiền sử, ghi chú bác sĩ và phương án điều trị.</p>
            </section>

            <section class="filter-card">
                <form method="get" action="${pageContext.request.contextPath}/patient-medical-records" class="filter-form">
                    <label for="patientId">Chọn bệnh nhân</label>
                    <select id="patientId" name="patientId" onchange="this.form.submit()">
                        <option value="">Tất cả bệnh nhân</option>
                        <c:forEach var="p" items="${patients}">
                            <option value="${p.patientId}" ${selectedPatientId == p.patientId ? 'selected' : ''}>${p.fullName}</option>
                        </c:forEach>
                    </select>
                </form>
            </section>

            <section class="stat-row">
                <div class="stat-card">
                    <div class="stat-label">Tổng hồ sơ đã lưu</div>
                    <div class="stat-value">${empty records ? 0 : records.size()}</div>
                </div>
            </section>

            <c:if test="${empty records}">
                <div class="empty-card">
                    Bạn chưa có hồ sơ bệnh án nào. Sau khi hoàn tất buổi khám, hồ sơ sẽ hiển thị tại đây.
                </div>
            </c:if>

            <c:if test="${not empty records}">
                <section class="record-list">
                    <c:forEach var="item" items="${records}">
                        <article class="record-card">
                            <div class="record-head">
                                <div>
                                    <span class="record-id">#${item.appointmentId}</span>
                                    <span class="patient-chip">Bệnh nhân: ${item.patientName}</span>
                                    <span class="doctor-chip">Bác sĩ: ${item.doctorName}</span>
                                </div>
                                <div class="record-date">
                                    <fmt:formatDate value="${item.appointmentDate}" pattern="dd/MM/yyyy" /> · ${item.appointmentTime}
                                </div>
                            </div>

                            <div class="record-grid">
                                <section class="section-box">
                                    <h3 class="section-title">Triệu chứng</h3>
                                    <p class="section-content ${empty item.symptoms ? 'muted' : ''}">
                                        ${empty item.symptoms ? 'Chưa có thông tin.' : item.symptoms}
                                    </p>
                                </section>

                                <section class="section-box">
                                    <h3 class="section-title">Chẩn đoán</h3>
                                    <p class="section-content ${empty item.diagnosis ? 'muted' : ''}">
                                        ${empty item.diagnosis ? 'Chưa có thông tin.' : item.diagnosis}
                                    </p>
                                </section>

                                <section class="section-box" style="grid-column: 1 / -1;">
                                    <h3 class="section-title">Tiền sử</h3>
                                    <div class="history-grid">
                                        <div class="history-item">
                                            <h4 class="history-item-title">Dị ứng</h4>
                                            <p class="history-item-value ${empty item.historyAllergies ? 'muted' : ''}">${empty item.historyAllergies ? 'Chưa có thông tin.' : item.historyAllergies}</p>
                                        </div>
                                        <div class="history-item">
                                            <h4 class="history-item-title">Bệnh mãn tính</h4>
                                            <p class="history-item-value ${empty item.historyChronic ? 'muted' : ''}">${empty item.historyChronic ? 'Chưa có thông tin.' : item.historyChronic}</p>
                                        </div>
                                        <div class="history-item">
                                            <h4 class="history-item-title">Tiền sử gia đình</h4>
                                            <p class="history-item-value ${empty item.historyFamily ? 'muted' : ''}">${empty item.historyFamily ? 'Chưa có thông tin.' : item.historyFamily}</p>
                                        </div>
                                        <div class="history-item">
                                            <h4 class="history-item-title">Tiền sử xã hội</h4>
                                            <p class="history-item-value ${empty item.historySocial ? 'muted' : ''}">${empty item.historySocial ? 'Chưa có thông tin.' : item.historySocial}</p>
                                        </div>
                                        <div class="history-item" style="grid-column: 1 / -1;">
                                            <h4 class="history-item-title">Lịch sử tiêm chủng</h4>
                                            <p class="history-item-value ${empty item.historyVaccination ? 'muted' : ''}">${empty item.historyVaccination ? 'Chưa có thông tin.' : item.historyVaccination}</p>
                                        </div>
                                    </div>
                                </section>

                                <section class="section-box">
                                    <h3 class="section-title">Ghi chú bác sĩ</h3>
                                    <p class="section-content ${empty item.doctorNote ? 'muted' : ''}">
                                        ${empty item.doctorNote ? 'Chưa có ghi chú thêm.' : item.doctorNote}
                                    </p>
                                </section>

                                <section class="section-box">
                                    <h3 class="section-title">Phương án điều trị</h3>
                                    <p class="section-content ${empty item.treatmentPlan ? 'muted' : ''}">
                                        ${empty item.treatmentPlan ? 'Chưa có phương án điều trị.' : item.treatmentPlan}
                                    </p>
                                </section>
                            </div>
                        </article>
                    </c:forEach>
                </section>
            </c:if>
        </main>
    </body>
</html>