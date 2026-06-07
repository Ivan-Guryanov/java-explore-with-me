package ru.practicum.compilations.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.StatsClient;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.service.CompilationsService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PublicCompController.class)
class PublicCompControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompilationsService compilationsService;

    @MockBean
    private StatsClient statsClient; // Заглушка для предотвращения сбоя StatsInterceptor

    private CompilationDto compilationDto;

    @BeforeEach
    void setUp() {
        compilationDto = new CompilationDto();
        compilationDto.setId(1L);
        compilationDto.setTitle("Горячие новинки");
        compilationDto.setPinned(true);
        compilationDto.setEvents(Collections.emptyList());
    }

    @Test
    @DisplayName("Подборки событий, Публичный, Контролер. Получение подборки по id.")
    void findCompilationById_WhenExists_ShouldReturn200AndDto() throws Exception {
        Long compId = 1L;
        when(compilationsService.findCompilationById(eq(compId))).thenReturn(compilationDto);

        mockMvc.perform(get("/compilations/{compId}", compId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Горячие новинки"))
                .andExpect(jsonPath("$.pinned").value(true));

        verify(compilationsService, times(1)).findCompilationById(compId);
    }

    @Test
    @DisplayName("Подборки событий, Публичный, Контролер. Получение списка подборок.")
    void findCompilationByParam_WithAllParams_ShouldReturn200AndList() throws Exception {
        when(compilationsService.findCompilationByParam(eq(true), eq(0), eq(10)))
                .thenReturn(List.of(compilationDto));

        mockMvc.perform(get("/compilations")
                        .param("pinned", "true")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Горячие новинки"));

        verify(compilationsService, times(1)).findCompilationByParam(true, 0, 10);
    }
}
