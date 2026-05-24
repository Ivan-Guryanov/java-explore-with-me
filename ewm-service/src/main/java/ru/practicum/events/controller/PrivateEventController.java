package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.service.EventService;
import ru.practicum.user.dto.NewUserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class PrivateEventController {
    private final EventService eventService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @RequestBody NewEventDto e) {
        log.info("Получен запрос от пользователя id{} на создание нового события", userId);
        EventFullDto event = eventService.createEvent(userId, e);
        log.info("Создано событие id{} пользователем id{}", event.getId(), event.getInitiator().getId());
        return event;
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> findEventByUser(@PathVariable Long userId,
                                               @RequestParam Long from,
                                               @RequestParam Long size) {
        log.info("Получен запрос на получение событий пользователя id{}", userId);
        List<EventShortDto> eventShortDto = eventService.findEventByUser(userId, from, size);
        log.info("Список событий пользователя id{} успешно получен", userId);
        return eventShortDto;
    }
}
