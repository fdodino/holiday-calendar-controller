package com.tenpines.holidaycalendar.dominio;

import java.time.LocalDate;
import java.time.MonthDay;

public class DayOfMonthHolidayDefinition extends HolidayDefinition {
    private MonthDay monthDay;

    public DayOfMonthHolidayDefinition(MonthDay aMonthDay) {
        monthDay = aMonthDay;
    }

    protected DayOfMonthHolidayDefinition() {}

    @Override
    public Boolean isHoliday(LocalDate aDate) {
        return MonthDay.from(aDate).equals(monthDay);
    }

    public MonthDay getMonthDay() {
        return monthDay;
    }
}
