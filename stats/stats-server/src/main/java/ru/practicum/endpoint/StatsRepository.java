package ru.practicum.endpoint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("""
            SELECT new ru.practicum.dto.ViewStatsDto(l.app, l.uri, COUNT(DISTINCT l.ip))
            FROM EndpointHit l
            WHERE l.timestamp BETWEEN :start AND :end
                AND l.uri IN :uris
            GROUP BY l.uri, l.app
            ORDER BY COUNT(DISTINCT l.ip) DESC
            """)
    List<ViewStatsDto> getStatsUnique(LocalDateTime start,
                                      LocalDateTime end,
                                      List<String> uris);


    @Query("""
            SELECT new ru.practicum.dto.ViewStatsDto(l.app, l.uri, COUNT(l.ip))
            FROM EndpointHit l
            WHERE l.timestamp BETWEEN :start AND :end
                AND l.uri IN :uris
            GROUP BY l.uri, l.app
            ORDER BY COUNT(l.ip) DESC
            """)
    List<ViewStatsDto> getStats(LocalDateTime start,
                                      LocalDateTime end,
                                      List<String> uris);

    @Query("""
            SELECT new ru.practicum.dto.ViewStatsDto(l.app, l.uri, COUNT(DISTINCT l.ip))
            FROM EndpointHit l
            WHERE l.timestamp BETWEEN :start AND :end
            GROUP BY l.uri, l.app
            ORDER BY COUNT(DISTINCT l.ip) DESC
            """)
    List<ViewStatsDto> getStatsUniqueAll(LocalDateTime start,
                                      LocalDateTime end);


    @Query("""
            SELECT new ru.practicum.dto.ViewStatsDto(l.app, l.uri, COUNT(l.ip))
            FROM EndpointHit l
            WHERE l.timestamp BETWEEN :start AND :end
            GROUP BY l.uri, l.app
            ORDER BY COUNT(l.ip) DESC
            """)
    List<ViewStatsDto> getStatsAll(LocalDateTime start,
                                LocalDateTime end);
}
