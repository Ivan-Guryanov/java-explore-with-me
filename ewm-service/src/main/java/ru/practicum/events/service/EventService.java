package ru.practicum.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.service.CategoryService;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.events.dto.*;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.requests.dto.RequestMapper;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.user.service.UserService;
import ru.practicum.events.controller.param.PublicEventsParams;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
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

        if (usersId != null) {
            if (usersId.get(0) == 0) {
                usersId = null;
            }
        }
        if (categoriesId != null) {
            if (categoriesId.get(0) == 0) {
                categoriesId = null;
            }
        }
        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, formatter);
        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, formatter);
        }
        if (start != null && end != null &&
                start.isAfter(end)) {
            throw new ValidationException("Начало периода не может быть позже конца периода");
        }

        long fromVal = Objects.requireNonNullElse(from, 0L);
        long sizeVal = Objects.requireNonNullElse(size, 10L);
        int pageNum = (int) (fromVal / sizeVal);
        Pageable pageable = PageRequest.of(pageNum, (int) sizeVal, Sort.unsorted());

        List<Event> events = eventRepository.findEventsByParam(usersId, states, categoriesId, start, end, pageable);
        List<EventFullDto> e = events.stream()
                .map(EventMapper::mapToEventFullDto)
                .collect(Collectors.toList());
        return e;
    }

    @Transactional
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest e) {
        Event event = findById(eventId);

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ConflictException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
        }
        if (event.getState() != State.PENDING) {
            throw new ConflictException("Событие можно публиковать, только если оно в состоянии ожидания публикации.");
        }

        if (e.getTitle() != null) {
            event.setTitle(e.getTitle());
        }
        if (e.getAnnotation() != null) {
            event.setAnnotation(e.getAnnotation());
        }
        if (e.getCategory() != null) {
            event.setCategory(categoryService.getCategory(e.getCategory()));
        }
        if (e.getDescription() != null) {
            event.setDescription(e.getDescription());
        }
        if (e.getEventDate() != null) {
            LocalDateTime newEventDate = LocalDateTime.parse(e.getEventDate(), formatter);

            if (newEventDate.isBefore(LocalDateTime.now()) && e.getEventDate() != null) {
                throw new ValidationException("Нельзя изменять начавшиеся или прошедшие события");
            }
            event.setEventDate(newEventDate);
        }
        if (e.getLocation() != null) {
            event.setLocation(e.getLocation());
        }
        if (e.getPaid() != null) {
            event.setPaid(e.getPaid());
        }
        if (e.getParticipantLimit() != null) {
            event.setParticipantLimit(e.getParticipantLimit());
        }
        if (e.getStateAction() != null) {
            if (e.getStateAction() == StateAction.PUBLISH_EVENT) {
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (e.getStateAction() == StateAction.REJECT_EVENT) {
                event.setState(State.CANCELED);
            }
        }
        return EventMapper.mapToEventFullDto(eventRepository.save(event));
    }

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " was not found"));
    }

    @Transactional
    public List<EventShortDto> findEventByUser(Long userId, Long from, Long size) {
        int page = (int) (from / size);
        int pageSize = size.intValue();
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id").ascending());

        return eventRepository.findEventByUser(userId, pageable).stream()
                .map(EventMapper::mapToEventshortDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<EventShortDto> getEventsByPublic(PublicEventsParams params) {
        LocalDateTime rangeStart = params.getRangeStart();
        LocalDateTime rangeEnd = params.getRangeEnd();
        boolean onlyAvailable = params.isOnlyAvailable();
        int from = params.getFrom();
        int size = params.getSize();

        if (rangeStart != null && rangeEnd != null &&
                rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Начало периода не может быть позже конца периода");
        }
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100);
        }

        boolean textIsBlank = params.getText() == null || params.getText().isBlank();
        String safeText = textIsBlank ? "" : params.getText();

        boolean paidIsNull = params.getPaid() == null;
        Boolean safePaid = !paidIsNull && params.getPaid();

        boolean categoriesIsEmpty = params.getCategories() == null || params.getCategories().isEmpty();
        List<Long> safeCategories = categoriesIsEmpty ? List.of(-1L) : params.getCategories();
        String sortValue = params.getSort() == null ? "EVENT_DATE" : params.getSort().name();

        List<Event> events = eventRepository.getEventsByPublic(safeText, textIsBlank, safeCategories, categoriesIsEmpty,
                safePaid, paidIsNull, rangeStart, rangeEnd, onlyAvailable, sortValue, from, size);

        return events.stream()
                .map(EventMapper::mapToEventshortDto)
                .toList();
    }

    @Transactional
    public EventFullDto publicFindByIdPlusView(Long id) {
        Event event = findById(id);
        if (event.getViews() == null) {
            event.setViews(1L);
        } else {
            event.setViews(event.getViews() + 1);
        }

        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException("Событие не опубликовано");
        }

        List<String> uri = new ArrayList<>(List.of("/events/" + event.getId()));
        ResponseEntity<List<ViewStatsDto>> response = statsClient.getStats(event.getCreatedOn().format(formatter),
                LocalDateTime.now().plusMinutes(1).format(formatter), uri, true);
        List<ViewStatsDto> stats = response.getBody();
        Long count = stats.get(0).getHits();
        event.setViews(count);
        eventRepository.save(event);

        return EventMapper.mapToEventFullDto(event);
    }

    @Transactional
    public EventFullDto privateFindById(Long id) {
        Event event = findById(id);
        eventRepository.save(event);
        return EventMapper.mapToEventFullDto(event);
    }

    @Transactional
    public EventFullDto updateEventUser(Long eventId, UpdateEventUserRequest e) {

        Event event = findById(eventId);

        if (event.getState() == State.PUBLISHED) {
            throw new ConflictException("Можно редактировать только не опубликованные события");
        }

        if (e.getTitle() != null) {
            event.setTitle(e.getTitle());
        }
        if (e.getAnnotation() != null) {
            event.setAnnotation(e.getAnnotation());
        }
        if (e.getCategory() != null) {
            event.setCategory(categoryService.getCategory(e.getCategory()));
        }
        if (e.getDescription() != null) {
            event.setDescription(e.getDescription());
        }
        if (e.getEventDate() != null) {
            LocalDateTime newEventDate = LocalDateTime.parse(e.getEventDate(), formatter);
            if (newEventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("Дата и время, на которые намечается событие, не может быть раньше," +
                        "чем через два часа от текущего момента");
            }
            event.setEventDate(newEventDate);
        }
        if (e.getLocation() != null) {
            event.setLocation(e.getLocation());
        }
        if (e.getPaid() != null) {
            event.setPaid(e.getPaid());
        }
        if (e.getParticipantLimit() != null) {
            event.setParticipantLimit(e.getParticipantLimit());
        }
        if (e.getRequestModeration() != null) {
            event.setRequestModeration(e.getRequestModeration());
        }
        if (e.getStateAction() == StateAction.CANCEL_REVIEW) {
            event.setState(State.CANCELED);
        } else if (e.getStateAction() == StateAction.SEND_TO_REVIEW) {
            event.setState(State.PENDING);
        }

        return EventMapper.mapToEventFullDto(eventRepository.save(event));
    }

    @Transactional
    public EventStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventStatusUpdateRequest r) {

        Event event = findById(eventId);

        if (r.getRequestIds() == null || r.getRequestIds().isEmpty()) {
            return new EventStatusUpdateResult(List.of(), List.of());
        }

        List<ParticipationRequest> requests = requestRepository.findByIdIn(r.getRequestIds());

        if (requests.size() != r.getRequestIds().size()) {
            throw new NotFoundException("Одна или несколько заявок не найдены");
        }

        for (ParticipationRequest participationRequest : requests) {
            if (participationRequest.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Статус можно изменить только у заявок в состоянии PENDING");
            }
        }

        List<ParticipationRequest> confirmedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();

        if (r.getStatus() == RequestStatus.REJECTED) {
            for (ParticipationRequest participationRequest : requests) {
                participationRequest.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(participationRequest);
            }

            requestRepository.saveAll(requests);
            eventRepository.save(event);
            return new EventStatusUpdateResult(
                    confirmedRequests.stream()
                            .map(RequestMapper::mapToParticipationRequestDto)
                            .toList(),
                    rejectedRequests.stream()
                            .map(RequestMapper::mapToParticipationRequestDto)
                            .toList()
            );
        }

        if (r.getStatus() == RequestStatus.CONFIRMED) {
            int participantLimit = event.getParticipantLimit();

            int confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

            if (participantLimit == 0 || !event.isRequestModeration()) {
                for (ParticipationRequest participationRequest : requests) {
                    participationRequest.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(participationRequest);
                    confirmedCount++;
                }

                event.setConfirmedRequests((long) confirmedCount);
                requestRepository.saveAll(requests);
                eventRepository.save(event);

                return new EventStatusUpdateResult(
                        confirmedRequests.stream()
                                .map(RequestMapper::mapToParticipationRequestDto)
                                .toList(),
                        rejectedRequests.stream()
                                .map(RequestMapper::mapToParticipationRequestDto)
                                .toList()
                );
            }

            if (confirmedCount >= participantLimit) {
                throw new ConflictException("Лимит участников события уже достигнут");
            }

            for (ParticipationRequest participationRequest : requests) {
                if (confirmedCount < participantLimit) {
                    participationRequest.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(participationRequest);
                    confirmedCount++;
                } else {
                    participationRequest.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(participationRequest);
                }
            }
            event.setConfirmedRequests((long) confirmedCount);
            requestRepository.saveAll(requests);
            eventRepository.save(event);

            return new EventStatusUpdateResult(
                    confirmedRequests.stream()
                            .map(RequestMapper::mapToParticipationRequestDto)
                            .toList(),
                    rejectedRequests.stream()
                            .map(RequestMapper::mapToParticipationRequestDto)
                            .toList()
            );
        }

        requestRepository.saveAll(requests);
        eventRepository.save(event);
        throw new ConflictException("Некорректный статус заявки");

    }
}
