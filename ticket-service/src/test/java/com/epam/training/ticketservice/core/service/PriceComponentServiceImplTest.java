package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.entity.Movie;
import com.epam.training.ticketservice.core.entity.PriceComponent;
import com.epam.training.ticketservice.core.entity.Room;
import com.epam.training.ticketservice.core.entity.Screening;
import com.epam.training.ticketservice.core.model.MovieDto;
import com.epam.training.ticketservice.core.model.PriceComponentDto;
import com.epam.training.ticketservice.core.model.RoomDto;
import com.epam.training.ticketservice.core.repository.MovieRepository;
import com.epam.training.ticketservice.core.repository.PriceComponentRepository;
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
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PriceComponentServiceImplTest {

    @Mock
    private PriceComponentRepository priceComponentRepositoryMock;
    @Mock
    private MovieRepository movieRepositoryMock;
    @Mock
    private RoomRepository roomRepositoryMock;
    @Mock
    private ScreeningRepository screeningRepositoryMock;
    @Mock
    private ApplicationDateFormatter dateFormatterMock;

    @InjectMocks
    private PriceComponentServiceImpl testPriceComponentService;

    private final String TEST_FORMATTED_TIME = "2022-12-12 12:00";

    private final String timePattern = "yyyy-MM-dd HH:mm";
    private final SimpleDateFormat testSimpleDateFormat = new SimpleDateFormat(timePattern);

    private final PriceComponent TEST_PRICE_COMPONENT = new PriceComponent(
            null,
            "dc",
            -500);

    private final PriceComponentDto TEST_PRICE_COMPONENT_DTO = new PriceComponentDto(
            "dc",
            TEST_PRICE_COMPONENT.getAmount());

    private final Movie TEST_MOVIE = new Movie(null, "Avengers", "action", 76, null);
    private final MovieDto TEST_MOVIE_DTO = new MovieDto("Avengers", "action", 76, 0);

    private final Room TEST_ROOM = new Room(null, "room", 20, 20, null);
    private final RoomDto TEST_ROOM_DTO = new RoomDto( "room", 20, 20, 0);

    @Test
    void testCreatePriceComponentShouldCreateNewPriceComponentWhenInputIsValid() {
        //Given
        Mockito.when(priceComponentRepositoryMock.findPriceComponentByName(TEST_PRICE_COMPONENT.getName()))
                .thenReturn(Optional.empty());

        String expected = TEST_PRICE_COMPONENT_DTO + " saved";

        //When
        String actual = testPriceComponentService.createPriceComponent(TEST_PRICE_COMPONENT_DTO);

        //Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(priceComponentRepositoryMock).save(TEST_PRICE_COMPONENT);
        Mockito.verify(priceComponentRepositoryMock).findPriceComponentByName(TEST_PRICE_COMPONENT.getName());
    }

    @Test
    void testCreatePriceComponentShouldReturnErrorMessageWhenPriceComponentAlreadyExists() {
        //Given
        Mockito.when(priceComponentRepositoryMock.findPriceComponentByName(TEST_PRICE_COMPONENT.getName()))
                .thenReturn(Optional.of(TEST_PRICE_COMPONENT));

        String expected = "Price component " + TEST_PRICE_COMPONENT_DTO + " already exists";

        //When
        String actual = testPriceComponentService.createPriceComponent(TEST_PRICE_COMPONENT_DTO);

        //Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(priceComponentRepositoryMock).findPriceComponentByName(TEST_PRICE_COMPONENT.getName());
    }

    @Test
    void testFindPriceComponentByNameShouldReturnOptionalPriceComponentWhenInputIsValid() {
        //Given
        Mockito.when(priceComponentRepositoryMock.findPriceComponentByName(TEST_PRICE_COMPONENT.getName()))
                .thenReturn(Optional.of(TEST_PRICE_COMPONENT));

        Optional<PriceComponentDto> expected = Optional.of(TEST_PRICE_COMPONENT_DTO);

        //When
        Optional<PriceComponentDto> actual = testPriceComponentService
                .findPriceComponentByName(TEST_PRICE_COMPONENT.getName());

        //Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(priceComponentRepositoryMock).findPriceComponentByName(TEST_PRICE_COMPONENT.getName());
    }

    @Test
    void testFindPriceComponentByNameShouldReturnOptionalEmptyWhenPriceComponentDoesNotExist() {
        //Given
        Mockito.when(priceComponentRepositoryMock.findPriceComponentByName(TEST_PRICE_COMPONENT.getName()))
                .thenReturn(Optional.empty());

        Optional<PriceComponentDto> expected = Optional.empty();

        //When
        Optional<PriceComponentDto> actual = testPriceComponentService
                .findPriceComponentByName(TEST_PRICE_COMPONENT.getName());

        //Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(priceComponentRepositoryMock).findPriceComponentByName(TEST_PRICE_COMPONENT.getName());
    }

    @Test
    void testUpdateMoviePriceComponentShouldUpdatePriceComponentWhenInputsAreValid() {
        //Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle()))
                .thenReturn(Optional.of(TEST_MOVIE));

        Mockito.when(priceComponentRepositoryMock.findPriceComponentByName(TEST_PRICE_COMPONENT.getName()))
                .thenReturn(Optional.of(TEST_PRICE_COMPONENT));

        //When
        testPriceComponentService.updateMoviePriceComponent(TEST_MOVIE_DTO.getTitle(), TEST_PRICE_COMPONENT_DTO.getName());

        //Then
        Mockito.verify(movieRepositoryMock).updateMoviePriceComponent(TEST_MOVIE, TEST_PRICE_COMPONENT);
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(priceComponentRepositoryMock).findPriceComponentByName(TEST_PRICE_COMPONENT.getName());
    }

    @Test
    void testUpdateMoviePriceComponentShouldThrowErrorWhenMovieDoesNotExist() {
        //Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle()))
                .thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testPriceComponentService
                        .updateMoviePriceComponent(TEST_MOVIE_DTO.getTitle(), TEST_PRICE_COMPONENT_DTO.getName())
        );

        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
    }

    @Test
    void testUpdateMoviePriceComponentShouldThrowErrorWhenPriceComponentDoesNotExist() {
        //Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle()))
                .thenReturn(Optional.of(TEST_MOVIE));

        Mockito.when(priceComponentRepositoryMock.findPriceComponentByName(TEST_PRICE_COMPONENT.getName()))
                .thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testPriceComponentService
                        .updateMoviePriceComponent(TEST_MOVIE_DTO.getTitle(), TEST_PRICE_COMPONENT_DTO.getName())
        );

        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(priceComponentRepositoryMock).findPriceComponentByName(TEST_PRICE_COMPONENT.getName());
    }

    @Test
    void testUpdateRoomPriceComponentShouldUpdatePriceComponentWhenInputsAreValid() {
        //Given
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName()))
                .thenReturn(Optional.of(TEST_ROOM));

        Mockito.when(priceComponentRepositoryMock.findPriceComponentByName(TEST_PRICE_COMPONENT.getName()))
                .thenReturn(Optional.of(TEST_PRICE_COMPONENT));

        //When
        testPriceComponentService.updateRoomPriceComponent(TEST_ROOM_DTO.getName(), TEST_PRICE_COMPONENT_DTO.getName());

        // Then
        Mockito.verify(roomRepositoryMock).updateRoomPriceComponent(TEST_ROOM, TEST_PRICE_COMPONENT);
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
        Mockito.verify(priceComponentRepositoryMock).findPriceComponentByName(TEST_PRICE_COMPONENT.getName());
    }

    @Test
    void testUpdateRoomPriceComponentShouldThrowErrorWhenRoomDoesNotExist() {
        //Given
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName()))
                .thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testPriceComponentService
                        .updateRoomPriceComponent(TEST_ROOM_DTO.getName(), TEST_PRICE_COMPONENT_DTO.getName())
        );

        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
    }

    @Test
    void testUpdateRoomPriceComponentShouldThrowErrorWhenPriceComponentDoesNotExist() {
        //Given
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName()))
                .thenReturn(Optional.of(TEST_ROOM));

        Mockito.when(priceComponentRepositoryMock.findPriceComponentByName(TEST_PRICE_COMPONENT.getName()))
                .thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testPriceComponentService
                        .updateRoomPriceComponent(TEST_ROOM_DTO.getName(), TEST_PRICE_COMPONENT_DTO.getName())
        );

        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
        Mockito.verify(priceComponentRepositoryMock).findPriceComponentByName(TEST_PRICE_COMPONENT.getName());
    }

    @Test
    void testUpdateScreeningPriceComponentShouldUpdatePriceComponentWhenInputsAreValid() throws ParseException {
        //Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle()))
                .thenReturn(Optional.of(TEST_MOVIE));

        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName()))
                .thenReturn(Optional.of(TEST_ROOM));

        Mockito.when(dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_FORMATTED_TIME)));

        Date date = testSimpleDateFormat.parse(TEST_FORMATTED_TIME);
        Screening screening = new Screening(null, TEST_MOVIE, TEST_ROOM, date, null);

        Mockito.when(screeningRepositoryMock.findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM, date))
                .thenReturn(Optional.of(screening));

        Mockito.when(priceComponentRepositoryMock.findPriceComponentByName(TEST_PRICE_COMPONENT.getName()))
                .thenReturn(Optional.of(TEST_PRICE_COMPONENT));

        //When
        testPriceComponentService.updateScreeningPriceComponent(
                TEST_MOVIE.getTitle(),
                TEST_ROOM.getName(),
                TEST_FORMATTED_TIME,
                TEST_PRICE_COMPONENT.getName()
        );

        //Then
        Mockito.verify(screeningRepositoryMock).updateScreeningPriceComponent(screening, TEST_PRICE_COMPONENT);
        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
        Mockito.verify(dateFormatterMock).parseStringToDate(TEST_FORMATTED_TIME);
        Mockito.verify(screeningRepositoryMock).findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM, date);
        Mockito.verify(priceComponentRepositoryMock).findPriceComponentByName(TEST_PRICE_COMPONENT.getName());
    }

    @Test
    void testUpdateScreeningPriceComponentShouldThrowErrorWhenMovieDoesNotExist() {
        //Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle()))
                .thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testPriceComponentService.updateScreeningPriceComponent(
                        TEST_MOVIE.getTitle(),
                        TEST_ROOM.getName(),
                        TEST_FORMATTED_TIME,
                        TEST_PRICE_COMPONENT.getName()
                )
        );

        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
    }

    @Test
    void testUpdateScreeningPriceComponentShouldThrowErrorWhenRoomDoesNotExist() {
        //Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle()))
                .thenReturn(Optional.of(TEST_MOVIE));

        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName()))
                .thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testPriceComponentService.updateScreeningPriceComponent(
                        TEST_MOVIE.getTitle(),
                        TEST_ROOM.getName(),
                        TEST_FORMATTED_TIME,
                        TEST_PRICE_COMPONENT.getName()
                )
        );

        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
    }

    @Test
    void testUpdateScreeningPriceComponentShouldThrowErrorWhenDateCannotBeParsed() {
        //Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle()))
                .thenReturn(Optional.of(TEST_MOVIE));

        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName()))
                .thenReturn(Optional.of(TEST_ROOM));

        Mockito.when(dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME))
                .thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testPriceComponentService.updateScreeningPriceComponent(
                        TEST_MOVIE.getTitle(),
                        TEST_ROOM.getName(),
                        TEST_FORMATTED_TIME,
                        TEST_PRICE_COMPONENT.getName()
                )
        );

        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
        Mockito.verify(dateFormatterMock).parseStringToDate(TEST_FORMATTED_TIME);
    }

    @Test
    void testUpdateScreeningPriceComponentShouldThrowErrorWhenScreeningDoesNotExist() throws ParseException {
        //Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle()))
                .thenReturn(Optional.of(TEST_MOVIE));

        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName()))
                .thenReturn(Optional.of(TEST_ROOM));

        Mockito.when(dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_FORMATTED_TIME)));

        Date date = testSimpleDateFormat.parse(TEST_FORMATTED_TIME);

        Mockito.when(screeningRepositoryMock.findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM, date))
                .thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testPriceComponentService.updateScreeningPriceComponent(
                        TEST_MOVIE.getTitle(),
                        TEST_ROOM.getName(),
                        TEST_FORMATTED_TIME,
                        TEST_PRICE_COMPONENT.getName()
                )
        );

        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
        Mockito.verify(dateFormatterMock).parseStringToDate(TEST_FORMATTED_TIME);
        Mockito.verify(screeningRepositoryMock).findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM, date);
    }

    @Test
    void testUpdateScreeningPriceComponentShouldThrowErrorWhenPriceComponentDoesNotExist() throws ParseException {
        //Given
        Mockito.when(movieRepositoryMock.findByTitle(TEST_MOVIE.getTitle()))
                .thenReturn(Optional.of(TEST_MOVIE));

        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName()))
                .thenReturn(Optional.of(TEST_ROOM));

        Mockito.when(dateFormatterMock.parseStringToDate(TEST_FORMATTED_TIME))
                .thenReturn(Optional.of(testSimpleDateFormat.parse(TEST_FORMATTED_TIME)));

        Date date = testSimpleDateFormat.parse(TEST_FORMATTED_TIME);
        Screening screening = new Screening(null, TEST_MOVIE, TEST_ROOM, date, null);

        Mockito.when(screeningRepositoryMock.findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM, date))
                .thenReturn(Optional.of(screening));
        Mockito.when(priceComponentRepositoryMock.findPriceComponentByName(TEST_PRICE_COMPONENT.getName()))
                .thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testPriceComponentService.updateScreeningPriceComponent(
                        TEST_MOVIE.getTitle(),
                        TEST_ROOM.getName(),
                        TEST_FORMATTED_TIME,
                        TEST_PRICE_COMPONENT.getName()
                )
        );

        Mockito.verify(movieRepositoryMock).findByTitle(TEST_MOVIE.getTitle());
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
        Mockito.verify(dateFormatterMock).parseStringToDate(TEST_FORMATTED_TIME);
        Mockito.verify(screeningRepositoryMock).findByMovieAndRoomAndDate(TEST_MOVIE, TEST_ROOM, date);
        Mockito.verify(priceComponentRepositoryMock).findPriceComponentByName(TEST_PRICE_COMPONENT.getName());
    }
}