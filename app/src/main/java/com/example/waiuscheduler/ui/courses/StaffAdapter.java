package com.example.waiuscheduler.ui.courses;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waiuscheduler.R;
import com.example.waiuscheduler.database.tables.StaffTable;

import java.util.ArrayList;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.StaffViewHolder> {

    // TODO: enable the removal of staff members when papers are removed
    private ArrayList<StaffTable> staffMembers;

    /// Constructor for staff adapter
    public StaffAdapter() {
        this.staffMembers = new ArrayList<>();
    }

    /// Submits staff members to the recycler view
    @SuppressLint("NotifyDataSetChanged")
    public void submitStaff(ArrayList<StaffTable> newStaff) {
        staffMembers.clear();
        for (StaffTable staff : newStaff) {
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
        public StaffViewHolder(@NonNull View staffView) {
            super(staffView);
            paperCode = staffView.findViewById(R.id.staff_paper_code);
            staffName = staffView.findViewById(R.id.staff_name);
            staffEmail = staffView.findViewById(R.id.staff_email);
            staffRole = staffView.findViewById(R.id.staff_role);
        }
    }

    /// Returns new staff view holder when initialised
    @NonNull @Override
    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff, parent, false);
        return new StaffViewHolder(view);
    }


    /// Sets data to xml items
    @Override
    public void onBindViewHolder(@NonNull StaffViewHolder holder, int position) {
        StaffTable staff = staffMembers.get(position);
        String code_fk = staff.getPaperId_fk();
        String code = code_fk.split("-")[0];

        holder.paperCode.setText(code);
        holder.staffName.setText(staff.getName());
        holder.staffEmail.setText(staff.getEmail());
        holder.staffRole.setText(staff.getPosition());
    }

    /// Returns count of staff members
    @Override
    public int getItemCount() {
        return staffMembers == null ? 0 : staffMembers.size();
    }
}
