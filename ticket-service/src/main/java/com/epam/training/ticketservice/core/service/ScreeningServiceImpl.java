package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.entity.Movie;
import com.epam.training.ticketservice.core.entity.Room;
import com.epam.training.ticketservice.core.entity.Screening;
import com.epam.training.ticketservice.core.model.MovieDto;
import com.epam.training.ticketservice.core.model.RoomDto;
import com.epam.training.ticketservice.core.model.ScreeningDto;
import com.epam.training.ticketservice.core.repository.MovieRepository;
import com.epam.training.ticketservice.core.repository.RoomRepository;
import com.epam.training.ticketservice.core.repository.ScreeningRepository;
import com.epam.training.ticketservice.core.time.ApplicationDateFormatter;
import com.epam.training.ticketservice.core.time.ApplicationDateHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScreeningServiceImpl implements ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final RoomRepository roomRepository;
    private final MovieRepository movieRepository;
    private final ApplicationDateFormatter dateFormatter;
    private final ApplicationDateHandler dateHandler;

    @Override
    public String createScreening(ScreeningDto screeningDto) {

        Date date = dateFormatter.parseStringToDate(screeningDto.getFormattedDateTime())
                .orElseThrow(() ->
                        new IllegalArgumentException("Can't parse date to "
                                + dateFormatter.getPattern()));

        Movie movie = movieRepository.findByTitle(screeningDto.getMovieTitle())
                .orElseThrow(() ->
                        new IllegalArgumentException("There is no movie with the given name!"));

        Room room = roomRepository.findByName(screeningDto.getRoomName())
                .orElseThrow(() ->
                        new IllegalArgumentException("There is no room with the given name!"));

        if (isOverlapping(date, movie, room, Optional.empty())) {
            return "There is an overlapping screening";
        }

        if (isOverlapping(date, movie, room, Optional.of(10))) {
            return "This would start in the break period after another screening in this room";
        }

        screeningRepository.save(new Screening(null, movie, room, date, null));

        return screeningDto + " created";
    }

    @Override
    public String deleteScreening(ScreeningDto screeningDto) {
        String movieTitle = screeningDto.getMovieTitle();
        String roomName = screeningDto.getRoomName();
        String formattedDateTime = screeningDto.getFormattedDateTime();

        Movie movie = movieRepository.findByTitle(movieTitle)
                .orElseThrow(() -> new IllegalArgumentException("There is no movie with the given name!"));

        Room room = roomRepository.findByName(roomName)
                .orElseThrow(() -> new IllegalArgumentException("There is no room with the given name!"));

        Date date = dateFormatter.parseStringToDate(formattedDateTime)
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date to "
                        + dateFormatter.getPattern()));

        Screening screening = screeningRepository.findByMovieAndRoomAndDate(movie, room, date)
                .orElseThrow(() -> new IllegalArgumentException("There is no screening with these parameters!"));

        screeningRepository.delete(screening);

        return movieTitle + " (" + movie.getGenre() + ", " + movie.getRunTime()
                + " minutes), screened in room " + roomName + ", at " + formattedDateTime
                +  " screening deleted";
    }

    @Override
    public List<ScreeningDto> getScreeningList() {
        return screeningRepository.findAll().stream()
                .map(this::convertScreeningToDto).collect(Collectors.toList());
    }

    @Override
    public Optional<ScreeningDto> findScreeningByMovieAndRoomAndDate(
            MovieDto movieDto,
            RoomDto roomDto,
            String dateTime) {

        Movie movie = movieRepository.findByTitle(movieDto.getTitle())
                .orElseThrow(() -> new IllegalArgumentException("There is no movie with the given name!"));

        Room room = roomRepository.findByName(roomDto.getName())
                .orElseThrow(() -> new IllegalArgumentException("There is no room with the given name!"));

        Date date = dateFormatter.parseStringToDate(dateTime)
                .orElseThrow(() ->
                        new IllegalArgumentException("Can't parse date to "
                                + dateFormatter.getPattern()));

        Optional<Screening> screening = screeningRepository.findByMovieAndRoomAndDate(movie, room, date);

        if (screening.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new ScreeningDto(
                screening.get().getMovie().getTitle(),
                screening.get().getRoom().getName(),
                dateFormatter.convertDateToString(screening.get().getDate()),
                screening.get().getPriceComponent() == null ? 0 : screening.get().getPriceComponent().getAmount()
                ));
    }

    private ScreeningDto convertScreeningToDto(Screening screening) {

        String formattedDate = dateFormatter.convertDateToString(screening.getDate());

        return new ScreeningDto(
                screening.getMovie().getTitle(),
                screening.getRoom().getName(),
                formattedDate,
                screening.getPriceComponent() == null ? 0 : screening.getPriceComponent().getAmount());
    }

    private boolean isOverlapping(
            Date screeningDate,
            Movie movie,
            Room room,
            Optional<Integer> breakPeriod) {

        int breakPeriodAmount = breakPeriod.orElse(0);

        Date screeningEndDate = dateHandler
                .addMinutesToDate(screeningDate, movie.getRunTime() + breakPeriodAmount);

        Optional<Screening> overlappingScreening = screeningRepository.findAll()
                .stream()
                .filter(s -> s.getRoom().getName().equals(room.getName()))
                .filter(s -> {

                    Date checkedScreeningDate = s.getDate();

                    Movie checkedMovie = s.getMovie();

                    Date checkedScreeningEndDate = dateHandler
                            .addMinutesToDate(checkedScreeningDate, checkedMovie.getRunTime() + breakPeriodAmount);

                    boolean beginsAfter = false;
                    boolean endsBefore = false;

                    if ((screeningDate.compareTo(checkedScreeningDate) >= 0)
                            && (screeningDate.compareTo(checkedScreeningEndDate) >= 0)) {

                        beginsAfter = true;
                    }

                    if ((screeningDate.compareTo(checkedScreeningDate) <= 0)
                            && (screeningEndDate.compareTo(checkedScreeningDate) <= 0)) {

                        endsBefore = true;
                    }

                    return !(beginsAfter || endsBefore);
                })
                .findFirst();

        return overlappingScreening.isPresent();
    }
}
