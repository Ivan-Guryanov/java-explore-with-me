package ru.practicum.user.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User mapToUser(NewUserDto u) {
        return User.builder()
                .name(u.getName())
                .email(u.getEmail())
                .build();
    }

    public static UserDto mapToUserDto(User u) {
        return UserDto.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .build();
    }

    public static UserShortDto mapToUserShortDto(User u) {
        return UserShortDto.builder()
                .id(u.getId())
                .name(u.getName())
                .build();
    }

}
