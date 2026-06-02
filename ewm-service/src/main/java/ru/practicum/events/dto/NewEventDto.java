package ru.practicum.events.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.events.model.Location;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NonNull
    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @NotBlank
    private String eventDate;

    @NotNull
    private Location location;

    @Builder.Default
    private boolean paid = false;

    @Builder.Default
    @PositiveOrZero
    private Integer participantLimit = 0;

    @Builder.Default
    private boolean requestModeration = true;

}