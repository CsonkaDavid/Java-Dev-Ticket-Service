package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.model.MovieDto;
import com.epam.training.ticketservice.core.model.RoomDto;
import com.epam.training.ticketservice.core.model.ScreeningDto;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ScreeningService {
    String createScreening(ScreeningDto screeningDto);

    void deleteScreening(MovieDto movieDto, RoomDto roomDto, Date date);

    List<ScreeningDto> getScreeningList();

    Optional<ScreeningDto> findScreeningByMovieAndRoomAndDate(MovieDto movieDto, RoomDto roomDto, String dateTime);
}
