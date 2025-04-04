package com.puddy.concerto.service.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReservationResult {
    String seatId;
    String statusCode;
    String statusMessage;
}
