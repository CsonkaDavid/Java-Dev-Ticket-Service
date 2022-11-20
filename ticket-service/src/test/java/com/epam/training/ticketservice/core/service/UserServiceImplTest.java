package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.entity.User;
import com.epam.training.ticketservice.core.model.UserDto;
import com.epam.training.ticketservice.core.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserServiceImplTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final UserService testUserService = new UserServiceImpl(userRepository);

    @Test
    void testSignInNonPrivilegedUserShouldReturnCurrentOptionalUserWhenUsernameAndPasswordAreCorrect() {
        // Given
        User user = new User(null,"user", "password", User.Role.USER);
        Optional<User> expected = Optional.of(user);
        Mockito.when(userRepository.findByUsernameAndPassword("user", "password"))
                .thenReturn(Optional.of(user));

        // When
        Optional<UserDto> actual = testUserService.signIn("user", "password");

        // Then
        Assertions.assertEquals(expected.get().getUsername(), actual.get().getUsername());
        Assertions.assertEquals(expected.get().getRole(), actual.get().getRole());
        Mockito.verify(userRepository).findByUsernameAndPassword("user", "password");
    }

    @Test
    void testSignInNonPrivilegedUserShouldReturnOptionalEmptyWhenUsernameAndPasswordAreIncorrect() {
        // Given
        Optional<UserDto> expected = Optional.empty();
        Mockito.when(userRepository.findByUsernameAndPassword("user", "password"))
                .thenReturn(Optional.empty());

        // When
        Optional<UserDto> actual = testUserService.signIn("user", "password");

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(userRepository).findByUsernameAndPassword("user", "password");
    }

    @Test
    void testSignInPrivilegedUserShouldReturnCurrentOptionalUserWhenUsernameAndPasswordAreCorrect() {
        // Given
        User user = new User(null,"admin", "admin", User.Role.ADMIN);
        Optional<User> expected = Optional.of(user);
        Mockito.when(userRepository.findByUsernameAndPassword("admin", "admin"))
                .thenReturn(Optional.of(user));

        // When
        Optional<UserDto> actual = testUserService.signInPrivileged("admin", "admin");

        // Then
        Assertions.assertEquals(expected.get().getUsername(), actual.get().getUsername());
        Assertions.assertEquals(expected.get().getRole(), actual.get().getRole());
        Mockito.verify(userRepository).findByUsernameAndPassword("admin", "admin");
    }

    @Test
    void testSignInPrivilegedUserShouldReturnOptionalEmptyWhenUsernameAndPasswordAreIncorrect() {
        // Given
        Optional<UserDto> expected = Optional.empty();
        Mockito.when(userRepository.findByUsernameAndPassword("admin", "admin"))
                .thenReturn(Optional.empty());

        // When
        Optional<UserDto> actual = testUserService.signInPrivileged("admin", "admin");

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(userRepository).findByUsernameAndPassword("admin", "admin");
    }

    @Test
    void testSignInPrivilegedUserShouldReturnOptionalEmptyWhenUsernameAndPasswordAreCorrectAndUserIsNotAdmin() {
        // Given
        User user = new User(null,"user", "password", User.Role.USER);
        Optional<UserDto> expected = Optional.empty();
        Mockito.when(userRepository.findByUsernameAndPassword("user", "password"))
                .thenReturn(Optional.of(user));

        // When
        Optional<UserDto> actual = testUserService.signInPrivileged("user", "password");

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(userRepository).findByUsernameAndPassword("user", "password");
    }

    @Test
    void testSignOutUserShouldReturnPreviouslySignedInOptionalUserWhenSignedIn() {
        // Given
        User user = new User(null,"user", "password", User.Role.USER);
        Mockito.when(userRepository.findByUsernameAndPassword("user", "password"))
                .thenReturn(Optional.of(user));
        Optional<UserDto> expected = testUserService.signIn("user", "password");

        // When
        Optional<UserDto> actual = testUserService.signOut();

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(userRepository).findByUsernameAndPassword("user", "password");
    }

    @Test
    void testSignOutUserShouldReturnOptionalEmptyWhenNotSignedIn() {
        // Given
        Optional<UserDto> expected = Optional.empty();

        // When
        Optional<UserDto> actual = testUserService.signOut();

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testGetCurrentUserShouldReturnCurrentOptionalUserWhenSignedInNonPrivileged() {
        // Given
        User user = new User(null,"user", "password", User.Role.USER);
        Mockito.when(userRepository.findByUsernameAndPassword("user", "pass"))
                .thenReturn(Optional.of(user));
        Optional<UserDto> expected = testUserService.signIn("user", "password");

        // When
        Optional<UserDto> actual = testUserService.getCurrentUser();

        // Then
        assertEquals(expected, actual);
        Mockito.verify(userRepository).findByUsernameAndPassword("user", "password");
    }

    @Test
    void testGetCurrentUserShouldReturnCurrentOptionalUserWhenSignedInPrivileged() {
        // Given
        User user = new User(null,"admin", "admin", User.Role.ADMIN);
        Mockito.when(userRepository.findByUsernameAndPassword("admin", "admin"))
                .thenReturn(Optional.of(user));
        Optional<UserDto> expected = testUserService.signInPrivileged("admin", "admin");

        // When
        Optional<UserDto> actual = testUserService.getCurrentUser();

        // Then
        assertEquals(expected, actual);
        Mockito.verify(userRepository).findByUsernameAndPassword("admin", "admin");
    }

    @Test
    void testGetCurrentUserShouldReturnOptionalEmptyWhenNotSignedIn() {
        // Given
        Optional<UserDto> expected = Optional.empty();

        // When
        Optional<UserDto> actual = testUserService.getCurrentUser();

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void testSignUpShouldSaveUserWhenUsernameIsNotTaken() {
        // Given
        User user = new User(null,"user", "password", User.Role.USER);
        Mockito.when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.empty());

        // When
        testUserService.signUp(user.getUsername(), user.getPassword());

        // Then
        Mockito.verify(userRepository).findByUsername(user.getUsername());
        Mockito.verify(userRepository).save(user);
    }

    @Test
    void testSignUpShouldThrowErrorUsernameIsTaken() {
        // Given
        User user = new User(null,"user", "password", User.Role.USER);
        Mockito.when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));

        // When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testUserService.signUp(user.getUsername(), "anypassword"));
        Mockito.verify(userRepository).findByUsername(user.getUsername());
    }
}