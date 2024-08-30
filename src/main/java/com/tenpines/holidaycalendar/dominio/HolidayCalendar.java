package com.tenpines.holidaycalendar.dominio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HolidayCalendar {
    private Long id;
    private String name;
    private List<HolidayDefinition> holidayDefinitions;

    private static Long lastId = 1L;

    public HolidayCalendar(String name, List<HolidayDefinition> holidayDefinitions) {
        this.id = lastId++;
        this.name = name;
        this.holidayDefinitions = copiaMutableDe(holidayDefinitions);
    }

    protected HolidayCalendar() {}

    public Boolean isHoliday(LocalDate aDate) {
        return holidayDefinitions.stream()
                .anyMatch(holidayDefinition -> holidayDefinition.isHoliday(aDate));
    }

    public void addHolidayDefinition(HolidayDefinition aHolidayDefinition) {
        holidayDefinitions.add(aHolidayDefinition);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<HolidayDefinition> getHolidayDefinitions() {
        return copiaMutableDe(holidayDefinitions);
    }

    public void setName(String newName) {
        name = newName;
    }

    public void setHolidayDefinitions(List<HolidayDefinition> newHolidayDefinitions) {
        holidayDefinitions = copiaMutableDe(newHolidayDefinitions);
    }

    private List<HolidayDefinition> copiaMutableDe(List<HolidayDefinition> newHolidayDefinitions) {
        return new ArrayList<>(newHolidayDefinitions);
    }
}
