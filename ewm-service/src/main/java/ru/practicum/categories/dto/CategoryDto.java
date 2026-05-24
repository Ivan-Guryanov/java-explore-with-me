package ru.practicum.categories.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    @NonNull
    private Long id;

    @NotBlank
    private String name;
}
