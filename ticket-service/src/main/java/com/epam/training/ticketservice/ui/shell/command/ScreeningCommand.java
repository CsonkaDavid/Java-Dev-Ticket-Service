package com.epam.training.ticketservice.ui.shell.command;

import com.epam.training.ticketservice.core.entity.User;
import com.epam.training.ticketservice.core.model.MovieDTO;
import com.epam.training.ticketservice.core.model.RoomDTO;
import com.epam.training.ticketservice.core.model.ScreeningDTO;
import com.epam.training.ticketservice.core.model.UserDTO;
import com.epam.training.ticketservice.core.service.MovieService;
import com.epam.training.ticketservice.core.service.RoomService;
import com.epam.training.ticketservice.core.service.ScreeningService;
import com.epam.training.ticketservice.core.service.UserService;
import com.epam.training.ticketservice.core.timeformat.LocalDateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.Date;
import java.text.ParseException;
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
    private final LocalDateFormatter localDateFormatter;

    @SuppressWarnings("unused")
    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "create screening")
    public String createScreening(String movieTitle, String roomName, String formattedDateTime) throws ParseException {

        Date date = localDateFormatter.parseToDate(formattedDateTime)
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date to " + localDateFormatter.getPattern()));

        ScreeningDTO screeningDTO = new ScreeningDTO(movieTitle, roomName, formattedDateTime);
        screeningService.createScreening(screeningDTO);

        return screeningDTO + " created";
    }

    @SuppressWarnings("unused")
    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "delete screening")
    public String deleteScreening(String movieTitle, String roomName, String formattedDateTime) throws ParseException {
        MovieDTO movieDTO =  movieService.listMovies()
                .stream().filter(mDTO -> mDTO.getTitle().equals(movieTitle)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("There is no movie with the given name!"));

        RoomDTO roomDTO = roomService.listRooms()
                .stream().filter(rDTO -> rDTO.getName().equals(roomName)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("There is no room with the given name!"));

        Date date = localDateFormatter.parseToDate(formattedDateTime)
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date to " + localDateFormatter.getPattern()));

        screeningService.deleteScreening(movieDTO, roomDTO, date);

        return movieDTO.getTitle() + "(" + movieDTO.getGenre() + ", " + movieDTO.getRunTime() + " minutes), screened in room " +
                roomDTO.getName()+ ", at " + date.toString() +  " screening deleted";
    }

    @SuppressWarnings("unused")
    @ShellMethod(key = "list screenings")
    public String listScreenings() {
         List<ScreeningDTO> screeningDTOList = screeningService.listScreenings();

        if(screeningDTOList.isEmpty())
            return "There are no rooms at the moment";

        return screeningDTOList.stream().map(screeningDTO -> {

            MovieDTO movieDTO = movieService.listMovies()
                    .stream().filter(mDTO -> mDTO.getTitle().equals(screeningDTO.getMovieTitle())).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("There is no movie with the given name!"));

            return movieDTO.getTitle() + "(" + movieDTO.getGenre() + ", " + movieDTO.getRunTime() + " minutes), screened in room " +
                    screeningDTO.getRoomName() + ", at " + screeningDTO.getFormattedDateTime();

        }).collect(Collectors.joining("\n"));
    }

    @SuppressWarnings("unused")
    private Availability isAdminInitiated() {
        Optional<UserDTO> userDTO = userService.getCurrentUser();

        return userDTO.isPresent() && userDTO.get().getRole() == User.Role.ADMIN
                ? Availability.available()
                : Availability.unavailable("You are not an admin!");
    }
}
