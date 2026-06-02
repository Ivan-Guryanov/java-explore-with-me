package ru.practicum.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.events.dto.EventShortDto;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CompilationDto {

    private  Long id;

    private List<EventShortDto> events;

    @Builder.Default
    private Boolean pinned = false;

    private String title;
}
