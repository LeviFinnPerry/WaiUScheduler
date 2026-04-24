package com.example.waiuscheduler.ui.calendar;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.waiuscheduler.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalendarAdapter extends BaseAdapter {

    private final Context context;
    private List<Date> days = new ArrayList<>();
    private List<CalendarOccurrence> events = new ArrayList<>();
    private Set<String> filters  = new HashSet<>();
    private OnDayClickListener listener;
    private final Calendar today = Calendar.getInstance();

    /// Constructor for calendar adapter
    /// @param context Application context
    public CalendarAdapter(Context context) {
        this.context = context;
    }

    /// Sets the listener interface for day selection
    /// @param l Day Click Listener
    public void setOnDayClickListener(OnDayClickListener l) { this.listener = l; }

    /// Updates the calendar items
    /// @param days Days in view
    /// @param events Calendar Occurrences
    /// @param filters Filtering types of occurrences
    public void update(List<Date> days, List<CalendarOccurrence> events, Set<String> filters) {
        this.days = days != null ? days : new ArrayList<>();            // Set days or initialise
        this.events = events != null ? events : new ArrayList<>();      // Set events or initialise
        this.filters = filters != null ? filters : HashSet.newHashSet(0);   // Set days or initialise
        notifyDataSetChanged();
    }

    /// Amount of days in view
    /// @return size of days
    @Override
    public int getCount() { return days != null ? days.size(): 0; }

    /// Finds the day selected
    /// @param position day index
    /// @return day in view
    @Override
    public Object getItem(int position) { return days.get(position); }

    /// Finds calendar occurrence
    /// @param position calendar occurrence
    /// @return position of the occurrence
    @Override
    public long getItemId(int position) { return position; }

    /// Initialises the calendar view for the fragment
    /// @param position location of the calendar
    /// @param convertView calendar view
    /// @param parent parent of calendar view
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if ( convertView == null) {     // If view not initialised
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.calendar_cell, parent, false);
        }

        TextView dayNumber = convertView.findViewById(R.id.text_day_number);
        TextView textOverflow = convertView.findViewById(R.id.text_overflow);
        TextView chip1 = convertView.findViewById(R.id.chip_event_1);
        TextView chip2 = convertView.findViewById(R.id.chip_event_2);
        TextView chip3 = convertView.findViewById(R.id.chip_event_3);

        Date date = days.get(position);

        // If no dates found in the calendar
        if (date == null) {
            // Pad cell
            dayNumber.setText("");
            textOverflow.setVisibility(View.GONE);
            chip1.setVisibility(View.GONE);
            chip2.setVisibility(View.GONE);
            chip3.setVisibility(View.GONE);
            convertView.setAlpha(0f);
            convertView.setOnClickListener(null);   // Clear listener
            return convertView;
        }

        convertView.setAlpha(1f);
        Calendar cellCal = Calendar.getInstance();
        cellCal.setTime(date);

        // Day number
        dayNumber.setText(String.valueOf(cellCal.get(Calendar.DAY_OF_MONTH)));

        // Highlight today
        boolean isToday = isSameDay(cellCal, today);
        convertView.setActivated(isToday);
        dayNumber.setTypeface(null, isToday ? Typeface.BOLD : Typeface.NORMAL);

        // Filtered events
        List<CalendarOccurrence> dayEvents = new ArrayList<>();
        for (CalendarOccurrence occ: events) {
            if (!filters.contains(occ.getType())) continue; // If filter doesn't match
            Calendar occCal = Calendar.getInstance();
            occCal.setTime(occ.getStartDateTime());
            if (isSameDay(occCal, cellCal)) dayEvents.add(occ); // If occurrence matches view
        }

        // Draw up to 3 colour chips
        TextView[] chips = { chip1, chip2, chip3 };
        for (int i = 0; i < chips.length; i++ ) {
            if (i < dayEvents.size()) {
                chips[i].setVisibility(View.VISIBLE);
                chips[i].setBackgroundColor(dayEvents.get(i).getColour());
            } else {
                chips[i].setVisibility(View.GONE);
            }
        }

        // Overflow label
        int overflow = dayEvents.size() - 3; // When more than 3 events in one day
        if (overflow > 0) {
            textOverflow.setVisibility(View.VISIBLE);
            textOverflow.setText("+" + overflow);
        } else {
            textOverflow.setVisibility(View.GONE);
        }

        // Notify event listener
        final List<CalendarOccurrence> finalEvents = dayEvents;
        final Date finalDate = date;
        convertView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDayClick(finalDate, finalEvents);
            }
        });

        // Return to fragment
        return convertView;
    }

    /// Method to determine if two days are the same
    /// @param a First day
    /// @param b Second day
    private static boolean isSameDay(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
                a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }
}
