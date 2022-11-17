package com.epam.training.ticketservice.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

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

    @JsonFormat(pattern = "YYYY-MM-DD hh:mm")
    private Date date;
}
