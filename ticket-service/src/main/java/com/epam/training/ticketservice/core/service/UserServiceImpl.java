package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.dao.UserDAO;
import com.epam.training.ticketservice.core.dto.UserDTO;
import com.epam.training.ticketservice.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private UserDTO currentUser = null;

    @Override
    public Optional<UserDTO> signIn(String username, String password) {
        Optional<UserDAO> user = userRepository.findByUsernameAndPassword(username, password);

        if(user.isEmpty()) return Optional.empty();

        currentUser = new UserDTO(user.get().getUsername(), user.get().getRole());

        return getCurrentUser();
    }

    @Override
    public Optional<UserDTO> signInPrivileged(String username, String password) {
        Optional<UserDAO> user = userRepository.findByUsernameAndPassword(username, password);

        if(user.isEmpty()) return Optional.empty();
        if(!user.get().getRole().equals(UserDAO.Role.ADMIN)) return Optional.empty();

        currentUser = new UserDTO(user.get().getUsername(), user.get().getRole());

        return getCurrentUser();
    }

    @Override
    public Optional<UserDTO> signOut() {
        Optional<UserDTO> previouslyLoggedInUser = getCurrentUser();
        currentUser = null;
        return previouslyLoggedInUser;
    }

    @Override
    public Optional<UserDTO> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }
}
