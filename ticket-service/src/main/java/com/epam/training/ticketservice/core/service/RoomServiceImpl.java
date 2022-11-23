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
    public void createRoom(RoomDto roomDto) {
        roomRepository.save(new Room(
                null,
                roomDto.getName(),
                roomDto.getRows(),
                roomDto.getColumns(),
                null));
    }

    @Override
    public void updateRoom(String name, RoomDto roomDto) {
        Room room = roomRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("There is no room with the given name!"));

        roomRepository.updateMovie(room.getName(), roomDto.getRows(), roomDto.getColumns());
    }

    @Override
    public void deleteRoom(RoomDto roomDto) {
        Room room = roomRepository.findByName(roomDto.getName())
                .orElseThrow(() -> new IllegalArgumentException("There is no room with the given name!"));

        roomRepository.delete(room);
    }

    @Override
    public Optional<RoomDto> findRoomByName(String name) {
        return convertRoomToDto(roomRepository.findByName(name));
    }

    @Override
    public List<RoomDto> getRoomList() {
        return roomRepository.findAll().stream()
                .map(this::convertRoomToDto)
                .collect(Collectors.toList());
    }

    private RoomDto convertRoomToDto(Room room) {
        return new RoomDto(
                room.getName(),
                room.getRows(),
                room.getColumns(),
                room.getPriceComponent() == null ? 0 : room.getPriceComponent().getAmount());
    }

    private Optional<RoomDto> convertRoomToDto(Optional<Room> room) {
        return room.isEmpty() ? Optional.empty() : Optional.of(convertRoomToDto(room.get()));
    }
}
