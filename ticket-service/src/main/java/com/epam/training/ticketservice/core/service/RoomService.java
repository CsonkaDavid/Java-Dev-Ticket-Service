package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.model.RoomDTO;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    void createRoom(RoomDTO roomDTO);
    void updateRoom(String name, RoomDTO roomDTO);
    void deleteRoom(RoomDTO roomDTO);
    Optional<RoomDTO> findRoomByName(String name);
    List<RoomDTO> getRoomList();
}
