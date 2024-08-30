package com.tenpines.holidaycalendar.dominio;

import java.time.LocalDate;

public abstract class HolidayDefinition {
    private Long id;

    public abstract Boolean isHoliday(LocalDate aDate);

    public Long getId() {
        return id;
    }
}
