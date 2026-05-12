package com.example.waiuscheduler.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.waiuscheduler.ui.dashboard.extension.rows.CourseGradeRow;
import com.example.waiuscheduler.ui.dashboard.extension.rows.StudyHourRow;
import com.example.waiuscheduler.ui.dashboard.extension.rows.UpcomingAssessments;

import java.util.List;

@Dao
public interface DashboardDao {

    // Study hours
    ///  Total study hours across all sessions
    @Query("SELECT COALESCE(SUM(dateTimeEnd - dateTimeStart) / 3600000.0, 0) AS duration FROM study_session")
    LiveData<Double> getTotalStudyHours();

    /// Study hours grouped by paper
    @Query("SELECT paperId, SUM(dateTimeEnd - dateTimeStart) / 3600000.0 AS hours FROM study_session GROUP BY paperId ORDER BY hours DESC")
    LiveData<List<StudyHourRow>> getStudyHoursByPaper();

    // Grades
    /// Average grade across all graded assignments
    @Query("SELECT COALESCE(AVG(grade),0) FROM assessment")
    LiveData<Double> getAvgGrade();

    /// Grade per paper for course grades
    @Query("SELECT paperId, COALESCE(AVG(grade), 0) AS avgGrade, COUNT(*) AS total, " +
            "SUM(CASE WHEN grade IS NOT NULL THEN 1 ELSE 0 END) AS graded FROM assessment " +
            "GROUP BY paperId")
    LiveData<List<CourseGradeRow>> getGradesByPaper();

    // Events
    /// Count of upcoming events today onwards
    @Query("SELECT COUNT(eventId) FROM event WHERE dateTimeStart >= :nowMs")
    LiveData<Integer> getUpcomingEventCount(long nowMs);

    /// Enrolled paper count
    @Query("SELECT COUNT(paperId) FROM paper")
    LiveData<Integer> getTotalPaperCount();

    // Upcoming deadlines
    /// Upcoming assessments ordered by date
    @Query("SELECT title, type, dueDate, paperId FROM assessment WHERE dueDate >= :nowMs ORDER BY dueDate ASC LIMIT 10")
    LiveData<List<UpcomingAssessments>> getUpcomingAssessments(long nowMs);
}
