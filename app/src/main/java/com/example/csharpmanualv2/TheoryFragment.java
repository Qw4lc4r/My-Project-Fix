package com.example.csharpmanualv2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.csharpmanualv2.model.Material;

public class TheoryFragment extends Fragment {

    private Material material;
    private TextView tvTheoryTitle, tvTheoryContent, tvCodeExample;

    public static TheoryFragment newInstance(Material material) {
        TheoryFragment fragment = new TheoryFragment();
        Bundle args = new Bundle();
        args.putInt("id", material != null ? material.id : 0);
        args.putString("title", material != null ? material.title : "");
        args.putString("content", material != null ? material.content : "");
        args.putString("code", material != null ? material.code_example : "");
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theory, container, false);

        tvTheoryTitle = view.findViewById(R.id.tvTheoryTitle);
        tvTheoryContent = view.findViewById(R.id.tvTheoryContent);
        tvCodeExample = view.findViewById(R.id.tvCodeExample);

        if (getArguments() != null) {
            tvTheoryTitle.setText(getArguments().getString("title", "Теория"));
            tvTheoryContent.setText(getArguments().getString("content", "Нет данных"));
            String code = getArguments().getString("code", "");
            if (code != null && !code.isEmpty()) {
                tvCodeExample.setText(code);
            } else {
                view.findViewById(R.id.cardCode).setVisibility(View.GONE);
            }
        }

        return view;
    }
}
