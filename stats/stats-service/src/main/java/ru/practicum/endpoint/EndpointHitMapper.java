package ru.practicum.endpoint;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EndpointHitMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static EndpointHit mapToEndpointHit(EndpointHitDto dto) {

        LocalDateTime parsedTimestamp = LocalDateTime.parse(dto.getTimestamp(), FORMATTER);

        return EndpointHit.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .timestamp(parsedTimestamp)
                .build();
    }
}