package com.epam.training.ticketservice.ui.shell.command;

import com.epam.training.ticketservice.core.entity.User;
import com.epam.training.ticketservice.core.model.UserDTO;
import com.epam.training.ticketservice.core.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.Optional;

@ShellComponent
@AllArgsConstructor
public class UserCommand {

    private final UserService userService;

    @SuppressWarnings("unused")
    @ShellMethod(key = "sign in privileged", value = "Sign in for users with special privileges")
    public String signInPrivileged(String username, String password) {
        Optional<UserDTO> user = userService.signInPrivileged(username, password);
        if (user.isEmpty()) {
            return "Login failed due to incorrect credentials";
        }
        return user.get().getUsername() + " is successfully logged in! Admin commands are available!";
    }

    @SuppressWarnings("unused")
    @ShellMethod(key = "sign in", value = "Sign in for users")
    public String signIn(String username, String password) {
        Optional<UserDTO> user = userService.signIn(username, password);
        if (user.isEmpty()) {
            return "Login failed due to incorrect credentials";
        }
        return "Signed in with account " + user.get().getUsername();
    }

    @SuppressWarnings("unused")
    @ShellMethod(key = "sign out")
    public String signOut() {
        Optional<UserDTO> user = userService.signOut();
        if (user.isEmpty()) {
            return "You are not signed in";
        }
        return user.get().getUsername() + " signed out";
    }

    @SuppressWarnings("unused")
    @ShellMethod(key = "describe account", value = "Provides information about the current user")
    public String describe() {
        Optional<UserDTO> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            return "You are not signed in";
        }

        String userName = user.get().getUsername();

        if(user.get().getRole() == User.Role.ADMIN)
            return "Signed in with privileged account '" + userName + "'";
        else
            return "Signed in as " + userName;
    }
}
