package ru.practicum.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.StatsClient;
import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminUserController.class)
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private StatsClient statsClient;

    @Test
    @DisplayName("Пользователи, Админ, Контроллер. Создание нового пользователя.")
    void shouldCreateUserThenReturn201() throws Exception {
        NewUserDto requestBody = new NewUserDto("ivan@mail.ru", "Ivan");

        UserDto responseDto = new UserDto();
        responseDto.setId(1L);
        responseDto.setName("Ivan");
        responseDto.setEmail("ivan@mail.ru");

        ArgumentCaptor<NewUserDto> dtoCaptor = ArgumentCaptor.forClass(NewUserDto.class);

        when(userService.createUser(dtoCaptor.capture())).thenReturn(responseDto);

        mockMvc.perform(post("/admin/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.email").value("ivan@mail.ru"));

        assertThat(dtoCaptor.getValue()).isNotNull();
        verify(userService, times(1)).createUser(any(NewUserDto.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Пользователи, Админ, Контроллер. Получение списка пользователей.")
    void shouldGetUsersThenReturn200() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Ivan");

        when(userService.getUsers(eq(List.of(1L)), eq(0L), eq(10L)))
                .thenReturn(List.of(userDto));

        mockMvc.perform(get("/admin/users")
                        .param("ids", "1")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Ivan"));

        verify(userService, times(1)).getUsers(List.of(1L), 0L, 10L);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Пользователи, Админ, Контроллер. Удаление пользователя.")
    void shouldDeleteUserThenReturn204() throws Exception {
        mockMvc.perform(delete("/admin/users/{userId}", 1L)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
        verifyNoMoreInteractions(userService);
    }
}
