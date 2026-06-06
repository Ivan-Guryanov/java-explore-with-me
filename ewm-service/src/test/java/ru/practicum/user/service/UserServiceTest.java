package ru.practicum.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Пользователи, Сервис. Успешное создание нового пользователя.")
    void shouldCreateUserSuccessfully() {
        NewUserDto inputDto = new NewUserDto();
        inputDto.setName("Иван Петров");
        inputDto.setEmail("ivan@practicum.ru");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Иван Петров");
        savedUser.setEmail("ivan@practicum.ru");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.createUser(inputDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Иван Петров");
        assertThat(result.getEmail()).isEqualTo("ivan@practicum.ru");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Пользователи, Сервис. Получение пользователей по конкретному списку ID.")
    void shouldGetUsersByIdsSuccessfully() {
        List<Long> ids = List.of(1L, 2L);
        Long from = 0L;
        Long size = 10L;

        User user1 = new User();
        user1.setId(1L);
        user1.setName("Пользователь 1");
        user1.setEmail("user1@mail.ru");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Пользователь 2");
        user2.setEmail("user2@mail.ru");

        when(userRepository.findAllById(ids, from, size)).thenReturn(List.of(user1, user2));

        List<UserDto> result = userService.getUsers(ids, from, size);

        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        verify(userRepository, times(1)).findAllById(ids, from, size);
        verify(userRepository, never()).findAll(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Пользователи, Сервис. Получение пользователей без фильтра по ID (список равен null).")
    void shouldGetAllUsersWhenIdsIsNull() {
        Long from = 0L;
        Long size = 5L;

        User user = new User();
        user.setId(3L);
        user.setName("Общий Пользователь");
        user.setEmail("common@mail.ru");

        when(userRepository.findAll(from, size)).thenReturn(List.of(user));

        List<UserDto> result = userService.getUsers(null, from, size);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(3L);
        verify(userRepository, times(1)).findAll(from, size);
        verify(userRepository, never()).findAllById(any(), anyLong(), anyLong());
    }

    @Test
    @DisplayName("Пользователи, Сервис. Получение пользователей без фильтра по ID (список пуст).")
    void shouldGetAllUsersWhenIdsIsEmpty() {
        Long from = 0L;
        Long size = 5L;

        User user = new User();
        user.setId(4L);
        user.setName("Пользователь 4");
        user.setEmail("user4@mail.ru");

        when(userRepository.findAll(from, size)).thenReturn(List.of(user));

        List<UserDto> result = userService.getUsers(List.of(), from, size);

        assertThat(result).isNotNull().hasSize(1);
        verify(userRepository, times(1)).findAll(from, size);
    }

    @Test
    @DisplayName("Пользователи, Сервис. Успешный поиск пользователя по ID.")
    void shouldGetUserByIdSuccessfully() {
        User user = new User();
        user.setId(1L);
        user.setName("Тест");
        user.setEmail("test@mail.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Тест");
    }

    @Test
    @DisplayName("Пользователи, Сервис. Ошибка поиска пользователя по ID: Пользователь не найден.")
    void shouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with id=99 was not found");
    }

    @Test
    @DisplayName("Пользователи, Сервис. Успешное удаление пользователя по ID.")
    void shouldDeleteUserSuccessfully() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
}
