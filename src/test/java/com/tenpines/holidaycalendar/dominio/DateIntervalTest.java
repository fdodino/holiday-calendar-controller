package com.tenpines.holidaycalendar.dominio;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DateIntervalTest {
    @Test
    public void startDateMustBeBeforeEndDate() {
        var startDate = LocalDate.of(2019, 1, 1);
        var endDate = LocalDate.of(2018, 1, 1);

        var error = assertThrows(RuntimeException.class, () -> {
            new DateInterval(startDate, endDate);
        });
        assertEquals(DateInterval.ERROR_DESCRIPION_INVALID_INTERVAL, error.getMessage());
    }

    @Test
    public void intervalContainsItsEndpoints() {
        var startDate = LocalDate.of(2018, 1, 1);
        var endDate = LocalDate.of(2019, 1, 1);
        var interval = new DateInterval(startDate, endDate);

        assertTrue(interval.contains(startDate));
        assertTrue(interval.contains(endDate));
    }

    @Test
    public void intervalContainsADateBetweenItsEndpoints() {
        var startDate = LocalDate.of(2018, 1, 1);
        var endDate = LocalDate.of(2019, 1, 1);
        var interval = new DateInterval(startDate, endDate);

        assertTrue(interval.contains(startDate.plusDays(1)));
        assertTrue(interval.contains(endDate.minusDays(1)));
    }

    @Test
    public void intervalDoesNotContainADateBeforeOrAfterItsEndpoints() {
        LocalDate startDate = LocalDate.of(2018, 1, 1);
        LocalDate endDate = LocalDate.of(2019, 1, 1);
        DateInterval interval = new DateInterval(startDate, endDate);

        assertFalse(interval.contains(startDate.minusDays(1)));
        assertFalse(interval.contains(endDate.plusDays(1)));
    }

    @Test
    public void anIntervalOfOneDateIsValid() {
        LocalDate onlyDate = LocalDate.of(2018, 1, 1);
        DateInterval interval = new DateInterval(onlyDate, onlyDate);

        assertFalse(interval.contains(onlyDate.minusDays(1)));
        assertTrue(interval.contains(onlyDate));
        assertFalse(interval.contains(onlyDate.plusDays(1)));
    }

    @Test
    public void canObtainTheDatesInsideTheInterval() {
        LocalDate startDate = LocalDate.of(2020, 12, 30);
        LocalDate endDate = LocalDate.of(2021, 1, 3);
        DateInterval interval = new DateInterval(startDate, endDate);

        assertEquals(
                List.of(
                        LocalDate.of(2020, 12, 30),
                        LocalDate.of(2020, 12, 31),
                        LocalDate.of(2021, 1, 1),
                        LocalDate.of(2021, 1, 2),
                        LocalDate.of(2021, 1, 3)
                ),
                interval.containedDates()
        );
    }
}
