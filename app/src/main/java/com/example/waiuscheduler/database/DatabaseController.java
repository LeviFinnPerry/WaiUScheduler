package com.example.waiuscheduler.database;

import androidx.lifecycle.LiveData;

import com.example.waiuscheduler.database.tables.AssessmentEntity;
import com.example.waiuscheduler.database.tables.EventEntity;
import com.example.waiuscheduler.database.tables.PaperEntity;
import com.example.waiuscheduler.database.tables.SemesterEntity;
import com.example.waiuscheduler.database.tables.StaffEntity;
import com.example.waiuscheduler.database.tables.TimetablePatternEntity;

import java.util.ArrayList;
import java.util.List;

/// Database controller to manage all DAOs
public class DatabaseController {
    // Each dao for the corresponding table
    private final AppDatabase db;


    ///  Database controller constructor from database
    /// @param db The database for the app
    public DatabaseController(AppDatabase db) {
        this.db = db;
    }


    /// Function to save to paper table
    /// @param paperOutline Paper details from the outline
    public void savePaper(PaperEntity paperOutline) {
        db.paperDao().insert(paperOutline);
    }

    /// Function to retrieve all papers in table
    /// @return All the papers in the table
    public LiveData<List<PaperEntity>> getAllPapers() {
        return db.paperDao().getAllPapers();
    }

    /// Function to delete specific paper
    /// @param paper A previously selected paper
    public void deletePaper(PaperEntity paper) { db.paperDao().delete(paper); }

    /// Function to save semester
    /// @param semesterEntity A Waikato Uni Semester
    public void saveSemester(SemesterEntity semesterEntity) {
        db.semesterDao().insert(semesterEntity);
    }

    /// Function to save to event table
    /// @param eventEntity An event created from the timetable pattern
    public void saveEvent(EventEntity eventEntity) {
        db.eventDao().insert(eventEntity);
    }

    /// Function to save to staff table
    /// @param staffEntity A staff members details
    public void saveStaff(StaffEntity staffEntity) {
        db.staffDao().insert(staffEntity);
    }

    /// Function to retrieve all staff in table
    /// @return All staff members in the table
    public LiveData<List<StaffEntity>> getAllStaff() {
        return db.staffDao().getAllStaff();
    }

    /// Function to save the assessment table
    /// @param assessmentEntity An assessment for a paper
    public void saveAssessment(AssessmentEntity assessmentEntity) {
        db.assessmentDao().insert(assessmentEntity);
    }

    /// Function to save the timetable pattern table
    /// @param timetablePatternEntity A timetable pattern occurance
    public void saveTimetablePattern(TimetablePatternEntity timetablePatternEntity) {
        db.timetablePatternDao().insert(timetablePatternEntity);
    }

    /// Function to return the semester based on the semester code
    /// @param semesterCode The occurrence code for the semester
    /// @return The semester matching the semester code
    public SemesterEntity getSemesters(String semesterCode) {
        ArrayList<SemesterEntity> semesters =
                (ArrayList<SemesterEntity>) db.semesterDao().getAllSemesters();

        for (SemesterEntity semester: semesters) {
            if (semester.getSemesterCode().equals(semesterCode)) {
                return semester;
            }
        }
        return null;
    }
}
