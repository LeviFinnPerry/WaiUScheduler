package com.example.waiuscheduler.ui.calendar;

import com.example.waiuscheduler.database.tables.AssessmentEntity;
import com.example.waiuscheduler.database.tables.EventEntity;
import com.example.waiuscheduler.database.tables.StudySessionEntity;

import java.util.Date;

public class CalendarOccurrence {

    // Constant variables
    public static final String TYPE_ASSESSMENT = "Assessment";
    public static final String TYPE_EVENT = "Event";
    public static final String TYPE_STUDY = "Study Session";

    // Field variables
    private final String type;
    private final String title;
    private final int colour;
    private final Date startDateTime;
    private final Date endDateTime;
    private final Object sourceEntity;  // For dialog actions

    /// Converts different events into a uniform calendar object
    private CalendarOccurrence(String type, String title, Date start, Date end, Object src) {
        this.type = type;
        this.title = title;
        this.colour = findColour(type);
        this.startDateTime = start;
        this.endDateTime = end;
        this.sourceEntity = src;
    }

    /// Converts an assessment entity into a calendar object
    /// @param assessment Occurrence of an assessment
    /// @return Calendar object representing the assessment
    public static CalendarOccurrence from(AssessmentEntity assessment) {
        // TODO: Separate start and end of day for assignment due dates
        return new CalendarOccurrence(
                TYPE_ASSESSMENT,
                assessment.getTitle(),
                assessment.getDueDate(),
                assessment.getDueDate(),
                assessment
        );
    }

    /// Converts an event entity into a calendar object
    /// @param event Occurrence of an event
    /// @return Calendar object representing the event
    public static CalendarOccurrence from(EventEntity event) {
        return new CalendarOccurrence(
                TYPE_EVENT,
                event.getTimetableId_fk(),
                event.getDateTimeStart(),
                event.getDateTimeEnd(),
                event
        );
    }

    /// Converts a study session entity into a calendar object
    /// @param study Occurrence of an study session
    /// @return Calendar object representing the study session
    public static CalendarOccurrence from(StudySessionEntity study) {
        return new CalendarOccurrence(
                TYPE_STUDY,
                study.getPaperId_fk().split("-")[0] != null ? study.getPaperId_fk() : "Study Session",
                study.getDateTimeStart(),
                study.getDateTimeEnd(),
                study
        );
    }

    /// Determines title for the types of events
    /// @param type type of event
    /// @return title
    private static int findColour(String type) {
        // TODO: Include different assessment types and event types
        switch (type) {
            case TYPE_STUDY: return 0xFF0000FF; // Blue
            case TYPE_ASSESSMENT: return 0xFFFF0000; // Red
            case TYPE_EVENT: return 0xFF00FF00; // Green
            default: return 0xFF757575; // Grey
        }
    }

    /// Get type of event
    /// @return type
    public String getType() {
        return type;
    }

    /// Get colour
    /// @return colour
    public int getColour() {
        return colour;
    }

    /// Get title of event
    /// @return title
    public String getTitle() {
        return title;
    }

    /// Get start of event
    /// @return start date and time
    public Date getStartDateTime() {
        return startDateTime;
    }

    /// Get end of event
    /// @return end date and time
    public Date getEndDateTime() {
        return endDateTime;
    }

    /// Get source of event information
    /// @return table for event type
    public Object getSourceEntity() {
        return sourceEntity;
    }
}
