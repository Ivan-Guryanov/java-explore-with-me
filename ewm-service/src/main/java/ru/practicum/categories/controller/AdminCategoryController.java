package ru.practicum.categories.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategorytDto;
import ru.practicum.categories.service.CategoryService;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class AdminCategoryController {
    private final CategoryService  categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid NewCategorytDto c) {
        log.info("Получен запрос на создание категории {}", c.getName());
        CategoryDto category = categoryService.createCategory(c);
        log.info("Катогория {} успешно создана с id{}.", category.getName(), category.getId());
        return category;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("Получен запрос на удаление категории id{}.", catId);
        categoryService.deleteCategory(catId);
        log.info("Категория с id{} успешно удален.", catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto patchCategory(@PathVariable Long catId,
                                     @RequestBody @Valid NewCategorytDto c) {
        log.info("Получен запрос на обновление категории id{}", catId);
        CategoryDto category = categoryService.patchCategory(catId, c);
        log.info("Категория с id{} успешно обновлена.", category.getId());
        return category;
    }

}
