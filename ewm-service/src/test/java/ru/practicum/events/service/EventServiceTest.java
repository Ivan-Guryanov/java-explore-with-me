package ru.practicum.events.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import ru.practicum.StatsClient;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.service.CategoryService;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.events.controller.param.EventSort;
import ru.practicum.events.controller.param.PublicEventsParams;
import ru.practicum.events.dto.*;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserService userService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private StatsClient statsClient;

    @InjectMocks
    private EventService eventService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    @DisplayName("События, Сервис. Создание нового события.")
    void shouldCreateEventSuccessfully() {
        NewEventDto inputDto = new NewEventDto();
        inputDto.setCategory(1L);
        inputDto.setEventDate(LocalDateTime.now().plusHours(3).format(formatter));
        inputDto.setAnnotation("Краткое описание события");
        inputDto.setDescription("Полное описание события");
        inputDto.setTitle("Концерт классической музыки");

        Category category = new Category();
        category.setId(1L);
        category.setName("Музыка");

        User initiator = new User();
        initiator.setId(1L);
        initiator.setName("Организатор");

        Event savedEvent = new Event();
        savedEvent.setId(1L);
        savedEvent.setCategory(category);
        savedEvent.setInitiator(initiator);
        savedEvent.setTitle("Концерт классической музыки");
        savedEvent.setEventDate(LocalDateTime.now().plusHours(3));
        savedEvent.setState(State.PENDING);

        when(categoryService.getCategory(1L)).thenReturn(category);
        when(userService.getUserById(1L)).thenReturn(initiator);
        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

        EventFullDto result = eventService.createEvent(1L, inputDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Концерт классической музыки");
        verify(categoryService, times(1)).getCategory(1L);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("События, Сервис. Ошибка создания события: дата менее чем через 2 часа.")
    void shouldThrowValidationExceptionWhenEventDateIsTooEarly() {
        NewEventDto inputDto = new NewEventDto();
        inputDto.setCategory(1L);
        inputDto.setEventDate(LocalDateTime.now().plusMinutes(30).format(formatter));

        Category category = new Category();
        category.setId(1L);

        when(categoryService.getCategory(1L)).thenReturn(category);

        assertThatThrownBy(() -> eventService.createEvent(1L, inputDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Дата события должна быть минимум через 2 часа");

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    @DisplayName("События, Сервис. Поиск событий по параметрам.")
    void shouldFindEventsByParamSuccessfully() {
        String rangeStart = LocalDateTime.now().minusDays(1).format(formatter);
        String rangeEnd = LocalDateTime.now().plusDays(1).format(formatter);

        Category category = new Category();
        category.setId(1L);
        category.setName("Выставки");

        User initiator = new User();
        initiator.setId(1L);
        initiator.setName("Организатор");

        Event event = new Event();
        event.setId(1L);
        event.setTitle("Выставка картин");
        event.setCategory(category);
        event.setInitiator(initiator);
        event.setEventDate(LocalDateTime.now().plusHours(2));

        when(eventRepository.findEventsByParam(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(List.of(event));

        List<EventFullDto> result = eventService.findEventsByParam(
                List.of(1L), List.of("PUBLISHED"), List.of(1L), rangeStart, rangeEnd, 0L, 10L
        );

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Выставка картин");
        verify(eventRepository, times(1)).findEventsByParam(any(), any(), any(), any(), any(), any(Pageable.class));
    }

    @Test
    @DisplayName("События, Сервис. Поиск событий пользователя с пагинацией.")
    void shouldFindEventByUserSuccessfully() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Хакатоны");

        User initiator = new User();
        initiator.setId(1L);
        initiator.setName("Организатор");

        Event event = new Event();
        event.setId(1L);
        event.setTitle("Хакатон");
        event.setCategory(category);
        event.setInitiator(initiator);
        event.setEventDate(LocalDateTime.now().plusHours(5));

        when(eventRepository.findEventByUser(eq(1L), any(Pageable.class))).thenReturn(List.of(event));

        List<EventShortDto> result = eventService.findEventByUser(1L, 0L, 10L);

        assertThat(result).isNotNull().hasSize(1);
        verify(eventRepository, times(1)).findEventByUser(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("События, Сервис. Поиск событий по параметрам: ошибка, если дата старта позже конца.")
    void shouldThrowValidationExceptionWhenStartAfterEnd() {
        String rangeStart = LocalDateTime.now().plusDays(2).format(formatter);
        String rangeEnd = LocalDateTime.now().plusDays(1).format(formatter);

        assertThatThrownBy(() -> eventService.findEventsByParam(
                null, null, null, rangeStart, rangeEnd, 0L, 10L
        ))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Начало периода не может быть позже конца периода");
    }

    @Test
    @DisplayName("События, Сервис. Обновление события администратором (Публикация).")
    void shouldUpdateEventAdminPublishSuccessfully() {
        UpdateEventAdminRequest updateRequest = UpdateEventAdminRequest.builder()
                .title("Новое название выставки")
                .annotation("Обновленное краткое описание события длиной более двадцати символов")
                .description("Обновленное полное описание выставки картин современного искусства")
                .eventDate(LocalDateTime.now().plusHours(5).format(formatter)) // Валидная новая дата
                .stateAction(StateAction.PUBLISH_EVENT)
                .build();

        Category category = new Category();
        category.setId(1L);
        category.setName("Культура");

        User initiator = new User();
        initiator.setId(1L);
        initiator.setName("Организатор");

        Event existingEvent = new Event();
        existingEvent.setId(1L);
        existingEvent.setTitle("Старое название");
        existingEvent.setState(State.PENDING);
        existingEvent.setEventDate(LocalDateTime.now().plusHours(3));
        existingEvent.setCategory(category);
        existingEvent.setInitiator(initiator);

        Event updatedEvent = new Event();
        updatedEvent.setId(1L);
        updatedEvent.setTitle("Новое название выставки");
        updatedEvent.setState(State.PUBLISHED);
        updatedEvent.setEventDate(LocalDateTime.now().plusHours(5));
        updatedEvent.setCategory(category);
        updatedEvent.setInitiator(initiator);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);

        EventFullDto result = eventService.updateEventAdmin(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Новое название выставки");
        verify(eventRepository, times(1)).save(existingEvent);
    }

    @Test
    @DisplayName("События, Сервис. Ошибка обновления администратором: публикация не в состоянии ожидания.")
    void shouldThrowConflictExceptionWhenEventNotPending() {
        UpdateEventAdminRequest updateRequest = UpdateEventAdminRequest.builder()
                .stateAction(StateAction.PUBLISH_EVENT)
                .build();

        Event existingEvent = new Event();
        existingEvent.setId(1L);
        existingEvent.setState(State.PUBLISHED);
        existingEvent.setEventDate(LocalDateTime.now().plusHours(3));

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));

        assertThatThrownBy(() -> eventService.updateEventAdmin(1L, updateRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Событие можно публиковать, только если оно в состоянии ожидания публикации");
    }

    @Test
    @DisplayName("События, Сервис. Ошибка обновления администратором: новая дата в прошлом.")
    void shouldThrowValidationExceptionWhenAdminSetsPastDate() {
        UpdateEventAdminRequest updateRequest = UpdateEventAdminRequest.builder()
                .eventDate(LocalDateTime.now().minusDays(1).format(formatter))
                .build();

        Event existingEvent = new Event();
        existingEvent.setId(1L);
        existingEvent.setState(State.PENDING);
        existingEvent.setEventDate(LocalDateTime.now().plusHours(3));

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));

        assertThatThrownBy(() -> eventService.updateEventAdmin(1L, updateRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Нельзя изменять начавшиеся или прошедшие события");
    }

    @Test
    @DisplayName("События, Сервис. Поиск события по ID.")
    void shouldFindEventByIdSuccessfully() {
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Кинопоказ");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Event result = eventService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Кинопоказ");
    }

    @Test
    @DisplayName("События, Сервис. Ошибка поиска события по ID: не найдено.")
    void shouldThrowNotFoundExceptionWhenEventDoesNotExist() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.findById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with id=99 was not found");
    }

    @Test
    @DisplayName("События, Сервис. Публичный поиск событий с фильтрацией (Успех).")
    void shouldGetEventsByPublicSuccessfully() {
        PublicEventsParams params = new PublicEventsParams();
        params.setText("фестиваль");
        params.setCategories(List.of(1L));
        params.setPaid(true);
        params.setRangeStart(LocalDateTime.now().minusDays(1));
        params.setRangeEnd(LocalDateTime.now().plusDays(5));
        params.setOnlyAvailable(false);
        params.setSort(EventSort.EVENT_DATE); // Предполагаемый enum в вашем проекте
        params.setFrom(0);
        params.setSize(10);

        Category category = new Category();
        category.setId(1L);
        category.setName("Музыка");

        User initiator = new User();
        initiator.setId(1L);
        initiator.setName("Организатор");

        Event event = new Event();
        event.setId(1L);
        event.setTitle("Рок фестиваль");
        event.setCategory(category);
        event.setInitiator(initiator);
        event.setEventDate(LocalDateTime.now().plusDays(2));

        when(eventRepository.getEventsByPublic(
                eq("фестиваль"), eq(false), eq(List.of(1L)), eq(false),
                eq(true), eq(false), any(LocalDateTime.class), any(LocalDateTime.class),
                eq(false), eq("EVENT_DATE"), eq(0), eq(10)
        )).thenReturn(List.of(event));

        List<EventShortDto> result = eventService.getEventsByPublic(params);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Рок фестиваль");
        verify(eventRepository, times(1)).getEventsByPublic(any(), anyBoolean(), any(), anyBoolean(),
                anyBoolean(), anyBoolean(), any(), any(), anyBoolean(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("События, Сервис. Публичный поиск: ошибка, если дата старта позже конца.")
    void shouldThrowValidationExceptionWhenPublicSearchStartAfterEnd() {
        PublicEventsParams params = new PublicEventsParams();
        params.setRangeStart(LocalDateTime.now().plusDays(2));
        params.setRangeEnd(LocalDateTime.now().plusDays(1));

        assertThatThrownBy(() -> eventService.getEventsByPublic(params))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Начало периода не может быть позже конца периода");
    }

    @Test
    @DisplayName("События, Сервис. Публичный просмотр события с обновлением просмотров из статистики.")
    void shouldPublicFindByIdPlusViewSuccessfully() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Выставки");

        User initiator = new User();
        initiator.setId(1L);
        initiator.setName("Организатор");

        Event event = new Event();
        event.setId(1L);
        event.setTitle("Выставка");
        event.setState(State.PUBLISHED); // Должно быть опубликовано
        event.setCreatedOn(LocalDateTime.now().minusDays(1));
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setCategory(category);
        event.setInitiator(initiator);
        event.setViews(10L);

        ViewStatsDto statsDto = new ViewStatsDto("ewm-main-service", "/events/1", 15L);
        ResponseEntity<List<ViewStatsDto>> responseEntity = ResponseEntity.ok(List.of(statsDto));

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(statsClient.getStats(anyString(), anyString(), anyList(), anyBoolean())).thenReturn(responseEntity);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventFullDto result = eventService.publicFindByIdPlusView(1L);

        assertThat(result).isNotNull();
        verify(statsClient, times(1)).getStats(anyString(), anyString(), anyList(), eq(true));
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    @DisplayName("События, Сервис. Публичный просмотр: ошибка NotFound, если событие не опубликовано.")
    void shouldThrowNotFoundExceptionWhenEventNotPublished() {
        Event event = new Event();
        event.setId(1L);
        event.setState(State.PENDING);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.publicFindByIdPlusView(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Событие не опубликовано");
    }

    @Test
    @DisplayName("События, Сервис. Приватный просмотр события по ID.")
    void shouldPrivateFindByIdSuccessfully() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Выставки");

        User initiator = new User();
        initiator.setId(1L);
        initiator.setName("Организатор");

        Event event = new Event();
        event.setId(1L);
        event.setTitle("Закрытая встреча");
        event.setCategory(category);
        event.setInitiator(initiator);
        event.setEventDate(LocalDateTime.now().plusDays(2));

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventFullDto result = eventService.privateFindById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Закрытая встреча");
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    @DisplayName("События, Сервис. Обновление события пользователем (Отправка на ревью).")
    void shouldUpdateEventUserSuccessfully() {
        UpdateEventUserRequest updateRequest = new UpdateEventUserRequest();
        updateRequest.setTitle("Новое название от пользователя");
        updateRequest.setEventDate(LocalDateTime.now().plusHours(3).format(formatter));
        updateRequest.setStateAction(StateAction.SEND_TO_REVIEW);

        Category category = new Category();
        category.setId(1L);
        category.setName("Категория");

        User initiator = new User();
        initiator.setId(1L);
        initiator.setName("Инициатор");

        Event existingEvent = new Event();
        existingEvent.setId(1L);
        existingEvent.setTitle("Старое название");
        existingEvent.setState(State.CANCELED);
        existingEvent.setCategory(category);
        existingEvent.setInitiator(initiator);
        existingEvent.setEventDate(LocalDateTime.now().plusHours(4));

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));

        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventFullDto result = eventService.updateEventUser(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Новое название от пользователя");

        assertThat(result.getState()).isEqualTo(State.PENDING);

        verify(eventRepository, times(1)).save(existingEvent);
    }



    @Test
    @DisplayName("События, Сервис. Ошибка обновления пользователем: нельзя редактировать опубликованное.")
    void shouldThrowConflictExceptionWhenUserUpdatesPublishedEvent() {
        UpdateEventUserRequest updateRequest = new UpdateEventUserRequest();

        Event existingEvent = new Event();
        existingEvent.setId(1L);
        existingEvent.setState(State.PUBLISHED); // Опубликовано — редактировать нельзя

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));

        assertThatThrownBy(() -> eventService.updateEventUser(1L, updateRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Можно редактировать только не опубликованные события");
    }

    @Test
    @DisplayName("События, Сервис. Ошибка обновления пользователем: дата менее чем через 2 часа.")
    void shouldThrowValidationExceptionWhenUserSetsTooEarlyDate() {
        UpdateEventUserRequest updateRequest = new UpdateEventUserRequest();
        updateRequest.setEventDate(LocalDateTime.now().plusHours(1).format(formatter));

        Event existingEvent = new Event();
        existingEvent.setId(1L);
        existingEvent.setState(State.PENDING);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));

        assertThatThrownBy(() -> eventService.updateEventUser(1L, updateRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Дата и время, на которые намечается событие, не может быть раньше");
    }

    @Test
    @DisplayName("События, Сервис. Изменение статуса заявок: возврат пустых списков, если список requestIds пуст.")
    void shouldReturnEmptyResultWhenRequestIdsIsEmpty() {
        EventStatusUpdateRequest requestDto = new EventStatusUpdateRequest();
        requestDto.setRequestIds(List.of());
        requestDto.setStatus(RequestStatus.CONFIRMED);

        Event event = new Event();
        event.setId(1L);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        EventStatusUpdateResult result = eventService.updateRequestStatus(1L, 1L, requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getConfirmedRequests()).isEmpty();
        assertThat(result.getRejectedRequests()).isEmpty();
        verify(requestRepository, never()).findByIdIn(any());
    }

    @Test
    @DisplayName("События, Сервис. Изменение статуса заявки: выброс NotFoundException, если количество найденных заявок не совпадает с запросом.")
    void shouldThrowNotFoundExceptionWhenSomeRequestsNotFound() {
        EventStatusUpdateRequest requestDto = new EventStatusUpdateRequest();
        requestDto.setRequestIds(List.of(10L, 11L));
        requestDto.setStatus(RequestStatus.CONFIRMED);

        Event event = new Event();
        event.setId(1L);

        ParticipationRequest req1 = new ParticipationRequest();
        req1.setId(10L);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.findByIdIn(List.of(10L, 11L))).thenReturn(List.of(req1));

        assertThatThrownBy(() -> eventService.updateRequestStatus(1L, 1L, requestDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Одна или несколько заявок не найдены");
    }

    @Test
    @DisplayName("События, Сервис. Изменение статуса заявки: выброс ConflictException, если заявка находится не в статусе PENDING.")
    void shouldThrowConflictExceptionWhenRequestStatusIsNotPending() {
        EventStatusUpdateRequest requestDto = new EventStatusUpdateRequest();
        requestDto.setRequestIds(List.of(10L));
        requestDto.setStatus(RequestStatus.CONFIRMED);

        Event event = new Event();
        event.setId(1L);

        ParticipationRequest req = new ParticipationRequest();
        req.setId(10L);
        req.setStatus(RequestStatus.CONFIRMED);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.findByIdIn(List.of(10L))).thenReturn(List.of(req));

        assertThatThrownBy(() -> eventService.updateRequestStatus(1L, 1L, requestDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Статус можно изменить только у заявок в состоянии PENDING");
    }

    @Test
    @DisplayName("События, Сервис. Изменение статуса заявки: успешное отклонение всех заявок.")
    void shouldRejectAllRequestsSuccessfully() {
        EventStatusUpdateRequest requestDto = new EventStatusUpdateRequest();
        requestDto.setRequestIds(List.of(10L, 11L));
        requestDto.setStatus(RequestStatus.REJECTED);

        Event event = new Event();
        event.setId(1L);

        User requester = new User();
        requester.setId(5L);

        ParticipationRequest req1 = new ParticipationRequest();
        req1.setId(10L);
        req1.setStatus(RequestStatus.PENDING);
        req1.setEvent(event);
        req1.setRequester(requester);
        req1.setCreated(LocalDateTime.now());

        ParticipationRequest req2 = new ParticipationRequest();
        req2.setId(11L);
        req2.setStatus(RequestStatus.PENDING);
        req2.setEvent(event);
        req2.setRequester(requester);
        req2.setCreated(LocalDateTime.now());

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.findByIdIn(List.of(10L, 11L))).thenReturn(List.of(req1, req2));

        EventStatusUpdateResult result = eventService.updateRequestStatus(1L, 1L, requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getConfirmedRequests()).isEmpty();
        assertThat(result.getRejectedRequests()).hasSize(2);
        assertThat(req1.getStatus()).isEqualTo(RequestStatus.REJECTED);
        assertThat(req2.getStatus()).isEqualTo(RequestStatus.REJECTED);
    }

    @Test
    @DisplayName("События, Сервис. Изменение статуса заявки: автоматическое подтверждение, если лимит участников равен 0.")
    void shouldConfirmRequestsWhenParticipantLimitIsZero() {
        EventStatusUpdateRequest requestDto = new EventStatusUpdateRequest();
        requestDto.setRequestIds(List.of(10L));
        requestDto.setStatus(RequestStatus.CONFIRMED);

        Event event = new Event();
        event.setId(1L);
        event.setParticipantLimit(0);

        User requester = new User();
        requester.setId(5L);

        ParticipationRequest req = new ParticipationRequest();
        req.setId(10L);
        req.setStatus(RequestStatus.PENDING);
        req.setEvent(event);
        req.setRequester(requester);
        req.setCreated(LocalDateTime.now());

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.findByIdIn(List.of(10L))).thenReturn(List.of(req));
        when(requestRepository.countByEventIdAndStatus(1L, RequestStatus.CONFIRMED)).thenReturn(5);

        EventStatusUpdateResult result = eventService.updateRequestStatus(1L, 1L, requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getConfirmedRequests()).hasSize(1);
        assertThat(req.getStatus()).isEqualTo(RequestStatus.CONFIRMED);
        assertThat(event.getConfirmedRequests()).isEqualTo(6L);
    }

    @Test
    @DisplayName("События, Сервис. Изменение статуса заявки: выброс ConflictException, если лимит участников уже исчерпан на момент обработки.")
    void shouldThrowConflictExceptionWhenParticipantLimitIsAlreadyReached() {
        EventStatusUpdateRequest requestDto = new EventStatusUpdateRequest();
        requestDto.setRequestIds(List.of(10L));
        requestDto.setStatus(RequestStatus.CONFIRMED);

        Event event = new Event();
        event.setId(1L);
        event.setParticipantLimit(5);
        event.setRequestModeration(true);

        ParticipationRequest req = new ParticipationRequest();
        req.setId(10L);
        req.setStatus(RequestStatus.PENDING);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.findByIdIn(List.of(10L))).thenReturn(List.of(req));
        when(requestRepository.countByEventIdAndStatus(1L, RequestStatus.CONFIRMED)).thenReturn(5);

        assertThatThrownBy(() -> eventService.updateRequestStatus(1L, 1L, requestDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Лимит участников события уже достигнут");
    }

    @Test
    @DisplayName("События, Сервис. Изменение статуса заявки: заполнение лимита в процессе (часть подтверждается, часть отклоняется).")
    void shouldConfirmUntilLimitAndRejectTheRest() {
        EventStatusUpdateRequest requestDto = new EventStatusUpdateRequest();
        requestDto.setRequestIds(List.of(10L, 11L));
        requestDto.setStatus(RequestStatus.CONFIRMED);

        Event event = new Event();
        event.setId(1L);
        event.setParticipantLimit(3);
        event.setRequestModeration(true);

        User requester = new User();
        requester.setId(5L);

        ParticipationRequest req1 = new ParticipationRequest();
        req1.setId(10L);
        req1.setStatus(RequestStatus.PENDING);
        req1.setEvent(event);
        req1.setRequester(requester);
        req1.setCreated(LocalDateTime.now());

        ParticipationRequest req2 = new ParticipationRequest();
        req2.setId(11L);
        req2.setStatus(RequestStatus.PENDING);
        req2.setEvent(event);
        req2.setRequester(requester);
        req2.setCreated(LocalDateTime.now());

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.findByIdIn(List.of(10L, 11L))).thenReturn(List.of(req1, req2));
        when(requestRepository.countByEventIdAndStatus(1L, RequestStatus.CONFIRMED)).thenReturn(2);

        EventStatusUpdateResult result = eventService.updateRequestStatus(1L, 1L, requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getConfirmedRequests()).hasSize(1);
        assertThat(result.getRejectedRequests()).hasSize(1);
        assertThat(req1.getStatus()).isEqualTo(RequestStatus.CONFIRMED);
        assertThat(req2.getStatus()).isEqualTo(RequestStatus.REJECTED);
        assertThat(event.getConfirmedRequests()).isEqualTo(3L);
    }
}