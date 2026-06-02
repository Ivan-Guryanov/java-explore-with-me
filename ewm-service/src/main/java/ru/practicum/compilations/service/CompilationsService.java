package ru.practicum.compilations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.CompilationsMapper;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.repository.CompilationsRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationsService {
    private final EventRepository eventRepository;
    private final CompilationsRepository compilationsRepository;

    @Transactional
    public CompilationDto createCompilation(NewCompilationDto c) {
        HashSet<Event> eventsSet;
        if (c.getEvents() != null) {
            eventsSet = new HashSet<>(eventRepository.findAllById(c.getEvents()));
        } else {
            eventsSet = null;
        }
        Compilation compilation = compilationsRepository.save(CompilationsMapper.mapToCompilation(c, eventsSet));

        return CompilationsMapper.mapToCompilationDto(compilation);
    }

    @Transactional
    public CompilationDto findCompilationById(Long compId) {
        Compilation c = compilationsRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        return CompilationsMapper.mapToCompilationDto(c);
    }

    @Transactional
    public List<CompilationDto> findCompilationByParam(Boolean pinned, int from, int size) {

        return compilationsRepository.findByPinned(pinned, from, size).stream()
                .map(CompilationsMapper::mapToCompilationDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteCompilation(Long compId) {
        Compilation c = compilationsRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        compilationsRepository.deleteById(compId);
    }

    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto comp) {
        Compilation c = compilationsRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        if (comp.getPinned() != null) {
            c.setPinned(comp.getPinned());
        }
        if (comp.getTitle() != null) {
            c.setTitle(comp.getTitle());
        }
        if (comp.getEvents() != null) {
            HashSet<Event> eventsSet = new HashSet<>(eventRepository.findAllById(comp.getEvents()));
            c.setEvents(eventsSet);
        }

        return CompilationsMapper.mapToCompilationDto(compilationsRepository.save(c));

    }
}
