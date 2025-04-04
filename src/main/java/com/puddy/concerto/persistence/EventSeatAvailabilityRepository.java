package com.puddy.concerto.persistence;

import com.puddy.concerto.domain.EventSeatAvailability;
import com.puddy.concerto.domain.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventSeatAvailabilityRepository extends JpaRepository<EventSeatAvailability, UUID> {
    List<EventSeatAvailability> findByEventId(UUID eventId);

    List<EventSeatAvailability> findByEventIdAndSeatStatusIn(UUID eventId, List<SeatStatus> seatStatuses);

    EventSeatAvailability findByEventIdAndSeatId(UUID eventId, String seatId);


}
