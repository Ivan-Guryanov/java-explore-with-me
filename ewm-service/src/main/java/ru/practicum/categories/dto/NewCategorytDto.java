package ru.practicum.categories.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCategorytDto {

    @NotBlank(message = "Поле название должно быть заполнено.")
    @Size(min = 1, max = 50)
    private String name;
}