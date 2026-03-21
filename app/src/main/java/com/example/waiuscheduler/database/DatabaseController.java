package com.example.waiuscheduler.database;

import com.example.waiuscheduler.dao.AssessmentDao;
import com.example.waiuscheduler.dao.EventDao;
import com.example.waiuscheduler.dao.PaperDao;
import com.example.waiuscheduler.dao.SemesterDao;
import com.example.waiuscheduler.dao.StaffDao;
import com.example.waiuscheduler.dao.StudySessionDao;
import com.example.waiuscheduler.dao.TimetablePatternDao;
import com.example.waiuscheduler.database.tables.EventTable;
import com.example.waiuscheduler.database.tables.PaperTable;
import com.example.waiuscheduler.database.tables.StaffTable;

public class DatabaseController {
    // Each dao for the corresponding table
    private final AssessmentDao assessmentDao;
    private TimetablePatternDao timetableDao;
    private EventDao eventDao;
    private PaperDao paperDao;
    private SemesterDao semesterDao;
    private StaffDao staffDao;
    private StudySessionDao studyDao;


    public DatabaseController(AppDatabase db) {
        this.assessmentDao = db.assessmentDao();
        this.eventDao = db.eventDao();
        this.paperDao = db.paperDao();
        this.semesterDao = db.semesterDao();
        this.staffDao = db.staffDao();
        this.studyDao = db.studySessionDao();
        this.timetableDao = db.timetablePatternDao();

    }

    // One method to save all data from a single paper
    // Function to save to paper table
    public long savePaper(PaperTable paperOutline) {
        return paperDao.insert(paperOutline);
    }

    // Function to save to event table
    public void saveEvent(EventTable eventTable) {
        eventDao.insert(eventTable);
    }

    // Function to save to staff table
    public void saveStaff(StaffTable staffTable) {
        staffDao.insert(staffTable);
    }
}
