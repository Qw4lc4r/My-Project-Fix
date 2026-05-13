package com.example.csharpmanualv2.model;

public class Subchapter {
    public int id;
    public int chapter_id;
    public String code;
    public String title;
    public String description;
    public int position;

    public Subchapter(int id, int chapter_id, String code, String title, String description, int position) {
        this.id = id;
        this.chapter_id = chapter_id;
        this.code = code;
        this.title = title;
        this.description = description;
        this.position = position;
    }
}
