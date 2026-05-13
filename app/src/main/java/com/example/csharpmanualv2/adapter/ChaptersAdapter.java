package com.example.csharpmanualv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csharpmanualv2.R;
import com.example.csharpmanualv2.model.Chapter;
import com.example.csharpmanualv2.network.ApiService;
import com.example.csharpmanualv2.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChaptersAdapter extends RecyclerView.Adapter<ChaptersAdapter.ChapterViewHolder> {

    private List<Chapter> chapters = new ArrayList<>();
    private OnChapterClickListener listener;

    public interface OnChapterClickListener {
        void onChapterClick(Chapter chapter);
    }

    public ChaptersAdapter(OnChapterClickListener listener) {
        this.listener = listener;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chapter, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapters.get(position);
        holder.tvTitle.setText(chapter.title);
        holder.tvSubtitle.setText(chapter.subtitle);

        // Загружаем прогресс по главе
        RetrofitClient.getApi().getChapterProgress(chapter.id).enqueue(new Callback<ApiService.ChapterProgressResponse>() {
            @Override
            public void onResponse(Call<ApiService.ChapterProgressResponse> call, Response<ApiService.ChapterProgressResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    holder.progressBar.setProgress(response.body().progress_percent);
                } else {
                    holder.progressBar.setProgress(0);
                }
            }

            @Override
            public void onFailure(Call<ApiService.ChapterProgressResponse> call, Throwable t) {
                holder.progressBar.setProgress(0);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onChapterClick(chapter);
        });
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle;
        ProgressBar progressBar;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvChapterTitle);
            tvSubtitle = itemView.findViewById(R.id.tvChapterSubtitle);
            progressBar = itemView.findViewById(R.id.progressChapter);
        }
    }
}