package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserMapper;
import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDto createUser(NewUserDto u) {
        return UserMapper.mapToUserDto(userRepository.save(UserMapper.mapToUser(u)));
    }

    @Transactional
    public List<UserDto> getUsers(List<Long> ids, Long from, Long size) {

        if(ids !=null && !ids.isEmpty()) {
            return userRepository.findAllById(ids, from, size).stream()
                    .map(UserMapper::mapToUserDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAll(from, size).stream()
                    .map(UserMapper::mapToUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public void deleteUser(Long userId) {
        getUserById(userId);
        userRepository.deleteById(userId);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
    }
}
