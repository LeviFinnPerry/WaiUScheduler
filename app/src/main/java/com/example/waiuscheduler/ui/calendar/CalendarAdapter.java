package com.example.waiuscheduler.ui.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.waiuscheduler.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarAdapter extends BaseAdapter {

    private final Context context;
    private List<Date> days;
    private List<CalendarOccurrence> events;
    private List<String> filters;

    public CalendarAdapter(Context context) {
        this.context = context;
    }

    public void update(List<Date> days, List<CalendarOccurrence> events, List<String> filters) {
        this.days = days != null ? days : new ArrayList<>();            // Set days or initialise
        this.events = events != null ? events : new ArrayList<>();      // Set events or initialise
        this.filters = filters != null ? filters : new ArrayList<>();   // Set days or initialise
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return days.size();
    }

    @Override
    public Object getItem(int position) {
        return days.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if ( convertView == null) {     // If view not initialised
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.fragment_calendar, parent, false);
        }

        TextView dayNumber = convertView.findViewById(R.id.day_labels);
        TextView chipText = convertView.findViewById(R.id.gridView_calendar);

        Date date = days.get(position);

        // If no dates found in the calendar
        if (date == null) {
            // Pad cell
            dayNumber.setText("");
            chipText.setText("");
            convertView.setAlpha(0f);
            return convertView;
        }

        convertView.setAlpha(1f);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        dayNumber.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));

        // Filtered events


        return convertView;
    }
}
