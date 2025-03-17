package com.savorybox.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/admin/updatePackage")
public class UpdatePackageServlet extends HttpServlet {

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
            String packageIdParam = request.getParameter("packageId");
            String packageName = request.getParameter("packageName");
            String description = request.getParameter("description");
            String priceParam = request.getParameter("price");
            String durationParam = request.getParameter("duration");

            if (packageIdParam == null || packageName == null || description == null || priceParam == null || durationParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("Missing package details.");
                return;
            }

            int packageId = Integer.parseInt(packageIdParam);
            double price = Double.parseDouble(priceParam);
            int duration = Integer.parseInt(durationParam);

            packageName = packageName.trim(); // Trim the package name
            description = description.trim(); // Trim the description

            // Database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Update the package details
            String updateQuery = "UPDATE packages SET package_name = ?, description = ?, price = ?, duration = ? WHERE package_id = ?";
            PreparedStatement statement = connection.prepareStatement(updateQuery);
            statement.setString(1, packageName);
            statement.setString(2, description);
            statement.setDouble(3, price);
            statement.setInt(4, duration);
            statement.setInt(5, packageId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("Package updated successfully.");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("Package not found.");
            }

            // Close resources
            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("Error updating package.");
        }
    }
}