package com.example.waiuscheduler.ui.courses;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waiuscheduler.R;
import com.example.waiuscheduler.database.tables.PaperTable;

import java.util.ArrayList;

public class PaperAdapter extends RecyclerView.Adapter<PaperAdapter.PaperViewHolder> {
    private ArrayList<PaperTable> papers = new ArrayList<>();
    private final OnDeleteClickListener deleteClickListener;

    /// Interface for deleting a paper when the button is pressed
    public interface OnDeleteClickListener {
        void onDelete(PaperTable paper);
    }

    /// Constructor for paper adapter
    public PaperAdapter(OnDeleteClickListener deleteClickListener) {
        this.deleteClickListener = deleteClickListener;
    }

    /// Submits papers to the recycler view
    @SuppressLint("NotifyDataSetChanged")
    public void submitPapers(ArrayList<PaperTable> newPapers) {
        papers = newPapers;
        notifyDataSetChanged();
    }

    /// View Holder class to manage items in the recycler view
    public static class PaperViewHolder extends RecyclerView.ViewHolder {
        TextView title, occurrence, code;
        ImageButton deleteBtn;

        /// Constructor for xml items
        public PaperViewHolder(@NonNull View itemView) {
            super(itemView);
            code = itemView.findViewById(R.id.staff_paper_code);
            title = itemView.findViewById(R.id.staff_name);
            occurrence = itemView.findViewById(R.id.staff_email);
            deleteBtn = itemView.findViewById(R.id.paper_delete);
        }
    }

    /// Returns new paper view holder when initialised
    @NonNull @Override
    public PaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paper, parent, false);
        return new PaperViewHolder(view);
    }

    /// Sets data to xml items
    @Override
    public void onBindViewHolder(@NonNull PaperViewHolder holder, int position) {
        PaperTable paper = papers.get(position);
        String title = paper.getPaperCode();
        if (title.length() >= 25) {
            title = title.substring(0,25);
        }
        holder.code.setText(paper.getPaperName());
        holder.title.setText(title);
        holder.occurrence.setText(paper.getSemesterCode_fk());

        holder.deleteBtn.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                new Thread(() -> deleteClickListener.onDelete(paper)).start();
            }
        });
    }

    /// Returns count of papers
    @Override
    public int getItemCount() {
        return papers == null ? 0 : papers.size();
    }

}
