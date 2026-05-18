package com.example.waiuscheduler.database.tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/// Entity for paper table
@Entity(tableName = "paper",
        foreignKeys = @ForeignKey(
                entity = SemesterEntity.class,   // Class of foreign key
                parentColumns = "semesterCode",   // Foreign key variable
                childColumns = "semesterCode_fk",    // Foreign key name
                onDelete = ForeignKey.CASCADE   // Dependency on semester
        ),
        indices = {@Index("semesterCode_fk")}        // Index of the foreign key
)
public class PaperEntity {
    @PrimaryKey @NonNull                 // Unique Id for the paper
    private String paperId;
    private final String paperCode;           // Paper code eg. COMPX576
    private final String paperName;           // Paper name eg. Programming Project
    private final int points;                 // Paper points
    private String semesterCode_fk;    // Foreign key referencing semester

    /// Constructor for the paper table
    /// @param paperId Id of the paper
    /// @param paperCode Code of the paper
    /// @param paperName Name of the paper
    /// @param points Amount of points the paper is worth
    /// @param semesterCode_fk The semester occurrence of the paper
    public PaperEntity(@NonNull String paperId, String paperCode, String paperName,
                       int points, String semesterCode_fk) {
        this.paperId = paperId;
        this.paperCode = paperCode;
        this.paperName = paperName;
        this.points = points;
        this.semesterCode_fk = semesterCode_fk;
    }

    /// Get paper Id
    /// @return The paper id
    @NonNull
    public String getPaperId() {
        return paperId;
    }

    /// Set paper Id
    /// @param paperId The id for the paper
    public void setPaperId(@NonNull String paperId) {
        this.paperId = paperId;
    }

    /// Get the paper code
    /// @return The paper code
    public String getPaperCode() {
        return paperCode;
    }

    /// Get the paper name
    /// @return The paper name
    public String getPaperName() {
        return paperName;
    }

    /// Get the paper points
    /// @return The amount of points the paper is worth
    public int getPoints() {
        return points;
    }

    /// Get the semester code foreign key
    /// @return Foreign key of the semester occurrence code
    public String getSemesterCode_fk() {
        return semesterCode_fk;
    }

    /// Set the semester code foreign key
    /// @param semesterCode_fk The semester occurrence code
    public void setSemesterCode_fk(String semesterCode_fk) {
        this.semesterCode_fk = semesterCode_fk;
    }
}
