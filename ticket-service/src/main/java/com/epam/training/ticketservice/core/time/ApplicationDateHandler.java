package com.epam.training.ticketservice.core.time;

import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Component
public class ApplicationDateHandler {
    private final Calendar calendar = Calendar.getInstance();

    public Date addMinutesToDate(Date date, Integer minutes) {
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    public Date addMinutesToDate(Optional<Date> date, Integer minutes) {
        return date.isPresent()
                ? addMinutesToDate(date.get(), minutes)
                : new Date();
    }
}
