package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.dto.MovieDTO;

import java.util.List;

public interface MovieService {
    void createMovie(MovieDTO movieDTO);
    void updateMovie(String title, String newGenre, Integer newRunTime);
    void deleteMovie(String title);
    List<MovieDTO> listMovies();
}
