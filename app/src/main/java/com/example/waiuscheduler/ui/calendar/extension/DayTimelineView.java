package com.example.waiuscheduler.ui.calendar.extension;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.waiuscheduler.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DayTimelineView {
    // Constant variables
    private static final int START_HOUR = 8;
    private static final int END_HOUR = 18;
    private static final int HOUR_HEIGHT_DP = 60;

    // Private variables
    private final Context context;
    private final RelativeLayout container;
    private final OnEventClickListener listener;

    /// Day Timeline View Constructor
    /// @param context Application context
    /// @param container Relative layout container
    /// @param listener On event click listener
    public DayTimelineView(Context context, RelativeLayout container, OnEventClickListener listener) {
        this.context = context;
        this.container = container;
        this.listener = listener;
    }

    /// Builds the full timeline for the given date and events
    public void build(Date date, List<CalendarOccurrence> events) {
        container.removeAllViews();

        float density = context.getResources().getDisplayMetrics().density;
        int hourHeightPx = (int) (HOUR_HEIGHT_DP * density);
        int totalHours = END_HOUR - START_HOUR;

        // Draw hour slot background and labels
        for (int h = 0; h < totalHours; h++) {
            int hour = START_HOUR + h;

            // Hour label
            TextView label = new TextView(context);
            label.setText(formatHour(hour));
            label.setTextSize(11);
            label.setTextColor(0xFF757575);
            label.setPadding(8, 4, 8, 0);

            RelativeLayout.LayoutParams labelParams = new RelativeLayout.LayoutParams(
                    (int) (48 * density), hourHeightPx);
            labelParams.topMargin = h * hourHeightPx;
            container.addView(label, labelParams);

            // Hour divider lines
            View divider = new View(context);
            divider.setBackgroundColor(0xFFE0E0E0);

            RelativeLayout.LayoutParams divParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, (int) (1 * density));
            divParams.topMargin = h * hourHeightPx;
            divParams.leftMargin = (int) (48 * density);
            container.addView(divider, divParams);
        }

        // Set total container height
        FrameLayout.LayoutParams containerParams =
                (FrameLayout.LayoutParams) container.getLayoutParams();
        if (containerParams == null) {
            containerParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, hourHeightPx * totalHours);
        } else {
            containerParams.height = hourHeightPx * totalHours;
        }
        container.setLayoutParams(containerParams);


        // Current time
        Calendar now = Calendar.getInstance();
        Calendar selectCal = Calendar.getInstance();
        selectCal.setTime(date);
        boolean isToday = isSameDay(now, selectCal);

        if (isToday) {
            int currentHour = now.get(Calendar.HOUR_OF_DAY);
            int currentMinute = now.get(Calendar.MINUTE);
            if (currentHour >= START_HOUR && currentHour < END_HOUR) {
                float offset = (currentHour - START_HOUR) + (currentMinute / 60f);
                int topPx = (int) (offset * hourHeightPx);

                View timeline = new View(context);
                timeline.setBackgroundColor(0xFFE53935);


                RelativeLayout.LayoutParams timeParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, (int) (2 * density));
                timeParams.topMargin = topPx;
                timeParams.leftMargin = (int) (48 * density);
                container.addView(timeline, timeParams);
            }
        }

        // Draw events
        if (events == null) return;
        SimpleDateFormat timeFmt = new SimpleDateFormat("h:mm a", Locale.getDefault());

        for (CalendarOccurrence occ: events) {
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(occ.getStartDateTime());
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(occ.getEndDateTime());

            // Only show events in this day
            if (!isSameDay(startCal, endCal)) continue;

            float startHour = startCal.get(Calendar.HOUR_OF_DAY) +
                    startCal.get(Calendar.MINUTE) / 60f;
            float endHour = endCal.get(Calendar.HOUR_OF_DAY) +
                    endCal.get(Calendar.MINUTE) / 60f;

            // Stick to visible window
            startHour = Math.max(startHour, START_HOUR);
            endHour = Math.min(endHour, END_HOUR);

            if (startHour >= endHour) continue;

            float topOffset = startHour - START_HOUR;
            float durationHours = endHour - startHour;

            int topPx = (int) (topOffset * hourHeightPx);
            int heightPx = Math.max((int) (durationHours * hourHeightPx), (int) (28 * density));

            // Inflate event chip
            String startTime = timeFmt.format(occ.getStartDateTime().getTime());
            String endTime = timeFmt.format(occ.getEndDateTime().getTime());
            String timeRange = startTime + " - " + endTime;
            View chip = LayoutInflater.from(context)
                    .inflate(R.layout.timeline_event_chip, container, false);
            ((TextView) chip.findViewById(R.id.text_timeline_title)).setText(occ.getTitle());
            ((TextView) chip.findViewById(R.id.text_timeline_time)).setText(timeRange);

            // Colour the background
            GradientDrawable bg = new GradientDrawable();
            bg.setColor(occ.getColour());
            bg.setCornerRadius(6 * density);
            chip.setBackground(bg);

            chip.setOnClickListener(v -> {
                if (listener != null) listener.onEventClick(occ);
            });

            // Get event parameters
            RelativeLayout.LayoutParams chipParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, heightPx);
            chipParams.topMargin = topPx;
            chipParams.leftMargin = (int) (52 * density);
            chipParams.rightMargin = (int) (4 * density);
            container.addView(chip, chipParams);

        }
    }

    /// Formats hour into string format
    /// @param hour Hour of day
    /// @return Time formatted string
    private String formatHour(int hour) {
        if (hour == 12) return "12 PM";
        if (hour > 12) return (hour - 12) + " PM";
        return hour + " AM";
    }

    /// Determines if two calendar objects are set to the same day
    /// @param a First calendar object
    /// @param b Second calendar object
    /// @return True if days are same, else false
    private boolean isSameDay(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
                a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }
}
