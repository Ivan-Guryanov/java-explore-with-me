package ru.practicum.categories.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    @NonNull
    private Long id;

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;
}
