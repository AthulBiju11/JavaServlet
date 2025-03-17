package com.savorybox.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/admin/viewPackages")
public class ViewPackageServlet extends HttpServlet {

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

            String query = "SELECT * FROM packages";

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            out.println("<html><head><title>Package Details</title></head><body>");
            out.println("<h2>Package Details</h2>");
            out.println("<table border='1'>");
            out.print("<tr><th>Package ID</th><th>Package Name</th><th>Description</th><th>Price</th><th>Duration</th><th>Action</th></tr>");

            while (rs.next()) {
                int packageId = rs.getInt("package_id");
                String packageName = rs.getString("package_name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                int duration = rs.getInt("duration");

                out.print("<tr>");
                out.print("<td>" + packageId + "</td>");
                out.print("<td>" + packageName + "</td>");
                out.print("<td>" + description + "</td>");
                out.print("<td>" + price + "</td>");
                out.print("<td>" + duration + "</td>");
                out.print("<td><button onclick=\"updatePackage(" + packageId + ")\">Update</button></td>");
                out.print("</tr>");
            }

            out.println("</table>");
            out.print("<div id='updateForm' style='display:none;'>");
            out.print("<h3>Update Package Details</h3>");
            out.print("<form id='packageUpdateForm'>");
            out.print("<input type='hidden' id='packageId' name='packageId'>");
            out.print("Package Name: <input type='text' id='packageName' name='packageName'><br>");
            out.print("Description: <input type='text' id='description' name='description'><br>");
            out.print("Price: <input type='text' id='price' name='price'><br>");
            out.print("Duration: <input type='text' id='duration' name='duration'><br>");
            out.print("<button type='button' onclick='submitUpdate()'>Submit</button>");
            out.print("</form>");
            out.print("</div>");

            out.print("<script>");
            out.print("function updatePackage(packageId) {");
            out.print("  document.getElementById('packageId').value = packageId;");
            out.print("  document.getElementById('updateForm').style.display = 'block';");
            out.print("}");
            out.print("function submitUpdate() {");
            out.print("  const packageId = document.getElementById('packageId').value;");
            out.print("  const packageName = document.getElementById('packageName').value;");
            out.print("  const description = document.getElementById('description').value;");
            out.print("  const price = document.getElementById('price').value;");
            out.print("  const duration = document.getElementById('duration').value;");
            out.print("  fetch('/my-webapp-1.0-SNAPSHOT/admin/updatePackage', {");
            out.print("    method: 'POST',");
            out.print("    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },");
            out.print("    body: 'packageId=' + packageId + '&packageName=' + encodeURIComponent(packageName) + '&description=' + encodeURIComponent(description) + '&price=' + price + '&duration=' + duration");
            out.print("  }).then(response => {");
            out.print("    if (response.ok) {");
            out.print("      alert('Package updated successfully.');");
            out.print("      location.reload();");
            out.print("    } else {");
            out.print("      alert('Failed to update package.');");
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