package ru.practicum.requests.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@Validated
public class PrivateRequestController {
    private final RequestService requestService;

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable @Positive Long userId,
                                                 @RequestParam(name = "eventId") @Positive Long eventId) {
        log.info("Получен запрос от пользователя id{} на участие в событие id{}", userId, eventId);
        ParticipationRequestDto requestDto = requestService.createRequest(userId, eventId);
        log.info("Запрос на участие пользователя id{} в событие id{} успешно создан", requestDto.getRequester(),
                requestDto.getEvent());
        return requestDto;
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> findRequestsByRequester(@PathVariable @Positive Long userId) {
        log.info("Получен запрос на получение списка запросов на участие в чужих событиях пользователя id{}", userId);
        List<ParticipationRequestDto> r = requestService.findRequestsByRequester(userId);
        log.info("Список запросов для пользователя id{} успешно получен", userId);
        return r;
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable @Positive Long userId,
                                                 @PathVariable @Positive Long requestId) {
        log.info("Получен запрос от пользователя id{} на отмену заявки на участие в событие id{}", userId, requestId);
        ParticipationRequestDto r = requestService.cancelRequest(userId, requestId);
        log.info("Заявка на участие в событие id{} от пользователя id{} отменена", r.getEvent(), r.getRequester());
        return r;
    }
}