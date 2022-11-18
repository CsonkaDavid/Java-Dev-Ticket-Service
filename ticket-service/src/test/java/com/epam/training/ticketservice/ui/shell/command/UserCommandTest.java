package com.epam.training.ticketservice.ui.shell.command;

import com.epam.training.ticketservice.core.entity.User;
import com.epam.training.ticketservice.core.model.UserDTO;
import com.epam.training.ticketservice.core.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

class UserCommandTest {

    private final UserService userServiceMock = Mockito.mock(UserService.class);
    private final UserDTO testUserDTO = new UserDTO("user", User.Role.USER);
    private final UserDTO testAdminDTO = new UserDTO("admin", User.Role.ADMIN);
    private final User testUser = new User(null, testUserDTO.getUsername(), "password", testUserDTO.getRole());
    private final User testAdmin = new User(null, testAdminDTO.getUsername(), "password", testAdminDTO.getRole());

    private final UserCommand testUserCommandComponent = new UserCommand(userServiceMock);

    @Test
    void testSignInCommandShouldSignUserInWhenUserExists() {
        //Given
        Mockito.when(userServiceMock.signIn(testUser.getUsername(), testUser.getPassword()))
                .thenReturn(Optional.of(testUserDTO));

        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(Optional.of(testUserDTO));

        String expectedOutput = "Signed in with account " + testUser.getUsername();
        Optional<UserDTO> expectedUserDTO = Optional.of(testUserDTO);

        //When
        String actualOutput = testUserCommandComponent.signIn(testUser.getUsername(), testUser.getPassword());
        Optional<UserDTO> actualUserDTO = userServiceMock.getCurrentUser();

        //Then
        Assertions.assertEquals(expectedOutput, actualOutput);
        Assertions.assertEquals(expectedUserDTO, actualUserDTO);
        Mockito.verify(userServiceMock).signIn(testUser.getUsername(), testUser.getPassword());
        Mockito.verify(userServiceMock).getCurrentUser();
    }

    @Test
    void testSignInCommandShouldGiveErrorMessageWhenUserDoesNotExist() {
        //Given
        Mockito.when(userServiceMock.signIn(testUser.getUsername(), testUser.getPassword()))
                .thenReturn(Optional.empty());

        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(Optional.empty());

        String expectedOutput = "Login failed due to incorrect credentials";
        Optional<UserDTO> expectedUserDTO = Optional.empty();

        //When
        String actualOutput = testUserCommandComponent.signIn(testUser.getUsername(), testUser.getPassword());
        Optional<UserDTO> actualUserDTO = userServiceMock.getCurrentUser();

        //Then
        Assertions.assertEquals(expectedOutput, actualOutput);
        Assertions.assertEquals(expectedUserDTO, actualUserDTO);
        Mockito.verify(userServiceMock).signIn(testUser.getUsername(), testUser.getPassword());
        Mockito.verify(userServiceMock).getCurrentUser();
    }

    @Test
    void testSignInPrivilegedCommandShouldSignUserInWhenUserExists() {
        //Given
        Mockito.when(userServiceMock.signInPrivileged(testAdmin.getUsername(), testAdmin.getPassword()))
                .thenReturn(Optional.of(testAdminDTO));

        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(Optional.of(testAdminDTO));

        String expectedOutput = testAdmin.getUsername() + " is successfully logged in! Admin commands are available!";
        Optional<UserDTO> expectedUserDTO = Optional.of(testAdminDTO);

        //When
        String actualOutput = testUserCommandComponent.signInPrivileged(testAdmin.getUsername(), testAdmin.getPassword());
        Optional<UserDTO> actualUserDTO = userServiceMock.getCurrentUser();

        //Then
        Assertions.assertEquals(expectedOutput, actualOutput);
        Assertions.assertEquals(expectedUserDTO, actualUserDTO);
        Mockito.verify(userServiceMock).signInPrivileged(testAdmin.getUsername(), testAdmin.getPassword());
        Mockito.verify(userServiceMock).getCurrentUser();
    }

    @Test
    void testSignInPrivilegedCommandShouldGiveErrorMessageWhenUserDoesNotExistOrIsNotAdmin() {
        //Given
        Mockito.when(userServiceMock.signInPrivileged(testAdmin.getUsername(), testAdmin.getPassword()))
                .thenReturn(Optional.empty());

        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(Optional.empty());

        String expectedOutput = "Login failed due to incorrect credentials";
        Optional<UserDTO> expectedUserDTO = Optional.empty();

        //When
        String actualOutput = testUserCommandComponent.signInPrivileged(testAdmin.getUsername(), testAdmin.getPassword());
        Optional<UserDTO> actualUserDTO = userServiceMock.getCurrentUser();

        //Then
        Assertions.assertEquals(expectedOutput, actualOutput);
        Assertions.assertEquals(expectedUserDTO, actualUserDTO);
        Mockito.verify(userServiceMock).signInPrivileged(testAdmin.getUsername(), testAdmin.getPassword());
        Mockito.verify(userServiceMock).getCurrentUser();
    }

    @Test
    void testSignOutShouldReturnPreviouslyLoggedInUserWhenUserIsLoggedIn() {
        //Given
        Mockito.when(userServiceMock.signOut())
                .thenReturn(Optional.of(testUserDTO));

        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(Optional.empty());

        String expectedOutput = testUser.getUsername() + " signed out";
        Optional<UserDTO> expectedUserDTO = Optional.empty();

        //When
        String actualOutput = testUserCommandComponent.signOut();
        Optional<UserDTO> actualUserDTO = userServiceMock.getCurrentUser();

        //Then
        Assertions.assertEquals(expectedOutput, actualOutput);
        Assertions.assertEquals(expectedUserDTO, actualUserDTO);
        Mockito.verify(userServiceMock).signOut();
        Mockito.verify(userServiceMock).getCurrentUser();
    }

    @Test
    void testSignOutShouldGiveErrorMessageWhenUserIsNotLoggedIn() {
        //Given
        Mockito.when(userServiceMock.signOut())
                .thenReturn(Optional.empty());

        String expectedOutput = "You are not signed in";

        //When
        String actualOutput = testUserCommandComponent.signOut();

        //Then
        Assertions.assertEquals(expectedOutput, actualOutput);
        Mockito.verify(userServiceMock).signOut();
    }

    @Test
    void testDescribeShouldReturnUserDescriptionWhenRegularUserIsLoggedIn() {
        //Given
        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(Optional.of(testUserDTO));

        String expectedOutput = "Signed in as " + testUser.getUsername();

        //When
        String actualOutput = testUserCommandComponent.describe();

        //Then
        Assertions.assertEquals(expectedOutput, actualOutput);
        Mockito.verify(userServiceMock).getCurrentUser();
    }

    @Test
    void testDescribeShouldReturnAdminDescriptionWhenPrivilegedUserIsLoggedIn() {
        //Given
        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(Optional.of(testAdminDTO));

        String expectedOutput = "Signed in with privileged account '" + testAdmin.getUsername() + "'";

        //When
        String actualOutput = testUserCommandComponent.describe();

        //Then
        Assertions.assertEquals(expectedOutput, actualOutput);
        Mockito.verify(userServiceMock).getCurrentUser();
    }

    @Test
    void testDescribeShouldGiveErrorMessageWhenNoUserIsLoggedIn() {
        //Given
        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(Optional.empty());

        String expectedOutput = "You are not signed in";

        //When
        String actualOutput = testUserCommandComponent.describe();

        //Then
        Assertions.assertEquals(expectedOutput, actualOutput);
        Mockito.verify(userServiceMock).getCurrentUser();
    }
}