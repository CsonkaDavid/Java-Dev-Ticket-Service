package com.epam.training.ticketservice.ui.shell.command;

import com.epam.training.ticketservice.core.entity.User;
import com.epam.training.ticketservice.core.model.MovieDTO;
import com.epam.training.ticketservice.core.model.UserDTO;
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
        MovieDTO movieDTO = new MovieDTO(title, genre, runTime);
        movieService.createMovie(movieDTO);

        return movieDTO + " created";
    }

    @SuppressWarnings("unused")
    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "update movie")
    public String updateMovie(String title, String genre, Integer runTime) {
        MovieDTO movieDTO = new MovieDTO(title, genre, runTime);

        movieService.updateMovie(title, movieDTO);

        return movieDTO + " updated";
    }

    @SuppressWarnings("unused")
    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "delete movie")
    public String deleteMovie(String title) {
        MovieDTO movieDTO = movieService.findMovieByTitle(title)
                .orElseThrow(() -> new IllegalArgumentException("There is no movie with the given title!"));

        movieService.deleteMovie(movieDTO);

        return movieDTO +  " deleted";
    }

    @SuppressWarnings("unused")
    @ShellMethod(key = "list movies")
    public String listMovies() {
        List<MovieDTO> movieDAOList = movieService.getMovieList();

        if(movieDAOList.isEmpty())
            return "There are no movies at the moment";

        return movieDAOList.stream().map(
                movieDTO -> movieDTO.getTitle() + " (" + movieDTO.getGenre() + ", " + movieDTO.getRunTime() + " minutes)")
                .collect(Collectors.joining("\n"));
    }

    @SuppressWarnings("unused")
    public Availability isAdminInitiated() {
        Optional<UserDTO> userDTO = userService.getCurrentUser();

        return userDTO.isPresent() && userDTO.get().getRole() == User.Role.ADMIN
                ? Availability.available()
                : Availability.unavailable("You are not an admin!");
    }
}
