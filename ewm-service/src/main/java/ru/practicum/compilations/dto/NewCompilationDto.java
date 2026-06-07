package ru.practicum.compilations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NewCompilationDto {

    private List<Long> events;

    @Builder.Default
    private Boolean pinned = false;

    @Size(min = 1, max = 50)
    @NotBlank
    private String title;

}