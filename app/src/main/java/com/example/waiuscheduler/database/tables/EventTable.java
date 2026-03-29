package com.example.waiuscheduler.database.tables;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;


/// Entity for event table
@Entity(tableName = "event",
        foreignKeys = @ForeignKey(
                entity = TimetablePatternTable.class,   // Class of foreign key
                parentColumns = "type",          // Foreign key variable
                childColumns = "timetableId_fk",        // Foreign key name
                onDelete = ForeignKey.CASCADE           // Dependency on timetable
        ),
        indices = {@Index("timetableId_fk")}            // Index of the foreign key
)
public class EventTable {
    @PrimaryKey(autoGenerate = true)
    private int eventId;     // Unique identifier for the event
    private Date dateTimeStart;   // Start time
    private Date dateTimeEnd;     // End time
    private Boolean attended;       // Monitor attendance of each event
    private String timetableId_fk;     // Foreign key references the timetable table

    /// Constructor for the event table
    public EventTable(Date dateTimeStart,
                      Date dateTimeEnd, Boolean attended, String timetableId_fk
    ) {
        this.dateTimeStart = dateTimeStart;
        this.dateTimeEnd = dateTimeEnd;
        this.attended = attended;
        this.timetableId_fk = timetableId_fk;
    }

    /// Getters and setters for each event variable
    // Id
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
    public int getEventId() {
        return eventId;
    }

    // Start
    public Date getDateTimeStart() {
        return dateTimeStart;
    }
    public void setDateTimeStart(Date dateTimeStart) {
        this.dateTimeStart = dateTimeStart;
    }

    // End
    public Date getDateTimeEnd() {
        return dateTimeEnd;
    }
    public void setDateTimeEnd(Date dateTimeEnd) {
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
    public String getTimetableId_fk() {
        return timetableId_fk;
    }
    public void setTimetableId_fk(String timetableId_fk) {
        this.timetableId_fk = timetableId_fk;
    }
}
