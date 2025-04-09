package com.visualsemester.gui;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.IntStream;

import com.visualsemester.manager.TaskManager;
import com.visualsemester.model.Task;
import com.visualsemester.model.Task.TaskType;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MainController {
    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> nameColumn;
    @FXML private TableColumn<Task, String> classColumn;
    @FXML private TableColumn<Task, TaskType> typeColumn;
    @FXML private TableColumn<Task, LocalDate> dateColumn;
    @FXML private TableColumn<Task, Boolean> reminderColumn;
    
    @FXML private TextField taskNameField;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<String> taskTypeComboBox;
    @FXML private ComboBox<String> classComboBox;
    @FXML private CheckBox reminderCheckbox;
    @FXML private HBox reminderTimeContainer;
    @FXML private DatePicker reminderDatePicker;
    @FXML private ComboBox<Integer> reminderHourCombo;
    @FXML private ComboBox<Integer> reminderMinuteCombo;
    @FXML private Button toggleAmPmButton;

    private TaskManager taskManager;
    private ObservableList<Task> tasks;
    private Alert alert = new Alert(AlertType.INFORMATION);

    private void loadTasks() {
        tasks = FXCollections.observableArrayList(taskManager.getTasks());
        taskTable.setItems(tasks);
    }
    
    @FXML
    public void initialize() {
        taskManager = new TaskManager();
        
        // Initialize columns
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        
        classColumn.setCellValueFactory(cellData -> cellData.getValue().classNameProperty());
        classColumn.setStyle("-fx-alignment: CENTER;");
        
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        typeColumn.setCellFactory(column -> new TableCell<Task, TaskType>() {
            @Override
            protected void updateItem(TaskType type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(type.toString());
                    switch (type) {
                        case ASSIGNMENT: setStyle("-fx-background-color: #ffd8a8;"); break;
                        case QUIZ: setStyle("-fx-background-color: #b5e6b5;"); break;
                        case TEST: setStyle("-fx-background-color: #ffb3ba;"); break;
                    }
                }
            }
        });
        
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dueDateProperty());
        dateColumn.setCellFactory(column -> new TableCell<Task, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(DateTimeFormatter.ofPattern("MMM d")));
                }
            }
        });
        
        reminderColumn.setCellFactory(column -> new TableCell<Task, Boolean>() {
            @Override
            protected void updateItem(Boolean hasReminder, boolean empty) {
                super.updateItem(hasReminder, empty);
                if (empty || hasReminder == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Task task = getTableView().getItems().get(getIndex());
                    if (hasReminder && task.getReminderTime() != null) {
                        setText(task.getReminderTime().format(
                            DateTimeFormatter.ofPattern("MMM d, h:mm a")
                        ));
                        setStyle("-fx-text-fill: #4a90e2;");
                    } else {
                        setText("");
                    }
                }
            }
        });
        
        // Initialize ComboBoxes
        taskTypeComboBox.setItems(FXCollections.observableArrayList(
            "ASSIGNMENT", "QUIZ", "TEST"
        ));
        
        classComboBox.setItems(FXCollections.observableArrayList(
            "Math", "Science", "History", "English", "Art", "Other"
        ));
         
        reminderHourCombo.setItems(FXCollections.observableArrayList(
            IntStream.rangeClosed(1, 12).boxed().toList()
        ));
        
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
        
        // Set defaults with proper formatting
        reminderHourCombo.setValue(9);
        reminderMinuteCombo.setValue(0); 
        toggleAmPmButton.setText("AM");
        
        // Listeners
        reminderCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            reminderTimeContainer.setVisible(newVal);
            if (newVal) {
                reminderDatePicker.setValue(LocalDate.now());
            }
        });
        
        toggleAmPmButton.setOnAction(e -> {
            toggleAmPmButton.setText(toggleAmPmButton.getText().equals("AM") ? "PM" : "AM");
        });
        
        loadTasks();
    }

    @FXML
    private void handleAddTask() {
        String name = taskNameField.getText();
        LocalDate dueDate = dueDatePicker.getValue();
        String type = taskTypeComboBox.getValue();
        String className = classComboBox.getValue();
        boolean hasReminder = reminderCheckbox.isSelected();
        
        LocalDateTime reminderTime = null;
        if (hasReminder) {
            LocalDate reminderDate = reminderDatePicker.getValue();
            Integer hour = reminderHourCombo.getValue();
            Integer minute = reminderMinuteCombo.getValue();
            String amPm = toggleAmPmButton.getText();
            
            if (reminderDate == null || hour == null || minute == null) {
                showAlert("ERROR", "Please fill all reminder fields");
                return;
            }
            
            int hour24 = hour;
            if (amPm.equals("PM") && hour24 != 12) hour24 += 12;
            else if (amPm.equals("AM") && hour24 == 12) hour24 = 0;
            
            reminderTime = LocalDateTime.of(reminderDate, LocalTime.of(hour24, minute));
        }

        if (name != null && !name.isEmpty() && dueDate != null && type != null && className != null) {
            Task task = new Task(name, dueDate, TaskType.valueOf(type), className, hasReminder, reminderTime);
            taskManager.addTask(task);
            loadTasks();
            clearForm();
        } else {
            showAlert("ERROR", "Please ensure all required fields are filled out");
        }
    }

    @FXML
    private void handleDeleteTask() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            taskManager.deleteTask(selectedTask.getId());
            loadTasks();
        } else {
            showAlert("ERROR", "Please select a task");
        }
    }

    @FXML
    public void handleEditTask() throws IOException {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/visualsemester/gui/task-editor.fxml"));
            Parent root = loader.load();

            TaskEditController editController = loader.getController();
            editController.setTaskData(selectedTask, taskManager, this);

            Stage stage = new Stage();
            stage.setTitle("Edit Task");
            stage.setScene(new Scene(root));
            stage.show();
        } else {
            showAlert("ERROR", "Please select a task");
        }
    }

    public void updateTaskTable() {
        loadTasks();
    }

    private void clearForm() {
        taskNameField.clear();
        dueDatePicker.setValue(null);
        taskTypeComboBox.setValue(null);
        reminderCheckbox.setSelected(false);
        reminderDatePicker.setValue(null);
        reminderHourCombo.setValue(9);
        reminderMinuteCombo.setValue(0);
        toggleAmPmButton.setText("AM");
        reminderTimeContainer.setVisible(false);
    }

    private void showAlert(String title, String message) {
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}