package ru.practicum.user.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Пользователи, Модель. Тестирование сущности User")
class UserTest {

    @Test
    @DisplayName("Модель User. Проверка структуры, билдера, геттеров и сеттеров.")
    void testUserStructure() {
        User user = User.builder()
                .id(1L)
                .name("Алексей")
                .email("alex@mail.ru")
                .build();

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Алексей");
        assertThat(user.getEmail()).isEqualTo("alex@mail.ru");

        user.setId(2L);
        assertThat(user.getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Модель User. Проверка логики equals для базовых сценариев.")
    void testEquals() {
        User user1 = new User(1L, "user1@mail.ru", "User 1");
        User user2 = new User(1L, "user2@mail.ru", "User 2");
        User user3 = new User(2L, "user@mail.ru", "User");

        assertThat(user1.equals(user1)).isTrue();
        assertThat(user1.equals(user2)).isTrue();
        assertThat(user1.equals(user3)).isFalse();
        assertThat(user1.equals(null)).isFalse();
        assertThat(user1.equals("not an object")).isFalse();
    }

    @Test
    @DisplayName("Модель User. Проверка работы метода hashCode.")
    void testHashCode() {
        User user1 = new User(1L, "user@mail.ru", "User");
        User user2 = new User(1L, "user@mail.ru", "User");

        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }
}
