package com.savorybox.orders;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/cancelOrder")
public class CancelOrderServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/cloud_kitchen";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        final String driver = "com.mysql.cj.jdbc.Driver";

        try {
            Class.forName(driver);

            // Retrieve the orderId from the request parameters
            String orderIdParam = request.getParameter("orderId");
            if (orderIdParam == null) {
                out.print("{\"success\": false, \"message\": \"Order ID is missing.\"}");
                return;
            }
            int orderId = Integer.parseInt(orderIdParam);

            // Database connection
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Update the order status to 'Cancelled'
            String updateQuery = "UPDATE orders SET order_status = 'Cancelled' WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(updateQuery);
            stmt.setInt(1, orderId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                out.print("{\"success\": true}");
            } else {
                out.print("{\"success\": false, \"message\": \"Order not found or already cancelled.\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\": false}");
        }
    }
}