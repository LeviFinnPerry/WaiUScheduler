package com.example.waiuscheduler.ui.calendar;

import java.util.Date;
import java.util.List;

public interface OnDayClickListener {
    /// On Click handling for a calendar day
    /// @param date date selected
    /// @param eventsOnDay List of calendar events
    void onDayClick(Date date, List<CalendarOccurrence> eventsOnDay);
}
