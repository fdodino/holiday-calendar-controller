package com.tenpines.holidaycalendar.dominio;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DayOfWeekHolidayDefinition extends HolidayDefinition {
    private DayOfWeek dayOfWeek;

    public DayOfWeekHolidayDefinition(DayOfWeek aDayOfWeeek) {
        dayOfWeek = aDayOfWeeek;
    }

    protected DayOfWeekHolidayDefinition() {}

    @Override
    public Boolean isHoliday(LocalDate aDate) {
        return aDate.getDayOfWeek().equals(dayOfWeek);
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }
}
