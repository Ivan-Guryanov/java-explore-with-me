package ru.practicum.comments.service;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentFullDto;
import ru.practicum.comments.dto.CommentMapper;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentDto createComment(Long userId, NewCommentDto c) {
        Event e = eventRepository.findById(c.getEvent())
                .orElseThrow(() -> new NotFoundException("User with id=" + c.getEvent() + " was not found"));
        if (e.getState() == State.PENDING) {
            throw new NotFoundException("Событие не опубликовано");
        }
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Comment parent;
        if (c.getParent() != null) {
            parent = findById(c.getParent());
        } else {
            parent = null;
        }
        Comment com = CommentMapper.mapToComment(c, e, u, parent);
        com.setDate(LocalDateTime.now());

        return CommentMapper.mapToCommentDto(commentRepository.save(com));
    }


    public CommentDto findCommentById(@PathVariable @Positive Long userId,
                                      @PathVariable @Positive Long comId) {
        Comment comment = findById(comId);
        if (!comment.getUser().getId().equals(userId)) {
            throw new ValidationException("Пользователь не является владельцем комментария");
        }
        return CommentMapper.mapToCommentDto(comment);
    }


    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Комментарий с id=" + id + " не найден"));
    }

    @Transactional
    public CommentDto updateComment(@PathVariable @Positive Long userId,
                                    @PathVariable @Positive Long comId,
                                    @RequestBody @Valid NewCommentDto c) {
        Comment comment = findById(comId);
        if (!comment.getUser().getId().equals(userId)) {
            throw new ValidationException("Пользователь не является владельцем комментария");
        }
        if (c.getText() != null) {
            comment.setText(c.getText());
        }
        return CommentMapper.mapToCommentDto(comment);
    }

    @Transactional
    public void deleteComment(Long userId, Long comId) {
        Comment comment = findById(comId);
        if (!comment.getUser().getId().equals(userId)) {
            throw new ValidationException("Пользователь не является владельцем комментария");
        }
        commentRepository.delete(comment);
    }


    public List<CommentFullDto> findCommentByEvent(Long eventId) {
        List<Comment> com = commentRepository.findAllByEventId(eventId);

        return CommentMapper.mapToCommentFullDto(com);
    }
}
