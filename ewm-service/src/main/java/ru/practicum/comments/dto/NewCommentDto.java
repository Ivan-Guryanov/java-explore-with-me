package ru.practicum.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentDto {

    @NotNull
    private Long event;

    @NotBlank
    @Size(min = 3, max = 2000)
    private String text;

    private Long parent;
}