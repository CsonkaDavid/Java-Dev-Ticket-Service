package com.epam.training.ticketservice.core.timeformat;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Component
public class LocalDateFormatter {
    private final String pattern = "yyyy-MM-dd hh:mm";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    public Optional<Date> parseToDate(String formattedDateTime) {
        try {
            return Optional.of(simpleDateFormat.parse(formattedDateTime));
        } catch (ParseException e) {
            return Optional.empty();
        }
    }

    public String convertDateToString(Date date) {
        return simpleDateFormat.format(date);
    }

    public String getPattern() {
        return pattern;
    }
}
