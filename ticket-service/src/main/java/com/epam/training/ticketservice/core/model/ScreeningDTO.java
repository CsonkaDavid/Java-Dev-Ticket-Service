package com.epam.training.ticketservice.core.model;

import lombok.Value;

@Value
public class ScreeningDTO {
    String movieTitle;
    String roomName;
    String formattedDateTime;
}
