package com.example.csharpmanualv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.csharpmanualv2.model.Material;

public class PracticeFragment extends Fragment {

    private TextView tvTaskDescription;
    private Button btnStartTask;

    public static PracticeFragment newInstance(Material material, int subchapterId, String subchapterTitle) {
        PracticeFragment fragment = new PracticeFragment();
        Bundle args = new Bundle();
        args.putString("task", material != null ? material.task_description : "Нажмите 'Начать задание' для прохождения теста");
        args.putInt("subchapter_id", subchapterId);
        args.putString("subchapter_title", subchapterTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_practice, container, false);

        tvTaskDescription = view.findViewById(R.id.tvTaskDescription);
        btnStartTask = view.findViewById(R.id.btnStartTask);

        if (getArguments() != null) {
            String task = getArguments().getString("task", "");
            tvTaskDescription.setText(task);
        }

        btnStartTask.setOnClickListener(v -> {
            int subchapterId = getArguments().getInt("subchapter_id", 0);
            String title = getArguments().getString("subchapter_title", "Тест");

            Intent intent = new Intent(requireActivity(), QuizActivity.class);
            intent.putExtra("subchapterId", subchapterId);
            intent.putExtra("title", title);
            startActivity(intent);
        });

        return view;
    }
}
