package com.tenpines.holidaycalendar.web;

import com.tenpines.holidaycalendar.dominio.HolidayCalendar;
import com.tenpines.holidaycalendar.web.serializacion.HolidayCalendarRequest;
import com.tenpines.holidaycalendar.web.serializacion.HolidayCalendarResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// FIXME: Borrar

@RestController
public class HolidayCalendarController {
    private List<HolidayCalendar> calendarios;

    public HolidayCalendarController() {
        this.reset();
    }

    public void reset() {
        calendarios = new ArrayList<>();
    }

    @GetMapping("/calendarios")
    public List<HolidayCalendarResponse> obtenerCalendarios(@RequestParam(defaultValue = "") String nombre) {
        return calendarios.stream().filter(calendario -> calendario.getName().contains(nombre))
                .map(HolidayCalendarResponse::from)
                .toList();
    }

    @PostMapping("/calendarios")
    @ResponseStatus(HttpStatus.CREATED)
    public HolidayCalendarResponse crearCalendario(@RequestBody HolidayCalendarRequest request) {
        var nombre = request.nombre();
        if (nombre == null) {
            throw new BusinessException("El calendario debe tener nombre");
        }


        var calendarioNuevo = new HolidayCalendar(nombre, Collections.emptyList());
        calendarios.add(calendarioNuevo);

        return HolidayCalendarResponse.from(calendarioNuevo);
    }

    @GetMapping("/calendarios/{id}")
    public HolidayCalendarResponse obtenerCalendario(@PathVariable long id) {
        return calendarios
                .stream()
                .filter(unCalendario -> unCalendario.getId() == id)
                .findFirst()
                .map(HolidayCalendarResponse::from)
                .orElseThrow();
    }

}
