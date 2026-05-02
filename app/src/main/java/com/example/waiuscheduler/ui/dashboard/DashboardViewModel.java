package com.example.waiuscheduler.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.waiuscheduler.ui.dashboard.extension.DashboardRepository;
import com.example.waiuscheduler.ui.dashboard.extension.rows.CourseGradeRow;
import com.example.waiuscheduler.ui.dashboard.extension.rows.StudyHourRow;
import com.example.waiuscheduler.ui.dashboard.extension.rows.UpcomingAssessments;

import java.util.List;

public class DashboardViewModel extends ViewModel {

    private final DashboardRepository dashRepository;
    
    public DashboardViewModel(@NonNull Application app) {
        dashRepository = new DashboardRepository(app);
    }

    public LiveData<Double> getTotalStudy() { return dashRepository.getTotalStudy(); }

    public LiveData<Double> getAvgGrade() { return dashRepository.getAvgGrade(); }

    public LiveData<Integer> getUpcomingEventCount() { return dashRepository.getUpcomingEventCount(); }

    public LiveData<Integer> getTotalPaperCount() { return dashRepository.getTotalPaperCount(); }
    public LiveData<List<UpcomingAssessments>> getUpcomingAssessments() { return dashRepository.getUpcomingAssessments(); }

    public LiveData<List<CourseGradeRow>> getGradesByPaper() { return dashRepository.getGradesByPaper(); }

    public LiveData<List<StudyHourRow>> getTotalStudyByPaper() { return dashRepository.getTotalStudyByPaper(); }

}