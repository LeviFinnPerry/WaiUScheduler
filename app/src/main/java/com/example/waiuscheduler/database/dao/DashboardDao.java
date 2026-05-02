package com.example.waiuscheduler.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.waiuscheduler.ui.dashboard.extension.rows.UpcomingAssessments;

@Dao
public interface DashboardDao {
    // Study hours
    ///  Total study hours across all sessions
    @Query("SELECT COALESCE(SUM(dateTimeEnd - dateTimeStart)) AS duration FROM study_session")
    LiveData<Double> getTotalStudyHours();

    /// Study hours grouped by paper
    // TODO: Make a study hour object

    // Grades
    /// Average grade across all graded assignments
    @Query("SELECT COALESCE(AVG(grade),0) FROM assessment")
    LiveData<Double> getAvgGrade();

    /// Grade per paper for course grades
    // TODO: Make a grade total object

    // Events
    /// Count of upcoming events today onwards
    @Query("SELECT COUNT(eventId) FROM event WHERE dateTimeStart >= date('now')")
    LiveData<Integer> getUpcomingEventCount();

    /// Enrolled paper count
    @Query("SELECT COUNT(paperId) FROM paper")
    LiveData<Integer> getTotalPaperCount();

    // Upcoming deadlines
    /// Upcoming assessments ordered by date
    @Query("SELECT title, type, dueDate, paperId_fk FROM assessment WHERE dueDate >= date('now') ORDER BY dueDate ASC LIMIT 10")
    LiveData<UpcomingAssessments> getUpcomingAssessments();
}
