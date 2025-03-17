package com.savorybox.authentication;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/signup")
public class RegisterServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	final String driver = "com.mysql.cj.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost:3306/cloud_kitchen";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "password";

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userRole = request.getParameter("user_role");
		String username = request.getParameter("username");
		String phone = request.getParameter("phone");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String street = request.getParameter("street");
		String city = request.getParameter("city");
		String state = request.getParameter("state");
		String pincode = request.getParameter("pincode");

		Connection connection = null;
		PreparedStatement userStatement = null;
		PreparedStatement addressStatement = null;

		try {

			Class.forName(driver);

			// Establish connection
			connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

			// Insert into users table
			String userSql = "INSERT INTO users (username, email, password, phone, user_role) VALUES (?, ?, ?, ?, ?)";
			userStatement = connection.prepareStatement(userSql, PreparedStatement.RETURN_GENERATED_KEYS);
			userStatement.setString(1, username);
			userStatement.setString(2, email);
			userStatement.setString(3, password);
			userStatement.setString(4, phone);
			userStatement.setString(5, userRole);
			userStatement.executeUpdate(); // This executes the query and stores the userID inside this object

			// Get the generated user_id
			int userId = 0;
			ResultSet rs = userStatement.getGeneratedKeys();
			if (rs.next()) {
				userId = rs.getInt(1);
			}



			// Insert into user_address table
			String addressSql = "INSERT INTO user_address (user_id, street, city, state, pincode) VALUES (?, ?, ?, ?, ?)";
			addressStatement = connection.prepareStatement(addressSql);
			addressStatement.setInt(1, userId);
			addressStatement.setString(2, street);
			addressStatement.setString(3, city);
			addressStatement.setString(4, state);
			addressStatement.setString(5, pincode);
			addressStatement.executeUpdate();

			response.setContentType("text/html");
			response.getWriter().println("<script type=\"text/javascript\">");
			response.getWriter().println("alert('Registration Successful');");
			response.getWriter().println("window.location.href = 'login.html';");
			response.getWriter().println("</script>");

		} catch (SQLException e) {
			e.printStackTrace();
			response.setContentType("text/html");
			response.getWriter().println("<script type=\"text/javascript\">");
			response.getWriter().println("alert('Registration Failed');");
			response.getWriter().println("window.location.href = 'register.html';");
			response.getWriter().println("</script>");
		} catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
			try {
				if (userStatement != null) userStatement.close();
				if (addressStatement != null) addressStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}