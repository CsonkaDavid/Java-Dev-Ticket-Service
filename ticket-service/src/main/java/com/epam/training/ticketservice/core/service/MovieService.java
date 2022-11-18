package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.model.MovieDto;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    void createMovie(MovieDto movieDTO);
    void updateMovie(String title, MovieDto movieDTO);
    void deleteMovie(MovieDto movieDTO);
    Optional<MovieDto> findMovieByTitle(String title);
    List<MovieDto> getMovieList();
}
