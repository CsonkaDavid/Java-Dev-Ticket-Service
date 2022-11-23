package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.entity.Room;
import com.epam.training.ticketservice.core.model.RoomDto;
import com.epam.training.ticketservice.core.repository.RoomRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepositoryMock;
    @InjectMocks
    private RoomServiceImpl testRoomService;

    private final Room TEST_ROOM = new Room(null, "R1", 15, 20, null);
    private final RoomDto TEST_ROOM_DTO = new RoomDto("R1", 15, 20, 0);

    @Test
    void testCreateRoomShouldSaveNewRoomWhenInputIsValid() {
        // Given
        Mockito.when(roomRepositoryMock.save(TEST_ROOM)).thenReturn(TEST_ROOM);

        // When
        testRoomService.createRoom(TEST_ROOM_DTO);

        // Then
        Mockito.verify(roomRepositoryMock).save(TEST_ROOM);
    }

    @Test
    void testGetRoomListShouldReturnListOfTestedElement() {
        //Given
        Mockito.when(roomRepositoryMock.findAll()).thenReturn(List.of(TEST_ROOM));
        List<RoomDto> expected = List.of(TEST_ROOM_DTO);

        //When
        List<RoomDto> actual = testRoomService.getRoomList();

        //Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(roomRepositoryMock).findAll();
    }

    @Test
    void testUpdateRoomShouldUpdateTestRoomWhenInputIsValid() {
        // Given
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName()))
                .thenReturn(Optional.of(TEST_ROOM));

        // When
        testRoomService.updateRoom(TEST_ROOM.getName(), TEST_ROOM_DTO);

        // Then
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
    }

    @Test
    void testUpdateRoomShouldThrowErrorWhenInputIsInvalid() {
        // Given
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName()))
                .thenReturn(Optional.empty());

        // When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testRoomService.updateRoom(TEST_ROOM.getName(), TEST_ROOM_DTO));
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
    }

    @Test
    void testDeleteRoomShouldDeleteTestRoomWhenInputIsValid() {
        // Given
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName())).thenReturn(Optional.of(TEST_ROOM));

        // When
        testRoomService.deleteRoom(TEST_ROOM_DTO);

        // Then
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
    }

    @Test
    void testDeleteRoomShouldThrowErrorWhenInputIsInvalid() {
        // Given
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName()))
                .thenReturn(Optional.empty());

        // When Then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testRoomService.deleteRoom(TEST_ROOM_DTO));
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
    }

    @Test
    void testFindRoomByNameShouldReturnMovieWhenInputIsValid() {
        // Given
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName()))
                .thenReturn(Optional.of(TEST_ROOM));
        Optional<RoomDto> expected = Optional.of(TEST_ROOM_DTO);

        // When
        Optional<RoomDto> actual = testRoomService.findRoomByName(TEST_ROOM.getName());

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
    }

    @Test
    void testFindRoomByNameShouldReturnOptionalEmptyWhenInputIsInvalid() {
        // Given
        Mockito.when(roomRepositoryMock.findByName(TEST_ROOM.getName())).thenReturn(Optional.empty());
        Optional<RoomDto> expected = Optional.empty();

        // When
        Optional<RoomDto> actual = testRoomService.findRoomByName(TEST_ROOM.getName());

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(roomRepositoryMock).findByName(TEST_ROOM.getName());
    }
}