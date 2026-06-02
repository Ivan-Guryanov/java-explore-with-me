package ru.practicum.events.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.events.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("""
            SELECT e
            FROM Event e
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

    @Query(
            value = """
                    SELECT *
                    FROM events
                    WHERE state = 'PUBLISHED'
                      AND (
                            :textIsBlank = true
                            OR LOWER(annotation) LIKE LOWER(CONCAT('%', :text, '%'))
                            OR LOWER(description) LIKE LOWER(CONCAT('%', :text, '%'))
                      )
                      AND (:categoriesIsEmpty = true OR category_id IN (:categories))
                      AND (:paidIsNull OR paid = :paid)
                      AND event_date >= :rangeStart
                      AND event_date <= :rangeEnd
                      AND (
                            :onlyAvailable = false
                            OR participant_limit = 0
                            OR confirmed_requests < participant_limit
                      )
                    ORDER BY
                      CASE WHEN :sort = 'EVENT_DATE' THEN event_date END ASC,
                      CASE WHEN :sort = 'VIEWS' THEN views END DESC,
                      id ASC
                    LIMIT :size OFFSET :from
                    """,
            nativeQuery = true
    )
    List<Event> getEventsByPublic(@Param("text") String text,
                                  @Param("textIsBlank") boolean textIsBlank,
                                  @Param("categories") List<Long> categories,
                                  @Param("categoriesIsEmpty") boolean categoriesIsEmpty,
                                  @Param("paid") Boolean paid,
                                  @Param("paidIsNull") boolean paidIsNull,
                                  @Param("rangeStart") LocalDateTime rangeStart,
                                  @Param("rangeEnd") LocalDateTime rangeEnd,
                                  @Param("onlyAvailable") boolean onlyAvailable,
                                  @Param("sort") String sort,
                                  @Param("from") int from,
                                  @Param("size") int size);


}