package com.epam.training.ticketservice.ui.shell.command;

import com.epam.training.ticketservice.core.dao.MovieDAO;
import com.epam.training.ticketservice.core.dao.UserDAO;
import com.epam.training.ticketservice.core.dto.MovieDTO;
import com.epam.training.ticketservice.core.dto.UserDTO;
import com.epam.training.ticketservice.core.service.MovieService;
import com.epam.training.ticketservice.core.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ShellComponent
@AllArgsConstructor
public class MovieCommand {

    private final MovieService movieService;
    private final UserService userService;

    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "create movie")
    public String createMovie(String title, String genre, Integer runTime) {
        MovieDTO movieDTO = new MovieDTO(title, genre, runTime);
        movieService.createMovie(movieDTO);

        return movieDTO.getTitle() + " created.";
    }

    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "update movie")
    public String updateMovie(String title, String newGenre, Integer newRunTime) {
        movieService.updateMovie(title, newGenre, newRunTime);

        return "Movie updated.";
    }

    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "list movies")
    public String listMovies() {
        List<MovieDTO> movieDAOList = movieService.listMovies();

        if(movieDAOList.isEmpty())
            return "There are no movies at the moment";

        return movieDAOList.stream().map(movieDTO -> movieDTO.getTitle() + "(" + movieDTO.getGenre() + ", " + movieDTO.getRunTime() + " minutes)")
                .collect(Collectors.joining("\n"));
    }

    private Availability isAdminInitiated() {
        Optional<UserDTO> userDTO = userService.getCurrentUser();

        return userDTO.isPresent() && userDTO.get().getRole() == UserDAO.Role.ADMIN
                ? Availability.available()
                : Availability.unavailable("You are not an admin!");
    }
}
