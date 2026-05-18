package com.example.waiuscheduler.ui.courses.extension;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waiuscheduler.R;
import com.example.waiuscheduler.database.tables.StaffEntity;

import java.util.ArrayList;
import java.util.List;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.StaffViewHolder> {
    private final ArrayList<StaffEntity> staffMembers;

    /// Constructor for staff adapter
    public StaffAdapter() {
        this.staffMembers = new ArrayList<>();
    }

    /// Submits staff members to the recycler view
    /// @param newStaff Arraylist of staff entities
    @SuppressLint("NotifyDataSetChanged")
    public void submitStaff(ArrayList<StaffEntity> newStaff) {
        staffMembers.clear();
        staffMembers.addAll(filterConvenors(newStaff));
        notifyDataSetChanged();
    }

    /// Finds staff that are convenors of the paper
    /// @param staff list of staff entities
    /// @return list of staff who are convenors
    private List<StaffEntity> filterConvenors(List<StaffEntity> staff) {
        List<StaffEntity> result = new ArrayList<>();
        for (StaffEntity s: staff) {
            if (isConvenor(s)) result.add(s);
        }
        return result;
    }

    /// Determines whether staff member is a convenor
    /// @param staff staff member
    /// @return true if convenor else false
    private boolean isConvenor(StaffEntity staff) {
        return "Convenor".equalsIgnoreCase(staff.getPosition());
    }

    /// View Holder class to manage the items in the recycler view
    public static class StaffViewHolder extends RecyclerView.ViewHolder {
        TextView paperCode, staffName, staffEmail, staffRole;

        /// Constructor for xml items
        /// @param staffView Recycler view for staff information
        public StaffViewHolder(@NonNull View staffView) {
            super(staffView);
            paperCode = staffView.findViewById(R.id.staff_paper_code);
            staffName = staffView.findViewById(R.id.staff_name);
            staffEmail = staffView.findViewById(R.id.staff_email);
            staffRole = staffView.findViewById(R.id.staff_role);
        }
    }

    /// Initialises staff information view
    /// @param parent View Group
    /// @param viewType Required in override
    /// @return Staff View holder
    @NonNull @Override
    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff, parent, false);
        return new StaffViewHolder(view);
    }


    /// Sets data to xml items
    /// @param holder Staff view holder
    /// @param position Position of the staff member in the view
    @Override
    public void onBindViewHolder(@NonNull StaffViewHolder holder, int position) {
        StaffEntity staff = staffMembers.get(position);
        holder.paperCode.setText(staff.getPaperId().split("-")[0]);
        holder.staffName.setText(staff.getName());
        holder.staffEmail.setText(staff.getEmail());
        holder.staffRole.setText(staff.getPosition());
    }

    /// Item count
    /// @return Size of the staff members table
    @Override
    public int getItemCount() {
        return staffMembers.size();
    }
}
