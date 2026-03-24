package com.example.waiuscheduler.parsing;

import com.example.waiuscheduler.database.DateConverter;
import com.example.waiuscheduler.database.tables.AssessmentTable;
import com.example.waiuscheduler.database.tables.EventTable;
import com.example.waiuscheduler.database.tables.PaperTable;
import com.example.waiuscheduler.database.tables.SemesterTable;
import com.example.waiuscheduler.database.tables.StaffTable;
import com.example.waiuscheduler.database.tables.TimetablePatternTable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/// Cleans HTML information from the paper outline
public class DataCleaner {
    private ArrayList<String> itemsData;
    private ArrayList<String> staffData;
    private ArrayList<String> assessmentData;
    private ArrayList<String> timetablePatternData;
    public String semester_fk;
    private ScrapedData results;

    private String paperId_fk;

    /// Main method to clean HTML document.
    /// Returns ScrapedData object
    public ScrapedData clean(Document paper) {
        results = new ScrapedData();

        // Process paper data into arraylists
        getInformation(paper);

        // Get the paper table object for the results
        results.setPaper(getPaperInformation());

        // Get the staff table object for the results
        results.setStaffs(getStaffInformation());

        // Get the assessment table object for the results
        results.setAssessments(getAssessmentInformation());

        // Get the timetable pattern table object for the results
        results.setTimetablePatterns(getTimetablePatternInformation());

        return results;
    }

    /// Function to retrieve outline information from the paper outline
    /// for the paper header and each of the tables in the html
    private void getInformation(Document paper) {
        ArrayList<String> labelNames = getLabelNames();

        itemsData = new ArrayList<>();
        staffData = new ArrayList<>();

        // Retrieve main information from paper outline
        for (String name : labelNames) {
            itemsData.add(retrieveItemData(name, paper));
        }

        // Retrieves information about the staff
        this.staffData = retrieveStaffTableData(paper);

        // Retrieves information about the assessments
        this.assessmentData = retrieveAssessmentTableData(paper);

        // Retrieves information about the timetable pattern
        this.timetablePatternData = retrieveTimeTablePatternData(paper);

    }

    /// Function to generate the label names for the core paper information.
    /// Returns a string arraylist of names.
    private ArrayList<String> getLabelNames() {
        // Labels of each element
        ArrayList<String> labelNames = new ArrayList<>();
        labelNames.add("Paper Title");
        labelNames.add("Paper Occurrence Code");
        labelNames.add("Points");
        labelNames.add("When taught");
        labelNames.add("Start Week");
        labelNames.add("End Week");

        return labelNames;
    }

    /// Helping function to retrieve information from the span elements
    private String retrieveItemData(String itemName, Document paper) {
        String query = "label:contains(" + itemName + ")";
        Element label = paper.select(query).first();    // Find the label
        if (label != null) {
            Element span = label.nextElementSibling();  // Find the span
            if (span != null) {
                return span.text();                     // Return the item
            }
        }
        return null;    // Else return null
    }

    /// Helping function to retrieve information from staff tables
    /// Returns an arraylist of staff information
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

    /// Helping function to retrieve information from assessment table
    /// Returns an arraylist of assessment information
    private ArrayList<String> retrieveAssessmentTableData(Document paper) {
        ArrayList<String> tableData = new ArrayList<>();
        ArrayList<String> colTexts;
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
        return tableData;
    }

    /// Helping function to retrieve information from timetable pattern
    /// Returns an arraylist of timetable pattern information.
    private ArrayList<String> retrieveTimeTablePatternData(Document paper) {
        ArrayList<String> tableData = new ArrayList<>();
        String query = "table.timetable";
        Element table = paper.select(query).first();
        assert table != null;
        Elements rows = table.select("tr");
        for (Element row: rows) {
            Elements cols = row.select("td");
            for (Element col: cols) {
                tableData.add(col.text());
            }
        }
        return tableData;
    }

    /// Function that creates event data for the event table
    /// Returns an arraylist of events created based on the semester and timetable table
    public ArrayList<EventTable> createEventData(String semesterCode) {
        SemesterTable semester = results.getSemester(semesterCode);
        ArrayList<TimetablePatternTable> timetablePattern = results.getTimetablePatterns();
        ArrayList<EventTable> eventTables = new ArrayList<>();

        Date startDate = semester.getStartDate();
        Date endDate = semester.getEndDate();
        Date breakStartDate = semester.getBreakStartDate();
        Date breakEndDate = semester.getBreakEndDate();

        for (TimetablePatternTable timetable: timetablePattern) {
            int timeTableId = timetable.getTimetableId();
            String type = timetable.getType();
            int dow = timetable.getDayOfWeek();
            Date startTime = timetable.getStartTime();
            Date endTime = timetable.getEndTime();

            // Method to create events
            eventTables.addAll(createEvents(startDate,  endDate, breakStartDate, breakEndDate,
                    type, dow, startTime, endTime, timeTableId));
        }
        return eventTables;
    }

    /// Function that creates events based on a timetable pattern occurrence
    /// Returns an arraylist of EventTable Objects
    private ArrayList<EventTable> createEvents(Date semStart, Date semEnd, Date breakSemStart, Date breakSemEnd,
                              String type, int dow, Date startTime, Date endTime, int event_fk) {
        ArrayList<EventTable> events = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(semStart);

        while (cal.getTime().before(semEnd)  || cal.getTime().equals(semEnd)) {
            Date current = cal.getTime();

            // Check if it is the right day of the week
            if (cal.get(Calendar.DAY_OF_WEEK) == dow) {
                // Check if it not during the break
                if (current.before(breakSemStart) || current.after(breakSemEnd)) {
                    Date startDateTime = convertToDateTime(current, startTime);
                    Date endDateTime = convertToDateTime(current, endTime);

                    events.add(new EventTable(type, startDateTime, endDateTime, false, event_fk));
                }
            }
        }
        return events;

    }

    /// Helping function to determine whether a row in the assignment table
    /// is an actual assignment.
    /// Returns an arraylist of assignment information for the row
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

    /// Function to retrieve the information from the paper arraylist
    /// Returns a new Paper Table object
    private PaperTable getPaperInformation() {
        String paperId = itemsData.get(1);
        String paperName = itemsData.get(0);
        String paperCode = itemsData.get(1).split("-")[0];
        int points = Integer.parseInt(itemsData.get(2));
        Date startWeek = convertToDate(itemsData.get(4));
        Date endWeek = convertToDate(itemsData.get(5));
        String semesterCode_fk = "26" + itemsData.get(3);

        this.paperId_fk = paperId;
        this.semester_fk = semesterCode_fk;

        return new PaperTable(paperId, paperName, paperCode, points, startWeek, endWeek, semesterCode_fk);
    }

    /// Function to retrieve the information from the staff arraylist
    /// Returns a new Staff Table object
    private ArrayList<StaffTable> getStaffInformation() {
        ArrayList<StaffTable> staffList = new ArrayList<>();
        String position = "";

        for (int i = 0; i < staffData.size(); i++) {
            String item = staffData.get(i);
            // If the item contains an email
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

    /// Function to retrieve the information from the assessment arraylist
    /// Returns a new Assessment Table object
    private ArrayList<AssessmentTable> getAssessmentInformation() {
        ArrayList<AssessmentTable> assessmentList = new ArrayList<>();

        for (int i = 0; i < assessmentData.size(); i+=3) {
            String title = assessmentData.get(i);
            Date dueDate = convertToDate(assessmentData.get(i + 1));
            Double weight = Double.valueOf(assessmentData.get(i + 2));
            String assessmentType = findAssessmentType(title);

            // Add the assessment table
            assessmentList.add(new AssessmentTable(title, dueDate, weight, assessmentType, 0.0, paperId_fk));
        }

        return assessmentList;
    }


    /// Helping function to determine the type of assessment based on the title
    /// Returns the type of assessment as a string
    private String findAssessmentType(String title) {
        ArrayList<String> assessmentTypes = setAssessmentTypes();
        // Compare the title to the assessment types
        for (String assessmentType: assessmentTypes) {
            if (title.contains(assessmentType)) {
                return assessmentType;
            }
        }
        return "Assessment";    // Default assessment type
    }

    /// Sets arraylist of assessment types to check
    /// Returns an arraylist of all the assessment types
    private ArrayList<String> setAssessmentTypes() {
        ArrayList<String> assessmentTypes = new ArrayList<>();
        assessmentTypes.add("Assessment");
        assessmentTypes.add("Quiz");
        assessmentTypes.add("Test");
        assessmentTypes.add("Report");
        assessmentTypes.add("Presentation");
        assessmentTypes.add("Exam");

        return assessmentTypes;
    }

    /// Function to retrieve the timetable pattern information based on the timetable
    /// arraylist.
    /// Returns an arraylist of timetable patterns
    private ArrayList<TimetablePatternTable> getTimetablePatternInformation() {
        ArrayList<TimetablePatternTable> timetablePatternList = new ArrayList<>();
        // Iterate through arraylist in groups of 5
        for (int i = 0; i < timetablePatternData.size(); i+=5) {
            String type = timetablePatternData.get(i);
            int dayOfWeek = convertDOW(timetablePatternData.get(i + 1));
            String startTimeString = timetablePatternData.get(i + 2);
            String endTimeString = timetablePatternData.get(i + 3);
            String location = timetablePatternData.get(i + 4);
            Double duration = getDuration(startTimeString, endTimeString);

            // Convert the times to date objects
            Date startTime = convertToTime(startTimeString);
            Date endTime = convertToTime(endTimeString);

            // Add to the timetable pattern table
            timetablePatternList.add(new TimetablePatternTable(type, dayOfWeek, startTime, endTime, location, duration, paperId_fk));
        }
        return timetablePatternList;
    }

    /// Convert the day of week into int representation
    private int convertDOW(String dayOfWeek) {
        ArrayList<String> days = setDays();
        for (String day: days) {
            if (day.matches(dayOfWeek)) {
                return days.indexOf(day) + 1;
            }
        }
        return 0;
    }

    /// Helping function to add days of week
    /// Returns arraylist of the days of the week
    private ArrayList<String> setDays() {
        ArrayList<String> days = new ArrayList<>();
        days.add("Mon");
        days.add("Tue");
        days.add("Wed");
        days.add("Thu");
        days.add("Fri");
        return days;
    }

    /// Calculates duration based on the start and end time
    /// Returns the duration as a double
    private Double getDuration(String startTime, String endTime) {
        Double start = Double.valueOf(startTime.split(":")[0]);
        Double end = Double.valueOf(endTime.split(":")[0]);

        return end - start;
    }

    /// Handles multiple staff member information by separating name and email for each staff member
    private void splitMultipleStaff(String cellData, ArrayList<String> tableData) {
        String[] multipleStaff = cellData.split(" ");
        for (int i = 0; i < multipleStaff.length; i+=4) {
            String staffName = multipleStaff[i] + " " + multipleStaff[i+1];
            String staffEmail = multipleStaff[i + 3];

            // Add to arraylist
            tableData.add(staffName);
            tableData.add(staffEmail);
        }
    }

    /// Splits the name and email of a staff member from a single string
    private void splitStaffInformation(String cellData, ArrayList<String> tableData) {
        String[] splitCell = cellData.split("-");
        // Add to arraylist
        tableData.add(splitCell[0].trim());
        tableData.add(splitCell[1].trim());
    }

    /// Converts a dateString into a date Date object
    private Date convertToDate(String dateString) {
        return DateConverter.stringToDate(dateString);
    }

    /// Converts a timeString into a time Date object
    private Date convertToTime(String timeString) {
        return DateConverter.stringToTime(timeString);
    }

    /// Combines date and time into a datetime Date object
    private Date convertToDateTime(Date date, Date time) {
        return DateConverter.ToDateTime(date, time);
    }
}
