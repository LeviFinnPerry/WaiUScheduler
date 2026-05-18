package com.example.waiuscheduler.ui.calendar.extension;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.waiuscheduler.R;
import com.example.waiuscheduler.database.tables.AssessmentEntity;
import com.example.waiuscheduler.database.tables.EventEntity;
import com.example.waiuscheduler.database.tables.StudySessionEntity;
import com.example.waiuscheduler.ui.calendar.CalendarViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarEventDetail extends BottomSheetDialogFragment {

    private CalendarOccurrence occurrence;
    private CalendarViewModel vm;

    /// Static factor for calendar event detail instances
    /// @param occ Calendar occurrence
    /// @return details for the occurrence
    public static CalendarEventDetail newInstance(CalendarOccurrence occ, CalendarViewModel viewModel) {
        CalendarEventDetail detail = new CalendarEventDetail();
        detail.occurrence = occ;
        detail.vm = viewModel;
        return detail;
    }

    /// Create view for calendar event detail
    /// @param inflater Layout inflater
    /// @param container View Group
    /// @param savedInstanceState Bundle
    /// @return Inflated view model
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState ) {
        return inflater.inflate(R.layout.calendar_event_detail, container, false);
    }

    /// Handling once view is created with headers, times, and event types
    /// @param view Calendar view
    /// @param savedInstanceState Bundle
    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState ) {
        super.onViewCreated(view, savedInstanceState);
        if (occurrence == null) {
            dismiss();
            return;
        }

        bindHeader(view);
        bindTime(view);
        bindTypeSection(view);

        view.findViewById(R.id.button_close_dialog).setOnClickListener(v -> dismiss());
    }

    ///  Populates time, type and colour from the occurrence
    /// @param view main view
    private void bindHeader(View view) {
        ((TextView) view.findViewById(R.id.text_event_title)).setText(occurrence.getTitle());
        ((TextView) view.findViewById(R.id.text_event_type_badge)).setText(occurrence.getType());
        view.findViewById(R.id.view_colour_indicator).setBackgroundColor(occurrence.getColour());
    }

    ///  Populates time label for the occurrence
    /// @param view main view
    private void bindTime(View view) {
        SimpleDateFormat fmt = new SimpleDateFormat("EEE d MMM yyyy, h:mm a", Locale.getDefault());
        String timeText = fmt.format(occurrence.getStartDateTime());
        if (!occurrence.getStartDateTime().equals(occurrence.getEndDateTime())) {
            timeText += " - " + fmt.format(occurrence.getEndDateTime());
        }
        ((TextView) view.findViewById(R.id.text_event_time)).setText(timeText);
    }

    /// Routes to the correct type section
    private void bindTypeSection(View view) {
        switch (occurrence.getType()) {
            case CalendarOccurrence.TYPE_ASSESSMENT:
                setupAssessmentSection(view);
                break;

            case CalendarOccurrence.TYPE_EVENT:
                setupEventSection(view);
                break;

            case CalendarOccurrence.TYPE_STUDY:
                setupStudySection(view);
                break;
        }
    }

    /// Sets up assessments within the calendar
    /// @param view Calendar view
    private void setupAssessmentSection(View view) {
        view.findViewById(R.id.section_assessment).setVisibility(View.VISIBLE);
        AssessmentEntity assessment = (AssessmentEntity) occurrence.getSourceEntity();
        EditText editGrade = view.findViewById(R.id.edit_grade);

        populateExistingGrade(editGrade, assessment);
        applyFutureAssessmentGating(view, editGrade, assessment);
        bindSaveGradeButton(view, editGrade, assessment);
    }

    /// Pre fills the grade field if a grade has been recorded
    /// @param editGrade grade input field
    /// @param assessment source entity
    private void populateExistingGrade(EditText editGrade, AssessmentEntity assessment) {
        if(assessment.getGrade() != null ) {
            editGrade.setText(String.valueOf(assessment.getGrade()));
        }
    }

    /// Disables grade entry and the save button if the assessment hasn't been due
    /// @param view root dialog view
    /// @param editGrade grade input field
    /// @param assessment source entity
    private void applyFutureAssessmentGating(View view, EditText editGrade, AssessmentEntity assessment) {
        if (isFutureDate(assessment.getDueDate())) {
            editGrade.setEnabled(false);
            editGrade.setHint(R.string.assessment_not_due);
            view.findViewById(R.id.button_save_grade).setEnabled(false);
        }
    }

    /// Connects to save button for grade
    /// @param view root dialog view
    /// @param editGrade grade input field
    /// @param assessment source entity
    private void bindSaveGradeButton(View view, EditText editGrade, AssessmentEntity assessment) {
        view.findViewById(R.id.button_save_grade).setOnClickListener(v -> {
            String input = trimmedText(editGrade);
            if (input.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a grade", Toast.LENGTH_SHORT).show();
                return;
            }
            double grade = parseGrade(input);
            if (Double.isNaN(grade)) return;
            if (!isValidGrade(grade)) {
                Toast.makeText(requireContext(), "Grade out of bounds", Toast.LENGTH_SHORT).show();
                return;
            }
            assessment.setGrade(grade);
            vm.updateGrade(assessment);
            Toast.makeText(requireContext(), "Grade saved: " + input, Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }

    /// Parses a grade string into a double
    /// @param input grade string
    /// @return grade double
    private double parseGrade(String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            Log.e("Number Format", "Invalid grade value");
            return Double.NaN;
        }
    }

    /// If the grade is between 0 and 100
    /// @param grade grade inputted
    /// @return true if in range else false
    private boolean isValidGrade(double grade) {
        return grade >=0 && grade <= 100;
    }

    /// Sets up the events within the calendar
    /// @param view Calendar view
    private void setupEventSection(View view) {
        view.findViewById(R.id.section_event).setVisibility(View.VISIBLE);
        EventEntity event = (EventEntity) occurrence.getSourceEntity();
        RadioGroup radioGroup = view.findViewById(R.id.radiogroup_attendance);

        populateAttendanceSection(radioGroup, event);
        applyFutureEventGating(view, radioGroup);
        bindSaveAttendanceButtons(view, radioGroup, event);
    }

    /// Preselects correct radio button on the attendance
    /// @param radioGroup attendance radio group
    /// @param event source entity
    private void populateAttendanceSection(RadioGroup radioGroup, EventEntity event) {
        if (event.getAttended().equals(true)) radioGroup.check(R.id.radio_attended);
        else if (event.getAttended().equals(false)) radioGroup.check(R.id.radio_missed);
    }

    /// Prevents attendance being marked on future events
    /// @param radioGroup attendance radio group
    /// @param view root dialog view
    private void applyFutureEventGating(View view, RadioGroup radioGroup) {
        if (isFutureDate(occurrence.getStartDateTime())) {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                radioGroup.getChildAt(i).setEnabled(false);
            }
            view.findViewById(R.id.button_save_attendance).setEnabled(false);
        }
    }

    /// Connects to save button for attendance
    /// @param view root dialog view
    /// @param radioGroup attendance radio group
    /// @param event source entity
    private void bindSaveAttendanceButtons(View view, RadioGroup radioGroup, EventEntity event) {
        view.findViewById(R.id.button_save_attendance).setOnClickListener(v -> {

            Boolean attendance = resolveAttendance(radioGroup);
            if (attendance != null) {
                event.setAttended(attendance);
                Toast.makeText(requireContext(), "Attendance saved", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(requireContext(), "Please select attendance", Toast.LENGTH_SHORT)
                        .show();
                }
        });
    }

    /// Maps the checked radio button to an attendance boolean
    /// @param radioGroup Attendance radio group
    /// @return true if attended else false
    @Nullable
    private Boolean resolveAttendance(RadioGroup radioGroup) {
        int checked = radioGroup.getCheckedRadioButtonId();
        if (checked == R.id.radio_attended) return true;
        else if (checked == R.id.radio_missed) return false;
        return null;
    }

    /// Shows the study section, renders details, and wires edit/delete interactions
    /// @param view Root dialog view
    private void setupStudySection(View view) {
        view.findViewById(R.id.section_study).setVisibility(View.VISIBLE);
        StudySessionEntity session = (StudySessionEntity) occurrence.getSourceEntity();
        TextView detailsView = view.findViewById(R.id.text_study_details);
        renderStudyDetails(detailsView, session);
        bindStudyEditListener(detailsView, session);
        bindStudyDeleteButton(view, session);
    }

    /// Formats and displays study session details in the text view.
    /// Separated from setup so it can be called again after an edit.
    /// @param detailsView TextView to populate
    /// @param session     Source entity
    private void renderStudyDetails(TextView detailsView, StudySessionEntity session) {
        detailsView.setText(formatStudyDetails(session));
    }

    /// Pure formatting function — returns the multi-line detail string for a study session.
    /// No view access; independently testable.
    /// @param session Source entity
    /// @return Formatted detail string
    private String formatStudyDetails(StudySessionEntity session) {
        SimpleDateFormat fmt = new SimpleDateFormat("EEE d MMM, h:mm a", Locale.getDefault());
        String notes = session.getNotes();
        return "Subject: " + session.getPaperId().split("-")[0] + "\n"
                + "Start: "   + fmt.format(session.getDateTimeStart()) + "\n"
                + "End: "     + fmt.format(session.getDateTimeEnd()) + "\n"
                + (hasNotes(notes) ? "Notes: " + notes : "");
    }

    /// Makes the details view clickable to trigger time editing
    /// @param detailsView Clickable text view
    /// @param session     Session being edited
    private void bindStudyEditListener(TextView detailsView, StudySessionEntity session) {
        detailsView.setClickable(true);
        detailsView.setOnClickListener(v ->
                pickStartTime(session, () -> renderStudyDetails(detailsView, session)));
    }

    /// Wires the delete button to study session
    /// @param view    Root dialog view
    /// @param session Session to delete
    private void bindStudyDeleteButton(View view, StudySessionEntity session) {
        view.findViewById(R.id.button_delete_study).setOnClickListener(v -> {
            if (vm != null) vm.deleteStudySession(session);
            Toast.makeText(requireContext(), "Study session deleted", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }

    /// Returns true when a notes string has content
    /// @param notes Notes string
    /// @return true if notes is present
    private boolean hasNotes(String notes) {
        return notes != null && !notes.trim().isEmpty();
    }

    /// Opens a date then time picker to set the session start time
    /// @param session Study session being edited
    /// @param onSaved Runnable called for confirmed times
    private void pickStartTime(StudySessionEntity session, Runnable onSaved) {
        Calendar startCal = calendarFromDate(session.getDateTimeStart());

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(session.getDateTimeEnd());

        // Pick start date
        new DatePickerDialog(requireContext(), (dp, year, month, day) -> {
            startCal.set(year, month, day);
            // Pick start time
            new TimePickerDialog(requireContext(), (tp, hour, minute) -> {
                startCal.set(Calendar.HOUR_OF_DAY, hour);
                startCal.set(Calendar.MINUTE, minute);

                // Send to picking end time
                pickEndTime(session, startCal, endCal, onSaved);

            }, startCal.get(Calendar.HOUR_OF_DAY), startCal.get(Calendar.MINUTE), false).show();

        }, startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), startCal.get(Calendar.DAY_OF_MONTH)).show();
    }

    /// Opens a date then time picker to set the session end time
    /// @param session Study session being edited
    /// @param onSaved Runnable called for confirmed times
    private void pickEndTime(StudySessionEntity session, Calendar startCal, Calendar endCal, Runnable onSaved) {
        new DatePickerDialog(requireContext(), (dp, year, month, day) -> {
            endCal.set(year, month, day);

            new TimePickerDialog(requireContext(), (tp, hour, minute) -> {
                endCal.set(Calendar.HOUR_OF_DAY, hour);
                endCal.set(Calendar.MINUTE, minute);

                applySessionTimes(startCal, endCal, session, onSaved);
            }, endCal.get(Calendar.HOUR_OF_DAY), endCal.get(Calendar.MINUTE), false).show();
        }, endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH), endCal.get(Calendar.DAY_OF_MONTH)).show();
    }

    /// Sets the pickers to set the session times
    /// @param startCal Start calendar time
    /// @param endCal End calendar time
    /// @param session Study session being edited
    /// @param onSaved Runnable called for confirmed times
    private void applySessionTimes(Calendar startCal, Calendar endCal, StudySessionEntity session, Runnable onSaved) {
        // End must be after start
        if (!endCal.getTime().after(startCal.getTime())) {
            Toast.makeText(requireContext(), "End time must be after start", Toast.LENGTH_SHORT).show();
        } else {
            // Set times to update session
            session.setDateTimeStart(startCal.getTime());
            session.setDateTimeEnd(endCal.getTime());
            if (vm != null) vm.updateStudySession(session);
            Toast.makeText(requireContext(), "Session time updated", Toast.LENGTH_SHORT).show();
        }
        onSaved.run();
    }


    /// Returns true when the given date is in the future relative to now
    /// @param date Date to test
    /// @return true if date is after the current instant
    private boolean isFutureDate(Date date) {
        return date.after(new Date());
    }

    /// Constructs a Calendar set to the given Date
    /// @param date Source date
    /// @return Initialised Calendar
    private Calendar calendarFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    /// Returns the trimmed text from an EditText, or an empty string if null
    /// @param editText Source field
    /// @return Trimmed string
    private String trimmedText(EditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }
}
