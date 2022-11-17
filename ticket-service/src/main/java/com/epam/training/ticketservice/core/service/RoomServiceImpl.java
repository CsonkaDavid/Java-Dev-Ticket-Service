package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.entity.Room;
import com.epam.training.ticketservice.core.model.RoomDTO;
import com.epam.training.ticketservice.core.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    public void createRoom(RoomDTO roomDTO) {
        Room room = new Room(null, roomDTO.getName(), roomDTO.getRows(), roomDTO.getColumns());

        roomRepository.save(room);
    }

    @Override
    public void updateRoom(String name, Integer rows, Integer columns) {
        Optional<Room> room = roomRepository.findByName(name);

        if(room.isEmpty()) return;

        roomRepository.updateMovie(room.get().getName(), rows, columns);
    }

    @Override
    public void deleteRoom(String name) {
        if(roomRepository.findByName(name).isEmpty()) return;

        roomRepository.deleteByName(name);
    }

    @Override
    public List<RoomDTO> listRooms() {
        return roomRepository.findAll().stream()
                .map(this::convertRoomToDTO)
                .collect(Collectors.toList());
    }

    private RoomDTO convertRoomToDTO(Room room) {
        return new RoomDTO(room.getName(), room.getRows(), room.getColumns());
    }
}
