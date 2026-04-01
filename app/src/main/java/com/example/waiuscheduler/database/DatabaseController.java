package com.example.waiuscheduler.database;

import androidx.lifecycle.LiveData;

import com.example.waiuscheduler.database.dao.AssessmentDao;
import com.example.waiuscheduler.database.dao.EventDao;
import com.example.waiuscheduler.database.dao.PaperDao;
import com.example.waiuscheduler.database.dao.SemesterDao;
import com.example.waiuscheduler.database.dao.StaffDao;
import com.example.waiuscheduler.database.dao.StudySessionDao;
import com.example.waiuscheduler.database.dao.TimetablePatternDao;
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
    private final AssessmentDao assessmentDao;
    private final TimetablePatternDao timetableDao;
    private final EventDao eventDao;
    private final PaperDao paperDao;
    private final SemesterDao semesterDao;
    private final StaffDao staffDao;
    private final StudySessionDao studyDao;


    ///  Database controller constructor from database
    /// @param db The database for the app
    public DatabaseController(AppDatabase db) {
        this.assessmentDao = db.assessmentDao();
        this.eventDao = db.eventDao();
        this.paperDao = db.paperDao();
        this.semesterDao = db.semesterDao();
        this.staffDao = db.staffDao();
        this.studyDao = db.studySessionDao();
        this.timetableDao = db.timetablePatternDao();

    }


    /// Function to save to paper table
    /// @param paperOutline Paper details from the outline
    public void savePaper(PaperEntity paperOutline) {
        paperDao.insert(paperOutline);
    }

    /// Function to retrieve all papers in table
    /// @return All the papers in the table
    public LiveData<List<PaperEntity>> getAllPapers() {
        return paperDao.getAllPapers();
    }

    /// Function to delete specific paper
    /// @param paper A previously selected paper
    public void deletePaper(PaperEntity paper) { paperDao.delete(paper); }

    /// Function to save semester
    /// @param semesterEntity A Waikato Uni Semester
    public void saveSemester(SemesterEntity semesterEntity) {
        semesterDao.insert(semesterEntity);
    }

    /// Function to save to event table
    /// @param eventEntity An event created from the timetable pattern
    public void saveEvent(EventEntity eventEntity) {
        eventDao.insert(eventEntity);
    }

    /// Function to save to staff table
    /// @param staffEntity A staff members details
    public void saveStaff(StaffEntity staffEntity) {
        staffDao.insert(staffEntity);
    }

    /// Function to retrieve all staff in table
    /// @return All staff members in the table
    public LiveData<List<StaffEntity>> getAllStaff() {
        return staffDao.getAllStaff();
    }

    /// Function to save the assessment table
    /// @param assessmentEntity An assessment for a paper
    public void saveAssessment(AssessmentEntity assessmentEntity) {
        assessmentDao.insert(assessmentEntity);
    }

    /// Function to save the timetable pattern table
    /// @param timetablePatternEntity A timetable pattern occurance
    public void saveTimetablePattern(TimetablePatternEntity timetablePatternEntity) {
        timetableDao.insert(timetablePatternEntity);
    }

    /// Function to return the semester based on the semester code
    /// @param semesterCode The occurrence code for the semester
    /// @return The semester matching the semester code
    public SemesterEntity getSemesters(String semesterCode) {
        ArrayList<SemesterEntity> semesters =
                (ArrayList<SemesterEntity>) semesterDao.getAllSemesters();

        for (SemesterEntity semester: semesters) {
            if (semester.getSemesterCode().equals(semesterCode)) {
                return semester;
            }
        }
        return null;
    }
}
