package ru.practicum.comments.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.events.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    @DisplayName("Comment: Проверка работы lombok builder и геттеров")
    void testBuilderAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        Event event = new Event();
        User user = new User();
        Comment parent = new Comment();

        Comment comment = Comment.builder()
                .id(1L)
                .text("Тестовый текст")
                .date(now)
                .event(event)
                .user(user)
                .parent(parent)
                .build();

        assertAll("Проверка полей сущности",
                () -> assertEquals(1L, comment.getId()),
                () -> assertEquals("Тестовый текст", comment.getText()),
                () -> assertEquals(now, comment.getDate()),
                () -> assertEquals(event, comment.getEvent()),
                () -> assertEquals(user, comment.getUser()),
                () -> assertEquals(parent, comment.getParent())
        );
    }

    @Test
    @DisplayName("Comment: Проверка контракта equals и hashCode по ID")
    void testEqualsAndHashCode() {
        Comment comment1 = new Comment();
        comment1.setId(10L);

        Comment comment2 = new Comment();
        comment2.setId(10L);

        Comment comment3 = new Comment();
        comment3.setId(20L);

        assertAll("Проверка равенства объектов",
                () -> assertEquals(comment1, comment2, "Объекты с одинаковым ID должны быть равны"),
                () -> assertNotEquals(comment1, comment3, "Объекты с разными ID не должны быть равны"),
                () -> assertEquals(comment1.hashCode(), comment2.hashCode(), "Хеш-коды для одинаковых ID должны совпадать")
        );
    }
}
