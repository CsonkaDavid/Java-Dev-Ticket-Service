package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.model.MovieDTO;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    void createMovie(MovieDTO movieDTO);
    void updateMovie(String title, MovieDTO movieDTO);
    void deleteMovie(MovieDTO movieDTO);
    Optional<MovieDTO> findMovieByTitle(String title);
    List<MovieDTO> getMovieList();
}
