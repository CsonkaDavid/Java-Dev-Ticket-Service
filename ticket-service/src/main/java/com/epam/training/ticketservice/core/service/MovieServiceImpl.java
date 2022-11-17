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
                .orElseThrow(() -> new IllegalArgumentException("There is no movie with the given title!"));

        movieRepository.updateMovie(movie.getTitle(), movieDTO.getGenre(), movieDTO.getRunTime());
    }

    @Override
    public void deleteMovie(MovieDTO movieDTO) {
        Movie movie = movieRepository.findByTitle(movieDTO.getTitle())
                .orElseThrow(() -> new IllegalArgumentException("There is no movie with the given title!"));

        movieRepository.delete(movie);
    }

    @Override
    public Optional<MovieDTO> findMovieByTitle(String title) {
        return convertMovieToDTO(movieRepository.findByTitle(title));
    }

    @Override
    public List<MovieDTO> getMovieList() {
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

    private Optional<MovieDTO> convertMovieToDTO(Optional<Movie> movie) {
        return movie.isEmpty() ? Optional.empty() : Optional.of(convertMovieToDTO(movie.get()));
    }
}
