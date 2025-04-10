package com.visualsemester.gui;

import com.visualsemester.manager.TaskManager;
import com.visualsemester.model.Task;
import com.visualsemester.model.Task.TaskType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.IntStream;

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
        // Initialize hour combo (1-12)
        ObservableList<Integer> hours = FXCollections.observableArrayList();
        IntStream.rangeClosed(1, 12).forEach(hours::add);
        reminderHourCombo.setItems(hours);
        
        // Initialize minute combo (0-59) with proper formatting
        ObservableList<Integer> minutes = FXCollections.observableArrayList();
        IntStream.rangeClosed(0, 59).forEach(minutes::add);
        reminderMinuteCombo.setItems(minutes);
        
        // Format minute display
        reminderMinuteCombo.setCellFactory(lv -> new ListCell<Integer>() {
            @Override
            protected void updateItem(Integer minute, boolean empty) {
                super.updateItem(minute, empty);
                setText(empty || minute == null ? "" : String.format("%02d", minute));
            }
        });
        
        reminderMinuteCombo.setButtonCell(new ListCell<Integer>() {
            @Override
            protected void updateItem(Integer minute, boolean empty) {
                super.updateItem(minute, empty);
                setText(empty || minute == null ? "" : String.format("%02d", minute));
            }
        });

        // Set default values
        reminderHourCombo.setValue(9);
        reminderMinuteCombo.setValue(0);
        toggleAmPmButton.setText("AM");
        
        // Initialize other comboboxes
        editClassComboBox.setItems(FXCollections.observableArrayList(
            "Math", "Science", "History", "English", "Art", "Other"
        ));
        
        taskTypeComboBox.setItems(FXCollections.observableArrayList(
            "ASSIGNMENT", "QUIZ", "TEST"
        ));
        
        // Bind visibility
        reminderTimeContainer.visibleProperty().bind(reminderCheckbox.selectedProperty());
        
        // AM/PM toggle
        toggleAmPmButton.setOnAction(e -> {
            toggleAmPmButton.setText(toggleAmPmButton.getText().equals("AM") ? "PM" : "AM");
            forceComboBoxRefresh();
        });
        
        // Add listeners to force UI updates
        reminderHourCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            forceComboBoxRefresh();
        });
        
        reminderMinuteCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            forceComboBoxRefresh();
        });
    }
    
    private void forceComboBoxRefresh() {
        Platform.runLater(() -> {
            reminderHourCombo.requestLayout();
            reminderMinuteCombo.requestLayout();
            reminderTimeContainer.requestLayout();
        });
    }
    
    public void setTaskData(Task selectedTask, TaskManager taskManager, MainController mainController) {
        this.task = selectedTask;
        this.taskManager = taskManager;
        this.mainController = mainController;
        
        // Populate fields with task data
        taskNameField.setText(task.getName());
        dueDatePicker.setValue(task.getDueDate());
        taskTypeComboBox.setValue(task.getType().toString());
        editClassComboBox.setValue(task.getClassName());
        reminderCheckbox.setSelected(task.getHasReminder());
        
        if (task.getReminderTime() != null) {
            reminderDatePicker.setValue(task.getReminderTime().toLocalDate());
            
            int hour24 = task.getReminderTime().getHour();
            final int finalHour12 = (hour24 % 12 == 0) ? 12 : hour24 % 12;  // Final copy for lambda
            
            Platform.runLater(() -> {
                reminderHourCombo.getSelectionModel().select(finalHour12);
                reminderMinuteCombo.getSelectionModel().select(task.getReminderTime().getMinute());
                toggleAmPmButton.setText(hour24 < 12 ? "AM" : "PM");  // hour24 is effectively final
                forceComboBoxRefresh();
            });
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
                    showAlert("Error", "Please complete all reminder fields");
                    return;
                }
                
                int hour24 = hour;
                if (amPm.equals("PM") && hour24 != 12) hour24 += 12;
                else if (amPm.equals("AM") && hour24 == 12) hour24 = 0;
                
                reminderTime = LocalDateTime.of(reminderDate, LocalTime.of(hour24, minute));
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
            showAlert("Error", "Invalid input: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleUndoChanges() {
        setTaskData(task, taskManager, mainController);
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
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}