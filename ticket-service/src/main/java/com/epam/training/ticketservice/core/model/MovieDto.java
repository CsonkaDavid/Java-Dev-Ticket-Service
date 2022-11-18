package com.epam.training.ticketservice.core.model;

import lombok.Value;

@Value
public class MovieDto {
    String title;
    String genre;
    Integer runTime;
}
