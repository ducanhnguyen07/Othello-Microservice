package com.anhnd.othellobotservice.model;

public class Tournament {
    private int id;
    private String description;

    public Tournament() {
    }

    public Tournament(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}