package ru.practicum.categories.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@Validated
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(
            @RequestParam(defaultValue = "0") Long from,
            @RequestParam(defaultValue = "10") Long size) {
        log.info("Получен запрос на получение списка категорий.");
        List<CategoryDto> cat = categoryService.getCategories(from, size);
        log.info("Список категорий успешно получен.");
        return cat;
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoriesById(@PathVariable @Positive Long catId) {
        log.info("Получен запрос на получение категории с Id{}", catId);
        CategoryDto cat = categoryService.getCategoriesById(catId);
        log.info("Категория с Id{} успешно получена.", cat.getId());
        return cat;
    }
}
