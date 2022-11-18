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
    private final ApplicationDateFormatter applicationDateFormatter;
    private final ApplicationDateHandler applicationDateHandler;

    @SuppressWarnings("unused")
    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "create screening")
    public String createScreening(String movieTitle, String roomName, String formattedDateTime) {

        ScreeningDTO screeningDTO = new ScreeningDTO(movieTitle, roomName, formattedDateTime);

        MovieDTO movieDTO = movieService.findMovieByTitle(movieTitle)
                .orElseThrow(() -> new IllegalArgumentException("There is no movie with the given title!"));

        RoomDTO roomDTO = roomService.findRoomByName(roomName)
                .orElseThrow(() -> new IllegalArgumentException("There is no room with the given name!"));

        if(isOverlapping(screeningDTO, movieDTO, roomDTO, Optional.empty()))
            return "There is an overlapping screening";

        if(isOverlapping(screeningDTO, movieDTO, roomDTO, Optional.of(10)))
            return "This would start in the break period after another screening in this room";

        screeningService.createScreening(screeningDTO);

        return screeningDTO + " created";
    }

    @SuppressWarnings("unused")
    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "delete screening")
    public String deleteScreening(String movieTitle, String roomName, String formattedDateTime) {
        MovieDTO movieDTO = movieService.findMovieByTitle(movieTitle)
                .orElseThrow(() -> new IllegalArgumentException("There is no movie with the given title!"));

        RoomDTO roomDTO = roomService.findRoomByName(roomName)
                .orElseThrow(() -> new IllegalArgumentException("There is no room with the given name!"));


        Date date = applicationDateFormatter.parseStringToDate(formattedDateTime)
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date to " + applicationDateFormatter.getPattern()));

        screeningService.deleteScreening(movieDTO, roomDTO, date);

        return movieDTO.getTitle() + " (" + movieDTO.getGenre() + ", " + movieDTO.getRunTime() + " minutes), screened in room " +
                roomDTO.getName()+ ", at " + date.toString() +  " screening deleted";
    }

    @SuppressWarnings("unused")
    @ShellMethod(key = "list screenings")
    public String listScreenings() {
         List<ScreeningDTO> screeningDTOList = screeningService.getScreeningList();

        if(screeningDTOList.isEmpty())
            return "There are no screenings";

        return screeningDTOList.stream()
                .sorted(
                        (screeningDTO1, screeningDTO2) -> {
                            Date date1 = applicationDateFormatter.parseStringToDate(screeningDTO1.getFormattedDateTime())
                                    .orElseThrow(() -> new IllegalArgumentException("Can't parse date to " + applicationDateFormatter.getPattern()));
                            Date date2 = applicationDateFormatter.parseStringToDate(screeningDTO2.getFormattedDateTime())
                                    .orElseThrow(() -> new IllegalArgumentException("Can't parse date to " + applicationDateFormatter.getPattern()));

                            return date1.compareTo(date2);
                        })
                .map(screeningDTO -> {
                    MovieDTO movieDTO = movieService.findMovieByTitle(screeningDTO.getMovieTitle())
                            .orElseThrow(() -> new IllegalArgumentException("There is no movie with the given title!"));

                    return movieDTO.getTitle() + " (" + movieDTO.getGenre() + ", " + movieDTO.getRunTime() + " minutes), screened in room " +
                            screeningDTO.getRoomName() + ", at " + screeningDTO.getFormattedDateTime();

                })

                .collect(Collectors.joining("\n"));
    }

    @SuppressWarnings("unused")
    private Availability isAdminInitiated() {
        Optional<UserDTO> userDTO = userService.getCurrentUser();

        return userDTO.isPresent() && userDTO.get().getRole() == User.Role.ADMIN
                ? Availability.available()
                : Availability.unavailable("You are not an admin!");
    }

    private boolean isOverlapping(ScreeningDTO screeningDTO, MovieDTO movieDTO, RoomDTO roomDTO, Optional<Integer> breakPeriod) {
        int breakPeriodAmount = breakPeriod.orElse(0);

        Date currentMovieDate = applicationDateFormatter.parseStringToDate(screeningDTO.getFormattedDateTime())
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date to " + applicationDateFormatter.getPattern()));

        Date currentMovieEndingDate = applicationDateHandler.addMinutesToDate(currentMovieDate, movieDTO.getRunTime() + breakPeriodAmount);

        Optional<ScreeningDTO> existingScreeningDTO = screeningService.getScreeningList()
                .stream()
                .filter(sDTO -> sDTO.getRoomName().equals(roomDTO.getName()))
                .filter(sDTO -> {

                    Date checkedMovieDate = applicationDateFormatter.parseStringToDate(sDTO.getFormattedDateTime()).orElseThrow(
                            () -> new IllegalArgumentException("There is no screening with the given parameters!"));

                    MovieDTO checkedMovieDTO = movieService.findMovieByTitle(sDTO.getMovieTitle())
                            .orElseThrow(() -> new IllegalArgumentException("There is no movie with the given title!"));

                    Date checkedMovieEnding = applicationDateHandler.addMinutesToDate(checkedMovieDate, checkedMovieDTO.getRunTime() + breakPeriodAmount);

                    boolean beginsAfter = false;
                    boolean endsBefore = false;

                    if((currentMovieDate.compareTo(checkedMovieDate) >= 0) && (currentMovieDate.compareTo(checkedMovieEnding) >= 0))
                        beginsAfter = true;

                    if((currentMovieDate.compareTo(checkedMovieDate) <= 0) && (currentMovieEndingDate.compareTo(checkedMovieDate) <= 0))
                        endsBefore = true;

                    return !(beginsAfter || endsBefore);
                })
                .findFirst();

        return existingScreeningDTO.isPresent();
    }
}
