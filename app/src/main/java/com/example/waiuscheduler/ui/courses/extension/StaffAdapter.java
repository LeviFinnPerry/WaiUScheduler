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
        for (StaffEntity staff : newStaff) {
            if (staff.getPosition().matches("Convenor")) {
                staffMembers.add(staff);
            }
        }
        notifyDataSetChanged();
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
        String code_fk = staff.getPaperId_fk();
        String code = code_fk.split("-")[0];

        holder.paperCode.setText(code);
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
