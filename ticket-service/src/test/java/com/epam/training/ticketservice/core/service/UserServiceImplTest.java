package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.entity.User;
import com.epam.training.ticketservice.core.model.UserDTO;
import com.epam.training.ticketservice.core.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserService testUserService = new UserServiceImpl(userRepository);

    @Test
    void testSignInNonPrivilegedUserWhenUsernameAndPasswordAreCorrect() {
        // Given
        User user = new User(null,"user", "password", User.Role.USER);
        Optional<User> expected = Optional.of(user);
        when(userRepository.findByUsernameAndPassword("user", "password")).thenReturn(Optional.of(user));

        // When
        Optional<UserDTO> actual = testUserService.signIn("user", "password");

        // Then
        Assertions.assertEquals(expected.get().getUsername(), actual.get().getUsername());
        Assertions.assertEquals(expected.get().getRole(), actual.get().getRole());
        verify(userRepository).findByUsernameAndPassword("user", "password");
    }

    @Test
    void testSignInNonPrivilegedUserWhenUsernameAndPasswordAreIncorrect() {
        // Given
        Optional<UserDTO> expected = Optional.empty();
        when(userRepository.findByUsernameAndPassword("user", "password")).thenReturn(Optional.empty());

        // When
        Optional<UserDTO> actual = testUserService.signIn("user", "password");

        // Then
        Assertions.assertEquals(expected, actual);
        verify(userRepository).findByUsernameAndPassword("user", "password");
    }

    @Test
    void testSignInPrivilegedUserWhenUsernameAndPasswordAreCorrect() {
        // Given
        User user = new User(null,"admin", "admin", User.Role.ADMIN);
        Optional<User> expected = Optional.of(user);
        when(userRepository.findByUsernameAndPassword("admin", "admin")).thenReturn(Optional.of(user));

        // When
        Optional<UserDTO> actual = testUserService.signInPrivileged("admin", "admin");

        // Then
        Assertions.assertEquals(expected.get().getUsername(), actual.get().getUsername());
        Assertions.assertEquals(expected.get().getRole(), actual.get().getRole());
        verify(userRepository).findByUsernameAndPassword("admin", "admin");
    }

    @Test
    void testSignInPrivilegedUserWhenUsernameAndPasswordAreIncorrect() {
        // Given
        Optional<UserDTO> expected = Optional.empty();
        when(userRepository.findByUsernameAndPassword("admin", "admin")).thenReturn(Optional.empty());

        // When
        Optional<UserDTO> actual = testUserService.signInPrivileged("admin", "admin");

        // Then
        Assertions.assertEquals(expected, actual);
        verify(userRepository).findByUsernameAndPassword("admin", "admin");
    }

    @Test
    void testSignInPrivilegedUserWhenUsernameAndPasswordAreCorrectAndUserIsNotAdmin() {
        // Given
        User user = new User(null,"user", "password", User.Role.USER);
        Optional<UserDTO> expected = Optional.empty();
        when(userRepository.findByUsernameAndPassword("user", "password")).thenReturn(Optional.of(user));

        // When
        Optional<UserDTO> actual = testUserService.signInPrivileged("user", "password");

        // Then
        Assertions.assertEquals(expected, actual);
        verify(userRepository).findByUsernameAndPassword("user", "password");
    }

    @Test
    void testSignOutUserWhenSignedIn() {
        // Given
        User user = new User(null,"user", "password", User.Role.USER);
        when(userRepository.findByUsernameAndPassword("user", "password")).thenReturn(Optional.of(user));
        Optional<UserDTO> expected = testUserService.signIn("user", "password");

        // When
        Optional<UserDTO> actual = testUserService.signOut();

        // Then
        Assertions.assertEquals(expected, actual);
        verify(userRepository).findByUsernameAndPassword("user", "password");
    }

    @Test
    void testSignOutUserWhenNotSignedIn() {
        // Given
        Optional<UserDTO> expected = Optional.empty();

        // When
        Optional<UserDTO> actual = testUserService.signOut();

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testGetCurrentUserWhenSignedInNonPrivileged() {
        // Given
        User user = new User(null,"user", "password", User.Role.USER);
        when(userRepository.findByUsernameAndPassword("user", "pass")).thenReturn(Optional.of(user));
        Optional<UserDTO> expected = testUserService.signIn("user", "password");

        // When
        Optional<UserDTO> actual = testUserService.getCurrentUser();

        // Then
        assertEquals(expected, actual);
        verify(userRepository).findByUsernameAndPassword("user", "password");
    }

    @Test
    void testGetCurrentUserWhenSignedInPrivileged() {
        // Given
        User user = new User(null,"admin", "admin", User.Role.ADMIN);
        when(userRepository.findByUsernameAndPassword("admin", "admin")).thenReturn(Optional.of(user));
        Optional<UserDTO> expected = testUserService.signInPrivileged("admin", "admin");

        // When
        Optional<UserDTO> actual = testUserService.getCurrentUser();

        // Then
        assertEquals(expected, actual);
        verify(userRepository).findByUsernameAndPassword("admin", "admin");
    }

    @Test
    void testGetCurrentUserWhenNotSignedIn() {
        // Given
        Optional<UserDTO> expected = Optional.empty();

        // When
        Optional<UserDTO> actual = testUserService.getCurrentUser();

        // Then
        assertEquals(expected, actual);
    }
}