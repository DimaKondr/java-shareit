package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTests {

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    private UserDto userDto1 = new UserDto(1L, "Олег", "some@email.com");

    @Test
    void testAddUser() throws Exception {
        when(userService.addUser(any()))
                .thenReturn(userDto1);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));

        verify(userService, times(1)).addUser(any());
    }

    @Test
    void testGetUser() throws Exception {
        when(userService.getUserById(1L))
                .thenReturn(userDto1);

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testUpdateUser() throws Exception {
        when(userService.updateUser(1L, userDto1))
                .thenReturn(userDto1);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));

        verify(userService, times(1)).updateUser(1L, userDto1);
    }

    @Test
    void testRemoveUser() throws Exception {
        doNothing().when(userService).removeUser(1L);

        mvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).removeUser(1L);
    }

    @Test
    void testGetAllUsers() throws Exception {
        UserDto userDto2 = new UserDto(2L, "Коля", "nik@email.com");
        UserDto userDto3 = new UserDto(3L, "Ирина", "ira@email.com");

        List<UserDto> userDtos = List.of(userDto1, userDto2, userDto3);
        when(userService.getAllUsers())
                .thenReturn(userDtos);

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(userDto1.getName())))
                .andExpect(jsonPath("$.[0].email", is(userDto1.getEmail())))
                .andExpect(jsonPath("$.[1].id", is(userDto2.getId()), Long.class))
                .andExpect(jsonPath("$.[1].name", is(userDto2.getName())))
                .andExpect(jsonPath("$.[1].email", is(userDto2.getEmail())))
                .andExpect(jsonPath("$.[2].id", is(userDto3.getId()), Long.class))
                .andExpect(jsonPath("$.[2].name", is(userDto3.getName())))
                .andExpect(jsonPath("$.[2].email", is(userDto3.getEmail())));

        verify(userService, times(1)).getAllUsers();
    }

}