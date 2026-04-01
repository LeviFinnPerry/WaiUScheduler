package com.example.waiuscheduler.parsing;

import com.example.waiuscheduler.database.tables.AssessmentEntity;
import com.example.waiuscheduler.database.tables.EventEntity;
import com.example.waiuscheduler.database.tables.PaperEntity;
import com.example.waiuscheduler.database.tables.StaffEntity;
import com.example.waiuscheduler.database.tables.TimetablePatternEntity;

import java.util.ArrayList;
/// Scraped data object to temporarily store information from the paper outline
public class ScrapedData {
    private PaperEntity paper;
    // Arraylists to save multiple of each table type
    private ArrayList<StaffEntity> staff;
    private ArrayList<AssessmentEntity> assessment;
    private ArrayList<EventEntity> event;
    private ArrayList<TimetablePatternEntity> timetablePattern;

    /// Constructor to initialise arraylists
    public ScrapedData() {
        this.staff = new ArrayList<>();
        this.assessment = new ArrayList<>();
        this.event = new ArrayList<>();
        this.timetablePattern = new ArrayList<>();
    }

    /// Get paper
    /// @return paper entity
    public PaperEntity getPaper() {
        return paper;
    }

    /// Set paper
    /// @param paper paper entity
    public void setPaper(PaperEntity paper) {
        this.paper = paper;
    }

    /// Get all staff
    /// @return All staff in the table
    public ArrayList<StaffEntity> getStaffs() {
        return staff;
    }

    /// Set all staff
    /// @param staff Arraylist of staff entities
    public void setStaffs(ArrayList<StaffEntity> staff) {
        this.staff = staff;
    }

    /// Get all assessments
    /// @return All assessments in the table
    public ArrayList<AssessmentEntity> getAssessments() {
        return assessment;
    }

    /// Set all assessments
    /// @param assessment Arraylist of assessment entities
    public void setAssessments(ArrayList<AssessmentEntity> assessment) {
        this.assessment = assessment;
    }

    /// Get all timetable occurrences
    /// @return All timetable patterns in the table
    public ArrayList<TimetablePatternEntity> getTimetablePatterns() {
        return timetablePattern;
    }

    /// Set all timetable occurrences
    /// @param timetablePattern Arraylist of timetable entities
    public void setTimetablePatterns(ArrayList<TimetablePatternEntity> timetablePattern) {
        this.timetablePattern = timetablePattern;
    }

    /// Get all events
    /// @return All events in the table
    public ArrayList<EventEntity> getEvents() {
        return event;
    }

    /// Set all events
    /// @param event Arraylist of event entities
    public void setEvents(ArrayList<EventEntity> event) {
        this.event = event;
    }
}
