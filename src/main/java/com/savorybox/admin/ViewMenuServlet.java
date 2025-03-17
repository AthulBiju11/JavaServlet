package com.savorybox.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/admin/viewMenu")
public class ViewMenuServlet extends HttpServlet {

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

            String query = "SELECT * FROM weekly_menu";

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            out.println("<html><head><title>Food Menu Items</title></head><body>");
            out.println("<h2>Weekly Menu</h2>");
            out.println("<table border='1'>");
            out.print("<tr><th>Day</th><th>Meal Type</th><th>Category</th><th>Description</th><th>Action</th></tr>");

            while (rs.next()) {
                String day = rs.getString("day_of_week");
                String mealType = rs.getString("meal_type");
                String category = rs.getString("category");
                String description = rs.getString("description");
                Integer id = rs.getInt("menu_id");

                out.print("<tr><td>" + day + "</td><td>" + mealType + "</td><td>" + category + "</td><td>" + description
                        + "</td>");
                out.print("<td><button onclick=\"updateMenu(" + id + ")\">Update</button></td>");
                out.println("</tr>");
            }

            out.println("</table>");
            out.print("<div id='updateForm' style='display:none;'>");
            out.print("<h3>Update Menu Description</h3>");
            out.print("<form id='menuUpdateForm'>");
            out.print("<input type='hidden' id='menuId' name='menuId'>");
            out.print("New Description: <input type='text' id='newDescription' name='newDescription'><br>");
            out.print("<button type='button' onclick='submitUpdate()'>Submit</button>");
            out.print("</form>");
            out.print("</div>");

            out.print("<script>");

            out.print("function updateMenu(menuId) {");
            out.print("  document.getElementById('menuId').value = menuId;");
            out.print("  document.getElementById('updateForm').style.display = 'block';");
            out.print("}");

            out.print("function submitUpdate() {");
            out.print("  const menuId = document.getElementById('menuId').value;");
            out.print("  const newDescription = document.getElementById('newDescription').value;");
            out.print("  fetch('/my-webapp-1.0-SNAPSHOT/admin/updateMenu', {");
            out.print("    method: 'POST',");
            out.print("    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },");
            out.print("    body: 'menuId=' + menuId + '&newDescription=' + encodeURIComponent(newDescription)");
            out.print("  }).then(response => {");
            out.print("    if (response.ok) {");
            out.print("      alert('Menu updated successfully.');");
            out.print("      location.reload();");
            out.print("    } else {");
            out.print("      alert('Failed to update menu.');");
            out.print("    }");
            out.print("  });");
            out.print("}");
            out.print("</script>");

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