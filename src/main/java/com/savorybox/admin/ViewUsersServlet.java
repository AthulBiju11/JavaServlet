package com.savorybox.admin;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/admin/viewUsers")
public class ViewUsersServlet extends HttpServlet {

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

            // SQL query to join users and user_address tables
            String query = "SELECT u.user_id, u.username, u.email, u.phone, u.user_role, a.street, a.city, a.state, a.pincode " +
                    "FROM users u " +
                    "LEFT JOIN user_address a ON u.user_id = a.user_id";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("Sample printing out");

            // Start HTML output
            out.println("<html><head><title>User List</title></head><body>");
            out.println("<h2>User List</h2>");
            out.println("<table border='1'>");
            out.println("<tr><th>Username</th><th>Email</th><th>Phone</th><th>Role</th><th>Street</th><th>City</th><th>State</th><th>Pincode</th><th>Action</th></tr>");

            // Iterate through the result set and populate the table
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String role = rs.getString("user_role");
                String street = rs.getString("street");
                String city = rs.getString("city");
                String state = rs.getString("state");
                String pincode = rs.getString("pincode");

                out.println("<tr>");
                out.println("<td>" + username + "</td>");
                out.println("<td>" + email + "</td>");
                out.println("<td>" + phone + "</td>");
                out.println("<td>" + role + "</td>");
                out.println("<td>" + street + "</td>");
                out.println("<td>" + city + "</td>");
                out.println("<td>" + state + "</td>");
                out.println("<td>" + pincode + "</td>");
                out.println("<td><button onclick=\"deleteUser(" + userId + ")\">Delete</button></td>");
                out.println("</tr>");
            }

            // End HTML output
            out.println("</table>");
            out.println("<script>");
            out.println("function deleteUser(userId) {");
            out.println("  if (confirm('Are you sure you want to delete this user?')) {");
            out.println("    fetch('/my-webapp-1.0-SNAPSHOT/admin/deleteUser', {");
            out.println("      method: 'POST',");
            out.println("      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },");
            out.println("      body: 'userId=' + userId");
            out.println("    }).then(response => {");
            out.println("      if (response.ok) {");
            out.println("        alert('User deleted successfully.');");
            out.println("        location.reload();");
            out.println("      } else {");
            out.println("        alert('Failed to delete user.');");
            out.println("      }");
            out.println("    });");
            out.println("  }");
            out.println("}");
            out.println("</script>");
            out.println("</body></html>");

            // Close resources
            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<p>Error fetching user data.</p>");
        }
    }
}