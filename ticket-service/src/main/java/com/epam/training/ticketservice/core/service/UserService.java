package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.model.UserDto;

import java.util.Optional;

public interface UserService {
    Optional<UserDto> signIn(String username, String password);

    Optional<UserDto> signInPrivileged(String username, String password);

    Optional<UserDto> signOut();

    Optional<UserDto> getCurrentUser();
}
