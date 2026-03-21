package com.example.waiuscheduler.parsing;

import com.example.waiuscheduler.database.tables.PaperTable;
import com.example.waiuscheduler.database.tables.StaffTable;

import java.util.ArrayList;

public class ScrapedData {
    private PaperTable paper;
    private ArrayList<StaffTable> staff;

    // Constructor
    public ScrapedData() {
        this.staff = new ArrayList<>();
    }

    public PaperTable getPaper() {
        return paper;
    }

    public void setPaper(PaperTable paper) {
        this.paper = paper;
    }

    public ArrayList<StaffTable> getStaff() {
        return staff;
    }

    public void setStaff(ArrayList<StaffTable> staff) {
        this.staff = staff;
    }
}
