package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests {

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    private ItemDto itemDto1 = new ItemDto(3L, "Самовар", "Для душевного чаепития", true, null);
    private ItemDto itemDtoForResponse1 = new ItemDto(3L, "Самовар", "Для душевного чаепития",
            true, null, null, new ArrayList<>(), null);

    @Test
    void testAddItem() throws Exception {
        when(itemService.addItem(1L, itemDto1))
                .thenReturn(itemDtoForResponse1);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemDtoForResponse1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoForResponse1.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoForResponse1.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoForResponse1.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", is(itemDtoForResponse1.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemDtoForResponse1.getNextBooking())))
                .andExpect(jsonPath("$.comments", is(itemDtoForResponse1.getComments())))
                .andExpect(jsonPath("$.requestId", is(itemDtoForResponse1.getRequestId())));

        verify(itemService, times(1)).addItem(1L, itemDto1);
    }

    @Test
    void testGetItem() throws Exception {
        when(itemService.getItemById(3L))
                .thenReturn(itemDtoForResponse1);

        mvc.perform(get("/items/3")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoForResponse1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoForResponse1.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoForResponse1.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoForResponse1.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", is(itemDtoForResponse1.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemDtoForResponse1.getNextBooking())))
                .andExpect(jsonPath("$.comments", is(itemDtoForResponse1.getComments())))
                .andExpect(jsonPath("$.requestId", is(itemDtoForResponse1.getRequestId()), Long.class));

        verify(itemService, times(1)).getItemById(3L);
    }

    @Test
    void testUpdateItem() throws Exception {
        when(itemService.updateItem(3L, 1L, itemDto1))
                .thenReturn(itemDtoForResponse1);

        mvc.perform(patch("/items/3")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoForResponse1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoForResponse1.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoForResponse1.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoForResponse1.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", is(itemDtoForResponse1.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemDtoForResponse1.getNextBooking())))
                .andExpect(jsonPath("$.comments", is(itemDtoForResponse1.getComments())))
                .andExpect(jsonPath("$.requestId", is(itemDtoForResponse1.getRequestId())));

        verify(itemService, times(1)).updateItem(3L, 1L, itemDto1);
    }

    @Test
    void testRemoveItem() throws Exception {
        doNothing().when(itemService).removeItem(1L, 3L);

        mvc.perform(delete("/items/3")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService, times(1)).removeItem(1L, 3L);
    }

    @Test
    void testGetAllItems() throws Exception {
        ItemDto itemDtoForResponse2 = new ItemDto(17L, "Балалайка", "Для душевного вечера",
                true, null, null, new ArrayList<>(), null);
        User user = new User(134L, "Коля", "nik@mail.com");
        Comment comment = new Comment("Клевый инструмент!",
                ItemMapper.dtoToItem(17L, 1L, itemDtoForResponse2), user);
        itemDtoForResponse2.setComments(List.of(comment));

        ItemDto itemDtoForResponse3 = new ItemDto(49L, "Велосипед", "Для душевных поездок",
                true, null, null, new ArrayList<>(), null);

        List<ItemDto> itemDtos = List.of(itemDtoForResponse1, itemDtoForResponse2, itemDtoForResponse3);
        when(itemService.getAllItems(1L))
                .thenReturn(itemDtos);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDtoForResponse1.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDtoForResponse1.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDtoForResponse1.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDtoForResponse1.getAvailable())))
                .andExpect(jsonPath("$.[0].lastBooking", is(itemDtoForResponse1.getLastBooking())))
                .andExpect(jsonPath("$.[0].nextBooking", is(itemDtoForResponse1.getNextBooking())))
                .andExpect(jsonPath("$.[0].comments", allOf(notNullValue(), empty())))
                .andExpect(jsonPath("$.[0].requestId", is(itemDtoForResponse1.getRequestId())))

                .andExpect(jsonPath("$.[1].id", is(itemDtoForResponse2.getId()), Long.class))
                .andExpect(jsonPath("$.[1].name", is(itemDtoForResponse2.getName())))
                .andExpect(jsonPath("$.[1].description", is(itemDtoForResponse2.getDescription())))
                .andExpect(jsonPath("$.[1].available", is(itemDtoForResponse2.getAvailable())))
                .andExpect(jsonPath("$.[1].lastBooking", is(itemDtoForResponse2.getLastBooking())))
                .andExpect(jsonPath("$.[1].nextBooking", is(itemDtoForResponse2.getNextBooking())))
                .andExpect(jsonPath("$.[1].comments.[0].text", is(comment.getText())))
                .andExpect(jsonPath("$.[1].comments.[0].item", is(comment.getItem()), Item.class))
                .andExpect(jsonPath("$.[1].comments.[0].author", is(comment.getAuthor()), User.class))
                .andExpect(jsonPath("$.[1].requestId", is(itemDtoForResponse2.getRequestId())))

                .andExpect(jsonPath("$.[2].id", is(itemDtoForResponse3.getId()), Long.class))
                .andExpect(jsonPath("$.[2].name", is(itemDtoForResponse3.getName())))
                .andExpect(jsonPath("$.[2].description", is(itemDtoForResponse3.getDescription())))
                .andExpect(jsonPath("$.[2].available", is(itemDtoForResponse3.getAvailable())))
                .andExpect(jsonPath("$.[2].lastBooking", is(itemDtoForResponse3.getLastBooking())))
                .andExpect(jsonPath("$.[2].nextBooking", is(itemDtoForResponse3.getNextBooking())))
                .andExpect(jsonPath("$.[2].comments", allOf(notNullValue(), empty())))
                .andExpect(jsonPath("$.[2].requestId", is(itemDtoForResponse3.getRequestId())));

        verify(itemService, times(1)).getAllItems(1L);
    }

    @Test
    void testsSearchItems() throws Exception {
        ItemDto itemDto2 = new ItemDto(178L, "Веники дубовые", "Для душевных бань", true, null);
        List<ItemDto> itemDtos = List.of(itemDto1, itemDto2);
        when(itemService.searchItems("душевн"))
                .thenReturn(itemDtos);

        mvc.perform(get("/items/search")
                        .queryParam("text", "душевн")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto1.getAvailable())))
                .andExpect(jsonPath("$.[0].requestId", is(itemDto1.getRequestId())))
                .andExpect(jsonPath("$.[1].id", is(itemDto2.getId()), Long.class))
                .andExpect(jsonPath("$.[1].name", is(itemDto2.getName())))
                .andExpect(jsonPath("$.[1].description", is(itemDto2.getDescription())))
                .andExpect(jsonPath("$.[1].available", is(itemDto2.getAvailable())))
                .andExpect(jsonPath("$.[1].requestId", is(itemDto2.getRequestId())));

        verify(itemService, times(1)).searchItems("душевн");
    }

    @Test
    void testsAddComment() throws Exception {
        CommentCreateDto commentDto = new CommentCreateDto("Комментарий");

        ItemDto itemDtoForResponse1 = new ItemDto(32L, "Балалайка", "Для душевного вечера",
                true, null, null, new ArrayList<>(), null);

        CommentResponseDto commentResponseDto = new CommentResponseDto(
                1L,
                "Комментарий",
                ItemMapper.dtoToItem(32L, 2L, itemDtoForResponse1),
                "Коля",
                LocalDateTime.of(2025, 3, 20, 11, 45, 35)
        );

        when(itemService.addComment(7L, commentDto, 32L))
                .thenReturn(commentResponseDto);

        mvc.perform(post("/items/32/comment")
                        .header("X-Sharer-User-Id", "7")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(commentResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentResponseDto.getText())))
                .andExpect(jsonPath("$.item", is(commentResponseDto.getItem()), Item.class))
                .andExpect(jsonPath("$.authorName", is(commentResponseDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentResponseDto.getCreated().toString())));

        verify(itemService, times(1)).addComment(7L, commentDto, 32L);
    }

}