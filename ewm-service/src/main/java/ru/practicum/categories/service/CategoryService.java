package ru.practicum.categories.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.CategoryMapper;
import ru.practicum.categories.dto.NewCategorytDto;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryDto createCategory(NewCategorytDto category) {

        if (categoryRepository.existsByName(category.getName())) {
            throw new ConflictException("Категория с названием '" + category.getName() + "' уже существует.");
        }
        return CategoryMapper.mapToCategoryDto(categoryRepository.save(CategoryMapper.mapToCategory(category)));
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(Long from, Long size) {
        return categoryRepository.findAllCategories(from, size).stream()
                .map(CategoryMapper::mapToCategoryDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategoriesById(Long catId) {
        Category cat = getCategory(catId);
        return CategoryMapper.mapToCategoryDto(cat);
    }

    @Transactional
    public void deleteCategory(@PathVariable Long catId) {
        getCategory(catId);
        categoryRepository.deleteById(catId);
    }

    @Transactional
    public CategoryDto patchCategory(Long catId, NewCategorytDto c) {
        if (categoryRepository.existsByNameAndIdNot(c.getName(), catId)) {
            throw new ConflictException("Категория с названием '" + c.getName() + "' уже существует.");
        }
        Category category = CategoryMapper.mapToCategory(c);
        category.setId(catId);
        return CategoryMapper.mapToCategoryDto(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public Category getCategory(Long id) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));
        return cat;
    }
}
