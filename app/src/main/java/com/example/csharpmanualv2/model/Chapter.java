package com.example.csharpmanualv2.model;

public class Chapter {
    public int id;
    public String title;
    public String subtitle;
    public String description;
    public int position;

    public Chapter(int id, String title, String subtitle, String description, int position) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.position = position;
    }
}
