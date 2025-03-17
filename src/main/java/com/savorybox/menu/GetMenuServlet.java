package com.savorybox.menu;

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

@WebServlet("/getMenu")
public class GetMenuServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/cloud_kitchen";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";
    final String driver = "com.mysql.cj.jdbc.Driver";


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "SELECT * FROM weekly_menu";
            statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            List<String> menuItems = new ArrayList<>();
            while (resultSet.next()) {
                String item = String.format(
                        "{\"day_of_week\": \"%s\", \"meal_type\": \"%s\", \"category\": \"%s\", \"description\": \"%s\"}",
                        resultSet.getString("day_of_week"),
                        resultSet.getString("meal_type"),
                        resultSet.getString("category"),
                        resultSet.getString("description")
                );
                menuItems.add(item);
            }
            out.println("[" + String.join(",", menuItems) + "]");

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            out.println("[]");
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