package com.example.waiuscheduler.ui.dashboard.extension;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.waiuscheduler.database.AppDatabase;
import com.example.waiuscheduler.database.dao.DashboardDao;
import com.example.waiuscheduler.ui.dashboard.extension.rows.CourseGradeRow;
import com.example.waiuscheduler.ui.dashboard.extension.rows.StudyHourRow;
import com.example.waiuscheduler.ui.dashboard.extension.rows.UpcomingAssessments;

import java.util.List;

public class DashboardRepository {
    private final DashboardDao dao;

    public DashboardRepository(Context context) {
        dao = AppDatabase.getInstance(context).dashboardDao();
    }

    public LiveData<Double> getTotalStudy() { return dao.getTotalStudyHours(); }

    public LiveData<Double> getAvgGrade() { return dao.getAvgGrade(); }

    public LiveData<Integer> getUpcomingEventCount() { return dao.getUpcomingEventCount(); }

    public LiveData<Integer> getTotalPaperCount() { return dao.getTotalPaperCount(); }
    public LiveData<List<UpcomingAssessments>> getUpcomingAssessments() { return dao.getUpcomingAssessments(); }

    public LiveData<List<CourseGradeRow>> getGradesByPaper() { return dao.getGradesByPaper(); }

    public LiveData<List<StudyHourRow>> getTotalStudyByPaper() { return dao.getStudyHoursByPaper(); }
}
