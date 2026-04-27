package com.example.waiuscheduler.ui.calendar.extension.timeline;

import android.content.Context;
import android.widget.RelativeLayout;

import com.example.waiuscheduler.ui.calendar.extension.CalendarOccurrence;
import com.example.waiuscheduler.ui.calendar.extension.OnEventClickListener;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DayTimelineView extends TimeLineView {

    /// Day Timeline View Constructor
    /// @param context Application context
    /// @param container Relative layout container
    /// @param listener On event click listener
    public DayTimelineView(Context context, RelativeLayout container, OnEventClickListener listener) {
        super(context, container, listener);
    }

    /// Builds the full timeline for the given date and events
    @Override
    public void build(Date date, List<CalendarOccurrence> events) {
        container.removeAllViews();

        float density = context.getResources().getDisplayMetrics().density;
        int totalHours = END_HOUR - START_HOUR;
        int hourHeightPx = hourHeightPx();
        int labelWidthPx = (int) (52 * density);

        // Draw hour slot background and labels
        for (int h = 0; h < totalHours; h++) {
            int topPx = h * hourHeightPx;
            drawHourLabel(START_HOUR + h, topPx, labelWidthPx, hourHeightPx);
        }

        // Set total container height
        setContainerHeight(hourHeightPx * totalHours);

        // Current time
        Calendar now = Calendar.getInstance();
        Calendar selectCal = Calendar.getInstance();
        selectCal.setTime(date);

        if (isSameDay(now, selectCal)) {
            float numHour = toFractionalHour(now);
            if (numHour >= START_HOUR && numHour < END_HOUR) {
                int topPx = (int) ((numHour - START_HOUR) * hourHeightPx);
                drawCurrentTimeline(topPx, labelWidthPx, RelativeLayout.LayoutParams.MATCH_PARENT);
            }
        }

        // Draw events
        if (events == null || events.isEmpty()) return;

        int chipWidth = context.getResources().getDisplayMetrics().widthPixels
                - labelWidthPx - (int) (4 * density);

        for (CalendarOccurrence occ: events) {
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(occ.getStartDateTime());
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(occ.getEndDateTime());

            // Only show events in this day
            if (!isSameDay(startCal, endCal)) continue;

            float startHour = roundHour(toFractionalHour(startCal), true);
            float endHour = roundHour(toFractionalHour(endCal), false);
            if (startHour >= endHour) continue;

            float topOffset = startHour - START_HOUR;
            float durationHours = endHour - startHour;

            int topPx = (int) (topOffset * hourHeightPx);
            int heightPx = Math.max((int) (durationHours * hourHeightPx), (int) (32 * density));

            // Inflate event chip
            drawEventChip(occ, topPx, heightPx,
                    labelWidthPx + (int) (2 * density), chipWidth);
        }
    }
}
