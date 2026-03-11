package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTests {

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    private ItemDtoForRequest itemDto1 = new ItemDtoForRequest(1L, "Самовар электрический", 5L);
    private ItemDtoForRequest itemDto2 = new ItemDtoForRequest(2L, "Самовар на углях", 8L);

    private ItemRequestCreateDto createDto = new ItemRequestCreateDto("Нужен самовар");

    private ItemRequestResponseDto emptyRequest = new ItemRequestResponseDto(
            55L,
            "Нужна балалайка",
            LocalDateTime.of(2025, 3, 20, 18, 10, 22),
            Collections.emptyList()
    );

    private ItemRequestResponseDto requestWithItems = new ItemRequestResponseDto(
            56L,
            "Нужен самовар",
            LocalDateTime.of(2025, 3, 20, 11, 45, 35),
            List.of(itemDto1, itemDto2)
    );

    @Test
    void testAddItemRequest() throws Exception {
        when(itemRequestService.addItemRequest(1L, createDto))
                .thenReturn(emptyRequest);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(createDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(emptyRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(emptyRequest.getDescription())))
                .andExpect(jsonPath("$.items", allOf(notNullValue(), empty())));

        verify(itemRequestService, times(1)).addItemRequest(1L, createDto);
    }

    @Test
    void testGetRequestsWithInfo() throws Exception {
        List<ItemRequestResponseDto> requestDtos = List.of(requestWithItems, emptyRequest);
        when(itemRequestService.getRequestsWithInfo(1L))
                .thenReturn(requestDtos);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(requestWithItems.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(requestWithItems.getDescription())))
                .andExpect(jsonPath("$.[0].created", is(requestWithItems.getCreated().toString())))
                .andExpect(jsonPath("$.[0].items.[0].id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.[0].items.[0].name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.[0].items.[0].ownerId", is(itemDto1.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.[0].items.[1].id", is(itemDto2.getId()), Long.class))
                .andExpect(jsonPath("$.[0].items.[1].name", is(itemDto2.getName())))
                .andExpect(jsonPath("$.[0].items.[1].ownerId", is(itemDto2.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.[1].id", is(emptyRequest.getId()), Long.class))
                .andExpect(jsonPath("$.[1].description", is(emptyRequest.getDescription())))
                .andExpect(jsonPath("$.[1].created", is(emptyRequest.getCreated().toString())))
                .andExpect(jsonPath("$.[1].items", allOf(notNullValue(), empty())));

        verify(itemRequestService, times(1)).getRequestsWithInfo(1L);
    }

    @Test
    void testGetOtherUsersRequests() throws Exception {
        List<ItemRequestResponseDto> requestDtos = List.of(requestWithItems, emptyRequest);
        when(itemRequestService.getOtherUsersRequests(2L))
                .thenReturn(requestDtos);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(requestWithItems.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(requestWithItems.getDescription())))
                .andExpect(jsonPath("$.[0].created", is(requestWithItems.getCreated().toString())))
                .andExpect(jsonPath("$.[0].items.[0].id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.[0].items.[0].name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.[0].items.[0].ownerId", is(itemDto1.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.[0].items.[1].id", is(itemDto2.getId()), Long.class))
                .andExpect(jsonPath("$.[0].items.[1].name", is(itemDto2.getName())))
                .andExpect(jsonPath("$.[0].items.[1].ownerId", is(itemDto2.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.[1].id", is(emptyRequest.getId()), Long.class))
                .andExpect(jsonPath("$.[1].description", is(emptyRequest.getDescription())))
                .andExpect(jsonPath("$.[1].created", is(emptyRequest.getCreated().toString())))
                .andExpect(jsonPath("$.[1].items", allOf(notNullValue(), empty())));

        verify(itemRequestService, times(1)).getOtherUsersRequests(2L);
    }

    @Test
    void testGetRequestById() throws Exception {
        when(itemRequestService.getRequestById(1L, 56L))
                .thenReturn(requestWithItems);

        mvc.perform(get("/requests/56")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestWithItems.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestWithItems.getDescription())))
                .andExpect(jsonPath("$.created", is(requestWithItems.getCreated().toString())))
                .andExpect(jsonPath("$.items.[0].id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.items.[0].name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.items.[0].ownerId", is(itemDto1.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.items.[1].id", is(itemDto2.getId()), Long.class))
                .andExpect(jsonPath("$.items.[1].name", is(itemDto2.getName())))
                .andExpect(jsonPath("$.items.[1].ownerId", is(itemDto2.getOwnerId()), Long.class));

        verify(itemRequestService, times(1)).getRequestById(1L, 56L);
    }

}