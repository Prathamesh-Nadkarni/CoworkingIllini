package com.example.coworkingillini;

import java.util.List;

public class ProfileData {
    private String name; // Profile name
    private int loud, pace, group, cram; // Loudness level
    private List<List<String>> courses; // Courses associated with the profile (can be a single string or a list)
    private List<String> skills; // Skills associated with the profile
    private int compatibilityScore; // Compatibility score
    boolean decision;

    public ProfileData(String name, int loud, int pace, int group, int cram, List<List<String>> courses, List<String> skills, int compatibilityScore) {
        this.name = name;
        this.loud = loud;
        this.cram = cram;
        this.group = group;
        this.pace = pace;
        this.courses = courses;
        this.skills = skills;
        this.compatibilityScore = compatibilityScore;
    }

    public String getName() {
        return name;
    }

    public int getLoud() {
        return loud;
    }

    public int getPace() {
        return pace;
    }

    public int getGroup() {
        return group;
    }

    public int getCram() {
        return cram;
    }

    public List<List<String>> getCourses() {
        return courses;
    }

    public List<String> getSkills() {
        return skills;
    }

    public int getCompatibilityScore() {
        return compatibilityScore;
    }

    public void setCompatibilityScore(int compatibilityScore) {
        this.compatibilityScore = compatibilityScore;
    }

    public boolean getDecision()
    {
        return decision;
    }

    public void setDecision(boolean decision)
    {
        this.decision = decision;
    }
}
