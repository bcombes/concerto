package com.puddy.concerto.service.model;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Reservation {
    private UUID userId;
    private UUID eventId;
    private String reservedName;
    private String seatId;
}
