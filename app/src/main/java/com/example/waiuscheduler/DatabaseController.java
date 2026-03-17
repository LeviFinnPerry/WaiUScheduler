package com.example.waiuscheduler;

import android.content.Context;

import com.example.waiuscheduler.dao.AssessmentDao;
import com.example.waiuscheduler.dao.EventDao;
import com.example.waiuscheduler.dao.PaperDao;
import com.example.waiuscheduler.dao.SemesterDao;
import com.example.waiuscheduler.dao.StaffDao;
import com.example.waiuscheduler.dao.StudySessionDao;
import com.example.waiuscheduler.dao.TimetablePatternDao;
import com.example.waiuscheduler.database.EventTable;
import com.example.waiuscheduler.database.PaperTable;

public class DatabaseController {
    // Each dao for the corresponding table
    private final AssessmentDao assessmentDao;
    private TimetablePatternDao timetableDao;
    private EventDao eventDao;
    private PaperDao paperDao;
    private SemesterDao semesterDao;
    private StaffDao staffDao;
    private StudySessionDao studyDao;


    public DatabaseController(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.assessmentDao = db.assessmentDao();
        this.eventDao = db.eventDao();
        this.paperDao = db.paperDao();
        this.semesterDao = db.semesterDao();
        this.staffDao = db.staffDao();
        this.studyDao = db.studySessionDao();
        this.timetableDao = db.timetablePatternDao();

    }

    public void savePaper(PaperTable paperOutline) {
        new Thread(() -> paperDao.insert(paperOutline)).start();
    }

    public void saveEvent(EventTable eventTable) {
        new Thread(() -> eventDao.insert(eventTable)).start();
    }
}
