package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests {

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    User booker = new User(4L, "Олег", "some@email.com");
    Item item = new Item(99L, "Перфоратор", "Супер инструмент", true, 4L, null);

    BookingCreateDto bookingCreateDto1 = new BookingCreateDto(
            LocalDateTime.of(2027, 2, 20, 5, 46, 37),
            LocalDateTime.of(2027, 3, 21, 23, 59, 1),
            item.getId(),
            booker.getId()
    );

    BookingResponseDto bookingResponseDto1 = new BookingResponseDto(
            2L,
            LocalDateTime.of(2027, 2, 20, 5, 46, 37),
            LocalDateTime.of(2027, 3, 21, 23, 59, 1),
            item,
            booker,
            BookingStatus.WAITING
    );

    @Test
    void testAddBooking() throws Exception {
        when(bookingService.addBooking(4L, bookingCreateDto1))
                .thenReturn(bookingResponseDto1);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "4")
                        .content(mapper.writeValueAsString(bookingCreateDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingResponseDto1.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponseDto1.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingResponseDto1.getEnd().toString())))
                .andExpect(jsonPath("$.item", is(bookingResponseDto1.getItem()), Item.class))
                .andExpect(jsonPath("$.booker", is(bookingResponseDto1.getBooker()), User.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto1.getStatus().toString())));

        verify(bookingService, times(1)).addBooking(4L, bookingCreateDto1);
    }

    @Test
    void testApproveBooking() throws Exception {
        BookingResponseDto bookingResponseDto2 = new BookingResponseDto(
                2L,
                LocalDateTime.of(2027, 2, 20, 5, 46, 37),
                LocalDateTime.of(2027, 3, 21, 23, 59, 1),
                item,
                booker,
                BookingStatus.APPROVED
        );

        when(bookingService.approveBooking(4L, 2L, "true"))
                .thenReturn(bookingResponseDto2);

        mvc.perform(patch("/bookings/2")
                        .header("X-Sharer-User-Id", "4")
                        .queryParam("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto2.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponseDto2.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingResponseDto2.getEnd().toString())))
                .andExpect(jsonPath("$.item", is(bookingResponseDto2.getItem()), Item.class))
                .andExpect(jsonPath("$.booker", is(bookingResponseDto2.getBooker()), User.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto2.getStatus().toString())));

        verify(bookingService, times(1)).approveBooking(4L, 2L, "true");
    }

    @Test
    void testGetBookingById() throws Exception {
        when(bookingService.getBookingById(4L, 2L))
                .thenReturn(bookingResponseDto1);

        mvc.perform(get("/bookings/2")
                        .header("X-Sharer-User-Id", "4")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto1.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponseDto1.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingResponseDto1.getEnd().toString())))
                .andExpect(jsonPath("$.item", is(bookingResponseDto1.getItem()), Item.class))
                .andExpect(jsonPath("$.booker", is(bookingResponseDto1.getBooker()), User.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto1.getStatus().toString())));

        verify(bookingService, times(1)).getBookingById(4L, 2L);
    }

    @Test
    void testGetBookingsMadeByUser() throws Exception {
        BookingResponseDto bookingResponseDto2 = new BookingResponseDto(
                3L,
                LocalDateTime.of(2026, 11, 20, 5, 46, 37),
                LocalDateTime.of(2026, 12, 21, 23, 59, 1),
                item,
                booker,
                BookingStatus.APPROVED
        );

        List<BookingResponseDto> bookingsDtos = List.of(bookingResponseDto1, bookingResponseDto2);
        when(bookingService.getBookingsMadeByUser(4L, "all"))
                .thenReturn(bookingsDtos);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "4")
                        .queryParam("state", "all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingResponseDto1.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(bookingResponseDto1.getStart().toString())))
                .andExpect(jsonPath("$.[0].end", is(bookingResponseDto1.getEnd().toString())))
                .andExpect(jsonPath("$.[0].item", is(bookingResponseDto1.getItem()), Item.class))
                .andExpect(jsonPath("$.[0].booker", is(bookingResponseDto1.getBooker()), User.class))
                .andExpect(jsonPath("$.[0].status", is(bookingResponseDto1.getStatus().toString())))
                .andExpect(jsonPath("$.[1].id", is(bookingResponseDto2.getId()), Long.class))
                .andExpect(jsonPath("$.[1].start", is(bookingResponseDto2.getStart().toString())))
                .andExpect(jsonPath("$.[1].end", is(bookingResponseDto2.getEnd().toString())))
                .andExpect(jsonPath("$.[1].item", is(bookingResponseDto2.getItem()), Item.class))
                .andExpect(jsonPath("$.[1].booker", is(bookingResponseDto2.getBooker()), User.class))
                .andExpect(jsonPath("$.[1].status", is(bookingResponseDto2.getStatus().toString())));

        verify(bookingService, times(1)).getBookingsMadeByUser(4L, "all");
    }

    @Test
    void testGetBookingsOfUserItems() throws Exception {
        BookingResponseDto bookingResponseDto2 = new BookingResponseDto(
                3L,
                LocalDateTime.of(2026, 11, 20, 5, 46, 37),
                LocalDateTime.of(2026, 12, 21, 23, 59, 1),
                item,
                booker,
                BookingStatus.REJECTED
        );

        List<BookingResponseDto> bookingsDtos = List.of(bookingResponseDto1, bookingResponseDto2);
        when(bookingService.getBookingsOfUserItems(4L, "all"))
                .thenReturn(bookingsDtos);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "4")
                        .queryParam("state", "all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingResponseDto1.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(bookingResponseDto1.getStart().toString())))
                .andExpect(jsonPath("$.[0].end", is(bookingResponseDto1.getEnd().toString())))
                .andExpect(jsonPath("$.[0].item", is(bookingResponseDto1.getItem()), Item.class))
                .andExpect(jsonPath("$.[0].booker", is(bookingResponseDto1.getBooker()), User.class))
                .andExpect(jsonPath("$.[0].status", is(bookingResponseDto1.getStatus().toString())))
                .andExpect(jsonPath("$.[1].id", is(bookingResponseDto2.getId()), Long.class))
                .andExpect(jsonPath("$.[1].start", is(bookingResponseDto2.getStart().toString())))
                .andExpect(jsonPath("$.[1].end", is(bookingResponseDto2.getEnd().toString())))
                .andExpect(jsonPath("$.[1].item", is(bookingResponseDto2.getItem()), Item.class))
                .andExpect(jsonPath("$.[1].booker", is(bookingResponseDto2.getBooker()), User.class))
                .andExpect(jsonPath("$.[1].status", is(bookingResponseDto2.getStatus().toString())));

        verify(bookingService, times(1)).getBookingsOfUserItems(4L, "all");
    }

}