package com.visualsemester.model;

import java.time.LocalDate;

public class Task {
	// Primary key from database
	private int id;
	private String name;
	private LocalDate dueDate;

	// Constructor for new tasks (no ID yet)
	public Task(String name, LocalDate dueDate) {
		this.name = name;
		this.dueDate = dueDate;
	}

	// Constructor for existing tasks (with ID)
	public Task(int id, String name, LocalDate dueDate) {
		this.id = id;
		this.name = name;
		this.dueDate = dueDate;
	}

	// Getters
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	// Setters
	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	@Override
	public String toString() {
		return String.format("Task [id=%d, name=%s, dueDate=%s]", id, name, dueDate);
	}
}