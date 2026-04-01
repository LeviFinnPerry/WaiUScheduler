package com.example.waiuscheduler.database.tables;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/// Entity for staff table
@Entity(tableName = "staff",
        foreignKeys = @ForeignKey(
                entity = PaperEntity.class,      // Class of foreign key
                parentColumns = "paperId",      // Foreign key variable
                childColumns = "paperId_fk",    // Foreign key name
                onDelete = ForeignKey.CASCADE   // Dependency on paper
        ),
        indices = {@Index("paperId_fk")}
)
public class StaffEntity {
    @PrimaryKey(autoGenerate = true)
    private int staffId;     // Unique Id for the staff member
    private String name;        // Name of staff member
    private String email;       // Email of staff member
    private String position;    // Position of staff member (eg. conveyor, tutor, etc.)
    private String paperId_fk; // Foreign key for paper ID

    /// Constructor for staff table
    /// @param name Full name of the staff member
    /// @param email Email address of the staff member
    /// @param position Role of the staff member for the paper
    /// @param paperId_fk Paper the staff member information is from
    public StaffEntity(String name, String email, String position, String paperId_fk) {
        this.name = name;
        this.email = email;
        this.position = position;
        this.paperId_fk = paperId_fk;
    }

    /// Get staff Id
    /// @return Auto generated id for staff member
    public int getStaffId() {
        return staffId;
    }

    /// Set staff Id
    /// @param staffId Auto generated id for staff member
    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    /// Get staff name
    /// @return Full name of the staff member
    public String getName() {
        return name;
    }

    /// Set staff name
    /// @param name Full name of the staff member
    public void setName(String name) {
        this.name = name;
    }

    /// Get staff email
    /// @return Email of the staff member
    public String getEmail() {
        return email;
    }

    /// Set staff email
    /// @param email Email of the staff member
    public void setEmail(String email) {
        this.email = email;
    }

    /// Get staff position
    /// @return Role the staff member has for the paper
    public String getPosition() {
        return position;
    }

    /// Set staff position
    /// @param position Role of the staff member has for the paper
    public void setPosition(String position) {
        this.position = position;
    }

    /// Get foreign key of paper id
    /// @return Id of the paper the staff is in
    public String getPaperId_fk() {
        return paperId_fk;
    }

    /// Set foreign key of paper id
    /// @param paperId_fk Id of the paper the staff is in
    public void setPaperId_fk(String paperId_fk) {
        this.paperId_fk = paperId_fk;
    }
}
