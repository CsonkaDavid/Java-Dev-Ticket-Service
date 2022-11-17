package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.model.RoomDTO;

import java.util.List;

public interface RoomService {
    void createRoom(RoomDTO roomDTO);
    void updateRoom(String name, RoomDTO roomDTO);
    void deleteRoom(RoomDTO roomDTO);
    RoomDTO findRoomByName(String name);
    List<RoomDTO> getRoomList();
}
