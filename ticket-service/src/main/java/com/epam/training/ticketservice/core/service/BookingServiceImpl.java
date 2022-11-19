package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.entity.Booking;
import com.epam.training.ticketservice.core.entity.BookingSeat;
import com.epam.training.ticketservice.core.entity.Movie;
import com.epam.training.ticketservice.core.entity.Room;
import com.epam.training.ticketservice.core.entity.Screening;
import com.epam.training.ticketservice.core.entity.User;
import com.epam.training.ticketservice.core.model.BookingDto;
import com.epam.training.ticketservice.core.model.ScreeningDto;
import com.epam.training.ticketservice.core.model.UserDto;
import com.epam.training.ticketservice.core.repository.BookingRepository;
import com.epam.training.ticketservice.core.repository.ScreeningRepository;
import com.epam.training.ticketservice.core.repository.MovieRepository;
import com.epam.training.ticketservice.core.repository.RoomRepository;
import com.epam.training.ticketservice.core.repository.UserRepository;
import com.epam.training.ticketservice.core.time.ApplicationDateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;
import java.util.Date;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ScreeningRepository screeningRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ApplicationDateFormatter applicationDateFormatter;

    @Override
    public Optional<String> findBookings(UserDto userDto) {
        User user = userRepository.findByUsername(userDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Booking> bookingList = bookingRepository.findAllByUser(user);

        if (bookingList.isEmpty()) {
            return Optional.empty();
        }

        List<BookingDto> bookingDtoList = bookingList.stream()
                .map(this::convertBookingToDto)
                .collect(Collectors.toList());

        return Optional.of(convertBookingDtoListToString(bookingDtoList));
    }

    @Override
    public String book(UserDto userDto, ScreeningDto screeningDto, String seats) {

        User user = userRepository.findByUsername(userDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Movie movie = movieRepository.findByTitle(screeningDto.getMovieTitle())
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));

        Room room = roomRepository.findByName(screeningDto.getRoomName())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        Date date = applicationDateFormatter.parseStringToDate(screeningDto.getFormattedDateTime())
                .orElseThrow(() -> new IllegalArgumentException("Cannot parse date "
                        + screeningDto.getFormattedDateTime()));

        Screening screening = screeningRepository.findByMovieAndRoomAndDate(movie, room, date)
                .orElseThrow(() -> new IllegalArgumentException("Requested screening does not exist"));

        List<String> splitStringList = Arrays.stream(seats.split(" "))
                .collect(Collectors.toList());

        List<BookingSeat> bookingSeatList = convertSeatStringListToBookingSeatList(splitStringList);
        int price = calculateBookingPrice(bookingSeatList);

        Booking booking = new Booking(null, user, screening, bookingSeatList, price);

        Optional<BookingSeat> missingSeat = checkForMissingSeat(bookingSeatList, booking);
        Optional<BookingSeat> takenSeat = checkForTakenSeat(bookingSeatList, booking);

        if (missingSeat.isPresent()) {
            return "Seat (" + missingSeat.get().getRow() + " ," + missingSeat.get().getColumn()
                    + ") does not exist in this room";
        }

        if (takenSeat.isPresent()) {
            return "Seat (" + takenSeat.get().getRow() + " ," + takenSeat.get().getColumn()
                    + ") is already taken";
        }

        bookingRepository.save(booking);

        String seatListString = convertSeatStringListToString(splitStringList);

        return "Seats booked: " + seatListString + "; the price for this booking is " + price + " HUF";
    }

    private int calculateBookingPrice(List<BookingSeat> seatList) {
        return 1500 * seatList.size();
    }

    private String convertSeatStringListToString(List<String> splitStringList) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < splitStringList.size() - 1; i++) {
            String[] seat = splitStringList.get(i).split(",");

            stringBuilder
                    .append("(")
                    .append(seat[0])
                    .append(", ")
                    .append(seat[1])
                    .append("), ");
        }

        String[] lastSeat = splitStringList
                .get(splitStringList.size() - 1)
                .split(",");

        stringBuilder
                .append("(")
                .append(lastSeat[0])
                .append(", ")
                .append(lastSeat[1])
                .append(")");

        return stringBuilder.toString();
    }

    private Optional<BookingSeat> checkForMissingSeat(List<BookingSeat> bookingSeatList, Booking booking) {
        Optional<BookingSeat> toReturn = Optional.empty();
        Room bookingRoom = booking.getScreening().getRoom();

        for (BookingSeat currentSeat : bookingSeatList) {
            if ((currentSeat.getRow() > bookingRoom.getRows() || currentSeat.getRow() <= 0)
                    || (currentSeat.getColumn() > bookingRoom.getColumns() || currentSeat.getColumn() <= 0)) {

                toReturn = Optional.of(currentSeat);
                break;
            }
        }

        return toReturn;
    }

    private Optional<BookingSeat> checkForTakenSeat(List<BookingSeat> bookingSeatList, Booking booking) {
        Optional<BookingSeat> toReturn = Optional.empty();

        List<Booking> bookingsForScreening = bookingRepository.findAllByScreening(booking.getScreening());

        for (Booking current : bookingsForScreening) {
            List<BookingSeat> currentTakenSeats = current.getSeats();

            for (BookingSeat currentSeat : bookingSeatList) {

                for (BookingSeat currentTakenSeat : currentTakenSeats) {

                    if (Objects.equals(currentTakenSeat.getRow(), currentSeat.getRow())
                            && Objects.equals(currentTakenSeat.getColumn(), currentSeat.getColumn())) {

                        toReturn = Optional.of(currentSeat);
                        break;
                    }
                }
            }
        }

        return toReturn;
    }

    private List<BookingSeat> convertSeatStringListToBookingSeatList(List<String> seats) {
        return seats.stream()
                .map(b -> {
                    String[] seat = b.split(",");

                    Integer row = Integer.valueOf(seat[0]);
                    Integer column = Integer.valueOf(seat[1]);

                    return new BookingSeat(null, row, column);

                })
                .collect(Collectors.toList());
    }

    private String convertBookingDtoListToString(List<BookingDto> bookingDtoList) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < bookingDtoList.size() - 1; i++) {
            BookingDto bookingDto = bookingDtoList.get(i);

            String movie = bookingDto.getMovieTitle();
            String room = bookingDto.getRoomName();
            String date = bookingDto.getFormattedDateTime();
            String seats = bookingDto.getSeats();
            Integer price = bookingDto.getPrice();

            stringBuilder
                    .append("Seats ")
                    .append(seats)
                    .append(" on ")
                    .append(movie)
                    .append(" in room ")
                    .append(room)
                    .append(" starting at ")
                    .append(date)
                    .append(" for ")
                    .append(price)
                    .append(" HUF")
                    .append("\n");
        }

        BookingDto lastBookingDto = bookingDtoList.get(bookingDtoList.size() - 1);

        stringBuilder
                .append("Seats ")
                .append(lastBookingDto.getSeats())
                .append(" on ")
                .append(lastBookingDto.getMovieTitle())
                .append(" in room ")
                .append(lastBookingDto.getRoomName())
                .append(" starting at ")
                .append(lastBookingDto.getFormattedDateTime())
                .append(" for ")
                .append(lastBookingDto.getPrice())
                .append(" HUF")
                .append("\n");

        return stringBuilder.toString();
    }

    private List<String> convertBookingSeatListToSeatStringList(List<BookingSeat> seats) {
        return seats.stream()
                .map(b -> b.getRow() + "," + b.getColumn())
                .collect(Collectors.toList());
    }

    private BookingDto convertBookingToDto(Booking booking) {
        Screening screening = booking.getScreening();
        String movie = screening.getMovie().getTitle();
        String room = screening.getRoom().getName();
        String date = applicationDateFormatter.convertDateToString(screening.getDate());
        String seats = convertSeatStringListToString(
                convertBookingSeatListToSeatStringList(
                        booking.getSeats()));

        return new BookingDto(movie, room, date, seats, booking.getPrice());
    }
}
