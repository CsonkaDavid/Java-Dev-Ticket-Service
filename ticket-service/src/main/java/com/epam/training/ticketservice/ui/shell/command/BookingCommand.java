package com.epam.training.ticketservice.ui.shell.command;

import com.epam.training.ticketservice.core.model.MovieDto;
import com.epam.training.ticketservice.core.model.RoomDto;
import com.epam.training.ticketservice.core.model.ScreeningDto;
import com.epam.training.ticketservice.core.model.UserDto;
import com.epam.training.ticketservice.core.service.BookingService;
import com.epam.training.ticketservice.core.service.MovieService;
import com.epam.training.ticketservice.core.service.RoomService;
import com.epam.training.ticketservice.core.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@AllArgsConstructor
public class BookingCommand {
    private final BookingService bookingService;
    private final RoomService roomService;
    private final MovieService movieService;
    private final UserService userService;

    @SuppressWarnings("unused")
    @ShellMethod(key = "book")
    public String createMovie(String movieTitle, String roomName, String formattedTime, String seats) {

        MovieDto movieDto = movieService.findMovieByTitle(movieTitle)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));

        RoomDto roomDto = roomService.findRoomByName(roomName)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        UserDto userDto = userService.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("User is not logged in"));

        ScreeningDto screeningDto = new ScreeningDto(movieDto.getTitle(), roomDto.getName(), formattedTime);

        return bookingService.book(userDto, screeningDto, seats);
    }
}
