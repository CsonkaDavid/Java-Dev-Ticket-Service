package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.entity.BasePrice;
import com.epam.training.ticketservice.core.entity.Booking;
import com.epam.training.ticketservice.core.entity.BookingSeat;
import com.epam.training.ticketservice.core.entity.Movie;
import com.epam.training.ticketservice.core.entity.Room;
import com.epam.training.ticketservice.core.entity.Screening;
import com.epam.training.ticketservice.core.entity.User;
import com.epam.training.ticketservice.core.model.ScreeningDto;
import com.epam.training.ticketservice.core.model.UserDto;
import com.epam.training.ticketservice.core.repository.BasePriceRepository;
import com.epam.training.ticketservice.core.repository.BookingRepository;
import com.epam.training.ticketservice.core.repository.MovieRepository;
import com.epam.training.ticketservice.core.repository.RoomRepository;
import com.epam.training.ticketservice.core.repository.ScreeningRepository;
import com.epam.training.ticketservice.core.repository.UserRepository;
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
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BasePriceRepository basePriceRepositoryMock;
    @Mock
    private BookingRepository bookingRepositoryMock;
    @Mock
    private ScreeningRepository screeningRepositoryMock;
    @Mock
    private MovieRepository movieRepositoryMock;
    @Mock
    private RoomRepository roomRepositoryMock;
    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private ApplicationDateFormatter applicationDateFormatterMock;

    @InjectMocks
    private BookingServiceImpl testBookingService;

    private final String timePattern = "yyyy-MM-dd HH:mm";
    private final SimpleDateFormat testSimpleDateFormat = new SimpleDateFormat(timePattern);

    private final String TEST_TIME_FORMATTED = "2022-12-22 14:00";

    private final User TEST_USER = new User(null, "user", "password", User.Role.USER);
    private final UserDto TEST_USER_DTO = new UserDto(TEST_USER.getUsername(), User.Role.USER);

    private final Movie TEST_MOVIE = new Movie(null, "Avengers", "action", 76, null);

    private final Room TEST_ROOM1 = new Room(null, "R1", 15, 20, null);
    private final Room TEST_ROOM2 = new Room(null, "R2", 15, 20, null);

    private final Screening TEST_SCREENING1 = new Screening(null, TEST_MOVIE, TEST_ROOM1, null, null);
    private final ScreeningDto TEST_SCREENING_DTO = new ScreeningDto(
            TEST_MOVIE.getTitle(),
            TEST_ROOM1.getName(),
            TEST_TIME_FORMATTED,
            null);

    private final Screening TEST_SCREENING2 = new Screening(null, TEST_MOVIE, TEST_ROOM2, null, null);

    private final BookingSeat TEST_BOOKING_SEAT1 = new BookingSeat(null, 1,2);
    private final BookingSeat TEST_BOOKING_SEAT2 = new BookingSeat(null, 1,3);

    private final Booking testBooking1 = new Booking(
            null, TEST_USER,
            TEST_SCREENING1,
            List.of(TEST_BOOKING_SEAT1, TEST_BOOKING_SEAT2),
            3000);

    private final Booking testBooking2 = new Booking(
            null, TEST_USER,
            TEST_SCREENING2,
            List.of(TEST_BOOKING_SEAT1, TEST_BOOKING_SEAT2),
            3000);

    @Test
    void testBookShouldReturnBookingInformationWhenInputsAreValidAndNoSeatsAreTakenOrNonexistent() throws ParseException {
        //Given
        Mockito.when(basePriceRepositoryMock.findAll())
                .thenReturn(List.of(new BasePrice(1, 1500)));
        Mockito.when(userRepositoryMock.findByUsername(TEST_USER.getUsername()))
                .thenReturn(Optional.of(TEST_USER));
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle()))
                .thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM1.getName()))
                .thenReturn(Optional.of(TEST_ROOM1));
        Mockito.when(applicationDateFormatterMock.parseStringToDate(TEST_TIME_FORMATTED))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_TIME_FORMATTED)));
        Mockito.when(screeningRepositoryMock.findByMovieAndRoomAndDate(
                        TEST_MOVIE, TEST_ROOM1, testSimpleDateFormat.parse(TEST_TIME_FORMATTED)))
                .thenReturn(Optional.of(TEST_SCREENING1));
        User seatTaker = new User(null, "user2", "password2", User.Role.USER);
        Booking takenBooking = new Booking(
                null,
                seatTaker,
                TEST_SCREENING1,
                List.of(TEST_BOOKING_SEAT1, TEST_BOOKING_SEAT2),
                0);

        Mockito.when(bookingRepositoryMock.findAllByScreening(TEST_SCREENING1))
                .thenReturn(List.of(takenBooking));

        String expected = "Seats booked: "
                + "(5,5), (5,6); the price for this booking is"
                + " 3000 HUF";

        //When
        String actual = testBookingService.book(TEST_USER_DTO, TEST_SCREENING_DTO, "5,5 5,6");

        //Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(userRepositoryMock).findByUsername(TEST_USER.getUsername());
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM1.getName());
        Mockito.verify(applicationDateFormatterMock).parseStringToDate(TEST_TIME_FORMATTED);
        Mockito.verify(screeningRepositoryMock)
                .findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM1,
                        testSimpleDateFormat.parse(TEST_TIME_FORMATTED));
        Mockito.verify(bookingRepositoryMock).findAllByScreening(TEST_SCREENING1);
    }

    @Test
    void testBookShouldReturnErrorMessageWhenInputsAreValidAndSeatRowIsNonexistent() throws ParseException {
        //Given
        Mockito.when(basePriceRepositoryMock.findAll())
                .thenReturn(List.of(new BasePrice(1, 1500)));
        Mockito.when(userRepositoryMock.findByUsername(TEST_USER.getUsername()))
                .thenReturn(Optional.of(TEST_USER));
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle()))
                .thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM1.getName()))
                .thenReturn(Optional.of(TEST_ROOM1));
        Mockito.when(applicationDateFormatterMock.parseStringToDate(TEST_TIME_FORMATTED))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_TIME_FORMATTED)));
        Mockito.when(screeningRepositoryMock.findByMovieAndRoomAndDate(
                        TEST_MOVIE, TEST_ROOM1, testSimpleDateFormat.parse(TEST_TIME_FORMATTED)))
                .thenReturn(Optional.of(TEST_SCREENING1));

        String expected = "Seat (100,2) does not exist in this room";

        //When
        String actual = testBookingService.book(
                TEST_USER_DTO,
                TEST_SCREENING_DTO,
                "2,2 2,3 100,2 4,6");

        //Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(userRepositoryMock).findByUsername(TEST_USER.getUsername());
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM1.getName());
        Mockito.verify(applicationDateFormatterMock).parseStringToDate(TEST_TIME_FORMATTED);
        Mockito.verify(screeningRepositoryMock)
                .findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM1,
                        testSimpleDateFormat.parse(TEST_TIME_FORMATTED));
    }

    @Test
    void testBookShouldReturnErrorMessageWhenInputsAreValidAndSeatColumnIsNonexistent() throws ParseException {
        //Given
        Mockito.when(basePriceRepositoryMock.findAll())
                .thenReturn(List.of(new BasePrice(1, 1500)));
        Mockito.when(userRepositoryMock.findByUsername(TEST_USER.getUsername()))
                .thenReturn(Optional.of(TEST_USER));
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle()))
                .thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM1.getName()))
                .thenReturn(Optional.of(TEST_ROOM1));
        Mockito.when(applicationDateFormatterMock.parseStringToDate(TEST_TIME_FORMATTED))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_TIME_FORMATTED)));
        Mockito.when(screeningRepositoryMock.findByMovieAndRoomAndDate(
                        TEST_MOVIE, TEST_ROOM1, testSimpleDateFormat.parse(TEST_TIME_FORMATTED)))
                .thenReturn(Optional.of(TEST_SCREENING1));

        String expected = "Seat (2,100) does not exist in this room";

        //When
        String actual = testBookingService.book(
                TEST_USER_DTO,
                TEST_SCREENING_DTO,
                "2,2 2,3 2,100 4,6");

        //Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(userRepositoryMock).findByUsername(TEST_USER.getUsername());
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM1.getName());
        Mockito.verify(applicationDateFormatterMock).parseStringToDate(TEST_TIME_FORMATTED);
        Mockito.verify(screeningRepositoryMock)
                .findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM1,
                        testSimpleDateFormat.parse(TEST_TIME_FORMATTED));
    }

    @Test
    void testBookShouldReturnErrorMessageWhenInputsAreValidAndSeatRowIsZero() throws ParseException {
        //Given
        Mockito.when(basePriceRepositoryMock.findAll())
                .thenReturn(List.of(new BasePrice(1, 1500)));
        Mockito.when(userRepositoryMock.findByUsername(TEST_USER.getUsername()))
                .thenReturn(Optional.of(TEST_USER));
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle()))
                .thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM1.getName()))
                .thenReturn(Optional.of(TEST_ROOM1));
        Mockito.when(applicationDateFormatterMock.parseStringToDate(TEST_TIME_FORMATTED))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_TIME_FORMATTED)));
        Mockito.when(screeningRepositoryMock.findByMovieAndRoomAndDate(
                        TEST_MOVIE, TEST_ROOM1, testSimpleDateFormat.parse(TEST_TIME_FORMATTED)))
                .thenReturn(Optional.of(TEST_SCREENING1));

        String expected = "Seat (0,2) does not exist in this room";

        //When
        String actual = testBookingService.book(
                TEST_USER_DTO,
                TEST_SCREENING_DTO,
                "2,2 2,3 0,2 4,6");

        //Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(userRepositoryMock).findByUsername(TEST_USER.getUsername());
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM1.getName());
        Mockito.verify(applicationDateFormatterMock).parseStringToDate(TEST_TIME_FORMATTED);
        Mockito.verify(screeningRepositoryMock)
                .findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM1,
                        testSimpleDateFormat.parse(TEST_TIME_FORMATTED));
    }

    @Test
    void testBookShouldReturnErrorMessageWhenInputsAreValidAndSeatColumnIsZero() throws ParseException {
        //Given
        Mockito.when(basePriceRepositoryMock.findAll())
                .thenReturn(List.of(new BasePrice(1, 1500)));
        Mockito.when(userRepositoryMock.findByUsername(TEST_USER.getUsername()))
                .thenReturn(Optional.of(TEST_USER));
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle()))
                .thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM1.getName()))
                .thenReturn(Optional.of(TEST_ROOM1));
        Mockito.when(applicationDateFormatterMock.parseStringToDate(TEST_TIME_FORMATTED))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_TIME_FORMATTED)));
        Mockito.when(screeningRepositoryMock.findByMovieAndRoomAndDate(
                        TEST_MOVIE, TEST_ROOM1, testSimpleDateFormat.parse(TEST_TIME_FORMATTED)))
                .thenReturn(Optional.of(TEST_SCREENING1));

        String expected = "Seat (2,0) does not exist in this room";

        //When

        String actual = testBookingService.book(
                TEST_USER_DTO,
                TEST_SCREENING_DTO,
                "2,2 2,3 2,0 4,6");

        //Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(userRepositoryMock).findByUsername(TEST_USER.getUsername());
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM1.getName());
        Mockito.verify(applicationDateFormatterMock).parseStringToDate(TEST_TIME_FORMATTED);
        Mockito.verify(screeningRepositoryMock)
                .findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM1,
                        testSimpleDateFormat.parse(TEST_TIME_FORMATTED));
    }

    @Test
    void testBookShouldReturnErrorMessageWhenInputsAreValidAndSeatsAreTaken() throws ParseException {
        //Given
        Mockito.when(basePriceRepositoryMock.findAll())
                .thenReturn(List.of(new BasePrice(1, 1500)));
        Mockito.when(userRepositoryMock.findByUsername(TEST_USER.getUsername()))
                .thenReturn(Optional.of(TEST_USER));
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle()))
                .thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM1.getName()))
                .thenReturn(Optional.of(TEST_ROOM1));
        Mockito.when(applicationDateFormatterMock.parseStringToDate(TEST_TIME_FORMATTED))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_TIME_FORMATTED)));
        Mockito.when(screeningRepositoryMock.findByMovieAndRoomAndDate(
                        TEST_MOVIE, TEST_ROOM1, testSimpleDateFormat.parse(TEST_TIME_FORMATTED)))
                .thenReturn(Optional.of(TEST_SCREENING1));

        User seatTaker = new User(null, "user2", "password2", User.Role.USER);
        Booking takenBooking = new Booking(
                null,
                seatTaker,
                TEST_SCREENING1,
                List.of(TEST_BOOKING_SEAT1, TEST_BOOKING_SEAT2),
                0);

        Mockito.when(bookingRepositoryMock.findAllByScreening(TEST_SCREENING1))
                .thenReturn(List.of(takenBooking));

        String expected = "Seat ("
                + TEST_BOOKING_SEAT2.getSeatRow()
                + ","
                + TEST_BOOKING_SEAT2.getSeatColumn()
                + ") is already taken";

        //When

        String actual = testBookingService.book(
                TEST_USER_DTO,
                TEST_SCREENING_DTO,
                TEST_BOOKING_SEAT1.getSeatRow()
                        + ","
                        + TEST_BOOKING_SEAT1.getSeatColumn()
                        + " "
                        + TEST_BOOKING_SEAT2.getSeatRow()
                        + ","
                        + TEST_BOOKING_SEAT2.getSeatColumn()
        );

        //Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(userRepositoryMock).findByUsername(TEST_USER.getUsername());
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM1.getName());
        Mockito.verify(applicationDateFormatterMock).parseStringToDate(TEST_TIME_FORMATTED);
        Mockito.verify(screeningRepositoryMock)
                .findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM1,
                        testSimpleDateFormat.parse(TEST_TIME_FORMATTED));
        Mockito.verify(bookingRepositoryMock).findAllByScreening(TEST_SCREENING1);
    }

    @Test
    void testBookShouldThrowErrorWhenUserDoesNotExist() {
        //Given
        Mockito.when(userRepositoryMock.findByUsername(TEST_USER.getUsername()))
                .thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testBookingService.book(TEST_USER_DTO, TEST_SCREENING_DTO, ""));
        Mockito.verify(userRepositoryMock).findByUsername(TEST_USER.getUsername());
    }

    @Test
    void testBookShouldThrowErrorWhenMovieDoesNotExist() {
        //Given
        Mockito.when(userRepositoryMock.findByUsername(TEST_USER.getUsername()))
                .thenReturn(Optional.of(TEST_USER));
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle()))
                .thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testBookingService.book(TEST_USER_DTO, TEST_SCREENING_DTO, ""));
        Mockito.verify(userRepositoryMock).findByUsername(TEST_USER.getUsername());
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
    }

    @Test
    void testBookShouldThrowErrorWhenRoomDoesNotExist() {
        //Given
        Mockito.when(userRepositoryMock.findByUsername(TEST_USER.getUsername()))
                .thenReturn(Optional.of(TEST_USER));
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle()))
                .thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM1.getName()))
                .thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testBookingService.book(TEST_USER_DTO, TEST_SCREENING_DTO, ""));
        Mockito.verify(userRepositoryMock).findByUsername(TEST_USER.getUsername());
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM1.getName());
    }

    @Test
    void testBookShouldThrowErrorWhenDateCannotBeParsed() {
        //Given
        Mockito.when(userRepositoryMock.findByUsername(TEST_USER.getUsername()))
                .thenReturn(Optional.of(TEST_USER));
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle()))
                .thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM1.getName()))
                .thenReturn(Optional.of(TEST_ROOM1));
        Mockito.when(applicationDateFormatterMock.parseStringToDate(TEST_TIME_FORMATTED))
                .thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testBookingService.book(TEST_USER_DTO, TEST_SCREENING_DTO, ""));
        Mockito.verify(userRepositoryMock).findByUsername(TEST_USER.getUsername());
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM1.getName());
        Mockito.verify(applicationDateFormatterMock).parseStringToDate(TEST_TIME_FORMATTED);
    }

    @Test
    void testBookShouldThrowErrorWhenScreeningDoesNotExist() throws ParseException {
        //Given
        Mockito.when(userRepositoryMock.findByUsername(TEST_USER.getUsername()))
                .thenReturn(Optional.of(TEST_USER));
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle()))
                .thenReturn(Optional.of(TEST_MOVIE));
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM1.getName()))
                .thenReturn(Optional.of(TEST_ROOM1));
        Mockito.when(applicationDateFormatterMock.parseStringToDate(TEST_TIME_FORMATTED))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_TIME_FORMATTED)));
        Mockito.when(screeningRepositoryMock.findByMovieAndRoomAndDate(
                TEST_MOVIE, TEST_ROOM1, testSimpleDateFormat.parse(TEST_TIME_FORMATTED)))
                .thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testBookingService.book(TEST_USER_DTO, TEST_SCREENING_DTO, ""));
        Mockito.verify(userRepositoryMock).findByUsername(TEST_USER.getUsername());
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM1.getName());
        Mockito.verify(applicationDateFormatterMock).parseStringToDate(TEST_TIME_FORMATTED);
        Mockito.verify(screeningRepositoryMock)
                .findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM1, testSimpleDateFormat.parse(TEST_TIME_FORMATTED));
    }

    @Test
    void testFindBookingsShouldReturnBookingListStringWhenUserHasBookings() {
        //Given
        Mockito.when(userRepositoryMock.findByUsername(TEST_USER.getUsername()))
                .thenReturn(Optional.of(TEST_USER));

        Mockito.when(bookingRepositoryMock.findAllByUser(TEST_USER))
                .thenReturn(List.of(testBooking1, testBooking2));

        Mockito.when(applicationDateFormatterMock.convertDateToString(TEST_SCREENING1.getDate()))
                .thenReturn(TEST_TIME_FORMATTED);

        StringBuilder expectedFirstLine = new StringBuilder();
        StringBuilder expectedSecondLine = new StringBuilder();
        StringBuilder expected = new StringBuilder();

        expectedFirstLine
                .append("Seats (")
                .append(TEST_BOOKING_SEAT1.getSeatRow())
                .append(",")
                .append(TEST_BOOKING_SEAT1.getSeatColumn())
                .append("), (")
                .append(TEST_BOOKING_SEAT2.getSeatRow())
                .append(",")
                .append(TEST_BOOKING_SEAT2.getSeatColumn())
                .append(") on ")
                .append(TEST_MOVIE.getTitle())
                .append(" in room ")
                .append(TEST_ROOM1.getName())
                .append(" starting at ")
                .append(TEST_TIME_FORMATTED)
                .append(" for 3000 HUF");

        expectedSecondLine
                .append("Seats (")
                .append(TEST_BOOKING_SEAT1.getSeatRow())
                .append(",")
                .append(TEST_BOOKING_SEAT1.getSeatColumn())
                .append("), (")
                .append(TEST_BOOKING_SEAT2.getSeatRow())
                .append(",")
                .append(TEST_BOOKING_SEAT2.getSeatColumn())
                .append(") on ")
                .append(TEST_MOVIE.getTitle())
                .append(" in room ")
                .append(TEST_ROOM2.getName())
                .append(" starting at ")
                .append(TEST_TIME_FORMATTED)
                .append(" for 3000 HUF");

        expected
                .append(expectedFirstLine)
                .append("\n")
                .append(expectedSecondLine);

        //When
        Optional<String> actual = testBookingService.findBookings(TEST_USER_DTO);

        //Then
        Assertions.assertEquals(Optional.of(expected.toString()), actual);
        Mockito.verify(userRepositoryMock).findByUsername(TEST_USER.getUsername());
        Mockito.verify(bookingRepositoryMock).findAllByUser(TEST_USER);
        Mockito.verify(applicationDateFormatterMock, Mockito.times(2))
                .convertDateToString(TEST_SCREENING1.getDate());
    }

    @Test
    void testFindBookingsShouldThrowErrorWhenUserCannotBeFound() {
        //Given
        Mockito.when(userRepositoryMock.findByUsername(TEST_USER.getUsername()))
                .thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testBookingService.findBookings(TEST_USER_DTO));
        Mockito.verify(userRepositoryMock).findByUsername(TEST_USER.getUsername());
    }

    @Test
    void findBookingsShouldReturnOptionalEmptyWhenBookingListIsEmpty() {
        //Given
        Mockito.when(userRepositoryMock.findByUsername(TEST_USER.getUsername()))
                .thenReturn(Optional.of(TEST_USER));

        Mockito.when(bookingRepositoryMock.findAllByUser(TEST_USER))
                .thenReturn(List.of());

        Optional<String> expected = Optional.empty();

        //When
        Optional<String> actual = testBookingService.findBookings(TEST_USER_DTO);

        //Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(userRepositoryMock).findByUsername(TEST_USER.getUsername());
        Mockito.verify(bookingRepositoryMock).findAllByUser(TEST_USER);
    }
}