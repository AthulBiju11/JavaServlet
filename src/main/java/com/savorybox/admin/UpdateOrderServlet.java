package com.savorybox.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/admin/updateOrder")
public class UpdateOrderServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/cloud_kitchen";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        final String driver = "com.mysql.cj.jdbc.Driver";

        try {
            Class.forName(driver);

            // Retrieve parameters from the request
            String orderIdParam = request.getParameter("orderId");
            String orderStatus = request.getParameter("orderStatus");

            if (orderIdParam == null || orderStatus == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("Missing order details.");
                return;
            }

            int orderId = Integer.parseInt(orderIdParam);

            // Database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Update the order status
            String updateQuery = "UPDATE orders SET order_status = ? WHERE order_id = ?";
            PreparedStatement statement = connection.prepareStatement(updateQuery);
            statement.setString(1, orderStatus);
            statement.setInt(2, orderId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("Order updated successfully.");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("Order not found.");
            }

            // Close resources
            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("Error updating order.");
        }
    }
}