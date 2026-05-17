package com.example.waiuscheduler.ui.dashboard.extension.adapters;

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

    /// Initialises the items to the upcoming assessments adapter
    /// @param items upcoming assessments
    public UpcomingAssessmentAdapter(List<UpcomingAssessments> items) {
        this.items = items != null ? items: new ArrayList<>();
    }

    /// Create view holder for upcoming assessments
    /// @param parent View group
    /// @param viewType used in override
    /// @return custom view holder
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deadline, parent, false);
        return new VH(v);
    }

    /// View holder to update assessment information
    /// @param holder custom view holder
    /// @param position position of grade section
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
        // Set urgency based on date
        setUrgency(holder, row.dueDate);
    }

    /// Sets visibility based on urgency
    /// @param holder View holder
    /// @param dueDate assessment due date
    private void setUrgency(VH holder, Date dueDate) {
        // Determine if urgent
        if (ifUrgent(dueDate)) {
            holder.textUrgent.setVisibility(View.VISIBLE);
        } else {
            holder.textUrgent.setVisibility(View.GONE);
        }
    }

    /// Sets the item colour based on the assessment type
    /// @param holder custom view holder
    /// @param type assessment type
    private void setBadgeColor(VH holder, String type) {
        int badgeColour;
        switch (type) {
            case "Report": badgeColour = 0xFF3949AB; break;
            case "Assignment": badgeColour = 0xFFE65100; break;
            case "Exam": badgeColour = 0xFFC62828; break;
            default: badgeColour = 0xFF757575; break;
        }
        holder.textTypeBadge.setBackgroundColor(badgeColour);
    }

    /// If the assessment is due within two days
    /// @param dueDate due date of assessment
    /// @return true if urgent else false
    private boolean ifUrgent(Date dueDate) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 2);
        Date newDate = cal.getTime();
        return dueDate.before(newDate);
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
