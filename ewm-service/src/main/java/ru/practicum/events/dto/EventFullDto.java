package ru.practicum.events.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.model.Category;
import ru.practicum.events.model.Location;
import ru.practicum.events.model.State;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EventFullDto {

    private Long id;

    private String title; //название

    private String annotation; //Краткое описание

    private CategoryDto category; //    Категория

    private Long confirmedRequests; //Количество одобренных заявок на участие в данном событии

    private String createdOn; //Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")

    private String description;  //Полное описание события

    private String eventDate; //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    private UserShortDto initiator; //    Пользователь (краткая информация)

    private Location location; //    Широта и долгота места проведения события

    private Boolean paid; //Нужно ли оплачивать участие

    private Integer participantLimit; //Ограничение на количество участников. Значение 0 - означает отсутствие ограничения

    private String publishedOn; //Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")

    private Boolean requestModeration; //Нужна ли пре-модерация заявок на участие

    private State state; //Список состояний жизненного цикла события

    private Long views; //Количество просмотрев события

}

