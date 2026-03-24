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

public class DataCleaner {
    private ArrayList<String> itemsData;
    private ArrayList<String> staffData;
    private ArrayList<String> assessmentData;
    private ArrayList<String> timetablePatternData;
    public String semester_fk;
    private ScrapedData results;

    private String paperId_fk;

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

        this.assessmentData = retrieveAssessmentTableData(paper);

        this.timetablePatternData = retrieveTimeTablePatternData(paper);

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
            Date dueDate = convertToDate(assessmentData.get(i + 1));
            Double weight = Double.valueOf(assessmentData.get(i + 2));
            String assessmentType = findAssessmentType(title);

            // Add the assessment table
            assessmentList.add(new AssessmentTable(title, dueDate, weight, assessmentType, 0.0, paperId_fk));
        }

        return assessmentList;
    }

    private String findAssessmentType(String title) {
        ArrayList<String> assessmentTypes = setAssessmentTypes();

        for (String assessmentType: assessmentTypes) {
            if (title.contains(assessmentType)) {
                return assessmentType;
            }
        }
        return "Assessment";
    }

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

    private ArrayList<TimetablePatternTable> getTimetablePatternInformation() {
        ArrayList<TimetablePatternTable> timetablePatternList = new ArrayList<>();

        for (int i = 0; i < timetablePatternData.size(); i+=5) {
            String type = timetablePatternData.get(i);
            int dayOfWeek = convertDOW(timetablePatternData.get(i + 1));
            String startTimeString = timetablePatternData.get(i + 2);
            String endTimeString = timetablePatternData.get(i + 3);
            String location = timetablePatternData.get(i + 4);
            Double duration = getDuration(startTimeString, endTimeString);

            Date startTime = convertToTime(startTimeString);
            Date endTime = convertToTime(endTimeString);

            // Add to the timetable pattern table
            timetablePatternList.add(new TimetablePatternTable(type, dayOfWeek, startTime, endTime, location, duration, paperId_fk));
        }
        return timetablePatternList;
    }

    // Convert the day of week into a number
    private int convertDOW(String dayOfWeek) {
        ArrayList<String> days = setDays();
        for (String day: days) {
            if (day.matches(dayOfWeek)) {
                return days.indexOf(day) + 1;
            }
        }
        return 0;
    }

    private ArrayList<String> setDays() {
        ArrayList<String> days = new ArrayList<>();
        days.add("Mon");
        days.add("Tue");
        days.add("Wed");
        days.add("Thu");
        days.add("Fri");
        return days;
    }

    // To get the duration
    private Double getDuration(String startTime, String endTime) {
        Double start = Double.valueOf(startTime.split(":")[0]);
        Double end = Double.valueOf(endTime.split(":")[0]);

        return end - start;
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

    private Date convertToDate(String dateString) {
        return DateConverter.stringToDate(dateString);
    }

    private Date convertToTime(String timeString) {
        return DateConverter.stringToTime(timeString);
    }

    private Date convertToDateTime(Date date, Date time) {
        return DateConverter.ToDateTime(date, time);
    }
}
