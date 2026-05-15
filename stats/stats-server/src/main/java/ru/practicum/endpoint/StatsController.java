package ru.practicum.endpoint;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class StatsController {
    private final StatsServiceImpl statsServiceImpl;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void createEndpoint(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("Получен запрос на создание записи");
        statsServiceImpl.createEndpoint(endpointHitDto);
        log.info("Запись успешно создана");
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam(name = "start") String start,
                                       @RequestParam(name = "end") String end,
                                       @RequestParam(name = "uris", required = false) List<String> uris,
                                       @RequestParam(name = "unique") boolean unique,
                                       HttpServletRequest request) {
        log.info("Получен запрос на получение статистики");
        System.out.println(request.getRequestURL().append("?").append(request.getQueryString()));
        List<ViewStatsDto> viewStatsDto = statsServiceImpl.getStats(start, end, uris, unique);
        log.info("Статистика успешно получена");
        return viewStatsDto;
    }

}