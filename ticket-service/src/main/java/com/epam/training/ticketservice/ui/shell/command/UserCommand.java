package com.epam.training.ticketservice.ui.shell.command;

import com.epam.training.ticketservice.core.dao.UserDAO;
import com.epam.training.ticketservice.core.dto.UserDTO;
import com.epam.training.ticketservice.core.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.Optional;

@ShellComponent
@AllArgsConstructor
public class UserCommand {

    private final UserService userService;

    @ShellMethod(key = "sign in privileged", value = "Sign in for users with special privileges")
    public String signInPrivileged(String username, String password) {
        Optional<UserDTO> user = userService.signInPrivileged(username, password);
        if (user.isEmpty()) {
            return "Login failed due to incorrect credentials";
        }
        return user.get().getUsername() + " is successfully logged in! Admin commands are available!";
    }

    @ShellMethod(key = "sign in", value = "Sign in for users")
    public String signIn(String username, String password) {
        Optional<UserDTO> user = userService.signIn(username, password);
        if (user.isEmpty()) {
            return "Login failed due to incorrect credentials";
        }
        return "Signed in with privileged account " + user.get().getUsername();
    }

    @ShellMethod(key = "sign out")
    public String signOut() {
        Optional<UserDTO> user = userService.signOut();
        if (user.isEmpty()) {
            return "You are not signed in";
        }
        return user.get().getUsername() + " signed out";
    }

    @ShellMethod(key = "describe account", value = "Provides information about the current user")
    public String describe() {
        Optional<UserDTO> user = userService.signOut();
        if (user.isEmpty()) {
            return "You are not signed in";
        }

        String userName = user.get().getUsername();

        if(user.get().getRole() == UserDAO.Role.ADMIN)
            return "Signed in with privileged account " + userName;
        else
            return "Signed in as " + userName;
    }
}
