package com.epam.training.ticketservice.core.repository;

import com.epam.training.ticketservice.core.entity.Movie;
import com.epam.training.ticketservice.core.entity.PriceComponent;
import com.epam.training.ticketservice.core.entity.Room;
import com.epam.training.ticketservice.core.entity.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Repository
public interface ScreeningRepository  extends JpaRepository<Screening, Integer> {
    Optional<Screening> findByMovieAndRoomAndDate(Movie movie, Room room, Date date);

    @Transactional
    @Modifying
    @Query("update Screening s set s.priceComponent = :component where s = :screening")
    void updateScreeningPriceComponent(Screening screening, PriceComponent component);
}
