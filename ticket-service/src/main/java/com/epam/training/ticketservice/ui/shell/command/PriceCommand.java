package com.epam.training.ticketservice.ui.shell.command;

import com.epam.training.ticketservice.core.entity.User;
import com.epam.training.ticketservice.core.model.PriceComponentDto;
import com.epam.training.ticketservice.core.model.UserDto;
import com.epam.training.ticketservice.core.service.BasePriceService;
import com.epam.training.ticketservice.core.service.PriceComponentService;
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
        priceComponentService.updateMoviePriceComponent(movieTitle, componentName);

        return "PriceComponent: " + componentName + " attached to Movie: " + movieTitle;
    }

    @SuppressWarnings("unused")
    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "attach price component to room")
    public String attachPriceToRoom(String componentName, String roomName) {
        priceComponentService.updateRoomPriceComponent(roomName, componentName);

        return "PriceComponent: " + componentName + " attached to Room: " + roomName;
    }

    @SuppressWarnings("unused")
    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "attach price component to screening")
    public String attachPriceToScreening(
            String componentName,
            String movieTitle,
            String roomName,
            String formattedDateTime) {

        priceComponentService.updateScreeningPriceComponent(movieTitle, roomName, formattedDateTime, componentName);

        return "PriceComponent: " + componentName + " attached to Screening: "
                + "(" + movieTitle + " in " + roomName + " at " + formattedDateTime + ")";
    }

    @SuppressWarnings("unused")
    private Availability isAdminInitiated() {
        Optional<UserDto> userDto = userService.getCurrentUser();

        return userDto.isPresent() && userDto.get().getRole() == User.Role.ADMIN
                ? Availability.available()
                : Availability.unavailable("You are not an admin!");
    }
}
