package com.visualsemester.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Task {
	public enum TaskType {
		ASSIGNMENT, QUIZ, TEST
	}

	private final int id;
	private final StringProperty name;
	private final ObjectProperty<LocalDate> dueDate;
	private final ObjectProperty<TaskType> type;
	private final StringProperty className;
	private final BooleanProperty hasReminder;
	private final ObjectProperty<LocalDateTime> reminderTime;

	public Task(String name, LocalDate dueDate, TaskType type, String className, boolean hasReminder,
			LocalDateTime reminderTime) {
		this(-1, name, dueDate, type, className, hasReminder, reminderTime);
	}

	public Task(int id, String name, LocalDate dueDate, TaskType type, String className, boolean hasReminder,
			LocalDateTime reminderTime) {
		this.id = id;
		this.name = new SimpleStringProperty(name);
		this.dueDate = new SimpleObjectProperty<>(dueDate);
		this.type = new SimpleObjectProperty<>(type);
		this.className = new SimpleStringProperty(className);
		this.hasReminder = new SimpleBooleanProperty(hasReminder);
		this.reminderTime = new SimpleObjectProperty<>(reminderTime);
	}

	// Property accessors
	public StringProperty nameProperty() {
		return name;
	}

	public ObjectProperty<LocalDate> dueDateProperty() {
		return dueDate;
	}

	public ObjectProperty<TaskType> typeProperty() {
		return type;
	}

	public StringProperty classNameProperty() {
		return className;
	}

	public BooleanProperty hasReminderProperty() {
		return hasReminder;
	}

	public ObjectProperty<LocalDateTime> reminderTimeProperty() {
		return reminderTime;
	}

	// Getters
	public int getId() {
		return id;
	}

	public String getName() {
		return name.get();
	}

	public LocalDate getDueDate() {
		return dueDate.get();
	}

	public TaskType getType() {
		return type.get();
	}

	public String getClassName() {
		return className.get();
	}

	public boolean getHasReminder() {
		return hasReminder.get();
	}

	public LocalDateTime getReminderTime() {
		return reminderTime.get();
	}

	// Setters
	public void setName(String name) {
		this.name.set(name);
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate.set(dueDate);
	}

	public void setType(TaskType type) {
		this.type.set(type);
	}

	public void setClassName(String className) {
		this.className.set(className);
	}

	public void setHasReminder(boolean hasReminder) {
		this.hasReminder.set(hasReminder);
	}

	public void setReminderTime(LocalDateTime reminderTime) {
		this.reminderTime.set(reminderTime);
	}

	@Override
	public String toString() {
		return String.format(
				"Task [id=%d, name=%s, dueDate=%s, type=%s, className=%s, hasReminder=%s, reminderTime=%s]", id,
				getName(), getDueDate(), getType(), getClassName(), getHasReminder(), getReminderTime());
	}
}