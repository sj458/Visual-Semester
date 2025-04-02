package com.visualsemester.database;

import com.visualsemester.model.Task;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
	private static final String DB_URL = "jdbc:sqlite:tasks.db";

	public DatabaseHandler() {
		createTable();
	}

	// Create the tasks table if it doesn't exist
	private void createTable() {
		String sql = "CREATE TABLE IF NOT EXISTS tasks (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "name TEXT NOT NULL, " + "dueDate TEXT NOT NULL)";

		try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			System.err.println("Error creating table: " + e.getMessage());
		}
	}

	// Add a task to the database
	public void addTask(Task task) {
		String sql = "INSERT INTO tasks(name, dueDate) VALUES(?, ?)";
		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, task.getName());
			pstmt.setString(2, task.getDueDate().toString());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Error adding task: " + e.getMessage());
		}
	}

	// Load all tasks from the database
	public List<Task> loadTasks() {
		List<Task> tasks = new ArrayList<>();
		String sql = "SELECT * FROM tasks";

		try (Connection conn = DriverManager.getConnection(DB_URL);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				tasks.add(new Task(rs.getInt("id"), rs.getString("name"), LocalDate.parse(rs.getString("dueDate"))));
			}
		} catch (SQLException e) {
			System.err.println("Error loading tasks: " + e.getMessage());
		}
		return tasks;
	}

	// Delete a task from the database
	public void deleteTask(int id) {
		String sql = "DELETE FROM tasks WHERE id = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Error deleting task: " + e.getMessage());
		}
	}

	// Update a task in the database
	public boolean updateTask(int id, String newName, LocalDate newDueDate) {
	    String sql = "UPDATE tasks SET name = ?, dueDate = ? WHERE id = ?";
	    try (Connection conn = DriverManager.getConnection(DB_URL);
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        
	        pstmt.setString(1, newName);
	        pstmt.setString(2, newDueDate.toString());
	        pstmt.setInt(3, id);
	        return pstmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        System.err.println("Error updating task: " + e.getMessage());
	        return false;
	    }
	}
}