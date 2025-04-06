// MainController.java
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
	private Alert alert = new Alert(AlertType.INFORMATION); // initializes a new alert dialog

	public void initialize() {
		taskManager = new TaskManager();
		refreshTasks();
	}

	// Add this method to refresh the task list
	private void refreshTasks() {
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
			refreshTasks(); // Refresh after adding
			taskNameField.clear();
			dueDatePicker.setValue(null);
		} else {
			alert.setTitle("ERROR");
			alert.setHeaderText(null);
			alert.setGraphic(null);
			alert.setContentText("ERROR: please ensure all fields are filled out");
			alert.showAndWait();
		}
	}

	@FXML
	private void handleDeleteTask() {
		Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
		if (selectedTask != null) {
			taskManager.deleteTask(selectedTask.getId());
			refreshTasks(); // Refresh after deleting
		} else {
			selectTaskError();
		}
	}

	@FXML
	public void handleEditTask() throws IOException {
		Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
		if (selectedTask != null) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/visualsemester/gui/task-editor.fxml"));
			Parent root = loader.load();

			TaskEditController editController = loader.getController();
			editController.initialize(selectedTask, taskManager, this); // Pass MainController reference

			Stage stage = new Stage(); // Create new stage instead of replacing main window
			stage.setTitle("Edit Task");
			stage.setScene(new Scene(root));
			stage.show();
		} else {
			selectTaskError();
		}
	}

	// Add this method to be called from TaskEditController
	public void updateTaskTable() {
		refreshTasks();
	}

	private void selectTaskError() {
		alert.setHeaderText(null);
		alert.setGraphic(null);
		alert.setTitle("ERROR");
		alert.setContentText("ERROR: please select a task");
		alert.showAndWait();
	}
}