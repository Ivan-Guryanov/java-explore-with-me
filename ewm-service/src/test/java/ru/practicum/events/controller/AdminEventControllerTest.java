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
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.UpdateEventAdminRequest;
import ru.practicum.events.service.EventService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminEventController.class)
class AdminEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @MockBean
    private StatsClient statsClient;

    @Test
    @DisplayName("События, Админ, Контроллер. Поиск событий по параметрам.")
    void shouldFindEventsByParamWhenValidRequestThenReturn200() throws Exception {
        EventFullDto eventDto = new EventFullDto();
        eventDto.setId(1L);
        eventDto.setTitle("Выставка");

        when(eventService.findEventsByParam(
                eq(List.of(1L, 2L)),
                eq(List.of("PUBLISHED")),
                eq(List.of(3L)),
                eq("2026-01-01 00:00:00"),
                eq("2026-01-02 00:00:00"),
                eq(0L),
                eq(10L)
        )).thenReturn(List.of(eventDto));

        mockMvc.perform(get("/admin/events")
                        .param("users", "1,2")
                        .param("states", "PUBLISHED")
                        .param("categories", "3")
                        .param("rangeStart", "2026-01-01 00:00:00")
                        .param("rangeEnd", "2026-01-02 00:00:00")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Выставка"));

        verify(eventService, times(1)).findEventsByParam(
                eq(List.of(1L, 2L)),
                eq(List.of("PUBLISHED")),
                eq(List.of(3L)),
                eq("2026-01-01 00:00:00"),
                eq("2026-01-02 00:00:00"),
                eq(0L),
                eq(10L)
        );
        verifyNoMoreInteractions(eventService);
    }

    @Test
    @DisplayName("События, Админ, Контроллер. Обновление события.")
    void shouldUpdateEventAdminWhenValidRequestThenReturn200() throws Exception {
        UpdateEventAdminRequest request = new UpdateEventAdminRequest(
                null, null, null, null, null, null, null, null, null
        );

        EventFullDto response = new EventFullDto();
        response.setId(1L);
        response.setTitle("Обновленное название");

        ArgumentCaptor<UpdateEventAdminRequest> dtoCaptor = ArgumentCaptor.forClass(UpdateEventAdminRequest.class);

        when(eventService.updateEventAdmin(eq(1L), dtoCaptor.capture()))
                .thenReturn(response);

        mockMvc.perform(patch("/admin/events/{eventId}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Обновленное название"));

        UpdateEventAdminRequest capturedDto = dtoCaptor.getValue();
        assertThat(capturedDto).isNotNull();
    }
}
