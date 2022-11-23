package com.epam.training.ticketservice.core.model;

import lombok.Value;

@Value
public class PriceComponentDto {
    String name;
    Integer amount;
}
