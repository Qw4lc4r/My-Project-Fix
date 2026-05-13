package com.example.csharpmanualv2.Task;

public class TaskResult {
    public int is_correct; // ИЗМЕНЕНО с boolean на int
    public String explanation;
    public String correct_answer;

    // Добавь метод для удобства
    public boolean isCorrect() {
        return is_correct == 1;
    }
}
