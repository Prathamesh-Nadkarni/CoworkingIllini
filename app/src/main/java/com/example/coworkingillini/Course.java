package com.example.coworkingillini;

import java.io.Serializable;
import java.util.ArrayList;

public class Course implements Serializable {
    private String name;
    private ArrayList<String> goals;

    public Course() {
        this.name = "";
        this.goals = new ArrayList<>();
    }

    // Getters and setters
    public void setName(String name) { this.name = name; }
    public String getName() { return name; }
    public ArrayList<String> getGoals() { return goals; }
    public void addGoal(String goal) { goals.add(goal); }
    public void removeGoal(String goal) { goals.remove(goal); }
}