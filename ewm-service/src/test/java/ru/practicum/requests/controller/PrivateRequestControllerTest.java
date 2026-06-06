package ru.practicum.requests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.StatsClient;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.service.RequestService;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PrivateRequestController.class)
class PrivateRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestService requestService;

    @MockBean
    private StatsClient statsClient;

    @Test
    @DisplayName("Запросы, Приватный, Контроллер. Создание запроса на участие.")
    void shouldCreateRequestThenReturn201() throws Exception {
        ParticipationRequestDto responseDto = new ParticipationRequestDto();
        responseDto.setId(1L);
        responseDto.setRequester(10L);
        responseDto.setEvent(5L);

        when(requestService.createRequest(eq(10L), eq(5L))).thenReturn(responseDto);

        mockMvc.perform(post("/users/{userId}/requests", 10L)
                        .param("eventId", "5")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.requester").value(10))
                .andExpect(jsonPath("$.event").value(5));

        verify(requestService, times(1)).createRequest(10L, 5L);
        verifyNoMoreInteractions(requestService);
    }

    @Test
    @DisplayName("Запросы, Приватный, Контроллер. Получение списка запросов пользователя.")
    void shouldFindRequestsByRequesterThenReturn200() throws Exception {
        ParticipationRequestDto requestDto = new ParticipationRequestDto();
        requestDto.setId(1L);
        requestDto.setRequester(10L);

        when(requestService.findRequestsByRequester(eq(10L))).thenReturn(List.of(requestDto));

        mockMvc.perform(get("/users/{userId}/requests", 10L)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].requester").value(10));

        verify(requestService, times(1)).findRequestsByRequester(10L);
        verifyNoMoreInteractions(requestService);
    }

    @Test
    @DisplayName("Запросы, Приватный, Контроллер. Отмена запроса на участие.")
    void shouldCancelRequestThenReturn200() throws Exception {
        ParticipationRequestDto responseDto = new ParticipationRequestDto();
        responseDto.setId(1L);
        responseDto.setRequester(10L);
        responseDto.setEvent(5L);

        when(requestService.cancelRequest(eq(10L), eq(1L))).thenReturn(responseDto);

        mockMvc.perform(patch("/users/{userId}/requests/{requestId}/cancel", 10L, 1L)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.requester").value(10))
                .andExpect(jsonPath("$.event").value(5));

        verify(requestService, times(1)).cancelRequest(10L, 1L);
        verifyNoMoreInteractions(requestService);
    }
}
