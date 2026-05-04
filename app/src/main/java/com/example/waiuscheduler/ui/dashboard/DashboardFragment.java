package com.example.waiuscheduler.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
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
import com.example.waiuscheduler.ui.dashboard.extension.adapters.CourseGradeAdapter;
import com.example.waiuscheduler.ui.dashboard.extension.adapters.UpcomingAssessmentAdapter;
import com.example.waiuscheduler.ui.dashboard.extension.rows.StudyHourRow;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    // Private variables
    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;
    private View openSection = null;

    /// Initialises the dashboard view
    /// @param inflater Layout inflater
    /// @param container View group
    /// @param savedInstanceState Bundle
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState
    ) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /// Methods once the view is created
    /// @param view created view
    /// @param savedInstanceState Bundle
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        // Functions to set up view
        setUpDropDown();
        setUpDropDownListeners();
        setupRecyclerView();
        observeViewModel();
    }

    /// Destroys view
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /// Sets up UI in drop down menu
    private void setUpDropDown() {
        // Set header titles
        binding.headerStudyHours.sectionTitle.setText(R.string.study_hours_by_course);
        binding.headerCourseGrades.sectionTitle.setText(R.string.grades);
        binding.headerUpcomingAssessments.sectionTitle.setText(R.string.upcoming_assessments);
    }

    /// Set on click listeners for each event
    private void setUpDropDownListeners() {
        binding.headerStudyHours.getRoot().setOnClickListener(v -> toggleSection(binding.contentStudyHours, binding.headerStudyHours.sectionDropdown));
        binding.headerCourseGrades.getRoot().setOnClickListener(v -> toggleSection(binding.contentCourseGrades, binding.headerCourseGrades.sectionDropdown));
        binding.headerUpcomingAssessments.getRoot().setOnClickListener(v -> toggleSection(binding.contentUpcomingAssessments, binding.headerUpcomingAssessments.sectionDropdown));
    }

    /// Set up managers for the recycler views
    private void setupRecyclerView() {
        binding.recycleviewCourseGrades.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.recycleviewUpcomingAssessments.setLayoutManager(
                new LinearLayoutManager(requireContext()));
    }

    /// Observe all the elements in the UI
    private void observeViewModel() {
        // Total Study Hours
        viewModel.getTotalStudy().observe(getViewLifecycleOwner(), hours -> {
            binding.cardStudyHours.statLabel.setText(R.string.total_study_hours);
            binding.cardStudyHours.statValue.setText(
                    String.format(Locale.getDefault(), "%.1f", hours));
        });
        // Grade average
        viewModel.getAvgGrade().observe(getViewLifecycleOwner(), grade -> {
            binding.cardAvgGrade.statLabel.setText(R.string.average_grade);
            binding.cardAvgGrade.statValue.setText(
                    String.format(Locale.getDefault(), "%.1f%%", grade));
        });
        // Paper count
        viewModel.getTotalPaperCount().observe(getViewLifecycleOwner(), count -> {
            binding.cardCourses.statLabel.setText(R.string.enrolled_courses);
            binding.cardCourses.statValue.setText(
                    String.valueOf(count));
        });
        // Upcoming events
        viewModel.getUpcomingEventCount().observe(getViewLifecycleOwner(), count -> {
            binding.cardEvents.statLabel.setText(R.string.upcoming_events);
            binding.cardEvents.statValue.setText(String.valueOf(count));
        });
        // Study hours by paper
        viewModel.getTotalStudyByPaper().observe(getViewLifecycleOwner(), this::setUpBarChart);
        // Grades by paper
        viewModel.getGradesByPaper().observe(getViewLifecycleOwner(), rows -> {
            Log.d("Dashboard", "Grades returned: " + rows.size());
            binding.recycleviewCourseGrades.setAdapter(new CourseGradeAdapter(rows));
        });
        // Upcoming events
        viewModel.getUpcomingAssessments().observe(getViewLifecycleOwner(), rows -> {
            Log.d("Dashboard", "Assessments returned: " + rows.size());
            binding.recycleviewUpcomingAssessments.setAdapter(new UpcomingAssessmentAdapter(rows));
        });
    }


    /// Opens the selected section and closes any other open section
    /// @param content View selected
    /// @param icon Icon to toggle
    private void toggleSection(View content, ImageView icon) {
        boolean isOpen = content.getVisibility()  == View.VISIBLE;

        // Close the currently open section if it is a different one
        if (openSection != null  && openSection != content) {
            openSection.setVisibility(View.GONE);
            rotateIcon();
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
    private void rotateIcon() {
        View prevHeader = (View) openSection.getParent();
        ImageView prevIcon = prevHeader.findViewById(R.id.section_dropdown);
        if (prevIcon != null) {
            iconAnimation(prevIcon, 0f);
        }
    }

    /// Animates the icon
    /// @param icon Icon to rotate
    /// @param rotation rotation amount (degrees)
    private void iconAnimation(ImageView icon, float rotation) {
        icon.animate().rotation(rotation).setDuration(200).start();
    }

    /// Sets up the bar chart for the total study hours
    /// @param rows Study hours per paper
    private void setUpBarChart(List<StudyHourRow> rows) {
        if (rows == null || rows.isEmpty()) return;

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            entries.add(new BarEntry(i, (float) rows.get(i).hours));
            // Shorten paper id for label
            labels.add(rows.get(i).paperId_fk.split("-")[0]);
        }

        // Set up bar data
        BarData barData = setBarData(entries);

        // Set up bar chart
        drawChart(barData, labels);
    }

    /// Sets the data for the bar chart
    /// @param entries data
    private BarData setBarData(List<BarEntry> entries) {
        // Dataset
        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColor(0xFF3949AB);
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(10f);

        // Bar data from dataset
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);
        return barData;
    }

    /// Draws the chart
    /// @param barData created barData
    /// @param labels labels for each paper
    private void drawChart(BarData barData, List<String> labels) {
        BarChart chart = binding.chartStudyHours;
        chart.setData(barData);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setFitBars(true);
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setDrawGridLines(false);
        chart.animateY(600);
        chart.invalidate();
    }
}