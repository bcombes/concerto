package com.puddy.concerto.persistence;

import com.puddy.concerto.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, String> {
}

