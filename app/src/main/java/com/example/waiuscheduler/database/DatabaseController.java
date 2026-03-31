package com.example.waiuscheduler.database;

import androidx.lifecycle.LiveData;

import com.example.waiuscheduler.dao.AssessmentDao;
import com.example.waiuscheduler.dao.EventDao;
import com.example.waiuscheduler.dao.PaperDao;
import com.example.waiuscheduler.dao.SemesterDao;
import com.example.waiuscheduler.dao.StaffDao;
import com.example.waiuscheduler.dao.StudySessionDao;
import com.example.waiuscheduler.dao.TimetablePatternDao;
import com.example.waiuscheduler.database.tables.AssessmentTable;
import com.example.waiuscheduler.database.tables.EventTable;
import com.example.waiuscheduler.database.tables.PaperTable;
import com.example.waiuscheduler.database.tables.SemesterTable;
import com.example.waiuscheduler.database.tables.StaffTable;
import com.example.waiuscheduler.database.tables.TimetablePatternTable;

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
    public void savePaper(PaperTable paperOutline) {
        paperDao.insert(paperOutline);
    }

    /// Function to retrieve all papers in table
    public LiveData<List<PaperTable>> getAllPapers() {
        return paperDao.getAllPapers();
    }

    /// Function to delete specific paper
    public void deletePaper(PaperTable paper) { paperDao.delete(paper); }

    /// Function to save semester
    public void saveSemester(SemesterTable semesterTable) {
        semesterDao.insert(semesterTable);
    }

    /// Function to save to event table
    public void saveEvent(EventTable eventTable) {
        eventDao.insert(eventTable);
    }

    /// Function to save to staff table
    public void saveStaff(StaffTable staffTable) {
        staffDao.insert(staffTable);
    }

    /// Function to retrieve all staff in table
    public LiveData<List<StaffTable>> getAllStaff() {
        return staffDao.getAllStaff();
    }

    /// Function to save the assessment table
    public void saveAssessment(AssessmentTable assessmentTable) {
        assessmentDao.insert(assessmentTable);
    }

    /// Function to save the timetable pattern table
    public void saveTimetablePattern(TimetablePatternTable timetablePatternTable) {
        timetableDao.insert(timetablePatternTable);
    }

    /// Function to return the semester based on the semester code
    public SemesterTable getSemesterTable(String semesterCode) {
        ArrayList<SemesterTable> semesters =
                (ArrayList<SemesterTable>) semesterDao.getAllSemesters();

        for (SemesterTable semester: semesters) {
            if (semester.getSemesterCode().equals(semesterCode)) {
                return semester;
            }
        }
        return null;
    }
}
