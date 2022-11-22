package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.entity.Movie;
import com.epam.training.ticketservice.core.entity.Room;
import com.epam.training.ticketservice.core.entity.Screening;
import com.epam.training.ticketservice.core.model.MovieDto;
import com.epam.training.ticketservice.core.model.RoomDto;
import com.epam.training.ticketservice.core.model.ScreeningDto;
import com.epam.training.ticketservice.core.repository.MovieRepository;
import com.epam.training.ticketservice.core.repository.RoomRepository;
import com.epam.training.ticketservice.core.repository.ScreeningRepository;
import com.epam.training.ticketservice.core.time.ApplicationDateFormatter;
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
    @InjectMocks
    private ScreeningServiceImpl testScreeningService;

    private final String timePattern = "yyyy-MM-dd HH:mm";
    private final SimpleDateFormat testSimpleDateFormat = new SimpleDateFormat(timePattern);

    private final Movie TEST_MOVIE = new Movie(null, "Avengers", "action", 76);
    private final MovieDto TEST_MOVIE_DTO = new MovieDto("Avengers", "action", 76);

    private final Room TEST_ROOM = new Room(null, "R1", 15, 20);
    private final RoomDto TEST_ROOM_DTO = new RoomDto("R1", 15, 20);

    private final String testTime = "2022-12-12 12:00";

    private final ScreeningDto TEST_SCREENING_DTO =
            new ScreeningDto(TEST_MOVIE_DTO.getTitle(), TEST_ROOM_DTO.getName(), "2022-12-12 12:00");

    @Test
    void testCreateScreeningShouldCreateNewScreeningWhenInputIsValid() throws ParseException {
        // Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName())).thenReturn(Optional.of(TEST_ROOM));

        Mockito.when(dateFormatterMock.parseStringToDate(testTime))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(testTime)));

        Date date = dateFormatterMock.parseStringToDate(testTime)
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date!"));

        Screening TEST_SCREENING = new Screening(null ,TEST_MOVIE, TEST_ROOM, date);

        Mockito.when(screeningRepositoryMock.save(TEST_SCREENING)).thenReturn(TEST_SCREENING);

        // When
        testScreeningService.createScreening(TEST_SCREENING_DTO);

        // Then
        Mockito.verify(screeningRepositoryMock).save(TEST_SCREENING);
        Mockito.verify(dateFormatterMock, Mockito.times(2)).parseStringToDate(testTime);
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
    }

    @Test
    void testCreateScreeningShouldThrowErrorWhenDateCannotBeParsed() {
        //Given
        Mockito.when(dateFormatterMock.parseStringToDate(testTime))
                .thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testScreeningService.createScreening(TEST_SCREENING_DTO));

        Mockito.verify(dateFormatterMock).parseStringToDate(testTime);
    }

    @Test
    void testCreateScreeningShouldThrowErrorWhenMovieDoesNotExist() throws ParseException {
        //Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName())).thenReturn(Optional.empty());

        Mockito.when(dateFormatterMock.parseStringToDate(testTime))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(testTime)));

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testScreeningService.createScreening(TEST_SCREENING_DTO));

        Mockito.verify(dateFormatterMock).parseStringToDate(testTime);
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
    }

    @Test
    void testCreateScreeningShouldThrowErrorWhenRoomDoesNotExist() throws ParseException {
        //Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.empty());

        Mockito.when(dateFormatterMock.parseStringToDate(testTime))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(testTime)));

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testScreeningService.createScreening(TEST_SCREENING_DTO));

        Mockito.verify(dateFormatterMock).parseStringToDate(testTime);
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
    }

    @Test
    void testDeleteScreeningShouldDeleteTestScreeningWhenInputIsValid() throws ParseException {
        //Given
        Mockito.when(dateFormatterMock.parseStringToDate(testTime))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(testTime)));

        Date date = dateFormatterMock.parseStringToDate(testTime)
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date!"));

        Screening TEST_SCREENING = new Screening(null ,TEST_MOVIE, TEST_ROOM, date);

        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName())).thenReturn(Optional.of(TEST_ROOM));
        Mockito.when(screeningRepositoryMock.findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM, date))
                .thenReturn(Optional.of(TEST_SCREENING));

        //When
        testScreeningService.deleteScreening(TEST_MOVIE_DTO, TEST_ROOM_DTO, date);

        //Then
        Mockito.verify(dateFormatterMock).parseStringToDate(testTime);
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
        Mockito.verify(screeningRepositoryMock).findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM, date);
    }

    @Test
    void testDeleteScreeningShouldThrowErrorWhenScreeningDoesNotExist() throws ParseException {
        //Given
        Mockito.when(dateFormatterMock.parseStringToDate(testTime))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(testTime)));

        Date date = dateFormatterMock.parseStringToDate(testTime)
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date!"));

        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName())).thenReturn(Optional.of(TEST_ROOM));
        Mockito.when(screeningRepositoryMock.findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM, date))
                .thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testScreeningService.deleteScreening(TEST_MOVIE_DTO, TEST_ROOM_DTO, date));

        Mockito.verify(dateFormatterMock).parseStringToDate(testTime);
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
        Mockito.verify(screeningRepositoryMock).findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM, date);
    }

    @Test
    void testDeleteScreeningShouldThrowErrorWhenMovieDoesNotExist() throws ParseException {
        //Given
        Mockito.when(dateFormatterMock.parseStringToDate(testTime))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(testTime)));

        Date date = dateFormatterMock.parseStringToDate(testTime)
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date!"));

        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testScreeningService.deleteScreening(TEST_MOVIE_DTO, TEST_ROOM_DTO, date));

        Mockito.verify(dateFormatterMock).parseStringToDate(testTime);
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
    }

    @Test
    void testDeleteScreeningShouldThrowErrorWhenRoomDoesNotExist() throws ParseException {
        //Given
        Mockito.when(dateFormatterMock.parseStringToDate(testTime))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(testTime)));

        Date date = dateFormatterMock.parseStringToDate(testTime)
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date!"));

        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName())).thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testScreeningService.deleteScreening(TEST_MOVIE_DTO, TEST_ROOM_DTO, date));

        Mockito.verify(dateFormatterMock).parseStringToDate(testTime);
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
    }

    @Test
    void testGetScreeningListShouldReturnTestScreeningListWhenItIsPresent() throws ParseException {
        // Given
        Mockito.when(dateFormatterMock.parseStringToDate(testTime))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(testTime)));

        Date date = dateFormatterMock.parseStringToDate(testTime)
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date!"));

        Mockito.when(dateFormatterMock.convertDateToString(date))
                .thenReturn(testSimpleDateFormat.format(date));

        Screening TEST_SCREENING = new Screening(null ,TEST_MOVIE, TEST_ROOM, date);

        Mockito.when(screeningRepositoryMock.findAll()).thenReturn(List.of(TEST_SCREENING));

        List<ScreeningDto> expected = List.of(TEST_SCREENING_DTO);

        //When
        List<ScreeningDto> actual = testScreeningService.getScreeningList();

        //Then
        Assertions.assertEquals(expected, actual);

        Mockito.verify(dateFormatterMock).parseStringToDate(testTime);
        Mockito.verify(dateFormatterMock).convertDateToString(date);
        Mockito.verify(screeningRepositoryMock).findAll();
    }
}