package com.epam.training.ticketservice.core.model;

import lombok.Value;

@Value
public class MovieDTO {
    String title;
    String genre;
    Integer runTime;
}
