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
import com.example.waiuscheduler.database.tables.PaperEntity;

import java.util.ArrayList;

public class PaperAdapter extends RecyclerView.Adapter<PaperAdapter.PaperViewHolder> {
    private ArrayList<PaperEntity> papers = new ArrayList<>();
    private final OnDeleteClickListener deleteClickListener;

    /// Interface for deleting a paper when the button is pressed
    public interface OnDeleteClickListener {
        void onDelete(PaperEntity paper);
    }

    /// Constructor for paper adapter
    /// @param deleteClickListener On Click listener for the delete button
    public PaperAdapter(OnDeleteClickListener deleteClickListener) {
        this.deleteClickListener = deleteClickListener;
    }

    /// Submits papers to the recycler view
    /// @param newPapers New paper entities to add
    @SuppressLint("NotifyDataSetChanged")
    public void submitPapers(ArrayList<PaperEntity> newPapers) {
        papers = newPapers;
        notifyDataSetChanged();
    }

    /// View Holder class to manage items in the recycler view
    public static class PaperViewHolder extends RecyclerView.ViewHolder {
        TextView title, occurrence, code;
        ImageButton deleteBtn;

        /// Constructor for xml items
        /// @param itemView View for the paper information
        public PaperViewHolder(@NonNull View itemView) {
            super(itemView);
            code = itemView.findViewById(R.id.paper_code);
            title = itemView.findViewById(R.id.paper_name);
            occurrence = itemView.findViewById(R.id.paper_occ);
            deleteBtn = itemView.findViewById(R.id.paper_delete);
        }
    }

    /// Initialises paper information view
    /// @param parent View Group
    /// @param viewType Required in override
    /// @return Paper View holder
    @NonNull @Override
    public PaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paper, parent, false);
        return new PaperViewHolder(view);
    }

    /// Sets data to xml items
    /// @param holder Paper view holder
    /// @param position Position of the paper in the view
    @Override
    public void onBindViewHolder(@NonNull PaperViewHolder holder, int position) {
        PaperEntity paper = papers.get(position);
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

    /// Item count
    /// @return Size of papers table
    @Override
    public int getItemCount() {
        return papers == null ? 0 : papers.size();
    }

}
