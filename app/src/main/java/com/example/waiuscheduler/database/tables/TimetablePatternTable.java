package com.example.waiuscheduler.database.tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

/// Entity for timetable pattern table
@Entity(tableName = "timetable_pattern",
        foreignKeys = @ForeignKey(
                entity = PaperTable.class,      // Class of the foreign key
                parentColumns = "paperId",      // Foreign key variable
                childColumns = "paperId_fk",    // Foreign key name
                onDelete = ForeignKey.CASCADE   // Dependency on paper
        ),
        indices = {@Index("paperId_fk")}
)
public class TimetablePatternTable {
    @PrimaryKey @NonNull
    private String type;            // Type of timetable event (eg. lecture)
    private int dayOfWeek;       // Day of week event occurs on
    private Date startTime;       // Start time
    private Date endTime;         // End time
    private String location;        // Location for event
    private Double duration;        // Total duration in hours
    private String paperId_fk;     // Foreign key references event table

    /// Constructor for the timetable table
    public TimetablePatternTable(
            @NonNull String type, int dayOfWeek, Date startTime,
            Date endTime, String location, Double duration, String paperId_fk
    ) {
        this.type = type;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.duration = duration;
        this.paperId_fk = paperId_fk;
    }

    /// Getters and setters of each timetable pattern variable
    // Type
    @NonNull
    public String getType() {
        return type;
    }
    public void setType(@NonNull String type) {
        this.type = type;
    }

    // Day of week
    public int getDayOfWeek() {
        return dayOfWeek;
    }
    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    // Start time
    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    // End time
    public Date getEndTime() {
        return endTime;
    }
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    // Location
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    // Duration
    public Double getDuration() {
        return duration;
    }
    public void setDuration(Double duration) {
        this.duration = duration;
    }

    // Event id
    public String getPaperId_fk() {
        return paperId_fk;
    }
    public void setPaperId_fk(String paperId_fk) {
        this.paperId_fk = paperId_fk;
    }
}
