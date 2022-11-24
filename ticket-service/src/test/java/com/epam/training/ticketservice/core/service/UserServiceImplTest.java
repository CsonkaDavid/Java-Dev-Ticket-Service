package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.entity.User;
import com.epam.training.ticketservice.core.model.UserDto;
import com.epam.training.ticketservice.core.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl testUserService;


    private final User TEST_USER = new User(null,"user", "password", User.Role.USER);
    private final User TEST_ADMIN = new User(null,"admin", "admin", User.Role.ADMIN);

    @Test
    void testSignInNonPrivilegedUserShouldReturnCurrentOptionalUserWhenUsernameAndPasswordAreCorrect() {
        // Given
        Optional<User> expected = Optional.of(TEST_USER);
        Mockito.when(userRepository.findByUsernameAndPassword(TEST_USER.getUsername(), TEST_USER.getPassword()))
                .thenReturn(Optional.of(TEST_USER));

        // When
        Optional<UserDto> actual = testUserService.signIn(TEST_USER.getUsername(), TEST_USER.getPassword());

        // Then
        Assertions.assertEquals(expected.get().getUsername(), actual.get().getUsername());
        Assertions.assertEquals(expected.get().getRole(), actual.get().getRole());
        Mockito.verify(userRepository).findByUsernameAndPassword(TEST_USER.getUsername(), TEST_USER.getPassword());
    }

    @Test
    void testSignInNonPrivilegedUserShouldReturnOptionalEmptyWhenUsernameAndPasswordAreIncorrect() {
        // Given
        Optional<UserDto> expected = Optional.empty();
        Mockito.when(userRepository.findByUsernameAndPassword(TEST_USER.getUsername(), TEST_USER.getPassword()))
                .thenReturn(Optional.empty());

        // When
        Optional<UserDto> actual = testUserService.signIn(TEST_USER.getUsername(), TEST_USER.getPassword());

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(userRepository).findByUsernameAndPassword(TEST_USER.getUsername(), TEST_USER.getPassword());
    }

    @Test
    void testSignInPrivilegedUserShouldReturnCurrentOptionalUserWhenUsernameAndPasswordAreCorrect() {
        // Given
        Optional<User> expected = Optional.of(TEST_ADMIN);
        Mockito.when(userRepository.findByUsernameAndPassword(TEST_ADMIN.getUsername(), TEST_ADMIN.getPassword()))
                .thenReturn(Optional.of(TEST_ADMIN));

        // When
        Optional<UserDto> actual = testUserService.signInPrivileged(TEST_ADMIN.getUsername(), TEST_ADMIN.getPassword());

        // Then
        Assertions.assertEquals(expected.get().getUsername(), actual.get().getUsername());
        Assertions.assertEquals(expected.get().getRole(), actual.get().getRole());
        Mockito.verify(userRepository).findByUsernameAndPassword(TEST_ADMIN.getUsername(), TEST_ADMIN.getPassword());
    }

    @Test
    void testSignInPrivilegedUserShouldReturnOptionalEmptyWhenUsernameAndPasswordAreIncorrect() {
        // Given
        Optional<UserDto> expected = Optional.empty();
        Mockito.when(userRepository.findByUsernameAndPassword(TEST_ADMIN.getUsername(), TEST_ADMIN.getPassword()))
                .thenReturn(Optional.empty());

        // When
        Optional<UserDto> actual = testUserService.signInPrivileged(TEST_ADMIN.getUsername(), TEST_ADMIN.getPassword());

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(userRepository).findByUsernameAndPassword(TEST_ADMIN.getUsername(), TEST_ADMIN.getPassword());
    }

    @Test
    void testSignInPrivilegedUserShouldReturnOptionalEmptyWhenUsernameAndPasswordAreCorrectAndUserIsNotAdmin() {
        // Given
        Optional<UserDto> expected = Optional.empty();
        Mockito.when(userRepository.findByUsernameAndPassword(TEST_USER.getUsername(), TEST_USER.getPassword()))
                .thenReturn(Optional.of(TEST_USER));

        // When
        Optional<UserDto> actual = testUserService.signInPrivileged(TEST_USER.getUsername(), TEST_USER.getPassword());

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(userRepository).findByUsernameAndPassword(TEST_USER.getUsername(), TEST_USER.getPassword());
    }

    @Test
    void testSignOutUserShouldReturnPreviouslySignedInOptionalUserWhenSignedIn() {
        // Given
        Mockito.when(userRepository.findByUsernameAndPassword(TEST_USER.getUsername(), TEST_USER.getPassword()))
                .thenReturn(Optional.of(TEST_USER));
        Optional<UserDto> expected = testUserService.signIn(TEST_USER.getUsername(), TEST_USER.getPassword());

        // When
        Optional<UserDto> actual = testUserService.signOut();

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(userRepository).findByUsernameAndPassword(TEST_USER.getUsername(), TEST_USER.getPassword());
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
        Mockito.when(userRepository.findByUsernameAndPassword(TEST_USER.getUsername(), TEST_USER.getPassword()))
                .thenReturn(Optional.of(TEST_USER));
        Optional<UserDto> expected = testUserService.signIn(TEST_USER.getUsername(), TEST_USER.getPassword());

        // When
        Optional<UserDto> actual = testUserService.getCurrentUser();

        // Then
        assertEquals(expected, actual);
        Mockito.verify(userRepository).findByUsernameAndPassword(TEST_USER.getUsername(), TEST_USER.getPassword());
    }

    @Test
    void testGetCurrentUserShouldReturnCurrentOptionalUserWhenSignedInPrivileged() {
        // Given
        Mockito.when(userRepository.findByUsernameAndPassword(TEST_ADMIN.getUsername(), TEST_ADMIN.getPassword()))
                .thenReturn(Optional.of(TEST_ADMIN));
        Optional<UserDto> expected = testUserService.signInPrivileged(TEST_ADMIN.getUsername(), TEST_ADMIN.getPassword());

        // When
        Optional<UserDto> actual = testUserService.getCurrentUser();

        // Then
        assertEquals(expected, actual);
        Mockito.verify(userRepository).findByUsernameAndPassword(TEST_ADMIN.getUsername(), TEST_ADMIN.getPassword());
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
        Mockito.when(userRepository.findByUsername(TEST_USER.getUsername()))
                .thenReturn(Optional.empty());

        String expected = TEST_USER.getUsername() + " account created.";

        // When
        String actual = testUserService.signUp(TEST_USER.getUsername(), TEST_USER.getPassword());

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(userRepository).findByUsername(TEST_USER.getUsername());
    }

    @Test
    void testSignUpShouldThrowErrorUsernameIsTaken() {
        // Given
        Mockito.when(userRepository.findByUsername(TEST_USER.getUsername()))
                .thenReturn(Optional.of(TEST_USER));

        String expected = "Username \""
                + TEST_USER.getUsername()
                + "\" is already taken!";

        // When
        String actual = testUserService.signUp(TEST_USER.getUsername(), TEST_USER.getPassword());

        // Then
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(expected, actual);
        Mockito.verify(userRepository).findByUsername(TEST_USER.getUsername());
    }
}