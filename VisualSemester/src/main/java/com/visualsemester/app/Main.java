package com.visualsemester.app;

import com.visualsemester.manager.ReminderScheduler;
import com.visualsemester.manager.TaskManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private ReminderScheduler reminderScheduler;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize scheduler
        TaskManager taskManager = new TaskManager();
        reminderScheduler = new ReminderScheduler(taskManager);
        reminderScheduler.start();

        // Load main view
        Parent root = FXMLLoader.load(getClass().getResource("/com/visualsemester/gui/main.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/visualsemester/gui/styles.css").toExternalForm());
        primaryStage.setTitle("Visual Semester");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (reminderScheduler != null) {
            reminderScheduler.stop();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}