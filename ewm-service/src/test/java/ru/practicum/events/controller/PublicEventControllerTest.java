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
import ru.practicum.events.controller.param.PublicEventsParams;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.service.EventService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PublicEventController.class)
class PublicEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @MockBean
    private StatsClient statsClient;

    @Test
    @DisplayName("События, Публичный, Контроллер. Поиск событий по параметрам.")
    void shouldFindEventPublicWhenValidParamsThenReturn200() throws Exception {
        EventShortDto shortDto = new EventShortDto();
        shortDto.setId(1L);
        shortDto.setTitle("Публичный Фестиваль");

        ArgumentCaptor<PublicEventsParams> paramsCaptor = ArgumentCaptor.forClass(PublicEventsParams.class);

        when(eventService.getEventsByPublic(paramsCaptor.capture()))
                .thenReturn(List.of(shortDto));

        mockMvc.perform(get("/events")
                        .param("text", "Фестиваль")
                        .param("categories", "1,2")
                        .param("paid", "false")
                        .param("rangeStart", "2026-01-01 00:00:00")
                        .param("rangeEnd", "2026-01-02 00:00:00")
                        .param("onlyAvailable", "false")
                        .param("sort", "EVENT_DATE")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Публичный Фестиваль"));

        PublicEventsParams capturedParams = paramsCaptor.getValue();
        assertThat(capturedParams).isNotNull();
        verify(eventService, times(1)).getEventsByPublic(any(PublicEventsParams.class));
    }

    @Test
    @DisplayName("События, Публичный, Контроллер. Получение события по id с увеличением просмотров.")
    void shouldFindByIdPlusViewThenReturn200() throws Exception {
        EventFullDto fullDto = new EventFullDto();
        fullDto.setId(10L);
        fullDto.setTitle("Публичное Событие");

        when(eventService.publicFindByIdPlusView(eq(10L))).thenReturn(fullDto);

        mockMvc.perform(get("/events/{id}", 10L)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Публичное Событие"));

        verify(eventService, times(1)).publicFindByIdPlusView(10L);
    }
}
