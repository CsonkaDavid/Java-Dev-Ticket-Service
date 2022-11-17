package com.epam.training.ticketservice.core.model;

import lombok.Value;

@Value
public class RoomDTO {
    String name;
    Integer rows;
    Integer columns;
}
