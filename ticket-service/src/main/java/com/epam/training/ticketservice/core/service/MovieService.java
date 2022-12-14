package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.model.MovieDto;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    void createMovie(MovieDto movieDto);

    void updateMovie(MovieDto movieDto);

    void deleteMovie(String title);

    Optional<MovieDto> findMovieByTitle(String title);

    List<MovieDto> getMovieList();
}
