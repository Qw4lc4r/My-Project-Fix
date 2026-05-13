package com.example.csharpmanualv2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.csharpmanualv2.Task.Task;
import com.example.csharpmanualv2.Task.TaskResult;
import com.example.csharpmanualv2.network.ApiService;
import com.example.csharpmanualv2.network.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView tvProgress, tvQuestion, tvCodeExample, tvResultStatus, tvExplanation;
    private CardView cardCodeExample, cardResult;
    private RadioGroup radioGroupOptions;
    private Button btnCheck, btnNext, btnFinish;

    private int subchapterId;
    private List<Task> tasks;
    private int currentTaskIndex = 0;
    private int correctCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        toolbar = findViewById(R.id.toolbar);
        tvProgress = findViewById(R.id.tvProgress);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvCodeExample = findViewById(R.id.tvCodeExample);
        cardCodeExample = findViewById(R.id.cardCodeExample);
        radioGroupOptions = findViewById(R.id.radioGroupOptions);
        btnCheck = findViewById(R.id.btnCheck);
        cardResult = findViewById(R.id.cardResult);
        tvResultStatus = findViewById(R.id.tvResultStatus);
        tvExplanation = findViewById(R.id.tvExplanation);
        btnNext = findViewById(R.id.btnNext);
        btnFinish = findViewById(R.id.btnFinish);

        subchapterId = getIntent().getIntExtra("subchapterId", 0);
        String title = getIntent().getStringExtra("title");

        Log.d("QUIZ", "subchapterId = " + subchapterId);
        Log.d("QUIZ", "title = " + title);

        toolbar.setTitle(title != null ? title : "Тест");
        toolbar.setNavigationOnClickListener(v -> finish());

        btnCheck.setOnClickListener(v -> checkAnswer());
        btnNext.setOnClickListener(v -> nextQuestion());
        btnFinish.setOnClickListener(v -> finishQuiz());

        loadTasks();
    }

    private void loadTasks() {
        Log.d("QUIZ", "Загружаю задания для subchapterId: " + subchapterId);
        RetrofitClient.getApi().getTasks(subchapterId).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                Log.d("QUIZ", "Ответ получен: " + response.code());
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    tasks = response.body();
                    Log.d("QUIZ", "Загружено заданий: " + tasks.size());
                    showCurrentTask();
                } else {
                    Toast.makeText(QuizActivity.this, "Нет доступных заданий", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Toast.makeText(QuizActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void showCurrentTask() {
        if (tasks == null || currentTaskIndex >= tasks.size()) return;

        Task task = tasks.get(currentTaskIndex);

        tvProgress.setText("Вопрос " + (currentTaskIndex + 1) + " из " + tasks.size());
        tvQuestion.setText(task.question);

        // Показываем пример кода если есть
        if (task.code_example != null && !task.code_example.isEmpty()) {
            tvCodeExample.setText(task.code_example);
            cardCodeExample.setVisibility(View.VISIBLE);
        } else {
            cardCodeExample.setVisibility(View.GONE);
        }

        // Очищаем и заполняем варианты ответов
        radioGroupOptions.removeAllViews();
        for (Task.TaskOption option : task.options) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(option.option_text);
            radioButton.setId(option.id);
            radioButton.setTextSize(16);
            radioButton.setTextSize(16);
            radioButton.setTextColor(getResources().getColor(android.R.color.black));
            radioButton.setButtonTintList(android.content.res.ColorStateList.valueOf(0xFF000000));
            radioButton.setPadding(16, 16, 16, 16);
            radioGroupOptions.addView(radioButton);
        }

        // Сбрасываем состояние
        cardResult.setVisibility(View.GONE);
        btnCheck.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.GONE);
        btnFinish.setVisibility(View.GONE);
        radioGroupOptions.clearCheck();
    }

    private void checkAnswer() {
        int selectedId = radioGroupOptions.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Выберите вариант ответа", Toast.LENGTH_SHORT).show();
            return;
        }

        Task task = tasks.get(currentTaskIndex);
        ApiService.CheckAnswerRequest request = new ApiService.CheckAnswerRequest(task.id, selectedId);

        btnCheck.setEnabled(false);

        RetrofitClient.getApi().checkAnswer(request).enqueue(new Callback<TaskResult>() {
            @Override
            public void onResponse(Call<TaskResult> call, Response<TaskResult> response) {
                btnCheck.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    TaskResult result = response.body();

                    // ИЗМЕНЕНО: используем метод isCorrect() или проверяем == 1
                    if (result.isCorrect()) {
                        correctCount++;
                        tvResultStatus.setText("✓ Правильно!");
                        tvResultStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    } else {
                        tvResultStatus.setText("✗ Неправильно");
                        tvResultStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    }

                    tvExplanation.setText(result.explanation);
                    cardResult.setVisibility(View.VISIBLE);
                    btnCheck.setVisibility(View.GONE);

                    // Показываем кнопку "Далее" или "Завершить"
                    if (currentTaskIndex < tasks.size() - 1) {
                        btnNext.setVisibility(View.VISIBLE);
                    } else {
                        btnFinish.setVisibility(View.VISIBLE);
                    }

                    // Блокируем выбор ответов
                    for (int i = 0; i < radioGroupOptions.getChildCount(); i++) {
                        radioGroupOptions.getChildAt(i).setEnabled(false);
                    }
                } else {
                    Toast.makeText(QuizActivity.this, "Ошибка проверки", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TaskResult> call, Throwable t) {
                btnCheck.setEnabled(true);
                Log.e("QUIZploxo", "Ошибка сети", t);
                Toast.makeText(QuizActivity.this, "Сеть: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void nextQuestion() {
        currentTaskIndex++;

        // Разблокируем варианты ответов
        for (int i = 0; i < radioGroupOptions.getChildCount(); i++) {
            radioGroupOptions.getChildAt(i).setEnabled(true);
        }

        showCurrentTask();
    }

    private void finishQuiz() {
        int score = Math.round((correctCount / (float) tasks.size()) * 100);
        String message = "Тест завершён!\n\nПравильных ответов: " + correctCount + " из " + tasks.size() +
                "\nРезультат: " + score + "%";

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Отмечаем подпункт как завершённый
        markSubchapterComplete(score);
    }

    private void markSubchapterComplete(int score) {
        ApiService.CompleteRequest request = new ApiService.CompleteRequest(subchapterId, score);

        RetrofitClient.getApi().markComplete(request).enqueue(new Callback<ApiService.SimpleMessage>() {
            @Override
            public void onResponse(Call<ApiService.SimpleMessage> call, Response<ApiService.SimpleMessage> response) {
                if (response.isSuccessful()) {
                    Log.d("QUIZ", "Прогресс сохранён");
                }
                finish();
            }

            @Override
            public void onFailure(Call<ApiService.SimpleMessage> call, Throwable t) {
                Log.e("QUIZ", "Ошибка сохранения прогресса: " + t.getMessage());
                finish();
            }
        });
    }
}