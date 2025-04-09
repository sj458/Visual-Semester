package com.visualsemester.database;

import com.visualsemester.model.Task;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
	private static final String DB_URL = "jdbc:sqlite:tasks.db";

	public DatabaseHandler() {
		createTable();
	}

	private void createTable() {
		String sql = "CREATE TABLE IF NOT EXISTS tasks (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "name TEXT NOT NULL, " + "dueDate TEXT NOT NULL, " + "type TEXT NOT NULL, "
				+ "className TEXT NOT NULL, " + "hasReminder BOOLEAN NOT NULL DEFAULT 0, " + "reminderTime TEXT)";

		try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			System.err.println("Error creating table: " + e.getMessage());
		}
	}

	public void addTask(Task task) {
		String sql = "INSERT INTO tasks(name, dueDate, type, className, hasReminder, reminderTime) VALUES(?, ?, ?, ?, ?, ?)";
		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, task.getName());
			pstmt.setString(2, task.getDueDate().toString());
			pstmt.setString(3, task.getType().toString());
			pstmt.setString(4, task.getClassName());
			pstmt.setBoolean(5, task.getHasReminder());
			pstmt.setString(6, task.getReminderTime() != null ? task.getReminderTime().toString() : null);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Error adding task: " + e.getMessage());
		}
	}

	public List<Task> loadTasks() {
		List<Task> tasks = new ArrayList<>();
		String sql = "SELECT id, name, dueDate, type, className, hasReminder, reminderTime FROM tasks";

		try (Connection conn = DriverManager.getConnection(DB_URL);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				tasks.add(new Task(rs.getInt("id"), rs.getString("name"), LocalDate.parse(rs.getString("dueDate")),
						Task.TaskType.valueOf(rs.getString("type")), rs.getString("className"),
						rs.getBoolean("hasReminder"),
						rs.getString("reminderTime") != null ? LocalDateTime.parse(rs.getString("reminderTime"))
								: null));
			}
		} catch (SQLException e) {
			System.err.println("Error loading tasks: " + e.getMessage());
		}
		return tasks;
	}

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

	public boolean updateTask(int id, String newName, LocalDate newDueDate, Task.TaskType newType, String newClassName,
			boolean hasReminder, LocalDateTime reminderTime) {
		String sql = "UPDATE tasks SET name = ?, dueDate = ?, type = ?, className = ?, hasReminder = ?, reminderTime = ? WHERE id = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, newName);
			pstmt.setString(2, newDueDate.toString());
			pstmt.setString(3, newType.toString());
			pstmt.setString(4, newClassName);
			pstmt.setBoolean(5, hasReminder);
			pstmt.setString(6, reminderTime != null ? reminderTime.toString() : null);
			pstmt.setInt(7, id);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error updating task: " + e.getMessage());
			return false;
		}
	}
}