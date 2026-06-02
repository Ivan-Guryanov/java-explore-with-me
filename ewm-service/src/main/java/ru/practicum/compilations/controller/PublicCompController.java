package ru.practicum.compilations.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.service.CompilationsService;

import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class PublicCompController {
    private final CompilationsService compilationsService;

    @GetMapping("/{compId}")
    public CompilationDto findCompilationById(@PathVariable Long compId) {
        log.info("Получен запрос на получение подборки событий id{}", compId);
        CompilationDto c = compilationsService.findCompilationById(compId);
        log.info("Подборка событий id{} успешно получена", c.getId());
        return c;
    }

    @GetMapping
    public List<CompilationDto> findCompilationByParam(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                       @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получен запрос на подборку событий по параметрам");
        List<CompilationDto> comp = compilationsService.findCompilationByParam(pinned, from, size);
        log.info("Список подборок получен");
        return comp;
    }
}
