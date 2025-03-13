package com.visualsemester.model;

import java.time.LocalDate;

public class Task {
    private int id; // Unique identifier for the task
    private String name;
    private LocalDate dueDate;

    public Task(String name, LocalDate dueDate) {
        this.name = name;
        this.dueDate = dueDate;
    }

    public Task(int id, String name, LocalDate dueDate) {
        this.id = id;
        this.name = name;
        this.dueDate = dueDate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public String toString() {
        return "Task: " + name + " | Due: " + dueDate;
    }
}