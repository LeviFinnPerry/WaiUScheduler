package com.example.waiuscheduler.parsing;

import static org.junit.jupiter.api.Assertions.*;

import com.example.waiuscheduler.database.tables.AssessmentEntity;
import com.example.waiuscheduler.database.tables.PaperEntity;
import com.example.waiuscheduler.database.tables.StaffEntity;
import com.example.waiuscheduler.database.tables.TimetablePatternEntity;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Unit tests for the {@link DataCleaner} class.
 * Verifies that the cleaning logic correctly maps raw HTML from a Waikato Paper Outline
 * into database-ready entities.
 */
class DataCleanerTest {

    private ScrapedData results;

    /**
     * Loads the static test HTML file from the project's test source tree.
     *
     * @return The raw HTML content of the test document as a string.
     * @throws IOException If the file cannot be accessed or does not exist.
     * @see DataCleaner#clean(org.jsoup.nodes.Document)
     */
    private String loadHtmlFile() throws IOException {
        String path = "com/example/waiuscheduler/resources/testDoc.html";
        InputStream inputStream = Objects.requireNonNull(getClass().getClassLoader()).getResourceAsStream(path);

        if (inputStream == null) {
            throw new FileNotFoundException("Could not find file at: " + path);
        }

        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * Set up logic executed before every test.
     * Loads the {@link #loadHtmlFile()} and populates the {@link ScrapedData} results.
     *
     * @throws IOException If the test resource fails to load.
     */
    @BeforeEach
    void setUp() throws IOException {
        String html = loadHtmlFile();
        DataCleaner cleaner = new DataCleaner();
        results = cleaner.clean(Jsoup.parse(html));
    }

    /**
     * Verifies that core paper metadata is extracted correctly from the HTML header section.
     *
     * @see PaperEntity
     */
    @Test
    void testPaperInformation() {
        PaperEntity paper = results.getPaper();
        assertNotNull(paper, "PaperEntity should not be null");

        assertAll("Paper Header Data",
                () -> assertEquals("Programming Project", paper.getPaperCode()),
                () -> assertEquals("COMPX576-26A (HAM)", paper.getPaperId()),
                () -> assertEquals(15, paper.getPoints()),
                () -> assertEquals("26A", paper.getSemesterCode_fk())
        );
    }

    /**
     * Verifies that staff names and emails are correctly parsed from the staff table.
     *
     * @see StaffEntity
     */
    @Test
    void testStaffInformation() {
        List<StaffEntity> staffs = results.getStaffs();
        assertFalse(staffs.isEmpty(), "Staff list should not be empty");

        StaffEntity convenor = staffs.get(0);
        assertAll("Convenor Details",
                () -> assertEquals("Dr Sapna Jaidka", convenor.getName()),
                () -> assertEquals("sapna.jaidka@waikato.ac.nz", convenor.getEmail())
        );
    }

    /**
     * Verifies that weekly timetable patterns are extracted from the timetable table.
     *
     * @see TimetablePatternEntity
     */
    @Test
    void testTimetableInformation() {
        List<TimetablePatternEntity> patterns = results.getTimetablePatterns();

        assertEquals(2, patterns.size(), "Should have found 2 timetable patterns");

        TimetablePatternEntity firstLecture = patterns.get(0);
        assertAll("First Lecture Details",
                () -> assertEquals("Lecture 1", firstLecture.getType()),
                () -> assertEquals(2, firstLecture.getDayOfWeek()), // Assuming 2 represents Monday
                () -> assertEquals("R.G.06 / R.G.07", firstLecture.getLocation())
        );
    }

    /**
     * Verifies that assessment items are correctly parsed, including titles and weights.
     *
     * @see AssessmentEntity
     */
    @Test
    void testAssessmentInformation() {
        List<AssessmentEntity> assessments = results.getAssessments();

        assertFalse(assessments.isEmpty(), "Assessments list should not be empty");

        AssessmentEntity firstAssessment = assessments.get(0);

        assertAll("First Assessment Details",
                () -> assertEquals("Project Proposal (2%)", firstAssessment.getTitle()),
                () -> assertEquals("Assessment", firstAssessment.getType()),
                () -> assertEquals(2.0, firstAssessment.getWeight(), 0.01),
                () -> assertEquals(0, firstAssessment.getGrade(), 0.01)
        );
    }
}
