package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

@Component
public class UserMapper {

    public static UserDto userToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }

    public static User dtoToUser(Long userId, UserDto userDto) {
        return new User(
                userId,
                userDto.getEmail(),
                userDto.getName()
        );
    }

}