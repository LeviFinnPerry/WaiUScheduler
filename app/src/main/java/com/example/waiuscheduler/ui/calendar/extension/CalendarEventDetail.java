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

        // Header
        ((TextView) view.findViewById(R.id.text_event_title)).setText(occurrence.getTitle());
        ((TextView) view.findViewById(R.id.text_event_type_badge)).setText(occurrence.getType());
        view.findViewById(R.id.view_colour_indicator).setBackgroundColor(occurrence.getColour());

        // Time
        SimpleDateFormat fmt = new SimpleDateFormat("EEE d MMM yyyy, h:mm a", Locale.getDefault());
        String timeText = fmt.format(occurrence.getStartDateTime());
        if (!occurrence.getStartDateTime().equals(occurrence.getEndDateTime())) {
            timeText += " - " + fmt.format(occurrence.getEndDateTime());
        }
        ((TextView) view.findViewById(R.id.text_event_time)).setText(timeText);

        // Type specific sections
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
        view.findViewById(R.id.button_close_dialog).setOnClickListener(v -> dismiss());
    }

    /// Sets up assessments within the calendar
    /// @param view Calendar view
    private void setupAssessmentSection(View view) {
        view.findViewById(R.id.section_assessment).setVisibility(View.VISIBLE);
        AssessmentEntity assessment = (AssessmentEntity) occurrence.getSourceEntity();
        EditText editGrade = view.findViewById(R.id.edit_grade);

        if(assessment.getGrade() != null ) {
            editGrade.setText(String.valueOf(assessment.getGrade()));
        }

        // If assessment due date has already passed
        boolean isFuture = assessment.getDueDate().after(new Date());
        if (isFuture) {
            editGrade.setEnabled(false);
            editGrade.setHint(R.string.assessment_not_due);
            view.findViewById(R.id.button_save_grade).setEnabled(false);
            return;
        }

        view.findViewById(R.id.button_save_grade).setOnClickListener(v -> {
            String input = editGrade.getText() != null ? editGrade.getText().toString().trim(): "";
            if (input.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a grade", Toast.LENGTH_SHORT).show();
                return;
            }
            double grade;
            try {
                grade = Double.parseDouble(input);
            } catch (NumberFormatException e) {
                Log.e("Number Format", "Invalid Grade Value");
                return;
            }
            if (grade < 0 || grade > 100) {
                Toast.makeText(requireContext(), "Grade out of bounds", Toast.LENGTH_SHORT).show();
                return;
            }
            assessment.setGrade(grade);
            vm.updateGrade(assessment);
            Toast.makeText(requireContext(), "Grade saved: " + input, Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }

    /// Sets up the events within the calendar
    /// @param view Calendar view
    private void setupEventSection(View view) {
        view.findViewById(R.id.section_event).setVisibility(View.VISIBLE);
        EventEntity event = (EventEntity) occurrence.getSourceEntity();

        RadioGroup radioGroup = view.findViewById(R.id.radiogroup_attendance);
        if (event.getAttended().equals(true)) radioGroup.check(R.id.radio_attended);
        else if (event.getAttended().equals(false)) radioGroup.check(R.id.radio_missed);

        // Disables inputting event attendance for future events
        boolean isFuture = occurrence.getStartDateTime().after(new Date());
        if (isFuture) {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                radioGroup.getChildAt(i).setEnabled(false);
            }
            view.findViewById(R.id.button_save_attendance).setEnabled(false);
            return;
        }

        view.findViewById(R.id.button_save_attendance).setOnClickListener(v -> {
            int checked = radioGroup.getCheckedRadioButtonId();
            boolean attendance;
            if (checked == R.id.radio_attended) attendance = true;
            else if (checked == R.id.radio_missed) attendance = false;
            else {
                Toast.makeText(requireContext(), "Please select attendance", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            event.setAttended(attendance);
            Toast.makeText(requireContext(), "Attendance saved", Toast.LENGTH_SHORT).show();
        });
    }

    /// Sets up the study sessions within the calendar
    /// @param view Calendar view
    private void setupStudySection(View view) {
        view.findViewById(R.id.section_study).setVisibility(View.VISIBLE);
        StudySessionEntity studySession = (StudySessionEntity) occurrence.getSourceEntity();
        TextView detailsView = view.findViewById(R.id.text_study_details);

        // Builds and displays section details
        refreshStudyGrid(detailsView, studySession);

        // Select details to edit times
        setDetails(detailsView, studySession);

        view.findViewById(R.id.button_delete_study).setOnClickListener(v -> {
            if (vm != null) {
                vm.deleteStudySession(studySession);
            }
            Toast.makeText(requireContext(), "Study session deleted", Toast.LENGTH_SHORT).show();
            dismiss();
        });

    }

    /// Rebuilds study detail text
    /// @param detailsView Textview to update
    /// @param studySession Source entity
    private void refreshStudyGrid(TextView detailsView, StudySessionEntity studySession) {
        SimpleDateFormat fmt = new SimpleDateFormat(
                "EEE d MMM, h:mm a", Locale.getDefault());
        
        String notes = studySession.getNotes();
        String details = "Subject: " + studySession.getPaperId().split("-")[0] + "\n"
                + "Start: "   + fmt.format(studySession.getDateTimeStart()) + "\n"
                + "End: "     + fmt.format(studySession.getDateTimeEnd()) + "\n"
                + (hasNotes(notes) ? "Notes: " + notes : "");
        ((TextView) detailsView.findViewById(R.id.text_study_details)).setText(details);
    }

    /// Whether the study session has notes
    /// @param notes notes taken
    /// @return True if has notes else false
    private boolean hasNotes(String notes) {
        return notes != null && !notes.trim().isEmpty();
    }

    /// Opens a date then time picker to set the session start time
    /// @param session Study session being edited
    /// @param onSaved Runnable called for confirmed times
    private void pickStartTime(StudySessionEntity session, Runnable onSaved) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(session.getDateTimeStart());

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

                setTimes(startCal, endCal, session, onSaved);
            }, endCal.get(Calendar.HOUR_OF_DAY), endCal.get(Calendar.MINUTE), false).show();
        }, endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH), endCal.get(Calendar.DAY_OF_MONTH)).show();
    }

    /// Sets the pickers to set the session times
    /// @param startCal Start calendar time
    /// @param endCal End calendar time
    /// @param session Study session being edited
    /// @param onSaved Runnable called for confirmed times
    private void setTimes(Calendar startCal, Calendar endCal, StudySessionEntity session, Runnable onSaved) {
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

    /// Sets the on click listener for setting times
    /// @param detailsView Text view to select
    /// @param studySession Study session to edit
    private void setDetails(TextView detailsView, StudySessionEntity studySession) {
        detailsView.setClickable(true);
        detailsView.setOnClickListener(v -> pickStartTime(studySession, () -> refreshStudyGrid(detailsView, studySession)));
    }





}
