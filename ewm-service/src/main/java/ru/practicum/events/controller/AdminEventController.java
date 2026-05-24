package ru.practicum.events.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.UpdateEventAdminRequest;
import ru.practicum.events.service.EventService;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> findEventsByParam(@RequestParam(name = "users", required = false) List<Long> usersId,
                                         @RequestParam(name = "states", required = false) List<String> states,
                                         @RequestParam(name ="categories", required = false) List<Long> categoriesId,
                                         @RequestParam(name ="rangeStart", required = false) String rangeStart,
                                         @RequestParam(name ="rangeEnd", required = false) String rangeEnd,
                                         @RequestParam(name ="from", required = false) Long from,
                                         @RequestParam(name ="size", required = false) Long size,
                                         HttpServletRequest request) {
        log.info("Получен запрос на получение списка событий по параметрам");
        List<EventFullDto> events = eventService.findEventsByParam(usersId, states, categoriesId, rangeStart, rangeEnd, from, size);
        log.info("Список событий по параметрам успешно предоставлен");
        return events;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventAdmin(@PathVariable Long eventId,
                                         @RequestBody @Valid UpdateEventAdminRequest e) {
        log.info("Получен запрос от админа на обновление фильма id{}", eventId);
        EventFullDto event = eventService.updateEventAdmin(eventId, e);
        log.info("Событие id{} успешно обновлено", event.getId());
        return event;
    }
}
