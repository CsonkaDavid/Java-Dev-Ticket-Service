package com.epam.training.ticketservice.core.model;

import lombok.Value;

@Value
public class BookingDto {
    String movieTitle;
    String roomName;
    String formattedDateTime;
    String seats;
    Integer price;
}
