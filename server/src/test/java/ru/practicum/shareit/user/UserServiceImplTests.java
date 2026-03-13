package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:h2:mem:shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTests {
    private final UserService userService;

    @Test
    void testAddUser() {
        UserDto userDto = new UserDto(null, "Иван", "ivan@email.com");

        UserDto addedUser = userService.addUser(userDto);

        assertThat(addedUser.getId(), notNullValue());
        assertThat(addedUser.getName(), equalTo(userDto.getName()));
        assertThat(addedUser.getEmail(), equalTo(userDto.getEmail()));

        UserDto foundUser = userService.getUserById(addedUser.getId());
        assertThat(foundUser, equalTo(addedUser));
    }

    @Test
    void testAddUserWithDuplicateEmail() {
        UserDto userDto1 = new UserDto(null, "Иван", "duplicate@email.com");
        userService.addUser(userDto1);

        UserDto userDto2 = new UserDto(null, "Петр", "duplicate@email.com");

        assertThrows(DataIntegrityViolationException.class,
                () -> userService.addUser(userDto2));
    }

    @Test
    void testGetUserByIdNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getUserById(999L));

        assertThat(exception.getMessage(), containsString("Пользователь с ID: "
        + 999 + " не найден"));
    }

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

    @Test
    void testUpdateUserWithAllFields() {
        UserDto userDto = new UserDto(null, "Анна", "anna@email.com");
        userDto = userService.addUser(userDto);

        UserDto updateDto = new UserDto(null, "Анна Иванова", "anna.ivanova@email.com");
        UserDto updatedUser = userService.updateUser(userDto.getId(), updateDto);

        assertThat(updatedUser.getId(), equalTo(userDto.getId()));
        assertThat(updatedUser.getName(), equalTo(updateDto.getName()));
        assertThat(updatedUser.getEmail(), equalTo(updateDto.getEmail()));
    }

    @Test
    void testUpdateUserOnlyName() {
        UserDto userDto = new UserDto(null, "Сергей", "sergey@email.com");
        userDto = userService.addUser(userDto);

        UserDto updateDto = new UserDto(null, "Сергей Петров", null);
        UserDto updatedUser = userService.updateUser(userDto.getId(), updateDto);

        assertThat(updatedUser.getId(), equalTo(userDto.getId()));
        assertThat(updatedUser.getName(), equalTo(updateDto.getName()));
        assertThat(updatedUser.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void testUpdateUserOnlyEmail() {
        UserDto userDto = new UserDto(null, "Мария", "maria@email.com");
        userDto = userService.addUser(userDto);

        UserDto updateDto = new UserDto(null, null, "maria.ivanova@email.com");
        UserDto updatedUser = userService.updateUser(userDto.getId(), updateDto);

        assertThat(updatedUser.getId(), equalTo(userDto.getId()));
        assertThat(updatedUser.getName(), equalTo(userDto.getName()));
        assertThat(updatedUser.getEmail(), equalTo(updateDto.getEmail()));
    }

    @Test
    void testUpdateUserWithEmptyName() {
        UserDto userDto = new UserDto(null, "Дмитрий", "dmitry@email.com");
        userDto = userService.addUser(userDto);

        UserDto updateDto = new UserDto(null, "", "new.email@email.com");
        UserDto updatedUser = userService.updateUser(userDto.getId(), updateDto);

        assertThat(updatedUser.getId(), equalTo(userDto.getId()));
        assertThat(updatedUser.getName(), equalTo(userDto.getName()));
        assertThat(updatedUser.getEmail(), equalTo(updateDto.getEmail()));
    }

    @Test
    void testUpdateUserWithBlankEmail() {
        UserDto userDto = new UserDto(null, "Елена", "elena@email.com");
        userDto = userService.addUser(userDto);

        UserDto updateDto = new UserDto(null, "Елена Новая", "   ");
        UserDto updatedUser = userService.updateUser(userDto.getId(), updateDto);

        assertThat(updatedUser.getId(), equalTo(userDto.getId()));
        assertThat(updatedUser.getName(), equalTo(updateDto.getName()));
        assertThat(updatedUser.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void testRemoveUser() {
        UserDto userDto = new UserDto(null, "Николай", "nikolay@email.com");
        userDto = userService.addUser(userDto);

        Long userId = userDto.getId();

        assertDoesNotThrow(() -> userService.getUserById(userId));

        userService.removeUser(userId);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getUserById(userId));

        assertThat(exception.getMessage(), containsString("Пользователь с ID: "
                + userId + " не найден"));
    }

    @Test
    void testRemoveNonExistentUser() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.removeUser(999L));

        assertThat(exception.getMessage(), containsString("Удаление пользователя. Пользователь с ID: "
                + 999L + " не найден"));
    }

    @Test
    void testGetAllUsers() {
        List<UserDto> initialUsers = userService.getAllUsers();
        int initialSize = initialUsers.size();

        UserDto user1 = new UserDto(null, "Пользователь 1", "user1@email.com");
        UserDto user2 = new UserDto(null, "Пользователь 2", "user2@email.com");
        UserDto user3 = new UserDto(null, "Пользователь 3", "user3@email.com");

        userService.addUser(user1);
        userService.addUser(user2);
        userService.addUser(user3);

        List<UserDto> allUsers = userService.getAllUsers();

        assertThat(allUsers.size(), equalTo(initialSize + 3));
        assertThat(allUsers, hasItem(hasProperty("email", equalTo("user1@email.com"))));
        assertThat(allUsers, hasItem(hasProperty("email", equalTo("user2@email.com"))));
        assertThat(allUsers, hasItem(hasProperty("email", equalTo("user3@email.com"))));
    }

    @Test
    void testGetAllUsersWhenEmpty() {
        List<UserDto> users = userService.getAllUsers();
        assertThat(users, notNullValue());
    }

}