package ru.practicum.categories.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.StatsClient;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.service.CategoryService;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PublicCategoryController.class)
class PublicCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private StatsClient statsClient;

    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Выставки");
    }

    @Test
    @DisplayName("Категория, Публичный, Контролер. Получение списка категорий.")
    void getCategories_ShouldReturn200AndList() throws Exception {
        // Given
        List<CategoryDto> expectedList = List.of(categoryDto);
        when(categoryService.getCategories(0L, 10L)).thenReturn(expectedList);

        // When & Then
        mockMvc.perform(get("/categories")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Выставки"));

        verify(categoryService, times(1)).getCategories(0L, 10L);
    }

    @Test
    @DisplayName("Категория, Публичный, Контролер. Получение категории по id.")
    void getCategoriesById_ShouldReturn200AndDto() throws Exception {
        // Given
        Long catId = 1L;
        when(categoryService.getCategoriesById(eq(catId))).thenReturn(categoryDto);

        // When & Then
        mockMvc.perform(get("/categories/{catId}", catId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Выставки"));

        verify(categoryService, times(1)).getCategoriesById(catId);
    }
}
