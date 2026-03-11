package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {

    // Добавляем нового пользователя.
    User addUser(User user);

    // Получаем пользователя по ID.
    User getUserById(Long userId);

    // Обновляем имеющегося пользователя.
    User updateUser(User updatedUser);

    // Удаляем имеющегося пользователя.
    User removeUser(Long userId);

    // Получаем список всех имеющихся пользователей.
    List<User> getAllUsers();

}