package com.savorybox.admin;


import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/admin/deleteUser")
public class DeleteUserServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/cloud_kitchen";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        final String driver = "com.mysql.cj.jdbc.Driver";

        try {
            Class.forName(driver);

            // Retrieve the userId from the request parameters
            String userIdParam = request.getParameter("userId");
            if (userIdParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("User ID is missing.");
                return;
            }

            System.out.println("The userID we are trying to delete is :" + userIdParam);
            int userId = Integer.parseInt(userIdParam);

            // Database connection
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Delete the user from the database
            String deleteQuery = "DELETE FROM users WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(deleteQuery);
            stmt.setInt(1, userId);
            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("User deleted successfully.");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("User not found.");
            }

            // Close resources
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("Error deleting user.");
        }
    }
}