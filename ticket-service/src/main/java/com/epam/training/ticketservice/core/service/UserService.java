package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.dto.UserDTO;

import java.util.Optional;

public interface UserService {
    Optional<UserDTO> login(String username, String password);
    Optional<UserDTO> logout();
    Optional<UserDTO> getCurrentUser();
}
