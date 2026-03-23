package com.example.waiuscheduler.database.tables;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "assessment",
        foreignKeys = @ForeignKey(
                entity = PaperTable.class,      // Class of foreign key
                parentColumns = "paperId",      // Foreign key variable
                childColumns = "paperId_fk",    // Foreign key name
                onDelete = ForeignKey.CASCADE   // Dependency on paper
        ),
        indices = {@Index("paperId_fk")}        // Index of the foreign key
)
public class AssessmentTable {
    @PrimaryKey(autoGenerate = true)
    private int assessmentId;    // Unique id for assessments
    private String title;           // Title for the assessment
    private Date dueDate;         // Due date of the assignment
    private Double weight;          // Weight of marks for the assignment
    private String type;            // Type of assessment (eg. test, assignment, exam)
    private Double grade;           // Grade given on assignment
    private String paperId_fk;     // Foreign key to reference the paper table

    // Constructor for the paper table
    public AssessmentTable(String title, Date dueDate, Double weight,
                           String type, Double grade, String paperId_fk
    ) {
        this.title = title;
        this.dueDate = dueDate;
        this.weight = weight;
        this.type = type;
        this.grade = grade;
        this.paperId_fk = paperId_fk;
    }

    // Getters and setters for each assignment variable
    // Id


    public void setAssessmentId(int assessmentId) {
        this.assessmentId = assessmentId;
    }

    public int getAssessmentId() {
        return assessmentId;
    }

    // Title

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Due Date

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    // Weight

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    // Type

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Grade

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    // Paper id


    public String getPaperId_fk() {
        return paperId_fk;
    }

    public void setPaperId_fk(String paperId_fk) {
        this.paperId_fk = paperId_fk;
    }
}
