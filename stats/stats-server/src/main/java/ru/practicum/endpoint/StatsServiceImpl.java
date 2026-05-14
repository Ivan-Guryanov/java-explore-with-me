package ru.practicum.endpoint;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl {
    private final StatsRepository statsRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public void createEndpoint(EndpointHitDto endpointHitDto) {
        statsRepository.save(EndpointHitMapper.mapToEndpointHit(endpointHitDto));
    }

    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {

        LocalDateTime startTime = LocalDateTime.parse(start, FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(end, FORMATTER);

        List<EndpointHit> allHits = statsRepository.findByUriInAndTimestampBetween(uris, startTime, endTime);

        if (unique) {
            allHits = allHits.stream()
                    .collect(Collectors.groupingBy(
                            hit -> List.of(hit.getApp(), hit.getUri(), hit.getIp()),
                            Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    list -> list.get(0)
                            )
                    ))
                    .values()
                    .stream()
                    .collect(Collectors.toList());
        }

        List<ViewStatsDto> getStats = allHits.stream()
                .collect(Collectors.groupingBy(hit -> List.of(hit.getApp(), hit.getUri()), Collectors.counting()))
                .entrySet().stream()
                .map(entry -> {
                    String app = entry.getKey().get(0);
                    String uri = entry.getKey().get(1);
                    Integer count = entry.getValue().intValue();

                    return new ViewStatsDto(app, uri, count);
                })

                .collect(Collectors.toList());

        return getStats;
    }
}