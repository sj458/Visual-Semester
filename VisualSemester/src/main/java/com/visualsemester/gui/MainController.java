package com.visualsemester.gui;

import java.io.IOException;
import java.time.LocalDate;

import com.visualsemester.manager.TaskManager;
import com.visualsemester.model.Task;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainController {
    @FXML
    private TableView<Task> taskTable;
    @FXML
    private TextField taskNameField;
    @FXML
    private DatePicker dueDatePicker;

    private TaskManager taskManager;
    private ObservableList<Task> tasks;

    public void initialize() {
        taskManager = new TaskManager();
        tasks = FXCollections.observableArrayList(taskManager.getTasks());
        taskTable.setItems(tasks);
    }

    @FXML
    private void handleAddTask() {
        String name = taskNameField.getText();
        LocalDate dueDate = dueDatePicker.getValue();

        if (name != null && !name.isEmpty() && dueDate != null) {
            Task task = new Task(name, dueDate);
            taskManager.addTask(task);
            tasks.add(task); // Add the task to the table
            taskNameField.clear();
            dueDatePicker.setValue(null);
        }
    }

    @FXML
    private void handleDeleteTask() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            taskManager.deleteTask(tasks.indexOf(selectedTask));
            tasks.remove(selectedTask); // Remove the task from the table
        }
    }
    
    @FXML
    public void handleEditTask() throws IOException {
    	Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/visualsemester/gui/task-editor.fxml"));
    	Parent root = loader.load();
    	Stage stage = (Stage) taskTable.getScene().getWindow();
    	stage.setTitle("Task Editor");
    	stage.setScene(new Scene(root));
    	
    }
}