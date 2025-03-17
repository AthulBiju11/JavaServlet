package com.savorybox.admin;


import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/admin/viewReviews")
public class ViewReviewsServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/cloud_kitchen";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        final String driver = "com.mysql.cj.jdbc.Driver";

        try {
            Class.forName(driver);

            // Database connection
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // SQL query to join reviews, orders, and users tables
            String query = "SELECT u.username, r.order_id, r.review_text, r.review_date " +
                    "FROM reviews r " +
                    "JOIN orders o ON r.order_id = o.order_id " +
                    "JOIN users u ON o.user_id = u.user_id";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // Start HTML output
            out.println("<html><head><title>Customer Reviews</title></head><body>");
            out.println("<h2>Customer Reviews</h2>");
            out.println("<table border='1'>");
            out.println("<tr><th>Username</th><th>Order ID</th><th>Review Text</th><th>Review Date</th></tr>");

            // Iterate through the result set and populate the table
            while (rs.next()) {
                String username = rs.getString("username");
                int orderId = rs.getInt("order_id");
                String reviewText = rs.getString("review_text");
                Timestamp reviewDate = rs.getTimestamp("review_date");

                out.println("<tr>");
                out.println("<td>" + username + "</td>");
                out.println("<td>" + orderId + "</td>");
                out.println("<td>" + reviewText + "</td>");
                out.println("<td>" + reviewDate + "</td>");
                out.println("</tr>");
            }



            // End HTML output
            out.println("</table>");
            out.println("</body></html>");

            // Close resources
            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<p>Error fetching reviews.</p>");
        }
    }
}