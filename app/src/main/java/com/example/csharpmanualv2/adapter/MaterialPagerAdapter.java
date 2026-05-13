package com.example.csharpmanualv2.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import android.os.Bundle;

import com.example.csharpmanualv2.PracticeFragment;
import com.example.csharpmanualv2.TheoryFragment;
import com.example.csharpmanualv2.model.Material;

import java.util.List;

public class MaterialPagerAdapter extends FragmentStateAdapter {

    private Material theoryMaterial;
    private Material practiceMaterial;
    private int subchapterId;
    private String subchapterTitle;

    public MaterialPagerAdapter(@NonNull FragmentActivity fragmentActivity,
                                List<Material> materials,
                                int subchapterId, String subchapterTitle) {
        super(fragmentActivity);
        this.subchapterId = subchapterId;
        this.subchapterTitle = subchapterTitle;

        // Распределяем материалы из списка по типу
        if (materials != null) {
            for (Material m : materials) {
                if ("theory".equalsIgnoreCase(m.type)) {
                    this.theoryMaterial = m;
                } else if ("practice".equalsIgnoreCase(m.type)) {
                    this.practiceMaterial = m;
                }
            }
        }

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return TheoryFragment.newInstance(theoryMaterial);
        } else {
            return PracticeFragment.newInstance(practiceMaterial, subchapterId, subchapterTitle);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }


    public static PracticeFragment newInstance(Material material, int subchapterId) {
        PracticeFragment fragment = new PracticeFragment();
        Bundle args = new Bundle();
        args.putString("task", material != null ? material.task_description : "");
        args.putInt("subchapter_id", subchapterId);
        fragment.setArguments(args);
        return fragment;
    }
}