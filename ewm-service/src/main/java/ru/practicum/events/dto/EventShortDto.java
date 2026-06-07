package ru.practicum.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EventShortDto {
    private Long id;

    private String title; //название

    private String annotation; //Краткое описание

    private CategoryDto category; //    Категория

    private Long confirmedRequests; //Количество одобренных заявок на участие в данном событии

    private String eventDate; //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    private UserShortDto initiator; //    Пользователь (краткая информация)

    private Boolean paid; //Нужно ли оплачивать участие

    private Long views; //Количество просмотрев события
}
