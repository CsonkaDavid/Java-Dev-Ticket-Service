package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.entity.Room;
import com.epam.training.ticketservice.core.model.RoomDTO;
import com.epam.training.ticketservice.core.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    public void createRoom(RoomDTO roomDTO) {
        roomRepository.save(new Room(null, roomDTO.getName(), roomDTO.getRows(), roomDTO.getColumns()));
    }

    @Override
    public void updateRoom(String name, RoomDTO roomDTO) {
        Room room = roomRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("There is no room with the given name!"));

        roomRepository.updateMovie(room.getName(), roomDTO.getRows(), roomDTO.getColumns());
    }

    @Override
    public void deleteRoom(RoomDTO roomDTO) {
        Room room = roomRepository.findByName(roomDTO.getName())
                .orElseThrow(() -> new IllegalArgumentException("There is no room with the given name!"));

        roomRepository.delete(room);
    }

    @Override
    public RoomDTO findRoomByName(String name) {
        return convertRoomToDTO(roomRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("There is no room with the given name!")));
    }

    @Override
    public List<RoomDTO> getRoomList() {
        return roomRepository.findAll().stream()
                .map(this::convertRoomToDTO)
                .collect(Collectors.toList());
    }

    private RoomDTO convertRoomToDTO(Room room) {
        return new RoomDTO(room.getName(), room.getRows(), room.getColumns());
    }
}
