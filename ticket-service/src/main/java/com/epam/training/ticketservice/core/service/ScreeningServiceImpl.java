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
import com.epam.training.ticketservice.core.time.ApplicationDateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScreeningServiceImpl implements ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final RoomRepository roomRepository;
    private final MovieRepository movieRepository;
    private final ApplicationDateFormatter applicationDateFormatter;

    @Override
    public void createScreening(ScreeningDTO screeningDTO) {

        Date date = applicationDateFormatter.parseStringToDate(screeningDTO.getFormattedDateTime())
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date to " + applicationDateFormatter.getPattern()));

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

        Screening screening = screeningRepository.findByMovieAndRoomAndDate(movie, room, date)
                .orElseThrow(() -> new IllegalArgumentException("There is no screening with these parameters!"));

        screeningRepository.delete(screening);
    }

    @Override
    public List<ScreeningDTO> getScreeningList() {
        return screeningRepository.findAll().stream()
                .map(this::convertScreeningToDTO).collect(Collectors.toList());
    }

    private ScreeningDTO convertScreeningToDTO(Screening screening) {

        String formattedDate = applicationDateFormatter.convertDateToString(screening.getDate());

        return new ScreeningDTO(screening.getMovie().getTitle(), screening.getRoom().getName(), formattedDate);
    }
}
