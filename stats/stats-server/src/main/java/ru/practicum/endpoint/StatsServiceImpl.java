package ru.practicum.endpoint;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl {
    private final StatsRepository statsRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public void createEndpoint(EndpointHitDto endpointHitDto) {
        statsRepository.save(EndpointHitMapper.mapToEndpointHit(endpointHitDto));
    }

    @Transactional
    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {

        LocalDateTime startTime = LocalDateTime.parse(start, FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(end, FORMATTER);

        List<ViewStatsDto> getStats;


        if (uris == null || uris.isEmpty()) {
            getStats = (unique)
                    ? statsRepository.getStatsUniqueAll(startTime, endTime)
                    : statsRepository.getStatsAll(startTime, endTime);
        } else {
            getStats = (unique)
                    ? statsRepository.getStatsUnique(startTime, endTime, uris)
                    : statsRepository.getStats(startTime, endTime, uris);
        }

        return getStats;
    }
}