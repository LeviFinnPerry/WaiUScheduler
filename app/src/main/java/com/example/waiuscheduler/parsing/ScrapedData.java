package com.example.waiuscheduler.parsing;

import com.example.waiuscheduler.database.tables.AssessmentTable;
import com.example.waiuscheduler.database.tables.EventTable;
import com.example.waiuscheduler.database.tables.PaperTable;
import com.example.waiuscheduler.database.tables.SemesterTable;
import com.example.waiuscheduler.database.tables.StaffTable;
import com.example.waiuscheduler.database.tables.TimetablePatternTable;

import java.util.ArrayList;
/// Scraped data object to temporarily store information from the paper outline
public class ScrapedData {
    private PaperTable paper;
    // Arraylists to save multiple of each table type
    private ArrayList<StaffTable> staff;
    private ArrayList<AssessmentTable> assessment;
    private ArrayList<EventTable> event;
    private ArrayList<SemesterTable> semester;
    private ArrayList<TimetablePatternTable> timetablePattern;

    /// Constructor to initialise arraylists
    public ScrapedData() {
        this.staff = new ArrayList<>();
        this.assessment = new ArrayList<>();
        this.event = new ArrayList<>();
        this.semester = new ArrayList<>();
        this.timetablePattern = new ArrayList<>();
    }

    /// Public getters and setters for each tables information
    // Paper
    public PaperTable getPaper() {
        return paper;
    }
    public void setPaper(PaperTable paper) {
        this.paper = paper;
    }

    // Staff
    public ArrayList<StaffTable> getStaffs() {
        return staff;
    }
    public void setStaffs(ArrayList<StaffTable> staff) {
        this.staff = staff;
    }

    // Assessment
    public ArrayList<AssessmentTable> getAssessments() {
        return assessment;
    }
    public void setAssessments(ArrayList<AssessmentTable> assessment) {
        this.assessment = assessment;
    }

    // Timetable Pattern
    public ArrayList<TimetablePatternTable> getTimetablePatterns() {
        return timetablePattern;
    }
    public void setTimetablePatterns(ArrayList<TimetablePatternTable> timetablePattern) {
        this.timetablePattern = timetablePattern;
    }

    // Event
    public ArrayList<EventTable> getEvents() {
        return event;
    }
    public void setEvents(ArrayList<EventTable> event) {
        this.event = event;
    }

    // Semester
    public void setSemesters(ArrayList<SemesterTable> semester) {
        this.semester = semester;
    }
    public ArrayList<SemesterTable> getSemesters() {
        return semester;
    }
    // Method to get specific semester
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
