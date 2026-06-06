package ru.practicum.requests.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.events.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ParticipationRequestTest {

    @Test
    @DisplayName("Модель ParticipationRequest. Проверка билдера, геттеров и сеттеров.")
    void testParticipationRequestStructure() {
        Event event = new Event();
        event.setId(10L);

        User requester = new User();
        requester.setId(5L);

        LocalDateTime now = LocalDateTime.now();

        ParticipationRequest request = ParticipationRequest.builder()
                .id(1L)
                .created(now)
                .event(event)
                .requester(requester)
                .status(RequestStatus.PENDING)
                .build();

        assertThat(request.getId()).isEqualTo(1L);
        assertThat(request.getCreated()).isEqualTo(now);
        assertThat(request.getEvent().getId()).isEqualTo(10L);
        assertThat(request.getRequester().getId()).isEqualTo(5L);
        assertThat(request.getStatus()).isEqualTo(RequestStatus.PENDING);

        request.setId(2L);
        request.setStatus(RequestStatus.CONFIRMED);

        assertThat(request.getId()).isEqualTo(2L);
        assertThat(request.getStatus()).isEqualTo(RequestStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Модель ParticipationRequest. Проверка логики equals для базовых сценариев.")
    void testEquals() {
        ParticipationRequest req1 = new ParticipationRequest();
        req1.setId(1L);

        ParticipationRequest req2 = new ParticipationRequest();
        req2.setId(1L);

        ParticipationRequest req3 = new ParticipationRequest();
        req3.setId(2L);

        assertThat(req1.equals(req1)).isTrue();
        assertThat(req1.equals(req2)).isTrue();
        assertThat(req1.equals(req3)).isFalse();
        assertThat(req1.equals(null)).isFalse();
        assertThat(req1.equals("string object")).isFalse();
    }

    @Test
    @DisplayName("Модель ParticipationRequest. Проверка работы метода hashCode.")
    void testHashCode() {
        ParticipationRequest req1 = new ParticipationRequest();
        req1.setId(1L);

        ParticipationRequest req2 = new ParticipationRequest();
        req2.setId(1L);

        assertThat(req1.hashCode()).isEqualTo(req2.hashCode());
    }
}
