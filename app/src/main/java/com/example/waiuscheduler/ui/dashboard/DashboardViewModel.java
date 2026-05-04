package com.example.waiuscheduler.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.waiuscheduler.ui.dashboard.extension.DashboardRepository;
import com.example.waiuscheduler.ui.dashboard.extension.rows.CourseGradeRow;
import com.example.waiuscheduler.ui.dashboard.extension.rows.StudyHourRow;
import com.example.waiuscheduler.ui.dashboard.extension.rows.UpcomingAssessments;

import java.util.List;

public class DashboardViewModel extends AndroidViewModel {

    // Dashboard repository
    private final DashboardRepository dashRepository;

    /// Initialised view model and repository for dashboard
    /// @param app Application
    public DashboardViewModel(@NonNull Application app) {
        super(app);
        dashRepository = new DashboardRepository(app);
    }

    /// Total hours for study sessions
    /// @return Number of hours
    public LiveData<Double> getTotalStudy() { return dashRepository.getTotalStudy(); }

    /// Average grade across all papers
    /// @return grade average
    public LiveData<Double> getAvgGrade() { return dashRepository.getAvgGrade(); }

    /// Number of events still upcoming in the papers
    /// @return amount of events
    public LiveData<Integer> getUpcomingEventCount() { return dashRepository.getUpcomingEventCount(); }

    /// Total enrolled papers
    /// @return number of papers
    public LiveData<Integer> getTotalPaperCount() { return dashRepository.getTotalPaperCount(); }

    /// All assessments that are not already past
    /// @return each assessment
    public LiveData<List<UpcomingAssessments>> getUpcomingAssessments() { return dashRepository.getUpcomingAssessments(); }

    /// Calculated grade for each paper
    /// @return each grade per paper
    public LiveData<List<CourseGradeRow>> getGradesByPaper() { return dashRepository.getGradesByPaper(); }

    /// Get total hours study per paper
    /// @return hours studied per paper
    public LiveData<List<StudyHourRow>> getTotalStudyByPaper() { return dashRepository.getTotalStudyByPaper(); }

}