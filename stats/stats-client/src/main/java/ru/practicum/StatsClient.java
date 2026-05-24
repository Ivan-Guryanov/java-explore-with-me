package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;


import java.util.*;


@Service
@RequiredArgsConstructor
public class StatsClient {
    private final RestTemplate restTemplate;

    public void saveHit(EndpointHitDto endpointHitDto) {
        ResponseEntity<Void> response = restTemplate.postForEntity("/hit", endpointHitDto, Void.class);

    }

    public ResponseEntity<List<ViewStatsDto>> getStats(String start, String end, List<String> uris, boolean unique) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            builder.queryParam("uris", String.join(",", uris));
        }

        String url = builder.build(false).toUriString();

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ViewStatsDto>>() {}
        );
    }
}