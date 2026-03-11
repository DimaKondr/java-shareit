package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody UserDto userDto) {
        return userService.addUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") Long userId) {
        return userService.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") Long userId,
                              @RequestBody UserDto updatedUserDto) {
        return userService.updateUser(userId, updatedUserDto);
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable("userId") Long removedUserId) {
        userService.removeUser(removedUserId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

}