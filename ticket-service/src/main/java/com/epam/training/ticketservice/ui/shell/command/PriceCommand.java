package com.epam.training.ticketservice.ui.shell.command;

import com.epam.training.ticketservice.core.entity.User;
import com.epam.training.ticketservice.core.model.MovieDto;
import com.epam.training.ticketservice.core.model.PriceComponentDto;
import com.epam.training.ticketservice.core.model.RoomDto;
import com.epam.training.ticketservice.core.model.ScreeningDto;
import com.epam.training.ticketservice.core.model.UserDto;
import com.epam.training.ticketservice.core.service.BasePriceService;
import com.epam.training.ticketservice.core.service.MovieService;
import com.epam.training.ticketservice.core.service.PriceComponentService;
import com.epam.training.ticketservice.core.service.RoomService;
import com.epam.training.ticketservice.core.service.ScreeningService;
import com.epam.training.ticketservice.core.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.Optional;

@ShellComponent
@AllArgsConstructor
public class PriceCommand {

    private final BasePriceService basePriceService;
    private final PriceComponentService priceComponentService;
    private final UserService userService;
    private final MovieService movieService;
    private final RoomService roomService;
    private final ScreeningService screeningService;

    @SuppressWarnings("unused")
    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "update base price")
    public String updateBasePrice(Integer amount) {
        basePriceService.updateBasePrice(amount);

        return "Base price updated to: " + amount + " HUF";
    }

    @SuppressWarnings("unused")
    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "create price component")
    public String createPriceComponent(String name, Integer amount) {
        PriceComponentDto priceComponentDto = new PriceComponentDto(name, amount);

        return priceComponentService.createPriceComponent(priceComponentDto);
    }


    @SuppressWarnings("unused")
    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "attach price component to movie")
    public String attachPriceToMovie(String componentName, String movieTitle) {
        MovieDto movieDto = movieService.findMovieByTitle(movieTitle)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));

        PriceComponentDto priceComponentDto = priceComponentService.findPriceComponentByName(componentName)
                .orElseThrow(() -> new IllegalArgumentException("PriceComponent not found"));

        priceComponentService.updateMoviePriceComponent(movieDto, priceComponentDto);

        return "PriceComponent: " + priceComponentDto + " attached to Movie: " + movieDto;
    }

    @SuppressWarnings("unused")
    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "attach price component to room")
    public String attachPriceToRoom(String componentName, String roomName) {
        RoomDto roomDto = roomService.findRoomByName(roomName)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        PriceComponentDto priceComponentDto = priceComponentService.findPriceComponentByName(componentName)
                .orElseThrow(() -> new IllegalArgumentException("PriceComponent not found"));

        priceComponentService.updateRoomPriceComponent(roomDto, priceComponentDto);

        return "PriceComponent: " + priceComponentDto + " attached to Room: " + roomDto;
    }

    @SuppressWarnings("unused")
    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "attach price component to screening")
    public String attachPriceToScreening(
            String componentName,
            String movieTitle,
            String roomName,
            String formattedDateTime) {

        MovieDto movieDto = movieService.findMovieByTitle(movieTitle)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));

        RoomDto roomDto = roomService.findRoomByName(roomName)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        ScreeningDto screeningDto = screeningService.findScreeningByMovieAndRoomAndDate(
                movieDto,
                roomDto,
                formattedDateTime
        ).orElseThrow(() -> new IllegalArgumentException("Screening not found"));

        PriceComponentDto priceComponentDto = priceComponentService.findPriceComponentByName(componentName)
                .orElseThrow(() -> new IllegalArgumentException("PriceComponent not found"));

        priceComponentService.updateScreeningPriceComponent(screeningDto, priceComponentDto);

        return "PriceComponent: " + priceComponentDto + " attached to Screening: " + screeningDto;
    }

    @SuppressWarnings("unused")
    public Availability isAdminInitiated() {
        Optional<UserDto> userDto = userService.getCurrentUser();

        return userDto.isPresent() && userDto.get().getRole() == User.Role.ADMIN
                ? Availability.available()
                : Availability.unavailable("You are not an admin!");
    }
}
