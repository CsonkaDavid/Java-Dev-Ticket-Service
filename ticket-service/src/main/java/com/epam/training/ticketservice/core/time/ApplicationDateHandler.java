package com.epam.training.ticketservice.core.time;

import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class ApplicationDateHandler {
    private final Calendar calendar = Calendar.getInstance();

    public Date addMinutesToDate(Date date, Integer minutes) {
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }
}
