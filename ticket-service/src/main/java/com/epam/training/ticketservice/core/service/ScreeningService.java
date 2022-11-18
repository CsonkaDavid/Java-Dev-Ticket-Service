package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.model.MovieDto;
import com.epam.training.ticketservice.core.model.RoomDto;
import com.epam.training.ticketservice.core.model.ScreeningDto;

import java.util.Date;
import java.util.List;

public interface ScreeningService {
    void createScreening(ScreeningDto screeningDTO);
    void deleteScreening(MovieDto movieDTO, RoomDto roomDTO, Date date);
    List<ScreeningDto> getScreeningList();
}
