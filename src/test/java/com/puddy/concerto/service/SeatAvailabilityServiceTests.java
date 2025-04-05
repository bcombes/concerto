package com.puddy.concerto.service;

import com.puddy.concerto.domain.EventSeatAvailability;
import com.puddy.concerto.domain.Seat;
import com.puddy.concerto.persistence.EventSeatAvailabilityRepository;
import com.puddy.concerto.persistence.SeatRepository;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("SeatAvailabilityService Unit Tests")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class SeatAvailabilityServiceTests {

    private EventSeatAvailabilityRepository eventSeatAvailabilityRepository;
    private SeatRepository seatRepository;
    private SeatAvailabilityService seatAvailabilityService;

    @BeforeEach
    void setUp() {
        eventSeatAvailabilityRepository = mock(EventSeatAvailabilityRepository.class);
        seatRepository = mock(SeatRepository.class);
        seatAvailabilityService = new SeatAvailabilityService(eventSeatAvailabilityRepository, seatRepository);
    }

    @Test
    @DisplayName("Should return only available seats when some are already reserved or booked")
    void fetchAvailableSeatsForEvent_shouldReturnAvailableSeats() {
        UUID eventId = UUID.randomUUID();

        Seat seatA = new Seat("A1");
        Seat seatB = new Seat("A2");
        Seat seatC = new Seat("A3");

        // All seats
        List<Seat> allSeats = new ArrayList<>(Arrays.asList(seatA, seatB, seatC));
        when(seatRepository.findAll()).thenReturn(allSeats);

        // Booked/Reserved seat is seatB
        EventSeatAvailability bookedSeat = new EventSeatAvailability();
        bookedSeat.setSeat(seatB);

        when(eventSeatAvailabilityRepository.findByEventIdAndSeatStatusIn(
                eq(eventId),
                ArgumentMatchers.anyList()
        )).thenReturn(List.of(bookedSeat));

        // Execute
        List<Seat> result = seatAvailabilityService.fetchAvailableSeatsForEvent(eventId);

        // Assert
        assertEquals(List.of(seatA, seatC), result);
        verify(seatRepository).findAll();
        verify(eventSeatAvailabilityRepository).findByEventIdAndSeatStatusIn(eq(eventId), ArgumentMatchers.anyList());
    }
}