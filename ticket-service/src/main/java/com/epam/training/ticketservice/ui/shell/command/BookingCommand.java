package com.epam.training.ticketservice.ui.shell.command;

import com.epam.training.ticketservice.core.entity.User;
import com.epam.training.ticketservice.core.model.MovieDto;
import com.epam.training.ticketservice.core.model.RoomDto;
import com.epam.training.ticketservice.core.model.ScreeningDto;
import com.epam.training.ticketservice.core.model.UserDto;
import com.epam.training.ticketservice.core.service.BookingService;
import com.epam.training.ticketservice.core.service.MovieService;
import com.epam.training.ticketservice.core.service.RoomService;
import com.epam.training.ticketservice.core.service.ScreeningService;
import com.epam.training.ticketservice.core.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ShellComponent
@AllArgsConstructor
public class BookingCommand {
    private final BookingService bookingService;
    private final ScreeningService screeningService;
    private final RoomService roomService;
    private final MovieService movieService;
    private final UserService userService;

    @SuppressWarnings("unused")
    @ShellMethodAvailability("isUserInitiated")
    @ShellMethod(key = "book")
    public String book(String movieTitle, String roomName, String formattedTime, String seats) {

        Optional<UserDto> userDto = userService.getCurrentUser();

        if (userDto.isEmpty()) {
            return "User is not logged in";
        }

        ScreeningDto screeningDto = new ScreeningDto(movieTitle, roomName, formattedTime, 0);

        return bookingService.book(userDto.get(), screeningDto, seats);
    }

    @SuppressWarnings("unused")
    @ShellMethod(key = "show price for")
    public String showPriceForBooking(String movieTitle, String roomName, String formattedTime, String seats) {

        MovieDto movieDto = movieService.findMovieByTitle(movieTitle)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));

        RoomDto roomDto = roomService.findRoomByName(roomName)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        ScreeningDto screeningDto = screeningService
                .findScreeningByMovieAndRoomAndDate(movieDto, roomDto, formattedTime)
                .orElseThrow(() -> new IllegalArgumentException("Screening with these parameters does not exist!"));

        List<String> splitStringList = Arrays.stream(seats.split(" "))
                .collect(Collectors.toList());

        int price = bookingService.calculateBookingPrice(
                movieDto.getPriceComponent(),
                roomDto.getPriceComponent(),
                screeningDto.getPriceComponent(),
                splitStringList.size()
        );

        return "The price for this booking would be " + price + " HUF";
    }

    @SuppressWarnings("unused")
    private Availability isUserInitiated() {
        Optional<UserDto> userDto = userService.getCurrentUser();

        return userDto.isPresent() && userDto.get().getRole() == User.Role.USER
                ? Availability.available()
                : Availability.unavailable("You are an admin!");
    }
}
