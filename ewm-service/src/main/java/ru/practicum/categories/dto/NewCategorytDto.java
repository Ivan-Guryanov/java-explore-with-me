package ru.practicum.categories.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCategorytDto {

    @NotBlank(message = "Поле название должно быть заполнено.")
    private String name;
}