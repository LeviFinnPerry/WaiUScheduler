package com.example.waiuscheduler.ui.dashboard.extension.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waiuscheduler.R;
import com.example.waiuscheduler.ui.dashboard.extension.rows.CourseGradeRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CourseGradeAdapter extends RecyclerView.Adapter<CourseGradeAdapter.VH> {
    private final List<CourseGradeRow> items;

    public CourseGradeAdapter(List<CourseGradeRow> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_grade, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        CourseGradeRow row = items.get(position);
        holder.textPaperId.setText(row.paperId.split("-")[0]);
        holder.textGrade.setText(String.format(Locale.getDefault(), "%.0f%%", row.avgGrade));

        // Set the colour of the item
        setItemColor(holder, row.avgGrade);

        // Progress bar
        holder.progressGrade.setProgress((int) row.avgGrade);
    }

    private void setItemColor(VH holder, Double avgGrade) {
        int colour;
        if (avgGrade >= 70) colour = 0xFF2E7D32;
        else if (avgGrade >= 50) colour = 0xFFE65100;
        else colour = 0xFFC62828;
        holder.textGrade.setTextColor(colour);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView textPaperId, textGrade;
        ProgressBar progressGrade;
        VH(View v) {
            super(v);
            textPaperId = v.findViewById(R.id.text_paper_id);
            textGrade = v.findViewById(R.id.text_grade);
            progressGrade = v.findViewById(R.id.progress_grade);
        }
    }
}
