package ru.practicum.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ParticipationRequestDto {

    private Long id;
    private String created;
    private Long event;
    private Long requester;
    private String status;
}
