// TaskEditController.java
package com.visualsemester.gui;

import java.io.IOException;
import java.time.LocalDate;
import com.visualsemester.manager.TaskManager;
import com.visualsemester.model.Task;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TaskEditController {
    private Task task;
    private TaskManager taskManager;
    private MainController mainController;
    
    @FXML private TextField taskNameField;
    @FXML private DatePicker dueDatePicker;
    
    public void initialize(Task task, TaskManager taskManager, MainController mainController) {
        this.task = task;
        this.taskManager = taskManager;
        this.mainController = mainController;
        taskNameField.setText(task.getName());
        dueDatePicker.setValue(task.getDueDate());
    }
    
    @FXML
    public void handleBack() {
        // Close just the edit window
        Stage stage = (Stage) taskNameField.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    public void handleSaveEdits() {
        String newName = taskNameField.getText();
        LocalDate newDueDate = dueDatePicker.getValue();
        
        if (newName != null && !newName.isEmpty() && newDueDate != null) {
            taskManager.updateTask(task, newName, newDueDate);
            mainController.updateTaskTable(); // Notify main controller to refresh
            handleBack(); // Close the edit window
        }
    }
    
    @FXML
    public void handleUndoChanges() {
        taskNameField.setText(task.getName());
        dueDatePicker.setValue(task.getDueDate());
    }
}