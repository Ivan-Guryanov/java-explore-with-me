package ru.practicum.categories.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.categories.model.Category;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryMapper {

    public static Category mapToCategory(NewCategorytDto u) {
        return Category.builder()
                .name(u.getName())
                .build();
    }

    public static CategoryDto mapToCategoryDto(Category u) {
        return CategoryDto.builder()
                .id(u.getId())
                .name(u.getName())
                .build();
    }
}
