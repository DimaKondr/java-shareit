package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
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
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        return userService.addUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId")
                               @NotNull(message = "userId не может быть null")
                               @Valid Long userId) {
        return userService.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId")
                                  @NotNull(message = "userId не может быть null")
                                  @Valid Long userId,
                              @RequestBody
                                  @Valid UserDto updatedUserDto) {
        return userService.updateUser(userId, updatedUserDto);
    }

    @DeleteMapping("/{userId}")
    public UserDto removeUser(@PathVariable("userId")
                                  @NotNull(message = "userId не может быть null")
                                  @Valid Long removedUserId) {
        return userService.removeUser(removedUserId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

}