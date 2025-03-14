package com.visualsemester.manager;

import com.visualsemester.database.DatabaseHandler;
import com.visualsemester.model.Task;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TaskManager {
    private List<Task> tasks;
    private DatabaseHandler dbHandler;

    public TaskManager() {
        tasks = new ArrayList<>();
        dbHandler = new DatabaseHandler();
        loadTasksFromDatabase(); // Load tasks from the database when the manager is created
    }

    // Load tasks from the database
    private void loadTasksFromDatabase() {
        tasks = dbHandler.loadTasks();
    }

    public void addTask(Task task) {
        tasks.add(task);
        dbHandler.addTask(task); // Save the task to the database
    }

    public void deleteTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            Task task = tasks.get(index);
            dbHandler.deleteTask(task.getId()); // Delete the task from the database
            tasks.remove(index);
        } else {
            System.out.println("Invalid task index.");
        }
    }

    public List<Task> getTasks() {
        tasks.sort(Comparator.comparing(Task::getDueDate));
        return tasks;
    }

    public void displayTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + ". " + tasks.get(i));
            }
        }
    }
}