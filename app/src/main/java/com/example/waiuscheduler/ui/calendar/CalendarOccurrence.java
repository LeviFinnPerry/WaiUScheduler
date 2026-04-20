package com.example.waiuscheduler.ui.calendar;

import com.example.waiuscheduler.database.tables.AssessmentEntity;
import com.example.waiuscheduler.database.tables.EventEntity;
import com.example.waiuscheduler.database.tables.StudySessionEntity;

import java.util.Date;

public class CalendarOccurrence {
    private String type;
    private final String colour;
    private Date startDateTime;
    private Date endDateTime;

    /// Converts different events into a uniform calendar object
    private CalendarOccurrence(String type, Date startDateTime, Date endDateTime) {
        this.type = type;
        this.colour = findColour(type);
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /// Converts an assessment entity into a calendar object
    /// @param assessment Occurrence of an assessment
    /// @return Calendar object representing the assessment
    public CalendarOccurrence assessmentToCalendar(AssessmentEntity assessment) {
        String type = assessment.getType();
        Date startDateTime = assessment.getDueDate();
        Date endDateTime = assessment.getDueDate();

        return new CalendarOccurrence(type, startDateTime, endDateTime);
    }

    /// Converts an event entity into a calendar object
    /// @param event Occurrence of an event
    /// @return Calendar object representing the event
    public CalendarOccurrence eventToCalendar(EventEntity event) {
        String type = "Event";
        Date startDateTime = event.getDateTimeStart();
        Date endDateTime = event.getDateTimeEnd();

        return new CalendarOccurrence(type, startDateTime, endDateTime);
    }

    /// Converts a study session entity into a calendar object
    /// @param study Occurrence of an study session
    /// @return Calendar object representing the study session
    public CalendarOccurrence studyToCalendar(StudySessionEntity study) {
        String type = "Study";
        Date startDateTime = study.getDateTimeStart();
        Date endDateTime = study.getDateTimeEnd();

        return new CalendarOccurrence(type, startDateTime, endDateTime);
    }

    /// Determines colour for the types of events
    /// @param type type of event
    /// @return colour
    private String findColour(String type) {
        // TODO: Include different assessment types and event types
        if (type.matches("Study")) {
            return "Blue";
        } else if (type.matches("Assessment")) {
            return "Red" ;
        } else if (type.matches("Lecture")) {
            return "Green";
        }
        return null;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColour() {
        return colour;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }
}
