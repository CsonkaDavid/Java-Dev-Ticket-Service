package com.epam.training.ticketservice.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "base_price")
@AllArgsConstructor
public class BasePrice {
    @Id
    private Integer id;

    public BasePrice() {
        id = 1;
    }

    private Integer amount;
}
