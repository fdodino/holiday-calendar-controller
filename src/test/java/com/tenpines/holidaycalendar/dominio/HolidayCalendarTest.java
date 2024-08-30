package com.tenpines.holidaycalendar.dominio;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.ArrayList;

import static java.time.DayOfWeek.*;
import static java.time.Month.MAY;
import static org.junit.jupiter.api.Assertions.*;

public class HolidayCalendarTest {

    private final HolidayCalendar aHolidayCalendar = new HolidayCalendar("Mi calendario", new ArrayList<>());

    @Test
    void anyNonMarkedDateIsNotAHoliday() {
        var aMonday = LocalDate.of(2021, 5, 31);

        assertFalse(aHolidayCalendar.isHoliday(aMonday));
    }

    @Test
    void canMarkAnyWeekdayAsHoliday() {
        var aSunday = LocalDate.of(2021, 5, 30);
        aHolidayCalendar.addHolidayDefinition(new DayOfWeekHolidayDefinition(SUNDAY));

        assertTrue(aHolidayCalendar.isHoliday(aSunday));
    }

    @Test
    void canMarkMoreThanOneWeekdayAsHoliday() {
        var aSaturday = LocalDate.of(2021, 5, 29);
        var aSunday = LocalDate.of(2021, 5, 30);
        aHolidayCalendar.addHolidayDefinition(new DayOfWeekHolidayDefinition(SATURDAY));
        aHolidayCalendar.addHolidayDefinition(new DayOfWeekHolidayDefinition(SUNDAY));

        assertTrue(aHolidayCalendar.isHoliday(aSaturday));
        assertTrue(aHolidayCalendar.isHoliday(aSunday));
    }

    @Test
    void canMarkAnyDayOfMonthAsHoliday() {
        var aChristmas = LocalDate.of(2021, 12, 25);
        aHolidayCalendar.addHolidayDefinition(new DayOfMonthHolidayDefinition(MonthDay.from(aChristmas)));

        assertTrue(aHolidayCalendar.isHoliday(aChristmas));
    }

    @Test
    void canMarkMoreThanOneMonthDayAsHoliday() {
        var aChristmas = LocalDate.of(2021, 12, 25);
        var aNewyearsDay = LocalDate.of(2021, 1, 1);
        aHolidayCalendar.addHolidayDefinition(new DayOfMonthHolidayDefinition(MonthDay.from(aChristmas)));
        aHolidayCalendar.addHolidayDefinition(new DayOfMonthHolidayDefinition(MonthDay.from(aNewyearsDay)));

        assertTrue(aHolidayCalendar.isHoliday(aChristmas));
        assertTrue(aHolidayCalendar.isHoliday(aNewyearsDay));
    }

    @Test
    void canMarkAnySpecificDateAsHoliday() {
        var anAnniversary = LocalDate.of(2021, 12, 7);
        aHolidayCalendar.addHolidayDefinition(new SpecificDateHolidayDefinition(anAnniversary));

        assertTrue(aHolidayCalendar.isHoliday(anAnniversary));
    }

    @Test
    void canMarkMoreThanOneSpecificDateAsHoliday() {
        var anAnniversary = LocalDate.of(2021, 12, 7);
        var anotherAnniversary = LocalDate.of(2021, 9, 20);
        aHolidayCalendar.addHolidayDefinition(new SpecificDateHolidayDefinition(anAnniversary));
        aHolidayCalendar.addHolidayDefinition(new SpecificDateHolidayDefinition(anotherAnniversary));

        assertTrue(aHolidayCalendar.isHoliday(anAnniversary));
        assertTrue(aHolidayCalendar.isHoliday(anotherAnniversary));
    }

    @Test
    void aHolidayRuleMatchingDateCanHoldDuringAnIntervalContaingDate() {
        var holidayRule = new DayOfWeekHolidayDefinition(MONDAY);
        var aMondayInInterval = LocalDate.of(2021, 1, 18);
        var start = LocalDate.of(2021, 1, 1);
        var end = LocalDate.of(2021, 12, 31);
        var interval = new DateInterval(start, end);
        aHolidayCalendar.addHolidayDefinition(new TemporaryHolidayDefinition(holidayRule, interval));

        assertTrue(aHolidayCalendar.isHoliday(aMondayInInterval));
    }

    @Test
    void canMarkTemporaryHolidays() {
        var holidayRule = new DayOfMonthHolidayDefinition(MonthDay.of(MAY, 2));
        var aMay2ndInInterval = LocalDate.of(2021, MAY, 2);
        var aMay1stInInterval = LocalDate.of(2021, MAY, 1);
        var aMay2ndOutsideInterval = LocalDate.of(2022, MAY, 2);
        var interval = new DateInterval(
                LocalDate.of(2021, 1, 1),
                LocalDate.of(2021, 12, 31)
        );
        aHolidayCalendar.addHolidayDefinition(new TemporaryHolidayDefinition(holidayRule, interval));

        assertTrue(aHolidayCalendar.isHoliday(aMay2ndInInterval));
        assertFalse(aHolidayCalendar.isHoliday(aMay1stInInterval));
        assertFalse(aHolidayCalendar.isHoliday(aMay2ndOutsideInterval));
    }
}
