package com.epam.training.ticketservice.ui.shell.command;

import com.epam.training.ticketservice.core.entity.User;
import com.epam.training.ticketservice.core.model.MovieDto;
import com.epam.training.ticketservice.core.model.ScreeningDto;
import com.epam.training.ticketservice.core.model.UserDto;
import com.epam.training.ticketservice.core.service.MovieService;
import com.epam.training.ticketservice.core.service.ScreeningService;
import com.epam.training.ticketservice.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class ScreeningCommand {

    private final UserService userService;
    private final MovieService movieService;
    private final ScreeningService screeningService;

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
        ScreeningDto screeningDto = new ScreeningDto(movieTitle, roomName, formattedDateTime, null);

        return screeningService.deleteScreening(screeningDto);
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
