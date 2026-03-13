package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    // Добавляем нового пользователя.
    UserDto addUser(UserDto userDto);

    // Получаем пользователя по ID.
    UserDto getUserById(Long userId);

    // Обновляем имеющегося пользователя.
    UserDto updateUser(Long userId, UserDto updatedUserDto);

    // Удаляем имеющегося пользователя.
    void removeUser(Long userId);

    // Получаем список всех имеющихся пользователей.
    List<UserDto> getAllUsers();

}