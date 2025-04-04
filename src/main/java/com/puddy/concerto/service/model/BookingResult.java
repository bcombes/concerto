package com.puddy.concerto.service.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookingResult {
    String seatId;
    String statusCode;
    String statusMessage;
    private UUID bookingId;
}
