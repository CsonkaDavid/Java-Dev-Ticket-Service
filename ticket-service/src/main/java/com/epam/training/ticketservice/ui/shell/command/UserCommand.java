package com.epam.training.ticketservice.ui.shell.command;

import com.epam.training.ticketservice.core.entity.User;
import com.epam.training.ticketservice.core.model.UserDto;
import com.epam.training.ticketservice.core.service.BookingService;
import com.epam.training.ticketservice.core.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.Optional;

@ShellComponent
@AllArgsConstructor
public class UserCommand {

    private final UserService userService;
    private final BookingService bookingService;

    @SuppressWarnings("unused")
    @ShellMethod(key = "sign in privileged", value = "Sign in for users with special privileges")
    public String signInPrivileged(String username, String password) {
        Optional<UserDto> user = userService.signInPrivileged(username, password);
        if (user.isEmpty()) {
            return "Login failed due to incorrect credentials";
        }

        return user.get().getUsername() + " is successfully logged in! Admin commands are available!";
    }

    @SuppressWarnings("unused")
    @ShellMethod(key = "sign in", value = "Sign in for users")
    public String signIn(String username, String password) {
        Optional<UserDto> user = userService.signIn(username, password);
        if (user.isEmpty()) {
            return "Login failed due to incorrect credentials";
        }

        return "Signed in with account " + user.get().getUsername();
    }

    @SuppressWarnings("unused")
    @ShellMethod(key = "sign out")
    public String signOut() {
        Optional<UserDto> user = userService.signOut();
        if (user.isEmpty()) {
            return "You are not signed in";
        }

        return user.get().getUsername() + " signed out";
    }

    @SuppressWarnings("unused")
    @ShellMethod(key = "describe account", value = "Provides information about the current user")
    public String describe() {
        Optional<UserDto> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            return "You are not signed in";
        }

        String userName = user.get().getUsername();

        if (user.get().getRole() == User.Role.ADMIN) {
            return "Signed in with privileged account '" + userName + "'";
        } else {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder
                    .append("Signed in as ")
                    .append(userName)
                    .append("\n");

            Optional<String> bookingListReadable = bookingService.findBookings(user.get());

            if (bookingListReadable.isEmpty()) {

                stringBuilder.append("You have not booked any tickets yet");

            } else {
                stringBuilder
                        .append("Your previous bookings are")
                        .append("\n")
                        .append(bookingListReadable.get());
            }

            return stringBuilder.toString();
        }
    }

    @SuppressWarnings("unused")
    @ShellMethod(key = "sign up")
    public String signUp(String username, String password) {
        userService.signUp(username, password);

        return username + " account created.";
    }
}
