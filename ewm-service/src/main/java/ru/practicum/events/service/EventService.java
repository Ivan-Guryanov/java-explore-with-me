package ru.practicum.events.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.service.CategoryService;
import ru.practicum.events.dto.*;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.Location;
import ru.practicum.events.model.State;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.service.UserService;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto e) {
        Category cat = categoryService.getCategory(e.getCategory());

        Event event = EventMapper.mapToEvent(e, cat);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата события должна быть минимум через 2 часа");
        }
        event.setInitiator(userService.getUserById(userId));
        event.setCreatedOn(LocalDateTime.now());

        return EventMapper.mapToEventFullDto(eventRepository.save(event));
    }

    @Transactional
    public List<EventFullDto> findEventsByParam(List<Long> usersId, List<String> states, List<Long> categoriesId,
                                                String rangeStart, String rangeEnd,
                                                Long from, Long size) {

        LocalDateTime start = null;
        LocalDateTime end = null;

        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, formatter);
        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, formatter);
        }

        int page = (int) (from / size);
        int pageSize = size.intValue();
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id").ascending());

        List<Event> events = eventRepository.findEventsByParam(usersId, states, categoriesId, start, end, pageable);
        List<EventFullDto> e = events.stream()
                .map(EventMapper::mapToEventFullDto)
                .collect(Collectors.toList());
        return e;
    }

    @Transactional
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest e) {
        Event event = findById(eventId);

        if(event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ConflictException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
        }
        if(event.getState() != State.PENDING) {
            throw new ConflictException("Событие можно публиковать, только если оно в состоянии ожидания публикации.");
        }
        if (event.getState() == State.PENDING && e.getStateAction() == StateAction.REJECT_EVENT) {
            throw new ConflictException("Событие можно отклонить, только если оно еще не опубликовано.");
        }

        if(e.getTitle() != null) {
            event.setTitle(e.getTitle());
        }
        if(e.getAnnotation() != null) {
            event.setAnnotation(e.getAnnotation());
        }
        if(e.getCategory() != null) {
            event.setCategory(categoryService.getCategory(e.getCategory()));
        }
        if(e.getDescription() != null) {
            event.setDescription(e.getDescription());
        }
        if(e.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(e.getEventDate(), formatter));
        }
        if(e.getLocation() != null) {
            event.setLocation(e.getLocation());
        }
        if(e.getPaid() != null) {
            event.setPaid(e.getPaid());
        }
        if(e.getParticipantLimit() != null) {
            event.setParticipantLimit(e.getParticipantLimit());
        }
        if(e.getStateAction() != null) {
            if(e.getStateAction() == StateAction.PUBLISH_EVENT) {
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if(e.getStateAction() == StateAction.REJECT_EVENT) {
                event.setState(State.CANCELED);
            }
        }
        return EventMapper.mapToEventFullDto(eventRepository.save(event));
    }

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " was not found"));
    }

    public List<EventShortDto> findEventByUser(Long userId, Long from, Long size) {
        int page = (int) (from / size);
        int pageSize = size.intValue();
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id").ascending());

        return eventRepository.findEventByUser(userId, pageable).stream()
                .map(EventMapper::mapToEventshortDto)
                .collect(Collectors.toList());
    }

}
