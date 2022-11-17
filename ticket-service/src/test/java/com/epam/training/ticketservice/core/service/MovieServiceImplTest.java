package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.entity.Movie;
import com.epam.training.ticketservice.core.model.MovieDTO;
import com.epam.training.ticketservice.core.repository.MovieRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;

class MovieServiceImplTest {

    private final Movie TEST_MOVIE = new Movie(null, "Avengers", "action", 76);
    private final MovieDTO TEST_MOVIE_DTO = new MovieDTO("Avengers", "action", 76);

    private final MovieRepository movieRepository = mock(MovieRepository.class);
    private final MovieService testMovieService = new MovieServiceImpl(movieRepository);

    @Test
    void testGetMovieListShouldReturnListWithOneTestElement() {
        //Given
        Mockito.when(movieRepository.findAll()).thenReturn(List.of(TEST_MOVIE));
        List<MovieDTO> expected = List.of(TEST_MOVIE_DTO);

        //When
        List<MovieDTO> actual = testMovieService.getMovieList();

        //Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(movieRepository).findAll();
    }

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
    void testUpdateMovieShouldUpdateGivenMovieWhenInputIsValid() {
        // Given
        Mockito.when(movieRepository.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.of(TEST_MOVIE));

        // When
        testMovieService.updateMovie(TEST_MOVIE.getTitle(), TEST_MOVIE_DTO);

        // Then
        Mockito.verify(movieRepository).findByTitle(TEST_MOVIE.getTitle());
    }

    @Test
    void testDeleteMovieShouldRemoveGivenMovieWhenInputIsValid() {
        // Given
        Mockito.when(movieRepository.findByTitle(TEST_MOVIE.getTitle())).thenReturn(Optional.of(TEST_MOVIE));

        // When
        testMovieService.deleteMovie(TEST_MOVIE_DTO);

        // Then
        Mockito.verify(movieRepository).findByTitle(TEST_MOVIE.getTitle());
    }
}