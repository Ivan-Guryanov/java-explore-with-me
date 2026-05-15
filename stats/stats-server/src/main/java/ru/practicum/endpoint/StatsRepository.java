package ru.practicum.endpoint;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    List<EndpointHit> findByUriInAndTimestampBetween(List<String> uris, LocalDateTime start, LocalDateTime end);

    List<EndpointHit> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
