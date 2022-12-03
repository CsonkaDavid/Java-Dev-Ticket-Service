package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.model.RoomDto;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    void createRoom(RoomDto roomDto);

    void updateRoom(String name, RoomDto roomDto);

    void deleteRoom(String roomName);

    Optional<RoomDto> findRoomByName(String name);

    List<RoomDto> getRoomList();
}
