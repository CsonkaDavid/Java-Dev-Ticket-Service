package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.model.MovieDTO;

import java.util.List;

public interface MovieService {
    void createMovie(MovieDTO movieDTO);
    void updateMovie(String title, MovieDTO movieDTO);
    void deleteMovie(MovieDTO movieDTO);
    List<MovieDTO> listMovies();
}
