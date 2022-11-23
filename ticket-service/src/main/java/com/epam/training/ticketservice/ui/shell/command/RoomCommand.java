package com.epam.training.ticketservice.ui.shell.command;

import com.epam.training.ticketservice.core.entity.User;
import com.epam.training.ticketservice.core.model.RoomDto;
import com.epam.training.ticketservice.core.model.UserDto;
import com.epam.training.ticketservice.core.service.RoomService;
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
public class RoomCommand {

    private UserService userService;
    private RoomService roomService;

    @SuppressWarnings("unused")
    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "create room")
    public String createRoom(String name, Integer rows, Integer columns) {
        RoomDto roomDto = new RoomDto(name, rows, columns, null);
        roomService.createRoom(roomDto);

        return roomDto + " created";
    }

    @SuppressWarnings("unused")
    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "update room")
    public String updateRoom(String name, Integer rows, Integer columns) {
        RoomDto updateDto = new RoomDto(name, rows, columns, null);

        roomService.updateRoom(name, updateDto);

        return name + " updated";
    }

    @SuppressWarnings("unused")
    @ShellMethodAvailability("isAdminInitiated")
    @ShellMethod(key = "delete room")
    public String deleteRoom(String name) {
        RoomDto roomDto = roomService.getRoomList()
                .stream()
                .filter(r -> r.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("There is no room with the given name!"));

        roomService.deleteRoom(roomDto);

        return roomDto +  " deleted";
    }

    @SuppressWarnings("unused")
    @ShellMethod(key = "list rooms")
    public String listRooms() {
        List<RoomDto> roomDtoList = roomService.getRoomList();

        if (roomDtoList.isEmpty()) {
            return "There are no rooms at the moment";
        }

        return roomDtoList.stream().map(roomDto -> {

            Integer rows = roomDto.getRows();
            Integer columns = roomDto.getColumns();

            return "Room " + roomDto.getName() + " with " + rows * columns
                    + " seats, " + rows + " rows and " + columns + " columns";

        }).collect(Collectors.joining("\n"));
    }

    @SuppressWarnings("unused")
    private Availability isAdminInitiated() {
        Optional<UserDto> userDto = userService.getCurrentUser();

        return userDto.isPresent() && userDto.get().getRole() == User.Role.ADMIN
                ? Availability.available()
                : Availability.unavailable("You are not an admin!");
    }
}
