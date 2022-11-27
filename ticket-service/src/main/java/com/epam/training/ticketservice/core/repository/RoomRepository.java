package com.epam.training.ticketservice.core.repository;

import com.epam.training.ticketservice.core.entity.PriceComponent;
import com.epam.training.ticketservice.core.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    Optional<Room> findByName(String name);

    @Transactional
    @Modifying
    @Query("update Room r set r.rows = :rows, r.columns = :columns where r.name = :name")
    void updateRoom(String name, Integer rows, Integer columns);

    @Transactional
    @Modifying
    @Query("update Room r set r.priceComponent = :component where r = :room")
    void updateRoomPriceComponent(Room room, PriceComponent component);
}
