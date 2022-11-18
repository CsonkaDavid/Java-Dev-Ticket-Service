package com.epam.training.ticketservice.core.time;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.mock;

class ApplicationDateHandlerTest {

    private final ApplicationDateFormatter dateFormatterMock = mock(ApplicationDateFormatter.class);
    private final String pattern = "yyyy-MM-dd HH:mm";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    @Test
    public void testAddMinutesShouldAdvanceDateForwardWhenInputIsValid() throws ParseException {
        //Given
        String testTimeFormat = "2001-11-05 14:42";
        int testMinutes = 10;
        String testAdvancedTimeFormat = "2001-11-05 14:52";

        Mockito.when(dateFormatterMock.parseStringToDate(testTimeFormat)).thenReturn(
                Optional.of(simpleDateFormat.parse(testTimeFormat)));

        Mockito.when(dateFormatterMock.parseStringToDate(testAdvancedTimeFormat)).thenReturn(
                Optional.of(simpleDateFormat.parse(testAdvancedTimeFormat)));

        ApplicationDateHandler testDateHandler = new ApplicationDateHandler();
        Optional<Date> testDate = dateFormatterMock.parseStringToDate(testTimeFormat);

        Optional<Date> expected = dateFormatterMock.parseStringToDate(testAdvancedTimeFormat);

        //When
        Optional<Date> actual = Optional.ofNullable(testDateHandler.addMinutesToDate(testDate, testMinutes));

        //Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testAddMinutesShouldReturnNewEmptyDateWhenInputIsEmpty() throws ParseException {
        //Given
        ApplicationDateHandler testDateHandler = new ApplicationDateHandler();
        Date expected = new Date();

        //When
        Date actual = testDateHandler.addMinutesToDate(Optional.empty(), 10);

        //Then
        Assertions.assertEquals(expected, actual);
    }
}