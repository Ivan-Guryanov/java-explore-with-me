package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.events.service.EventService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.dto.RequestMapper;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final UserService userService;

    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {

        if (requestRepository.findByEventAndRequester(userId, eventId) != null) {
            throw new ConflictException("Запрос уже создан.");
        }
        Event event = eventService.findById(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии.");
        }
        if (event.getState() == State.CANCELED || event.getState() == State.PENDING) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии.");
        }
        if ((int) event.getParticipantLimit() <= event.getConfirmedRequests() && event.getParticipantLimit() != 0) {
            throw new ConflictException("Достигнут лимин участников.");
        }
        RequestStatus status;
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        } else {
            status = RequestStatus.PENDING;
        }

        ParticipationRequest request = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(userService.getUserById(userId))
                .status(status)
                .build();

        ParticipationRequest r = requestRepository.save(request);
        eventRepository.save(event);

        return RequestMapper.mapToParticipationRequestDto(r);
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> findRequestsByRequester(Long userId) {
        List<ParticipationRequest> r = requestRepository.findByRequesterId(userId);
        return r.stream()
                .map(RequestMapper::mapToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> findRequestByEvent(Long eventId) {
        List<ParticipationRequest> r = requestRepository.findByEventId(eventId);
        return r.stream()
                .map(RequestMapper::mapToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest r = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id = " + requestId + " не найден"));
        r.setStatus(RequestStatus.CANCELED);
        requestRepository.save(r);
        return RequestMapper.mapToParticipationRequestDto(r);
    }
}
