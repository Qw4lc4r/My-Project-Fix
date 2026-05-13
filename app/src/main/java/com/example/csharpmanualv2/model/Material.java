package com.example.csharpmanualv2.model;

public class Material {
    public int id;
    public int subchapter_id;
    public String type; // "theory" или "practice"
    public String title;
    public String content;
    public String code_example;
    public String task_description;

    public Material(int id, int subchapter_id, String type, String title,
                    String content, String code_example, String task_description) {
        this.id = id;
        this.subchapter_id = subchapter_id;
        this.type = type;
        this.title = title;
        this.content = content;
        this.code_example = code_example;
        this.task_description = task_description;
    }
}