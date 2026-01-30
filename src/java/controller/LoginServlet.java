package controller;

import dal.UserDAO;
import model.User;
import java.io.IOException;
import java.lang.reflect.Method;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    /** Lấy tên role từ User (hỗ trợ cả getRole() và getRoleId() để tương thích nhiều bản build). */
    private static String getRoleName(User u) {
        if (u == null) return "";
        try {
            Method m = u.getClass().getMethod("getRole");
            Object r = m.invoke(u);
            return r != null ? r.toString().toLowerCase() : "";
        } catch (NoSuchMethodException e) {
            try {
                Method m = u.getClass().getMethod("getRoleId");
                Object id = m.invoke(u);
                if (id == null) return "";
                int roleId = id instanceof Integer ? (Integer) id : ((Number) id).intValue();
                String[] names = { "admin", "patient", "doctor", "receptionist", "technician" };
                return roleId >= 0 && roleId < names.length ? names[roleId] : "";
            } catch (Exception e2) {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    /** Lấy tên status từ User (hỗ trợ getStatus() hoặc mặc định active). */
    private static String getStatusName(User u) {
        if (u == null) return "inactive";
        try {
            Method m = u.getClass().getMethod("getStatus");
            Object s = m.invoke(u);
            return s != null ? s.toString().toLowerCase() : "inactive";
        } catch (Exception e) {
            return "inactive";
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/pages/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String phone = request.getParameter("phone");
        String pass = request.getParameter("password");
        String selectedRole = request.getParameter("role");

        UserDAO dao = new UserDAO();
        User u = dao.checkLogin(phone, pass);
        String error = null;

        if (u == null) {
            error = "Sai thông tin đăng nhập!";
        } else {
            String roleName = getRoleName(u);
            String status = getStatusName(u);

            if ("staff".equals(selectedRole)) {
                if ("patient".equals(roleName)) {
                    error = "Bệnh nhân vui lòng sang tab Bệnh nhân!";
                }
            } else if ("patient".equals(selectedRole)) {
                if (!"patient".equals(roleName)) {
                    error = "Nhân viên vui lòng sang tab Nhân viên!";
                }
            }

            if (!"active".equals(status)) {
                error = "Tài khoản bị khóa!";
            }
        }

        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("phone", phone);
            request.getRequestDispatcher("/pages/auth/login.jsp").forward(request, response);
        } else {
            HttpSession session = request.getSession();
            session.setAttribute("account", u);

            String roleName = getRoleName(u);
            if ("admin".equals(roleName)) {
                response.sendRedirect(request.getContextPath() + "/admin-dashboard.jsp");
            } else if ("technician".equals(roleName)) {
                response.sendRedirect(request.getContextPath() + "/technician-dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/pages/home/home.jsp");
            }
        }
    }
}