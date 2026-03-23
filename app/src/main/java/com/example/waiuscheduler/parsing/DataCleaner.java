package com.example.waiuscheduler.parsing;

import com.example.waiuscheduler.database.tables.AssessmentTable;
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
    private ArrayList<String> assessmentData;

    private String paperId_fk;

    public ScrapedData clean(Document paper) {
        ScrapedData results = new ScrapedData();

        // Process paper data into arraylists
        getInformation(paper);

        // Get the paper table object for the results
        results.setPaper(getPaperInformation());

        // Get the staff table object for the results
        results.setStaff(getStaffInformation());

        results.setAssessment(getAssessmentInformation());

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

        this.assessmentData = retrieveAssessmentData(paper);


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

    // Helping function to retrieve information from staff tables
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

    // Helping function to retrieve information from assessment table
    private ArrayList<String> retrieveAssessmentData(Document paper) {
        ArrayList<String> tableData = new ArrayList<>();
        ArrayList<String> colTexts = new ArrayList<>();
        String query = "table.assessments";
        Element table = paper.select(query).first();
        assert table != null;
        Elements rows = table.select("tr");
        for (Element row : rows) {
            colTexts = checkAssignment(row);
            if (colTexts != null) {
                tableData.addAll(colTexts);
            }
        }
        // TODO: Clean arraylist to match the entity structure
        // (Remove the empty spaces & only need the name, date, percentage )

        return tableData;
    }

    private ArrayList<String> checkAssignment(Element row) {
        ArrayList<String> rowElements = new ArrayList<>();
        ArrayList<String> finalRow = new ArrayList<>();
        Elements cols = row.select("td");
        for (Element col: cols) {
            String colText = col.text();
            if (!colText.isEmpty()) {
                rowElements.add(colText);
            }
        }
        if (rowElements.size() > 3) {
            for (int i = 0; i < 3; i++) {
                finalRow.add(rowElements.get(i));
            }
            return finalRow;
        } else { return null; }

    }


    private PaperTable getPaperInformation() {
        String paperId = itemsData.get(1).split(" ")[0];
        String paperName = itemsData.get(0);
        String paperCode = itemsData.get(1).split("-")[0];
        int points = Integer.parseInt(itemsData.get(2));
        String startWeek = itemsData.get(4);
        String endWeek = itemsData.get(5);
        String semesterCode_fk = "26" + itemsData.get(3);

        this.paperId_fk = paperId;

        return new PaperTable(paperId, paperName, paperCode, points, startWeek, endWeek, semesterCode_fk);
    }

    private ArrayList<StaffTable> getStaffInformation() {
        ArrayList<StaffTable> staffList = new ArrayList<>();
        String position = "";

        for (int i = 0; i < staffData.size(); i++) {
            String item = staffData.get(i);

            if (item.contains("@")) {
                String name = (i > 0) ? staffData.get(i - 1) : "Unknown";
                // Use the paperId we generated above as the foreign key
                staffList.add(new StaffTable(name, item, position, paperId_fk));
            }
            // If it's a role/position header
            else if (i + 1 < staffData.size() && !staffData.get(i + 1).contains("@")) {
                position = item;
            }
        }
        return staffList;
    }

    private ArrayList<AssessmentTable> getAssessmentInformation() {
        ArrayList<AssessmentTable> assessmentList = new ArrayList<>();

        for (int i = 0; i < assessmentData.size(); i+=3) {
            String title = assessmentData.get(i);
            String dueDate = assessmentData.get(i + 1);
            Double weight = Double.valueOf(assessmentData.get(i + 2));
            // TODO: Determine the type of assessment
            // Add the assessment table
            assessmentList.add(new AssessmentTable(title, dueDate, weight, "Assessments", 0.0, paperId_fk));
        }

        return assessmentList;
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
