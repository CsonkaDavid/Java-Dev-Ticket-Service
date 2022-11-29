package com.epam.training.ticketservice.ui.shell.command;

import com.epam.training.ticketservice.core.entity.User;
import com.epam.training.ticketservice.core.model.MovieDto;
import com.epam.training.ticketservice.core.model.UserDto;
import com.epam.training.ticketservice.core.service.MovieService;
import com.epam.training.ticketservice.core.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ShellComponent
@AllArgsConstructor
public class MovieCommand {

    private final MovieService movieService;
    private final UserService userService;

    @SuppressWarnings("unused")
    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "create movie")
    public String createMovie(String title, String genre, Integer runTime) {
        MovieDto movieDto = new MovieDto(title, genre, runTime, null);
        movieService.createMovie(movieDto);

        return movieDto + " created";
    }

    @SuppressWarnings("unused")
    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "update movie")
    public String updateMovie(String title, String genre, Integer runTime) {
        MovieDto movieDto = new MovieDto(title, genre, runTime, null);

        movieService.updateMovie(movieDto);

        return movieDto + " updated";
    }

    @SuppressWarnings("unused")
    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "delete movie")
    public String deleteMovie(String title) {
        movieService.deleteMovie(title);

        return title +  " deleted";
    }

    @SuppressWarnings("unused")
    @ShellMethod(key = "list movies")
    public String listMovies() {
        List<MovieDto> movieDtoList = movieService.getMovieList();

        if (movieDtoList.isEmpty()) {
            return "There are no movies at the moment";
        }

        return movieDtoList
                .stream()
                .map(m -> m.getTitle()
                        + " ("
                        + m.getGenre()
                        + ", "
                        + m.getRunTime()
                        + " minutes)")
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
