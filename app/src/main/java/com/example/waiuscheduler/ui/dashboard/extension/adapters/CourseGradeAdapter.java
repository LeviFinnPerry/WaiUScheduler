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

    /// Initialises the items to the course grade adapter
    /// @param items grades and papers
    public CourseGradeAdapter(List<CourseGradeRow> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    /// Create view holder for course grades
    /// @param parent View group
    /// @param viewType used in override
    /// @return custom view holder
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_grade, parent, false);
        return new VH(v);
    }

    /// View holder to update course grade information
    /// @param holder custom view holder
    /// @param position position of grade section
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

    /// Sets the item colour based on the grade
    /// @param holder custom view holder
    /// @param avgGrade average grade
    private void setItemColor(VH holder, Double avgGrade) {
        int colour;
        if (avgGrade >= 70) colour = 0xFF2E7D32;
        else if (avgGrade >= 50) colour = 0xFFE65100;
        else colour = 0xFFC62828;
        holder.textGrade.setTextColor(colour);
    }

    /// Amount of items
    /// @return total items
    @Override
    public int getItemCount() {
        return items.size();
    }

    /// Static class for the custom view holder
    /// @extends RecyclerView.ViewHolder
    public static class VH extends RecyclerView.ViewHolder {
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
