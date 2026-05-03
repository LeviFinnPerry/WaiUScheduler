package com.example.waiuscheduler.ui.dashboard.extension.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waiuscheduler.R;
import com.example.waiuscheduler.ui.dashboard.extension.rows.UpcomingAssessments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpcomingAssessmentAdapter extends RecyclerView.Adapter<UpcomingAssessmentAdapter.VH> {

    private final List<UpcomingAssessments> items;
    private final SimpleDateFormat fmt = new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault());

    public UpcomingAssessmentAdapter(List<UpcomingAssessments> items) {
        this.items = items != null ? items: new ArrayList<>();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deadline, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        UpcomingAssessments row = items.get(position);
        holder.textTitle.setText(row.title);
        holder.textDate.setText(fmt.format(row.dueDate));
        holder.textPaper.setText(row.paperId.split("-")[0]);
        String type = row.type;
        holder.textTypeBadge.setText(type);

        // Set badge colour based on type
        setBadgeColor(holder, type);

        // Determine if urgent
        if (ifUrgent(row.dueDate)) {
            holder.textUrgent.setVisibility(View.VISIBLE);
        } else {
            holder.textUrgent.setVisibility(View.GONE);
        }
    }

    private void setBadgeColor(VH holder, String type) {
        int badgeColour;
        switch (type) {
            case "event": badgeColour = 0xFF3949AB; break;
            case "assignment": badgeColour = 0xFFE65100; break;
            case "exam": badgeColour = 0xFFC62828; break;
            default: badgeColour = 0xFF757575; break;
        }
        holder.textTypeBadge.setBackgroundTintList(ColorStateList.valueOf(badgeColour));
    }

    private boolean ifUrgent(Date dueDate) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 2);
        Date newDate = cal.getTime();
        return dueDate.before(newDate);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView textTitle, textDate, textPaper, textTypeBadge, textUrgent;
        VH(View v) {
            super(v);
            textTitle     = v.findViewById(R.id.text_deadline_title);
            textDate      = v.findViewById(R.id.text_deadline_date);
            textPaper     = v.findViewById(R.id.text_deadline_paper);
            textTypeBadge = v.findViewById(R.id.text_deadline_type);
            textUrgent    = v.findViewById(R.id.text_deadline_urgent);
        }
    }

}
