package ru.practicum.categories.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.StatsClient;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategorytDto;
import ru.practicum.categories.service.CategoryService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminCategoryController.class)
class AdminCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private StatsClient statsClient;

    private NewCategorytDto inputDto;
    private CategoryDto outputDto;

    @BeforeEach
    void setUp() {
        inputDto = new NewCategorytDto();
        inputDto.setName("Категория");

        outputDto = new CategoryDto();
        outputDto.setId(1L);
        outputDto.setName("Категория");
    }

    @Test
    @DisplayName("Категория, Админ, Контролер. Создание категории.")
    void shouldCreateCategoryThenReturn201() throws Exception {
        NewCategorytDto request = new NewCategorytDto();
        request.setName("Концерты");

        CategoryDto response = new CategoryDto();
        response.setId(1L);
        response.setName("Концерты");

        when(categoryService.createCategory(any(NewCategorytDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/admin/categories")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Концерты"));

        verify(categoryService, times(1)).createCategory(any(NewCategorytDto.class));
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    @DisplayName("Категория, Админ, Контролер. Создание категории. Ошибка - пустое название.")
    void shouldCreateCategoryWhenNameIsEmptyThenReturn400() throws Exception {
        NewCategorytDto request = new NewCategorytDto();
        request.setName("");

        mockMvc.perform(post("/admin/categories")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(categoryService);
    }

    @Test
    @DisplayName("Категория, Админ, Контролер. Создание категории. Ошибка - строка из пробелов.")
    void shouldCreateCategoryWhenNameIsBlankThenReturn400() throws Exception {
        NewCategorytDto request = new NewCategorytDto();
        request.setName("   ");

        mockMvc.perform(post("/admin/categories")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(categoryService);
    }

    @Test
    @DisplayName("Категория, Админ, Контролер. Создание категории. Ошибка - имя длинее 50 символов.")
    void shouldCreateCategoryWhenNameIsTooLongThenReturn400() throws Exception {
        NewCategorytDto request = new NewCategorytDto();
        request.setName("A".repeat(51));

        mockMvc.perform(post("/admin/categories")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(categoryService);
    }

    @Test
    @DisplayName("Категория, Админ, Контролер. Удаление категории.")
    void shouldDeleteCategoryWhenCategoryExistsThenReturn204() throws Exception {
        mockMvc.perform(delete("/admin/categories/{catId}", 1L))
                .andExpect(status().isNoContent());

        Mockito.verify(categoryService).deleteCategory(1L);
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    @DisplayName("Категория, Админ, Контролер. Обновление категории.")
    void shouldUpdateCategoryWhenValidRequestThenReturn200() throws Exception {
        NewCategorytDto request = new NewCategorytDto();
        request.setName("Театры");

        CategoryDto response = new CategoryDto();
        response.setId(1L);
        response.setName("Театры");

        ArgumentCaptor<NewCategorytDto> dtoCaptor = ArgumentCaptor.forClass(NewCategorytDto.class);

        when(categoryService.patchCategory(eq(1L), dtoCaptor.capture()))
                .thenReturn(response);

        mockMvc.perform(patch("/admin/categories/{catId}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Театры"));

        // НОВАЯ ПРОВЕРКА: Проверяем, что в сервис ушел именно тот объект, который мы послали контроллеру
        NewCategorytDto capturedDto = dtoCaptor.getValue();
        assertThat(capturedDto.getName()).isEqualTo("Театры");
    }

}