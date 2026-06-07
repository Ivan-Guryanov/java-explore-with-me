package ru.practicum.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDto {


    @NonNull
    private Long id;

    @NotBlank
    private String name;

}

