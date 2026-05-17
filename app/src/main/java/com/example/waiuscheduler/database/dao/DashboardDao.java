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
    /// @return total hours studied
    @Query("SELECT SUM(duration) FROM study_session")
    LiveData<Double> getTotalStudyHours();

    /// Study hours grouped by paper
    /// @return total hours studied per paper
    @Query("SELECT paperId, SUM(duration) AS hours FROM study_session GROUP BY paperId ORDER BY hours DESC")
    LiveData<List<StudyHourRow>> getStudyHoursByPaper();

    // Grades
    /// Average grade across all graded assignments
    /// @return average of all grades
    @Query("SELECT COALESCE(AVG(grade),0) FROM assessment") // 0 if grade is null
    LiveData<Double> getAvgGrade();

    /// Grade per paper for course grades
    /// @return average of grades per paper
    @Query("SELECT paperId, COALESCE(AVG(grade), 0) AS avgGrade, COUNT(*) AS total, " +
            "SUM(CASE WHEN grade IS NOT NULL THEN 1 ELSE 0 END) AS graded FROM assessment " +
            "GROUP BY paperId")
    LiveData<List<CourseGradeRow>> getGradesByPaper();

    // Events
    /// Count of upcoming events today onwards
    /// @return number of events
    @Query("SELECT COUNT(eventId) FROM event WHERE dateTimeStart >= :nowMs")
    LiveData<Integer> getUpcomingEventCount(long nowMs);

    /// Enrolled paper count
    /// @return number of enrolled papers
    @Query("SELECT COUNT(paperId) FROM paper")
    LiveData<Integer> getTotalPaperCount();

    // Upcoming deadlines
    /// Upcoming assessments ordered by date
    /// @return all upcoming assessments
    @Query("SELECT title, type, dueDate, paperId FROM assessment WHERE dueDate >= :nowMs ORDER BY dueDate ASC LIMIT 10")
    LiveData<List<UpcomingAssessments>> getUpcomingAssessments(long nowMs);
}
