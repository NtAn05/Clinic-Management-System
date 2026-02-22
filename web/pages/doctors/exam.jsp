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
                <div>
                    <p class="kicker">Hồ sơ khám</p>
                    <h2>Phiên khám bệnh</h2>
                </div>
                <div class="actions">
                    <button class="btn-outline" onclick="history.back()">← Quay lại</button>
                    <button class="btn-primary">💾 Lưu</button>
                    <button class="btn-success">✔ Hoàn thành</button>
                </div>
            </div>

            <div class="tabs">
                <button class="tab active" data-target="info" onclick="showTab('info')">Thông tin</button>
                <button class="tab" data-target="lab" onclick="showTab('lab')">Kết quả XN</button>
                <button class="tab" data-target="prescription" onclick="showTab('prescription')">Đơn thuốc</button>
                <button class="tab" data-target="history" onclick="showTab('history')">Lịch sử</button>
            </div>

            <div class="tab-content active" id="info">
                <div class="card-grid">
                    <section class="card">
                        <h3>Chỉ số sinh tồn</h3>
                        <div class="grid">
                            <input placeholder="Huyết áp">
                            <input placeholder="Nhiệt độ">
                            <input placeholder="Nhịp tim">
                            <input placeholder="Nhịp thở">
                        </div>
                    </section>

                    <section class="card">
                        <h3>Đánh giá lâm sàng</h3>
                        <textarea rows="4" placeholder="Nhập triệu chứng..."></textarea>
                        <textarea rows="4" placeholder="Chẩn đoán sơ bộ..."></textarea>
                    </section>
                </div>
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

        <button class="floating-panel-toggle" type="button" onclick="toggleQuickPanel()">☰ Bảng điều khiển</button>

        <div id="quickPanelBackdrop" class="quick-backdrop" onclick="toggleQuickPanel(false)"></div>
        <aside id="quickPanel" class="quick-panel" aria-hidden="true">
            <div class="quick-panel-header">
                <h4>Bảng điều khiển nhanh</h4>
                <button class="btn-close" type="button" onclick="toggleQuickPanel(false)">✕</button>
            </div>
            <div class="quick-actions">
                <button type="button">🧾 Mẫu ghi chú</button>
                <button type="button">🧪 Tạo chỉ định XN</button>
                <button type="button">📌 Đánh dấu tái khám</button>
                <button type="button">🖨 In phiếu tóm tắt</button>
            </div>
        </aside>

        <script>
            function showTab(id) {
                document.querySelectorAll('.tab').forEach(t => {
                    t.classList.toggle('active', t.dataset.target === id);
                });
                document.querySelectorAll('.tab-content').forEach(c => {
                    c.classList.toggle('active', c.id === id);
                });
            }

            function toggleQuickPanel(force) {
                const panel = document.getElementById('quickPanel');
                const backdrop = document.getElementById('quickPanelBackdrop');
                const shouldOpen = typeof force === 'boolean' ? force : !panel.classList.contains('open');

                panel.classList.toggle('open', shouldOpen);
                backdrop.classList.toggle('open', shouldOpen);
                panel.setAttribute('aria-hidden', shouldOpen ? 'false' : 'true');
            }

            document.addEventListener('keydown', function (e) {
                if (e.key === 'Escape') {
                    toggleQuickPanel(false);
                }
            });
        </script>

    </body>
</html>