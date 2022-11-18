package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.entity.Room;
import com.epam.training.ticketservice.core.model.RoomDto;
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
    public void createRoom(RoomDto roomDTO) {
        roomRepository.save(new Room(null, roomDTO.getName(), roomDTO.getRows(), roomDTO.getColumns()));
    }

    @Override
    public void updateRoom(String name, RoomDto roomDTO) {
        Room room = roomRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("There is no room with the given name!"));

        roomRepository.updateMovie(room.getName(), roomDTO.getRows(), roomDTO.getColumns());
    }

    @Override
    public void deleteRoom(RoomDto roomDTO) {
        Room room = roomRepository.findByName(roomDTO.getName())
                .orElseThrow(() -> new IllegalArgumentException("There is no room with the given name!"));

        roomRepository.delete(room);
    }

    @Override
    public Optional<RoomDto> findRoomByName(String name) {
        return convertRoomToDTO(roomRepository.findByName(name));
    }

    @Override
    public List<RoomDto> getRoomList() {
        return roomRepository.findAll().stream()
                .map(this::convertRoomToDTO)
                .collect(Collectors.toList());
    }

    private RoomDto convertRoomToDTO(Room room) {
        return new RoomDto(room.getName(), room.getRows(), room.getColumns());
    }

    private Optional<RoomDto> convertRoomToDTO(Optional<Room> room) {
        return room.isEmpty() ? Optional.empty() : Optional.of(convertRoomToDTO(room.get()));
    }
}
