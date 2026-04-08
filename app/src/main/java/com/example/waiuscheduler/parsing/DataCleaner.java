package com.example.waiuscheduler.parsing;

import com.example.waiuscheduler.database.DateConverter;
import com.example.waiuscheduler.database.tables.AssessmentEntity;
import com.example.waiuscheduler.database.tables.EventEntity;
import com.example.waiuscheduler.database.tables.PaperEntity;
import com.example.waiuscheduler.database.tables.SemesterEntity;
import com.example.waiuscheduler.database.tables.StaffEntity;
import com.example.waiuscheduler.database.tables.TimetablePatternEntity;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    /// @param paper Paper outline document
    /// @return ScrapedData object that holds all paper details
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
    /// @param paper Paper outline document
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
    /// @return arraylist of html labels in the paper outline
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
    /// @param itemName HTML label from the outline
    /// @return Text from the element if exists
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
    /// @param paper Paper outline document
    /// @return Staff table as an arraylist
    private ArrayList<String> retrieveStaffTableData(Document paper) {
        ArrayList<String> tableData = new ArrayList<>();
        String query = "table.staff";        // Find the staff table
        Element table = paper.select(query).first();
        // Find the rows in the table
        if (table != null) {       // Make sure there is a table
            Elements rows = table.select("tr");
            // Get the columns from the rows
            for (Element row : rows) {
                Elements cols = row.select("td");
                // Process each cell in the row
                for (Element col : cols) {
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
        }
        return tableData;
    }

    /// Helping function to retrieve information from assessment table
    /// @param paper Paper outline document
    /// @return Arraylist of assessment information
    private ArrayList<String> retrieveAssessmentTableData(Document paper) {
        ArrayList<String> tableData = new ArrayList<>();
        ArrayList<String> colTexts;
        String query = "table.assessments";
        Element table = paper.select(query).first();
        if (table != null) {
            Elements rows = table.select("tr");
            for (Element row : rows) {
                colTexts = checkAssignment(row);
                if (colTexts != null) {
                    tableData.addAll(colTexts);
                }
            }
        }
        return tableData;
    }

    /// Helping function to retrieve information from timetable pattern
    /// @param paper Paper outline document
    /// @return Arraylist of timetable pattern information.
    private ArrayList<String> retrieveTimeTablePatternData(Document paper) {
        ArrayList<String> tableData = new ArrayList<>();
        String query = "table.timetable";
        Element table = paper.select(query).first();
        if (table != null) {
            Elements rows = table.select("tr");
            for (Element row : rows) {
                Elements cols = row.select("td");
                for (Element col : cols) {
                    tableData.add(col.text());
                }
            }
        }
        return tableData;
    }

    /// Function that creates event data for the event table
    /// @param semester Semester the events occur within
    /// @return Arraylist of events created based on the semester and timetable table
    public ArrayList<EventEntity> createEventData(SemesterEntity semester) {
        ArrayList<TimetablePatternEntity> timetablePattern = results.getTimetablePatterns();
        ArrayList<EventEntity> eventEntities = new ArrayList<>();

        Date startDate = semester.getStartDate();
        Date endDate = semester.getEndDate();
        Date breakStartDate = semester.getBreakStartDate();
        Date breakEndDate = semester.getBreakEndDate();

        for (TimetablePatternEntity timetable: timetablePattern) {
            String type = timetable.getType();
            int dow = timetable.getDayOfWeek();
            Date startTime = timetable.getStartTime();
            Date endTime = timetable.getEndTime();

            // Method to create events
            eventEntities.addAll(createEvents(startDate,  endDate, breakStartDate, breakEndDate,
                    type, dow, startTime, endTime));
        }
        return eventEntities;
    }

    /// Function that creates events based on a timetable pattern occurrence
    /// @param semStart Start date of the semester
    /// @param semEnd End date of the semester
    /// @param breakSemStart Start date of the semester break
    /// @param breakSemEnd End date of the semester break
    /// @param type Type of timetable pattern event
    /// @param dow Numeric day of the week the event occurs
    /// @param startTime Start time of the event
    /// @param endTime End time of the event
    /// @return Arraylist of EventEntity Objects
    private ArrayList<EventEntity> createEvents(Date semStart, Date semEnd, Date breakSemStart, Date breakSemEnd,
                                                String type, int dow, Date startTime, Date endTime) {
        ArrayList<EventEntity> events = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(semStart);

        while (cal.getTime().before(semEnd)  || cal.getTime().equals(semEnd)) {
            Date current = cal.getTime();

            // Check if it is the right day of the week
            int calDay = cal.get(Calendar.DAY_OF_WEEK);
            if (calDay == dow) {
                // Check if it not during the break
                if (current.before(breakSemStart) || current.after(breakSemEnd)) {
                    Date startDateTime = convertToDateTime(current, startTime);
                    Date endDateTime = convertToDateTime(current, endTime);

                    events.add(new EventEntity(startDateTime, endDateTime, false, type));
                }
            }
            // Move to the next day
            cal.add(Calendar.DATE, 1);
        }
        return events;

    }

    /// Helping function to determine whether a row in the assignment table
    /// is an actual assignment.
    /// @param row HTML element of the assignment
    /// @return Arraylist of assignment information
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
    /// @return new paper entity
    private PaperEntity getPaperInformation() {
        String paperId = itemsData.get(1);
        String paperName = itemsData.get(0);
        String paperCode = itemsData.get(1).split("-")[0];
        int points = Integer.parseInt(itemsData.get(2));
        String semesterCode_fk = "26" + itemsData.get(3);

        this.paperId_fk = paperId;
        this.semester_fk = semesterCode_fk;

        return new PaperEntity(paperId, paperName, paperCode, points, semesterCode_fk);
    }

    /// Function to retrieve the information from the staff arraylist
    /// @return new Staff entity
    private ArrayList<StaffEntity> getStaffInformation() {
        ArrayList<StaffEntity> staffList = new ArrayList<>();
        String position = "";

        for (int i = 0; i < staffData.size(); i++) {
            String item = staffData.get(i);
            // If the item contains an email
            if (item.contains("@")) {
                String name = (i > 0) ? staffData.get(i - 1) : "Unknown";
                // Use the paperId we generated above as the foreign key
                staffList.add(new StaffEntity(name, item, position, paperId_fk));
            }
            // If it's a role/position header
            else if (i + 1 < staffData.size() && !staffData.get(i + 1).contains("@")) {
                position = item;
            }
        }
        return staffList;
    }

    /// Function to retrieve the information from the assessment arraylist
    /// @return new assessment entity
    private ArrayList<AssessmentEntity> getAssessmentInformation() {
        ArrayList<AssessmentEntity> assessmentList = new ArrayList<>();

        for (int i = 0; i < assessmentData.size(); i+=3) {
            String title = assessmentData.get(i);
            Date dueDate = convertToDate(assessmentData.get(i + 1));
            Double weight = Double.valueOf(assessmentData.get(i + 2));
            String assessmentType = findAssessmentType(title);

            // Add the assessment table
            assessmentList.add(new AssessmentEntity(title, dueDate, weight, assessmentType, 0.0, paperId_fk));
        }

        return assessmentList;
    }


    /// Helping function to determine the type of assessment based on the title
    /// @param title name of the assessment
    /// @return type of assessment
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
    /// @return Arraylist of all the assessment types
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
    /// @return Arraylist of timetable patterns
    private ArrayList<TimetablePatternEntity> getTimetablePatternInformation() {
        ArrayList<TimetablePatternEntity> timetablePatternList = new ArrayList<>();
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
            timetablePatternList.add(new TimetablePatternEntity(type, dayOfWeek, startTime, endTime, location, duration, paperId_fk));
        }
        return timetablePatternList;
    }

    /// Convert the day of week into int representation
    /// @param dayOfWeek Three letter representation of weekday
    /// @return Numeric representation for day of the week
    private int convertDOW(String dayOfWeek) {
        ArrayList<String> days = setDays();
        for (String day: days) {
            if (day.matches(dayOfWeek)) {
                return days.indexOf(day) + 2;
            }
        }
        return 0;
    }

    /// Helping function to add days of week
    /// @return arraylist of the three letter days of the week
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
    /// @param startTime Text start time
    /// @param endTime Text end time
    /// @return Duration as a double
    private Double getDuration(String startTime, String endTime) {
        Double start = Double.valueOf(startTime.split(":")[0]);
        Double end = Double.valueOf(endTime.split(":")[0]);

        return end - start;
    }

    /// Handles multiple staff member information by separating name and email for each staff member
    /// @param cellData Text from a cell in the staff table
    /// @param tableData Arraylist of all current staff members
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
    /// @param cellData Text from a cell in the staff table
    /// @param tableData Arraylist of all current staff members
    private void splitStaffInformation(String cellData, ArrayList<String> tableData) {
        String[] splitCell = cellData.split("-");
        // Add to arraylist
        tableData.add(splitCell[0].trim());
        tableData.add(splitCell[1].trim());
    }

    /// Converts a dateString into a date
    /// @param dateString Text format of a date with a text month
    /// @return Date object
    private Date convertToDate(String dateString) {
        return DateConverter.stringAbvToDate(dateString);
    }

    /// Converts a timeString into a time
    /// @param timeString Text format of a time
    /// @return Date object for time
    private Date convertToTime(String timeString) {
        return DateConverter.stringToTime(timeString);
    }

    /// Combines date and time into a datetime
    /// @param date Date object
    /// @param time Date object for time
    /// @return Combined date and time
    private Date convertToDateTime(Date date, Date time) {
        return DateConverter.toDateTime(date, time);
    }
}
