package com.epam.training.ticketservice.ui.shell.command;

import com.epam.training.ticketservice.core.entity.User;
import com.epam.training.ticketservice.core.model.UserDto;
import com.epam.training.ticketservice.core.service.BookingService;
import com.epam.training.ticketservice.core.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

class UserCommandTest {

    private final UserService userServiceMock = Mockito.mock(UserService.class);
    private final BookingService bookingServiceMock = Mockito.mock(BookingService.class);
    private final UserDto testUserDto = new UserDto("user", User.Role.USER);
    private final UserDto testAdminDTO = new UserDto("admin", User.Role.ADMIN);
    private final User testUser = new User(null, testUserDto.getUsername(), "password", testUserDto.getRole());
    private final User testAdmin = new User(null, testAdminDTO.getUsername(), "password", testAdminDTO.getRole());

    private final UserCommand testUserCommandComponent = new UserCommand(userServiceMock, bookingServiceMock);

    @Test
    void testSignInCommandShouldSignUserInWhenUserExists() {
        //Given
        Mockito.when(userServiceMock.signIn(testUser.getUsername(), testUser.getPassword()))
                .thenReturn(Optional.of(testUserDto));

        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(Optional.of(testUserDto));

        String expectedOutput = "Signed in with account '" + testUser.getUsername() + "'";
        Optional<UserDto> expectedUserDTO = Optional.of(testUserDto);

        //When
        String actualOutput = testUserCommandComponent.signIn(testUser.getUsername(), testUser.getPassword());
        Optional<UserDto> actualUserDTO = userServiceMock.getCurrentUser();

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
        Optional<UserDto> expectedUserDTO = Optional.empty();

        //When
        String actualOutput = testUserCommandComponent.signIn(testUser.getUsername(), testUser.getPassword());
        Optional<UserDto> actualUserDTO = userServiceMock.getCurrentUser();

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
        Optional<UserDto> expectedUserDTO = Optional.of(testAdminDTO);

        //When
        String actualOutput = testUserCommandComponent.signInPrivileged(testAdmin.getUsername(), testAdmin.getPassword());
        Optional<UserDto> actualUserDTO = userServiceMock.getCurrentUser();

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

        String expectedOutput = "Login failed due to incorrect credentials";
        Optional<UserDto> expectedUserDTO = Optional.empty();

        //When
        String actualOutput = testUserCommandComponent.signInPrivileged(testAdmin.getUsername(), testAdmin.getPassword());
        Optional<UserDto> actualUserDTO = userServiceMock.getCurrentUser();

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
                .thenReturn(Optional.of(testUserDto));

        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(Optional.empty());

        String expectedOutput = testUser.getUsername() + " signed out";
        Optional<UserDto> expectedUserDTO = Optional.empty();

        //When
        String actualOutput = testUserCommandComponent.signOut();
        Optional<UserDto> actualUserDTO = userServiceMock.getCurrentUser();

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
    void testDescribeShouldReturnUserDescriptionWithoutBookingsWhenRegularUserIsLoggedInAndHasNoBookingsYet() {
        //Given
        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(Optional.of(testUserDto));

        Mockito.when(bookingServiceMock.findBookings(testUserDto))
                .thenReturn(Optional.empty());

        String expectedOutput = "Signed in with account '" + testUser.getUsername() + "'"
                + "\nYou have not booked any tickets yet";

        //When
        String actualOutput = testUserCommandComponent.describe();

        //Then
        Assertions.assertEquals(expectedOutput, actualOutput);
        Mockito.verify(userServiceMock).getCurrentUser();
        Mockito.verify(bookingServiceMock).findBookings(testUserDto);
    }

    @Test
    void testDescribeShouldReturnUserDescriptionWithBookingsWhenRegularUserIsLoggedInAndHasBookings() {
        //Given
        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(Optional.of(testUserDto));

        String bookingListString = "bookingsList";

        Mockito.when(bookingServiceMock.findBookings(testUserDto))
                .thenReturn(Optional.of(bookingListString));

        String expectedOutput = "Signed in with account '" + testUser.getUsername() + "'"
                + "\nYour previous bookings are\n"
                + bookingListString;

        //When
        String actualOutput = testUserCommandComponent.describe();

        //Then
        Assertions.assertEquals(expectedOutput, actualOutput);
        Mockito.verify(userServiceMock).getCurrentUser();
        Mockito.verify(bookingServiceMock).findBookings(testUserDto);
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

    @Test
    void testSignUpShouldCreateNewAccountWhenInputsAreValid() {
        //Given
        String expectedOutput = testUser.getUsername() + " account created.";

        //When

        String actualOutput = testUserCommandComponent
                .signUp(testUser.getUsername(), testUser.getPassword());

        //Then
        Assertions.assertEquals(expectedOutput, actualOutput);
        Mockito.verify(userServiceMock).signUp(testUser.getUsername(), testUser.getPassword());
    }
}