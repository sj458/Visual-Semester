package com.visualsemester.manager;

import com.visualsemester.database.DatabaseHandler;
import com.visualsemester.model.Task;
import com.visualsemester.model.Task.TaskType;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class TaskManager {
    private static volatile TaskManager instance;
    private final DatabaseHandler dbHandler;
    private final List<Task> tasks;
    private final Object lock = new Object();
    private ReminderScheduler reminderScheduler;

    private TaskManager() {
        this.dbHandler = DatabaseHandler.getInstance();
        this.tasks = new CopyOnWriteArrayList<>();
        initialize();
    }

    public static TaskManager getInstance() {
        if (instance == null) {
            synchronized (TaskManager.class) {
                if (instance == null) {
                    instance = new TaskManager();
                }
            }
        }
        return instance;
    }

    public void setReminderScheduler(ReminderScheduler reminderScheduler) {
        this.reminderScheduler = reminderScheduler;
    }

    private void initialize() {
        System.out.println("[TaskManager] Initializing...");
        loadTasksFromDatabase();
        System.out.printf("[TaskManager] Initialized with %d tasks%n", tasks.size());
        debugPrintTasks();
    }

    private void loadTasksFromDatabase() {
        synchronized (lock) {
            List<Task> loadedTasks = dbHandler.loadTasks();
            tasks.clear();
            tasks.addAll(loadedTasks);
            sortTasksByDate();
        }
    }

    private void sortTasksByDate() {
        Collections.sort(tasks, Comparator.comparing(Task::getDueDate));
    }

    public void addTask(Task task) {
        synchronized (lock) {
            dbHandler.addTask(task);
            loadTasksFromDatabase();
            triggerImmediateReminderCheck();
            debugPrintTasks();
        }
    }

    public boolean deleteTask(int id) {
        synchronized (lock) {
            dbHandler.deleteTask(id);
            loadTasksFromDatabase();
            triggerImmediateReminderCheck();
            return true;
        }
    }

    public boolean updateTask(int id, String newName, LocalDate newDueDate, 
                            TaskType newType, String newClassName, 
                            boolean hasReminder, LocalDateTime reminderTime) {
        synchronized (lock) {
            boolean success = dbHandler.updateTask(id, newName, newDueDate, newType, 
                                                 newClassName, hasReminder, reminderTime);
            if (success) {
                loadTasksFromDatabase();
                triggerImmediateReminderCheck();
                debugPrintTasks();
            }
            return success;
        }
    }

    private void triggerImmediateReminderCheck() {
        if (reminderScheduler != null) {
            System.out.println("Triggering immediate reminder check");
            new Thread(() -> reminderScheduler.checkReminders()).start();
        }
    }

    public Task getTaskById(int id) {
        synchronized (lock) {
            return tasks.stream()
                       .filter(task -> task.getId() == id)
                       .findFirst()
                       .orElse(null);
        }
    }

    public List<Task> getTasks() {
        synchronized (lock) {
            return Collections.unmodifiableList(new ArrayList<>(tasks));
        }
    }

    public void debugPrintTasks() {
        synchronized (lock) {
            System.out.println("\n[TaskManager] Current Tasks:");
            System.out.println("ID  | Name                | Due Date   | Type      | Class    | Reminder? | Reminder Time");
            System.out.println("----|---------------------|------------|-----------|----------|-----------|--------------");
            
            for (Task task : tasks) {
                String reminderInfo = task.getHasReminder() && task.getReminderTime() != null ?
                    task.getReminderTime().format(DateTimeFormatter.ofPattern("MMM d, h:mm a")) :
                    "None";
                
                System.out.printf("%-3d | %-19s | %-10s | %-9s | %-8s | %-9s | %s%n",
                    task.getId(),
                    task.getName(),
                    task.getDueDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                    task.getType(),
                    task.getClassName(),
                    task.getHasReminder() ? "Yes" : "No",
                    reminderInfo);
            }
            System.out.println();
        }
    }
}