package com.savorybox.packages;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/getPackages")
public class GetPackagesServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/cloud_kitchen";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        final String driver = "com.mysql.cj.jdbc.Driver";

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "SELECT * FROM packages";
            statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            List<String> packages = new ArrayList<>();
            while (resultSet.next()) {
                String pkg = String.format(
                        "{\"package_id\": %d, \"package_name\": \"%s\", \"description\": \"%s\", \"price\": %.2f, \"duration\": %d}",
                        resultSet.getInt("package_id"),
                        resultSet.getString("package_name"),
                        resultSet.getString("description"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("duration")
                );
                packages.add(pkg);
            }
            out.println("[" + String.join(",", packages) + "]");

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("[]");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}