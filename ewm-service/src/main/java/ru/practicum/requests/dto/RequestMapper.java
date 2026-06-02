package ru.practicum.requests.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.requests.model.ParticipationRequest;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ParticipationRequestDto mapToParticipationRequestDto(ParticipationRequest r) {
        return ParticipationRequestDto.builder()
                .id(r.getId())
                .created(r.getCreated().format(formatter))
                .event(r.getEvent().getId())
                .requester(r.getRequester().getId())
                .status(r.getStatus().name())
                .build();
    }
}