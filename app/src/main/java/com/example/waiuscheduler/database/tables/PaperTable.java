package com.example.waiuscheduler.database.tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

/// Entity for paper table
@Entity(tableName = "paper",
        foreignKeys = @ForeignKey(
                entity = SemesterTable.class,   // Class of foreign key
                parentColumns = "semesterCode",   // Foreign key variable
                childColumns = "semesterCode_fk",    // Foreign key name
                onDelete = ForeignKey.CASCADE   // Dependency on semester
        ),
        indices = {@Index("semesterCode_fk")}        // Index of the foreign key
)
public class PaperTable {
    @PrimaryKey @NonNull                       // Unique Id for the paper
    private String paperId;
    private String paperCode;           // Paper code eg. COMPX576
    private String paperName;           // Paper name eg. Programming Project
    private int points;                 // Paper points
    private Date startWeek;           // Start week for the paper
    private Date endWeek;             // End week for the paper
    private String semesterCode_fk;    // Foreign key referencing semester

    /// Constructor for the paper table
    public PaperTable(@NonNull String paperId, String paperCode, String paperName,
                      int points, Date startWeek, Date endWeek, String semesterCode_fk) {
        this.paperId = paperId;
        this.paperCode = paperCode;
        this.paperName = paperName;
        this.points = points;
        this.startWeek = startWeek;
        this.endWeek = endWeek;
        this.semesterCode_fk = semesterCode_fk;
    }

    /// Getters and setters for each paper variable
    // Id
    @NonNull
    public String getPaperId() {
        return paperId;
    }
    public void setPaperId(@NonNull String paperId) {
        this.paperId = paperId;
    }

    // Paper code
    public String getPaperCode() {
        return paperCode;
    }
    public void setPaperCode(String paperCode) {
        this.paperCode = paperCode;
    }

    // Paper name
    public String getPaperName() {
        return paperName;
    }
    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    // Points
    public int getPoints() {
        return points;
    }
    public void setPoints(int points) {
        this.points = points;
    }

    // Start Week
    public Date getStartWeek() {
        return startWeek;
    }
    public void setStartWeek(Date startWeek) {
        this.startWeek = startWeek;
    }

    // End Week
    public Date getEndWeek() {
        return endWeek;
    }
    public void setEndWeek(Date endWeek) {
        this.endWeek = endWeek;
    }

    // Semester code
    public String getSemesterCode_fk() {
        return semesterCode_fk;
    }
    public void setSemesterCode_fk(String semesterCode_fk) {
        this.semesterCode_fk = semesterCode_fk;
    }
}
