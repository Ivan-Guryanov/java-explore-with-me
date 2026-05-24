package ru.practicum;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class StatsConfig {

    @Value("${stats-server.url:http://localhost:9090}")
    private String statsServerUrl;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri(statsServerUrl)
                .build();
    }
}