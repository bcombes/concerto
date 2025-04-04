package com.puddy.concerto.controller.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AvailableSeatsResponseDto {
    private UUID eventId;
    private String eventName;
    private List<String> seats;
}
