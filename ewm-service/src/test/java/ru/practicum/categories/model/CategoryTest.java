package ru.practicum.categories.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {

    @Test
    @DisplayName("Модель Category. Проверка билдера, геттеров и сеттеров.")
    void testCategoryStructure() {
        Category category = Category.builder()
                .id(1L)
                .name("Выставки")
                .build();

        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getName()).isEqualTo("Выставки");

        category.setId(2L);
        category.setName("Концерты");

        assertThat(category.getId()).isEqualTo(2L);
        assertThat(category.getName()).isEqualTo("Концерты");
    }

    @Test
    @DisplayName("Модель Category. Проверка логики equals для базовых сценариев.")
    void testEquals() {
        Category cat1 = new Category(1L, "Кино");
        Category cat2 = new Category(1L, "Театр");
        Category cat3 = new Category(2L, "Лекции");

        assertThat(cat1.equals(cat1)).isTrue();
        assertThat(cat1.equals(cat2)).isTrue();
        assertThat(cat1.equals(cat3)).isFalse();
        assertThat(cat1.equals(null)).isFalse();
        assertThat(cat1.equals("just a string")).isFalse();
    }

    @Test
    @DisplayName("Модель Category. Проверка работы метода hashCode.")
    void testHashCode() {
        Category cat1 = new Category(1L, "Спорт");
        Category cat2 = new Category(1L, "Спорт");

        assertThat(cat1.hashCode()).isEqualTo(cat2.hashCode());
    }
}
