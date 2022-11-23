package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.entity.Movie;
import com.epam.training.ticketservice.core.entity.PriceComponent;
import com.epam.training.ticketservice.core.entity.Room;
import com.epam.training.ticketservice.core.entity.Screening;
import com.epam.training.ticketservice.core.model.MovieDto;
import com.epam.training.ticketservice.core.model.PriceComponentDto;
import com.epam.training.ticketservice.core.model.RoomDto;
import com.epam.training.ticketservice.core.model.ScreeningDto;
import com.epam.training.ticketservice.core.repository.BasePriceRepository;
import com.epam.training.ticketservice.core.repository.MovieRepository;
import com.epam.training.ticketservice.core.repository.PriceComponentRepository;
import com.epam.training.ticketservice.core.repository.RoomRepository;
import com.epam.training.ticketservice.core.repository.ScreeningRepository;
import com.epam.training.ticketservice.core.time.ApplicationDateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriceComponentServiceImpl implements PriceComponentService {

    private final PriceComponentRepository priceComponentRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    private final ScreeningRepository screeningRepository;
    private final ApplicationDateFormatter dateFormatter;

    @Override
    public String createPriceComponent(PriceComponentDto priceComponentDto) {
        Optional<PriceComponent> existingPriceComponent =
                priceComponentRepository.findPriceComponentByName(priceComponentDto.getName());

        if (existingPriceComponent.isPresent()) {
            return "Price component " + priceComponentDto + " already exists";
        }

        PriceComponent priceComponent = new PriceComponent(
                null,
                priceComponentDto.getName(),
                priceComponentDto.getAmount());

        priceComponentRepository.save(priceComponent);

        return priceComponentDto + " saved";
    }

    @Override
    public Optional<PriceComponentDto> findPriceComponentByName(String name) {
        Optional<PriceComponent> priceComponent = priceComponentRepository.findPriceComponentByName(name);

        if (priceComponent.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new PriceComponentDto(priceComponent.get().getName(), priceComponent.get().getAmount()));
    }

    @Override
    public void updateMoviePriceComponent(MovieDto movieDto, PriceComponentDto priceComponentDto) {
        Movie movie = movieRepository.findByTitle(movieDto.getTitle())
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));

        PriceComponent priceComponent = priceComponentRepository
                .findPriceComponentByName(priceComponentDto.getName())
                .orElseThrow(() -> new IllegalArgumentException("Price component not found"));

        movieRepository.updateMoviePriceComponent(movie, priceComponent);
    }

    @Override
    public void updateRoomPriceComponent(RoomDto roomDto, PriceComponentDto priceComponentDto) {
        Room room = roomRepository.findByName(roomDto.getName())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        PriceComponent priceComponent = priceComponentRepository
                .findPriceComponentByName(priceComponentDto.getName())
                .orElseThrow(() -> new IllegalArgumentException("Price component not found"));

        roomRepository.updateRoomPriceComponent(room, priceComponent);
    }

    @Override
    public void updateScreeningPriceComponent(ScreeningDto screeningDto, PriceComponentDto priceComponentDto) {
        Movie movie = movieRepository.findByTitle(screeningDto.getMovieTitle())
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));

        Room room = roomRepository.findByName(screeningDto.getRoomName())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        Date date = dateFormatter.parseStringToDate(screeningDto.getFormattedDateTime())
                .orElseThrow(() -> new IllegalArgumentException("Can't parse date"));

        Screening screening = screeningRepository.findByMovieAndRoomAndDate(movie, room, date)
                .orElseThrow(() -> new IllegalArgumentException("Screening not found"));

        PriceComponent priceComponent = priceComponentRepository
                .findPriceComponentByName(priceComponentDto.getName())
                .orElseThrow(() -> new IllegalArgumentException("Price component not found"));

        screeningRepository.updateScreeningPriceComponent(screening, priceComponent);
    }
}
