package com.example.waiuscheduler.database.tables;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "study_session",
        foreignKeys = @ForeignKey(
                entity = PaperTable.class,  // Class of foreign key
                parentColumns = "paperId",  // Foreign key variable
                childColumns = "paperId_fk",    // Foreign key name
                onDelete = ForeignKey.CASCADE   // Dependency on paper
        ),
        indices = {@Index("paperId_fk")}
)
public class StudySessionTable {
    @PrimaryKey(autoGenerate = true)        // Unique Id for the study session
    private int sessionId;
    private String dateTimeStart;           // Start time of study session
    private String dateTimeEnd;             // End time of study session
    private Double duration;                // Duration of study session
    private String notes;                   // Notes from session
    private String paperId_fk;          // Foreign key referencing paper

    // Constructor for the study session table
    public StudySessionTable(String dateTimeStart, String dateTimeEnd, Double duration,
                             String notes, String paperId_fk) {
        this.dateTimeStart = dateTimeStart;
        this.dateTimeEnd = dateTimeEnd;
        this.duration = duration;
        this.notes = notes;
        this.paperId_fk = paperId_fk;
    }

    // Getter and setters
    // Id

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
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
    // Duration

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    // Notes

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Paper Id

    public String getPaperId_fk() {
        return paperId_fk;
    }

    public void setPaperId_fk(String paperId_fk) {
        this.paperId_fk = paperId_fk;
    }
}
