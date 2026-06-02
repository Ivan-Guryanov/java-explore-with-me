package ru.practicum.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EventStatusUpdateResult {
    private List<ParticipationRequestDto> confirmedRequests;

    private List<ParticipationRequestDto> rejectedRequests;
}
