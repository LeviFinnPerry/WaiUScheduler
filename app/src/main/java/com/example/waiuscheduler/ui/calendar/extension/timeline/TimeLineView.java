package com.example.waiuscheduler.ui.calendar.extension.timeline;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.waiuscheduler.R;
import com.example.waiuscheduler.ui.calendar.extension.CalendarOccurrence;
import com.example.waiuscheduler.ui.calendar.extension.OnEventClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public abstract class TimeLineView {
    // Constant variables
    protected static final int START_HOUR = 8;
    protected static final int END_HOUR = 18;

    // Private variables
    protected final Context context;
    protected final RelativeLayout container;
    protected final OnEventClickListener listener;
    protected int cachedHourHeightPx = 0;

    // Colours
    protected static final int COLOUR_EVENT = 0xFF007968;
    protected static final int COLOUR_STUDY = 0xFF3946AB;
    protected static final int COLOUR_ASSESSMENT = 0xFFE65100;

    /// Base constructor
    /// @param context Application context
    /// @param container Relative layout container
    /// @param listener On event click listener
    protected TimeLineView(Context context,
                           RelativeLayout container,
                           OnEventClickListener listener) {
        this.context = context;
        this.container = container;
        this.listener = listener;
    }

    /// Abstract build method that are implemented by subclasses
    /// @param date day to build
    /// @param events events on that day
    public abstract void build(Date date, List<CalendarOccurrence> events);

    /// Calculates hour slot height
    protected int hourHeightPx() {
        float density = context.getResources().getDisplayMetrics().density;
        int totalHours = END_HOUR - START_HOUR;

        if (cachedHourHeightPx > 0) return cachedHourHeightPx;

        int measured = container.getHeight();
        if (measured > 0) {
            cachedHourHeightPx = measured / totalHours;
            return cachedHourHeightPx;
        }

        // Calculate from screen height
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        int available = (screenHeight - (int) (220 * density));
        return Math.max(available / totalHours, (int) (48 * density));
    }

    /// Clears the cached heights
    public void reset() {
        cachedHourHeightPx = 0;
    }

    /// Separates all day occurrences from timed ones
    /// @param events full list of occurrences
    /// @param allDay Output list for all day items
    /// @param timed Output list for timed items
    protected static void splitEvents(List<CalendarOccurrence> events,
                                      List<CalendarOccurrence> allDay,
                                      List<CalendarOccurrence> timed) {
        if (events == null) return;
        for (CalendarOccurrence c: events) {
            if (isAllDay(c)) allDay.add(c);
            else timed.add(c);
        }
    }

    /// Draws the all day banner strip for chips above the hourly grid
    ///
    /// @param allDayEvents All-day occurrences to render
    /// @param labelWidthPx Left offset for the time label column
    /// @param chipWidthPx  Width of each chip
    /// @return Height in px of the rendered banner area
    protected int drawAllDayBanner(List<CalendarOccurrence> allDayEvents,
                                   int labelWidthPx, int chipWidthPx
    ) {
        if (allDayEvents == null || allDayEvents.isEmpty()) return 0;

        float density = context.getResources().getDisplayMetrics().density;
        int chipHeightPx = (int) (28 * density);
        int chipPaddingPx = (int) (6 * density);
        int bannerHeight = allDayEvents.size() * (chipHeightPx + chipPaddingPx) + chipPaddingPx;

        // Background strip
        drawBackgroundStrip(bannerHeight);

        // One chip per all-day event
        for (int i = 0; i < allDayEvents.size(); i++) {
            int chipTop = chipPaddingPx + i * (chipHeightPx + chipPaddingPx);
            drawEventChip(allDayEvents.get(i), chipTop, chipHeightPx,
                    labelWidthPx + (int) (2 * density), chipWidthPx);
        }

        return bannerHeight;
    }

    /// Draws a divider between each hour
    /// @param topPx start position
    /// @param leftMarginPx left position
    protected void drawDivider(int topPx, int leftMarginPx) {
        float density = context.getResources().getDisplayMetrics().density;
        View divider = new View(context);
        divider.setBackgroundColor(0xFFE0E0E0);
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, (int) (density)
        );
        p.topMargin = topPx;
        p.leftMargin = leftMarginPx;
        container.addView(divider, p);
    }

    /// Draws the background strip for all day items
    /// @param bannerHeight Height to draw strip
    private void drawBackgroundStrip(int bannerHeight) {
        View strip = new View(context);
        strip.setBackgroundColor(0xFFF0F0F0);
        RelativeLayout.LayoutParams stripParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, bannerHeight);
        stripParams.topMargin = 0;
        container.addView(strip, stripParams);
    }

    /// Draws an hour label at each section
    /// @param hour hour of day
    /// @param topPx start position of hour label
    /// @param widthPx width for hour label
    /// @param heightPx height of hour label
    protected void drawHourLabel(int hour, int topPx, int widthPx, int heightPx) {
        float density = context.getResources().getDisplayMetrics().density;
        TextView label = new TextView(context);
        label.setText(formatHour(hour));
        label.setTextSize(11);
        label.setTextColor(0xFF757575);
        label.setPadding((int) (4 * density), (int) (2 * density), (int) (4 * density), 0);

        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(widthPx, heightPx);
        p.topMargin = topPx;
        container.addView(label, p);
    }

    /// Draws current timeline
    /// @param topPx start position
    /// @param leftMarginPx left position
    /// @param widthPx width
    protected void drawCurrentTimeline(int topPx, int leftMarginPx, int widthPx) {
        float density = context.getResources().getDisplayMetrics().density;
        View line = new View(context);
        line.setBackgroundColor(0xFFE53935);
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                widthPx, (int) (2 * density)
        );
        p.topMargin = topPx;
        p.leftMargin = leftMarginPx;
        container.addView(line, p);
    }

    /// Formats colours, positions as event chips in the container
    /// @param occ calendar item
    /// @param topPx top of item
    /// @param heightPx height of item
    /// @param widthPx width of item
    /// @param leftMarginPx left of item
    protected void drawEventChip(CalendarOccurrence occ,
                                 int topPx, int heightPx,
                                 int leftMarginPx, int widthPx) {
        float density = context.getResources().getDisplayMetrics().density;
        SimpleDateFormat timeFmt = new SimpleDateFormat("h:mm a", Locale.getDefault());

        // Inflate event chip
        View chip = LayoutInflater.from(context)
                .inflate(R.layout.timeline_event_chip, container, false);

        String startTime = timeFmt.format(occ.getStartDateTime().getTime());
        String endTime = timeFmt.format(occ.getEndDateTime().getTime());
        String timeRange = startTime + " - " + endTime;
        ((TextView) chip.findViewById(R.id.text_timeline_title)).setText(occ.getTitle());
        ((TextView) chip.findViewById(R.id.text_timeline_time)).setText(
                isAllDay(occ) ? "Due Today": timeRange
        );

        // Colour the background
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(chipColour(occ.getType()));
        bg.setCornerRadius(4 * density);
        chip.setBackground(bg);

        chip.setOnClickListener(v -> {
            if (listener != null) listener.onEventClick(occ);
        });

        // Get event parameters
        RelativeLayout.LayoutParams chipParams = new RelativeLayout.LayoutParams(
                widthPx, heightPx);
        chipParams.topMargin = topPx;
        chipParams.leftMargin = leftMarginPx;
        container.addView(chip, chipParams);
    }

    /// Sets the total height of the container
    /// @param totalHeightPx total height of container
    protected void setContainerHeight(int totalHeightPx) {
        ViewGroup.LayoutParams p = container.getLayoutParams();
        if (p == null) {
            p = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, totalHeightPx
            );
        } else {
            p.height = totalHeightPx;
        }
        container.setLayoutParams(p);
    }

    /// Formats hour into string format
    /// @param hour Hour of day
    /// @return Time formatted string
    protected String formatHour(int hour) {
        if (hour == 12) return "12 PM";
        if (hour > 12) return (hour - 12) + " PM";
        return hour + " AM";
    }

    /// Determines chip colour based on type of occurrence
    /// @param type Type of occurrence
    /// @return Colour for occurrence
    protected int chipColour(String type) {
        switch (type) {
            case CalendarOccurrence.TYPE_STUDY: return COLOUR_STUDY;
            case CalendarOccurrence.TYPE_ASSESSMENT: return COLOUR_ASSESSMENT;
            case CalendarOccurrence.TYPE_EVENT: return COLOUR_EVENT;
            default: return 0xFF757575;
        }
    }

    /// Determines if two calendar objects are set to the same day
    /// @param a First calendar object
    /// @param b Second calendar object
    /// @return True if days are same, else false
    protected boolean isSameDay(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
                a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }

    /// Finds the hour of the event rounded
    /// @param hour hour of day
    /// @param isStart whether is start time
    protected float roundHour(float hour, boolean isStart) {
        if (isStart) {
            return Math.max(hour, START_HOUR);
        } else {
            return Math.min(hour, END_HOUR);
        }
    }

    /// Converts a calendars time to a fractional hour value
    /// @param cal calendar item
    protected float toFractionalHour(Calendar cal) {
        return cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) / 60F;
    }

    /// Determines if the occurrence is an all day occurrence
    /// @param occ calendar item
    /// @return Whether the calendar occurrence is an assessment
    protected static boolean isAllDay(CalendarOccurrence occ) {
        return CalendarOccurrence.TYPE_ASSESSMENT.equals(occ.getType());
    }
}
