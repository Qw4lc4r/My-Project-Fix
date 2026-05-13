package com.example.csharpmanualv2.model;

public class UserStats {
    public int chapters_completed;
    public int exercises_completed;
    public int avg_score;
    public int progress_percent;

    public UserStats(int chapters_completed, int exercises_completed, int avg_score, int progress_percent) {
        this.chapters_completed = chapters_completed;
        this.exercises_completed = exercises_completed;
        this.avg_score = avg_score;
        this.progress_percent = progress_percent;
    }
}
