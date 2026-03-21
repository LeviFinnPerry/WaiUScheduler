package com.example.waiuscheduler.database.tables;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "staff",
        foreignKeys = @ForeignKey(
                entity = PaperTable.class,      // Class of foreign key
                parentColumns = "paperId",      // Foreign key variable
                childColumns = "paperId_fk",    // Foreign key name
                onDelete = ForeignKey.CASCADE   // Dependency on paper
        ),
        indices = {@Index("paperId_fk")}
)
public class StaffTable {
    @PrimaryKey(autoGenerate = true)
    private int StaffId;     // Unique Id for the staff member
    private String name;        // Name of staff member
    private String email;       // Email of staff member
    private String position;    // Position of staff member (eg. conveyor, tutor, etc.)
    private long paperId_fk; // Foreign key for paper ID

    // Constructor for staff table
    public StaffTable(String name, String email, String position, long paperId_fk) {
        this.name = name;
        this.email = email;
        this.position = position;
        this.paperId_fk = paperId_fk;
    }

    // Getters and setters for the staff variables
    // Staff ID
    public int getStaffId() {
        return StaffId;
    }

    public void setStaffId(int staffId) {
        StaffId = staffId;
    }

    // Name

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    // Email

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Position

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    // Paper id

    public long getPaperId_fk() {
        return paperId_fk;
    }

    public void setPaperId_fk(long paperId_fk) {
        this.paperId_fk = paperId_fk;
    }
}
