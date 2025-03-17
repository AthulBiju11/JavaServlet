package com.savorybox.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/admin/viewOrders")
public class ViewOrderServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/cloud_kitchen";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        final String driver = "com.mysql.cj.jdbc.Driver";

        PrintWriter out = response.getWriter();

        try {
            Class.forName(driver);

            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // SQL query to join orders, users, and packages tables
            String query = "SELECT o.order_id, o.diet_type, o.start_date, o.order_status, " +
                    "u.username, u.phone, p.package_name " +
                    "FROM orders o " +
                    "JOIN users u ON o.user_id = u.user_id " +
                    "JOIN packages p ON o.package_id = p.package_id";

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            out.println("<html><head><title>Order Details</title></head><body>");
            out.println("<h2>Order Details</h2>");
            out.println("<table border='1'>");
            out.print("<tr><th>Order ID</th><th>Username</th><th>Phone</th><th>Package Name</th><th>Diet Type</th><th>Start Date</th><th>Status</th><th>Action</th></tr>");

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String username = rs.getString("username");
                String phone = rs.getString("phone");
                String packageName = rs.getString("package_name");
                String dietType = rs.getString("diet_type");
                Date startDate = rs.getDate("start_date");
                String orderStatus = rs.getString("order_status");

                out.print("<tr>");
                out.print("<td>" + orderId + "</td>");
                out.print("<td>" + username + "</td>");
                out.print("<td>" + phone + "</td>");
                out.print("<td>" + packageName + "</td>");
                out.print("<td>" + dietType + "</td>");
                out.print("<td>" + startDate + "</td>");
                out.print("<td>" + orderStatus + "</td>");
                out.print("<td><button onclick=\"updateOrder(" + orderId + ")\">Update</button></td>");
                out.print("</tr>");
            }

            out.println("</table>");
            out.print("<div id='updateForm' style='display:none;'>");
            out.print("<h3>Update Order Status</h3>");
            out.print("<form id='orderUpdateForm'>");
            out.print("<input type='hidden' id='orderId' name='orderId'>");
            out.print("Order Status: <select id='orderStatus' name='orderStatus'>");
            out.print("<option value='Active'>Active</option>");
            out.print("<option value='Completed'>Completed</option>");
            out.print("<option value='Cancelled'>Cancelled</option>");
            out.print("</select><br>");
            out.print("<button type='button' onclick='submitUpdate()'>Submit</button>");
            out.print("</form>");
            out.print("</div>");

            out.print("<script>");
            out.print("function updateOrder(orderId) {");
            out.print("  document.getElementById('orderId').value = orderId;");
            out.print("  document.getElementById('updateForm').style.display = 'block';");
            out.print("}");
            out.print("function submitUpdate() {");
            out.print("  const orderId = document.getElementById('orderId').value;");
            out.print("  const orderStatus = document.getElementById('orderStatus').value;");
            out.print("  fetch('/my-webapp-1.0-SNAPSHOT/admin/updateOrder', {");
            out.print("    method: 'POST',");
            out.print("    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },");
            out.print("    body: 'orderId=' + orderId + '&orderStatus=' + orderStatus");
            out.print("  }).then(response => {");
            out.print("    if (response.ok) {");
            out.print("      alert('Order updated successfully.');");
            out.print("      location.reload();");
            out.print("    } else {");
            out.print("      alert('Failed to update order.');");
            out.print("    }");
            out.print("  });");
            out.print("}");
            out.print("</script>");

            out.println("</body></html>");

            rs.close();
            statement.close();
            connection.close();

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}