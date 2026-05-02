package com.example.waiuscheduler.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.waiuscheduler.R;
import com.example.waiuscheduler.databinding.FragmentDashboardBinding;

import java.util.Locale;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;
    private View openSection = null;

    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState
    ) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        // Functions to set up view
        setUpDropDown();
        setupRecyclerView();
        observeViewModel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setUpDropDown() {
        // Set header titles
        binding.headerStudyHours.sectionTitle.setText(R.string.study_hours_by_course);
        binding.headerCourseGrades.sectionTitle.setText(R.string.grades);
        binding.headerUpcomingAssessments.sectionTitle.setText(R.string.upcoming_assessments);

        // Set on click listeners for each event
        binding.headerStudyHours.getRoot().setOnClickListener(v -> {
            toggleSection(binding.contentStudyHours, binding.headerStudyHours.sectionDropdown);
        });
        binding.headerCourseGrades.getRoot().setOnClickListener(v -> {
            toggleSection(binding.contentCourseGrades, binding.headerCourseGrades.sectionDropdown);
        });
        binding.headerUpcomingAssessments.getRoot().setOnClickListener(v -> {
            toggleSection(binding.contentUpcomingAssessments, binding.headerUpcomingAssessments.sectionDropdown);
        });
    }

    private void setupRecyclerView() {
        binding.recycleviewCourseGrades.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.recycleviewUpcomingAssessments.setLayoutManager(
                new LinearLayoutManager(requireContext()));
    }

    private void observeViewModel() {
        viewModel.getTotalStudy().observe(getViewLifecycleOwner(), hours -> {
            binding.cardStudyHours.statLabel.setText(R.string.total_study_hours);
            binding.cardStudyHours.statValue.setText(
                    String.format(Locale.getDefault(), "%.1f", hours));
        });
        viewModel.getAvgGrade().observe(getViewLifecycleOwner(), grade -> {
            binding.cardStudyHours.statLabel.setText(R.string.average_grade);
            binding.cardStudyHours.statValue.setText(
                    String.format(Locale.getDefault(), "%.1f%%", grade));
        });
        viewModel.getTotalPaperCount().observe(getViewLifecycleOwner(), count -> {
            binding.cardStudyHours.statLabel.setText(R.string.upcoming_events);
            binding.cardStudyHours.statValue.setText(
                    String.valueOf(count));
        });
    }


    /// Opens the selected section and closes any other open section
    private void toggleSection(View content, ImageView icon) {
        boolean isOpen = content.getVisibility()  == View.VISIBLE;

        // Close the currently open section if it is a different one
        if (openSection != null  && openSection != content) {
            openSection.setVisibility(View.GONE);
            rotateIcon(icon);
        }

        if (isOpen) {
            // Close the section
            content.setVisibility(View.GONE);
            iconAnimation(icon, 0f);
            openSection = null;
        } else {
            // Open this section
            content.setVisibility(View.VISIBLE);
            iconAnimation(icon, 180f);
            openSection = content;
        }
    }

    /// Rotates the icon based on section visibility
    private void rotateIcon(ImageView icon) {
        View prevHeader = (View) openSection.getParent();
        ImageView prevIcon = prevHeader.findViewById(R.id.section_dropdown);
        if (prevIcon != null) {
            iconAnimation(icon, 0f);
        }
    }

    /// Animates the icon
    private void iconAnimation(ImageView icon, float rotation) {
        icon.animate().rotation(rotation).setDuration(200).start();
    }
}