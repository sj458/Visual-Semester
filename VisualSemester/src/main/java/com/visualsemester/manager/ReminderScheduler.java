package com.visualsemester.manager;

import com.visualsemester.gui.AlertHelper;
import com.visualsemester.model.Task;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ReminderScheduler {
    private final TaskManager taskManager;
    private Timer timer;

    public ReminderScheduler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void start() {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkReminders();
            }
        }, 0, 60 * 1000); // Check every minute
    }

    private void checkReminders() {
        List<Task> tasks = taskManager.getTasks();
        LocalDateTime now = LocalDateTime.now();

        tasks.stream()
            .filter(Task::getHasReminder)
            .filter(task -> task.getReminderTime() != null)
            .filter(task -> {
                long minutesDiff = ChronoUnit.MINUTES.between(now, task.getReminderTime());
                return minutesDiff >= 0 && minutesDiff < 1; 
            })
            .forEach(task -> {
                javafx.application.Platform.runLater(() -> {
                    AlertHelper.showAlert("Reminder", 
                        "Task due soon: " + task.getName() + 
                        "\nDue: " + task.getDueDate() +
                        "\nReminder time: " + task.getReminderTime().toLocalTime());
                });
            });
        System.out.println("Checking reminders at: " + LocalDateTime.now());
        tasks.forEach(task -> {
            if (task.getHasReminder()) {
                System.out.println("Task: " + task.getName() + 
                    " | Reminder Time: " + task.getReminderTime());
            }
        });
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }
}