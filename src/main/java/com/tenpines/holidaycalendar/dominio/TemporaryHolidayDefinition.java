package com.tenpines.holidaycalendar.dominio;

import java.time.LocalDate;

public class TemporaryHolidayDefinition extends HolidayDefinition {
    private HolidayDefinition holidayDefinition;

    private DateInterval interval;

    public TemporaryHolidayDefinition(HolidayDefinition aHolidayDefinition, DateInterval anInterval) {
        holidayDefinition = aHolidayDefinition;
        interval = anInterval;
    }

    protected TemporaryHolidayDefinition() {}

    public Boolean isHoliday(LocalDate aDate) {
        return interval.contains(aDate) && holidayDefinition.isHoliday(aDate);
    }

    public HolidayDefinition getHolidayDefinition() {
        return holidayDefinition;
    }

    public DateInterval getInterval() {
        return interval;
    }
}
