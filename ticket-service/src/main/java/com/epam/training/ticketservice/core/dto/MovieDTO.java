package com.epam.training.ticketservice.core.dto;

import lombok.Value;

@Value
public class MovieDTO {
    String title;
    String genre;
    Integer runTime;
}
