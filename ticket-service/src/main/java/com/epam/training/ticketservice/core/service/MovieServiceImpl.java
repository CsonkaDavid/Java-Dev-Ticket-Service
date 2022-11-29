package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.entity.Movie;
import com.epam.training.ticketservice.core.model.MovieDto;
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
    public void createMovie(MovieDto movieDto) {
        movieRepository.save(new Movie(
                null,
                movieDto.getTitle(),
                movieDto.getGenre(),
                movieDto.getRunTime(),
                null
        ));
    }

    @Override
    public void updateMovie(MovieDto movieDto) {
        Movie movie = movieRepository.findByTitle(movieDto.getTitle())
                .orElseThrow(() -> new IllegalArgumentException("There is no movie with the given name!"));

        movieRepository.updateMovie(movie.getTitle(), movieDto.getGenre(), movieDto.getRunTime());
    }

    @Override
    public void deleteMovie(String movieTitle) {
        Movie movie = movieRepository.findByTitle(movieTitle)
                .orElseThrow(() -> new IllegalArgumentException("There is no movie with the given title!"));

        movieRepository.delete(movie);
    }

    @Override
    public Optional<MovieDto> findMovieByTitle(String title) {
        return convertMovieToDto(movieRepository.findByTitle(title));
    }

    @Override
    public List<MovieDto> getMovieList() {
        return movieRepository.findAll().stream()
                .map(this::convertMovieToDto)
                .collect(Collectors.toList());
    }

    private MovieDto convertMovieToDto(Movie movie) {
        return new MovieDto(
                movie.getTitle(),
                movie.getGenre(),
                movie.getRunTime(),
                movie.getPriceComponent() == null ? 0 : movie.getPriceComponent().getAmount()
        );
    }

    private Optional<MovieDto> convertMovieToDto(Optional<Movie> movie) {
        return movie.isEmpty() ? Optional.empty() : Optional.of(convertMovieToDto(movie.get()));
    }
}
