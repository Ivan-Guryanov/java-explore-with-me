package ru.practicum.events.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.StatsClient;
import ru.practicum.events.dto.*;
import ru.practicum.events.model.Location;
import ru.practicum.events.service.EventService;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.service.RequestService;
import ru.practicum.user.dto.UserShortDto;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PrivateEventController.class)
class PrivateEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @MockBean
    private RequestService requestService;

    @MockBean
    private StatsClient statsClient;

    @Test
    @DisplayName("События, Приватный, Контроллер. Создание нового события.")
    void shouldCreateEventThenReturn201() throws Exception {
        Location testLocation = new ru.practicum.events.model.Location();

        // Передаем ровно 9 аргументов в соответствии со структурой полей класса
        NewEventDto requestBody = new NewEventDto(
                "Заголовок события",
                "Очень длинная аннотация события, которая должна содержать минимум 20 символов",
                3L,
                "Длинное описание события, которое также должно быть не менее 20 символов в длину для прохождения валидации",
                "2026-07-07 19:00:00",
                testLocation,
                false,
                10,
                false
        );

        UserShortDto initiator = new UserShortDto();
        initiator.setId(1L);

        EventFullDto responseDto = new EventFullDto();
        responseDto.setId(10L);
        responseDto.setInitiator(initiator);
        responseDto.setTitle("Концерт");

        ArgumentCaptor<NewEventDto> dtoCaptor = ArgumentCaptor.forClass(NewEventDto.class);

        when(eventService.createEvent(eq(1L), dtoCaptor.capture())).thenReturn(responseDto);

        mockMvc.perform(post("/users/{userId}/events", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Концерт"));

        assertThat(dtoCaptor.getValue()).isNotNull();
        verify(eventService, times(1)).createEvent(eq(1L), any(NewEventDto.class));
    }

    @Test
    @DisplayName("События, Приватный, Контроллер. Получение событий текущего пользователя.")
    void shouldFindEventByUserThenReturn200() throws Exception {
        EventShortDto shortDto = new EventShortDto();
        shortDto.setId(5L);
        shortDto.setTitle("Выставка");

        when(eventService.findEventByUser(eq(1L), eq(0L), eq(10L)))
                .thenReturn(List.of(shortDto));

        mockMvc.perform(get("/users/{userId}/events", 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[0].title").value("Выставка"));

        verify(eventService, times(1)).findEventByUser(1L, 0L, 10L);
    }

    @Test
    @DisplayName("События, Приватный, Контроллер. Получение полной информации о событии по id.")
    void shouldFindByIdThenReturn200() throws Exception {
        EventFullDto responseDto = new EventFullDto();
        responseDto.setId(10L);
        responseDto.setTitle("Спектакль");

        when(eventService.privateFindById(eq(10L))).thenReturn(responseDto);

        mockMvc.perform(get("/users/{userId}/events/{eventId}", 1L, 10L)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Спектакль"));

        verify(eventService, times(1)).privateFindById(10L);
    }

    @Test
    @DisplayName("События, Приватный, Контроллер. Получение информации о запросах на участие.")
    void shouldFindRequestByEventThenReturn200() throws Exception {
        ParticipationRequestDto requestDto = new ParticipationRequestDto();
        requestDto.setId(20L);
        requestDto.setEvent(10L);

        when(requestService.findRequestByEvent(eq(10L))).thenReturn(List.of(requestDto));

        mockMvc.perform(get("/users/{userId}/events/{eventId}/requests", 1L, 10L)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(20))
                .andExpect(jsonPath("$[0].event").value(10));

        verify(requestService, times(1)).findRequestByEvent(10L);
    }

    @Test
    @DisplayName("События, Приватный, Контроллер. Изменение события создателем.")
    void shouldUpdateEventThenReturn200() throws Exception {
        UpdateEventUserRequest requestBody = new UpdateEventUserRequest(
                null, null, null, null, null, null, null, null, null, null
        );

        EventFullDto responseDto = new EventFullDto();
        responseDto.setId(10L);
        responseDto.setTitle("Обновленный Концерт");

        ArgumentCaptor<UpdateEventUserRequest> dtoCaptor = ArgumentCaptor.forClass(UpdateEventUserRequest.class);

        when(eventService.updateEventUser(eq(10L), dtoCaptor.capture())).thenReturn(responseDto);

        mockMvc.perform(patch("/users/{userId}/events/{eventId}", 1L, 10L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Обновленный Концерт"));

        assertThat(dtoCaptor.getValue()).isNotNull();
        verify(eventService, times(1)).updateEventUser(eq(10L), any(UpdateEventUserRequest.class));
    }

    @Test
    @DisplayName("События, Приватный, Контроллер. Изменение статуса заявок на участие.")
    void shouldUpdateRequestStatusThenReturn200() throws Exception {
        EventStatusUpdateRequest requestBody = new EventStatusUpdateRequest();
        requestBody.setRequestIds(List.of(1L, 2L));
        requestBody.setStatus(RequestStatus.CONFIRMED);

        EventStatusUpdateResult responseDto = new EventStatusUpdateResult();

        ArgumentCaptor<EventStatusUpdateRequest> dtoCaptor = ArgumentCaptor.forClass(EventStatusUpdateRequest.class);

        when(eventService.updateRequestStatus(eq(1L), eq(10L), dtoCaptor.capture())).thenReturn(responseDto);

        mockMvc.perform(patch("/users/{userId}/events/{eventId}/requests", 1L, 10L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        assertThat(dtoCaptor.getValue()).isNotNull();
        verify(eventService, times(1)).updateRequestStatus(eq(1L), eq(10L), any(EventStatusUpdateRequest.class));
    }
}
