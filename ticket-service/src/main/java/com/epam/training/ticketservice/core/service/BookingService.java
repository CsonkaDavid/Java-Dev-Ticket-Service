package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.model.ScreeningDto;
import com.epam.training.ticketservice.core.model.UserDto;

import java.util.Optional;

public interface BookingService {
    String book(UserDto userDto, ScreeningDto screeningDto, String seats);

    Optional<String> findBookings(UserDto userDto);

    int calculateBookingPrice(int moviePriceComponent, int roomPriceComponent, int screeningPriceComponent, int seats);
}
