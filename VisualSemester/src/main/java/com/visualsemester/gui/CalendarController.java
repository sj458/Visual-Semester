package com.visualsemester.gui;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.visualsemester.manager.TaskManager;
import com.visualsemester.model.Task;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CalendarController{
	ZonedDateTime dateFocus;
	LocalDate today;
	private TaskManager taskManager;

    @FXML
    private Text year;

    @FXML
    private Text month;

    @FXML
    private FlowPane calendar;

    @FXML
    public void initialize() {
    	taskManager = TaskManager.getInstance();
        dateFocus = ZonedDateTime.now();
        today = LocalDate.now();
        drawCalendar();
    }

    @FXML
    void backOneMonth(ActionEvent event) {
        dateFocus = dateFocus.minusMonths(1);
        calendar.getChildren().clear();
        drawCalendar();
    }

    @FXML
    void forwardOneMonth(ActionEvent event) {
        dateFocus = dateFocus.plusMonths(1);
        calendar.getChildren().clear();
        drawCalendar();
    }
    
    private void drawCalendar(){
        year.setText(String.valueOf(dateFocus.getYear()));
        month.setText(String.valueOf(dateFocus.getMonth()));

        double calendarWidth = calendar.getPrefWidth();
        double calendarHeight = calendar.getPrefHeight();
        double strokeWidth = 1;
        double spacingH = calendar.getHgap();
        double spacingV = calendar.getVgap();

        //List of activities for a given month
        Map<Integer, List<Task>> calendarActivityMap = getCalendarActivitiesMonth(dateFocus);

        int monthMaxDate = dateFocus.getMonth().maxLength();
        //Check for leap year
        if(dateFocus.getYear() % 4 != 0 && monthMaxDate == 29){
            monthMaxDate = 28;
        }
        int dateOffset = ZonedDateTime.of(dateFocus.getYear(), dateFocus.getMonthValue(), 1,0,0,0,0,dateFocus.getZone()).getDayOfWeek().getValue();

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                StackPane stackPane = new StackPane();

                Rectangle rectangle = new Rectangle();
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setStroke(Color.BLACK);
                rectangle.setStrokeWidth(strokeWidth);
                double rectangleWidth =(calendarWidth/7) - strokeWidth - spacingH;
                rectangle.setWidth(rectangleWidth);
                double rectangleHeight = (calendarHeight/6) - strokeWidth - spacingV;
                rectangle.setHeight(rectangleHeight);
                stackPane.getChildren().add(rectangle);

                int calculatedDate = (j+1)+(7*i);
                if(calculatedDate > dateOffset){
                    int currentDate = calculatedDate - dateOffset;
                    if(currentDate <= monthMaxDate){
                        Text date = new Text(String.valueOf(currentDate));
                        double textTranslationY = - (rectangleHeight / 2) * 0.75;
                        date.setTranslateY(textTranslationY);
                        stackPane.getChildren().add(date);

                        List<Task> calendarActivities = calendarActivityMap.get(currentDate);
                        if(calendarActivities != null){
                            createCalendarActivity(calendarActivities, rectangleHeight, rectangleWidth, stackPane);
                        }
                    }
                    if(today.getYear() == dateFocus.getYear() && today.getMonth() == dateFocus.getMonth() && today.getDayOfMonth() == currentDate){
                        rectangle.setStroke(Color.BLUE);
                    }
                }
                calendar.getChildren().add(stackPane);
            }
        }
    }
    
    private void createCalendarActivity(List<Task> calendarTask, double rectangleHeight, double rectangleWidth, StackPane stackPane) {
        VBox calendarActivityBox = new VBox();
        for (int k = 0; k < calendarTask.size(); k++) {
            if(k >= 2) {
                Text moreActivities = new Text("...");
                calendarActivityBox.getChildren().add(moreActivities);
                moreActivities.setOnMouseClicked(mouseEvent -> {
                    //On ... click print all activities for given date
                    System.out.println(calendarTask);
                });
                break;
            }
            Text text = new Text(calendarTask.get(k).getName());
            calendarActivityBox.getChildren().add(text);
            text.setOnMouseClicked(mouseEvent -> {
                //On Text clicked
                System.out.println(text.getText());
            });
        }
        calendarActivityBox.setTranslateY((rectangleHeight / 2) * 0.20);
        calendarActivityBox.setMaxWidth(rectangleWidth * 0.8);
        calendarActivityBox.setMaxHeight(rectangleHeight * 0.65);
        calendarActivityBox.setStyle("-fx-background-color:TRANSPARENT");
        stackPane.getChildren().add(calendarActivityBox);
    }
    private Map<Integer, List<Task>> createCalendarMap(List<Task> calendarTasks) {
        Map<Integer, List<Task>> calendarTaskMap = new HashMap<>();

        for (Task activity: calendarTasks) {
            int activityDate = activity.getDueDate().getDayOfMonth();
            if(!calendarTaskMap.containsKey(activityDate)){
            	calendarTaskMap.put(activityDate, List.of(activity));
            } else {
                List<Task> OldListByDate = calendarTaskMap.get(activityDate);

                List<Task> newList = new ArrayList<>(OldListByDate);
                newList.add(activity);
                calendarTaskMap.put(activityDate, newList);
            }
        }
        return  calendarTaskMap;
    }

    private Map<Integer, List<Task>> getCalendarActivitiesMonth(ZonedDateTime dateFocus) {
        List<Task> calendarTasks = new ArrayList<>();
        List<Task> tasks = taskManager.getTasks();
        
        for (Task task : tasks) {
			if(task.getDueDate().getMonth() == dateFocus.getMonth()) {
				calendarTasks.add(task);
			}
		}

        return createCalendarMap(calendarTasks);
    }
    
    @FXML
    private void handleManageTasks() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/visualsemester/gui/main.fxml"));
        Parent root = loader.load();


        Stage stage = (Stage)year.getScene().getWindow();
        stage.setTitle("Edit Task");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
