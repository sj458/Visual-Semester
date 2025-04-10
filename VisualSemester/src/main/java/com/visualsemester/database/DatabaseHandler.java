package com.visualsemester.database;

import com.visualsemester.model.Task;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private static volatile DatabaseHandler instance;
    private static final String DB_URL = "jdbc:sqlite:tasks.db";
    
    private DatabaseHandler() {
        createTable();
    }

    public static DatabaseHandler getInstance() {
        if (instance == null) {
            synchronized (DatabaseHandler.class) {
                if (instance == null) {
                    instance = new DatabaseHandler();
                }
            }
        }
        return instance;
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS tasks ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "name TEXT NOT NULL, "
            + "dueDate TEXT NOT NULL, "
            + "type TEXT NOT NULL, "
            + "className TEXT NOT NULL, "
            + "hasReminder BOOLEAN NOT NULL DEFAULT 0, "
            + "reminderTime TEXT)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("[Database] Table created/verified");
        } catch (SQLException e) {
            System.err.println("[Database] Error creating table: " + e.getMessage());
        }
    }

    public void addTask(Task task) {
        String sql = "INSERT INTO tasks(name, dueDate, type, className, hasReminder, reminderTime) "
                   + "VALUES(?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, task.getName());
            pstmt.setString(2, task.getDueDate().toString());
            pstmt.setString(3, task.getType().toString());
            pstmt.setString(4, task.getClassName());
            pstmt.setBoolean(5, task.getHasReminder());
            pstmt.setString(6, task.getReminderTime() != null ? task.getReminderTime().toString() : null);
            
            pstmt.executeUpdate();
            System.out.printf("[Database] Added task: %s%n", task.getName());
        } catch (SQLException e) {
            System.err.println("[Database] Error adding task: " + e.getMessage());
        }
    }

    public List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT id, name, dueDate, type, className, hasReminder, reminderTime FROM tasks";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Task task = new Task(
                    rs.getInt("id"),
                    rs.getString("name"),
                    LocalDate.parse(rs.getString("dueDate")),
                    Task.TaskType.valueOf(rs.getString("type")),
                    rs.getString("className"),
                    rs.getBoolean("hasReminder"),
                    rs.getString("reminderTime") != null ? 
                        LocalDateTime.parse(rs.getString("reminderTime")) : null
                );
                tasks.add(task);
            }
            System.out.printf("[Database] Loaded %d tasks%n", tasks.size());
        } catch (SQLException e) {
            System.err.println("[Database] Error loading tasks: " + e.getMessage());
        }
        return tasks;
    }

    public void deleteTask(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            System.out.printf("[Database] Deleted task ID %d (%d rows affected)%n", id, affectedRows);
        } catch (SQLException e) {
            System.err.println("[Database] Error deleting task: " + e.getMessage());
        }
    }

    public boolean updateTask(int id, String newName, LocalDate newDueDate, 
                            Task.TaskType newType, String newClassName,
                            boolean hasReminder, LocalDateTime reminderTime) {
        String sql = "UPDATE tasks SET "
                   + "name = ?, dueDate = ?, type = ?, className = ?, "
                   + "hasReminder = ?, reminderTime = ? "
                   + "WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newName);
            pstmt.setString(2, newDueDate.toString());
            pstmt.setString(3, newType.toString());
            pstmt.setString(4, newClassName);
            pstmt.setBoolean(5, hasReminder);
            pstmt.setString(6, reminderTime != null ? reminderTime.toString() : null);
            pstmt.setInt(7, id);
            
            int affectedRows = pstmt.executeUpdate();
            System.out.printf("[Database] Updated task ID %d (%d rows affected)%n", id, affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("[Database] Error updating task: " + e.getMessage());
            return false;
        }
    }

    // For debugging database issues
    public void printDatabaseStats() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("\n[Database Statistics]");
            
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM tasks");
            System.out.println("Total tasks: " + rs.getInt(1));
            
            rs = stmt.executeQuery("SELECT COUNT(*) FROM tasks WHERE hasReminder = 1");
            System.out.println("Tasks with reminders: " + rs.getInt(1));
            
        } catch (SQLException e) {
            System.err.println("[Database] Error getting stats: " + e.getMessage());
        }
    }
}