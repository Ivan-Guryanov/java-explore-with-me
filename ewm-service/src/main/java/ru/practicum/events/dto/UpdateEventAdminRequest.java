package ru.practicum.events.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.events.model.Location;

@Builder
@Data
@AllArgsConstructor
public class UpdateEventAdminRequest {

    @Size(min = 3, max = 120)
    private String title; //название

    @Size(min = 20, max = 2000)
    private String annotation; //Краткое описание

    private Long category; //    Категория

    @Size(min = 20, max = 7000)
    private String description;  //Полное описание события

    private String eventDate; //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    private Location location; //    Широта и долгота места проведения события

    private Boolean paid; //Нужно ли оплачивать участие

    private Integer participantLimit; //Ограничение на количество участников. Значение 0 - означает отсутствие ограничения

    private StateAction stateAction; //Список состояний жизненного цикла события
}
