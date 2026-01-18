package controller;

import dal.UserDAO;
import model.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "AdminUserServlet", urlPatterns = {"/admin-users"})
public class AdminUserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserDAO dao = new UserDAO();
        List<User> listDoctors = dao.getUsersByRole(2);
        List<User> listStaff = dao.getUsersByRole(3);

        request.setAttribute("doctors", listDoctors);
        request.setAttribute("staffs", listStaff);
        request.getRequestDispatcher("admin-users.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        UserDAO dao = new UserDAO();

        if ("add".equals(action)) {
            try {
                String name = request.getParameter("fullname");
                String phone = request.getParameter("phone");
                String pass = request.getParameter("password");
                String roleRaw = request.getParameter("role");
                
                // Mặc định là Nhân viên (3) nếu lỗi
                int role = 3; 
                if (roleRaw != null && !roleRaw.trim().isEmpty()) {
                    try {
                        role = Integer.parseInt(roleRaw);
                    } catch (NumberFormatException e) {
                        System.out.println("Lỗi parse role: " + e.getMessage());
                    }
                }
                
                // Kiểm tra trùng SĐT trước khi thêm
                if (dao.isPhoneExist(phone)) {
                    // Nếu trùng thì thôi không thêm, reload lại trang (hoặc báo lỗi nếu muốn)
                    response.sendRedirect("admin-users");
                    return;
                }

                // Tạo đối tượng User
                // Lưu ý: Các trường không nhập thì để null hoặc chuỗi rỗng
                User u = new User();
                u.setFullName(name);
                u.setPhone(phone);
                u.setPassword(pass);
                u.setGender("Khác"); // Mặc định
                u.setRoleId(role);   // Quan trọng: Set RoleID
                u.setStatus(true);   // Quan trọng: Set Status
                
                // Các trường này admin form không nhập nên để null
                // DB phải cho phép NULL ở các cột DOB, Email, Address
                u.setDob(null); 
                u.setEmail(null);
                u.setAddress(null);

                dao.register(u);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } 
        else if ("delete".equals(action)) {
            String phone = request.getParameter("phone");
            dao.deleteUser(phone);
        }
        
        response.sendRedirect("admin-users");
    }
}