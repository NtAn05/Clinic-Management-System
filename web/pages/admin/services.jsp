<%-- 
    Document   : services
    Created on : Feb 1, 2026, 11:10:19 PM
    Author     : admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý giá dịch vụ</title>
        <style>
            table {
                border-collapse: collapse;
                width: 100%;
            }
            th, td {
                border: 1px solid #ccc;
                padding: 8px;
                text-align: left;
            }
            th {
                background: #f2f2f2;
            }
            form {
                margin: 0;
            }
            .add-box {
                margin-bottom: 20px;
                padding: 12px;
                border: 1px solid #ccc;
                background: #fafafa;
            }
            button {
                padding: 4px 10px;
            }
        </style>
    </head>
    <body>
        <h2>Quản lý giá dịch vụ</h2>
        <!--them dich vu-->
        <div class="add-box">
            <h3>Thêm dịch vụ</h3>
            <form method="post" action="${pageContext.request.contextPath}/admin/services">
                <input type="hidden" name="action" value="add"/>

                <label>Tên dịch vụ</label>
                <input type="text" name="name" required/>

                <label>Giá:</label>
                <input type="number" name="price" min="0" required/>
                <button type="submit">Thêm</button>
            </form>
        </div>

        <!--danh sach dich vu-->
        <table>
            <tr>
                <th>Tên dịch vụ</th>
                <th>Danh mục</th>
                <th>Giá</th>
                <th colspan="2">Thao tác</th>
            </tr>

            <c:forEach var="s" items="${services}">
                <tr>
                    <!--form sua-->
                <form method="post" action="${pageContext.request.contextPath}/admin/services">
                    <td>
                        <input type="text" name="name" value="${s.name}" required/>
                    </td>
                    <td>
                        <input type="text" name="serviceType" value="${s.serviceType}" required/>
                    </td>
                    <td>
                        <input type="number" name="price" value="${s.price}" min="0" required/>
                    </td>
                    <td>
                        <input type="hidden" name="serviceId" value="${s.serviceId}"/>
                        <input type="hidden" name="action" value="update"/>
                        <button type="submit">Lưu</button>
                    </td>
                </form>

                <!--form xoa-->
                <td>
                    <form method="post" action="${pageContext.request.contextPath}/admin/services"
                          onsubmit="return confirm('Bạn có muốn xóa dịch vụ này không?');">
                        <input type="hidden" name="serviceId" value="${s.serviceId}"/>
                        <input type="hidden" name="action" value="delete"/>
                        <button type="submit">Xóa</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>
