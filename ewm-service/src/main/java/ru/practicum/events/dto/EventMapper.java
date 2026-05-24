package ru.practicum.events.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.CategoryMapper;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.service.CategoryService;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.Location;
import ru.practicum.events.model.State;
import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserMapper;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Event mapToEvent(NewEventDto e, Category c) {
        return Event.builder()
                .title(e.getTitle())
                .annotation(e.getAnnotation())
                .category(c)
                .description(e.getDescription())
                .eventDate(LocalDateTime.parse(e.getEventDate(), formatter))
                .location(e.getLocation())
                .paid(e.isPaid())
                .participantLimit(e.getParticipantLimit())
                .requestModeration(e.isRequestModeration())
                .build();
    }

    public static EventFullDto mapToEventFullDto(Event e) {
        return EventFullDto.builder()
                .id(e.getId())
                .title(e.getTitle())
                .annotation(e.getAnnotation())
                .category(CategoryMapper.mapToCategoryDto(e.getCategory()))
                .confirmedRequests(e.getConfirmedRequests())
                .createdOn(e.getCreatedOn() != null ? e.getCreatedOn().format(formatter) : null)
                .description(e.getDescription())
                .eventDate(e.getEventDate().format(formatter))
                .initiator(UserMapper.mapToUserShortDto(e.getInitiator()))
                .location(e.getLocation())
                .paid(e.isPaid())
                .participantLimit(e.getParticipantLimit())
                .publishedOn(e.getCreatedOn() != null ? e.getCreatedOn().format(formatter) : null)
                .requestModeration(e.isRequestModeration())
                .state(e.getState())
                .views(e.getViews())
                .build();
    }

    public static EventShortDto mapToEventshortDto(Event e) {
        return EventShortDto.builder()
                .id(e.getId())
                .title(e.getTitle())
                .annotation(e.getAnnotation())
                .category(CategoryMapper.mapToCategoryDto(e.getCategory()))
                .confirmedRequests(e.getConfirmedRequests())
                .eventDate(e.getEventDate().format(formatter))
                .initiator(UserMapper.mapToUserShortDto(e.getInitiator()))
                .paid(e.isPaid())
                .views(e.getViews())
                .build();
    }
}
