<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
        <h2>Phòng khám ABC</h2>
        <div class="actions">
            <button class="btn-outline" onclick="history.back()">← Quay lại</button>
            <button class="btn-primary">💾 Lưu</button>
            <button class="btn-success">✔ Hoàn thành</button>
        </div>
    </div>

    <!-- Tabs -->
    <div class="tabs">
        <button class="tab active" onclick="showTab('info')">Thông tin</button>
        <button class="tab" onclick="showTab('lab')">Kết quả XN</button>
        <button class="tab" onclick="showTab('prescription')">Đơn thuốc</button>
        <button class="tab" onclick="showTab('history')">Lịch sử</button>
    </div>

    <!-- Tab content -->
    <div class="tab-content active" id="info">
        <h3>Chỉ số sinh tồn</h3>
        <div class="grid">
            <input placeholder="Huyết áp">
            <input placeholder="Nhiệt độ">
            <input placeholder="Nhịp tim">
            <input placeholder="Nhịp thở">
        </div>

        <h3>Triệu chứng</h3>
        <textarea rows="4" placeholder="Nhập triệu chứng..."></textarea>
    </div>

    <div class="tab-content" id="lab">
        <p>Chưa có kết quả xét nghiệm</p>
    </div>

    <div class="tab-content" id="prescription">
        <p>Form đơn thuốc</p>
    </div>

    <div class="tab-content" id="history">
        <p>Lịch sử khám</p>
    </div>
</div>

<script>
function showTab(id) {
    document.querySelectorAll(".tab").forEach(t => t.classList.remove("active"));
    document.querySelectorAll(".tab-content").forEach(c => c.classList.remove("active"));

    document.querySelector(".tab[onclick=\"showTab('" + id + "')\"]").classList.add("active");
    document.getElementById(id).classList.add("active");
}
</script>

</body>
</html>
