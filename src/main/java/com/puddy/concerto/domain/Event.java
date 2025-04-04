package com.puddy.concerto.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "events")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "artist_name", nullable = false)
    private String artistName;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(name= "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name= "event_time", nullable = false)
    private LocalTime eventTime;

    @Column(name = "ticket_price", nullable = false)
    private BigDecimal ticketPrice;
}
