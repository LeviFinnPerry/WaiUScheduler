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
                entity = PaperEntity.class,      // Class of the foreign key
                parentColumns = "paperId",      // Foreign key variable
                childColumns = "paperId",    // Foreign key name
                onDelete = ForeignKey.CASCADE   // Dependency on paper
        ),
        indices = {@Index("paperId")}
)
public class TimetablePatternEntity {
    @PrimaryKey @NonNull
    private String type;            // Type of timetable event (eg. lecture)
    private final int dayOfWeek;       // Day of week event occurs on
    private final Date startTime;       // Start time
    private final Date endTime;         // End time
    private final String location;        // Location for event
    private final Double duration;        // Total duration in hours
    private String paperId;     // Foreign key references event table

    /// Constructor for the timetable table
    /// @param type Type of scheduled timetable occurrence
    /// @param dayOfWeek Number alternative of the day of the week
    /// @param startTime Time the timetable occurrence started
    /// @param endTime Time the timetable occurrence ended
    /// @param location Building the timetable occurrence is in
    /// @param duration Duration of the timetable occurrence
    /// @param paperId Foreign key to the paper id the timetable is from
    public TimetablePatternEntity(
            @NonNull String type, int dayOfWeek, Date startTime,
            Date endTime, String location, Double duration, String paperId
    ) {
        this.type = type;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.duration = duration;
        this.paperId = paperId;
    }

    /// Get the type of timetable pattern occurrence
    /// @return The type of timetable occurrence
    @NonNull
    public String getType() {
        return type;
    }

    /// Set the type of timetable pattern occurrence
    /// @param type The type of timetable occurrence
    public void setType(@NonNull String type) {
        this.type = type;
    }

    /// Get the day of the week for the timetable pattern
    /// @return Numeric representation for the day of the week
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    /// Get the occurrence start time
    /// @return Time the timetable pattern starts
    public Date getStartTime() {
        return startTime;
    }

    /// Get the occurrence end time
    /// @return Time the timetable pattern ends
    public Date getEndTime() {
        return endTime;
    }

    /// Get the location of the timetable pattern
    /// @return Building the occurrence is in
    public String getLocation() {
        return location;
    }

    /// Get the duration of the timetable pattern
    /// @return Length of duration in hours
    public Double getDuration() {
        return duration;
    }

    /// Get the paper id that corresponds to the timetable occurrence
    /// @return Foreign key paper id the timetable pattern is from
    public String getPaperId() {
        return paperId;
    }

    /// Set the paper id that corresponds to the timetable occurrence
    /// @param paperId Foreign key paper id the timetable pattern is from
    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }
}
