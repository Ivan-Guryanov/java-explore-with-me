package ru.practicum.events.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.events.model.Location;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventUserRequest {

    @Size(min = 3, max = 120)
    private String title;

    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    private String eventDate;

    private Location location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;

    @Builder.Default
    private StateAction stateAction = StateAction.SEND_TO_REVIEW;
}
