// TaskEditController.java
package com.visualsemester.gui;

import java.time.LocalDate;
import java.util.Optional;

import com.visualsemester.manager.TaskManager;
import com.visualsemester.model.Task;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TaskEditController {
    private Task task;
    private TaskManager taskManager;
    private MainController mainController;
    private Alert alert = new Alert(AlertType.ERROR);
    
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
        
        if (!newName.equals(task.getName()) || !newDueDate.equals(task.getDueDate())) {// altered so not always true
        	alert.setAlertType(AlertType.CONFIRMATION); // sets up confirmation dialog
        	alert.setTitle("Confirm changes");
        	alert.setHeaderText(null);
        	alert.setGraphic(null);
        	alert.setContentText("Save changes to task?");

        	Optional<ButtonType> result = alert.showAndWait();
        	if (result.get() == ButtonType.OK){ // only saves changes after hitting okay button
            	taskManager.updateTask(task, newName, newDueDate);
                mainController.updateTaskTable(); // Notify main controller to refresh
                handleBack(); // Close the edit window
        	} else {
        	    alert.close(); //keeps you in edit window if you hit cancel
        	}
        }
        else {//displays pop up dialog to notify user of issue
        	alert.setAlertType(AlertType.ERROR);// makes sure the AlertType is error before changing values
    		alert.setHeaderText(null);
    		alert.setGraphic(null);
    		alert.setTitle("ERROR");
    		alert.setContentText("ERROR: no changes detected");
    		alert.showAndWait();
        }
    }
    
    @FXML
    public void handleUndoChanges() { //resets changes
        taskNameField.setText(task.getName());
        dueDatePicker.setValue(task.getDueDate());
    }
}