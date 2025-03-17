package com.savorybox.orders;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/orderHistory")
public class OrderHistoryServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/cloud_kitchen";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        final String driver = "com.mysql.cj.jdbc.Driver";

        try {
            Class.forName(driver);

            // Retrieve the session and get the userId
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                out.print("{\"success\": false, \"message\": \"User not logged in\"}");
                return;
            }
            int userId = (Integer) session.getAttribute("userId");

            // Database connection
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Query to fetch order history
            String query = "SELECT orders.order_id, packages.package_name, orders.diet_type, orders.start_date, orders.order_status " +
                    "FROM orders " +
                    "LEFT JOIN packages ON orders.package_id = packages.package_id " +
                    "WHERE orders.user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            // Prepare JSON response
            List<String> orders = new ArrayList<>();
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String packageName = rs.getString("package_name");
                String dietType = rs.getString("diet_type");
                Date startDate = rs.getDate("start_date");
                String orderStatus = rs.getString("order_status");

                String orderJson = String.format("{\"orderId\": %d, \"packageName\": \"%s\", \"dietType\": \"%s\", \"startDate\": \"%s\", \"orderStatus\": \"%s\"}",
                        orderId, packageName, dietType, startDate.toString(), orderStatus);
                orders.add(orderJson);
            }

            out.print("{\"success\": true, \"orders\": [" + String.join(",", orders) + "]}");

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\": false}");
        }
    }
}