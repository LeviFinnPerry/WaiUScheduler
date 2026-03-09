package com.example.waiuscheduler.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "event",
        foreignKeys = @ForeignKey(
                entity = TimetablePatternTable.class,   // Class of foreign key
                parentColumns = "timetableId",          // Foreign key variable
                childColumns = "timetableId_fk",        // Foreign key name
                onDelete = ForeignKey.CASCADE           // Dependency on timetable
        ),
        indices = {@Index("timetableId_fk")}            // Index of the foreign key
)
public class EventTable {
    @PrimaryKey(autoGenerate = true)
    private String eventId;     // Unique identifier for the event
    private String eventType;   // Type of event
    private String dateTimeStart;   // Start time
    private String dateTimeEnd;     // End time
    private Boolean attended;       // Monitor attendance of each event
    private ForeignKey timetableId_fk;     // Foreign key references the timetable table

    // Constructor for the event table
    public EventTable(String eventType, String dateTimeStart,
                      String dateTimeEnd, Boolean attended, ForeignKey timetableId_fk
    ) {
        this.eventType = eventType;
        this.dateTimeStart = dateTimeStart;
        this.dateTimeEnd = dateTimeEnd;
        this.attended = attended;
        this.timetableId_fk = timetableId_fk;
    }

    // Getters and setters for each event variable
    // Id

    public String getEventId() {
        return eventId;
    }

    // Type

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    // Start

    public String getDateTimeStart() {
        return dateTimeStart;
    }

    public void setDateTimeStart(String dateTimeStart) {
        this.dateTimeStart = dateTimeStart;
    }

    // End

    public String getDateTimeEnd() {
        return dateTimeEnd;
    }

    public void setDateTimeEnd(String dateTimeEnd) {
        this.dateTimeEnd = dateTimeEnd;
    }


    // Attended

    public Boolean getAttended() {
        return attended;
    }

    public void setAttended(Boolean attended) {
        this.attended = attended;
    }

    // Paper id

    public ForeignKey getTimetableId_fk() {
        return timetableId_fk;
    }

    public void setTimetableId_fk(ForeignKey timetableId_fk) {
        this.timetableId_fk = timetableId_fk;
    }
}
