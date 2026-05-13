package com.example.csharpmanualv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csharpmanualv2.adapter.ChaptersAdapter;
import com.example.csharpmanualv2.model.Chapter;
import com.example.csharpmanualv2.model.UserStats;
import com.example.csharpmanualv2.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChaptersFragment extends Fragment {

    private RecyclerView recyclerChapters;
    private ChaptersAdapter adapter;
    private TextView tvProgress;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chapters, container, false);

        tvProgress = view.findViewById(R.id.tvProgress);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerChapters = view.findViewById(R.id.recyclerChapters);

        recyclerChapters.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ChaptersAdapter(chapter -> {
            Intent intent = new Intent(requireActivity(), SubchaptersActivity.class);
            intent.putExtra("chapterId", chapter.id);
            intent.putExtra("chapterTitle", chapter.title);
            intent.putExtra("chapterDescription", chapter.description);
            startActivity(intent);
        });
        recyclerChapters.setAdapter(adapter);

        loadChapters();
        loadProgress();

        return view;
    }

    private void loadProgress() {
        Log.d("CHAPTERS", "Начинаю загрузку прогресса...");
        RetrofitClient.getApi().getUserStats().enqueue(new Callback<UserStats>() {
            @Override
            public void onResponse(Call<UserStats> call, Response<UserStats> response) {
                Log.d("CHAPTERS", "Ответ получен: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    UserStats stats = response.body();
                    Log.d("CHAPTERS", "Прогресс: " + stats.progress_percent + "%");
                    tvProgress.setText("Ваш прогресс: " + stats.progress_percent + "%");
                    progressBar.setProgress(stats.progress_percent);
                }
            }

            @Override
            public void onFailure(Call<UserStats> call, Throwable t) {
                // Игнорируем ошибку или показываем 0%
            }
        });
    }

    private void loadChapters() {
        RetrofitClient.getApi().getChapters().enqueue(new Callback<List<Chapter>>() {
            @Override
            public void onResponse(Call<List<Chapter>> call, Response<List<Chapter>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setChapters(response.body());
                } else {
                    Toast.makeText(requireContext(), "Ошибка загрузки глав", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Chapter>> call, Throwable t) {
                Toast.makeText(requireContext(), "Сеть: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void refreshData() {
        if (isAdded() && getView() != null) {
            loadChapters(); // Используй существующий метод загрузки глав
        }
    }
}
