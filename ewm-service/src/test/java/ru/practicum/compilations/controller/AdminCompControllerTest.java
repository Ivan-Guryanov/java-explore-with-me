package ru.practicum.compilations.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.StatsClient;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationDto;
import ru.practicum.compilations.service.CompilationsService;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminCompController.class)
class AdminCompControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompilationsService compilationsService;

    @MockBean
    private StatsClient statsClient;

    private NewCompilationDto newCompilationDto;
    private UpdateCompilationDto updateCompilationDto;
    private CompilationDto compilationDto;

    @BeforeEach
    void setUp() {
        newCompilationDto = new NewCompilationDto();
        newCompilationDto.setTitle("Летние фестивали");
        newCompilationDto.setPinned(true);
        newCompilationDto.setEvents(Collections.emptyList());

        updateCompilationDto = new UpdateCompilationDto();
        updateCompilationDto.setTitle("Обновленные фестивали");
        updateCompilationDto.setPinned(false);

        compilationDto = new CompilationDto();
        compilationDto.setId(1L);
        compilationDto.setTitle("Летние фестивали");
        compilationDto.setPinned(true);
        compilationDto.setEvents(Collections.emptyList());
    }

    @Test
    @DisplayName("Подборки событий, Админ, Контролер. Создание подборки.")
    void createCompilation_ShouldReturn201AndDto() throws Exception {
        when(compilationsService.createCompilation(any(NewCompilationDto.class)))
                .thenReturn(compilationDto);

        mockMvc.perform(post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCompilationDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Летние фестивали"))
                .andExpect(jsonPath("$.pinned").value(true));

        verify(compilationsService, times(1)).createCompilation(any(NewCompilationDto.class));
    }

    @Test
    @DisplayName("Подборки событий, Админ, Контролер. Удаление подборки.")
    void deleteCompilation_ShouldReturn204() throws Exception {
        Long compId = 1L;
        doNothing().when(compilationsService).deleteCompilation(compId);

        mockMvc.perform(delete("/admin/compilations/{compId}", compId))
                .andExpect(status().isNoContent());

        verify(compilationsService, times(1)).deleteCompilation(compId);
    }

    @Test
    @DisplayName("Подборки событий, Админ, Контролер. Обновление подборки.")
    void updateCompilation_ShouldReturn200AndUpdatedDto() throws Exception {
        Long compId = 1L;
        compilationDto.setTitle("Обновленные фестивали");
        compilationDto.setPinned(false);

        ArgumentCaptor<UpdateCompilationDto> captor = ArgumentCaptor.forClass(UpdateCompilationDto.class);

        when(compilationsService.updateCompilation(eq(compId), captor.capture()))
                .thenReturn(compilationDto);

        mockMvc.perform(patch("/admin/compilations/{compId}", compId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCompilationDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Обновленные фестивали"))
                .andExpect(jsonPath("$.pinned").value(false));

        assertThat(captor.getValue().getTitle()).isEqualTo("Обновленные фестивали");
        verify(compilationsService, times(1)).updateCompilation(eq(compId), any(UpdateCompilationDto.class));
    }
}
