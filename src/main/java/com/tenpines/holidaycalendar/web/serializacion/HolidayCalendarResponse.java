package com.tenpines.holidaycalendar.web.serializacion;

import com.tenpines.holidaycalendar.dominio.*;

// FIXME: Borrar

public record HolidayCalendarResponse(long id, String nombre) {
     public static HolidayCalendarResponse from(HolidayCalendar calendario) {
        return new HolidayCalendarResponse(
                calendario.getId(),
                calendario.getName()
        );
    }
}
