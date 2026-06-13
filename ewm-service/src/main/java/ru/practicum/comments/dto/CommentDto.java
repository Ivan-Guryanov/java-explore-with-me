package ru.practicum.comments.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private Long event;
    private Long author;
    private String text;
    private Long parent;
    private String date;
}
