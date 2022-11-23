package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.model.MovieDto;
import com.epam.training.ticketservice.core.model.PriceComponentDto;
import com.epam.training.ticketservice.core.model.RoomDto;
import com.epam.training.ticketservice.core.model.ScreeningDto;

import java.util.Optional;

public interface PriceComponentService {
    String createPriceComponent(PriceComponentDto priceComponentDto);

    Optional<PriceComponentDto> findPriceComponentByName(String name);

    void updateMoviePriceComponent(MovieDto movieDto, PriceComponentDto priceComponentDto);

    void updateRoomPriceComponent(RoomDto roomDto, PriceComponentDto priceComponentDto);

    void updateScreeningPriceComponent(ScreeningDto screeningDto, PriceComponentDto priceComponentDto);
}
