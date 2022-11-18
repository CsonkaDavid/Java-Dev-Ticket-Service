package com.epam.training.ticketservice.core.time;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

class ApplicationDateFormatterTest {

    private final String pattern = "yyyy-MM-dd HH:mm";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    @Test
    void testConvertDateShouldFormatDateToPattern() throws ParseException {
        //Given
        ApplicationDateFormatter applicationDateFormatter = new ApplicationDateFormatter();
        String testPattern = "yyyy.MM.dd";
        SimpleDateFormat testSimpleDateFormat = new SimpleDateFormat(testPattern);

        String testTimeFormat = "2001.11.05";
        Date testDateFormat = testSimpleDateFormat.parse(testTimeFormat);

        String expected = "2001-11-05 00:00";

        //When
        String actual = applicationDateFormatter.convertDateToString(testDateFormat);

        //When Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testParseStringShouldParseDateWhenInputIsInValidFormat() {
        //Given
        ApplicationDateFormatter applicationDateFormatter = new ApplicationDateFormatter();
        String date = "2022-11-05 14:42";
        Optional<Date> expected;

        try {
            expected = Optional.ofNullable(simpleDateFormat.parse(date));
        } catch (ParseException e) {
            expected = Optional.empty();
        }

        //When
        Optional<Date> actual = applicationDateFormatter.parseStringToDate(date);

        //Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testParseStringShouldThrowParseExceptionWhenInputIsNotInValidFormat() {
        //Given
        ApplicationDateFormatter applicationDateFormatter = new ApplicationDateFormatter();
        String date = "2022.11.05";
        Optional<Date> expected = Optional.empty();

        //When
        Optional<Date> actual = applicationDateFormatter.parseStringToDate(date);

        //Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testGetPatternShouldReturnCorrectPattern() {
        //Given
        ApplicationDateFormatter applicationDateFormatter = new ApplicationDateFormatter();

        //When
        String actual = applicationDateFormatter.getPattern();

        //Then
        Assertions.assertEquals(pattern, actual);
    }
}