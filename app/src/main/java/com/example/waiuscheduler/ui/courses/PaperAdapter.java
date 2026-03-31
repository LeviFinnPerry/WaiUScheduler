package com.example.waiuscheduler.ui.courses;

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
    public void submitPapers(ArrayList<PaperTable> newPapers) {
        papers = newPapers;
        notifyDataSetChanged();
    }

    public static class PaperViewHolder extends RecyclerView.ViewHolder {
        TextView title, occurrence, code;
        ImageButton deleteBtn;

        public PaperViewHolder(@NonNull View itemView) {
            super(itemView);
            code = itemView.findViewById(R.id.paperCode);
            title = itemView.findViewById(R.id.paperTitle);
            occurrence = itemView.findViewById(R.id.paperOccurrence);
            deleteBtn = itemView.findViewById(R.id.paper_delete);
        }
    }

    @NonNull @Override
    public PaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paper, parent, false);
        return new PaperViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return papers == null ? 0 : papers.size();
    }

}
