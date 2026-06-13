package ru.practicum.comments.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.service.CommentService;
import ru.practicum.events.dto.*;

@RestController
@RequestMapping(path = "/comment")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@Validated
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable @Positive Long userId,
                                    @RequestBody @Valid NewCommentDto c) {
        log.info("Получен запрос от пользователя id{} на создание нового комментария", userId);
        CommentDto comm = commentService.createComment(userId, c);
        log.info("Создано комментарий id{} пользователем id{}", comm.getId(), comm.getAuthor());
        return comm;
    }

    @GetMapping("/{userId}/{comId}")
    public CommentDto findCommentById(@PathVariable @Positive Long userId,
                                      @PathVariable @Positive Long comId) {
        log.info("Получен запрос от пользователя Id{} на получение комментария ID{}", userId, comId);
        CommentDto comm = commentService.findCommentById(userId, comId);
        log.info("Комментарий id{} успешно получен", comm.getId());
        return comm;
    }

    @PatchMapping("/{userId}/{comId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto updateComment(@PathVariable @Positive Long userId,
                                    @PathVariable @Positive Long comId,
                                    @RequestBody @Valid NewCommentDto c) {
        log.info("Получен запрос от пользователя id{} на создание нового комментария", userId);
        CommentDto comm = commentService.updateComment(userId, comId, c);
        log.info("Создан комментарий id{} пользователем id{}", comm.getId(), comm.getAuthor());
        return comm;
    }

    @DeleteMapping("/{userId}/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Long userId,
                                    @PathVariable @Positive Long comId) {
        log.info("Получен запрос от пользователя id{} на удаление комментария id{}", userId, comId);
        commentService.deleteComment(userId, comId);
        log.info("Комментарий id{} успешно удален", comId);
    }
}
