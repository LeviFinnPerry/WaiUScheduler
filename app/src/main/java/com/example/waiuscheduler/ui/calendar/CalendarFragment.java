package com.example.waiuscheduler.ui.calendar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.waiuscheduler.database.AppDatabase;
import com.example.waiuscheduler.databinding.FragmentCalendarBinding;
import com.example.waiuscheduler.ui.calendar.extension.CalendarAdapter;
import com.example.waiuscheduler.ui.calendar.extension.CalendarEventDetail;
import com.example.waiuscheduler.ui.calendar.extension.CalendarOccurrence;
import com.example.waiuscheduler.ui.calendar.extension.DayTimelineView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CalendarFragment extends Fragment {
    // TODO: Make the week view easier to read
    // TODO: Make the day view fit the page
    private FragmentCalendarBinding binding;
    private CalendarViewModel viewModel;
    private CalendarAdapter adapter;
    private DayTimelineView dayTimelineView;

    private final Handler refreshHandler = new Handler(Looper.getMainLooper());
    private Runnable refreshRunnable;


    /// Initialises fragment view
    /// @param inflater Layout inflater
    /// @param container View Group of the fragment
    /// @param savedInstanceState Bundle for the instance
    /// @return View for calendar
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState
    ) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);

        // Grid adapter
        adapter = new CalendarAdapter(requireContext());
        binding.gridViewCalendar.setAdapter(adapter);

        // Day timeline
        RelativeLayout timelineContainer = binding.timelineContainer;
        this.dayTimelineView = new DayTimelineView(
                requireContext(),
                timelineContainer,
                this::openDetailDialog
        );

        // Set up methods
        setupNavButtons();
        setupViewToggle();
        setupFilterButtons();
        setupAdapterClickListener();
        observeViewModel();
        viewModel.initialLoad();
    }

    /// Destroys the view
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupNavButtons() {
        binding.buttonPrev.setOnClickListener(v -> viewModel.goToPrevious());
        binding.buttonNext.setOnClickListener(v -> viewModel.goToNext());
        binding.buttonToday.setOnClickListener(v -> viewModel.goToToday());
    }

    private void setupViewToggle() {
        binding.radioDay.setOnClickListener(v ->
                viewModel.setViewMode(CalendarViewModel.MODE_DAY));
        binding.radioWeek.setOnClickListener(v ->
                viewModel.setViewMode(CalendarViewModel.MODE_WEEK));
        binding.radioMonth.setOnClickListener(v ->
                viewModel.setViewMode(CalendarViewModel.MODE_MONTH));

    }

    private void setupFilterButtons() {
        binding.buttonFilterStudy.setOnClickListener(v -> {
            viewModel.toggleFilter(CalendarOccurrence.TYPE_STUDY);
            refreshFilterButtons();
        });
        binding.buttonFilterLecture.setOnClickListener(v -> {
            viewModel.toggleFilter(CalendarOccurrence.TYPE_EVENT);
            refreshFilterButtons();
        });
        binding.buttonFilterAssignment.setOnClickListener(v -> {
            viewModel.toggleFilter(CalendarOccurrence.TYPE_ASSESSMENT);
            refreshFilterButtons();
        });
    }

    private void refreshFilterButtons() {
        binding.buttonFilterStudy.setAlpha(
                viewModel.isFilterActive(CalendarOccurrence.TYPE_STUDY) ? 1f : 0.4f);
        binding.buttonFilterLecture.setAlpha(
                viewModel.isFilterActive(CalendarOccurrence.TYPE_EVENT) ? 1f : 0.4f);
        binding.buttonFilterAssignment.setAlpha(
                viewModel.isFilterActive(CalendarOccurrence.TYPE_ASSESSMENT) ? 1f : 0.4f);

    }

    private void setupAdapterClickListener() {
        adapter.setOnDayClickListener((date, eventsOnDay) -> {
            if (eventsOnDay.isEmpty()) return; // no events
            if (eventsOnDay.size() == 1) {
                // Open the dialog directly for a single event
                openDetailDialog(eventsOnDay.get(0));
            } else {
                // Show a picker list when multiple events fall on the same day
                showEventPickerDialog(eventsOnDay);
            }
        });
    }

    private void openDetailDialog(CalendarOccurrence occ) {
        CalendarEventDetail.newInstance(occ, viewModel)
                .show(getChildFragmentManager(), "event_detail");

    }

    private void showEventPickerDialog(List<CalendarOccurrence> events) {
        String[] titles = events.stream()
                .map(o -> o.getType() + ": " + o.getTitle())
                .toArray(String[]::new);
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Select event")
                .setItems(titles, (dlg, which) -> openDetailDialog(events.get(which)))
                .show();
    }

    private void observeViewModel() {
        viewModel.getCurrentDate().observe(getViewLifecycleOwner(), date -> {
            updateHeaderLabel(date);
            refreshGrid(viewModel.getOccurrences().getValue());
        });
        viewModel.getViewMode().observe(getViewLifecycleOwner(), (Observer<? super String>) mode -> {
            // Sync radio buttons without triggering listeners
            binding.radioDay.setChecked(CalendarViewModel.MODE_DAY.equals(mode));
            binding.radioWeek.setChecked(CalendarViewModel.MODE_WEEK.equals(mode));
            binding.radioMonth.setChecked(CalendarViewModel.MODE_MONTH.equals(mode));
            // Day labels row — only meaningful for month/week
            binding.dayLabels.setVisibility(
                    CalendarViewModel.MODE_DAY.equals(mode) ? View.GONE : View.VISIBLE);
            refreshGrid(viewModel.getOccurrences().getValue());
        });


        viewModel.getOccurrences().observe(getViewLifecycleOwner(), this::refreshGrid);
        viewModel.getFilters().observe(getViewLifecycleOwner(),
                f -> refreshGrid(viewModel.getOccurrences().getValue())
        );
    }

    private void updateHeaderLabel(Calendar date) {
        String mode = viewModel.getViewMode().getValue();
        String format;
        if (CalendarViewModel.MODE_DAY.equals(mode)) format = "EEEE d MMMM yyyy";
        else if (CalendarViewModel.MODE_WEEK.equals(mode)) format = "'Week of' d MMM yyyy";
        else format = "MMMM yyyy";
        binding.textMonthYear.setText(
                new SimpleDateFormat(format, Locale.getDefault()).format(date.getTime()));

    }

    private void refreshGrid(List<CalendarOccurrence> events) {
        Set<String> filters = viewModel.getFilters().getValue();

        if (refreshRunnable != null) refreshHandler.removeCallbacks(refreshRunnable);
        refreshRunnable = () -> performRefresh(events, filters);
        refreshHandler.postDelayed(refreshRunnable, 50);
    }

    private void performRefresh(List<CalendarOccurrence> events, Set<String> filters) {
        String mode = viewModel.getViewMode().getValue();
        Calendar current = viewModel.getCurrentDate().getValue();
        if (current == null || mode == null) return;

        // Safe defaults
        List<CalendarOccurrence> safeEvents = events != null ? events : new ArrayList<>();
        Set<String> safeFilters = filters != null ? filters : new HashSet<>();

        // Handling of timeline for days
        if (CalendarViewModel.MODE_DAY.equals(mode)) {
            // Day view
            binding.scrollDayTimeline.setVisibility(View.VISIBLE);
            binding.calendarGrid.setVisibility(View.GONE);
            binding.dayLabels.setVisibility(View.GONE);

            // Apply filters
            List<CalendarOccurrence> filtered = new ArrayList<>();
            for (CalendarOccurrence occ: safeEvents) {
                if (safeFilters.contains(occ.getType())) filtered.add(occ);
            }

            dayTimelineView.build(current.getTime(), filtered);
        } else {
            binding.scrollDayTimeline.setVisibility(View.GONE);
            binding.calendarGrid.setVisibility(View.VISIBLE);
            binding.dayLabels.setVisibility(View.VISIBLE);

            // Set column count
            binding.gridViewCalendar.setNumColumns(7);
        }

        // Run on background thread
        Calendar currentCopy = (Calendar) current.clone();
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Date> days;
            if (CalendarViewModel.MODE_MONTH.equals(mode)) days = buildMonthDays(currentCopy);
            else if (CalendarViewModel.MODE_WEEK.equals(mode)) days = buildWeekDays(currentCopy);
            else days = buildDay(currentCopy);

            final List<Date> finalDays = days;
            requireActivity().runOnUiThread(() -> {
                if (binding == null) return;

                // Adjust GridView column count
                binding.gridViewCalendar.setNumColumns(
                        CalendarViewModel.MODE_DAY.equals(mode) ? 1 : 7);

                adapter.update(finalDays, events, filters);
            });
        });
    }

    private List<Date> buildMonthDays(Calendar c) {
        List<Date> days = new ArrayList<>();
        Calendar first = (Calendar) c.clone();
        first.set(Calendar.DAY_OF_MONTH, 1);
        int startOffset = first.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
        // Prepend nulls
        for (int i = 0; i < startOffset; i++) days.add(null);
        int max = first.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar day = (Calendar) first.clone();
        for (int d = 1; d <= max; d++) {
            day.set(Calendar.DAY_OF_MONTH, d);
            days.add(day.getTime());
        }
        // Pad to multiple of 7
        while (days.size() % 7 != 0) days.add(null);
        return days;
    }

    private List<Date> buildWeekDays(Calendar c) {
        List<Date> days = new ArrayList<>();
        Calendar sunday = (Calendar) c.clone();
        sunday.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        for (int i = 0; i < 7; i++) {
            days.add(sunday.getTime());
            sunday.add(Calendar.DAY_OF_MONTH, 1);
        }
        return days;
    }

    private List<Date> buildDay(Calendar c) {
        return Collections.singletonList(c.getTime());
    }
}