package ru.practicum.compilations.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.categories.model.Category;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.repository.CompilationsRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompilationsServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CompilationsRepository compilationsRepository;

    @InjectMocks
    private CompilationsService compilationsService;

    @Test
    @DisplayName("Подборки, Сервис. Создание новой подборки событий.")
    void shouldCreateCompilationSuccessfully() {
        NewCompilationDto inputDto = new NewCompilationDto();
        inputDto.setTitle("Летние фестивали");
        inputDto.setPinned(true);
        inputDto.setEvents(List.of(1L, 2L));

        Category category = new Category();
        category.setId(1L);
        category.setName("Фестивали");

        User initiator = new User();
        initiator.setId(1L);
        initiator.setName("Организатор");

        Event event1 = new Event();
        event1.setId(1L);
        event1.setCategory(category);
        event1.setInitiator(initiator);
        event1.setTitle("Фестиваль 1");
        event1.setEventDate(LocalDateTime.now());

        Event event2 = new Event();
        event2.setId(2L);
        event2.setCategory(category);
        event2.setInitiator(initiator);
        event2.setTitle("Фестиваль 2");
        event2.setEventDate(LocalDateTime.now());

        Compilation savedCompilation = new Compilation();
        savedCompilation.setId(1L);
        savedCompilation.setTitle("Летние фестивали");
        savedCompilation.setPinned(true);
        savedCompilation.setEvents(new HashSet<>(List.of(event1, event2)));

        when(eventRepository.findAllById(inputDto.getEvents())).thenReturn(List.of(event1, event2));
        when(compilationsRepository.save(any(Compilation.class))).thenReturn(savedCompilation);

        CompilationDto result = compilationsService.createCompilation(inputDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Летние фестивали");
        assertThat(result.getPinned()).isTrue();
        verify(eventRepository, times(1)).findAllById(any());
        verify(compilationsRepository, times(1)).save(any(Compilation.class));
    }

    @Test
    @DisplayName("Подборки, Сервис. Поиск подборки по ID.")
    void shouldFindCompilationByIdSuccessfully() {
        Compilation compilation = new Compilation();
        compilation.setId(1L);
        compilation.setTitle("Зимние праздники");
        compilation.setPinned(false);
        compilation.setEvents(new HashSet<>());

        when(compilationsRepository.findById(1L)).thenReturn(Optional.of(compilation));

        CompilationDto result = compilationsService.findCompilationById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Зимние праздники");
    }

    @Test
    @DisplayName("Подборки, Сервис. Поиск подборок по параметрам (закреплено/пагинация).")
    void shouldFindCompilationByParamSuccessfully() {
        Compilation compilation = new Compilation();
        compilation.setId(1L);
        compilation.setTitle("Популярное");
        compilation.setPinned(true);
        compilation.setEvents(new HashSet<>());

        when(compilationsRepository.findByPinned(true, 0, 10)).thenReturn(List.of(compilation));

        List<CompilationDto> result = compilationsService.findCompilationByParam(true, 0, 10);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Популярное");
        verify(compilationsRepository, times(1)).findByPinned(true, 0, 10);
    }

    @Test
    @DisplayName("Подборки, Сервис. Удаление подборки по ID.")
    void shouldDeleteCompilationSuccessfully() {
        Compilation compilation = new Compilation();
        compilation.setId(1L);

        when(compilationsRepository.findById(1L)).thenReturn(Optional.of(compilation));
        doNothing().when(compilationsRepository).deleteById(1L);

        compilationsService.deleteCompilation(1L);

        verify(compilationsRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Подборки, Сервис. Обновление подборки событий.")
    void shouldUpdateCompilationSuccessfully() {
        UpdateCompilationDto updateDto = new UpdateCompilationDto();
        updateDto.setTitle("Новое название");
        updateDto.setPinned(false);
        updateDto.setEvents(List.of(3L));

        Compilation existingCompilation = new Compilation();
        existingCompilation.setId(1L);
        existingCompilation.setTitle("Старое название");
        existingCompilation.setPinned(true);
        existingCompilation.setEvents(new HashSet<>());

        Category category = new Category();
        category.setId(1L);
        category.setName("Культура");

        User initiator = new User();
        initiator.setId(1L);
        initiator.setName("Организатор");

        Event newEvent = new Event();
        newEvent.setId(3L);
        newEvent.setCategory(category);
        newEvent.setInitiator(initiator);
        newEvent.setTitle("Новое событие");
        newEvent.setEventDate(LocalDateTime.now());

        Compilation updatedCompilation = new Compilation();
        updatedCompilation.setId(1L);
        updatedCompilation.setTitle("Новое название");
        updatedCompilation.setPinned(false);
        updatedCompilation.setEvents(new HashSet<>(List.of(newEvent)));

        when(compilationsRepository.findById(1L)).thenReturn(Optional.of(existingCompilation));
        when(eventRepository.findAllById(updateDto.getEvents())).thenReturn(List.of(newEvent));
        when(compilationsRepository.save(any(Compilation.class))).thenReturn(updatedCompilation);

        CompilationDto result = compilationsService.updateCompilation(1L, updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Новое название");
        assertThat(result.getPinned()).isFalse();
        verify(compilationsRepository, times(1)).save(existingCompilation);
    }
}
