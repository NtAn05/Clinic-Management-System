<%@page contentType="text/html" pageEncoding="UTF-8"%>
<style>

    .site-footer {

        margin-top: auto;
        background: linear-gradient(135deg, #0f172a, #1e3a8a);
        color: #dbeafe;
        width: 100%;
        box-sizing: border-box;
    }

    .footer-inner {
        max-width: 1200px;
        margin: 0 auto;
        padding: 40px 20px 30px;
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        gap: 30px;
    }

    .footer-brand {
        flex: 1;
        min-width: 300px;
        max-width: 500px;
    }

    .footer-brand h4 {
        margin: 0 0 12px;
        color: #ffffff;
        font-size: 22px;
        font-weight: 700;
    }

    .footer-brand p {
        margin: 0;
        line-height: 1.6;
        font-size: 15px;
        color: #bfdbfe;
    }

    .footer-contact {
        flex: 1;
        min-width: 280px;
        text-align: right;
        font-size: 15px;
        line-height: 1.8;
        color: #dbeafe;
    }

    .footer-contact strong {
        color: #ffffff;
    }

    .footer-copy {
        border-top: 1px solid rgba(191, 219, 254, 0.25);
        text-align: center;
        padding: 15px 20px;
        font-size: 13px;
        color: #bfdbfe;
        letter-spacing: 0.5px;
    }
    @media (max-width: 768px) {
        .footer-contact {
            text-align: left;
        }
    }
</style>

<footer class="site-footer">
    <div class="footer-inner">
        <div class="footer-brand">
            <h4>Hệ thống Quản lý Phòng khám ABC</h4>
            <p>
                Nền tảng hỗ trợ quản lý lịch khám, bác sĩ và hồ sơ bệnh nhân nhanh chóng,
                an toàn, chuyên nghiệp. Phục vụ 24/7.
            </p>
        </div>
        <div class="footer-contact">
            <div><strong>Hotline:</strong> 1900 1234</div>
            <div><strong>Email:</strong> support@phongkhamabc.vn</div>
            <div><strong>Địa chỉ:</strong> 123 Đường Da Liễu, TP. Hà Nội</div>
        </div>
    </div>
    <div class="footer-copy">Copyright &copy; 2026 - Hệ thống Phòng Khám ABC</div>
</footer>