package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewUserDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String name;

}
