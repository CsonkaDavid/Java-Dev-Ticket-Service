package com.epam.training.ticketservice.core.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDAO {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(unique = true)
    private String title;
    private String genre;
    @Column(name = "run_time")
    private Integer runTime;
}
