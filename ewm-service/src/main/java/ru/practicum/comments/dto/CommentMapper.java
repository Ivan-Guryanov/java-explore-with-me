package ru.practicum.comments.dto;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.comments.model.Comment;
import ru.practicum.events.model.Event;
import ru.practicum.user.model.User;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Comment mapToComment(NewCommentDto c, Event e, User u, Comment parent) {
        return Comment.builder()
                .event(e)
                .user(u)
                .text(c.getText())
                .parent(parent)
                .build();
    }

    public static CommentDto mapToCommentDto(Comment c) {
        return CommentDto.builder()
                .id(c.getId())
                .event(c.getEvent().getId())
                .author(c.getUser().getId())
                .text(c.getText())
                .parent(c.getParent() != null ? c.getParent().getId() : null)
                .date(c.getDate().format(formatter))
                .build();
    }

    public static List<CommentFullDto> mapToCommentFullDto(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return new ArrayList<>();
        }

        List<CommentFullDto> rootComments = new ArrayList<>();

        for (Comment c : comments) {
            if (c.getParent() == null) {
                CommentFullDto rootDto = convertToDto(c);
                rootDto.setAnswer(findAnswersRecursively(rootDto.getId(), comments));
                rootComments.add(rootDto);
            }
        }

        rootComments.sort(Comparator.comparing(CommentFullDto::getDate));

        return rootComments;
    }

    private static List<CommentFullDto> findAnswersRecursively(Long parentId, List<Comment> allComments) {
        List<CommentFullDto> answers = new ArrayList<>();

        for (Comment c : allComments) {
            if (c.getParent() != null && c.getParent().getId().equals(parentId)) {
                CommentFullDto childDto = convertToDto(c);

                childDto.setAnswer(findAnswersRecursively(childDto.getId(), allComments));

                answers.add(childDto);
            }
        }

        answers.sort(Comparator.comparing(CommentFullDto::getDate));

        return answers;
    }

    private static CommentFullDto convertToDto(Comment c) {
        return CommentFullDto.builder()
                .id(c.getId())
                .event(c.getEvent().getId())
                .author(c.getUser().getName())
                .text(c.getText())
                .parent(c.getParent() != null ? c.getParent().getId() : null)
                .date(c.getDate().format(formatter))
                .answer(new ArrayList<>())
                .build();
    }
}
