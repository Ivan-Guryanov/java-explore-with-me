package ru.practicum.events.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("""
            SELECT e FROM Event e 
            WHERE (:usersId IS NULL OR e.initiator.id IN :usersId) 
            AND (:states IS NULL OR e.state IN :states) 
            AND (:categoriesId IS NULL OR e.category.id IN :categoriesId) 
            AND (CAST(:rangeStart AS timestamp) IS NULL OR e.eventDate >= :rangeStart) 
            AND (CAST(:rangeEnd AS timestamp) IS NULL OR e.eventDate <= :rangeEnd)
            """)
    List<Event> findEventsByParam(@Param("usersId") List<Long> usersId,
                                  @Param("states") List<String> states,
                                  @Param("categoriesId") List<Long> categoriesId,
                                  @Param("rangeStart") LocalDateTime rangeStart,
                                  @Param("rangeEnd") LocalDateTime rangeEnd,
                                  Pageable pageable);

    @Query("""
            SELECT e FROM Event e 
            WHERE (:userId IS NULL OR e.initiator.id IN :userId) 
            """)
    List<Event> findEventByUser(@Param("userId") Long userId,
                                Pageable pageable);
}