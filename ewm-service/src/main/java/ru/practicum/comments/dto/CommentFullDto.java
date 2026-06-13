package ru.practicum.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentFullDto {
    private Long id;
    private Long event;
    private String author;
    private String text;
    private Long parent;
    private String date;
    private List<CommentFullDto> answer;
}
