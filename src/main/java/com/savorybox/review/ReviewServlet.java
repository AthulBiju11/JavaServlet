package com.savorybox.review;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/review")
public class ReviewServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/cloud_kitchen";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

            // Query to check if a review exists for the order
            String query = "SELECT review_text FROM reviews WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String reviewText = rs.getString("review_text");
                out.print("{\"success\": true, \"reviewExists\": true, \"reviewText\": \"" + reviewText + "\"}");
            } else {
                out.print("{\"success\": true, \"reviewExists\": false}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\": false}");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        final String driver = "com.mysql.cj.jdbc.Driver";

        try {
            Class.forName(driver);

            // Retrieve parameters from the request
            String orderIdParam = request.getParameter("orderId");
            String reviewText = request.getParameter("reviewText");
            if (orderIdParam == null || reviewText == null) {
                out.print("{\"success\": false, \"message\": \"Missing parameters.\"}");
                return;
            }
            int orderId = Integer.parseInt(orderIdParam);

            // Database connection
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Insert the review into the database
            String insertQuery = "INSERT INTO reviews (order_id, review_text) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertQuery);
            stmt.setInt(1, orderId);
            stmt.setString(2, reviewText);
            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                out.print("{\"success\": true}");
            } else {
                out.print("{\"success\": false, \"message\": \"Failed to insert review.\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\": false}");
        }
    }
}