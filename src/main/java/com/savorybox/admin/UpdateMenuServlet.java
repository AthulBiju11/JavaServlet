package com.savorybox.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/admin/updateMenu")
public class UpdateMenuServlet extends HttpServlet {

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

            // Retrieve and trim parameters from the request
            String menuIdParam = request.getParameter("menuId");
            String newDescription = request.getParameter("newDescription");

            if (menuIdParam == null || newDescription == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("Menu ID or new description is missing.");
                return;
            }

            int menuId = Integer.parseInt(menuIdParam);
            newDescription = newDescription.trim(); // Trim the description

            // Database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Update the menu description
            String updateQuery = "UPDATE weekly_menu SET description = ? WHERE menu_id = ?";
            PreparedStatement statement = connection.prepareStatement(updateQuery);
            statement.setString(1, newDescription);
            statement.setInt(2, menuId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("Menu updated successfully.");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("Menu item not found.");
            }

            // Close resources
            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("Error updating menu.");
        }
    }
}