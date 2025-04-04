package com.puddy.concerto.domain;

public enum SeatStatus {
    AVAILABLE,
    BOOKED,
    RESERVED; //No payment has been received, reserve seat for a while
}
