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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CalendarFragment extends Fragment {
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

    /// Sets the calendar view, calendar layout, initialises buttons and listeners
    /// @param view Calendar view
    /// @param savedInstanceState Bundle
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
        dayTimelineView = new DayTimelineView(
                requireContext(),
                timelineContainer,
                this::openDetailDialog
        );

        // Set up methods
        setUpDayLabels();
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

    /// Sets up the date navigation buttons
    private void setupNavButtons() {
        binding.buttonPrev.setOnClickListener(v -> viewModel.goToPrevious());
        binding.buttonNext.setOnClickListener(v -> viewModel.goToNext());
        binding.buttonToday.setOnClickListener(v -> viewModel.goToToday());
    }


    /// Sets up the different types of calendar views
    private void setupViewToggle() {
        binding.radioDay.setOnClickListener(v ->
                viewModel.setViewMode(CalendarViewModel.MODE_DAY));
        binding.radioWeek.setOnClickListener(v ->
                viewModel.setViewMode(CalendarViewModel.MODE_WEEK));
        binding.radioMonth.setOnClickListener(v ->
                viewModel.setViewMode(CalendarViewModel.MODE_MONTH));

    }

    /// Sets up filter buttons for different types of calendar occurrences
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

    /// Refreshes all filter buttons to visible
    private void refreshFilterButtons() {
        binding.buttonFilterStudy.setAlpha(
                viewModel.isFilterActive(CalendarOccurrence.TYPE_STUDY) ? 1f : 0.4f);
        binding.buttonFilterLecture.setAlpha(
                viewModel.isFilterActive(CalendarOccurrence.TYPE_EVENT) ? 1f : 0.4f);
        binding.buttonFilterAssignment.setAlpha(
                viewModel.isFilterActive(CalendarOccurrence.TYPE_ASSESSMENT) ? 1f : 0.4f);

    }

    /// Sets up the click listener for calendar days
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

    /// Opens the detail dialog about a calendar occurrence
    /// @param occ Calendar occurrence
    private void openDetailDialog(CalendarOccurrence occ) {
        CalendarEventDetail.newInstance(occ, viewModel)
                .show(getChildFragmentManager(), "event_detail");

    }

    /// Shows the event picker to the user to select an event
    /// @param events List of all calendar occurrences
    private void showEventPickerDialog(List<CalendarOccurrence> events) {
        String[] titles = events.stream()
                .map(o -> o.getType() + ": " + o.getTitle())
                .toArray(String[]::new);

        final int[] selected = { 0 };

        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Select event")
                .setSingleChoiceItems(titles, 0, (dialog, which) -> selected[0] = which)
                .setPositiveButton("Open", (dialog, which) ->
                        openDetailDialog(events.get(selected[0])))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /// Watches the view model for any changes by user
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

            // Update header labels on any change
            Calendar current = viewModel.getCurrentDate().getValue();
            if (current != null) updateHeaderLabel(current);

            refreshGrid(viewModel.getOccurrences().getValue());
        });


        viewModel.getOccurrences().observe(getViewLifecycleOwner(), this::refreshGrid);
        viewModel.getFilters().observe(getViewLifecycleOwner(),
                f -> refreshGrid(viewModel.getOccurrences().getValue())
        );
    }

    /// Updates the header label in the calendar view
    /// @param date Date to update label for
    private void updateHeaderLabel(Calendar date) {
        String mode = viewModel.getViewMode().getValue();
        String format;
        if (CalendarViewModel.MODE_DAY.equals(mode)) format = "EEEE d MMMM yyyy";
        else if (CalendarViewModel.MODE_WEEK.equals(mode)) format = "'Week of' d MMM yyyy";
        else format = "MMMM yyyy";
        binding.textMonthYear.setText(
                new SimpleDateFormat(format, Locale.getDefault()).format(date.getTime()));

    }

    /// Refreshes entire grid on navigation on calendar
    /// @param events Events for calendar grid
    private void refreshGrid(List<CalendarOccurrence> events) {
        Set<String> filters = viewModel.getFilters().getValue();

        if (refreshRunnable != null) refreshHandler.removeCallbacks(refreshRunnable);
        refreshRunnable = () -> performRefresh(events, filters);
        refreshHandler.postDelayed(refreshRunnable, 50);
    }

    /// Performs the refresh from the handler
    /// @param events Events for calendar grid
    /// @param filters Selected filters on view
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

            final List<CalendarOccurrence> finalFiltered = filtered;
            final Date finalDate = current.getTime();
            binding.scrollDayTimeline.post(() -> {
                if (binding != null) {
                    dayTimelineView.build(finalDate, finalFiltered);
                }
            });
            dayTimelineView.build(current.getTime(), filtered);
        } else {
            binding.scrollDayTimeline.setVisibility(View.GONE);
            binding.calendarGrid.setVisibility(View.VISIBLE);
            binding.dayLabels.setVisibility(View.VISIBLE);

            boolean isWeek = CalendarViewModel.MODE_WEEK.equals(mode);

            // Set column count
            binding.gridViewCalendar.setNumColumns(5);

            // Run on background thread
            Calendar currentCopy = (Calendar) current.clone();
            AppDatabase.databaseWriteExecutor.execute(() -> {

                final List<Date> finalDays = isWeek
                        ? buildWeekDays(currentCopy)
                        : buildMonthDays(currentCopy);
                if (getActivity() == null) return;
                requireActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    adapter.update(finalDays, events, filters);
                });
            });
        }
    }

    /// Indefinitely hide weekends
    private void setUpDayLabels() {
        binding.dayLabels.getChildAt(0).setVisibility(View.GONE);
        binding.dayLabels.getChildAt(6).setVisibility(View.GONE);
    }


    /// Builds a calendar month view
    /// @param c First day for calendar view
    /// @return Days in the view
    private List<Date> buildMonthDays(Calendar c) {
        List<Date> days = new ArrayList<>();
        Calendar first = (Calendar) c.clone();
        first.set(Calendar.DAY_OF_MONTH, 1);

        // Offset from monday
        int dayOfWeek = first.get(Calendar.DAY_OF_WEEK);
        int startOffset;

        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            startOffset = 0;
        } else {
            startOffset = dayOfWeek - Calendar.MONDAY;
        }

        // Prepend nulls
        for (int i = 0; i < startOffset; i++) days.add(null);

        int max = first.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar day = (Calendar) first.clone();
        for (int d = 1; d <= max; d++) {
            day.set(Calendar.DAY_OF_MONTH, d);
            int dow = day.get(Calendar.DAY_OF_WEEK);
            if (dow != Calendar.SATURDAY && dow != Calendar.SUNDAY) {
                days.add(day.getTime());
            }
        }
        // Pad to multiple of 7
        while (days.size() % 5 != 0) days.add(null);
        return days;
    }

    /// Builds a calendar week view
    /// @param c First day for calendar view
    /// @return Days in the view
    private List<Date> buildWeekDays(Calendar c) {
        List<Date> days = new ArrayList<>();
        Calendar monday = (Calendar) c.clone();

        int dow = monday.get(Calendar.DAY_OF_WEEK);
        // If it is a sunday it is 6 days, otherwise calculation works
        int daysFromMonday = (dow == Calendar.SUNDAY) ? 6 : dow - Calendar.MONDAY;
        monday.add(Calendar.DAY_OF_MONTH, -daysFromMonday);

        int dom = monday.get(Calendar.DAY_OF_MONTH);
        for (int i = 1; i < 6; i++) {
            days.add(monday.getTime());
            monday.set(Calendar.DAY_OF_MONTH, dom + i);
        }
        return days;
    }
}