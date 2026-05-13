package com.example.csharpmanualv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csharpmanualv2.R;
import com.example.csharpmanualv2.model.Subchapter;

import java.util.ArrayList;
import java.util.List;

public class SubchaptersAdapter extends RecyclerView.Adapter<SubchaptersAdapter.SubchapterViewHolder> {

    private List<Subchapter> subchapters = new ArrayList<>();
    private OnSubchapterClickListener listener;

    public interface OnSubchapterClickListener {
        void onSubchapterClick(Subchapter subchapter);
    }

    public SubchaptersAdapter(OnSubchapterClickListener listener) {
        this.listener = listener;
    }

    public void setSubchapters(List<Subchapter> subchapters) {
        this.subchapters = subchapters;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubchapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subchapter, parent, false);
        return new SubchapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubchapterViewHolder holder, int position) {
        Subchapter sub = subchapters.get(position);
        holder.tvCode.setText(sub.code);
        holder.tvTitle.setText(sub.title);
        holder.tvDescription.setText(sub.description);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onSubchapterClick(sub);
        });
    }

    @Override
    public int getItemCount() {
        return subchapters.size();
    }

    static class SubchapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvTitle, tvDescription;

        public SubchapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvSubCode);
            tvTitle = itemView.findViewById(R.id.tvSubTitle);
            tvDescription = itemView.findViewById(R.id.tvSubDescription);
        }
    }
}