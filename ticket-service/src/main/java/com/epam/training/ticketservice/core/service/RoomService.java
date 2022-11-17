package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.model.RoomDTO;

import java.util.List;

public interface RoomService {
    void createRoom(RoomDTO roomDTO);
    void updateRoom(String name, Integer rows, Integer columns);
    void deleteRoom(String name);
    List<RoomDTO> listRooms();
}
