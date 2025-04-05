package com.puddy.concerto.service;

import com.puddy.concerto.domain.Event;
import com.puddy.concerto.domain.EventSeatAvailability;
import com.puddy.concerto.domain.Seat;
import com.puddy.concerto.domain.SeatStatus;
import com.puddy.concerto.persistence.EventRepository;
import com.puddy.concerto.persistence.EventSeatAvailabilityRepository;
import com.puddy.concerto.persistence.SeatRepository;
import com.puddy.concerto.service.model.Booking;
import com.puddy.concerto.service.model.BookingResult;
import com.puddy.concerto.service.model.Reservation;
import com.puddy.concerto.service.model.ReservationResult;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EventBookingServiceTests {

    @Mock
    private EventSeatAvailabilityRepository eventSeatAvailabilityRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventBookingService bookingService;

    private UUID eventId;
    private String seatId;
    private UUID userId;
    private String reservedName;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        eventId = UUID.randomUUID();
        userId = UUID.randomUUID();
        seatId = "A1";
        reservedName = "John Doe";
    }

    @Test
    @DisplayName("Should successfully reserve a seat if available")
    void testRequestSeatReservationSuccess() {
        Reservation reservation = new Reservation(userId, eventId, reservedName, seatId);
        Seat seat = new Seat(seatId);
        Event event = new Event();

        when(eventSeatAvailabilityRepository.findByEventIdAndSeatId(eventId, seatId)).thenReturn(null);
        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        ReservationResult result = bookingService.requestSeatReservation(reservation);

        assertNotNull(result);
        assertEquals("0", result.getStatusCode());
        //verify(eventSeatAvailabilityRepository).save(any(EventSeatAvailability.class));
    }

    @Test
    @DisplayName("Should fail when seat is already reserved")
    void testRequestSeatReservationAlreadyReserved() {
        Reservation reservation = new Reservation(userId, eventId, reservedName, seatId);
        EventSeatAvailability existing = new EventSeatAvailability();
        when(eventSeatAvailabilityRepository.findByEventIdAndSeatId(eventId, seatId)).thenReturn(existing);

        ReservationResult result = bookingService.requestSeatReservation(reservation);

        assertNotNull(result);
        assertEquals("-2", result.getStatusCode());
       // verify(eventSeatAvailabilityRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException if seat not found")
    void testSeatNotFound() {
        Reservation reservation = new Reservation(userId, eventId, reservedName, seatId);
        when(eventSeatAvailabilityRepository.findByEventIdAndSeatId(eventId, seatId)).thenReturn(null);
        when(seatRepository.findById(seatId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> { bookingService.requestSeatReservation(reservation);
            });
        assertTrue(exception.getMessage().contains("Seat with this id not found"));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException if event not found")
    void testEventNotFound() {
        Reservation reservation = new Reservation(userId, eventId, reservedName, seatId);
        Seat seat = new Seat(seatId);
        when(eventSeatAvailabilityRepository.findByEventIdAndSeatId(eventId, seatId)).thenReturn(null);
        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.requestSeatReservation(reservation));
        assertTrue(exception.getMessage().contains("Event with this id not found"));
    }

    @Test
    @DisplayName("Should successfully update seat status from RESERVED to BOOKED")
    void testUpdateSeatStatus_SuccessfulBooking() {
        Booking booking = new Booking(eventId, seatId, userId);
        EventSeatAvailability seatAvailability = EventSeatAvailability.builder()
                .id(UUID.randomUUID())
                .seatStatus(SeatStatus.RESERVED)
                .build();

        when(eventSeatAvailabilityRepository.findByEventIdAndSeatId(eventId, seatId)).thenReturn(seatAvailability);

        BookingResult result = bookingService.updateSeatStatus(booking);

        assertNotNull(result);
        assertEquals("0", result.getStatusCode());
        assertEquals(seatId, result.getSeatId());
        assertEquals(seatAvailability.getId(), result.getBookingId());
        assertEquals("Seating ticket has been booked successfully", result.getStatusMessage());

        assertEquals(SeatStatus.BOOKED, seatAvailability.getSeatStatus());
    }

    @Test
    @DisplayName("Should return error if seat status is not RESERVED")
    void testUpdateSeatStatus_SeatNotReserved() {
        Booking booking = new Booking(eventId, seatId, userId);
        EventSeatAvailability seatAvailability = EventSeatAvailability.builder()
                .id(UUID.randomUUID())
                .seatStatus(SeatStatus.BOOKED) // Already booked
                .build();

        when(eventSeatAvailabilityRepository.findByEventIdAndSeatId(eventId, seatId)).thenReturn(seatAvailability);

        BookingResult result = bookingService.updateSeatStatus(booking);

        assertNotNull(result);
        assertEquals("-1", result.getStatusCode());
        assertEquals("Either there is no reservation for this seat or it is no longer available", result.getStatusMessage());

    }

    @Test
    @DisplayName("Should return error if no seat reservation exists")
    void testUpdateSeatStatus_ReservationNotFound() {
        Booking booking = new Booking(eventId, seatId, userId);

        when(eventSeatAvailabilityRepository.findByEventIdAndSeatId(eventId, seatId)).thenReturn(null);

        BookingResult result = bookingService.updateSeatStatus(booking);

        assertNotNull(result);
        assertEquals("-1", result.getStatusCode());
        assertEquals("Either there is no reservation for this seat or it is no longer available", result.getStatusMessage());

    }
}
