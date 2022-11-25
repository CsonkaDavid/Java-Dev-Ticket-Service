package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.model.PriceComponentDto;

import java.util.Optional;

public interface PriceComponentService {
    String createPriceComponent(PriceComponentDto priceComponentDto);

    Optional<PriceComponentDto> findPriceComponentByName(String name);

    void updateMoviePriceComponent(String movieTitle, String componentName);

    void updateRoomPriceComponent(String roomName, String componentName);

    void updateScreeningPriceComponent(String movieTitle, String roomName, String formattedDateTime, String componentName);
}
