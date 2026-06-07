package ru.practicum.requests.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.events.service.EventService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RequestService requestService;

    @Test
    @DisplayName("Запросы, Сервис. Ошибка создания: запрос от пользователя на это событие уже существует.")
    void shouldThrowConflictExceptionWhenRequestAlreadyExists() {
        when(requestRepository.findByEventAndRequester(1L, 2L)).thenReturn(new ParticipationRequest());

        assertThatThrownBy(() -> requestService.createRequest(1L, 2L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Запрос уже создан.");

        verify(eventService, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Запросы, Сервис. Ошибка создания: инициатор события отправляет запрос на участие в своём событии.")
    void shouldThrowConflictExceptionWhenInitiatorAddsRequestToOwnEvent() {
        User initiator = new User();
        initiator.setId(1L); // Тот же ID пользователя

        Event event = new Event();
        event.setId(10L);
        event.setInitiator(initiator);

        when(requestRepository.findByEventAndRequester(1L, 10L)).thenReturn(null);
        when(eventService.findById(10L)).thenReturn(event);

        assertThatThrownBy(() -> requestService.createRequest(1L, 10L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Инициатор события не может добавить запрос на участие");
    }

    @Test
    @DisplayName("Запросы, Сервис. Ошибка создания: попытка участвовать в неопубликованном событии.")
    void shouldThrowConflictExceptionWhenEventNotPublished() {
        User initiator = new User();
        initiator.setId(99L);

        Event event = new Event();
        event.setId(10L);
        event.setInitiator(initiator);
        event.setState(State.PENDING);

        when(requestRepository.findByEventAndRequester(1L, 10L)).thenReturn(null);
        when(eventService.findById(10L)).thenReturn(event);

        assertThatThrownBy(() -> requestService.createRequest(1L, 10L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Нельзя участвовать в неопубликованном событии.");
    }

    @Test
    @DisplayName("Запросы, Сервис. Ошибка создания: лимит участников на событие уже исчерпан.")
    void shouldThrowConflictExceptionWhenParticipantLimitIsReached() {
        User initiator = new User();
        initiator.setId(99L);

        Event event = new Event();
        event.setId(10L);
        event.setInitiator(initiator);
        event.setState(State.PUBLISHED);
        event.setParticipantLimit(10);
        event.setConfirmedRequests(10L);

        when(requestRepository.findByEventAndRequester(1L, 10L)).thenReturn(null);
        when(eventService.findById(10L)).thenReturn(event);

        assertThatThrownBy(() -> requestService.createRequest(1L, 10L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Достигнут лимин участников.");
    }

    @Test
    @DisplayName("Запросы, Сервис. Успешное создание запроса со статусом CONFIRMED (без модерации).")
    void shouldCreateConfirmedRequestWhenNoModeration() {
        User initiator = new User();
        initiator.setId(99L);

        Event event = new Event();
        event.setId(10L);
        event.setInitiator(initiator);
        event.setState(State.PUBLISHED);
        event.setParticipantLimit(0);
        event.setRequestModeration(false);
        event.setConfirmedRequests(5L);

        User requester = new User();
        requester.setId(1L);

        ParticipationRequest savedRequest = new ParticipationRequest();
        savedRequest.setId(100L);
        savedRequest.setEvent(event);
        savedRequest.setRequester(requester);
        savedRequest.setStatus(RequestStatus.CONFIRMED);
        savedRequest.setCreated(LocalDateTime.now());

        when(requestRepository.findByEventAndRequester(1L, 10L)).thenReturn(null);
        when(eventService.findById(10L)).thenReturn(event);
        when(userService.getUserById(1L)).thenReturn(requester);
        when(requestRepository.save(any(ParticipationRequest.class))).thenReturn(savedRequest);

        ParticipationRequestDto result = requestService.createRequest(1L, 10L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(RequestStatus.CONFIRMED.name());
        assertThat(event.getConfirmedRequests()).isEqualTo(6L); // Количество подтвержденных увеличилось
        verify(requestRepository, times(1)).save(any(ParticipationRequest.class));
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    @DisplayName("Запросы, Сервис. Успешное создание запроса со статусом PENDING (требуется модерация).")
    void shouldCreatePendingRequestWhenModerationRequired() {
        User initiator = new User();
        initiator.setId(99L);

        Event event = new Event();
        event.setId(10L);
        event.setInitiator(initiator);
        event.setState(State.PUBLISHED);
        event.setParticipantLimit(20);
        event.setRequestModeration(true);
        event.setConfirmedRequests(5L);

        User requester = new User();
        requester.setId(1L);

        ParticipationRequest savedRequest = new ParticipationRequest();
        savedRequest.setId(100L);
        savedRequest.setEvent(event);
        savedRequest.setRequester(requester);
        savedRequest.setStatus(RequestStatus.PENDING);
        savedRequest.setCreated(LocalDateTime.now());

        when(requestRepository.findByEventAndRequester(1L, 10L)).thenReturn(null);
        when(eventService.findById(10L)).thenReturn(event);
        when(userService.getUserById(1L)).thenReturn(requester);
        when(requestRepository.save(any(ParticipationRequest.class))).thenReturn(savedRequest);

        ParticipationRequestDto result = requestService.createRequest(1L, 10L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(RequestStatus.PENDING.name());
        assertThat(event.getConfirmedRequests()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Запросы, Сервис. Поиск всех запросов пользователя (покупателя).")
    void shouldFindRequestsByRequesterSuccessfully() {
        Event event = new Event();
        event.setId(10L);

        User requester = new User();
        requester.setId(1L);

        ParticipationRequest request = new ParticipationRequest();
        request.setId(100L);
        request.setEvent(event);
        request.setRequester(requester);
        request.setStatus(RequestStatus.CONFIRMED);
        request.setCreated(LocalDateTime.now());

        when(requestRepository.findByRequesterId(1L)).thenReturn(List.of(request));

        List<ParticipationRequestDto> result = requestService.findRequestsByRequester(1L);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("Запросы, Сервис. Поиск всех заявок на участие в конкретном событии.")
    void shouldFindRequestByEventSuccessfully() {
        Event event = new Event();
        event.setId(10L);

        User requester = new User();
        requester.setId(1L);

        ParticipationRequest request = new ParticipationRequest();
        request.setId(100L);
        request.setEvent(event);
        request.setRequester(requester);
        request.setStatus(RequestStatus.PENDING);
        request.setCreated(LocalDateTime.now());

        when(requestRepository.findByEventId(10L)).thenReturn(List.of(request));

        List<ParticipationRequestDto> result = requestService.findRequestByEvent(10L);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("Запросы, Сервис. Успешная отмена своего запроса пользователем.")
    void shouldCancelRequestSuccessfully() {
        Event event = new Event();
        event.setId(10L);

        User requester = new User();
        requester.setId(1L);

        ParticipationRequest request = new ParticipationRequest();
        request.setId(100L);
        request.setEvent(event);
        request.setRequester(requester);
        request.setStatus(RequestStatus.PENDING);
        request.setCreated(LocalDateTime.now());

        when(requestRepository.findById(100L)).thenReturn(Optional.of(request));
        when(requestRepository.findById(100L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(ParticipationRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ParticipationRequestDto result = requestService.cancelRequest(1L, 100L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getStatus()).isEqualTo(RequestStatus.CANCELED.name());
        verify(requestRepository, times(1)).save(request);
    }

    @Test
    @DisplayName("Запросы, Сервис. Ошибка отмены: запрос с указанным ID не найден.")
    void shouldThrowNotFoundExceptionWhenCancelingNonExistentRequest() {
        when(requestRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.cancelRequest(1L, 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Запрос с id = 999 не найден");

        verify(requestRepository, never()).save(any());
    }
}