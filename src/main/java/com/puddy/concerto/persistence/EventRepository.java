package com.puddy.concerto.persistence;

import com.puddy.concerto.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
}

