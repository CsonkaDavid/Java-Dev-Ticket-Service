package com.epam.training.ticketservice.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import java.util.Date;

@Entity
@Data
@Table(name = "screening")
@NoArgsConstructor
@AllArgsConstructor
public class Screening {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private Movie movie;

    @ManyToOne
    private Room room;

    @DateTimeFormat(pattern = "YYYY-MM-DD hh:mm")
    private Date date;
}
