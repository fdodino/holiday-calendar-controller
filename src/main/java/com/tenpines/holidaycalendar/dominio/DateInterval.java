package com.tenpines.holidaycalendar.dominio;

import java.time.LocalDate;
import java.util.List;

public class DateInterval {
    public static final String ERROR_DESCRIPION_INVALID_INTERVAL = "Invalid Interval";

    private LocalDate startDate;
    private LocalDate endDate;

    public DateInterval(LocalDate aStartDate, LocalDate anEndDate) {
        assertStartAndEndDatesAreValid(aStartDate, anEndDate);

        startDate = aStartDate;
        endDate = anEndDate;
    }

    protected DateInterval() {}

    private void assertStartAndEndDatesAreValid(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new RuntimeException(ERROR_DESCRIPION_INVALID_INTERVAL);
        }
    }

    public boolean contains(LocalDate aDate) {
        return !aDate.isBefore(startDate) && !aDate.isAfter(endDate);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public List<LocalDate> containedDates() {
        return startDate.datesUntil(endDate.plusDays(1)).toList();
    }
}
