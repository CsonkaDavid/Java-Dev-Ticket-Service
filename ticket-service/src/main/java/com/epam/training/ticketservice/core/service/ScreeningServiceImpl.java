package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.entity.Movie;
import com.epam.training.ticketservice.core.entity.Room;
import com.epam.training.ticketservice.core.entity.Screening;
import com.epam.training.ticketservice.core.model.MovieDTO;
import com.epam.training.ticketservice.core.model.RoomDTO;
import com.epam.training.ticketservice.core.model.ScreeningDTO;
import com.epam.training.ticketservice.core.repository.MovieRepository;
import com.epam.training.ticketservice.core.repository.RoomRepository;
import com.epam.training.ticketservice.core.repository.ScreeningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScreeningServiceImpl implements ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final RoomRepository roomRepository;
    private final MovieRepository movieRepository;

    @Override
    public void createScreening(ScreeningDTO screeningDTO) {
        Date date = Date.valueOf(screeningDTO.getFormattedDateTime());
        Movie movie = movieRepository.findByTitle(screeningDTO.getMovieTitle())
                .orElseThrow(() -> new IllegalArgumentException("There is no movie with the given name!"));

        Room room = roomRepository.findByName(screeningDTO.getRoomName())
                .orElseThrow(() -> new IllegalArgumentException("There is no room with the given name!"));

        screeningRepository.save(new Screening(null, movie, room, date));
    }

    @Override
    public void deleteScreening(MovieDTO movieDTO, RoomDTO roomDTO, Date date) {
        Movie movie = movieRepository.findByTitle(movieDTO.getTitle())
                .orElseThrow(() -> new IllegalArgumentException("There is no movie with the given name!"));

        Room room = roomRepository.findByName(roomDTO.getName())
                .orElseThrow(() -> new IllegalArgumentException("There is no room with the given name!"));

        screeningRepository.deleteByMovieAndRoomAndDate(movie, room, date);
    }

    @Override
    public List<ScreeningDTO> listMovies() {
        return screeningRepository.findAll().stream()
                .map(this::convertScreeningToDTO).collect(Collectors.toList());
    }

    private ScreeningDTO convertScreeningToDTO(Screening screening) {
        return new ScreeningDTO(screening.getMovie().getTitle(), screening.getRoom().getName(), screening.getDate().toString());
    }
}
