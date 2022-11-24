package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.entity.Movie;
import com.epam.training.ticketservice.core.entity.PriceComponent;
import com.epam.training.ticketservice.core.entity.Room;
import com.epam.training.ticketservice.core.entity.Screening;
import com.epam.training.ticketservice.core.model.MovieDto;
import com.epam.training.ticketservice.core.model.RoomDto;
import com.epam.training.ticketservice.core.model.ScreeningDto;
import com.epam.training.ticketservice.core.repository.MovieRepository;
import com.epam.training.ticketservice.core.repository.RoomRepository;
import com.epam.training.ticketservice.core.repository.ScreeningRepository;
import com.epam.training.ticketservice.core.time.ApplicationDateFormatter;
import com.epam.training.ticketservice.core.time.ApplicationDateHandler;
import org.checkerframework.checker.nullness.Opt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ScreeningServiceImplTest {

    @Mock
    private ScreeningRepository screeningRepositoryMock;
    @Mock
    private RoomRepository roomRepositoryMock;
    @Mock
    private MovieRepository movieRepositoryMock;
    @Mock
    private ApplicationDateFormatter dateFormatterMock;
    @Mock
    private ApplicationDateHandler dateHandlerMock;
    @InjectMocks
    private ScreeningServiceImpl testScreeningService;

    private final String timePattern = "yyyy-MM-dd HH:mm";
    private final SimpleDateFormat testSimpleDateFormat = new SimpleDateFormat(timePattern);

    private final Movie TEST_MOVIE = new Movie(null, "Avengers", "action", 76, null);
    private final MovieDto TEST_MOVIE_DTO = new MovieDto("Avengers", "action", 76, 0);

    private final Room TEST_ROOM = new Room(null, "R1", 15, 20, null);
    private final RoomDto TEST_ROOM_DTO = new RoomDto("R1", 15, 20, 0);

    private final String TEST_FORMATTED_TIME = "2022-12-12 12:00";

    private final ScreeningDto TEST_SCREENING_DTO =
            new ScreeningDto(TEST_MOVIE_DTO.getTitle(), TEST_ROOM_DTO.getName(), TEST_FORMATTED_TIME, 0);

    private final ScreeningDto TEST_SCREENING_DTO_WITH_PRICE =
            new ScreeningDto(TEST_MOVIE_DTO.getTitle(), TEST_ROOM_DTO.getName(), TEST_FORMATTED_TIME, -300);

    @Test
    void testCreateScreeningShouldCreateNewScreeningWhenInputIsValidAndThereAreNoOverlaps() throws ParseException {
        // Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName())).thenReturn(Optional.of(TEST_ROOM));
        Mockito.when(dateFormatterMock.parseStringToDate(TEST_SCREENING_DTO.getFormattedDateTime()))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_SCREENING_DTO.getFormattedDateTime())));

        Date screeningDate = dateFormatterMock.parseStringToDate(TEST_SCREENING_DTO.getFormattedDateTime())
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date!"));

        Mockito.when(dateHandlerMock.addMinutesToDate(screeningDate, TEST_MOVIE.getRunTime()))
                .thenReturn(testSimpleDateFormat.parse("2022-12-12 13:16"));

        Mockito.when(dateHandlerMock.addMinutesToDate(screeningDate, TEST_MOVIE.getRunTime() + 10))
                .thenReturn(testSimpleDateFormat.parse("2022-12-12 13:26"));

        String testEndsBeforeTime = "2022-12-12 13:30";

        Mockito.when(dateFormatterMock.parseStringToDate(testEndsBeforeTime))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(testEndsBeforeTime)));

        Date endsBeforeDate = dateFormatterMock.parseStringToDate(testEndsBeforeTime).get();

        Screening endsBeforeScreening = new Screening(null ,TEST_MOVIE, TEST_ROOM, endsBeforeDate, null);

        Mockito.when(dateHandlerMock.addMinutesToDate(endsBeforeDate, endsBeforeScreening.getMovie().getRunTime()))
                .thenReturn(testSimpleDateFormat.parse("2022-12-12 14:46"));

        Mockito.when(dateHandlerMock.addMinutesToDate(endsBeforeDate, endsBeforeScreening.getMovie().getRunTime() + 10))
                .thenReturn(testSimpleDateFormat.parse("2022-12-12 14:56"));

        String testBeginsAfterTime = "2022-12-12 10:30";

        Mockito.when(dateFormatterMock.parseStringToDate(testBeginsAfterTime))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(testBeginsAfterTime)));

        Date beginsAfterDate = dateFormatterMock.parseStringToDate(testBeginsAfterTime).get();

        Screening beginsAfterScreening = new Screening(null ,TEST_MOVIE, TEST_ROOM, beginsAfterDate, null);

        Mockito.when(dateHandlerMock.addMinutesToDate(beginsAfterDate, beginsAfterScreening.getMovie().getRunTime()))
                .thenReturn(testSimpleDateFormat.parse("2022-12-12 11:46"));

        Mockito.when(dateHandlerMock.addMinutesToDate(beginsAfterDate, beginsAfterScreening.getMovie().getRunTime() + 10))
                .thenReturn(testSimpleDateFormat.parse("2022-12-12 11:56"));

        Mockito.when(screeningRepositoryMock.findAll()).thenReturn(List.of(endsBeforeScreening, beginsAfterScreening));

        String expected = TEST_SCREENING_DTO + " created";

        // When
        String actual = testScreeningService.createScreening(TEST_SCREENING_DTO);

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
        Mockito.verify(dateFormatterMock, Mockito.times(2))
                .parseStringToDate(TEST_SCREENING_DTO.getFormattedDateTime());
        Mockito.verify(dateHandlerMock).addMinutesToDate(screeningDate, TEST_MOVIE.getRunTime());
        Mockito.verify(dateHandlerMock).addMinutesToDate(screeningDate, TEST_MOVIE.getRunTime() + 10);
        Mockito.verify(dateFormatterMock).parseStringToDate(testEndsBeforeTime);
        Mockito.verify(dateHandlerMock).addMinutesToDate(endsBeforeDate, endsBeforeScreening.getMovie().getRunTime());
        Mockito.verify(dateHandlerMock)
                .addMinutesToDate(endsBeforeDate, endsBeforeScreening.getMovie().getRunTime() + 10);
        Mockito.verify(dateFormatterMock).parseStringToDate(testBeginsAfterTime);
        Mockito.verify(dateHandlerMock).addMinutesToDate(beginsAfterDate, beginsAfterScreening.getMovie().getRunTime());
        Mockito.verify(dateHandlerMock)
                .addMinutesToDate(beginsAfterDate, beginsAfterScreening.getMovie().getRunTime() + 10);
        Mockito.verify(screeningRepositoryMock, Mockito.times(2)).findAll();
    }

    @Test
    void testCreateScreeningShouldReturnErrorMessageWhenInputIsValidButScreeningIsOverlappingAfter() throws ParseException {
        // Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName())).thenReturn(Optional.of(TEST_ROOM));
        Mockito.when(dateFormatterMock.parseStringToDate(TEST_SCREENING_DTO.getFormattedDateTime()))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_SCREENING_DTO.getFormattedDateTime())));

        Date screeningDate = dateFormatterMock.parseStringToDate(TEST_SCREENING_DTO.getFormattedDateTime())
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date!"));

        Mockito.when(dateHandlerMock.addMinutesToDate(screeningDate, TEST_MOVIE.getRunTime()))
                .thenReturn(testSimpleDateFormat.parse("2022-12-12 13:16"));

        String testOverlapTimeAfter = "2022-12-12 12:30";
        Mockito.when(dateFormatterMock.parseStringToDate(testOverlapTimeAfter))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(testOverlapTimeAfter)));

        Date existingDate = dateFormatterMock.parseStringToDate(testOverlapTimeAfter).get();

        Screening existingScreening = new Screening(null ,TEST_MOVIE, TEST_ROOM, existingDate, null);

        Mockito.when(dateHandlerMock.addMinutesToDate(existingDate, existingScreening.getMovie().getRunTime()))
                .thenReturn(testSimpleDateFormat.parse("2022-12-12 13:46"));

        Mockito.when(screeningRepositoryMock.findAll()).thenReturn(List.of(existingScreening));

        String expected = "There is an overlapping screening";

        // When
        String actual = testScreeningService.createScreening(TEST_SCREENING_DTO);

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
        Mockito.verify(dateFormatterMock, Mockito.times(2))
                .parseStringToDate(TEST_SCREENING_DTO.getFormattedDateTime());
        Mockito.verify(dateHandlerMock).addMinutesToDate(screeningDate, TEST_MOVIE.getRunTime());
        Mockito.verify(dateFormatterMock).parseStringToDate(testOverlapTimeAfter);
        Mockito.verify(dateHandlerMock).addMinutesToDate(existingDate, existingScreening.getMovie().getRunTime());
        Mockito.verify(screeningRepositoryMock).findAll();
    }

    @Test
    void testCreateScreeningShouldReturnErrorMessageWhenInputIsValidButScreeningIsOverlappingBefore() throws ParseException {
        // Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName())).thenReturn(Optional.of(TEST_ROOM));
        Mockito.when(dateFormatterMock.parseStringToDate(TEST_SCREENING_DTO.getFormattedDateTime()))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_SCREENING_DTO.getFormattedDateTime())));

        Date screeningDate = dateFormatterMock.parseStringToDate(TEST_SCREENING_DTO.getFormattedDateTime())
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date!"));

        Mockito.when(dateHandlerMock.addMinutesToDate(screeningDate, TEST_MOVIE.getRunTime()))
                .thenReturn(testSimpleDateFormat.parse("2022-12-12 13:16"));

        String testOverlapTimeBefore = "2022-12-12 11:40";
        Mockito.when(dateFormatterMock.parseStringToDate(testOverlapTimeBefore))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(testOverlapTimeBefore)));

        Date existingDate = dateFormatterMock.parseStringToDate(testOverlapTimeBefore).get();

        Screening existingScreening = new Screening(null ,TEST_MOVIE, TEST_ROOM, existingDate, null);

        Mockito.when(dateHandlerMock.addMinutesToDate(existingDate, existingScreening.getMovie().getRunTime()))
                .thenReturn(testSimpleDateFormat.parse("2022-12-12 12:56"));

        Mockito.when(screeningRepositoryMock.findAll()).thenReturn(List.of(existingScreening));

        String expected = "There is an overlapping screening";

        // When
        String actual = testScreeningService.createScreening(TEST_SCREENING_DTO);

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
        Mockito.verify(dateFormatterMock, Mockito.times(2))
                .parseStringToDate(TEST_SCREENING_DTO.getFormattedDateTime());
        Mockito.verify(dateHandlerMock).addMinutesToDate(screeningDate, TEST_MOVIE.getRunTime());
        Mockito.verify(dateFormatterMock).parseStringToDate(testOverlapTimeBefore);
        Mockito.verify(dateHandlerMock).addMinutesToDate(existingDate, existingScreening.getMovie().getRunTime());
        Mockito.verify(screeningRepositoryMock).findAll();
    }

    @Test
    void testCreateScreeningShouldReturnErrorMessageWhenInputIsValidButScreeningIsOverlappingWithABreakPeriod()
            throws ParseException {

        // Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName())).thenReturn(Optional.of(TEST_ROOM));
        Mockito.when(dateFormatterMock.parseStringToDate(TEST_SCREENING_DTO.getFormattedDateTime()))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_SCREENING_DTO.getFormattedDateTime())));

        Date screeningDate = dateFormatterMock.parseStringToDate(TEST_SCREENING_DTO.getFormattedDateTime())
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date!"));

        Mockito.when(dateHandlerMock.addMinutesToDate(screeningDate, TEST_MOVIE.getRunTime()))
                .thenReturn(testSimpleDateFormat.parse("2022-12-12 13:16"));

        String testOverlapBreakPeriodTime = "2022-12-12 13:20";
        Mockito.when(dateFormatterMock.parseStringToDate(testOverlapBreakPeriodTime))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(testOverlapBreakPeriodTime)));

        Date existingDate = dateFormatterMock.parseStringToDate(testOverlapBreakPeriodTime).get();

        Screening existingScreening = new Screening(null ,TEST_MOVIE, TEST_ROOM, existingDate, null);

        Mockito.when(dateHandlerMock.addMinutesToDate(existingDate, existingScreening.getMovie().getRunTime()))
                .thenReturn(testSimpleDateFormat.parse("2022-12-12 14:36"));

        Mockito.when(dateHandlerMock.addMinutesToDate(screeningDate, TEST_MOVIE.getRunTime() + 10))
                .thenReturn(testSimpleDateFormat.parse("2022-12-12 13:26"));

        Mockito.when(dateHandlerMock.addMinutesToDate(existingDate, TEST_MOVIE.getRunTime() + 10))
                .thenReturn(testSimpleDateFormat.parse("2022-12-12 14:46"));

        Mockito.when(screeningRepositoryMock.findAll()).thenReturn(List.of(existingScreening));

        String expected = "This would start in the break period after another screening in this room";

        // When
        String actual = testScreeningService.createScreening(TEST_SCREENING_DTO);

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(dateFormatterMock, Mockito.times(2))
                .parseStringToDate(TEST_SCREENING_DTO.getFormattedDateTime());
        Mockito.verify(dateFormatterMock).parseStringToDate(testOverlapBreakPeriodTime);
        Mockito.verify(dateHandlerMock).addMinutesToDate(existingDate, existingScreening.getMovie().getRunTime());
        Mockito.verify(dateHandlerMock).addMinutesToDate(screeningDate, TEST_MOVIE.getRunTime());
        Mockito.verify(dateHandlerMock).addMinutesToDate(screeningDate, TEST_MOVIE.getRunTime() + 10);
        Mockito.verify(dateHandlerMock).addMinutesToDate(existingDate, TEST_MOVIE.getRunTime() + 10);
        Mockito.verify(screeningRepositoryMock, Mockito.times(2)).findAll();
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
    }

    @Test
    void testCreateScreeningShouldThrowErrorWhenDateCannotBeParsed() {
        //Given
        Mockito.when(dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME))
                .thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testScreeningService.createScreening(TEST_SCREENING_DTO));

        Mockito.verify(dateFormatterMock).parseStringToDate(TEST_FORMATTED_TIME);
    }

    @Test
    void testCreateScreeningShouldThrowErrorWhenMovieDoesNotExist() throws ParseException {
        //Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName())).thenReturn(Optional.empty());

        Mockito.when(dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_FORMATTED_TIME)));

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testScreeningService.createScreening(TEST_SCREENING_DTO));

        Mockito.verify(dateFormatterMock).parseStringToDate(TEST_FORMATTED_TIME);
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
    }

    @Test
    void testCreateScreeningShouldThrowErrorWhenRoomDoesNotExist() throws ParseException {
        //Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.empty());

        Mockito.when(dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_FORMATTED_TIME)));

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testScreeningService.createScreening(TEST_SCREENING_DTO));

        Mockito.verify(dateFormatterMock).parseStringToDate(TEST_FORMATTED_TIME);
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
    }

    @Test
    void testDeleteScreeningShouldDeleteTestScreeningWhenInputIsValid() throws ParseException {
        //Given
        Mockito.when(dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_FORMATTED_TIME)));

        Date date = dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME)
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date!"));

        Screening TEST_SCREENING = new Screening(null ,TEST_MOVIE, TEST_ROOM, date, null);

        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName())).thenReturn(Optional.of(TEST_ROOM));
        Mockito.when(screeningRepositoryMock.findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM, date))
                .thenReturn(Optional.of(TEST_SCREENING));

        //When
        testScreeningService.deleteScreening(TEST_MOVIE_DTO, TEST_ROOM_DTO, date);

        //Then
        Mockito.verify(dateFormatterMock).parseStringToDate(TEST_FORMATTED_TIME);
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
        Mockito.verify(screeningRepositoryMock).findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM, date);
    }

    @Test
    void testDeleteScreeningShouldThrowErrorWhenScreeningDoesNotExist() throws ParseException {
        //Given
        Mockito.when(dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_FORMATTED_TIME)));

        Date date = dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME)
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date!"));

        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName())).thenReturn(Optional.of(TEST_ROOM));
        Mockito.when(screeningRepositoryMock.findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM, date))
                .thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testScreeningService.deleteScreening(TEST_MOVIE_DTO, TEST_ROOM_DTO, date));

        Mockito.verify(dateFormatterMock).parseStringToDate(TEST_FORMATTED_TIME);
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
        Mockito.verify(screeningRepositoryMock).findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM, date);
    }

    @Test
    void testDeleteScreeningShouldThrowErrorWhenMovieDoesNotExist() throws ParseException {
        //Given
        Mockito.when(dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_FORMATTED_TIME)));

        Date date = dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME)
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date!"));

        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testScreeningService.deleteScreening(TEST_MOVIE_DTO, TEST_ROOM_DTO, date));

        Mockito.verify(dateFormatterMock).parseStringToDate(TEST_FORMATTED_TIME);
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
    }

    @Test
    void testDeleteScreeningShouldThrowErrorWhenRoomDoesNotExist() throws ParseException {
        //Given
        Mockito.when(dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_FORMATTED_TIME)));

        Date date = dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME)
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date!"));

        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName())).thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testScreeningService.deleteScreening(TEST_MOVIE_DTO, TEST_ROOM_DTO, date));

        Mockito.verify(dateFormatterMock).parseStringToDate(TEST_FORMATTED_TIME);
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
    }

    @Test
    void testGetScreeningListShouldReturnTestScreeningListWhenItIsPresent() throws ParseException {
        // Given
        Mockito.when(dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_FORMATTED_TIME)));

        Date date = dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME)
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date!"));

        Mockito.when(dateFormatterMock.convertDateToString(date))
                .thenReturn(testSimpleDateFormat.format(date));

        Screening TEST_SCREENING = new Screening(null ,TEST_MOVIE, TEST_ROOM, date, null);

        Mockito.when(screeningRepositoryMock.findAll()).thenReturn(List.of(TEST_SCREENING));

        List<ScreeningDto> expected = List.of(TEST_SCREENING_DTO);

        //When
        List<ScreeningDto> actual = testScreeningService.getScreeningList();

        //Then
        Assertions.assertEquals(expected, actual);

        Mockito.verify(dateFormatterMock).parseStringToDate(TEST_FORMATTED_TIME);
        Mockito.verify(dateFormatterMock).convertDateToString(date);
        Mockito.verify(screeningRepositoryMock).findAll();
    }

    @Test
    void testGetScreeningListShouldReturnTestScreeningListWhenItIsPresentWithPriceComponent() throws ParseException {
        // Given
        Mockito.when(dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_FORMATTED_TIME)));

        Date date = dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME)
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date!"));

        Mockito.when(dateFormatterMock.convertDateToString(date))
                .thenReturn(testSimpleDateFormat.format(date));

        Screening TEST_SCREENING = new Screening(
                null,
                TEST_MOVIE,
                TEST_ROOM,
                date,
                new PriceComponent(null, "dc", TEST_SCREENING_DTO_WITH_PRICE.getPriceComponent()));

        Mockito.when(screeningRepositoryMock.findAll()).thenReturn(List.of(TEST_SCREENING));

        List<ScreeningDto> expected = List.of(TEST_SCREENING_DTO_WITH_PRICE);

        //When
        List<ScreeningDto> actual = testScreeningService.getScreeningList();

        //Then
        Assertions.assertEquals(expected, actual);

        Mockito.verify(dateFormatterMock).parseStringToDate(TEST_FORMATTED_TIME);
        Mockito.verify(dateFormatterMock).convertDateToString(date);
        Mockito.verify(screeningRepositoryMock).findAll();
    }

    @Test
    void testFindScreeningShouldReturnScreeningWhenInputsAreValid() throws ParseException {
        //Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName())).thenReturn(Optional.of(TEST_ROOM));
        Mockito.when(dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_FORMATTED_TIME)));

        Date screeningDate = dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME)
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date!"));

        Screening screening = new Screening(null, TEST_MOVIE, TEST_ROOM, screeningDate, null);

        Mockito.when(dateFormatterMock.convertDateToString(screeningDate))
                .thenReturn(testSimpleDateFormat.format(screeningDate));

        Mockito.when(screeningRepositoryMock.findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM, screeningDate))
                .thenReturn(Optional.of(screening));

        Optional<ScreeningDto> expected = Optional.of(TEST_SCREENING_DTO);

        //When Then

        Optional<ScreeningDto> actual = testScreeningService
                .findScreeningByMovieAndRoomAndDate(TEST_MOVIE_DTO, TEST_ROOM_DTO, TEST_FORMATTED_TIME);

        Assertions.assertEquals(expected, actual);

        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
        Mockito.verify(dateFormatterMock, Mockito.times(2)).parseStringToDate(TEST_FORMATTED_TIME);
        Mockito.verify(screeningRepositoryMock).findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM, screeningDate);
        Mockito.verify(dateFormatterMock).convertDateToString(screeningDate);
    }

    @Test
    void testFindScreeningShouldReturnScreeningWhenInputsAreValidWithPrice() throws ParseException {
        //Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName())).thenReturn(Optional.of(TEST_ROOM));
        Mockito.when(dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_FORMATTED_TIME)));

        Date screeningDate = dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME)
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date!"));

        Screening screening = new Screening(
                null,
                TEST_MOVIE,
                TEST_ROOM,
                screeningDate,
                new PriceComponent(null, "dc", TEST_SCREENING_DTO_WITH_PRICE.getPriceComponent()));

        Mockito.when(dateFormatterMock.convertDateToString(screeningDate))
                .thenReturn(testSimpleDateFormat.format(screeningDate));

        Mockito.when(screeningRepositoryMock.findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM, screeningDate))
                .thenReturn(Optional.of(screening));

        Optional<ScreeningDto> expected = Optional.of(TEST_SCREENING_DTO_WITH_PRICE);

        //When Then

        Optional<ScreeningDto> actual = testScreeningService
                .findScreeningByMovieAndRoomAndDate(TEST_MOVIE_DTO, TEST_ROOM_DTO, TEST_FORMATTED_TIME);

        Assertions.assertEquals(expected, actual);

        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
        Mockito.verify(dateFormatterMock, Mockito.times(2)).parseStringToDate(TEST_FORMATTED_TIME);
        Mockito.verify(screeningRepositoryMock).findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM, screeningDate);
        Mockito.verify(dateFormatterMock).convertDateToString(screeningDate);
    }

    @Test
    void testFindScreeningShouldReturnOptionalEmptyWhenScreeningDoesNotExist() throws ParseException {
        //Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName())).thenReturn(Optional.of(TEST_ROOM));
        Mockito.when(dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_FORMATTED_TIME)));

        Date screeningDate = dateFormatterMock.parseStringToDate(TEST_SCREENING_DTO.getFormattedDateTime())
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date!"));

        Mockito.when(screeningRepositoryMock.findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM, screeningDate))
                .thenReturn(Optional.empty());

        Optional<ScreeningDto> expected = Optional.empty();

        //When Then

        Optional<ScreeningDto> actual = testScreeningService
                .findScreeningByMovieAndRoomAndDate(TEST_MOVIE_DTO, TEST_ROOM_DTO, TEST_FORMATTED_TIME);

        Assertions.assertEquals(expected, actual);

        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
        Mockito.verify(dateFormatterMock, Mockito.times(2)).parseStringToDate(TEST_FORMATTED_TIME);
        Mockito.verify(screeningRepositoryMock).findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM, screeningDate);
    }

    @Test
    void testFindScreeningShouldThrowErrorWhenMovieDoesNotExist() {
        //Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.empty());

        //When Then

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testScreeningService
                        .findScreeningByMovieAndRoomAndDate(TEST_MOVIE_DTO, TEST_ROOM_DTO, TEST_FORMATTED_TIME)
        );

        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
    }

    @Test
    void testFindScreeningShouldThrowErrorWhenRoomDoesNotExist() {
        //Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName())).thenReturn(Optional.empty());

        //When Then

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testScreeningService
                        .findScreeningByMovieAndRoomAndDate(TEST_MOVIE_DTO, TEST_ROOM_DTO, TEST_FORMATTED_TIME)
        );

        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
    }

    @Test
    void testFindScreeningShouldThrowErrorWhenDateCannotBeParsed() {
        //Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName())).thenReturn(Optional.of(TEST_ROOM));
        Mockito.when(dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME)).thenReturn(Optional.empty());

        //When Then

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testScreeningService
                        .findScreeningByMovieAndRoomAndDate(TEST_MOVIE_DTO, TEST_ROOM_DTO, TEST_FORMATTED_TIME)
        );

        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
        Mockito.verify(dateFormatterMock).parseStringToDate(TEST_FORMATTED_TIME);
    }
}