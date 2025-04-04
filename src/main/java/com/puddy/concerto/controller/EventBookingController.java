package com.puddy.concerto.controller;

import com.puddy.concerto.controller.dto.AvailableSeatsResponseDto;
import com.puddy.concerto.controller.dto.BookingRequestDto;
import com.puddy.concerto.controller.dto.ReservationRequestDto;
import com.puddy.concerto.domain.Event;
import com.puddy.concerto.domain.Seat;
import com.puddy.concerto.service.EventBookingService;
import com.puddy.concerto.service.SeatAvailabilityService;
import com.puddy.concerto.service.model.Booking;
import com.puddy.concerto.service.model.BookingResult;
import com.puddy.concerto.service.model.Reservation;
import com.puddy.concerto.service.model.ReservationResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.propertyeditors.UUIDEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events")
@AllArgsConstructor
public class EventBookingController {


    SeatAvailabilityService seatAvailabilityService;
    EventBookingService eventBookingService;
    ModelMapper modelMapper;

    @GetMapping(value="/{eventId}/seats_available", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AvailableSeatsResponseDto> getSeatsByStatus(@PathVariable @NotNull UUID eventId) {

        List<String> seats = seatAvailabilityService.fetchAvailableSeatsForEvent(eventId)
                .stream().map(Seat::getId).toList();

        Event event = eventBookingService.getEventDetails(eventId);

        AvailableSeatsResponseDto availableSeatsResponse = AvailableSeatsResponseDto.builder()
                .eventId(event.getId())
                .eventName(event.getEventName())
                .seats(seats)
                .build();

        return ResponseEntity.ok(availableSeatsResponse);
    }

    @PostMapping(value = "/make_reservation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReservationResult> makeReservation(@RequestBody @Valid ReservationRequestDto requestDto) {

        Reservation reservation = modelMapper.map(requestDto, Reservation.class);

        ReservationResult result = eventBookingService.requestSeatReservation(reservation);

        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/finalize_booking", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookingResult> finalizeBooking(@RequestBody BookingRequestDto requestDto) {

        Booking booking = modelMapper.map(requestDto, Booking.class);

        BookingResult result = eventBookingService.updateSeatStatus(booking);

        return ResponseEntity.ok(result);
    }
}
