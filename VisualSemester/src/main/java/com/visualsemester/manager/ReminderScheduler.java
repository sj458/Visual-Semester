package com.visualsemester.manager;

import com.visualsemester.gui.AlertHelper;
import com.visualsemester.model.Task;
import com.dustinredmond.fxtrayicon.FXTrayIcon;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.control.MenuItem;

public class ReminderScheduler {
    private final TaskManager taskManager;
    private ScheduledExecutorService executor;
    private final Set<Integer> processedReminders = new HashSet<>();
    private Stage primaryStage;
    private FXTrayIcon trayIcon;

    public ReminderScheduler(TaskManager taskManager, Stage primaryStage) {
        this.taskManager = taskManager;
        this.primaryStage = primaryStage;
        initializeTrayIcon();
    }

    private void initializeTrayIcon() {
        Platform.runLater(() -> {
            try {
                trayIcon = new FXTrayIcon(primaryStage, getClass().getResource("/icon.png"));
                MenuItem showItem = new MenuItem("Show App");
                showItem.setOnAction(e -> {
                    primaryStage.show();
                    primaryStage.toFront();
                });
                
                MenuItem exitItem = new MenuItem("Exit");
                exitItem.setOnAction(e -> System.exit(0));
                
                trayIcon.addMenuItem(showItem);
                trayIcon.addSeparator();
                trayIcon.addMenuItem(exitItem);
                trayIcon.setTrayIconTooltip("VisualSemester");
                trayIcon.show();
                System.out.println("Tray icon initialized");
            } catch (Exception e) {
                System.err.println("Tray icon error: " + e.getMessage());
            }
        });
    }

    public void startCheckingReminders() {
        System.out.println("Starting reminder checks...");
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }
        
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::checkReminders, 0, 5, TimeUnit.SECONDS);
    }

    void checkReminders() {
        System.out.println("Checking reminders at: " + LocalTime.now());
        List<Task> tasks = taskManager.getTasks();
        
        for (Task task : tasks) {
            if (shouldTriggerReminder(task)) {
                System.out.println("Triggering reminder for: " + task.getName());
                Platform.runLater(() -> showReminderAlert(task));
                markReminderAsProcessed(task);
            }
        }
    }

    private boolean shouldTriggerReminder(Task task) {
        boolean shouldTrigger = task.getHasReminder() 
                && task.getReminderTime() != null 
                && !processedReminders.contains(task.getId()) 
                && LocalDateTime.now().isAfter(task.getReminderTime());
        
        System.out.println("Checking task: " + task.getName() 
                + " | Should trigger: " + shouldTrigger);
        return shouldTrigger;
    }

    private void showReminderAlert(Task task) {
        try {
            if (trayIcon != null) {
                trayIcon.showInfoMessage(
                    "Task Reminder",
                    createNotificationMessage(task)
                );
            }
            
            AlertHelper.showAlert("Reminder", 
                task.getName() + " is due!\n" +
                "Scheduled time: " + 
                task.getReminderTime().format(DateTimeFormatter.ofPattern("MMM d, h:mm a")));
        } catch (Exception e) {
            System.err.println("Error showing reminder: " + e.getMessage());
        }
    }

    private String createNotificationMessage(Task task) {
        return String.format(
            "%s is due!\nType: %s\nClass: %s\nDue: %s",
            task.getName(),
            task.getType(),
            task.getClassName(),
            task.getDueDate().format(DateTimeFormatter.ofPattern("MMM d"))
        );
    }

    private void markReminderAsProcessed(Task task) {
        processedReminders.add(task.getId());
        task.setHasReminder(false);
        
        taskManager.updateTask(
            task.getId(),
            task.getName(),
            task.getDueDate(),
            task.getType(),
            task.getClassName(),
            false,
            null
        );
    }

    public void stopCheckingReminders() {
        if (executor != null) {
            executor.shutdownNow();
            try {
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        if (trayIcon != null) {
            trayIcon.hide();
        }
    }
}