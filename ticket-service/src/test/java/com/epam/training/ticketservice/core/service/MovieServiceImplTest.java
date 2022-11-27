package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.entity.Movie;
import com.epam.training.ticketservice.core.entity.PriceComponent;
import com.epam.training.ticketservice.core.model.MovieDto;
import com.epam.training.ticketservice.core.repository.MovieRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    private final PriceComponent TEST_PRICE_COMPONENT = new PriceComponent(null, "dc", -500);

    private final Movie TEST_MOVIE = new Movie(null, "Avengers", "action", 76, null);
    private final MovieDto TEST_MOVIE_DTO = new MovieDto("Avengers", "action", 76, 0);

    private final Movie TEST_MOVIE_WITH_PRICE = new Movie(
            null,
            "Avengers",
            "action",
            76,
            TEST_PRICE_COMPONENT);
    private final MovieDto TEST_MOVIE_WITH_PRICE_DTO = new MovieDto(
            "Avengers",
            "action",
            76,
            TEST_PRICE_COMPONENT.getAmount());


    @Mock
    private MovieRepository movieRepository;
    @InjectMocks
    private MovieServiceImpl testMovieService;

    @Test
    void testCreateMovieShouldStoreGivenMovieWhenInputIsValid() {
        // Given
        Mockito.when(movieRepository.save(TEST_MOVIE)).thenReturn(TEST_MOVIE);

        // When
        testMovieService.createMovie(TEST_MOVIE_DTO);

        // Then
        Mockito.verify(movieRepository).save(TEST_MOVIE);
    }

    @Test
    void testGetMovieListShouldReturnListWithOneTestElement() {
        //Given
        Mockito.when(movieRepository.findAll()).thenReturn(List.of(TEST_MOVIE));
        List<MovieDto> expected = List.of(TEST_MOVIE_DTO);

        //When
        List<MovieDto> actual = testMovieService.getMovieList();

        //Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(movieRepository).findAll();
    }

    @Test
    void testGetMovieListShouldReturnListWithOneTestElementWithPrice() {
        //Given
        Mockito.when(movieRepository.findAll()).thenReturn(List.of(TEST_MOVIE_WITH_PRICE));
        List<MovieDto> expected = List.of(TEST_MOVIE_WITH_PRICE_DTO);

        //When
        List<MovieDto> actual = testMovieService.getMovieList();

        //Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(movieRepository).findAll();
    }

    @Test
    void testUpdateMovieShouldUpdateGivenMovieWhenInputIsValid() {
        // Given
        Mockito.when(movieRepository.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.of(TEST_MOVIE));

        // When
        testMovieService.updateMovie(TEST_MOVIE.getTitle(), TEST_MOVIE_DTO);

        // Then
        Mockito.verify(movieRepository).findByTitle(TEST_MOVIE.getTitle());
    }

    @Test
    void testUpdateMovieShouldThrowErrorWhenInputIsInvalid() {
        // Given
        Mockito.when(movieRepository.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.empty());

        // When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testMovieService.updateMovie(TEST_MOVIE.getTitle(), TEST_MOVIE_DTO));
        Mockito.verify(movieRepository).findByTitle(TEST_MOVIE.getTitle());
    }

    @Test
    void testDeleteMovieShouldRemoveGivenMovieWhenInputIsValid() {
        // Given
        Mockito.when(movieRepository.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.of(TEST_MOVIE));

        // When
        testMovieService.deleteMovie(TEST_MOVIE_DTO.getTitle());

        // Then
        Mockito.verify(movieRepository).findByTitle(TEST_MOVIE.getTitle());
    }

    @Test
    void testDeleteMovieShouldThrowErrorWhenInputIsInvalid() {
        // Given
        Mockito.when(movieRepository.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.empty());

        // When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testMovieService.deleteMovie(TEST_MOVIE_DTO.getTitle()));
        Mockito.verify(movieRepository).findByTitle(TEST_MOVIE.getTitle());
    }

    @Test
    void testFindMovieByTitleShouldReturnOptionalEmptyWhenInputIsInvalid() {
        // Given
        Mockito.when(movieRepository.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.empty());
        Optional<MovieDto> expected = Optional.empty();

        // When
        Optional<MovieDto> actual = testMovieService.findMovieByTitle(TEST_MOVIE.getTitle());

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(movieRepository).findByTitle(TEST_MOVIE.getTitle());
    }

    @Test
    void testFindMovieByTitleShouldReturnMovieWhenInputIsValid() {
        // Given
        Mockito.when(movieRepository.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.of(TEST_MOVIE));
        Optional<MovieDto> expected = Optional.of(TEST_MOVIE_DTO);

        // When
        Optional<MovieDto> actual = testMovieService.findMovieByTitle(TEST_MOVIE.getTitle());

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(movieRepository).findByTitle(TEST_MOVIE.getTitle());
    }
}