package ru.practicum.events.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.controller.param.PublicEventsParams;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
@CrossOrigin
@Validated
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> findEventPublic(@Valid PublicEventsParams params,
                                               HttpServletRequest request) {
        log.info("Получен запрос на публичный поиск событий по параметрам");
        List<EventShortDto> event = eventService.getEventsByPublic(params);
        log.info("Запрос на публичный поиск событий по параметрам выполнен");
        return event;
    }

    @GetMapping("/{id}")
    public EventFullDto findById(@PathVariable @Positive Long id) {
        log.info("Получен запрос на получение события с id{}", id);
        EventFullDto event = eventService.publicFindByIdPlusView(id);
        log.info("Событие с id{} успешно получено", event.getId());
        return event;
    }
}
