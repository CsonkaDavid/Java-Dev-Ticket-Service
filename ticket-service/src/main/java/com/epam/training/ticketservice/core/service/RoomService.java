package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.model.RoomDto;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    void createRoom(RoomDto roomDTO);
    void updateRoom(String name, RoomDto roomDTO);
    void deleteRoom(RoomDto roomDTO);
    Optional<RoomDto> findRoomByName(String name);
    List<RoomDto> getRoomList();
}
