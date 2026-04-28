package com.example.waiuscheduler.ui.calendar.extension;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
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
        TextInputEditText editGrade = view.findViewById(R.id.edit_grade);

        if(assessment.getGrade() != null ) {
            editGrade.setText(String.valueOf(assessment.getGrade()));
        }

        view.findViewById(R.id.button_save_grade).setOnClickListener(v -> {
            double input =
                    Double.parseDouble(editGrade.getText() != null ? editGrade.getText().toString()
                            .trim() : "");
            if (Double.isNaN(input)) {
                Toast.makeText(requireContext(), "Please enter a grade", Toast.LENGTH_SHORT).show();
            }
            if (input < 0 || input > 100) {
                Toast.makeText(requireContext(), "Grade out of bounds", Toast.LENGTH_SHORT).show();
                return;
            }
            assessment.setGrade(input);
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
        SimpleDateFormat fmt = new SimpleDateFormat(
                "EEE d MMM, h:mm a", Locale.getDefault());
        String details = "Subject: " + studySession.getPaperId_fk().split("-")[0] + "\n"
                + "Start: "   + fmt.format(studySession.getDateTimeStart()) + "\n"
                + "End: "     + fmt.format(studySession.getDateTimeEnd());
        ((TextView) view.findViewById(R.id.text_study_details)).setText(details);

        view.findViewById(R.id.button_view_study).setOnClickListener(v -> {
            // TODO: Navigate to study session details
            dismiss();
        });

        view.findViewById(R.id.button_delete_study).setOnClickListener(v -> {
            if (vm != null) {
                vm.deleteStudySession(studySession);
            }
            Toast.makeText(requireContext(), "Study session deleted", Toast.LENGTH_SHORT).show();
            dismiss();
        });

    }

}
