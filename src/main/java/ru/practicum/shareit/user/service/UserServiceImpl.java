package ru.practicum.shareit.user.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Getter
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        User addedUser = UserMapper.dtoToUser(userDto.getId(), userDto);
        userDao.addUser(addedUser);
        return UserMapper.userToDto(addedUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userDao.getUserById(userId);
        return UserMapper.userToDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto updatedUserDto) {
        User updatedUser = UserMapper.dtoToUser(userId, updatedUserDto);
        userDao.updateUser(updatedUser);
        return UserMapper.userToDto(updatedUser);
    }

    @Override
    public UserDto removeUser(Long userId) {
        User removedUser = userDao.removeUser(userId);
        return UserMapper.userToDto(removedUser);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userDao.getAllUsers();
        return users.stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

}