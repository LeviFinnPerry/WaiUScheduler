package com.example.waiuscheduler.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

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
    @PrimaryKey(autoGenerate = true)
    private int timetableId;     // Unique Id for the timetable occurance
    private String type;            // Type of timetable event (eg. lecture)
    private String dayOfWeek;       // Day of week event occurs on
    private String startTime;       // Start time
    private String endTime;         // End time
    private String location;        // Location for event
    private Double duration;        // Total duration in hours
    private String paperId_fk;     // Foreign key references event table

    // Constructor for the timetable table
    public TimetablePatternTable(String type, String dayOfWeek, String startTime,
                                 String endTime, String location, Double duration, String paperId_fk
    ) {
        this.type = type;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.duration = duration;
        this.paperId_fk = paperId_fk;
    }

    // Getters and setters of each timetable pattern variable
    // Timetable id
    public int getTimetableId() {
        return timetableId;
    }

    public void setTimetableId(int timetableId) {
        this.timetableId = timetableId;
    }

    // Type

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    // Day of week

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    // Start time

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    // End time

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
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
