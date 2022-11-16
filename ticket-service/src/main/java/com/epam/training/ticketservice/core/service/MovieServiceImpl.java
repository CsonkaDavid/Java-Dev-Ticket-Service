package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.dao.MovieDAO;
import com.epam.training.ticketservice.core.dto.MovieDTO;
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
        MovieDAO movieDAO = new MovieDAO(
                null,
                movieDTO.getTitle(),
                movieDTO.getGenre(),
                movieDTO.getRunTime()
        );

        movieRepository.save(movieDAO);
    }

    @Override
    public void updateMovie(String title, String newGenre, Integer newRunTime) {
        Optional<MovieDAO> movieDAO = movieRepository.findByTitle(title);

        if(movieDAO.isEmpty()) return;

        movieRepository.updateMovie(movieDAO.get().getTitle(), newGenre, newRunTime);
    }

    @Override
    public void deleteMovie(String title) {
        if(movieRepository.findByTitle(title).isEmpty()) return;

        movieRepository.deleteByTitle(title);
    }

    @Override
    public List<MovieDTO> listMovies() {
        return movieRepository.findAll().stream()
                .map(this::convertMovieToDTO)
                .collect(Collectors.toList());
    }

    private MovieDTO convertMovieToDTO(MovieDAO movieDAO) {
        return new MovieDTO(
                movieDAO.getTitle(),
                movieDAO.getGenre(),
                movieDAO.getRunTime()
        );
    }
}
