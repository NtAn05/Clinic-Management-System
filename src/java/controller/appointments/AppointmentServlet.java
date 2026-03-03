package controller.appointments;

import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import dal.AppointmentDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import model.Appointment;
import model.Doctor;
import model.Patient;

public class AppointmentServlet extends HttpServlet {

    private static final String CLIENT_ID = "e76a6cbb-71b7-40a3-bd89-69c577698cb9";
    private static final String API_KEY = "512e43a7-c663-4519-ab90-6f183569a75d";
    private static final String CHECKSUM_KEY = "370d7efb2d9ce65c36e7b943087d5876090b8664cc64edb9ec7ba9a334ee56c1";
    private static final PayOS payOS = new PayOS(CLIENT_ID, API_KEY, CHECKSUM_KEY);
    private static final String BASE_URL = "http://localhost:8080/PhongKhamDaLieu";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String doctorID = request.getParameter("btnDoctorID");

        AppointmentDAO dao = new AppointmentDAO();
        Doctor doctor = dao.getDoctorById(doctorID);
        request.setAttribute("doctor", doctor);

        request.getRequestDispatcher("/pages/appointments/appointment/appointmentInformation.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Nhận thông tin
        String userID = request.getParameter("userID");
        int useId = Integer.parseInt(userID);

        String doctorID = request.getParameter("doctorID");
        int doctorId = Integer.parseInt(doctorID);

        String name = request.getParameter("name");
        String sdt = request.getParameter("sdt");
        String email = request.getParameter("email");

        String dateofbirth = request.getParameter("dateofbirth");
        LocalDate localDate = LocalDate.parse(dateofbirth);
        java.sql.Date birthDate = java.sql.Date.valueOf(localDate);

        String gender = request.getParameter("gender");
        String address = request.getParameter("address");
        String note = request.getParameter("note");
        String date = request.getParameter("date");
        java.sql.Date sqlDate = java.sql.Date.valueOf(date);
        String time = request.getParameter("time");
        String submit = request.getParameter("btnSubmit");

        // Tạo đối tượng
        AppointmentDAO dao = new AppointmentDAO();
        Doctor doctor = dao.getDoctorById(doctorID);
        request.setAttribute("doctor", doctor);

        Patient patient = new Patient(useId, name, sdt, birthDate, email, gender);
        Appointment appointment = new Appointment(1, doctorId, 1, "online", sqlDate, time, "booked", note);

        // Validate thông tin
        String errorPhone = "";
        String errorEmail = "";
        String status = "";

        if (!checkPhone(sdt)) {
            errorPhone = "Phone must form 0xxx xxx xxx";
        } else if (!checkEmail(email)) {
            errorEmail = "abc@xxx.com";
        } else {
            status = "yes";
        }

        request.setAttribute("errorPhone", errorPhone);
        request.setAttribute("errorEmail", errorEmail);
        request.setAttribute("appointment", appointment);
        request.setAttribute("patient", patient);
        request.setAttribute("address", address);
        
        request.setAttribute("status", status);
        request.setAttribute("time", time);
        request.setAttribute("date", date);

        // ✅ Xử lý step2: tạo đơn thanh toán
        if (submit != null && submit.equalsIgnoreCase("step2")) {

            try {
                String priceStr = request.getParameter("pricePay");

                long amount;
                try {
                    String cleaned = priceStr.trim().replace(",", "");
                    amount = (long) Double.parseDouble(cleaned);
                } catch (NumberFormatException e) {
                    throw new Exception("Giá tiền không đúng định dạng: " + priceStr);
                }

                HttpSession session = request.getSession();
                session.setAttribute("pendingPatient", patient);
                session.setAttribute("pendingAppointment", appointment);
                long orderCode = (System.currentTimeMillis() % 100000000L) * 1000
                        + (long) (Math.random() * 1000);

                CreatePaymentLinkRequest paymentRequest = CreatePaymentLinkRequest.builder()
                        .orderCode(orderCode)
                        .amount(amount)
                        .description( name +" thanh toan"  )
                        .returnUrl("http://localhost:8080/PhongKhamDaLieu/appointmentpaymentservlet")
                        .cancelUrl("http://localhost:8080/PhongKhamDaLieu/pages/appointments/appointment/appointmentFailPayment.jsp")
                        .build();

                CreatePaymentLinkResponse result = payOS.paymentRequests().create(paymentRequest);

                String checkoutUrl = result.getCheckoutUrl();
                response.sendRedirect(checkoutUrl);
                return;

            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("errorPay", "Lỗi thanh toán: " + e.getMessage());
                request.getRequestDispatcher("/pages/appointments/appointment/appointmentPayment.jsp")
                        .forward(request, response);
                return;
            }
        }

        // ✅ Xử lý bước xác nhận thông tin (step1)
        if (status.equals("yes")) {
            request.getRequestDispatcher("/pages/appointments/appointment/appointmentCheck.jsp")
                    .forward(request, response);
        } else {
            request.getRequestDispatcher("/pages/appointments/appointment/appointmentInformation.jsp")
                    .forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

    private boolean checkPhone(String sdt) {
        if (sdt == null) {
            return false;
        }
        return sdt.matches("^0\\d{9}$");
    }

    private boolean checkEmail(String email) {
        if (email == null) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}