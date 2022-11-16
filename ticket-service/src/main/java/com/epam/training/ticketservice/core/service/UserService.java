package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.model.UserDTO;

import java.util.Optional;

public interface UserService {
    Optional<UserDTO> signIn(String username, String password);
    Optional<UserDTO> signInPrivileged(String username, String password);
    Optional<UserDTO> signOut();
    Optional<UserDTO> getCurrentUser();
}
