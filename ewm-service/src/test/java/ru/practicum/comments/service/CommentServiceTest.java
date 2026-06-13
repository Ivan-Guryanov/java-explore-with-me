package ru.practicum.comments.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentFullDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    private User user;
    private Event event;
    private Comment comment;
    private NewCommentDto newCommentDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Ivan");

        event = new Event();
        event.setId(10L);
        event.setState(State.PUBLISHED);

        comment = new Comment();
        comment.setId(100L);
        comment.setText("Оригинальный текст");
        comment.setUser(user);
        comment.setEvent(event);
        comment.setDate(LocalDateTime.now());
        comment.setParent(null);

        newCommentDto = new NewCommentDto();
        newCommentDto.setEvent(10L);
        newCommentDto.setText("Новый текст комментария");
        newCommentDto.setParent(null);
    }

    @Test
    @DisplayName("createComment: создание корневого комментария для опубликованного события")
    void createComment_WhenValid_ShouldSaveAndReturnDto() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = commentService.createComment(1L, newCommentDto);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("Комментарии, сервис. Успешное получение комментария его владельцем")
    void findCommentById_WhenUserIsOwner_ShouldReturnDto() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        CommentDto result = commentService.findCommentById(1L, 100L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(1, result.getAuthor());
    }

    @Test
    @DisplayName("Комментарии, сервис. Успешный поиск сущности в репозитории")
    void findById_WhenExists_ShouldReturnCommentEntity() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        Comment result = commentService.findById(100L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Оригинальный текст", result.getText());
    }

    @Test
    @DisplayName("Комментарии, сервис. Обновление текста комментария владельцем")
    void updateComment_WhenUserIsOwner_ShouldUpdateTextAndReturnDto() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        CommentDto result = commentService.updateComment(1L, 100L, newCommentDto);

        assertNotNull(result);
        assertEquals("Новый текст комментария", comment.getText());
        assertEquals(100L, result.getId());
    }

    @Test
    @DisplayName("Комментарии, сервис. Выброс ValidationException, если удаляет не владелец")
    void deleteComment_WhenUserNotOwner_ShouldThrowValidationException() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        assertThrows(ValidationException.class, () ->
                commentService.deleteComment(99L, 100L)
        );

        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    @DisplayName("Комментарии, сервис. Получение списка комментариев, преобразованных в древовидный Dto")
    void findCommentByEvent_WhenExists_ShouldReturnFullDtoList() {
        List<Comment> mockComments = List.of(comment);
        when(commentRepository.findAllByEventId(10L)).thenReturn(mockComments);

        List<CommentFullDto> result = commentService.findCommentByEvent(10L);

        assertNotNull(result);
        verify(commentRepository, times(1)).findAllByEventId(10L);
    }
}
