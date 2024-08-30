package com.tenpines.holidaycalendar.dominio;

import java.time.LocalDate;

public class SpecificDateHolidayDefinition extends HolidayDefinition {
    private LocalDate date;

    public SpecificDateHolidayDefinition(LocalDate aDate) {
        date = aDate;
    }

    protected SpecificDateHolidayDefinition() {}

    @Override
    public Boolean isHoliday(LocalDate aDate) {
        return aDate.equals(date);
    }

    public LocalDate getDate() {
        return date;
    }
}
