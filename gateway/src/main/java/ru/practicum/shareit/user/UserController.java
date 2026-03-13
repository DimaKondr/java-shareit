package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @NotNull(message = "user can not be null")
            @Validated(UserRequestDto.OnCreate.class)
                UserRequestDto requestDto) {
        log.info("Creating user {}", requestDto);
        return userClient.addUser(requestDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable("userId") @NotNull(message = "userId can not be null")
                                              Long userId) {
        log.info("Get user {}", userId);
        return userClient.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable("userId") @NotNull(message = "userId can not be null")
                                                 Long userId,
                                             @RequestBody @Validated(UserRequestDto.OnUpdate.class)
                                                 UserRequestDto updatedUserDto) {
        log.info("Update user {}", userId);
        return userClient.updateUser(userId, updatedUserDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> removeUser(@PathVariable("userId")
                                  @NotNull(message = "userId не может быть null")
                                  @Valid Long removedUserId) {
        log.info("Remove user {}", removedUserId);
        return userClient.removeUser(removedUserId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }

}