package com.example.waiuscheduler.parsing;

import com.example.waiuscheduler.database.tables.AssessmentTable;
import com.example.waiuscheduler.database.tables.EventTable;
import com.example.waiuscheduler.database.tables.PaperTable;
import com.example.waiuscheduler.database.tables.SemesterTable;
import com.example.waiuscheduler.database.tables.StaffTable;
import com.example.waiuscheduler.database.tables.TimetablePatternTable;

import java.util.ArrayList;

public class ScrapedData {
    private PaperTable paper;
    private ArrayList<StaffTable> staff;
    private ArrayList<AssessmentTable> assessment;
    private ArrayList<EventTable> event;
    private ArrayList<SemesterTable> semester;

    private ArrayList<TimetablePatternTable> timetablePattern;

    // Constructor
    public ScrapedData() {
        this.staff = new ArrayList<>();
    }

    public PaperTable getPaper() {
        return paper;
    }

    public void setPaper(PaperTable paper) {
        this.paper = paper;
    }

    public ArrayList<StaffTable> getStaffs() {
        return staff;
    }

    public void setStaffs(ArrayList<StaffTable> staff) {
        this.staff = staff;
    }

    public ArrayList<AssessmentTable> getAssessments() {
        return assessment;
    }

    public void setAssessments(ArrayList<AssessmentTable> assessment) {
        this.assessment = assessment;
    }

    public ArrayList<TimetablePatternTable> getTimetablePatterns() {
        return timetablePattern;
    }

    public void setTimetablePatterns(ArrayList<TimetablePatternTable> timetablePattern) {
        this.timetablePattern = timetablePattern;
    }

    public ArrayList<EventTable> getEvents() {
        return event;
    }

    public void setEvents(ArrayList<EventTable> event) {
        this.event = event;
    }

    public void setSemesters(ArrayList<SemesterTable> semester) {
        this.semester = semester;
    }

    public ArrayList<SemesterTable> getSemesters() {
        return semester;
    }

    public SemesterTable getSemester(String semesterCode) {
        if (semester != null) {
            for (SemesterTable s: semester) {
                if (semesterCode.matches(s.getSemesterCode())) {
                    return s;
                }
            }
        }
        return null;
    }
}
