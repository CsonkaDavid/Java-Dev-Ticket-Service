package com.epam.training.ticketservice.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "base_price")
@AllArgsConstructor
@NoArgsConstructor
public class BasePrice {
    @Id
    private Integer id;

    private Integer amount;
}
