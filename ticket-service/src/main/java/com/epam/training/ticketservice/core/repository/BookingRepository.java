package com.epam.training.ticketservice.core.repository;

import com.epam.training.ticketservice.core.entity.Booking;
import com.epam.training.ticketservice.core.entity.Screening;
import com.epam.training.ticketservice.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByScreening(Screening screening);

    List<Booking> findAllByUser(User user);
}
