package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.entity.Movie;
import com.epam.training.ticketservice.core.model.MovieDTO;
import com.epam.training.ticketservice.core.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    public void createMovie(MovieDTO movieDTO) {
        movieRepository.save(new Movie(
                null,
                movieDTO.getTitle(),
                movieDTO.getGenre(),
                movieDTO.getRunTime()
        ));
    }

    @Override
    public void updateMovie(String title, MovieDTO movieDTO) {
        Movie movie = movieRepository.findByTitle(title)
                .orElseThrow(() -> new IllegalArgumentException("There is no movie with the given name!"));

        movieRepository.updateMovie(movie.getTitle(), movieDTO.getGenre(), movieDTO.getRunTime());
    }

    @Override
    public void deleteMovie(MovieDTO movieDTO) {
        Movie movie = movieRepository.findByTitle(movieDTO.getTitle())
                .orElseThrow(() -> new IllegalArgumentException("There is no movie with the given name!"));

        movieRepository.delete(movie);
    }

    @Override
    public List<MovieDTO> listMovies() {
        return movieRepository.findAll().stream()
                .map(this::convertMovieToDTO)
                .collect(Collectors.toList());
    }

    private MovieDTO convertMovieToDTO(Movie movieDAO) {
        return new MovieDTO(
                movieDAO.getTitle(),
                movieDAO.getGenre(),
                movieDAO.getRunTime()
        );
    }
}
