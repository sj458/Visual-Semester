package com.visualsemester.gui;

import java.io.IOException;

import com.visualsemester.manager.TaskManager;
import com.visualsemester.model.Task;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TaskEditController {
	private Task task;
	private TaskManager manager;
	@FXML
	private TextField taskNameField;
    @FXML
    private DatePicker dueDatePicker;
    
    
    public void initialize(Task task) {
    	manager = new TaskManager();
    	this.task = task;
    	taskNameField.setText(task.getName());
    	dueDatePicker.setValue(task.getDueDate());
    }
    
    @FXML
    public void handleBack() throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/visualsemester/gui/main.fxml"));
    	Parent root = loader.load();
    	Stage stage = (Stage) taskNameField.getScene().getWindow();
    	stage.setTitle("Visual Semesterr");
    	stage.setScene(new Scene(root));
    }
    
    @FXML
    public void handleSaveEdits() {
    	//TO-DO confirmation screen
    	manager.updateTask(task, taskNameField.getText(), dueDatePicker.getValue());
    }
    
    @FXML
    public void handleUndoChanges() {
    	//TO-DO confirmation screen
    	taskNameField.setText(task.getName());
    	dueDatePicker.setValue(task.getDueDate());
    	manager.updateTask(task, task.getName(), task.getDueDate());
    }
}
