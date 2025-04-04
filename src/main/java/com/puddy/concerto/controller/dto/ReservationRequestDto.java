package com.puddy.concerto.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ReservationRequestDto {
    @NotNull(message = "User ID cannot be null")
    private UUID userId;

    @NotNull(message = "Event ID cannot be null")
    private UUID eventId;

    private String reservedName;

    @NotBlank(message = "Seat ID is required")
    private String seatId;
}
