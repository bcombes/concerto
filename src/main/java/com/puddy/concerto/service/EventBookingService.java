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
import jakarta.persistence.PersistenceException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EventBookingService {

    EventSeatAvailabilityRepository eventSeatAvailabilityRepository;
    SeatRepository seatRepository;
    EventRepository eventRepository;


    public List<Event> getAllEvents() {

        return eventRepository.findAll();
    }

    public Event getEventDetails(UUID eventId) {

        Event event =  eventRepository.findById(eventId).orElse(null);

        if(event == null) {
            throw new EntityNotFoundException("Event with this id not found : " + eventId);
        }

        return event;
    }

    //Request Seat Reservations
    public ReservationResult requestSeatReservation(Reservation reservation) {
        ReservationResult reservationResult = null;

        EventSeatAvailability seatAvailability = eventSeatAvailabilityRepository.findByEventIdAndSeatId(reservation.getEventId(),
                reservation.getSeatId());

        //Seat is still available, go ahead and book it
        if(seatAvailability == null) {
            try {
                Optional<Seat> seatNullable = seatRepository.findById(reservation.getSeatId());

                if(seatNullable.isEmpty()) {
                    throw new EntityNotFoundException("Seat with this id not found : " + reservation.getSeatId());
                }

                Optional<Event> eventNullable = eventRepository.findById(reservation.getEventId());

                if(eventNullable.isEmpty()) {
                    throw new EntityNotFoundException("Event with this id not found : " + reservation.getEventId());
                }

                seatAvailability = EventSeatAvailability.builder()
                        .seat(seatNullable.get())
                        .event(eventNullable.get())
                        .userId(reservation.getUserId())
                        .reservationTimestamp(LocalDateTime.now())
                        .seatStatus(SeatStatus.RESERVED)
                        .build();

                eventSeatAvailabilityRepository.save(seatAvailability);

                reservationResult = ReservationResult.builder()
                        .seatId(reservation.getSeatId())
                        .statusCode("0")
                        .statusMessage("Seat reservation will be held for x minutes")
                        .build();

            } catch(EntityNotFoundException enfe) {
              throw enfe;

            } catch(PersistenceException ex) {
                reservationResult = ReservationResult.builder()
                        .seatId(reservation.getSeatId())
                        .statusCode("-1")
                        .statusMessage(String.format("%s : there was an error creating a reservation", ex.getMessage()))
                        .build();
            }

        } else {
            reservationResult = ReservationResult.builder()
                    .seatId(reservation.getSeatId())
                    .statusCode("-2")
                    .statusMessage("This seat is currently not available, please check and confirm availability")
                    .build();
        }

        return reservationResult;
    }

    public BookingResult updateSeatStatus(Booking booking) {
        BookingResult bookingResult = null;

            EventSeatAvailability seatAvailability = eventSeatAvailabilityRepository.findByEventIdAndSeatId(booking.getEventId(), booking.getSeatId());

            //Go ahead an update the seat status
            if(seatAvailability != null && seatAvailability.getSeatStatus().equals(SeatStatus.RESERVED)) {

                seatAvailability.setSeatStatus(SeatStatus.BOOKED);
                eventSeatAvailabilityRepository.save(seatAvailability);

                bookingResult = BookingResult.builder()
                        .seatId(booking.getSeatId())
                        .bookingId(seatAvailability.getId())
                        .statusCode("0")
                        .statusMessage("Seating ticket has been booked successfully")
                        .build();

            } else { //Otherwise return an error to the user...
                bookingResult = BookingResult.builder()
                        .seatId(booking.getSeatId())
                        .statusCode("-1")
                        .statusMessage("Either there is no reservation for this seat or it is no longer available")
                        .build();

            }

        return bookingResult;
    }
}