package ru.practicum.comments.controller;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.StatsClient;
import ru.practicum.comments.dto.CommentFullDto;
import ru.practicum.comments.service.CommentService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
        import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PublicCommentController.class)
class PublicCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private StatsClient statsClient; // Заглушка для предотвращения сбоя StatsInterceptor

    private CommentFullDto rootCommentDto;

    @BeforeEach
    void setUp() {
        CommentFullDto childDto = new CommentFullDto();
        childDto.setId(2L);
        childDto.setEvent(1L);
        childDto.setAuthor("User2");
        childDto.setText("Это текст ответа на комментарий");
        childDto.setParent(1L);
        childDto.setDate("2026-06-12T19:45:00");
        childDto.setAnswer(new ArrayList<>());

        rootCommentDto = new CommentFullDto();
        rootCommentDto.setId(1L);
        rootCommentDto.setEvent(1L);
        rootCommentDto.setAuthor("User1");
        rootCommentDto.setText("Это текст корневого комментария");
        rootCommentDto.setParent(null);
        rootCommentDto.setDate("2026-06-12T19:40:50");
        rootCommentDto.setAnswer(List.of(childDto)); // Вкладываем ответ
    }

    @Test
    @DisplayName("Комментарии, Публичный, Контроллер. Получение древовидного списка комментариев по id события.")
    void findCommentByEvent_WhenExists_ShouldReturn200AndHierarchicalList() throws Exception {
        Long eventId = 1L;
        when(commentService.findCommentByEvent(eq(eventId))).thenReturn(List.of(rootCommentDto));

        mockMvc.perform(get("/comment/{eventId}", eventId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.length()").value(1))

                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].author").value("User1"))
                .andExpect(jsonPath("$[0].text").value("Это текст корневого комментария"))
                .andExpect(jsonPath("$[0].parent").value(nullValue()))

                .andExpect(jsonPath("$[0].answer").value(hasSize(1)))
                .andExpect(jsonPath("$[0].answer[0].id").value(2))
                .andExpect(jsonPath("$[0].answer[0].author").value("User2"))
                .andExpect(jsonPath("$[0].answer[0].parent").value(1))
                .andExpect(jsonPath("$[0].answer[0].answer").value(hasSize(0)));

        verify(commentService, times(1)).findCommentByEvent(eventId);
    }

    @Test
    @DisplayName("Комментарии, Публичный, Контроллер. Передача отрицательного id события вызывает ошибку.")
    void findCommentByEvent_WhenIdNegative_ShouldReturn400BadRequest() {
        Long incorrectEventId = -1L;

        ServletException exception = assertThrows(ServletException.class, () -> {
            mockMvc.perform(get("/comment/{eventId}", incorrectEventId)
                    .accept(MediaType.APPLICATION_JSON));
        });

        assertTrue(exception.getCause() instanceof jakarta.validation.ConstraintViolationException);

        verify(commentService, never()).findCommentByEvent(anyLong());
    }
}
