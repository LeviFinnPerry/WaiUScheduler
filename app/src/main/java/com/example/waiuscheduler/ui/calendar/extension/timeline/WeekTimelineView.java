package com.example.waiuscheduler.ui.calendar.extension.timeline;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.waiuscheduler.ui.calendar.extension.CalendarOccurrence;
import com.example.waiuscheduler.ui.calendar.extension.OnEventClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeekTimelineView extends TimeLineView {

    /// Constructor for the week timeline view
    /// @param context Application context
    /// @param container Relative layout
    /// @param listener Event click listener
    public WeekTimelineView(Context context,
                            RelativeLayout container,
                            OnEventClickListener listener) {
        super(context, container, listener);
    }

    /// Builds the full timeline for the given date and events
    @Override
    public void build(Date date, List<CalendarOccurrence> events) {
        // Build a 5 day list starting from monday
        Calendar monday = Calendar.getInstance();
        int dom = monday.get(Calendar.DAY_OF_MONTH);
        monday.setTime(date);
        List<Date> weekDays = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            weekDays.add(monday.getTime());
            monday.add(Calendar.DAY_OF_MONTH, dom + i);
        }
        build(weekDays, events);
    }

    /// Builds a 5 column timeline for the given week
    /// @param weekDays 5 days
    /// @param events calendar occurrences for the week
    public void build(List<Date> weekDays, List<CalendarOccurrence> events) {
        container.removeAllViews();

        if (weekDays == null || weekDays.size() != 5) return;

        float density = context.getResources().getDisplayMetrics().density;
        int totalHours = END_HOUR - START_HOUR;
        int hourHeightPx = hourHeightPx();
        int labelWidthPx = (int) (52 * density);
        int headerHeightPx = (int) (40 * density);

        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int dayColumnWidth = (screenWidth - labelWidthPx) / 5;

        Calendar today = Calendar.getInstance();
        SimpleDateFormat dayFmt = new SimpleDateFormat("EEE\nd", Locale.getDefault());

        // Day header row
        for (int col = 0; col < 5; col++) {
            Date dayDate = weekDays.get(col);
            if (dayDate == null) continue;

            Calendar dayCal = Calendar.getInstance();
            dayCal.setTime(dayDate);

            TextView header = new TextView(context);
            header.setText(dayFmt.format(dayDate));
            header.setTextSize(11);
            header.setGravity(Gravity.CENTER);
            header.setBackgroundColor(0xFFF5F5F5);

            boolean isToday = isSameDay(dayCal, today);
            header.setTextColor(isToday ? 0xFF1565C0 : 0xFF212121);
            header.setTypeface(null, isToday ? Typeface.BOLD : Typeface.NORMAL);

            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                    dayColumnWidth, headerHeightPx);
            p.leftMargin = labelWidthPx + col * dayColumnWidth;
            p.topMargin  = 0;
            container.addView(header, p);
        }

        // Hour labels
        for (int h = 0; h < totalHours; h++) {
            int topPx = headerHeightPx + h * hourHeightPx;
            drawHourLabel(START_HOUR + h, topPx, labelWidthPx, hourHeightPx);
            drawDivider(topPx, labelWidthPx);
        }

        // Vertical divider between days
        for (int col = 1; col < 5; col++) {
            View colDivider = new View(context);
            colDivider.setBackgroundColor(0xFFE0E0E0);
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                    (int) (1 * density),
                    headerHeightPx + totalHours * hourHeightPx);
            p.topMargin  = 0;
            p.leftMargin = labelWidthPx + col * dayColumnWidth;
            container.addView(colDivider, p);
        }
        setContainerHeight(headerHeightPx + (headerHeightPx * totalHours));

        // Current time indicator
        Calendar now = Calendar.getInstance();
        float nowHour = toFractionalHour(now);

        if (nowHour >= START_HOUR && nowHour < END_HOUR) {
            for (int col = 0; col < 5; col++) {
                Date dayDate = weekDays.get(col);
                if (dayDate == null) continue;
                Calendar dayCal = Calendar.getInstance();
                dayCal.setTime(dayDate);
                if (isSameDay(dayCal, now)) {
                    int topPx = headerHeightPx
                            + (int) ((nowHour - START_HOUR) * hourHeightPx);
                    drawCurrentTimeline(
                            topPx,
                            labelWidthPx + col * dayColumnWidth,
                            dayColumnWidth
                    );
                    break;
                }
            }
        }

        // ── Event chips ───────────────────────────────────────────────────────
        if (events == null || events.isEmpty()) return;

        for (CalendarOccurrence occ : events) {
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(occ.getStartDateTime());

            // Find which column this event belongs to
            int col = -1;
            for (int i = 0; i < 5; i++) {
                Date dayDate = weekDays.get(i);
                if (dayDate == null) continue;
                Calendar dayCal = Calendar.getInstance();
                dayCal.setTime(dayDate);
                if (isSameDay(startCal, dayCal)) {
                    col = i;
                    break;
                }
            }
            if (col == -1) continue;

            Calendar endCal = Calendar.getInstance();
            endCal.setTime(occ.getEndDateTime());

            float startHour = roundHour(toFractionalHour(startCal), true);
            float endHour = roundHour(toFractionalHour(endCal), false);
            if (startHour >= endHour) continue;

            float density2 = context.getResources().getDisplayMetrics().density;
            int topPx = headerHeightPx
                    + (int) ((startHour - START_HOUR) * hourHeightPx);
            int heightPx = Math.max(
                    (int) ((endHour - startHour) * hourHeightPx),
                    (int) (28 * density2)
            );

            int chipLeft = labelWidthPx + col * dayColumnWidth + (int) (1 * density2);
            int chipWidth = dayColumnWidth - (int) (2 * density2);

            drawEventChip(occ, topPx, heightPx, chipLeft, chipWidth);
        }
    }
}
