package com.example.csharpmanualv2.Task;

import java.util.List;

public class Task {
    public int id;
    public String type;
    public String question;
    public String code_example;
    public int position;
    public List<TaskOption> options;

    public static class TaskOption {
        public int id;
        public int task_id;
        public String option_text;
        public int position;
    }
}