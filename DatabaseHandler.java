package com.visualsemester.database;

import com.visualsemester.model.Task;
import java.util.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DatabaseHandler {
    private static final String DB_URL = "jdbc:sqlite:tasks.db"; // SQLite database file

    public DatabaseHandler() {
        createTable(); // Ensure the table exists when the handler is created
    }

    // Create the tasks table if it doesn't exist
    private void createTable() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS tasks (" +
                          "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                          "name TEXT NOT NULL, " +
                          "dueDate TEXT NOT NULL)";
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
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
            System.out.println("Error adding task: " + e.getMessage());
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
                int id = rs.getInt("id");
                String name = rs.getString("name");
                LocalDate dueDate = LocalDate.parse(rs.getString("dueDate"));
                tasks.add(new Task(id, name, dueDate));
            }
        } catch (SQLException e) {
            System.out.println("Error loading tasks: " + e.getMessage());
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
            System.out.println("Error deleting task: " + e.getMessage());
        }
    }
    
    //update a task in the database
    public void updateTask(int taskId) {
    	//Scanner scanner = new Scanner(System.in);
    	//System.out.println("enter new task name:");
    	String newName = "";//scanner.nextLine(); connect to edit button
    	//System.out.println("Enter new due date (YYYY-MM-DD");
    	String dueDateInput = ""; //scanner.nextLine();connect to edit button
    	
    	LocalDate newDueDate = null;
    	try {
    		newDueDate = LocalDate.parse(dueDateInput);
    	} catch(Exception e) {
    		System.out.println("Invalid date format.");
    	}
    	String sql = "Update tasks Set name = ?, dueDate = ? WHERE id =?";
    	
    	try(Connection conn = DriverManager.getConnection(DB_URL);
    		PreparedStatement pstmt = conn.prepareStatement(sql)){
    		pstmt.setString(1, newName);
    		pstmt.setString(2, newDueDate.toString());
    		pstmt.setInt(3, taskId);
    		
    		int rowsUpdated = pstmt.executeUpdate();
    		if (rowsUpdated > 0) {
    			System.out.println("Task updated successfully.");
    		} else {
    			System.out.println("Task not found with ID " + taskId);
    		}
    	}catch(SQLException e) {
    		System.out.println("Error updating task:" + e.getMessage());
    		
    	}
    	//scanner.close();
    }
    }
    
    
