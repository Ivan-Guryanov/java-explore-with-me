package ru.practicum.comments.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentFullDto;
import ru.practicum.comments.service.CommentService;

import java.util.List;

@RestController
@RequestMapping(path = "/comment")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@Validated
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping("/{eventId}")
    public List<CommentFullDto> findCommentByEvent(@PathVariable @Positive Long eventId) {
        log.info("Получен запрос на получение списка комментариев к событию id{}", eventId);
        List<CommentFullDto> commentFullDtos = commentService.findCommentByEvent(eventId);
        log.info("Список комментариев к событию id{} успешно получен", commentFullDtos.get(0).getEvent());
        return commentFullDtos;
    }
}
