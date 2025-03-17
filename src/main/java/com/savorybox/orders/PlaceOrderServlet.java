package com.savorybox.orders;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/placeOrder")
public class PlaceOrderServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/cloud_kitchen";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        final String driver = "com.mysql.cj.jdbc.Driver";

        try {
            Class.forName(driver);
            String packageId = request.getParameter("packageId"); // Assuming packageId is sent from the frontend
            String dietType = request.getParameter("dietType");
            String startDate = request.getParameter("startDate");
            double amount = Double.parseDouble(request.getParameter("amount"));
            String transactionId = request.getParameter("transactionId");

            // Retrieve the session and get the userId
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                out.print("{\"success\": false, \"message\": \"User not logged in\"}");
                return;
            }
            int userId = (Integer) session.getAttribute("userId");

            // Database connection
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Insert into orders table
            String orderQuery = "INSERT INTO orders (user_id, package_id, diet_type, start_date, order_status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement orderStmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, userId); // Use the userId from the session
            orderStmt.setInt(2, Integer.parseInt(packageId)); // Use the packageId from the request
            orderStmt.setString(3, dietType);
            orderStmt.setDate(4, Date.valueOf(startDate));
            orderStmt.setString(5, "Active");
            orderStmt.executeUpdate();

            ResultSet rs = orderStmt.getGeneratedKeys();
            int orderId = 0;
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            // Insert into payments table
            String paymentQuery = "INSERT INTO payments (order_id, amount, payment_status, transaction_id) VALUES (?, ?, ?, ?)";
            PreparedStatement paymentStmt = conn.prepareStatement(paymentQuery);
            paymentStmt.setInt(1, orderId);
            paymentStmt.setDouble(2, amount);
            paymentStmt.setString(3, "Successful");
            paymentStmt.setString(4, transactionId);
            paymentStmt.executeUpdate();

            out.print("{\"success\": true}");

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\": false}");
        }
    }
}