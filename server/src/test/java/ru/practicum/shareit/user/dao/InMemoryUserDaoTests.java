package ru.practicum.shareit.user.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserDaoTests {

    private InMemoryUserDao userDao;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        userDao = new InMemoryUserDao();

        user1 = new User();
        user1.setName("John Doe");
        user1.setEmail("john@example.com");

        user2 = new User();
        user2.setName("Jane Smith");
        user2.setEmail("jane@example.com");
    }

    @Test
    void addUser_ShouldAddUser_WhenUserIsValid() {
        User addedUser = userDao.addUser(user1);

        assertNotNull(addedUser.getId());
        assertEquals(1L, addedUser.getId());
        assertEquals("John Doe", addedUser.getName());
        assertEquals("john@example.com", addedUser.getEmail());

        User retrievedUser = userDao.getUserById(1L);
        assertEquals(addedUser, retrievedUser);
    }

    @Test
    void addUser_ShouldThrowValidationException_WhenUserIsNull() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userDao.addUser(null));

        assertEquals("Запрос на добавление пользователя поступил с пустым телом",
                exception.getMessage());
    }

    @Test
    void addUser_ShouldThrowValidationException_WhenEmailAlreadyExists() {
        userDao.addUser(user1);

        User duplicateUser = new User();
        duplicateUser.setName("John Doe Copy");
        duplicateUser.setEmail("john@example.com");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userDao.addUser(duplicateUser));

        assertEquals("Указанный E-mail: john@example.com уже используется",
                exception.getMessage());
    }

    @Test
    void addUser_ShouldGenerateIncrementalIds() {
        User addedUser1 = userDao.addUser(user1);
        User addedUser2 = userDao.addUser(user2);

        assertEquals(1L, addedUser1.getId());
        assertEquals(2L, addedUser2.getId());
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        User addedUser = userDao.addUser(user1);

        User retrievedUser = userDao.getUserById(addedUser.getId());

        assertEquals(addedUser, retrievedUser);
        assertEquals(addedUser.getId(), retrievedUser.getId());
        assertEquals(addedUser.getName(), retrievedUser.getName());
        assertEquals(addedUser.getEmail(), retrievedUser.getEmail());
    }

    @Test
    void getUserById_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userDao.getUserById(999L));

        assertEquals("Попытка получения пользователя. Пользователь с ID: 999 не найден",
                exception.getMessage());
    }

    @Test
    void getUserById_ShouldThrowNotFoundException_WhenUserIdIsNull() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userDao.getUserById(null));

        assertEquals("Попытка получения пользователя. Пользователь с ID: null не найден",
                exception.getMessage());
    }

    @Test
    void updateUser_ShouldUpdateName_WhenNameIsChanged() {
        User addedUser = userDao.addUser(user1);

        User updateData = new User();
        updateData.setId(addedUser.getId());
        updateData.setName("Updated Name");
        updateData.setEmail(addedUser.getEmail());

        User updatedUser = userDao.updateUser(updateData);

        assertEquals(addedUser.getId(), updatedUser.getId());
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals(addedUser.getEmail(), updatedUser.getEmail());
    }

    @Test
    void updateUser_ShouldUpdateEmail_WhenEmailIsChanged() {
        User addedUser = userDao.addUser(user1);

        User updateData = new User();
        updateData.setId(addedUser.getId());
        updateData.setName(addedUser.getName());
        updateData.setEmail("updated@example.com");

        User updatedUser = userDao.updateUser(updateData);

        assertEquals(addedUser.getId(), updatedUser.getId());
        assertEquals(addedUser.getName(), updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    void updateUser_ShouldUpdateBothNameAndEmail_WhenBothAreChanged() {
        User addedUser = userDao.addUser(user1);

        User updateData = new User();
        updateData.setId(addedUser.getId());
        updateData.setName("Updated Name");
        updateData.setEmail("updated@example.com");

        User updatedUser = userDao.updateUser(updateData);

        assertEquals(addedUser.getId(), updatedUser.getId());
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    void updateUser_ShouldNotChangeAnything_WhenUpdateDataHasNullFields() {
        User addedUser = userDao.addUser(user1);

        User updateData = new User();
        updateData.setId(addedUser.getId());
        updateData.setName(null);
        updateData.setEmail(null);

        User updatedUser = userDao.updateUser(updateData);

        assertEquals(addedUser.getId(), updatedUser.getId());
        assertEquals(addedUser.getName(), updatedUser.getName());
        assertEquals(addedUser.getEmail(), updatedUser.getEmail());
    }

    @Test
    void updateUser_ShouldNotChangeAnything_WhenUpdateDataHasBlankFields() {
        User addedUser = userDao.addUser(user1);

        User updateData = new User();
        updateData.setId(addedUser.getId());
        updateData.setName("   ");
        updateData.setEmail("   ");

        User updatedUser = userDao.updateUser(updateData);

        assertEquals(addedUser.getId(), updatedUser.getId());
        assertEquals(addedUser.getName(), updatedUser.getName());
        assertEquals(addedUser.getEmail(), updatedUser.getEmail());
    }

    @Test
    void updateUser_ShouldThrowValidationException_WhenUpdatingToExistingEmail() {
        userDao.addUser(user1);
        User addedUser2 = userDao.addUser(user2);

        User updateData = new User();
        updateData.setId(addedUser2.getId());
        updateData.setEmail(user1.getEmail());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userDao.updateUser(updateData));

        assertEquals("Обновляемый E-mail: john@example.com уже используется",
                exception.getMessage());
    }

    @Test
    void updateUser_ShouldThrowValidationException_WhenUserIsNull() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userDao.updateUser(null));

        assertEquals("Запрос на обновление данных пользователя поступил с пустым телом",
                exception.getMessage());
    }

    @Test
    void updateUser_ShouldThrowValidationException_WhenUserIdIsNull() {
        User invalidUser = new User();
        invalidUser.setName("Test");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userDao.updateUser(invalidUser));

        assertEquals("ID пользователя должен быть указан", exception.getMessage());
    }

    @Test
    void updateUser_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        User nonExistentUser = new User();
        nonExistentUser.setId(999L);
        nonExistentUser.setName("Test");

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userDao.updateUser(nonExistentUser));

        assertEquals("Попытка обновления данных пользователя. Пользователь с ID: 999 не найден",
                exception.getMessage());
    }

    @Test
    void removeUser_ShouldRemoveAndReturnUser_WhenUserExists() {
        User addedUser = userDao.addUser(user1);

        User removedUser = userDao.removeUser(addedUser.getId());

        assertEquals(addedUser, removedUser);

        assertThrows(NotFoundException.class,
                () -> userDao.getUserById(addedUser.getId()));
    }

    @Test
    void removeUser_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userDao.removeUser(999L));

        assertEquals("Попытка удаления пользователя. Пользователь с ID: 999 не найден",
                exception.getMessage());
    }

    @Test
    void getAllUsers_ShouldReturnEmptyList_WhenNoUsers() {
        List<User> users = userDao.getAllUsers();

        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers_WhenUsersExist() {
        userDao.addUser(user1);
        userDao.addUser(user2);

        List<User> users = userDao.getAllUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }

    @Test
    void getNextId_ShouldGenerateCorrectIds() {
        assertEquals(0, userDao.getAllUsers().size());

        User firstUser = userDao.addUser(user1);
        assertEquals(1L, firstUser.getId());

        User secondUser = userDao.addUser(user2);
        assertEquals(2L, secondUser.getId());

        userDao.removeUser(1L);

        User thirdUser = new User();
        thirdUser.setName("Third User");
        thirdUser.setEmail("third@example.com");
        User addedThirdUser = userDao.addUser(thirdUser);
        assertEquals(3L, addedThirdUser.getId());
    }

}