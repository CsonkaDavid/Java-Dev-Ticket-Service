package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.entity.User;
import com.epam.training.ticketservice.core.model.UserDto;
import com.epam.training.ticketservice.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private UserDto currentUser = null;

    @Override
    public Optional<UserDto> signIn(String username, String password) {
        Optional<User> user = userRepository.findByUsernameAndPassword(username, password);

        if (user.isEmpty()) {
            return Optional.empty();
        }

        currentUser = new UserDto(user.get().getUsername(), user.get().getRole());

        return getCurrentUser();
    }

    @Override
    public Optional<UserDto> signInPrivileged(String username, String password) {
        Optional<User> user = userRepository.findByUsernameAndPassword(username, password);

        if (user.isEmpty()) {
            return Optional.empty();
        }

        if (!user.get().getRole().equals(User.Role.ADMIN)) {
            return Optional.empty();
        }

        currentUser = new UserDto(user.get().getUsername(), user.get().getRole());

        return getCurrentUser();
    }

    @Override
    public Optional<UserDto> signOut() {
        Optional<UserDto> previouslyLoggedInUser = getCurrentUser();

        currentUser = null;

        return previouslyLoggedInUser;
    }

    @Override
    public Optional<UserDto> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    @Override
    public void signUp(String username, String password) {
        User user = new User(null, username, password, User.Role.USER);

        Optional<User> existingUser = userRepository.findByUsername(username);

        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Username \""
                    + username
                    + "\" is already taken!");
        }

        userRepository.save(user);
    }
}
