package ru.practicum.compilations.dto;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.events.dto.EventMapper;
import ru.practicum.events.model.Event;

import java.util.HashSet;
import java.util.List;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationsMapper {

    public static Compilation mapToCompilation(NewCompilationDto c, HashSet<Event> e) {
        return Compilation.builder()
                .events(e)
                .pinned(c.getPinned())
                .title(c.getTitle())
                .build();
    }

    public static CompilationDto mapToCompilationDto(Compilation c) {
        return CompilationDto.builder()
                .id(c.getId())
                .events(c.getEvents() != null ?
                        c.getEvents().stream()
                                .map(EventMapper::mapToEventshortDto)
                                .toList()
                        : List.of())
                .pinned(c.getPinned())
                .title(c.getTitle())
                .build();
    }
}