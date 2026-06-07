package ru.practicum.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    @Query("""
            SELECT r
            FROM ParticipationRequest r
            WHERE r.event.id = :eventId
            AND r.requester.id = :userId
            """)
    ParticipationRequest findByEventAndRequester(@Param("eventId") Long eventId,
                                                 @Param("userId") Long userId);

    List<ParticipationRequest> findByRequesterId(Long userId);

    List<ParticipationRequest> findByEventId(Long eventId);

    List<ParticipationRequest> findByIdIn(List<Long> rs);

    int countByEventIdAndStatus(Long eventId, RequestStatus status);
}