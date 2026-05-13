package com.example.csharpmanualv2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csharpmanualv2.adapter.SubchaptersAdapter;
import com.example.csharpmanualv2.model.Subchapter;
import com.example.csharpmanualv2.network.ApiService;
import com.example.csharpmanualv2.network.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubchaptersActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView tvChapterDescription, tvChapterProgress;
    private RecyclerView recyclerSubchapters;
    private SubchaptersAdapter adapter;

    private int chapterId;
    private String chapterTitle;
    private String chapterDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subchapters);

        toolbar = findViewById(R.id.toolbar);
        tvChapterDescription = findViewById(R.id.tvChapterDescription);
        tvChapterProgress = findViewById(R.id.tvChapterProgress);
        recyclerSubchapters = findViewById(R.id.recyclerSubchapters);

        chapterId = getIntent().getIntExtra("chapterId", 0);
        chapterTitle = getIntent().getStringExtra("chapterTitle");
        chapterDescription = getIntent().getStringExtra("chapterDescription");

        toolbar.setTitle(chapterTitle);
        toolbar.setNavigationOnClickListener(v -> finish());
        tvChapterDescription.setText(chapterDescription);

        recyclerSubchapters.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SubchaptersAdapter(subchapter -> {
            Intent intent = new Intent(SubchaptersActivity.this, MaterialActivity.class);
            intent.putExtra("subchapterId", subchapter.id);
            intent.putExtra("subchapterTitle", subchapter.code + " " + subchapter.title);
            startActivity(intent);
        });
        recyclerSubchapters.setAdapter(adapter);

        loadSubchapters();
        loadChapterProgress();
    }

    private void loadSubchapters() {
        RetrofitClient.getApi().getSubchapters(chapterId).enqueue(new Callback<List<Subchapter>>() {
            @Override
            public void onResponse(Call<List<Subchapter>> call, Response<List<Subchapter>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setSubchapters(response.body());
                } else {
                    Toast.makeText(SubchaptersActivity.this, "Ошибка загрузки подпунктов", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Subchapter>> call, Throwable t) {
                Toast.makeText(SubchaptersActivity.this, "Сеть: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadChapterProgress() {
        Log.d("SUBCHAPTERS", "Загружаю прогресс для главы " + chapterId);

        RetrofitClient.getApi().getChapterProgress(chapterId).enqueue(new Callback<ApiService.ChapterProgressResponse>() {
            @Override
            public void onResponse(Call<ApiService.ChapterProgressResponse> call, Response<ApiService.ChapterProgressResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ChapterProgressResponse progress = response.body();
                    Log.d("SUBCHAPTERS", "Прогресс получен: " + progress.progress_percent + "%");

                    String progressText = "Прогресс: " + progress.completed + "/" + progress.total +
                            " (" + progress.progress_percent + "%)";
                    tvChapterProgress.setText(progressText);
                } else {
                    Log.e("SUBCHAPTERS", "Ошибка: " + response.code());
                    tvChapterProgress.setText("Прогресс: 0%");
                }
            }

            @Override
            public void onFailure(Call<ApiService.ChapterProgressResponse> call, Throwable t) {
                Log.e("SUBCHAPTERS", "Сеть: " + t.getMessage());
                tvChapterProgress.setText("Прогресс: недоступен");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Обновляем прогресс при возврате на экран (если пользователь завершил подпункт)
        loadChapterProgress();
    }
}