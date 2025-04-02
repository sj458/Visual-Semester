package com.visualsemester.manager;

import com.visualsemester.database.DatabaseHandler;
import com.visualsemester.model.Task;
import java.time.LocalDate;
import java.util.*;

public class TaskManager {
	private final List<Task> tasks;
	private final DatabaseHandler dbHandler;

	public TaskManager() {
		this.dbHandler = new DatabaseHandler();
		this.tasks = new ArrayList<>();
		loadTasksFromDatabase();
	}

	private void loadTasksFromDatabase() {
		tasks.clear();
		tasks.addAll(dbHandler.loadTasks());
		sortTasksByDate();
	}

	private void sortTasksByDate() {
		tasks.sort(Comparator.comparing(Task::getDueDate));
	}

	public void addTask(Task task) {
		dbHandler.addTask(task);
		// Refresh from DB to get generated ID
		loadTasksFromDatabase();
	}

	public boolean deleteTask(int index) {
		if (index >= 0 && index < tasks.size()) {
			Task task = tasks.get(index);
			dbHandler.deleteTask(task.getId());
			loadTasksFromDatabase();
			return true;
		}
		return false;
	}

	public boolean updateTask(Task task, String newName, LocalDate newDueDate) {
	    boolean success = dbHandler.updateTask(task.getId(), newName, newDueDate);
	    if (success) {
	        task.setName(newName);
	        task.setDueDate(newDueDate);
	        loadTasksFromDatabase(); // Refresh from database
	        return true;
	    }
	    return false;
	}

	public Task getTaskById(int id) {
		return tasks.stream().filter(task -> task.getId() == id).findFirst().orElse(null);
	}

	public List<Task> getTasks() {
		return Collections.unmodifiableList(tasks);
	}

	public void displayTasks() {
		if (tasks.isEmpty()) {
			System.out.println("No tasks found.");
		} else {
			for (int i = 0; i < tasks.size(); i++) {
				System.out.printf("%d. %s%n", i + 1, tasks.get(i));
			}
		}
	}
}