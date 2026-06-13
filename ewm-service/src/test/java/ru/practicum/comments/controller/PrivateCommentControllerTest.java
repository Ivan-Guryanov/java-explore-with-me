package ru.practicum.comments.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.StatsClient;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.service.CommentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PrivateCommentController.class)
class PrivateCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private StatsClient statsClient; // Заглушка для предотвращения сбоя StatsInterceptor

    private NewCommentDto newCommentDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        newCommentDto = new NewCommentDto();
        newCommentDto.setText("Это текст валидного комментария");
        newCommentDto.setEvent(1L);
        newCommentDto.setParent(null);

        commentDto = new CommentDto();
        commentDto.setId(100L);
        commentDto.setAuthor(1L);
        commentDto.setText("Это текст валидного комментария");
        commentDto.setEvent(1L);
        commentDto.setParent(null);
        commentDto.setDate("2026-06-12T19:40:50");
    }

    @Test
    @DisplayName("Комментарии, Приватный, Контроллер. Создание нового комментария.")
    void createComment_WhenValid_ShouldReturn201AndDto() throws Exception {
        Long userId = 1L;
        when(commentService.createComment(eq(userId), any(NewCommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(post("/comment/{userId}", userId)
                        .content(objectMapper.writeValueAsString(newCommentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.text").value("Это текст валидного комментария"))
                .andExpect(jsonPath("$.author").value(1))
                .andExpect(jsonPath("$.event").value(1));

        verify(commentService, times(1)).createComment(eq(userId), any(NewCommentDto.class));
    }

    @Test
    @DisplayName("Комментарии, Приватный, Контроллер. Получение комментария по id.")
    void findCommentById_WhenExists_ShouldReturn200AndDto() throws Exception {
        Long userId = 1L;
        Long comId = 100L;
        when(commentService.findCommentById(eq(userId), eq(comId))).thenReturn(commentDto);

        mockMvc.perform(get("/comment/{userId}/{comId}", userId, comId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.text").value("Это текст валидного комментария"));

        verify(commentService, times(1)).findCommentById(userId, comId);
    }

    @Test
    @DisplayName("Комментарии, Приватный, Контроллер. Обновление комментария.")
    void updateComment_WhenValid_ShouldReturn201AndDto() throws Exception {
        Long userId = 1L;
        Long comId = 100L;
        when(commentService.updateComment(eq(userId), eq(comId), any(NewCommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(patch("/comment/{userId}/{comId}", userId, comId)
                        .content(objectMapper.writeValueAsString(newCommentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.text").value("Это текст валидного комментария"));

        verify(commentService, times(1)).updateComment(eq(userId), eq(comId), any(NewCommentDto.class));
    }

    @Test
    @DisplayName("Комментарии, Приватный, Контроллер. Удаление комментария.")
    void deleteComment_WhenValid_ShouldReturn204() throws Exception {
        Long userId = 1L;
        Long comId = 100L;
        doNothing().when(commentService).deleteComment(eq(userId), eq(comId));

        mockMvc.perform(delete("/comment/{userId}/{comId}", userId, comId))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteComment(userId, comId);
    }
}
