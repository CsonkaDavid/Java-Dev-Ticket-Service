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

    @SuppressWarnings("unused")
    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "create screening")
    public String createScreening(String movieTitle, String roomName, String formattedDateTime) {

        ScreeningDto screeningDto = new ScreeningDto(movieTitle, roomName, formattedDateTime, null);

        return screeningService.createScreening(screeningDto);
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

        ScreeningDto screeningDto = new ScreeningDto(movieTitle, roomName, formattedDateTime, null);

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
}
