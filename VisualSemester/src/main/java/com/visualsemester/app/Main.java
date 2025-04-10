package com.visualsemester.app;

import com.visualsemester.manager.ReminderScheduler;
import com.visualsemester.manager.TaskManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Main extends Application {
    private ReminderScheduler reminderScheduler;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        TaskManager taskManager = TaskManager.getInstance();
        this.reminderScheduler = new ReminderScheduler(taskManager, primaryStage);
        taskManager.setReminderScheduler(reminderScheduler);
        
        Parent root = FXMLLoader.load(getClass().getResource("/com/visualsemester/gui/main.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/visualsemester/gui/styles.css").toExternalForm());
        
        primaryStage.setTitle("Visual Semester");
        primaryStage.setScene(scene);
        
        primaryStage.setOnShown(e -> {
            reminderScheduler.startCheckingReminders();
            logTimeZoneInfo();
        });
        
        primaryStage.iconifiedProperty().addListener((obs, wasMinimized, isNowMinimized) -> {
            if (!isNowMinimized) {
                reminderScheduler.startCheckingReminders();
            }
        });
        
        // macOS-specific configuration
        configureForMacOS();
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (reminderScheduler != null) {
            reminderScheduler.stopCheckingReminders();
        }
        System.out.println("Application shutdown complete");
    }

    private void configureForMacOS() {
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            System.setProperty("apple.awt.UIElement", "true");
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
    }

    private void logTimeZoneInfo() {
        System.out.println("JVM Time Zone: " + ZoneId.systemDefault());
        System.out.println("Current Eastern Time: " + 
            ZonedDateTime.now(ZoneId.of("America/New_York"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a")));
    }

    public static void main(String[] args) {
        launch(args);
    }
}