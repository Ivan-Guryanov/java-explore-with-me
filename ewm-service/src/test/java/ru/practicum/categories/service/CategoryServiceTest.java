package ru.practicum.categories.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategorytDto;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("Категория, Сервис. Создание новой категории.")
    void shouldCreateCategorySuccessfully() {
        NewCategorytDto inputDto = new NewCategorytDto();
        inputDto.setName("Выставки");

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Выставки");

        when(categoryRepository.existsByName("Выставки")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        CategoryDto result = categoryService.createCategory(inputDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Выставки");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Категория, Сервис. Получение списка категорий.")
    void shouldGetCategoriesSuccessfully() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Концерты");

        when(categoryRepository.findAllCategories(0L, 10L)).thenReturn(List.of(category));

        List<CategoryDto> result = categoryService.getCategories(0L, 10L);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("Концерты");
        verify(categoryRepository, times(1)).findAllCategories(0L, 10L);
    }

    @Test
    @DisplayName("Категория, Сервис. Получение категории по ID.")
    void shouldGetCategoryByIdSuccessfully() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Театры");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryDto result = categoryService.getCategoriesById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Театры");
    }

    @Test
    @DisplayName("Категория, Сервис. Удаление категории.")
    void shouldDeleteCategorySuccessfully() {
        Category category = new Category();
        category.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).deleteById(1L);

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Категория, Сервис. Обновление категории (пачтинг).")
    void shouldPatchCategorySuccessfully() {
        NewCategorytDto updateDto = new NewCategorytDto();
        updateDto.setName("Кино");

        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Кино");

        when(categoryRepository.existsByNameAndIdNot("Кино", 1L)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        CategoryDto result = categoryService.patchCategory(1L, updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Кино");
    }

    @Test
    @DisplayName("Категория, Сервис. Получение категории или ошибка NotFound.")
    void shouldThrowNotFoundExceptionWhenCategoryDoesNotExist() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategory(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Category with id=99 was not found");
    }
}
