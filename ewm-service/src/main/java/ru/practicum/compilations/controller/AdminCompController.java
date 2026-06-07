package ru.practicum.compilations.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationDto;
import ru.practicum.compilations.service.CompilationsService;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@Positive
public class AdminCompController {
    private final CompilationsService compilationsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto c) {
        log.info("Получен запрос на создание подборки событий");
        CompilationDto compilationDto = compilationsService.createCompilation(c);
        log.info("Подборка событий успешно создана с id{}", compilationDto.getId());
        return compilationDto;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @Positive Long compId) {
        log.info("Получен запрос на удаление подборки событий id{}", compId);
        compilationsService.deleteCompilation(compId);
        log.info("Событие id{} успешно удалено.", compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable @Positive Long compId,
                                            @RequestBody @Valid UpdateCompilationDto c) {
        log.info("Получен запрос на обновление подборки событий id{}", compId);
        CompilationDto compilationDto = compilationsService.updateCompilation(compId, c);
        log.info("Подборка событий id{} успешно обновлена", compilationDto.getId());
        return compilationDto;
    }
}
