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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/// Cleans HTML information from the paper outline.
/// Parsing (raw text extraction) and entity construction are kept separate:
/// extract* methods return plain strings/arrays; build* methods turn those into entities.
public class DataCleaner {
    private ArrayList<String> itemsData;
    private ArrayList<String> staffData;
    private ArrayList<String> assessmentData;
    private ArrayList<String> timetablePatternData;
    private ScrapedData results;
    private String paperId_fk;

    /// Main method to clean an HTML document.
    /// @param paper Paper outline document
    /// @return ScrapedData object that holds all paper details
    public ScrapedData clean(Document paper) {
        newResults();
        extractInformation(paper);
        buildResults();
        return results;
    }

    /// Initialises a new results object
    private void newResults() {
        this.results = new ScrapedData();
    }

    // ─────────────────────────────────────────────────────────────
    // Extraction — raw text only, no entity construction
    // ─────────────────────────────────────────────────────────────

    /// Extracts all raw text arrays from the HTML document
    /// @param paper Paper outline document
    private void extractInformation(Document paper) {
        clearLists();
        for (String name : getLabelNames()) {
            itemsData.add(extractItemData(name, paper));
        }
        this.staffData = extractStaffTableData(paper);
        this.assessmentData = extractAssessmentTableData(paper);
        this.timetablePatternData = extractTimetablePatternData(paper);
    }

    /// Initialises new arraylists
    private void clearLists() {
        this.itemsData = new ArrayList<>();
        this.staffData = new ArrayList<>();
        this.assessmentData = new ArrayList<>();
        this.timetablePatternData = new ArrayList<>();
    }

    /// Returns the ordered label names used to pull core paper fields
    /// @return arraylist of html labels in the paper outline
    private ArrayList<String> getLabelNames() {
        ArrayList<String> labelNames = new ArrayList<>();
        labelNames.add("Paper Title");
        labelNames.add("Paper Occurrence Code");
        labelNames.add("Points");
        labelNames.add("When taught");
        labelNames.add("Start Week");
        labelNames.add("End Week");
        return labelNames;
    }

    /// Extracts a single span value following a matching label element
    /// @param itemName HTML label text from the outline
    /// @param paper    Document to search
    /// @return Text content of the sibling span, or null
    private String extractItemData(String itemName, Document paper) {
        String query = "label:contains(" + itemName + ")";
        Element label = paper.select(query).first();
        if (label != null) {
            Element span = label.nextElementSibling();
            if (span != null) return span.text();
        }
        return null;
    }

    /// Extracts raw cell text from the staff table
    /// @param paper Paper outline document
    /// @return Flat list of staff cell strings
    private ArrayList<String> extractStaffTableData(Document paper) {
        ArrayList<String> tableData = new ArrayList<>();
        Element table = paper.select("table.staff").first();
        if (table != null) {
            for (Element row : table.select("tr")) {
                for (Element col : row.select("td")) {
                    String cellData = col.text();
                    if (cellData.matches(".*@.*@.*")) {
                        splitMultipleStaff(cellData, tableData);
                    } else if (cellData.contains("-")) {
                        splitStaffInformation(cellData, tableData);
                    } else {
                        tableData.add(cellData);
                    }
                }
            }
        }
        return tableData;
    }

    /// Extracts raw cell text from the assessments table (title, date, weight triples)
    /// @param paper Paper outline document
    /// @return Flat list of assessment cell strings
    private ArrayList<String> extractAssessmentTableData(Document paper) {
        ArrayList<String> tableData = new ArrayList<>();
        Element table = paper.select("table.assessments").first();
        if (table != null) {
            for (Element row : table.select("tr")) {
                ArrayList<String> cols = extractAssessmentRow(row);
                if (cols != null) tableData.addAll(cols);
            }
        }
        return tableData;
    }

    /// Extracts raw cell text from the timetable pattern table
    /// @param paper Paper outline document
    /// @return Flat list of timetable cell strings
    private ArrayList<String> extractTimetablePatternData(Document paper) {
        ArrayList<String> tableData = new ArrayList<>();
        Element table = paper.select("table.timetable").first();
        if (table != null) {
            for (Element row : table.select("tr")) {
                for (Element col : row.select("td")) {
                    tableData.add(col.text());
                }
            }
        }
        return tableData;
    }

    // ─────────────────────────────────────────────────────────────
    // Construction — entity building only, no HTML touching
    // ─────────────────────────────────────────────────────────────

    /// Builds all entities from the extracted raw data and populates results
    private void buildResults() {
        results.setPaper(buildPaperEntity());
        results.setStaffs(buildStaffEntities(extractStaffPairs()));
        results.setAssessments(buildAssessmentEntities());
        results.setTimetablePatterns(buildTimetablePatternEntities());
    }

    /// Parses the core paper fields and constructs a PaperEntity.
    /// Also sets paperId_fk and writes semesterCode into results so downstream
    /// code does not need to reach into DataCleaner internals.
    /// @return new PaperEntity
    private PaperEntity buildPaperEntity() {
        String paperId       = itemsData.get(1);
        String paperName     = itemsData.get(0);
        String paperCode     = paperId.split("-")[0];
        int    points        = Integer.parseInt(itemsData.get(2));
        String semesterCode  = "26" + itemsData.get(3);

        this.paperId_fk = paperId;
        results.setSemesterCode(semesterCode);

        return new PaperEntity(paperId, paperName, paperCode, points, semesterCode);
    }

    /// Extracts (name, email, position) triples from the flat staff list.
    /// Pure extraction — no entity construction.
    /// @return list of String arrays: [, email, position]
    private List<String[]> extractStaffPairs() {
        List<String[]> pairs = new ArrayList<>();
        String position = "";
        for (int i = 0; i < staffData.size(); i++) {
            String item = staffData.get(i);
            if (item.contains("@")) {
                String name = (i > 0) ? staffData.get(i - 1) : "Unknown";
                pairs.add(new String[]{ name, item, position });
            } else if (i + 1 < staffData.size() && !staffData.get(i + 1).contains("@")) {
                position = item;
            }
        }
        return pairs;
    }

    /// Constructs StaffEntity objects from pre-extracted (name, email, position) triples.
    /// @param pairs Output of extractStaffPairs()
    /// @return Arraylist of StaffEntity
    private ArrayList<StaffEntity> buildStaffEntities(List<String[]> pairs) {
        ArrayList<StaffEntity> staffList = new ArrayList<>();
        for (String[] pair : pairs) {
            staffList.add(new StaffEntity(pair[0], pair[1], pair[2], paperId_fk));
        }
        return staffList;
    }

    /// Constructs AssessmentEntity objects from the flat assessmentData list.
    /// @return Arraylist of AssessmentEntity
    private ArrayList<AssessmentEntity> buildAssessmentEntities() {
        ArrayList<AssessmentEntity> assessmentList = new ArrayList<>();
        for (int i = 0; i + 2 < assessmentData.size(); i += 3) {
            String title         = assessmentData.get(i);
            Date   dueDate       = DateConverter.stringAbvToDate(assessmentData.get(i + 1));
            Double weight        = Double.valueOf(assessmentData.get(i + 2));
            String assessmentType = findAssessmentType(title);
            assessmentList.add(new AssessmentEntity(title, dueDate, weight, assessmentType, 0.0, paperId_fk));
        }
        return assessmentList;
    }

    /// Constructs TimetablePatternEntity objects from the flat timetablePatternData list.
    /// @return Arraylist of TimetablePatternEntity
    private ArrayList<TimetablePatternEntity> buildTimetablePatternEntities() {
        ArrayList<TimetablePatternEntity> list = new ArrayList<>();
        for (int i = 0; i + 4 < timetablePatternData.size(); i += 5) {
            String type            = timetablePatternData.get(i);
            int    dayOfWeek       = convertDOW(timetablePatternData.get(i + 1));
            String startTimeString = timetablePatternData.get(i + 2);
            String endTimeString   = timetablePatternData.get(i + 3);
            String location        = timetablePatternData.get(i + 4);
            Double duration        = getDuration(startTimeString, endTimeString);
            Date   startTime       = DateConverter.stringToTime(startTimeString);
            Date   endTime         = DateConverter.stringToTime(endTimeString);
            list.add(new TimetablePatternEntity(type, dayOfWeek, startTime, endTime, location, duration, paperId_fk));
        }
        return list;
    }

    // ─────────────────────────────────────────────────────────────
    // Event generation (depends on semester, called by DataRepository)
    // ─────────────────────────────────────────────────────────────

    /// Creates event occurrences from the timetable patterns within a semester window.
    /// @param semester Semester the events occur within
    /// @return Arraylist of EventEntity
    public ArrayList<EventEntity> createEventData(SemesterEntity semester) {
        ArrayList<TimetablePatternEntity> timetablePattern = results.getTimetablePatterns();
        ArrayList<EventEntity> eventEntities = new ArrayList<>();

        Date startDate      = semester.getStartDate();
        Date endDate        = semester.getEndDate();
        Date breakStartDate = semester.getBreakStartDate();
        Date breakEndDate   = semester.getBreakEndDate();

        for (TimetablePatternEntity timetable : timetablePattern) {
            eventEntities.addAll(createEvents(
                    startDate, endDate, breakStartDate, breakEndDate,
                    timetable.getType(), timetable.getDayOfWeek(),
                    timetable.getStartTime(), timetable.getEndTime()));
        }
        return eventEntities;
    }

    /// Generates individual EventEntity occurrences for one timetable pattern.
    /// @param semStart     Start date of the semester
    /// @param semEnd       End date of the semester
    /// @param breakSemStart Start date of the semester break
    /// @param breakSemEnd   End date of the semester break
    /// @param type         Type of timetable pattern event
    /// @param dow          Numeric day of the week the event occurs
    /// @param startTime    Start time of the event
    /// @param endTime      End time of the event
    /// @return Arraylist of EventEntity objects
    private ArrayList<EventEntity> createEvents(
            Date semStart, Date semEnd, Date breakSemStart, Date breakSemEnd,
            String type, int dow, Date startTime, Date endTime) {

        ArrayList<EventEntity> events = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(semStart);

        while (!cal.getTime().after(semEnd)) {
            Date current = cal.getTime();
            if (cal.get(Calendar.DAY_OF_WEEK) == dow) {
                if (current.before(breakSemStart) || current.after(breakSemEnd)) {
                    Date startDateTime = DateConverter.toDateTime(current, startTime);
                    Date endDateTime   = DateConverter.toDateTime(current, endTime);
                    events.add(new EventEntity(startDateTime, endDateTime, false, type));
                }
            }
            cal.add(Calendar.DATE, 1);
        }
        return events;
    }

    // ─────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────

    /// Validates and extracts the first three non-empty columns from an assessment row.
    /// @param row HTML row element
    /// @return Three-element list [, date, weight] or null if not a valid assessment row
    private ArrayList<String> extractAssessmentRow(Element row) {
        ArrayList<String> cells = new ArrayList<>();
        for (Element col : row.select("td")) {
            String text = col.text();
            if (!text.isEmpty()) cells.add(text);
        }
        if (cells.size() < 3) return null;
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < 3; i++) result.add(cells.get(i));
        return result;
    }

    /// Resolves assessment type from title keywords
    /// @param title name of the assessment
    /// @return matching type string, or "Assessment" as default
    private String findAssessmentType(String title) {
        for (String type : buildAssessmentTypes()) {
            if (title.contains(type)) return type;
        }
        return "Assessment";
    }

    /// Returns the ordered list of assessment type keywords to check
    /// @return Arraylist of type strings
    private ArrayList<String> buildAssessmentTypes() {
        ArrayList<String> types = new ArrayList<>();
        types.add("Assessment");
        types.add("Quiz");
        types.add("Test");
        types.add("Report");
        types.add("Presentation");
        types.add("Exam");
        return types;
    }

    /// Converts a three-letter day abbreviation to a Calendar.DAY_OF_WEEK int
    /// @param dayOfWeek Three-letter day string eg. "Mon"
    /// @return Calendar day constant (2 = Monday … 6 = Friday), or 0 if unrecognised
    private int convertDOW(String dayOfWeek) {
        ArrayList<String> days = buildDayList();
        for (String day : days) {
            if (day.equals(dayOfWeek)) return days.indexOf(day) + 2;
        }
        return 0;
    }

    /// Returns ordered weekday abbreviations Mon–Fri
    /// @return arraylist of three-letter day strings
    private ArrayList<String> buildDayList() {
        ArrayList<String> days = new ArrayList<>();
        days.add("Mon");
        days.add("Tue");
        days.add("Wed");
        days.add("Thu");
        days.add("Fri");
        return days;
    }

    /// Calculates whole-hour duration from start and end time strings
    /// @param startTime Text start time eg. "09:00"
    /// @param endTime   Text end time eg. "11:00"
    /// @return Duration as a double
    private Double getDuration(String startTime, String endTime) {
        Double start = Double.valueOf(startTime.split(":")[0]);
        Double end   = Double.valueOf(endTime.split(":")[0]);
        return end - start;
    }

    /// Splits a cell containing multiple staff entries (identified by two @ symbols)
    /// @param cellData Text from a multi-person cell
    /// @param tableData Accumulator list
    private void splitMultipleStaff(String cellData, ArrayList<String> tableData) {
        String[] parts = cellData.split(" ");
        for (int i = 0; i + 3 < parts.length; i += 4) {
            tableData.add(parts[i] + " " + parts[i + 1]);
            tableData.add(parts[i + 3]);
        }
    }

    /// Splits a "Name - email" cell into two separate strings
    /// @param cellData Text from a hyphen-delimited cell
    /// @param tableData Accumulator list
    private void splitStaffInformation(String cellData, ArrayList<String> tableData) {
        String[] parts = cellData.split("-");
        tableData.add(parts[0].trim());
        tableData.add(parts[1].trim());
    }
}