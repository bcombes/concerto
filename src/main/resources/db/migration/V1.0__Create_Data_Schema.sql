CREATE TABLE events (
    id UUID PRIMARY KEY NOT NULL,
    event_name VARCHAR(255) NOT NULL,
    artist_name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    event_date DATE NOT NULL,
    event_time TIME NOT NULL,
    ticket_price DECIMAL(19, 2) NOT NULL
);

CREATE TABLE seats (
    id VARCHAR(3) PRIMARY KEY NOT NULL
);

CREATE TABLE event_seat_availability (
    id UUID PRIMARY KEY NOT NULL,
    user_id UUID,
    reserved_name VARCHAR(255),
    event_id UUID NOT NULL,
    seat_id VARCHAR(3) NOT NULL,
    reservation_timestamp TIMESTAMP,
    seat_status VARCHAR(255) NOT NULL,
    FOREIGN KEY (event_id) REFERENCES events(id),
    FOREIGN KEY (seat_id) REFERENCES seats(id)
);
