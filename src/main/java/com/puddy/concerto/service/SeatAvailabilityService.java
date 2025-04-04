package com.puddy.concerto.service;

import com.puddy.concerto.domain.EventSeatAvailability;
import com.puddy.concerto.domain.Seat;
import com.puddy.concerto.domain.SeatStatus;
import com.puddy.concerto.persistence.EventSeatAvailabilityRepository;
import com.puddy.concerto.persistence.SeatRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SeatAvailabilityService {

    EventSeatAvailabilityRepository eventSeatAvailabilityRepository;
    SeatRepository seatRepository;

    public List<Seat> fetchAvailableSeatsForEvent(UUID eventId) {

        List<Seat> availableSeats = seatRepository.findAll();

        List<Seat> bookedOrPendingSeats = eventSeatAvailabilityRepository.findByEventIdAndSeatStatusIn(eventId, List.of(SeatStatus.BOOKED, SeatStatus.RESERVED))
                .stream().map(EventSeatAvailability::getSeat)
                .toList();

        availableSeats.removeAll(bookedOrPendingSeats);

        return availableSeats;
    }
}
