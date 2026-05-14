package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;


import java.util.*;


@Service
public class StatsClient {
    private final RestTemplate restTemplate;

    public StatsClient(@Value("${stats-server.url}") String serverUrl) {
        this.restTemplate = new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .build();
    }

    public ResponseEntity<Void> saveHit(EndpointHitDto endpointHitDto) {
ResponseEntity<Void> response = restTemplate.postForEntity("/hit", endpointHitDto, Void.class);

        return response;
    }

    public ResponseEntity<List<ViewStatsDto>> getStats(String start, String end, List<String> uris, boolean unique) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            builder.queryParam("uris", String.join(",", uris));
        }
        return restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
    }
}