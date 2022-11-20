package com.epam.training.ticketservice.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

@Entity
@Data
@Table(name = "booking_seat")
@NoArgsConstructor
@AllArgsConstructor
public class BookingSeat {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "seat_row")
    private Integer seatRow;

    @Column(name = "seat_column")
    private Integer seatColumn;
}
