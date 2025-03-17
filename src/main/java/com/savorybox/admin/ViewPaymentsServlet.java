package com.savorybox.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/admin/viewPayments")
public class ViewPaymentsServlet extends HttpServlet {

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

            // SQL query to join payments, orders, and users tables
            String query = "SELECT p.payment_id, p.order_id, p.payment_date, p.amount, p.payment_status, p.transaction_id, " +
                    "u.username, u.phone " +
                    "FROM payments p " +
                    "JOIN orders o ON p.order_id = o.order_id " +
                    "JOIN users u ON o.user_id = u.user_id";

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            out.println("<html><head><title>Payment Transactions</title></head><body>");
            out.println("<h2>Payment Transactions</h2>");
            out.println("<table border='1'>");
            out.print("<tr><th>Payment ID</th><th>Order ID</th><th>Payment Date</th><th>Amount</th><th>Status</th><th>Transaction ID</th><th>Username</th><th>Phone</th></tr>");

            while (rs.next()) {
                int paymentId = rs.getInt("payment_id");
                int orderId = rs.getInt("order_id");
                Timestamp paymentDate = rs.getTimestamp("payment_date");
                double amount = rs.getDouble("amount");
                String paymentStatus = rs.getString("payment_status");
                String transactionId = rs.getString("transaction_id");
                String username = rs.getString("username");
                String phone = rs.getString("phone");

                out.print("<tr>");
                out.print("<td>" + paymentId + "</td>");
                out.print("<td>" + orderId + "</td>");
                out.print("<td>" + paymentDate + "</td>");
                out.print("<td>" + amount + "</td>");
                out.print("<td>" + paymentStatus + "</td>");
                out.print("<td>" + transactionId + "</td>");
                out.print("<td>" + username + "</td>");
                out.print("<td>" + phone + "</td>");
                out.print("</tr>");
            }

            out.println("</table>");
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