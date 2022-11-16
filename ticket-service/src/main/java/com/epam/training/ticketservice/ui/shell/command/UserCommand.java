package com.epam.training.ticketservice.ui.shell.command;

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

    @ShellMethod(key = "sign in privileged", value = "Sign in for users with special privileges.")
    public String signInPrivileged(String username, String password) {
        Optional<UserDTO> user = userService.signIn(username, password);
        if (user.isEmpty()) {
            return "Login failed due to incorrect credentials";
        }
        return user.get() + " is successfully logged in! Admin commands are available!";
    }

}
