package com.puddy.concerto.service.model;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Booking {
    private UUID eventId;
    private String seatId;
    private UUID userId;
}
