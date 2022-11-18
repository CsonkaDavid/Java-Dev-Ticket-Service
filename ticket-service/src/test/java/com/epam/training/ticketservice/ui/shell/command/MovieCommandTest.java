package com.epam.training.ticketservice.ui.shell.command;

import com.epam.training.ticketservice.core.entity.Movie;
import com.epam.training.ticketservice.core.entity.User;
import com.epam.training.ticketservice.core.model.MovieDto;
import com.epam.training.ticketservice.core.model.UserDto;
import com.epam.training.ticketservice.core.service.MovieService;
import com.epam.training.ticketservice.core.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.shell.Availability;

import java.util.List;
import java.util.Optional;

class MovieCommandTest {

    private final MovieService movieServiceMock = Mockito.mock(MovieService.class);
    private final UserService userServiceMock = Mockito.mock(UserService.class);

    private final Movie TEST_MOVIE = new Movie(null, "Avengers", "action", 76);
    private final MovieDto TEST_MOVIE_DTO = new MovieDto("Avengers", "action", 76);

    private final UserDto testUserDto = new UserDto("user", User.Role.USER);
    private final UserDto testAdminDTO = new UserDto("admin", User.Role.ADMIN);

    private final MovieCommand testMovieCommandComponent = new MovieCommand(movieServiceMock, userServiceMock);

    @Test
    void testCreateMovieShouldReturnSuccessMessageWhenInputsAreValid() {
        //Given
        String expectedOutput = TEST_MOVIE_DTO + " created";

        //When

        String actualOutput = testMovieCommandComponent.createMovie(
                TEST_MOVIE.getTitle(),
                TEST_MOVIE.getGenre(),
                TEST_MOVIE.getRunTime());

        //Then
        Assertions.assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void testUpdateMovieShouldUpdateTestMovieWhenMovieExists() {
        //Given
        MovieDto updateDTO = new MovieDto(TEST_MOVIE_DTO.getTitle(), "valami", 99);

        String expectedOutput = updateDTO + " updated";

        //When
        String actualOutput = testMovieCommandComponent.updateMovie(
                TEST_MOVIE.getTitle(),
                updateDTO.getGenre(),
                updateDTO.getRunTime());

        //Then
        Assertions.assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void testDeleteMovieShouldDeleteTestMovieWhenMovieExists() {
        //Given
        Mockito.when(movieServiceMock.findMovieByTitle(TEST_MOVIE.getTitle()))
                .thenReturn(Optional.of(TEST_MOVIE_DTO));

        String expectedOutput = TEST_MOVIE_DTO + " deleted";

        //When
        String actualOutput = testMovieCommandComponent.deleteMovie(TEST_MOVIE.getTitle());

        //Then
        Assertions.assertEquals(expectedOutput, actualOutput);
        Mockito.verify(movieServiceMock).findMovieByTitle(TEST_MOVIE.getTitle());
    }

    @Test
    void testDeleteMovieShouldThrowErrorWhenMovieDoesNotExist() {
        //Given
        Mockito.when(movieServiceMock.findMovieByTitle(TEST_MOVIE.getTitle()))
                .thenReturn(Optional.empty());

        //When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testMovieCommandComponent.deleteMovie(TEST_MOVIE.getTitle()));
        Mockito.verify(movieServiceMock).findMovieByTitle(TEST_MOVIE.getTitle());
    }

    @Test
    void testListMoviesShouldReturnListWithTestMovieWhenListIsNotEmpty() {
        //Given
        Mockito.when(movieServiceMock.getMovieList())
                .thenReturn(List.of(TEST_MOVIE_DTO));

        String expectedOutput = TEST_MOVIE_DTO.getTitle() + " ("
                + TEST_MOVIE_DTO.getGenre() + ", "
                + TEST_MOVIE_DTO.getRunTime() + " minutes)";

        //When
        String actualOutput = testMovieCommandComponent.listMovies();

        //Then
        Assertions.assertEquals(expectedOutput, actualOutput);
        Mockito.verify(movieServiceMock).getMovieList();
    }

    @Test
    void testListMoviesShouldGiveErrorMessageWhenListIsEmpty() {
        //Given
        Mockito.when(movieServiceMock.getMovieList())
                .thenReturn(List.of());

        String expectedOutput = "There are no movies at the moment";

        //When
        String actualOutput = testMovieCommandComponent.listMovies();

        //Then
        Assertions.assertEquals(expectedOutput, actualOutput);
        Mockito.verify(movieServiceMock).getMovieList();
    }

    @Test
    void testIsAdminInitiatedShouldReturnAvailableWhenUserIsPrivileged() {
        //Given
        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(Optional.of(testAdminDTO));

        boolean expected = Availability.available().isAvailable();

        //When
        boolean actual = testMovieCommandComponent.isAdminInitiated().isAvailable();

        //Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(userServiceMock).getCurrentUser();
    }

    @Test
    void testIsAdminInitiatedShouldReturnUnavailableWhenUserIsNotPrivileged() {
        //Given
        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(Optional.of(testUserDto));

        boolean expected = Availability.unavailable("").isAvailable();

        //When
        boolean actual = testMovieCommandComponent.isAdminInitiated().isAvailable();

        //Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(userServiceMock).getCurrentUser();
    }

    @Test
    void testIsAdminInitiatedShouldReturnUnavailableWhenUserIsNotLoggedIn() {
        //Given
        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(Optional.empty());

        boolean expected = Availability.unavailable("").isAvailable();

        //When
        boolean actual = testMovieCommandComponent.isAdminInitiated().isAvailable();

        //Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(userServiceMock).getCurrentUser();
    }
}