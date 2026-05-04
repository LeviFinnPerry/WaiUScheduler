package com.example.waiuscheduler.ui.dashboard.extension;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.waiuscheduler.database.AppDatabase;
import com.example.waiuscheduler.database.dao.DashboardDao;
import com.example.waiuscheduler.ui.dashboard.extension.rows.CourseGradeRow;
import com.example.waiuscheduler.ui.dashboard.extension.rows.StudyHourRow;
import com.example.waiuscheduler.ui.dashboard.extension.rows.UpcomingAssessments;

import java.util.List;

public class DashboardRepository {
    // Dashboard object
    private final DashboardDao dao;

    /// Initialises dashboard repository and database object
    /// @param application Application
    public DashboardRepository(Application application) {
        dao = AppDatabase.getInstance(application).dashboardDao();
    }

    /// Total hours for study sessions
    /// @return Number of hours
    public LiveData<Double> getTotalStudy() { return dao.getTotalStudyHours(); }

    /// Average grade across all papers
    /// @return grade average
    public LiveData<Double> getAvgGrade() { return dao.getAvgGrade(); }

    /// Number of events still upcoming in the papers
    /// @return amount of events
    public LiveData<Integer> getUpcomingEventCount() { return dao.getUpcomingEventCount(); }

    /// Total enrolled papers
    /// @return number of papers
    public LiveData<Integer> getTotalPaperCount() { return dao.getTotalPaperCount(); }
    /// All assessments that are not already past
    /// @return each assessment
    public LiveData<List<UpcomingAssessments>> getUpcomingAssessments() { return dao.getUpcomingAssessments(); }
    /// Calculated grade for each paper
    /// @return each grade per paper
    public LiveData<List<CourseGradeRow>> getGradesByPaper() { return dao.getGradesByPaper(); }
    /// Get total hours study per paper
    /// @return hours studied per paper
    public LiveData<List<StudyHourRow>> getTotalStudyByPaper() { return dao.getStudyHoursByPaper(); }
}
