package com.visualsemester.gui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.IntStream;

import com.visualsemester.manager.TaskManager;
import com.visualsemester.model.Task;
import com.visualsemester.model.Task.TaskType;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class TaskEditController {
    private Task task;
    private TaskManager taskManager;
    private MainController mainController;
    
    @FXML private TextField taskNameField;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<String> taskTypeComboBox;
    @FXML private ComboBox<String> editClassComboBox;
    @FXML private CheckBox reminderCheckbox;
    @FXML private DatePicker reminderDatePicker;
    @FXML private ComboBox<Integer> reminderHourCombo;
    @FXML private ComboBox<Integer> reminderMinuteCombo;
    @FXML private HBox reminderTimeContainer;
    @FXML private Button toggleAmPmButton;
    
    @FXML
    public void initialize() {
        // Initialize time options
        reminderHourCombo.setItems(FXCollections.observableArrayList(
            IntStream.rangeClosed(1, 12).boxed().toList()
        ));
        
        // Initialize minutes with proper formatting
        reminderMinuteCombo.setItems(FXCollections.observableArrayList(
            IntStream.rangeClosed(0, 59).boxed().toList()
        ));
        
        // Format minute display in dropdown
        reminderMinuteCombo.setCellFactory(lv -> new ListCell<Integer>() {
            @Override
            protected void updateItem(Integer minute, boolean empty) {
                super.updateItem(minute, empty);
                setText(empty || minute == null ? "" : String.format("%02d", minute));
            }
        });
        
        // Format selected minute display
        reminderMinuteCombo.setButtonCell(new ListCell<Integer>() {
            @Override
            protected void updateItem(Integer minute, boolean empty) {
                super.updateItem(minute, empty);
                setText(empty || minute == null ? "" : String.format("%02d", minute));
            }
        });
        
        // Initialize class and type comboboxes
        editClassComboBox.setItems(FXCollections.observableArrayList(
            "Math", "Science", "History", "English", "Art", "Other"
        ));
        
        taskTypeComboBox.setItems(FXCollections.observableArrayList(
            "ASSIGNMENT", "QUIZ", "TEST"
        ));
        
        // Format hour display
        reminderHourCombo.setCellFactory(lv -> new ListCell<Integer>() {
            @Override
            protected void updateItem(Integer hour, boolean empty) {
                super.updateItem(hour, empty);
                if (empty || hour == null) {
                    setText("");
                } else {
                    setText(String.valueOf(hour));
                }
            }
        });
        
        // Update AM/PM when hour changes
        reminderHourCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                toggleAmPmButton.setText(newVal < 12 ? "AM" : "PM");
            }
        });
        
        // AM/PM toggle button
        toggleAmPmButton.setOnAction(e -> {
            toggleAmPmButton.setText(toggleAmPmButton.getText().equals("AM") ? "PM" : "AM");
        });
        
        // Set default time to 9:00 AM with proper formatting
        reminderHourCombo.setValue(9);
        reminderMinuteCombo.setValue(0); 
        toggleAmPmButton.setText("AM");
        
        // Bind visibility
        reminderTimeContainer.visibleProperty().bind(reminderCheckbox.selectedProperty());
    }
    
    public void setTaskData(Task selectedTask, TaskManager taskManager, MainController mainController) {
        this.task = selectedTask;
        this.taskManager = taskManager;
        this.mainController = mainController;
        
        // Set initial values
        taskNameField.setText(task.getName());
        dueDatePicker.setValue(task.getDueDate());
        taskTypeComboBox.setValue(task.getType().toString());
        editClassComboBox.setValue(task.getClassName());
        reminderCheckbox.setSelected(task.getHasReminder());
        
        if (task.getReminderTime() != null) {
            reminderDatePicker.setValue(task.getReminderTime().toLocalDate());
            
            int hour24 = task.getReminderTime().getHour();
            int hour12 = hour24 % 12;
            if (hour12 == 0) hour12 = 12;
            
            reminderHourCombo.setValue(hour12);
            reminderMinuteCombo.setValue(task.getReminderTime().getMinute());
            toggleAmPmButton.setText(hour24 < 12 ? "AM" : "PM");
        } else {
            reminderDatePicker.setValue(LocalDate.now());
        }
    }

    @FXML
    private void handleSaveEdits() {
        try {
            String newName = taskNameField.getText().trim();
            LocalDate newDueDate = dueDatePicker.getValue();
            TaskType newType = TaskType.valueOf(taskTypeComboBox.getValue());
            String newClassName = editClassComboBox.getValue();
            boolean hasReminder = reminderCheckbox.isSelected();
            
            LocalDateTime reminderTime = null;
            if (hasReminder) {
                LocalDate reminderDate = reminderDatePicker.getValue();
                Integer hour = reminderHourCombo.getValue();
                Integer minute = reminderMinuteCombo.getValue();
                String amPm = toggleAmPmButton.getText();
                
                if (reminderDate == null || hour == null || minute == null) {
                    showAlert("Invalid Reminder", "Please fill all reminder fields");
                    return;
                }
                
                int hour24 = hour;
                if (amPm.equals("PM") && hour24 != 12) hour24 += 12;
                else if (amPm.equals("AM") && hour24 == 12) hour24 = 0;
                
                reminderTime = LocalDateTime.of(reminderDate, LocalTime.of(hour24, minute));
                
                if (reminderTime.isAfter(LocalDateTime.of(newDueDate, LocalTime.MAX))) {
                    showAlert("Invalid Reminder", "Reminder time must be before the due date");
                    return;
                }
            }

            if (newName.isEmpty() || newDueDate == null || newClassName == null) {
                showAlert("Error", "Please fill in all required fields");
                return;
            }

            if (taskManager.updateTask(
            	    task.getId(), 
            	    newName, 
            	    newDueDate, 
            	    newType,
            	    newClassName, 
            	    hasReminder,
            	    reminderTime)) {
            	    mainController.updateTaskTable();
            	    closeWindow();
            	}
        } catch (Exception e) {
            showAlert("Error", "Failed to save changes: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleUndoChanges() {
        taskNameField.setText(task.getName());
        dueDatePicker.setValue(task.getDueDate());
        taskTypeComboBox.setValue(task.getType().toString());
        reminderCheckbox.setSelected(task.getHasReminder());
        
        if (task.getReminderTime() != null) {
            reminderDatePicker.setValue(task.getReminderTime().toLocalDate());
            
            int hour24 = task.getReminderTime().getHour();
            int hour12 = hour24 % 12;
            if (hour12 == 0) hour12 = 12;
            
            reminderHourCombo.setValue(hour12);
            reminderMinuteCombo.setValue(task.getReminderTime().getMinute());
            toggleAmPmButton.setText(hour24 < 12 ? "AM" : "PM");
        }
    }
    
    @FXML
    private void handleBack() {
        closeWindow();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) taskNameField.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}