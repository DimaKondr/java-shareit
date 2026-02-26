package ru.practicum.shareit.user.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto addUser(UserDto userDto) {
        User addedUser = UserMapper.dtoToUser(userDto.getId(), userDto);
        repository.save(addedUser);
        return UserMapper.userToDto(addedUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        Optional<User> user = repository.findById(userId);
        return UserMapper.userToDto(user.orElseThrow());
    }

    @Override
    public UserDto updateUser(Long userId, UserDto updatedUserDto) {
        User oldUser = repository.findById(userId).orElseThrow(() -> new NotFoundException("Обновление пользователя. " +
                "Пользователь с ID: " + userId + " не найден"));
        User updatedUser = UserMapper.dtoToUser(userId, updatedUserDto);

        if (updatedUser.getName() == null || updatedUser.getName().isBlank()) {
            log.debug("Обновление пользователя. Имя не указано. Оставляем имя без изменений");
            updatedUser.setName(oldUser.getName());
        }

        if (updatedUser.getEmail() == null || updatedUser.getEmail().isBlank()) {
            log.debug("Обновление пользователя. Email не указан. Оставляем Email без изменений");
            updatedUser.setEmail(oldUser.getEmail());
        }

        repository.save(updatedUser);
        return UserMapper.userToDto(updatedUser);
    }

    @Override
    public void removeUser(Long userId) {
        repository.deleteById(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = repository.findAll();
        return users.stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

}