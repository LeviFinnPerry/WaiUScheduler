package com.example.waiuscheduler.cleaner;

import com.example.waiuscheduler.database.tables.PaperTable;
import com.example.waiuscheduler.database.tables.StaffTable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class DataCleaner {
    private ArrayList<String> itemsData;
    private ArrayList<String> staffData;

    public ScrapedData clean(Document paper) {
        ScrapedData results = new ScrapedData();

        // Process paper data into arraylists
        getInformation(paper);

        // Get the paper table object for the results
        results.setPaper(getPaperInformation());

        // Get the staff table object for the results
        results.setStaff(getStaffInformation());

        return results;
    }

    // Function to retrieve outline information from the paper outline
    private void getInformation(Document paper) {
        // Labels of each element
        List<String> labelNames = new ArrayList<>();
        labelNames.add("Paper Title");
        labelNames.add("Paper Occurrence Code");
        labelNames.add("Points");
        labelNames.add("When taught");
        labelNames.add("Start Week");
        labelNames.add("End Week");

        itemsData = new ArrayList<>();
        staffData = new ArrayList<>();

        // Retrieve main information from paper outline
        for (String name : labelNames) {
            itemsData.add(retrieveItemData(name, paper));
        }

        this.staffData = retrieveStaffTableData(paper);

    }

    // Helping function to retrieve information from the span elements
    private String retrieveItemData(String itemName, Document paper) {
        String query = "label:contains(" + itemName + ")";
        // Find the label
        Element label = paper.select(query).first();
        // Get the information of the element
        if (label != null) {
            Element span = label.nextElementSibling();
            if (span != null) {
                return span.text();
            }
        }
        return null;
    }

    private PaperTable getPaperInformation() {
        String paperName = itemsData.get(0);
        String paperCode = itemsData.get(1).split("-")[0];
        int points = Integer.parseInt(itemsData.get(2));
        String startWeek = itemsData.get(4);
        String endWeek = itemsData.get(5);
        String semesterCode_fk = "26" + itemsData.get(3);

        return new PaperTable(paperName, paperCode, points, startWeek, endWeek, semesterCode_fk);
    }

    private ArrayList<StaffTable> getStaffInformation() {
        ArrayList<StaffTable> staffList = new ArrayList<>();
        String position = "";

        for (int i = 0; i < staffData.size(); i++) {
            String item = staffData.get(i);

            if (item.contains("@")) {
                String name = (i > 0) ? staffData.get(i - 1) : "Unknown";
                // Use the paperId we generated above as the foreign key
                //staffList.add(new StaffTable(name, item, position, results.paperId));
            }
            // If it's a role/position header
            else if (i + 1 < staffData.size() && !staffData.get(i + 1).contains("@")) {
                position = item;
            }
        }
        return staffList;
    }

    // Helping function to retrieve information from tables
    private ArrayList<String> retrieveStaffTableData(Document paper) {
        ArrayList<String> tableData = new ArrayList<>();
        String query = "table.staff";        // Find the staff table
        Element table = paper.select(query).first();
        // Find the rows in the table
        assert table != null;       // Make sure there is a table
        Elements rows = table.select("tr");
        // Get the columns from the rows
        for (Element row: rows) {
            Elements cols = row.select("td");
            // Process each cell in the row
            for (Element col: cols) {
                String cellData = col.text();
                // Check if there is more than one person listed
                if (cellData.matches(".*@.*@.*")) {
                    splitMultipleStaff(cellData, tableData);
                } else if (cellData.contains("-")) {
                    // Check if there contains a "-" as it seperates name and email
                    splitStaffInformation(cellData, tableData);
                } else {
                    tableData.add(cellData);
                }
            }
        }
        return tableData;
    }

    // Handles multiple staff member information
    private void splitMultipleStaff(String cellData, ArrayList<String> tableData) {
        String[] multipleStaff = cellData.split(" ");
        for (int i = 0; i < multipleStaff.length; i+=4) {
            String staffName = multipleStaff[i] + " " + multipleStaff[i+1];
            String staffEmail = multipleStaff[i + 3];

            tableData.add(staffName);
            tableData.add(staffEmail);
        }
    }

    // Splits the name and email of a staff member
    private void splitStaffInformation(String cellData, ArrayList<String> tableData) {
        String[] splitCell = cellData.split("-");
        tableData.add(splitCell[0].trim());
        tableData.add(splitCell[1].trim());
    }

}
