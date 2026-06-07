package ru.practicum.events.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.*;
import ru.practicum.events.service.EventService;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@Validated
public class PrivateEventController {
    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable @Positive Long userId,
                                    @RequestBody @Valid NewEventDto e) {
        log.info("Получен запрос от пользователя id{} на создание нового события", userId);
        EventFullDto event = eventService.createEvent(userId, e);
        log.info("Создано событие id{} пользователем id{}", event.getId(), event.getInitiator().getId());
        return event;
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> findEventByUser(@PathVariable @Positive Long userId,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                               @RequestParam(defaultValue = "10") @Positive Long size) {
        log.info("Получен запрос на получение событий пользователя id{}", userId);
        List<EventShortDto> eventShortDto = eventService.findEventByUser(userId, from, size);
        log.info("Список событий пользователя id{} успешно получен", userId);
        return eventShortDto;
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto findById(@PathVariable @Positive Long eventId) {
        log.info("Получен запрос на получение события с id{}", eventId);
        EventFullDto event = eventService.privateFindById(eventId);
        log.info("Событие с id{} успешно получено", event.getId());
        return event;
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> findRequestByEvent(@PathVariable @Positive Long eventId) {
        log.info("Получен запрос на получение списка запросов на участие в событие id{}", eventId);
        List<ParticipationRequestDto> r = requestService.findRequestByEvent(eventId);
        log.info("Список запросов на участие в событие id{} успешно получен", eventId);
        return r;
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable @Positive Long eventId,
                                    @RequestBody @Valid UpdateEventUserRequest e) {
        log.info("Получен запрос от админа на обновление события id{}", eventId);
        EventFullDto event = eventService.updateEventUser(eventId, e);
        log.info("Событие id{} успешно обновлено", event.getId());
        return event;
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventStatusUpdateResult updateRequestStatus(@PathVariable @Positive Long userId,
                                                       @PathVariable @Positive Long eventId,
                                                       @RequestBody @Valid EventStatusUpdateRequest r) {
        log.info("Получен запрос от пользователя id{} на обновление статуса заявок события id{}", userId, eventId);
        EventStatusUpdateResult requests = eventService.updateRequestStatus(userId, eventId, r);
        log.info("Статус заявок к событию id{} успешно обновлен", eventId);
        return requests;
    }
}
