package com.savorybox.authentication;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/signin")
public class LoginServlet extends HttpServlet {

	final String driver = "com.mysql.cj.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost:3306/cloud_kitchen";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "password";

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("email");
		String password = request.getParameter("password");

		Connection connection = null;
		PreparedStatement statement = null;

		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

			String sql = "SELECT user_id, user_role FROM users WHERE email = ? AND password = ?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, email);
			statement.setString(2, password);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				int userId = resultSet.getInt("user_id");
				String userRole = resultSet.getString("user_role");

				HttpSession session = request.getSession();
				session.setAttribute("userId", userId);
				session.setAttribute("userRole", userRole);

				response.setContentType("text/html");
				response.getWriter().println("<script type=\"text/javascript\">");
				if ("ADMIN".equalsIgnoreCase(userRole)) {
					response.getWriter().println("alert('Admin Login Successful');");
					response.getWriter().println("window.location.href = 'adminPage.html';");
				} else {
					response.getWriter().println("alert('Login Successful');");
					response.getWriter().println("window.location.href = 'landing.html';");
				}
				response.getWriter().println("</script>");
			} else {
				response.setContentType("text/html");
				response.getWriter().println("<script type=\"text/javascript\">");
				response.getWriter().println("alert('Invalid email or password');");
				response.getWriter().println("window.location.href = 'login.html';");
				response.getWriter().println("</script>");
			}

		} catch (SQLException e) {
			e.printStackTrace();
			response.getWriter().println("<h1>Error during login</h1>");
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