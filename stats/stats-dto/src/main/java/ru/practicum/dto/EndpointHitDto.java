package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHitDto {

    @NotBlank(message = "Поле не может быть пустым")
    private String app;

    @NotBlank(message = "Поле не может быть пустым")
    private String uri;

    @NotBlank(message = "Поле не может быть пустым")
    private String ip;

    @NotBlank(message = "Поле не может быть пустым")
    private String timestamp;
    //Дата и время, когда был совершен запрос к эндпоинту (в формате "yyyy-MM-dd HH:mm:ss")

}
