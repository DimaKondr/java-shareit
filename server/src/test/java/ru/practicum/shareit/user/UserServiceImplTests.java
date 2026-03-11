package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:h2:mem:shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTests {
    private final UserService userService;

    @Test
    void testUpdateUser() {
        UserDto userDto1 = new UserDto(null, "Олег", "some@email.com");
        userDto1 = userService.addUser(userDto1);

        UserDto userForUpdate = new UserDto(null, "Олег Петров", " ");

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.updateUser(999L, userForUpdate));

        assertThat(exception.getMessage(),
                containsString("Обновление пользователя. Пользователь с ID: " + 999L + " не найден"));

        UserDto updatedDto = userService.updateUser(userDto1.getId(), userForUpdate);
        UserDto testUser = userService.getUserById(updatedDto.getId());

        assertThat(testUser.getId(), equalTo(userDto1.getId()));
        assertThat(testUser.getName(), equalTo(userForUpdate.getName()));
        assertThat(testUser.getEmail(), equalTo(userDto1.getEmail()));
    }

}