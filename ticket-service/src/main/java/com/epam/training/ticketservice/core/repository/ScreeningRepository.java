package com.epam.training.ticketservice.core.repository;

import com.epam.training.ticketservice.core.entity.Movie;
import com.epam.training.ticketservice.core.entity.Room;
import com.epam.training.ticketservice.core.entity.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ScreeningRepository  extends JpaRepository<Screening, Integer> {
    void deleteByMovieAndRoomAndDate(Movie movie, Room room, Date date);
}
