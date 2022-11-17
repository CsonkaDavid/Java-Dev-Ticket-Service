package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.model.MovieDTO;
import com.epam.training.ticketservice.core.model.RoomDTO;
import com.epam.training.ticketservice.core.model.ScreeningDTO;

import java.util.Date;
import java.util.List;

public interface ScreeningService {
    void createScreening(ScreeningDTO screeningDTO);
    void deleteScreening(MovieDTO movieDTO, RoomDTO roomDTO, Date date);
    List<ScreeningDTO> listScreenings();
}
