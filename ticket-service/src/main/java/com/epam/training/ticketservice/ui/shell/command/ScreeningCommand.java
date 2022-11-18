package com.epam.training.ticketservice.ui.shell.command;

import com.epam.training.ticketservice.core.entity.User;
import com.epam.training.ticketservice.core.model.MovieDto;
import com.epam.training.ticketservice.core.model.RoomDto;
import com.epam.training.ticketservice.core.model.ScreeningDto;
import com.epam.training.ticketservice.core.model.UserDto;
import com.epam.training.ticketservice.core.service.MovieService;
import com.epam.training.ticketservice.core.service.RoomService;
import com.epam.training.ticketservice.core.service.ScreeningService;
import com.epam.training.ticketservice.core.service.UserService;
import com.epam.training.ticketservice.core.time.ApplicationDateFormatter;
import com.epam.training.ticketservice.core.time.ApplicationDateHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class ScreeningCommand {

    private final UserService userService;
    private final MovieService movieService;
    private final RoomService roomService;
    private final ScreeningService screeningService;
    private final ApplicationDateFormatter dateFormatter;
    private final ApplicationDateHandler dateHandler;

    @SuppressWarnings("unused")
    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "create screening")
    public String createScreening(String movieTitle, String roomName, String formattedDateTime) {

        ScreeningDto screeningDto = new ScreeningDto(movieTitle, roomName, formattedDateTime);

        MovieDto movieDto = movieService.findMovieByTitle(movieTitle)
                .orElseThrow(() -> new IllegalArgumentException("There is no movie with the given title!"));

        RoomDto roomDto = roomService.findRoomByName(roomName)
                .orElseThrow(() -> new IllegalArgumentException("There is no room with the given name!"));

        if (isOverlapping(screeningDto, movieDto, roomDto, Optional.empty())) {
            return "There is an overlapping screening";
        }

        if (isOverlapping(screeningDto, movieDto, roomDto, Optional.of(10))) {
            return "This would start in the break period after another screening in this room";
        }

        screeningService.createScreening(screeningDto);

        return screeningDto + " created";
    }

    @SuppressWarnings("unused")
    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "delete screening")
    public String deleteScreening(String movieTitle, String roomName, String formattedDateTime) {
        MovieDto movieDto = movieService.findMovieByTitle(movieTitle)
                .orElseThrow(() -> new IllegalArgumentException("There is no movie with the given title!"));

        RoomDto roomDto = roomService.findRoomByName(roomName)
                .orElseThrow(() -> new IllegalArgumentException("There is no room with the given name!"));


        Date date = dateFormatter.parseStringToDate(formattedDateTime)
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date to " + dateFormatter.getPattern()));

        screeningService.deleteScreening(movieDto, roomDto, date);

        return movieDto.getTitle() + " (" + movieDto.getGenre() + ", " + movieDto.getRunTime()
                + " minutes), screened in room " + roomDto.getName() + ", at " + date.toString()
                +  " screening deleted";
    }

    @SuppressWarnings("unused")
    @ShellMethod(key = "list screenings")
    public String listScreenings() {
        List<ScreeningDto> screeningDtoList = screeningService.getScreeningList();

        if (screeningDtoList.isEmpty()) {
            return "There are no screenings";
        }

        return screeningDtoList.stream()
                .map(s -> {
                    MovieDto movieDto = movieService.findMovieByTitle(s.getMovieTitle())
                            .orElseThrow(() -> new IllegalArgumentException("There is no movie with the given title!"));

                    return movieDto.getTitle() + " (" + movieDto.getGenre() + ", " + movieDto.getRunTime()
                            + " minutes), screened in room " + s.getRoomName() + ", at " + s.getFormattedDateTime();

                })
                .collect(Collectors.joining("\n"));
    }

    @SuppressWarnings("unused")
    private Availability isAdminInitiated() {
        Optional<UserDto> userDto = userService.getCurrentUser();

        return userDto.isPresent() && userDto.get().getRole() == User.Role.ADMIN
                ? Availability.available()
                : Availability.unavailable("You are not an admin!");
    }

    private boolean isOverlapping(
            ScreeningDto screeningDto,
            MovieDto movieDto,
            RoomDto roomDto,
            Optional<Integer> breakPeriod) {

        int breakPeriodAmount = breakPeriod.orElse(0);

        Date currentMovieDate = dateFormatter.parseStringToDate(screeningDto.getFormattedDateTime())
                .orElseThrow(() ->
                        new IllegalArgumentException("Can't parse date to " + dateFormatter.getPattern()));


        Date currentMovieEndingDate = dateHandler
                .addMinutesToDate(currentMovieDate, movieDto.getRunTime() + breakPeriodAmount);

        Optional<ScreeningDto> overlappingScreeningDto = screeningService.getScreeningList()
                .stream()
                .filter(s -> s.getRoomName().equals(roomDto.getName()))
                .filter(s -> {

                    Date checkedMovieDate = dateFormatter.parseStringToDate(s.getFormattedDateTime())
                            .orElseThrow(() ->
                                    new IllegalArgumentException("There is no screening with the given parameters!"));

                    MovieDto checkedMovieDto = movieService.findMovieByTitle(s.getMovieTitle())
                            .orElseThrow(() ->
                                    new IllegalArgumentException("There is no movie with the given title!"));

                    Date checkedMovieEnding = dateHandler
                            .addMinutesToDate(checkedMovieDate, checkedMovieDto.getRunTime() + breakPeriodAmount);

                    boolean beginsAfter = false;
                    boolean endsBefore = false;

                    if ((currentMovieDate.compareTo(checkedMovieDate) >= 0)
                            && (currentMovieDate.compareTo(checkedMovieEnding) >= 0)) {

                        beginsAfter = true;
                    }

                    if ((currentMovieDate.compareTo(checkedMovieDate) <= 0)
                            && (currentMovieEndingDate.compareTo(checkedMovieDate) <= 0)) {

                        endsBefore = true;
                    }

                    return !(beginsAfter || endsBefore);
                })
                .findFirst();

        return overlappingScreeningDto.isPresent();
    }
}
